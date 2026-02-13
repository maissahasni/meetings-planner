package com.example.backend.repository;

import com.example.backend.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    List<Meeting> findByOrganizerId(Long organizerId);
    List<Meeting> findByParticipantsId(Long participantId);
    List<Meeting> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
}
