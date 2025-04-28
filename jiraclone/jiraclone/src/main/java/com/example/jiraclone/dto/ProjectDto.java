package com.example.jiraclone.dto;
import lombok.Data; 
@Data public class ProjectDto { 
	private Long id; private String name; 
	private String description; 
	private Long createdById; 
	private String createdByName; 
	}