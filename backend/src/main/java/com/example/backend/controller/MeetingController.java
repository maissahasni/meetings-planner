package com.example.backend.controller;

import com.example.backend.dto.MeetingRequestDTO;
import com.example.backend.dto.MeetingResponseDTO;
import com.example.backend.entity.Meeting;
import com.example.backend.entity.User;
import com.example.backend.mapper.MeetingMapper;
import com.example.backend.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class MeetingController {

    private final MeetingService meetingService;
    private final MeetingMapper meetingMapper;

    @PostMapping
    public ResponseEntity<MeetingResponseDTO> createMeeting(@RequestBody MeetingRequestDTO request) {
        Meeting meeting = meetingMapper.toEntity(request);
        
        User organizer = new User();
        organizer.setId(request.getOrganizerId());
        meeting.setOrganizer(organizer);
        
        if (request.getParticipantIds() != null && !request.getParticipantIds().isEmpty()) {
            List<User> participants = request.getParticipantIds().stream()
                    .map(id -> {
                        User user = new User();
                        user.setId(id);
                        return user;
                    })
                    .collect(Collectors.toList());
            meeting.setParticipants(participants);
        }
        
        Meeting createdMeeting = meetingService.createMeeting(meeting);
        return new ResponseEntity<>(meetingMapper.toResponseDTO(createdMeeting), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeetingResponseDTO> getMeetingById(@PathVariable("id") Long id) {
        Meeting meeting = meetingService.getMeetingById(id);
        return ResponseEntity.ok(meetingMapper.toResponseDTO(meeting));
    }

    @GetMapping
    public ResponseEntity<List<MeetingResponseDTO>> getAllMeetings() {
        List<Meeting> meetings = meetingService.getAllMeetings();
        return ResponseEntity.ok(meetingMapper.toResponseDTOList(meetings));
    }

    @GetMapping("/organizer/{organizerId}")
    public ResponseEntity<List<MeetingResponseDTO>> getMeetingsByOrganizer(@PathVariable("organizerId") Long organizerId) {
        List<Meeting> meetings = meetingService.getMeetingsByOrganizer(organizerId);
        return ResponseEntity.ok(meetingMapper.toResponseDTOList(meetings));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MeetingResponseDTO>> getMeetingsByUser(@PathVariable("userId") Long userId) {
        List<Meeting> meetings = meetingService.getMeetingsByUser(userId);
        return ResponseEntity.ok(meetingMapper.toResponseDTOList(meetings));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MeetingResponseDTO> updateMeeting(
            @PathVariable("id") Long id, 
            @RequestBody MeetingRequestDTO request) {
        Meeting meeting = meetingMapper.toEntity(request);
        Meeting updatedMeeting = meetingService.updateMeeting(id, meeting);
        return ResponseEntity.ok(meetingMapper.toResponseDTO(updatedMeeting));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeeting(@PathVariable("id") Long id) {
        meetingService.deleteMeeting(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{meetingId}/participants/{userId}")
    public ResponseEntity<MeetingResponseDTO> addParticipant(
            @PathVariable("meetingId") Long meetingId,
            @PathVariable("userId") Long userId) {
        Meeting meeting = meetingService.addParticipant(meetingId, userId);
        return ResponseEntity.ok(meetingMapper.toResponseDTO(meeting));
    }

    @DeleteMapping("/{meetingId}/participants/{userId}")
    public ResponseEntity<MeetingResponseDTO> removeParticipant(
            @PathVariable("meetingId") Long meetingId,
            @PathVariable("userId") Long userId) {
        Meeting meeting = meetingService.removeParticipant(meetingId, userId);
        return ResponseEntity.ok(meetingMapper.toResponseDTO(meeting));
    }
}
