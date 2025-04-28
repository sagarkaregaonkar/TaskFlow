package com.example.jiraclone.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private String status = "OPEN"; // Default status

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_user_id") // Can be nullable if not assigned initially
    private User assignedTo;

     public Ticket(String title, String description, Project project, User assignedTo) {
        this.title = title;
        this.description = description;
        this.project = project;
        this.assignedTo = assignedTo;
        this.status = "OPEN"; // Explicitly set default
    }
}