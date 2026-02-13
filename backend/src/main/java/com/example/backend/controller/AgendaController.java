package com.example.backend.controller;

import com.example.backend.dto.AgendaRequestDTO;
import com.example.backend.dto.AgendaResponseDTO;
import com.example.backend.entity.Agenda;
import com.example.backend.entity.User;
import com.example.backend.mapper.AgendaMapper;
import com.example.backend.service.AgendaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agendas")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class AgendaController {

    private final AgendaService agendaService;
    private final AgendaMapper agendaMapper;

    @PostMapping
    public ResponseEntity<AgendaResponseDTO> createAgenda(@RequestBody AgendaRequestDTO request) {
        Agenda agenda = agendaMapper.toEntity(request);
        
        User user = new User();
        user.setId(request.getUserId());
        agenda.setUser(user);
        
        Agenda createdAgenda = agendaService.createAgenda(agenda);
        return new ResponseEntity<>(agendaMapper.toResponseDTO(createdAgenda), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgendaResponseDTO> getAgendaById(@PathVariable("id") Long id) {
        Agenda agenda = agendaService.getAgendaById(id);
        return ResponseEntity.ok(agendaMapper.toResponseDTO(agenda));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AgendaResponseDTO>> getAgendasByUser(@PathVariable("userId") Long userId) {
        List<Agenda> agendas = agendaService.getAgendasByUser(userId);
        return ResponseEntity.ok(agendaMapper.toResponseDTOList(agendas));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgendaResponseDTO> updateAgenda(
            @PathVariable("id") Long id, 
            @RequestBody AgendaRequestDTO request) {
        Agenda agenda = agendaMapper.toEntity(request);
        Agenda updatedAgenda = agendaService.updateAgenda(id, agenda);
        return ResponseEntity.ok(agendaMapper.toResponseDTO(updatedAgenda));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAgenda(@PathVariable("id") Long id) {
        agendaService.deleteAgenda(id);
        return ResponseEntity.noContent().build();
    }
}
