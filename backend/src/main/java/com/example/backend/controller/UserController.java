package com.example.backend.controller;

import com.example.backend.dto.UserRequestDTO;
import com.example.backend.dto.UserResponseDTO;
import com.example.backend.dto.UserUpdateRequestDTO;
import com.example.backend.entity.User;
import com.example.backend.mapper.UserMapper;
import com.example.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserRequestDTO request) {
        User user = userMapper.toEntity(request);
        User createdUser = userService.createUser(user);
        return new ResponseEntity<>(userMapper.toResponseDTO(createdUser), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable("id") Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(userMapper.toResponseDTO(user));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(userMapper.toResponseDTOList(users));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable("id") Long id, 
            @RequestBody UserRequestDTO request) {
        User user = userMapper.toEntity(request);
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(userMapper.toResponseDTO(updatedUser));
    }

    /**
     * Alternative update endpoint that reads the user id from the request body instead
     * of the URL path. This avoids any potential issues with parameter name discovery.
     */
    @PutMapping
    public ResponseEntity<UserResponseDTO> updateUserByBody(@RequestBody UserUpdateRequestDTO request) {
        UserRequestDTO dto = new UserRequestDTO(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                request.getRole()
        );
        User user = userMapper.toEntity(dto);
        User updatedUser = userService.updateUser(request.getId(), user);
        return ResponseEntity.ok(userMapper.toResponseDTO(updatedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Alternative delete endpoint that reads the user id from the request body.
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteUserByBody(@RequestBody UserUpdateRequestDTO request) {
        userService.deleteUser(request.getId());
        return ResponseEntity.noContent().build();
    }
}
