package com.example.demo.services;

import com.example.demo.dtos.EmployeeResponse;
import com.example.demo.dtos.common.ApiResponse;
import com.example.demo.entities.Employee;
import com.example.demo.repositories.DepartmentRepository;
import com.example.demo.repositories.EmployeeRepository;
import com.example.demo.repositories.RoleRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private UserService userService;

    public ResponseEntity<?> getAllActiveEmployees() {
        List<Employee> activeEmployees = employeeRepository.findByUser_EnableTrue();

        List<EmployeeResponse> responses = activeEmployees.stream()
                .map(emp -> userService.mapToEmployeeResponse(emp))
                .toList();

        return ResponseEntity.ok(ApiResponse.success("Success", responses));
    }

    public ResponseEntity<?> updateEmployee(String id, Map<String, Object> updates) {
        return employeeRepository.findById(id).map(employee -> {
            // --- Basic Info ---
            if (updates.containsKey("firstName")) employee.setFirstName((String) updates.get("firstName"));
            if (updates.containsKey("lastName")) employee.setLastName((String) updates.get("lastName"));
            if (updates.containsKey("email")) employee.setEmail((String) updates.get("email"));
            if (updates.containsKey("phone")) employee.setPhone((String) updates.get("phone"));
            if (updates.containsKey("gender")) employee.setGender((String) updates.get("gender"));
            if (updates.containsKey("nationalId")) employee.setNationalId((String) updates.get("nationalId"));
            if (updates.containsKey("maritalStatus")) employee.setMaritalStatus((String) updates.get("maritalStatus"));
            if (updates.containsKey("address")) employee.setAddress((String) updates.get("address"));
            
            // --- Dates ---
            if (updates.containsKey("dateOfBirth")) {
                employee.setDateOfBirth(parseDate(updates.get("dateOfBirth")));
            }
            if (updates.containsKey("hireDate")) {
                employee.setHireDate(parseDate(updates.get("hireDate")));
            }

            // --- Work Info ---
            if (updates.containsKey("position")) employee.setPosition((String) updates.get("position"));
            if (updates.containsKey("jobType")) employee.setJobType((String) updates.get("jobType"));
            if (updates.containsKey("status")) employee.setStatus((String) updates.get("status"));
            
            if (updates.containsKey("salary")) {
                Object salary = updates.get("salary");
                if (salary instanceof Number) {
                    employee.setSalary(((Number) salary).doubleValue());
                } else if (salary instanceof String) {
                    try { employee.setSalary(Double.parseDouble((String) salary)); } catch(Exception e){}
                }
            }

            // --- Emergency Contact ---
            if (updates.containsKey("emergencyContactName")) employee.setEmergencyContactName((String) updates.get("emergencyContactName"));
            if (updates.containsKey("emergencyContactPhone")) employee.setEmergencyContactPhone((String) updates.get("emergencyContactPhone"));

            // --- Bank Info ---
            if (updates.containsKey("bankName")) employee.setBankName((String) updates.get("bankName"));
            if (updates.containsKey("bankAccountName")) employee.setBankAccountName((String) updates.get("bankAccountName"));
            if (updates.containsKey("bankAccountNumber")) employee.setBankAccountNumber((String) updates.get("bankAccountNumber"));

            // --- Education ---
            if (updates.containsKey("educationLevel")) employee.setEducationLevel((String) updates.get("educationLevel"));
            if (updates.containsKey("educationField")) employee.setEducationField((String) updates.get("educationField"));

            // --- Department ---
            if (updates.containsKey("department")) {
                Object deptObj = updates.get("department");
                if (deptObj instanceof java.util.Map) {
                    String deptId = (String) ((java.util.Map<?, ?>) deptObj).get("deptId");
                    if (deptId != null) {
                        departmentRepository.findById(deptId).ifPresent(employee::setDepartment);
                    }
                }
            }

            // --- Profile Image (Base64) ---
            if (updates.containsKey("profileImage")) {
                String imgData = (String) updates.get("profileImage");
                if (imgData != null && imgData.startsWith("data:image")) {
                    try {
                        String savedPath = saveBase64Image(imgData);
                        employee.setProfileImage(savedPath);
                    } catch (IOException e) {
                        System.err.println("Failed to save profile image: " + e.getMessage());
                    }
                }
            }

            // --- User account updates ---
            if (updates.containsKey("user") && employee.getUser() != null) {
                Object userObj = updates.get("user");
                if (userObj instanceof java.util.Map) {
                    java.util.Map<?, ?> userMap = (java.util.Map<?, ?>) userObj;
                    com.example.demo.entities.User user = employee.getUser();
                    
                    if (userMap.containsKey("username")) user.setUsername((String) userMap.get("username"));
                    if (userMap.containsKey("isEnable")) user.setEnable((Boolean) userMap.get("isEnable"));
                    
                    // Handle password update if not empty
                    String newPass = (String) userMap.get("password");
                    if (newPass != null && !newPass.trim().isEmpty()) {
                        user.setPassword(encoder.encode(newPass));
                    }
                    
                    // Handle Role updates
                    if (userMap.containsKey("roles")) {
                        Object rolesObj = userMap.get("roles");
                        System.out.println("Processing roles update: " + rolesObj);
                        if (rolesObj instanceof java.util.List) {
                            java.util.List<?> rolesList = (java.util.List<?>) rolesObj;
                            user.getRoles().clear();
                            for (Object roleItem : rolesList) {
                                if (roleItem instanceof String roleName) {
                                    System.out.println("Searching for role: " + roleName);
                                    roleRepository.findByRoleName(roleName)
                                        .or(() -> roleRepository.findById(roleName))
                                        .ifPresent(r -> {
                                            System.out.println("Found role! Adding to user: " + r.getRoleName());
                                            user.getRoles().add(r);
                                        });
                                }
                            }
                        }
                    }
                    userRepository.saveAndFlush(user);
                }
            }

            employeeRepository.save(employee);
            return ResponseEntity.ok(ApiResponse.success("Employee updated successfully", userService.mapToEmployeeResponse(employee)));
        }).orElse(ResponseEntity.notFound().build());
    }

    private LocalDate parseDate(Object dateObj) {
        if (dateObj == null) return null;
        if (dateObj instanceof String dateStr) {
            if (dateStr.trim().isEmpty()) return null;
            try {
                // Handle ISO string like "2024-03-19T08:49:17.000Z"
                return LocalDate.parse(dateStr.substring(0, 10));
            } catch (Exception e) {
                try {
                    return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                } catch (Exception e2) {
                    return null;
                }
            }
        }
        return null;
    }

    private String saveBase64Image(String base64Data) throws IOException {
        String base64Image = base64Data.split(",")[1];
        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        
        String extension = "png";
        if (base64Data.contains("image/jpeg")) extension = "jpg";
        else if (base64Data.contains("image/gif")) extension = "gif";
        
        String fileName = UUID.randomUUID().toString() + "." + extension;
        Path uploadPath = Paths.get("uploads/users/");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        Files.write(uploadPath.resolve(fileName), imageBytes);
        return "/uploads/users/" + fileName;
    }
}
