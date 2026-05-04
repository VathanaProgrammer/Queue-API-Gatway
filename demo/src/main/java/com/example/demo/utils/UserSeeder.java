package com.example.demo.utils;

import com.example.demo.entities.Employee;
import com.example.demo.entities.Role;
import com.example.demo.entities.User;
import com.example.demo.repositories.EmployeeRepository;
import com.example.demo.repositories.RoleRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.core.annotation.Order;

import java.time.LocalDate;

@Component
@Order(2) // Run after PermissionSeeder
public class UserSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public void run(String... args) throws Exception {
        // 1. Ensure STAFF role exists
        Role staffRole = roleRepository.findByRoleName("STAFF")
                .orElseGet(() -> roleRepository.save(new Role("STAFF")));

        // 2. Seed a STAFF user and employee with ALL columns
        String staffUsername = "staff_user";
        if (!userRepository.existsByUsername(staffUsername)) {
            User staffUser = new User();
            staffUser.setUsername(staffUsername);
            staffUser.setPassword(encoder.encode("password123"));
            staffUser.setEnable(true);
            staffUser.getRoles().add(staffRole);
            User savedStaffUser = userRepository.save(staffUser);

            Employee staffEmp = employeeRepository.findByEmail("staff@example.com");
            if (staffEmp == null) {
                staffEmp = new Employee();
            }
            
            if (staffEmp.getEmpId() == null) {
                // Basic Info
                staffEmp.setFirstName("Vathana");
                staffEmp.setLastName("Staff");
                staffEmp.setEmail("staff@example.com");
                staffEmp.setPhone("+855 12 345 678");
                staffEmp.setGender("Male");
                staffEmp.setDateOfBirth(LocalDate.of(2000, 1, 1));
                staffEmp.setNationalId("ID-STAFF-001");
                staffEmp.setMaritalStatus("Single");
                
                // Work Info
                staffEmp.setPosition("Frontend Developer");
                staffEmp.setJobType("FULL_TIME");
                staffEmp.setSalary(1200.0);
                staffEmp.setHireDate(LocalDate.now());
                staffEmp.setStatus("ACTIVE");
                staffEmp.setAddress("Phnom Penh, Cambodia");
                staffEmp.setProfileImage("/uploads/users/default-profile.png");
                
                // Emergency Contact
                staffEmp.setEmergencyContactName("Emergency Contact Name");
                staffEmp.setEmergencyContactPhone("+855 88 111 222");
                
                // Bank Info
                staffEmp.setBankName("ABA Bank");
                staffEmp.setBankAccountName("VATHANA STAFF");
                staffEmp.setBankAccountNumber("000 123 456");
                
                // Education
                staffEmp.setEducationLevel("Bachelor");
                staffEmp.setEducationField("Computer Science");
            }
            
            // Relationships
            staffEmp.setUser(savedStaffUser);
            employeeRepository.save(staffEmp);

            System.out.println("Seeded: Created user 'staff_user' with full Employee profile.");
        }
        
        // 3. Ensure ADMIN user exists and has a full employee profile
        String adminUsername = "admin";
        if (!userRepository.existsByUsername(adminUsername)) {
            Role adminRole = roleRepository.findByRoleName("ADMIN")
                    .orElseGet(() -> roleRepository.save(new Role("ADMIN")));
            
            User adminUser = new User();
            adminUser.setUsername(adminUsername);
            adminUser.setPassword(encoder.encode("admin123"));
            adminUser.setEnable(true);
            adminUser.getRoles().add(adminRole);
            User savedAdminUser = userRepository.save(adminUser);

            Employee adminEmp = employeeRepository.findByEmail("admin@example.com");
            if (adminEmp == null) {
                adminEmp = new Employee();
            }
            
            if (adminEmp.getEmpId() == null) {
                // Basic Info
                adminEmp.setFirstName("Vathana");
                adminEmp.setLastName("Admin");
                adminEmp.setEmail("admin@example.com");
                adminEmp.setPhone("+855 10 999 000");
                adminEmp.setGender("Male");
                adminEmp.setDateOfBirth(LocalDate.of(1995, 5, 15));
                adminEmp.setNationalId("ID-ADMIN-001");
                adminEmp.setMaritalStatus("Married");

                // Work Info
                adminEmp.setPosition("System Administrator");
                adminEmp.setJobType("FULL_TIME");
                adminEmp.setSalary(3500.0);
                adminEmp.setHireDate(LocalDate.of(2023, 1, 1));
                adminEmp.setStatus("ACTIVE");
                adminEmp.setAddress("Main HQ Office, Phnom Penh");
                adminEmp.setProfileImage("/uploads/users/default-profile.png");

                // Emergency Info
                adminEmp.setEmergencyContactName("Admin Sister");
                adminEmp.setEmergencyContactPhone("+855 11 000 111");

                // Bank Info
                adminEmp.setBankName("Canadia Bank");
                adminEmp.setBankAccountName("VATHANA ADMIN");
                adminEmp.setBankAccountNumber("999 888 777");

                // Education
                adminEmp.setEducationLevel("Master");
                adminEmp.setEducationField("Information Technology");
            }

            // Relationships
            adminEmp.setUser(savedAdminUser);
            employeeRepository.save(adminEmp);

            System.out.println("Seeded: Created user 'admin' with full Employee profile.");
        }
    }
}
