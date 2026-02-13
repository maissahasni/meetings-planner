# Meeting Planner Application

A full-stack Meeting Planner application with Spring Boot backend and Angular frontend.

## Project Structure

```
.
├── backend/          # Spring Boot REST API
└── frontend/         # Angular TypeScript application
```

## Features

### User Management
- Create, read, update, and delete users
- User roles (ADMIN, USER)
- Email uniqueness validation

### Meeting Management
- Create and manage meetings
- Assign organizers and participants
- Schedule meetings with start/end times
- Add/remove participants dynamically

### Agenda Management
- Track user availability (FREE/BUSY)
- View agendas by user
- Date and time-based scheduling

## Technology Stack

### Backend
- Java 17
- Spring Boot 3.5.10
- Spring Data JPA
- MySQL Database
- Lombok
- Maven

### Frontend
- Angular 17
- TypeScript 5.2
- RxJS
- Standalone Components
- HttpClient

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Node.js 18+
- npm 9+
- MySQL 8.0+
- Angular CLI 17+

### Backend Setup

1. Navigate to backend directory:
```bash
cd backend
```

2. Configure database in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/meeting_planner?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=your_password
```

3. Run the application:
```bash
./mvnw spring-boot:run
```

The backend will start on `http://localhost:8080`

### Frontend Setup

1. Navigate to frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Start the development server:
```bash
npm start
```

The frontend will be available at `http://localhost:4200`

## API Endpoints

### Users
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `POST /api/users` - Create new user
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

### Meetings
- `GET /api/meetings/{id}` - Get meeting by ID
- `GET /api/meetings/organizer/{organizerId}` - Get meetings by organizer
- `POST /api/meetings` - Create new meeting
- `PUT /api/meetings/{id}` - Update meeting
- `DELETE /api/meetings/{id}` - Delete meeting
- `POST /api/meetings/{meetingId}/participants/{userId}` - Add participant
- `DELETE /api/meetings/{meetingId}/participants/{userId}` - Remove participant

### Agendas
- `GET /api/agendas/{id}` - Get agenda by ID
- `GET /api/agendas/user/{userId}` - Get user's agendas
- `POST /api/agendas` - Create new agenda
- `PUT /api/agendas/{id}` - Update agenda
- `DELETE /api/agendas/{id}` - Delete agenda

## Architecture

### Backend Architecture (Clean Layered Architecture)

```
com.example.backend/
├── entity/          # JPA entities
├── repository/      # Spring Data repositories
├── service/         # Business logic
├── controller/      # REST controllers
├── dto/            # Data Transfer Objects
├── mapper/         # Entity-DTO mappers
├── exception/      # Custom exceptions & handlers
└── config/         # Configuration classes
```

### Frontend Architecture

```
src/app/
├── components/     # UI components
├── models/        # TypeScript interfaces
├── services/      # HTTP services
└── environments/  # Environment configs
```

## Database Schema

### Users Table
- id (PK)
- name
- email (unique)
- role (ADMIN/USER)

### Meetings Table
- id (PK)
- title
- description
- start_time
- end_time
- organizer_id (FK → users)

### Meeting_Participants Table (Join Table)
- meeting_id (FK → meetings)
- user_id (FK → users)

### Agendas Table
- id (PK)
- user_id (FK → users)
- date
- start_time
- end_time
- status (FREE/BUSY)

## Development

### Backend Development
- The backend uses Lombok to reduce boilerplate code
- JPA entities follow best practices with explicit fetch types
- Global exception handling provides consistent error responses
- CORS is configured for frontend communication

### Frontend Development
- Uses Angular standalone components (no NgModule)
- Reactive programming with RxJS
- Type-safe models matching backend DTOs
- Responsive design with CSS Grid

## Testing

### Backend
```bash
cd backend
./mvnw test
```

### Frontend
```bash
cd frontend
npm test
```

## Building for Production

### Backend
```bash
cd backend
./mvnw clean package
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

### Frontend
```bash
cd frontend
npm run build
```

## License

This project is for educational purposes.
