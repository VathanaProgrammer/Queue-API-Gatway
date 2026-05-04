package com.example.demo.controllers.auth;

import com.example.demo.dtos.*;
import com.example.demo.dtos.common.ApiResponse;
import com.example.demo.entities.Employee;
import com.example.demo.entities.RefreshToken;
import com.example.demo.entities.User;
import com.example.demo.repositories.EmployeeRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.security.UserDetailsImpl;
import com.example.demo.services.RefreshTokenService;
import com.example.demo.services.UserService;
import com.example.demo.utils.JwtUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.WebUtils;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @Value("${ems.app.jwtRefreshCookieName}") // Name of your cookie
    private String jwtRefreshCookie;


    @Autowired
    @Lazy
    RefreshTokenService refreshTokenService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            // 1. Generate Access Token Cookie
            ResponseCookie jwtAccessCookie = jwtUtils.generateJwtCookie(userDetails);

            // 2. Generate Refresh Token Cookie
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

            ResponseCookie refreshCookie = ResponseCookie.from(jwtRefreshCookie, refreshToken.getToken())
                    .path("/") // Change this to "/" so the WHOLE app can see it for now
                    .maxAge(7 * 24 * 60 * 60)
                    .httpOnly(true)
                    .secure(false) // Must be false for http://localhost
                    .sameSite("Lax")
                    .build();

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            // 3. Add to headers
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtAccessCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .body(ApiResponse.success("Login successfully", true));
        }catch (AuthenticationException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error( "Invalid username or password!"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(HttpServletRequest request) {
        // Get Refresh Token from cookie instead of Request Body
        Cookie cookie = WebUtils.getCookie(request, jwtRefreshCookie);
        String requestRefreshToken = (cookie != null) ? cookie.getValue() : null;

        if (requestRefreshToken != null && !requestRefreshToken.isEmpty()) {
            return refreshTokenService.findByToken(requestRefreshToken)
                    .map(refreshTokenService::verifyExpiration)
                    .map(RefreshToken::getUser)
                    .map(user -> {
                        // Generate new Access Cookie
                        ResponseCookie jwtAccessCookie = jwtUtils.generateJwtCookie(UserDetailsImpl.build(user));
                        return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, jwtAccessCookie.toString())
                                .body("Token refreshed successfully!");
                    })
                    .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
        }
        return ResponseEntity.badRequest().body("Refresh Token is missing!");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        ResponseCookie jwtAccessCookie = jwtUtils.getCleanJwtCookie();
        ResponseCookie refreshCookie = ResponseCookie.from(jwtRefreshCookie, null)
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtAccessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(ApiResponse.success("Logout successfully", false));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 1. Basic Security Check
        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 2. Get User Details from Security Context
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // 3. Fetch full entities from Database (Oracle)
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(user.isEnable() == false){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("User is disabled!"));
        }

        if(user == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("User not found!"));
        }
        // Find the linked profile
        Employee employee = employeeRepository.findByUser(user); // Or handle as error if profile is mandatory

        if(employee == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Employee not found!"));
        }
        // 4. Map to your combined DTO
        UserInfoResponse response = userService.mapToUserInfo(user, employee);

        // 5. Return the full package
        return ResponseEntity.ok(ApiResponse.success("User profile retrieved successfully", response));
    }

    @PostMapping(value = "/dev/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> registerUser(
            @Valid @RequestPart("user") SignupRequest signupRequest,
            @Valid @RequestPart("employee") EmployeeSignupRequest employeeSignupRequest,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {

        // Delegating the fucking logic to the service you just provided
        return userService.registerEmployeeWithUser(signupRequest, employeeSignupRequest, imageFile);
    }
}