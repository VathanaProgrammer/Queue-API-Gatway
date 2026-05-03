package com.example.demo.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "tbl_users")
public class User {

    // ១. បន្ថែម Default Constructor សម្រាប់ Hibernate
    public User() {
    }

    // ២. Constructor ដែលបងប្រើសម្រាប់បង្កើត User ថ្មីក្នុង Service
    public User(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    @NotBlank(message = "Name cannot be null")
    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public User(String name) {
        this.name = name;
    }

}
