package com.example.jiraclone.controller;

import com.example.jiraclone.dto.CreateTicketRequest;
import com.example.jiraclone.dto.TicketDto;
import com.example.jiraclone.service.TicketService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<?> createTicket(@PathVariable Long projectId, @RequestBody CreateTicketRequest request) {
        try {
            TicketDto createdTicket = ticketService.createTicket(projectId, request);
            return new ResponseEntity<>(createdTicket, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to create ticket: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<?> getTicketsForProject(@PathVariable Long projectId) {
         try {
            List<TicketDto> tickets = ticketService.getTicketsByProjectId(projectId);
            return ResponseEntity.ok(tickets);
         } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
         } catch (Exception e) {
             // Log error
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

     // Optional: Endpoint to assign a ticket (needs Ticket ID)
     // This might be better under /api/tickets/{ticketId}/assign
     // Let's add a simple assign endpoint under the project context for now
     @PutMapping("/{ticketId}/assign/{userId}") // Example path, adjust as needed
     public ResponseEntity<?> assignTicket(@PathVariable Long projectId, @PathVariable Long ticketId, @PathVariable Long userId) {
         try {
             // Optional: Check if ticket belongs to project (projectId) before assigning
             TicketDto updatedTicket = ticketService.assignTicket(ticketId, userId);
             return ResponseEntity.ok(updatedTicket);
         } catch (EntityNotFoundException e) {
             return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
         } catch (Exception e) {
             return new ResponseEntity<>("Failed to assign ticket: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
         }
     }
}