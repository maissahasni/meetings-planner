package com.example.backend.service;

import com.example.backend.entity.Agenda;
import com.example.backend.entity.AgendaStatus;
import com.example.backend.entity.Meeting;
import com.example.backend.entity.User;
import com.example.backend.exception.InvalidRequestException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.AgendaRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendaService {

    private final AgendaRepository agendaRepository;
    private final UserRepository userRepository;

    @Transactional
    public Agenda createAgenda(Agenda agenda) {
        User user = userRepository.findById(agenda.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User with id " + agenda.getUser().getId() + " not found"));
        
        if (agenda.getStartTime().isAfter(agenda.getEndTime())) {
            throw new InvalidRequestException("Start time must be before end time");
        }
        
        agenda.setUser(user);
        return agendaRepository.save(agenda);
    }
    
    @Transactional
    public void createAgendaFromMeeting(User user, LocalDateTime meetingStart, LocalDateTime meetingEnd, Meeting meeting) {
        Agenda agenda = Agenda.builder()
                .user(user)
                .meeting(meeting)
                .date(meetingStart.toLocalDate())
                .startTime(meetingStart.toLocalTime())
                .endTime(meetingEnd.toLocalTime())
                .status(AgendaStatus.BUSY)
                .build();
        agendaRepository.save(agenda);
    }

    public Agenda getAgendaById(Long id) {
        return agendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agenda with id " + id + " not found"));
    }

    public List<Agenda> getAgendasByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User with id " + userId + " not found");
        }
        return agendaRepository.findByUserId(userId);
    }

    public List<Agenda> getAgendasByUserAndDate(Long userId, LocalDate date) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User with id " + userId + " not found");
        }
        return agendaRepository.findByUserIdAndDate(userId, date);
    }

    @Transactional
    public Agenda updateAgenda(Long id, Agenda agenda) {
        Agenda existingAgenda = getAgendaById(id);
        
        if (agenda.getStartTime().isAfter(agenda.getEndTime())) {
            throw new InvalidRequestException("Start time must be before end time");
        }
        
        existingAgenda.setDate(agenda.getDate());
        existingAgenda.setStartTime(agenda.getStartTime());
        existingAgenda.setEndTime(agenda.getEndTime());
        existingAgenda.setStatus(agenda.getStatus());
        
        return agendaRepository.save(existingAgenda);
    }

    @Transactional
    public void deleteAgenda(Long id) {
        Agenda agenda = getAgendaById(id);
        agendaRepository.delete(agenda);
    }

    public boolean isUserAvailable(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User with id " + userId + " not found");
        }
        
        List<Agenda> agendas = agendaRepository.findByUserIdAndDate(userId, startTime.toLocalDate());
        
        return agendas.stream().noneMatch(agenda -> 
            agenda.getStatus().toString().equals("BUSY") &&
            !(endTime.toLocalTime().isBefore(agenda.getStartTime()) || 
              startTime.toLocalTime().isAfter(agenda.getEndTime()))
        );
    }
}
