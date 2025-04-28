package com.example.jiraclone.repository;

import com.example.jiraclone.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}