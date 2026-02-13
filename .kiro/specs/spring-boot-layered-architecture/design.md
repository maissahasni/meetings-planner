# Design Document: Spring Boot Layered Architecture

## Overview

This design specifies the implementation of a clean layered architecture for a Spring Boot Maven project, including a complete Meeting Planner application with JPA entities, repositories, services, controllers, DTOs, and mappers. The architecture follows separation of concerns principles, organizing code into eight distinct packages, each with specific responsibilities.

The implementation will create a fully functional Meeting Planner API that allows users to create meetings, manage participants, and track availability through agendas.

## Architecture

### Layered Architecture Pattern

The application follows a traditional layered architecture with clear separation between:

1. **Presentation Layer** (Controller): Handles HTTP requests/responses
2. **Business Logic Layer** (Service): Implements core business rules
3. **Data Access Layer** (Repository): Manages database operations
4. **Domain Layer** (Entity): Represents database models
5. **Transfer Layer** (DTO): Defines API contracts
6. **Mapping Layer** (Mapper): Converts between entities and DTOs
7. **Exception Layer**: Centralizes error handling
8. **Configuration Layer**: Manages application configuration

### Dependency Flow

```
Controller → Service → Repository → Entity
     ↓          ↓
    DTO ← Mapper → Entity
```

Dependencies flow inward: Controllers depend on Services, Services depend on Repositories. DTOs are used at the API boundary, and Mappers convert between DTOs and Entities.

## Components and Interfaces

### 1. Entity Layer (com.example.backend.entity)

**Purpose**: Define JPA entities representing database tables.

**Components**:

#### User Entity
```java
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    
    @Column(nullable = false)
    String name;
    
    @Column(unique = true, nullable = false)
    String email;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Role role;
    
    @OneToMany(mappedBy = "organizer")
    @JsonIgnore
    List<Meeting> organizedMeetings;
    
    @ManyToMany(mappedBy = "participants")
    @JsonIgnore
    List<Meeting> participatingMeetings;
    
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    List<Agenda> agendas;
}
```

#### Role Enum
```java
enum Role {
    ADMIN,
    USER
}
```

#### Meeting Entity
```java
@Entity
@Table(name = "meetings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    
    @Column(nullable = false)
    String title;
    
    @Column(length = 1000)
    String description;
    
    @Column(nullable = false)
    LocalDateTime startTime;
    
    @Column(nullable = false)
    LocalDateTime endTime;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    User organizer;
    
    @ManyToMany
    @JoinTable(
        name = "meeting_participants",
        joinColumns = @JoinColumn(name = "meeting_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    List<User> participants;
}
```

#### Agenda Entity
```java
@Entity
@Table(name = "agendas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
class Agenda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;
    
    @Column(nullable = false)
    LocalDate date;
    
    @Column(nullable = false)
    LocalTime startTime;
    
    @Column(nullable = false)
    LocalTime endTime;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    AgendaStatus status;
}
```

#### AgendaStatus Enum
```java
enum AgendaStatus {
    FREE,
    BUSY
}
```

### 2. Repository Layer (com.example.backend.repository)

**Purpose**: Provide data access interfaces using Spring Data JPA.

**Components**:

#### UserRepository
```java
@Repository
interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
```

#### MeetingRepository
```java
@Repository
interface MeetingRepository extends JpaRepository<Meeting, Long> {
    List<Meeting> findByOrganizerId(Long organizerId);
    List<Meeting> findByParticipantsId(Long participantId);
    List<Meeting> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
}
```

#### AgendaRepository
```java
@Repository
interface AgendaRepository extends JpaRepository<Agenda, Long> {
    List<Agenda> findByUserId(Long userId);
    List<Agenda> findByUserIdAndDate(Long userId, LocalDate date);
    List<Agenda> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
}
```

### 3. Service Layer (com.example.backend.service)

**Purpose**: Implement business logic and orchestrate operations.

**Components**:

#### UserService
```java
@Service
class UserService {
    UserRepository userRepository;
    
    User createUser(User user);
    User getUserById(Long id);
    User getUserByEmail(String email);
    List<User> getAllUsers();
    User updateUser(Long id, User user);
    void deleteUser(Long id);
}
```

#### MeetingService
```java
@Service
class MeetingService {
    MeetingRepository meetingRepository;
    UserRepository userRepository;
    AgendaService agendaService;
    
    Meeting createMeeting(Meeting meeting);
    Meeting getMeetingById(Long id);
    List<Meeting> getMeetingsByOrganizer(Long organizerId);
    List<Meeting> getMeetingsByParticipant(Long participantId);
    Meeting updateMeeting(Long id, Meeting meeting);
    void deleteMeeting(Long id);
    Meeting addParticipant(Long meetingId, Long userId);
    Meeting removeParticipant(Long meetingId, Long userId);
}
```

#### AgendaService
```java
@Service
class AgendaService {
    AgendaRepository agendaRepository;
    UserRepository userRepository;
    
    Agenda createAgenda(Agenda agenda);
    Agenda getAgendaById(Long id);
    List<Agenda> getAgendasByUser(Long userId);
    List<Agenda> getAgendasByUserAndDate(Long userId, LocalDate date);
    Agenda updateAgenda(Long id, Agenda agenda);
    void deleteAgenda(Long id);
    boolean isUserAvailable(Long userId, LocalDateTime startTime, LocalDateTime endTime);
}
```

### 4. Controller Layer (com.example.backend.controller)

**Purpose**: Expose REST API endpoints.

**Components**:

#### UserController
```java
@RestController
@RequestMapping("/api/users")
class UserController {
    UserService userService;
    UserMapper userMapper;
    
    @PostMapping
    ResponseEntity<UserResponseDTO> createUser(@RequestBody UserRequestDTO request);
    
    @GetMapping("/{id}")
    ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id);
    
    @GetMapping
    ResponseEntity<List<UserResponseDTO>> getAllUsers();
    
    @PutMapping("/{id}")
    ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody UserRequestDTO request);
    
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteUser(@PathVariable Long id);
}
```

#### MeetingController
```java
@RestController
@RequestMapping("/api/meetings")
class MeetingController {
    MeetingService meetingService;
    MeetingMapper meetingMapper;
    
    @PostMapping
    ResponseEntity<MeetingResponseDTO> createMeeting(@RequestBody MeetingRequestDTO request);
    
    @GetMapping("/{id}")
    ResponseEntity<MeetingResponseDTO> getMeetingById(@PathVariable Long id);
    
    @GetMapping("/organizer/{organizerId}")
    ResponseEntity<List<MeetingResponseDTO>> getMeetingsByOrganizer(@PathVariable Long organizerId);
    
    @PutMapping("/{id}")
    ResponseEntity<MeetingResponseDTO> updateMeeting(@PathVariable Long id, @RequestBody MeetingRequestDTO request);
    
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteMeeting(@PathVariable Long id);
    
    @PostMapping("/{meetingId}/participants/{userId}")
    ResponseEntity<MeetingResponseDTO> addParticipant(@PathVariable Long meetingId, @PathVariable Long userId);
    
    @DeleteMapping("/{meetingId}/participants/{userId}")
    ResponseEntity<MeetingResponseDTO> removeParticipant(@PathVariable Long meetingId, @PathVariable Long userId);
}
```

#### AgendaController
```java
@RestController
@RequestMapping("/api/agendas")
class AgendaController {
    AgendaService agendaService;
    AgendaMapper agendaMapper;
    
    @PostMapping
    ResponseEntity<AgendaResponseDTO> createAgenda(@RequestBody AgendaRequestDTO request);
    
    @GetMapping("/{id}")
    ResponseEntity<AgendaResponseDTO> getAgendaById(@PathVariable Long id);
    
    @GetMapping("/user/{userId}")
    ResponseEntity<List<AgendaResponseDTO>> getAgendasByUser(@PathVariable Long userId);
    
    @PutMapping("/{id}")
    ResponseEntity<AgendaResponseDTO> updateAgenda(@PathVariable Long id, @RequestBody AgendaRequestDTO request);
    
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteAgenda(@PathVariable Long id);
}
```

### 5. DTO Layer (com.example.backend.dto)

**Purpose**: Define data transfer objects for API requests and responses.

**Components**:

#### User DTOs
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
class UserRequestDTO {
    String name;
    String email;
    Role role;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class UserResponseDTO {
    Long id;
    String name;
    String email;
    Role role;
}
```

#### Meeting DTOs
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
class MeetingRequestDTO {
    String title;
    String description;
    LocalDateTime startTime;
    LocalDateTime endTime;
    Long organizerId;
    List<Long> participantIds;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class MeetingResponseDTO {
    Long id;
    String title;
    String description;
    LocalDateTime startTime;
    LocalDateTime endTime;
    UserResponseDTO organizer;
    List<UserResponseDTO> participants;
}
```

#### Agenda DTOs
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
class AgendaRequestDTO {
    Long userId;
    LocalDate date;
    LocalTime startTime;
    LocalTime endTime;
    AgendaStatus status;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class AgendaResponseDTO {
    Long id;
    Long userId;
    String userName;
    LocalDate date;
    LocalTime startTime;
    LocalTime endTime;
    AgendaStatus status;
}
```

### 6. Mapper Layer (com.example.backend.mapper)

**Purpose**: Convert between entities and DTOs.

**Components**:

#### UserMapper
```java
@Component
class UserMapper {
    User toEntity(UserRequestDTO dto);
    UserResponseDTO toResponseDTO(User entity);
    List<UserResponseDTO> toResponseDTOList(List<User> entities);
    void updateEntityFromDTO(UserRequestDTO dto, User entity);
}
```

#### MeetingMapper
```java
@Component
class MeetingMapper {
    UserMapper userMapper;
    
    Meeting toEntity(MeetingRequestDTO dto);
    MeetingResponseDTO toResponseDTO(Meeting entity);
    List<MeetingResponseDTO> toResponseDTOList(List<Meeting> entities);
    void updateEntityFromDTO(MeetingRequestDTO dto, Meeting entity);
}
```

#### AgendaMapper
```java
@Component
class AgendaMapper {
    Agenda toEntity(AgendaRequestDTO dto);
    AgendaResponseDTO toResponseDTO(Agenda entity);
    List<AgendaResponseDTO> toResponseDTOList(List<Agenda> entities);
    void updateEntityFromDTO(AgendaRequestDTO dto, Agenda entity);
}
```

### 7. Exception Layer (com.example.backend.exception)

**Purpose**: Define custom exceptions and global exception handling.

**Components**:

#### Custom Exceptions
```java
class ResourceNotFoundException extends RuntimeException {
    ResourceNotFoundException(String message);
}

class DuplicateResourceException extends RuntimeException {
    DuplicateResourceException(String message);
}

class InvalidRequestException extends RuntimeException {
    InvalidRequestException(String message);
}
```

#### Global Exception Handler
```java
@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex);
    
    @ExceptionHandler(DuplicateResourceException.class)
    ResponseEntity<ErrorResponse> handleDuplicateResource(DuplicateResourceException ex);
    
    @ExceptionHandler(InvalidRequestException.class)
    ResponseEntity<ErrorResponse> handleInvalidRequest(InvalidRequestException ex);
    
    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> handleGenericException(Exception ex);
}
```

#### Error Response DTO
```java
@Data
@AllArgsConstructor
class ErrorResponse {
    LocalDateTime timestamp;
    int status;
    String error;
    String message;
    String path;
}
```

### 8. Config Layer (com.example.backend.config)

**Purpose**: Configure application components.

**Components**:

#### JPA Configuration
```java
@Configuration
@EnableJpaAuditing
class JpaConfig {
    // JPA configuration if needed
}
```

#### Web Configuration
```java
@Configuration
class WebConfig implements WebMvcConfigurer {
    @Override
    void addCorsMappings(CorsRegistry registry);
}
```

## Data Models

### Entity Relationships

```
User (1) ----< (N) Meeting [as organizer]
User (N) ----< (N) Meeting [as participant]
User (1) ----< (N) Agenda

Meeting (N) >---- (1) User [organizer]
Meeting (N) >----< (N) User [participants]

Agenda (N) >---- (1) User
```

### Database Schema

#### users table
- id: BIGINT PRIMARY KEY AUTO_INCREMENT
- name: VARCHAR(255) NOT NULL
- email: VARCHAR(255) UNIQUE NOT NULL
- role: VARCHAR(50) NOT NULL

#### meetings table
- id: BIGINT PRIMARY KEY AUTO_INCREMENT
- title: VARCHAR(255) NOT NULL
- description: VARCHAR(1000)
- start_time: TIMESTAMP NOT NULL
- end_time: TIMESTAMP NOT NULL
- organizer_id: BIGINT NOT NULL FOREIGN KEY → users(id)

#### meeting_participants table (join table)
- meeting_id: BIGINT FOREIGN KEY → meetings(id)
- user_id: BIGINT FOREIGN KEY → users(id)
- PRIMARY KEY (meeting_id, user_id)

#### agendas table
- id: BIGINT PRIMARY KEY AUTO_INCREMENT
- user_id: BIGINT NOT NULL FOREIGN KEY → users(id)
- date: DATE NOT NULL
- start_time: TIME NOT NULL
- end_time: TIME NOT NULL
- status: VARCHAR(50) NOT NULL

### JSON Serialization Strategy

To prevent infinite recursion in bidirectional relationships:

1. **Entity Level**: Use `@JsonIgnore` on the inverse side of relationships
   - User.organizedMeetings → @JsonIgnore
   - User.participatingMeetings → @JsonIgnore
   - User.agendas → @JsonIgnore

2. **DTO Level**: Use DTOs for all API responses
   - Never serialize entities directly
   - DTOs contain only necessary fields
   - Nested objects are also DTOs (e.g., MeetingResponseDTO contains UserResponseDTO)

3. **Mapper Level**: Mappers handle conversion and prevent circular references
   - Convert entities to DTOs before returning from controllers
   - Only include necessary relationship data in DTOs

## Correctness Properties

*A property is a characteristic or behavior that should hold true across all valid executions of a system—essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.*

### Property Reflection

After analyzing all acceptance criteria, I've identified the following testable properties. Many criteria are organizational principles or design guidelines that aren't programmatically testable. The testable properties fall into two categories:

1. **Structural Properties**: Verify that classes in specific packages have appropriate annotations
2. **Entity Properties**: Verify that JPA entities follow best practices

Several redundant properties have been consolidated:
- Individual entity field verification (11.1-13.8) are examples that verify specific implementation details, not universal properties
- Package existence checks (1.1, 2.1, 3.1, etc.) are all examples of the same pattern
- Annotation checks for different layers (1.3, 2.3, 3.3, etc.) can be generalized into properties about layer compliance

### Properties

**Property 1: Entity classes have JPA annotations**
*For any* class in the entity package (excluding enums), the class should be annotated with @Entity
**Validates: Requirements 1.3**

**Property 2: Repository interfaces extend Spring Data repositories**
*For any* interface in the repository package, the interface should extend a Spring Data repository type (JpaRepository, CrudRepository, etc.)
**Validates: Requirements 2.3**

**Property 3: Service classes have service annotations**
*For any* class in the service package, the class should be annotated with @Service
**Validates: Requirements 3.3**

**Property 4: Controller classes have controller annotations**
*For any* class in the controller package, the class should be annotated with @RestController or @Controller
**Validates: Requirements 4.3**

**Property 5: DTOs do not contain JPA annotations**
*For any* class in the dto package, the class should not have JPA entity annotations (@Entity, @Table, @ManyToOne, @OneToMany, @ManyToMany)
**Validates: Requirements 5.4**

**Property 6: Mapper classes have conversion methods**
*For any* mapper class in the mapper package, the class should have methods that convert between entities and DTOs (methods accepting entities and returning DTOs, and vice versa)
**Validates: Requirements 6.3**

**Property 7: Exception package contains exceptions or advice**
*For any* class in the exception package, the class should either extend Exception/RuntimeException or be annotated with @ControllerAdvice/@RestControllerAdvice
**Validates: Requirements 7.3**

**Property 8: Config classes have configuration annotations**
*For any* class in the config package, the class should be annotated with @Configuration
**Validates: Requirements 8.3**

**Property 9: Entities use explicit fetch types**
*For any* entity class with relationship annotations (@ManyToOne, @OneToMany, @ManyToMany, @OneToOne), the annotation should specify an explicit fetch type
**Validates: Requirements 14.1**

**Property 10: Bidirectional relationships prevent JSON recursion**
*For any* entity class with bidirectional relationships, the inverse side should have @JsonIgnore or similar JSON serialization control annotations
**Validates: Requirements 14.2**

**Property 11: Entities avoid Lombok @Data annotation**
*For any* entity class, the class should not use @Data annotation and should instead use @Getter, @Setter, and @NoArgsConstructor
**Validates: Requirements 14.5**

## Error Handling

### Exception Hierarchy

1. **ResourceNotFoundException**: Thrown when a requested resource (User, Meeting, Agenda) is not found
   - HTTP Status: 404 NOT FOUND
   - Use case: GET, PUT, DELETE operations on non-existent resources

2. **DuplicateResourceException**: Thrown when attempting to create a resource that already exists
   - HTTP Status: 409 CONFLICT
   - Use case: Creating a user with an email that already exists

3. **InvalidRequestException**: Thrown when request data is invalid
   - HTTP Status: 400 BAD REQUEST
   - Use case: Invalid date ranges, missing required fields, business rule violations

### Global Exception Handler

The `GlobalExceptionHandler` class intercepts all exceptions and converts them to standardized error responses:

```java
{
    "timestamp": "2024-01-15T10:30:00",
    "status": 404,
    "error": "Not Found",
    "message": "User with id 123 not found",
    "path": "/api/users/123"
}
```

### Service Layer Error Handling

Services should throw appropriate exceptions:

```java
// Example in UserService
User getUserById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
}

// Example in MeetingService
Meeting createMeeting(Meeting meeting) {
    if (meeting.getStartTime().isAfter(meeting.getEndTime())) {
        throw new InvalidRequestException("Start time must be before end time");
    }
    // ... rest of logic
}
```

## Testing Strategy

### Dual Testing Approach

This project requires both unit tests and property-based tests for comprehensive coverage:

1. **Unit Tests**: Verify specific examples, edge cases, and integration between components
2. **Property Tests**: Verify universal properties across all inputs using randomized testing

### Unit Testing

**Focus Areas**:
- Specific examples demonstrating correct behavior
- Edge cases (empty lists, null values, boundary conditions)
- Error conditions and exception handling
- Integration between layers (controller → service → repository)

**Example Unit Tests**:
- Creating a user with valid data returns the created user
- Creating a user with duplicate email throws DuplicateResourceException
- Getting a non-existent user throws ResourceNotFoundException
- Meeting with end time before start time throws InvalidRequestException

**Testing Framework**: JUnit 5 with Mockito for mocking dependencies

### Property-Based Testing

**Focus Areas**:
- Universal properties that hold for all inputs
- Structural validation of the layered architecture
- JPA entity best practices compliance

**Configuration**:
- Minimum 100 iterations per property test
- Use jqwik (Java property-based testing library)
- Each test must reference its design document property

**Tag Format**: 
```java
// Feature: spring-boot-layered-architecture, Property 1: Entity classes have JPA annotations
```

**Example Property Tests**:
- Property 1: All entity classes have @Entity annotation
- Property 2: All repository interfaces extend Spring Data repositories
- Property 3: All service classes have @Service annotation
- Property 9: All entity relationships specify explicit fetch types

### Test Organization

```
src/test/java/com/example/backend/
├── entity/
│   └── EntityPropertiesTest.java (Properties 1, 9, 10, 11)
├── repository/
│   ├── UserRepositoryTest.java (unit tests)
│   ├── MeetingRepositoryTest.java (unit tests)
│   └── RepositoryPropertiesTest.java (Property 2)
├── service/
│   ├── UserServiceTest.java (unit tests)
│   ├── MeetingServiceTest.java (unit tests)
│   ├── AgendaServiceTest.java (unit tests)
│   └── ServicePropertiesTest.java (Property 3)
├── controller/
│   ├── UserControllerTest.java (unit tests)
│   ├── MeetingControllerTest.java (unit tests)
│   ├── AgendaControllerTest.java (unit tests)
│   └── ControllerPropertiesTest.java (Property 4)
├── dto/
│   └── DtoPropertiesTest.java (Property 5)
├── mapper/
│   ├── UserMapperTest.java (unit tests)
│   ├── MeetingMapperTest.java (unit tests)
│   ├── AgendaMapperTest.java (unit tests)
│   └── MapperPropertiesTest.java (Property 6)
├── exception/
│   ├── GlobalExceptionHandlerTest.java (unit tests)
│   └── ExceptionPropertiesTest.java (Property 7)
└── config/
    └── ConfigPropertiesTest.java (Property 8)
```

### Maven Dependencies for Testing

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>net.jqwik</groupId>
    <artifactId>jqwik</artifactId>
    <version>1.7.4</version>
    <scope>test</scope>
</dependency>
```

### Testing Balance

- Unit tests should focus on specific scenarios and integration points
- Property tests should verify architectural compliance and universal rules
- Avoid writing too many unit tests for scenarios that property tests already cover
- Each correctness property must be implemented by a single property-based test
