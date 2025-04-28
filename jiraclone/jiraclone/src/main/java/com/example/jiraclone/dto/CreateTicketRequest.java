package com.example.jiraclone.dto;
import lombok.Data;
@Data public class CreateTicketRequest { private String title; private String description; private Long assignedUserId; }