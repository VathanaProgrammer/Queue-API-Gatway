package com.example.demo.utils;

import com.example.demo.entities.Permission;
import com.example.demo.entities.Role;
import com.example.demo.repositories.PermissionRepository;
import com.example.demo.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PermissionSeeder implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public void run(String... args) throws Exception {
        // 1. Define all system permissions
        String[] permissions = {
            "CAN_SEE_DASHBOARD",
            "CAN_MANAGE_USERS",
            "CAN_MANAGE_DEPARTMENTS",
            "CAN_MANAGE_ROLES",
            "CAN_MANAGE_LEAVE",
            "CAN_MANAGE_RESIGNATION"
        };

        // 2. Ensure ADMIN role exists
        Role adminRole = roleRepository.findByRoleName("ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ADMIN")));

        // 3. Seed permissions and assign to ADMIN
        for (String permName : permissions) {
            Permission permission = permissionRepository.findByPermName(permName)
                    .orElseGet(() -> permissionRepository.save(new Permission(permName)));

            boolean alreadyHas = adminRole.getPermissions().stream()
                    .anyMatch(p -> p.getPermName().equals(permName));
            
            if (!alreadyHas) {
                adminRole.addPermission(permission);
                System.out.println("Seeded: Assigned " + permName + " to ADMIN role.");
            }
        }
        
        // 4. Seeding permissions to STAFF role (basic access)
        Role staffRole = roleRepository.findByRoleName("STAFF")
                .orElseGet(() -> roleRepository.save(new Role("STAFF")));
        
        Permission dashboardPerm = permissionRepository.findByPermName("CAN_SEE_DASHBOARD")
                .orElseGet(() -> permissionRepository.save(new Permission("CAN_SEE_DASHBOARD")));
        
        if (!staffRole.getPermissions().contains(dashboardPerm)) {
            staffRole.addPermission(dashboardPerm);
            roleRepository.save(staffRole);
            System.out.println("Seeded: Assigned CAN_SEE_DASHBOARD to STAFF role.");
        }
    }
}
