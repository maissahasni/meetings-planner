package com.example.backend.dto;

import com.example.backend.entity.AgendaStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgendaResponseDTO {
    private Long id;
    private Long userId;
    private String userName;
    private Long meetingId;
    private String meetingTitle;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private AgendaStatus status;
}
