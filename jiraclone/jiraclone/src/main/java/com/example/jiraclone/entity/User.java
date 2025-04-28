package com.example.jiraclone.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Entity
@Table(name = "users") // "user" is often a reserved keyword
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    // User can create many projects (optional mapping, not strictly needed for requirements)
    // @OneToMany(mappedBy = "createdBy")
    // private Set<Project> createdProjects;

    // User can be assigned many tickets
    @OneToMany(mappedBy = "assignedTo")
    private Set<Ticket> assignedTickets;

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }
}