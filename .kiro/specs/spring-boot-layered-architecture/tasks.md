# Implementation Plan: Spring Boot Layered Architecture

## Overview

This implementation plan creates a clean layered architecture for a Spring Boot Meeting Planner application. The implementation follows a bottom-up approach: first creating the package structure, then entities, repositories, services, DTOs, mappers, controllers, exception handling, and configuration. Each layer is tested as it's built to ensure correctness.

## Tasks

- [x] 1. Create package structure
  - Create all eight packages: entity, repository, service, controller, dto, mapper, exception, config
  - Verify BackendApplication.java remains unchanged
  - _Requirements: 1.1, 2.1, 3.1, 4.1, 5.1, 6.1, 7.1, 8.1, 9.1, 9.2, 9.3_

- [x] 2. Implement entity layer with JPA entities
  - [x] 2.1 Create Role enum with ADMIN and USER values
    - Define enum in entity package
    - _Requirements: 11.5_
  
  - [x] 2.2 Create User entity with all fields and relationships
    - Implement id (auto-generated), name (not null), email (unique, not null), role
    - Add @JsonIgnore on bidirectional relationships (organizedMeetings, participatingMeetings, agendas)
    - Use Lombok @Getter, @Setter, @NoArgsConstructor, @AllArgsConstructor, @Builder
    - _Requirements: 11.1, 11.2, 11.3, 11.4, 11.5, 11.6, 11.7_
  
  - [x] 2.3 Create AgendaStatus enum with FREE and BUSY values
    - Define enum in entity package
    - _Requirements: 13.6_
  
  - [x] 2.4 Create Meeting entity with all fields and relationships
    - Implement id, title (not null), description, startTime, endTime (LocalDateTime)
    - Add ManyToOne relationship to User (organizer) with LAZY fetch
    - Add ManyToMany relationship to User (participants) with join table
    - Use Lombok @Getter, @Setter, @NoArgsConstructor, @AllArgsConstructor, @Builder
    - _Requirements: 12.1, 12.2, 12.3, 12.4, 12.5, 12.6, 12.7_
  
  - [x] 2.5 Create Agenda entity with all fields and relationships
    - Implement id, user (ManyToOne with LAZY fetch), date (LocalDate), startTime, endTime (LocalTime), status
    - Use Lombok @Getter, @Setter, @NoArgsConstructor, @AllArgsConstructor, @Builder
    - _Requirements: 13.1, 13.2, 13.3, 13.4, 13.5, 13.6, 13.7_
  
  - [ ]* 2.6 Write property test for entity JPA annotations
    - **Property 1: Entity classes have JPA annotations**
    - **Validates: Requirements 1.3**
  
  - [ ]* 2.7 Write property test for entity fetch types
    - **Property 9: Entities use explicit fetch types**
    - **Validates: Requirements 14.1**
  
  - [ ]* 2.8 Write property test for JSON recursion prevention
    - **Property 10: Bidirectional relationships prevent JSON recursion**
    - **Validates: Requirements 14.2**
  
  - [ ]* 2.9 Write property test for Lombok usage
    - **Property 11: Entities avoid Lombok @Data annotation**
    - **Validates: Requirements 14.5**

- [x] 3. Implement repository layer
  - [x] 3.1 Create UserRepository interface
    - Extend JpaRepository<User, Long>
    - Add findByEmail and existsByEmail methods
    - _Requirements: 2.1, 2.3_
  
  - [x] 3.2 Create MeetingRepository interface
    - Extend JpaRepository<Meeting, Long>
    - Add findByOrganizerId, findByParticipantsId, findByStartTimeBetween methods
    - _Requirements: 2.1, 2.3_
  
  - [x] 3.3 Create AgendaRepository interface
    - Extend JpaRepository<Agenda, Long>
    - Add findByUserId, findByUserIdAndDate, findByUserIdAndDateBetween methods
    - _Requirements: 2.1, 2.3_
  
  - [ ]* 3.4 Write property test for repository interfaces
    - **Property 2: Repository interfaces extend Spring Data repositories**
    - **Validates: Requirements 2.3**
  
  - [ ]* 3.5 Write unit tests for custom repository queries
    - Test findByEmail, findByOrganizerId, findByUserIdAndDate
    - _Requirements: 2.3_

- [x] 4. Implement DTO layer
  - [x] 4.1 Create User DTOs (UserRequestDTO and UserResponseDTO)
    - UserRequestDTO: name, email, role
    - UserResponseDTO: id, name, email, role
    - Use Lombok @Data, @NoArgsConstructor, @AllArgsConstructor, @Builder
    - _Requirements: 5.1, 5.3_
  
  - [x] 4.2 Create Meeting DTOs (MeetingRequestDTO and MeetingResponseDTO)
    - MeetingRequestDTO: title, description, startTime, endTime, organizerId, participantIds
    - MeetingResponseDTO: id, title, description, startTime, endTime, organizer (UserResponseDTO), participants (List<UserResponseDTO>)
    - Use Lombok @Data, @NoArgsConstructor, @AllArgsConstructor, @Builder
    - _Requirements: 5.1, 5.3_
  
  - [x] 4.3 Create Agenda DTOs (AgendaRequestDTO and AgendaResponseDTO)
    - AgendaRequestDTO: userId, date, startTime, endTime, status
    - AgendaResponseDTO: id, userId, userName, date, startTime, endTime, status
    - Use Lombok @Data, @NoArgsConstructor, @AllArgsConstructor, @Builder
    - _Requirements: 5.1, 5.3_
  
  - [ ]* 4.4 Write property test for DTO JPA annotation absence
    - **Property 5: DTOs do not contain JPA annotations**
    - **Validates: Requirements 5.4**

- [x] 5. Implement mapper layer
  - [x] 5.1 Create UserMapper component
    - Implement toEntity, toResponseDTO, toResponseDTOList, updateEntityFromDTO methods
    - Annotate with @Component
    - _Requirements: 6.1, 6.3_
  
  - [x] 5.2 Create MeetingMapper component
    - Implement toEntity, toResponseDTO, toResponseDTOList, updateEntityFromDTO methods
    - Inject UserMapper for nested conversions
    - Annotate with @Component
    - _Requirements: 6.1, 6.3_
  
  - [x] 5.3 Create AgendaMapper component
    - Implement toEntity, toResponseDTO, toResponseDTOList, updateEntityFromDTO methods
    - Annotate with @Component
    - _Requirements: 6.1, 6.3_
  
  - [ ]* 5.4 Write property test for mapper conversion methods
    - **Property 6: Mapper classes have conversion methods**
    - **Validates: Requirements 6.3**
  
  - [ ]* 5.5 Write unit tests for mapper conversions
    - Test entity to DTO conversion preserves all fields
    - Test DTO to entity conversion
    - Test nested conversions in MeetingMapper
    - _Requirements: 6.3_

- [ ] 6. Checkpoint - Ensure data layer is complete
  - Ensure all tests pass, ask the user if questions arise.

- [x] 7. Implement exception layer
  - [x] 7.1 Create custom exception classes
    - Create ResourceNotFoundException extending RuntimeException
    - Create DuplicateResourceException extending RuntimeException
    - Create InvalidRequestException extending RuntimeException
    - _Requirements: 7.1, 7.3_
  
  - [x] 7.2 Create ErrorResponse DTO
    - Fields: timestamp, status, error, message, path
    - Use Lombok @Data and @AllArgsConstructor
    - _Requirements: 7.1_
  
  - [x] 7.3 Create GlobalExceptionHandler with @RestControllerAdvice
    - Implement handlers for ResourceNotFoundException (404)
    - Implement handlers for DuplicateResourceException (409)
    - Implement handlers for InvalidRequestException (400)
    - Implement generic exception handler (500)
    - _Requirements: 7.1, 7.3_
  
  - [ ]* 7.4 Write property test for exception layer structure
    - **Property 7: Exception package contains exceptions or advice**
    - **Validates: Requirements 7.3**
  
  - [ ]* 7.5 Write unit tests for GlobalExceptionHandler
    - Test each exception type returns correct HTTP status
    - Test error response format
    - _Requirements: 7.3_

- [x] 8. Implement service layer with business logic
  - [x] 8.1 Create UserService
    - Implement createUser, getUserById, getUserByEmail, getAllUsers, updateUser, deleteUser
    - Add validation: check for duplicate email before creating
    - Throw ResourceNotFoundException when user not found
    - Annotate with @Service and inject UserRepository
    - _Requirements: 3.1, 3.3_
  
  - [x] 8.2 Create AgendaService
    - Implement createAgenda, getAgendaById, getAgendasByUser, getAgendasByUserAndDate, updateAgenda, deleteAgenda
    - Implement isUserAvailable method to check user availability
    - Add validation: verify user exists, validate time ranges
    - Throw ResourceNotFoundException when agenda or user not found
    - Annotate with @Service and inject AgendaRepository, UserRepository
    - _Requirements: 3.1, 3.3_
  
  - [x] 8.3 Create MeetingService
    - Implement createMeeting, getMeetingById, getMeetingsByOrganizer, getMeetingsByParticipant, updateMeeting, deleteMeeting
    - Implement addParticipant and removeParticipant methods
    - Add validation: verify organizer and participants exist, validate start/end times
    - Throw ResourceNotFoundException when meeting or user not found
    - Throw InvalidRequestException for invalid time ranges
    - Annotate with @Service and inject MeetingRepository, UserRepository, AgendaService
    - _Requirements: 3.1, 3.3_
  
  - [ ]* 8.4 Write property test for service annotations
    - **Property 3: Service classes have service annotations**
    - **Validates: Requirements 3.3**
  
  - [ ]* 8.5 Write unit tests for UserService
    - Test createUser with valid data
    - Test createUser with duplicate email throws exception
    - Test getUserById with non-existent id throws exception
    - Test updateUser and deleteUser
    - _Requirements: 3.3_
  
  - [ ]* 8.6 Write unit tests for MeetingService
    - Test createMeeting with valid data
    - Test createMeeting with invalid time range throws exception
    - Test addParticipant and removeParticipant
    - Test getMeetingsByOrganizer and getMeetingsByParticipant
    - _Requirements: 3.3_
  
  - [ ]* 8.7 Write unit tests for AgendaService
    - Test createAgenda with valid data
    - Test isUserAvailable method
    - Test getAgendasByUserAndDate
    - _Requirements: 3.3_

- [x] 9. Implement controller layer with REST endpoints
  - [x] 9.1 Create UserController
    - Implement POST /api/users (create user)
    - Implement GET /api/users/{id} (get user by id)
    - Implement GET /api/users (get all users)
    - Implement PUT /api/users/{id} (update user)
    - Implement DELETE /api/users/{id} (delete user)
    - Annotate with @RestController and @RequestMapping("/api/users")
    - Inject UserService and UserMapper
    - Convert between DTOs and entities using mapper
    - _Requirements: 4.1, 4.3_
  
  - [x] 9.2 Create MeetingController
    - Implement POST /api/meetings (create meeting)
    - Implement GET /api/meetings/{id} (get meeting by id)
    - Implement GET /api/meetings/organizer/{organizerId} (get meetings by organizer)
    - Implement PUT /api/meetings/{id} (update meeting)
    - Implement DELETE /api/meetings/{id} (delete meeting)
    - Implement POST /api/meetings/{meetingId}/participants/{userId} (add participant)
    - Implement DELETE /api/meetings/{meetingId}/participants/{userId} (remove participant)
    - Annotate with @RestController and @RequestMapping("/api/meetings")
    - Inject MeetingService and MeetingMapper
    - Convert between DTOs and entities using mapper
    - _Requirements: 4.1, 4.3_
  
  - [x] 9.3 Create AgendaController
    - Implement POST /api/agendas (create agenda)
    - Implement GET /api/agendas/{id} (get agenda by id)
    - Implement GET /api/agendas/user/{userId} (get agendas by user)
    - Implement PUT /api/agendas/{id} (update agenda)
    - Implement DELETE /api/agendas/{id} (delete agenda)
    - Annotate with @RestController and @RequestMapping("/api/agendas")
    - Inject AgendaService and AgendaMapper
    - Convert between DTOs and entities using mapper
    - _Requirements: 4.1, 4.3_
  
  - [ ]* 9.4 Write property test for controller annotations
    - **Property 4: Controller classes have controller annotations**
    - **Validates: Requirements 4.3**
  
  - [ ]* 9.5 Write integration tests for UserController
    - Test POST /api/users creates user and returns 201
    - Test GET /api/users/{id} returns user and 200
    - Test GET /api/users/{id} with non-existent id returns 404
    - Test PUT and DELETE endpoints
    - Use @WebMvcTest or @SpringBootTest
    - _Requirements: 4.3_
  
  - [ ]* 9.6 Write integration tests for MeetingController
    - Test POST /api/meetings creates meeting and returns 201
    - Test GET /api/meetings/{id} returns meeting with nested user DTOs
    - Test add/remove participant endpoints
    - _Requirements: 4.3_
  
  - [ ]* 9.7 Write integration tests for AgendaController
    - Test POST /api/agendas creates agenda and returns 201
    - Test GET /api/agendas/user/{userId} returns user's agendas
    - _Requirements: 4.3_

- [x] 10. Implement configuration layer
  - [x] 10.1 Create JpaConfig class
    - Annotate with @Configuration and @EnableJpaAuditing
    - Configure JPA settings if needed
    - _Requirements: 8.1, 8.3_
  
  - [x] 10.2 Create WebConfig class
    - Annotate with @Configuration
    - Implement WebMvcConfigurer
    - Configure CORS mappings for API endpoints
    - _Requirements: 8.1, 8.3_
  
  - [ ]* 10.3 Write property test for config annotations
    - **Property 8: Config classes have configuration annotations**
    - **Validates: Requirements 8.3**

- [ ] 11. Add Maven dependencies for testing
  - [ ] 11.1 Update pom.xml with testing dependencies
    - Add spring-boot-starter-test (if not already present)
    - Add jqwik for property-based testing (version 1.7.4)
    - Ensure H2 database dependency for testing
    - _Requirements: Testing Strategy_

- [ ] 12. Final checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

## Notes

- Tasks marked with `*` are optional and can be skipped for faster MVP
- Each task references specific requirements for traceability
- The implementation follows a bottom-up approach: entities → repositories → services → controllers
- DTOs and mappers are created before controllers to ensure proper API contracts
- Exception handling is implemented before services to enable proper error responses
- Property tests verify architectural compliance (annotations, structure)
- Unit tests verify specific business logic and edge cases
- Integration tests verify end-to-end API functionality
- Checkpoints ensure incremental validation at key milestones
