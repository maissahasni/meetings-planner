package com.example.backend.service;

import com.example.backend.entity.Meeting;
import com.example.backend.entity.User;
import com.example.backend.exception.InvalidRequestException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.MeetingRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;
    private final AgendaService agendaService;

    @Transactional
    public Meeting createMeeting(Meeting meeting) {
        if (meeting.getStartTime().isAfter(meeting.getEndTime())) {
            throw new InvalidRequestException("Start time must be before end time");
        }
        
        User organizer = userRepository.findById(meeting.getOrganizer().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Organizer with id " + meeting.getOrganizer().getId() + " not found"));
        
        // Check if organizer has conflicting meetings
        List<Meeting> organizerMeetings = meetingRepository.findByOrganizerId(organizer.getId());
        for (Meeting existingMeeting : organizerMeetings) {
            if (hasTimeConflict(meeting.getStartTime(), meeting.getEndTime(), 
                               existingMeeting.getStartTime(), existingMeeting.getEndTime())) {
                throw new InvalidRequestException(
                    "Organizer already has a meeting scheduled between " + 
                    existingMeeting.getStartTime() + " and " + existingMeeting.getEndTime());
            }
        }
        
        meeting.setOrganizer(organizer);
        
        if (meeting.getParticipants() != null && !meeting.getParticipants().isEmpty()) {
            List<User> participants = new ArrayList<>();
            for (User participant : meeting.getParticipants()) {
                User user = userRepository.findById(participant.getId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Participant with id " + participant.getId() + " not found"));
                
                // Check if participant has conflicting meetings
                List<Meeting> participantMeetings = meetingRepository.findByParticipantsId(user.getId());
                for (Meeting existingMeeting : participantMeetings) {
                    if (hasTimeConflict(meeting.getStartTime(), meeting.getEndTime(), 
                                       existingMeeting.getStartTime(), existingMeeting.getEndTime())) {
                        throw new InvalidRequestException(
                            user.getName() + " already has a meeting scheduled between " + 
                            existingMeeting.getStartTime() + " and " + existingMeeting.getEndTime());
                    }
                }
                
                participants.add(user);
            }
            meeting.setParticipants(participants);
        }
        
        Meeting savedMeeting = meetingRepository.save(meeting);
        
        // Create agenda for organizer
        agendaService.createAgendaFromMeeting(organizer, meeting.getStartTime(), meeting.getEndTime(), savedMeeting);
        
        // Create agendas for all participants
        if (savedMeeting.getParticipants() != null) {
            for (User participant : savedMeeting.getParticipants()) {
                agendaService.createAgendaFromMeeting(participant, meeting.getStartTime(), meeting.getEndTime(), savedMeeting);
            }
        }
        
        return savedMeeting;
    }
    
    private boolean hasTimeConflict(LocalDateTime start1, LocalDateTime end1, 
                                    LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && end1.isAfter(start2);
    }

    public Meeting getMeetingById(Long id) {
        return meetingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meeting with id " + id + " not found"));
    }

    public List<Meeting> getAllMeetings() {
        return meetingRepository.findAll();
    }

    public List<Meeting> getMeetingsByOrganizer(Long organizerId) {
        if (!userRepository.existsById(organizerId)) {
            throw new ResourceNotFoundException("User with id " + organizerId + " not found");
        }
        return meetingRepository.findByOrganizerId(organizerId);
    }

    public List<Meeting> getMeetingsByParticipant(Long participantId) {
        if (!userRepository.existsById(participantId)) {
            throw new ResourceNotFoundException("User with id " + participantId + " not found");
        }
        return meetingRepository.findByParticipantsId(participantId);
    }
    
    public List<Meeting> getMeetingsByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User with id " + userId + " not found");
        }
        // Get meetings where user is organizer or participant
        List<Meeting> organizerMeetings = meetingRepository.findByOrganizerId(userId);
        List<Meeting> participantMeetings = meetingRepository.findByParticipantsId(userId);
        
        // Combine and remove duplicates
        List<Meeting> allMeetings = new ArrayList<>(organizerMeetings);
        for (Meeting meeting : participantMeetings) {
            if (!allMeetings.contains(meeting)) {
                allMeetings.add(meeting);
            }
        }
        return allMeetings;
    }

    @Transactional
    public Meeting updateMeeting(Long id, Meeting meeting) {
        Meeting existingMeeting = getMeetingById(id);
        
        if (meeting.getStartTime().isAfter(meeting.getEndTime())) {
            throw new InvalidRequestException("Start time must be before end time");
        }
        
        existingMeeting.setTitle(meeting.getTitle());
        existingMeeting.setDescription(meeting.getDescription());
        existingMeeting.setStartTime(meeting.getStartTime());
        existingMeeting.setEndTime(meeting.getEndTime());
        
        return meetingRepository.save(existingMeeting);
    }

    @Transactional
    public void deleteMeeting(Long id) {
        Meeting meeting = getMeetingById(id);
        meetingRepository.delete(meeting);
    }

    @Transactional
    public Meeting addParticipant(Long meetingId, Long userId) {
        Meeting meeting = getMeetingById(meetingId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));
        
        if (meeting.getParticipants() == null) {
            meeting.setParticipants(new ArrayList<>());
        }
        
        if (!meeting.getParticipants().contains(user)) {
            meeting.getParticipants().add(user);
            return meetingRepository.save(meeting);
        }
        
        return meeting;
    }

    @Transactional
    public Meeting removeParticipant(Long meetingId, Long userId) {
        Meeting meeting = getMeetingById(meetingId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));
        
        if (meeting.getParticipants() != null) {
            meeting.getParticipants().remove(user);
            return meetingRepository.save(meeting);
        }
        
        return meeting;
    }
}
