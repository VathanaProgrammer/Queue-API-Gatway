package com.example.demo.services;

import com.example.demo.dtos.common.ApiResponse;
import com.example.demo.entities.Role;
import com.example.demo.entities.Permission;
import com.example.demo.repositories.RoleRepository;
import com.example.demo.repositories.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Set;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    public ResponseEntity<?> saveRole(String roleName, List<String> permissionNames) {
        Role role = new Role(roleName);
        if (permissionNames != null && !permissionNames.isEmpty()) {
            Set<Permission> permissions = permissionNames.stream()
                    .map(name -> permissionRepository.findByPermName(name))
                    .filter(opt -> opt.isPresent())
                    .map(opt -> opt.get())
                    .collect(Collectors.toSet());
            role.setPermissions(permissions);
        }
        return ResponseEntity.ok(ApiResponse.success("Role created successfully!", roleRepository.save(role)));
    }

    public ResponseEntity<?> updateRole(String id, String roleName, List<String> permissionNames) {
        return roleRepository.findById(id).map(role -> {
            role.setRoleName(roleName);
            if (permissionNames != null) {
                Set<Permission> permissions = permissionNames.stream()
                        .map(name -> permissionRepository.findByPermName(name))
                        .filter(opt -> opt.isPresent())
                        .map(opt -> opt.get())
                        .collect(Collectors.toSet());
                role.setPermissions(permissions);
            }
            return ResponseEntity.ok(ApiResponse.success("Role updated successfully!", roleRepository.save(role)));
        }).orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<?> getAllPermissions() {
        return ResponseEntity.ok(ApiResponse.success("Success", permissionRepository.findAll()));
    }

    public boolean isRoleExist(String roleName) {
        return roleRepository.existsByRoleName(roleName);
    }

    public ResponseEntity<?> getAllRoles() {
        return ResponseEntity.ok(ApiResponse.success("Success", roleRepository.findAll()));
    }

    public ResponseEntity<?> deleteRole(String id) {
        return roleRepository.findById(id).map(role -> {
            roleRepository.delete(role);
            return ResponseEntity.ok(ApiResponse.success("Role deleted successfully!", null));
        }).orElse(ResponseEntity.notFound().build());
    }
}
