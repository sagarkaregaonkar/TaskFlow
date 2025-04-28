package com.example.jiraclone.dto;
import lombok.Data;
@Data public class TicketDto { private Long id; private String title; private String description; private String status; private Long projectId; private Long assignedUserId; private String assignedUserName; }