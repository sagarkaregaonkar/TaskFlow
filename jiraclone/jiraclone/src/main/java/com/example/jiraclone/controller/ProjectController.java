package com.example.jiraclone.controller;

import com.example.jiraclone.dto.CreateProjectRequest;
import com.example.jiraclone.dto.ProjectDto;
import com.example.jiraclone.service.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;


import java.security.Principal; // Use Principal
import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<?> createProject(@RequestBody CreateProjectRequest request, Principal principal) {
         if (principal == null || principal.getName() == null) {
             return new ResponseEntity<>("User must be logged in to create a project", HttpStatus.UNAUTHORIZED);
         }
        try {
            String userEmail = principal.getName();
            ProjectDto createdProject = projectService.createProject(request, userEmail);
            return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
             return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to create project: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<ProjectDto>> getAllProjects() {
        try {
            List<ProjectDto> projects = projectService.getAllProjects();
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
             // Log error
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}