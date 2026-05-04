package com.example.demo.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "PERMISSIONS")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, updatable = false, nullable = false)
    private String permId;

    @Column(unique = true, nullable = false)
    private String permName;

    // --- CONSTRUCTORS ---

    public Permission() {
    }

    public Permission(String permName) {
        this.permName = permName;
    }

    // --- GETTERS AND SETTERS ---

    public String getPermId() {
        return permId;
    }

    public void setPermId(String permId) {
        this.permId = permId;
    }

    public String getPermName() {
        return permName;
    }

    public void setPermName(String permName) {
        this.permName = permName;
    }
}