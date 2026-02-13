package com.example.backend.repository;

import com.example.backend.entity.Agenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AgendaRepository extends JpaRepository<Agenda, Long> {
    List<Agenda> findByUserId(Long userId);
    List<Agenda> findByUserIdAndDate(Long userId, LocalDate date);
    List<Agenda> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
}
