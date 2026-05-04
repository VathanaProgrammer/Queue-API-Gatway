package com.example.demo.entities;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ROLES") // Keep it in your Oracle SYSTEM schema
public class Role {

    @Id
    @Column(length = 36, updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String roleId;

    @Column(unique = true, nullable = false)
    private String roleName;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "ROLE_PERMISSIONS", // Better name for mapping Roles to Permissions
            schema = "DEV_VATHANA",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();

    // --- CONSTRUCTORS ---

    public Role() {
    }

    public Role(String roleName) {
        this.roleName = roleName;
    }

    // --- GETTERS AND SETTERS ---

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    // --- HELPER METHODS ---

    public void addPermission(Permission permission) {
        this.permissions.add(permission);
    }
}