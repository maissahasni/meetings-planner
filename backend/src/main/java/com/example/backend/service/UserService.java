package com.example.backend.service;

import com.example.backend.entity.Agenda;
import com.example.backend.entity.Meeting;
import com.example.backend.entity.User;
import com.example.backend.exception.DuplicateResourceException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.AgendaRepository;
import com.example.backend.repository.MeetingRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final MeetingRepository meetingRepository;
    private final AgendaRepository agendaRepository;

    @Transactional
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateResourceException("User with email " + user.getEmail() + " already exists");
        }
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with email " + email + " not found"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User updateUser(Long id, User user) {
        User existingUser = getUserById(id);
        
        if (!existingUser.getEmail().equals(user.getEmail()) && 
            userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateResourceException("User with email " + user.getEmail() + " already exists");
        }
        
        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setRole(user.getRole());
        
        // Update password only if provided
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(user.getPassword());
        }
        
        return userRepository.save(existingUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);
        
        // Step 1: Remove user from all meetings where they are a participant
        List<Meeting> participatingMeetings = meetingRepository.findByParticipantsId(id);
        for (Meeting meeting : participatingMeetings) {
            meeting.getParticipants().remove(user);
            meetingRepository.save(meeting);
        }
        
        // Step 2: Delete all agendas for this user
        List<Agenda> userAgendas = agendaRepository.findByUserId(id);
        agendaRepository.deleteAll(userAgendas);
        
        // Step 3: Delete all meetings organized by this user
        List<Meeting> organizedMeetings = meetingRepository.findByOrganizerId(id);
        meetingRepository.deleteAll(organizedMeetings);
        
        // Step 4: Finally delete the user
        userRepository.delete(user);
    }
}
