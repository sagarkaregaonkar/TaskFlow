package com.example.jiraclone.service;

import com.example.jiraclone.dto.CreateTicketRequest;
import com.example.jiraclone.dto.TicketDto;
import com.example.jiraclone.entity.Project;
import com.example.jiraclone.entity.Ticket;
import com.example.jiraclone.entity.User;
import com.example.jiraclone.repository.ProjectRepository;
import com.example.jiraclone.repository.TicketRepository;
import com.example.jiraclone.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public TicketService(TicketRepository ticketRepository, ProjectRepository projectRepository, UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TicketDto createTicket(Long projectId, CreateTicketRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + projectId));

        User assignedUser = null;
        if (request.getAssignedUserId() != null) {
            assignedUser = userRepository.findById(request.getAssignedUserId())
                    .orElseThrow(() -> new EntityNotFoundException("Assigned user not found with id: " + request.getAssignedUserId()));
        }

        Ticket ticket = new Ticket();
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setProject(project);
        ticket.setAssignedTo(assignedUser);
        ticket.setStatus("OPEN"); // Default status

        Ticket savedTicket = ticketRepository.save(ticket);
        return mapToTicketDto(savedTicket);
    }

    public List<TicketDto> getTicketsByProjectId(Long projectId) {
         if (!projectRepository.existsById(projectId)) {
             throw new EntityNotFoundException("Project not found with id: " + projectId);
         }
        return ticketRepository.findByProjectId(projectId).stream()
                .map(this::mapToTicketDto)
                .collect(Collectors.toList());
    }

    // Optional: Assign/Reassign ticket
    @Transactional
    public TicketDto assignTicket(Long ticketId, Long userId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found with id: " + ticketId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        ticket.setAssignedTo(user);
        Ticket updatedTicket = ticketRepository.save(ticket);
         return mapToTicketDto(updatedTicket);
    }
    
    

    private TicketDto mapToTicketDto(Ticket ticket) {
        TicketDto dto = new TicketDto();
        dto.setId(ticket.getId());
        dto.setTitle(ticket.getTitle());
        dto.setDescription(ticket.getDescription());
        dto.setStatus(ticket.getStatus());
        if (ticket.getProject() != null) {
            dto.setProjectId(ticket.getProject().getId());
        }
        if (ticket.getAssignedTo() != null) {
            dto.setAssignedUserId(ticket.getAssignedTo().getId());
            dto.setAssignedUserName(ticket.getAssignedTo().getName());
        }
        return dto;
    }
}