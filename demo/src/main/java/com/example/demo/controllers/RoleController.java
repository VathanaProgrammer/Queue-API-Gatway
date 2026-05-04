package com.example.demo.controllers;

import com.example.demo.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping("/role/dev/create")
    @PreAuthorize("hasAuthority('CAN_MANAGE_ROLES') or hasRole('ADMIN')")
    public ResponseEntity<?> saveRole(@RequestParam String roleName, @RequestParam(required = false) List<String> permissions) {
        boolean isRoleExist = roleService.isRoleExist(roleName);
        if(isRoleExist) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Role " + roleName + " already exist!");
        }
        return roleService.saveRole(roleName, permissions);
    }

    @PutMapping("/role/update/{id}")
    @PreAuthorize("hasAuthority('CAN_MANAGE_ROLES') or hasRole('ADMIN')")
    public ResponseEntity<?> updateRole(@PathVariable String id, @RequestParam String roleName, @RequestParam(required = false) List<String> permissions) {
        return roleService.updateRole(id, roleName, permissions);
    }

    @GetMapping("/permissions/all")
    public ResponseEntity<?> getAllPermissions() {
        return roleService.getAllPermissions();
    }

    @GetMapping("/role/all")
    @PreAuthorize(value = "hasAuthority('CAN_MANAGE_ROLES') or hasRole('ADMIN')")
    public ResponseEntity<?> getAllRoles() {
        return roleService.getAllRoles();
    }

    @DeleteMapping("/role/delete/{id}")
    @PreAuthorize("hasAuthority('CAN_MANAGE_ROLES') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteRole(@PathVariable String id) {
        return roleService.deleteRole(id);
    }
}
