package com.example.demo.services;

import com.example.demo.dtos.EmployeeResponse;
import com.example.demo.dtos.EmployeeSignupRequest;
import com.example.demo.dtos.SignupRequest;
import com.example.demo.dtos.UserInfoResponse;
import com.example.demo.dtos.common.ApiResponse;
import com.example.demo.entities.Employee;
import com.example.demo.entities.Role;
import com.example.demo.entities.User;
import com.example.demo.repositories.DepartmentRepository;
import com.example.demo.repositories.EmployeeRepository;
import com.example.demo.repositories.RoleRepository;
import com.example.demo.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Transactional
    public ResponseEntity<ApiResponse<?>> registerEmployeeWithUser(SignupRequest userRequest, EmployeeSignupRequest empRequest, MultipartFile file) {
        // 1. Check if username exists
        if (userRepository.existsByUsername(userRequest.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Username is already taken!"));
        }

        if(employeeRepository.existsByEmail(empRequest.getEmail())){
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Email is already taken!"));
        }

        try {
            String finalImagePath = "/uploads/users/default-profile.png";

            // 2. Process image file if it exists
            if (file != null && !file.isEmpty()) {
                finalImagePath = this.saveImage(file);
            }

            // 3. Create and Save User Entity
            User user = new User();
            user.setUsername(userRequest.getUsername());
            user.setPassword(encoder.encode(userRequest.getPassword()));
            user.setEnable(true);

            if(userRequest.getRoleId() != null){
                roleRepository.findById(userRequest.getRoleId()).ifPresent(role -> {
                    user.getRoles().add(role);
                });
            }

            User savedUser = userRepository.saveAndFlush(user);

            userRepository.findById(savedUser.getUserId()).orElseThrow();

            System.out.println("just saved User: " + savedUser.getUserId());

            Employee employee = new Employee();

            // --- Manual Mapping for all fields ---
            employee.setFirstName(empRequest.firstName());
            employee.setLastName(empRequest.lastName());
            employee.setEmail(empRequest.email());
            employee.setPhone(empRequest.phone());
            employee.setGender(empRequest.gender());
            employee.setDateOfBirth(empRequest.dateOfBirth());
            employee.setNationalId(empRequest.nationalId());
            employee.setMaritalStatus(empRequest.maritalStatus());

            employee.setPosition(empRequest.position());
            employee.setJobType(empRequest.jobType());
            employee.setSalary(empRequest.salary());
            employee.setHireDate(empRequest.hireDate());
            employee.setStatus(empRequest.status() != null ? empRequest.status() : "ACTIVE");
            employee.setAddress(empRequest.address());

            employee.setEmergencyContactName(empRequest.emergencyContactName());
            employee.setEmergencyContactPhone(empRequest.emergencyContactPhone());

            employee.setBankName(empRequest.bankName());
            employee.setBankAccountName(empRequest.bankAccountName());
            employee.setBankAccountNumber(empRequest.bankAccountNumber());

            employee.setEducationLevel(empRequest.educationLevel());
            employee.setEducationField(empRequest.educationField());

            // Set Profile Image
            employee.setProfileImage(finalImagePath);

            // Set Relationships
            employee.setUser(savedUser);

            System.out.println("Employee: " + employee);
            System.out.println("User: " + savedUser);
            System.out.println("Role: " + userRequest.getRoleId());
            System.out.println("Department: " + empRequest.deptId() + "");
            if (empRequest.deptId() != null) {
                departmentRepository.findById(empRequest.getDeptId())
                        .ifPresent(employee::setDepartment);
            }

            employeeRepository.save(employee);

            // 4. Return the fucking ApiResponse
            return ResponseEntity.ok(ApiResponse.success("Employee and User account created successfully!", null));

        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to upload image: " + e.getMessage()));
        }
    }

    /**
     * Helper to save file to 'uploads/users/' directory
     */
    private String saveImage(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get("uploads/users/");

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        return "/uploads/users/" + fileName;
    }

    public UserInfoResponse mapToUserInfo(User user, Employee emp) {
        // 1. Map Employee + Department
        EmployeeResponse empDto = null;
        if (emp != null) {
            empDto = new EmployeeResponse(
                    emp.getEmpId(),
                    emp.getFirstName(),
                    emp.getLastName(),
                    emp.getEmail(),
                    emp.getPhone(),
                    emp.getGender(),
                    emp.getDateOfBirth(),
                    emp.getNationalId(),
                    emp.getMaritalStatus(),
                    emp.getPosition(),
                    emp.getJobType(),
                    emp.getSalary(),
                    emp.getHireDate(),
                    emp.getStatus(),
                    emp.getProfileImage(),
                    emp.getAddress(),
                    emp.getEmergencyContactName(),
                    emp.getEmergencyContactPhone(),
                    emp.getBankName(),
                    emp.getBankAccountName(),
                    emp.getBankAccountNumber(),
                    emp.getEducationLevel(),
                    emp.getEducationField(),
                    emp.getDepartment() != null ?
                            new EmployeeResponse.DepartmentDto(
                                    emp.getDepartment().getDeptId(),
                                    emp.getDepartment().getDeptName()) : null,
                    null
            );
        }

        // 2. Map User + Roles + Employee
        return new UserInfoResponse(
                user.getUserId(),
                user.getUsername(),
                user.isEnable(),
                user.getRoles().stream()
                        .map(Role::getRoleName)
                        .collect(java.util.stream.Collectors.toSet()),
                user.getRoles().stream()
                        .flatMap(role -> role.getPermissions().stream())
                        .map(permission -> permission.getPermName())
                        .collect(java.util.stream.Collectors.toSet()),
                empDto
        );
    }

    public EmployeeResponse mapToEmployeeResponse(Employee emp) {
        if (emp == null) return null;

        // 1. Map the User part
        EmployeeResponse.UserAccountDto userDto = null;
        if (emp.getUser() != null) {
            userDto = new EmployeeResponse.UserAccountDto(
                    emp.getUser().getUserId(),
                    emp.getUser().getUsername(),
                    emp.getUser().isEnable(),
                    emp.getUser().getRoles().stream()
                            .map(role -> role.getRoleName()) // FIXED: Use the correct Role getter
                            .collect(java.util.stream.Collectors.toSet())
            );
        }

        // 2. Map the Department part
        EmployeeResponse.DepartmentDto deptDto = null;
        if (emp.getDepartment() != null) {
            deptDto = new EmployeeResponse.DepartmentDto(
                    emp.getDepartment().getDeptId(),
                    emp.getDepartment().getDeptName()
            );
        }

        // 3. Return the full Object
        return new EmployeeResponse(
                emp.getEmpId(),
                emp.getFirstName(),
                emp.getLastName(),
                emp.getEmail(),
                emp.getPhone(),
                emp.getGender(),
                emp.getDateOfBirth(),
                emp.getNationalId(),
                emp.getMaritalStatus(),
                emp.getPosition(),
                emp.getJobType(),
                emp.getSalary(),
                emp.getHireDate(),
                emp.getStatus(),
                emp.getProfileImage(),
                emp.getAddress(),
                emp.getEmergencyContactName(),
                emp.getEmergencyContactPhone(),
                emp.getBankName(),
                emp.getBankAccountName(),
                emp.getBankAccountNumber(),
                emp.getEducationLevel(),
                emp.getEducationField(),
                deptDto,
                userDto
        );
    }
}
