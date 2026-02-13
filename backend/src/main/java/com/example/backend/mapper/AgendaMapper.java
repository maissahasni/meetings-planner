package com.example.backend.mapper;

import com.example.backend.dto.AgendaRequestDTO;
import com.example.backend.dto.AgendaResponseDTO;
import com.example.backend.entity.Agenda;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AgendaMapper {

    public Agenda toEntity(AgendaRequestDTO dto) {
        return Agenda.builder()
                .date(dto.getDate())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .status(dto.getStatus())
                .build();
    }

    public AgendaResponseDTO toResponseDTO(Agenda entity) {
        return AgendaResponseDTO.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .userName(entity.getUser().getName())
                .meetingId(entity.getMeeting() != null ? entity.getMeeting().getId() : null)
                .meetingTitle(entity.getMeeting() != null ? entity.getMeeting().getTitle() : null)
                .date(entity.getDate())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .status(entity.getStatus())
                .build();
    }

    public List<AgendaResponseDTO> toResponseDTOList(List<Agenda> entities) {
        return entities.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public void updateEntityFromDTO(AgendaRequestDTO dto, Agenda entity) {
        entity.setDate(dto.getDate());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setStatus(dto.getStatus());
    }
}
