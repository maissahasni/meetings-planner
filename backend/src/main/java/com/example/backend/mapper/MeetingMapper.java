package com.example.backend.mapper;

import com.example.backend.dto.MeetingRequestDTO;
import com.example.backend.dto.MeetingResponseDTO;
import com.example.backend.entity.Meeting;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MeetingMapper {

    private final UserMapper userMapper;

    public Meeting toEntity(MeetingRequestDTO dto) {
        return Meeting.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .build();
    }

    public MeetingResponseDTO toResponseDTO(Meeting entity) {
        return MeetingResponseDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .organizer(userMapper.toResponseDTO(entity.getOrganizer()))
                .participants(entity.getParticipants() != null ? 
                        userMapper.toResponseDTOList(entity.getParticipants()) : List.of())
                .build();
    }

    public List<MeetingResponseDTO> toResponseDTOList(List<Meeting> entities) {
        return entities.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public void updateEntityFromDTO(MeetingRequestDTO dto, Meeting entity) {
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
    }
}
