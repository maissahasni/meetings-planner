# Quick Start Guide

## Prerequisites Check

Before starting, ensure you have:
- ✅ Java 17+ installed (`java -version`)
- ✅ Maven installed (`mvn -version`)
- ✅ Node.js 18+ installed (`node -version`)
- ✅ npm installed (`npm -version`)
- ✅ MySQL running on localhost:3306

## Step 1: Start MySQL Database

Make sure MySQL is running and accessible on `localhost:3306`

## Step 2: Start Backend

```bash
# Navigate to backend directory
cd backend

# Run Spring Boot application
./mvnw spring-boot:run

# On Windows use:
mvnw.cmd spring-boot:run
```

The backend will:
- Start on http://localhost:8080
- Auto-create the `meeting_planner` database
- Create all necessary tables

**Verify backend is running:**
Open http://localhost:8080/api/users in your browser (should return an empty array `[]`)

## Step 3: Start Frontend

Open a NEW terminal window:

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies (first time only)
npm install

# Start Angular development server
npm start
```

The frontend will:
- Start on http://localhost:4200
- Auto-open in your browser
- Connect to backend at http://localhost:8080

## Step 4: Use the Application

1. **Create Users**
   - Click "New User" in the navigation
   - Fill in name, email, and role
   - Click "Create User"

2. **Create Meetings**
   - Click "New Meeting" in the navigation
   - Fill in meeting details
   - Select an organizer
   - Select participants (optional)
   - Click "Create Meeting"

3. **View Agendas**
   - Click "Agendas" in the navigation
   - Select a user from the dropdown
   - View their schedule

## Troubleshooting

### Backend Issues

**Port 8080 already in use:**
```bash
# Change port in backend/src/main/resources/application.properties
server.port=8081
```

**Database connection error:**
- Check MySQL is running
- Verify credentials in `application.properties`
- Ensure user has CREATE DATABASE permission

**Lombok errors:**
- Ensure your IDE has Lombok plugin installed
- Enable annotation processing in IDE settings

### Frontend Issues

**Port 4200 already in use:**
```bash
ng serve --port 4201
```

**npm install fails:**
```bash
# Clear npm cache
npm cache clean --force
npm install
```

**CORS errors:**
- Verify backend is running on port 8080
- Check WebConfig.java has correct CORS settings

## Testing the API with curl

### Create a user:
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com","role":"USER"}'
```

### Get all users:
```bash
curl http://localhost:8080/api/users
```

### Create a meeting:
```bash
curl -X POST http://localhost:8080/api/meetings \
  -H "Content-Type: application/json" \
  -d '{
    "title":"Team Standup",
    "description":"Daily standup meeting",
    "startTime":"2024-03-20T09:00:00",
    "endTime":"2024-03-20T09:30:00",
    "organizerId":1,
    "participantIds":[1]
  }'
```

## Next Steps

- Explore the code in `backend/src/main/java/com/example/backend/`
- Check out the Angular components in `frontend/src/app/components/`
- Review the API documentation in README.md
- Customize the styling in component CSS files

## Stopping the Application

### Stop Backend:
Press `Ctrl+C` in the backend terminal

### Stop Frontend:
Press `Ctrl+C` in the frontend terminal

## Clean Restart

If you need to start fresh:

```bash
# Backend - clean and rebuild
cd backend
./mvnw clean install

# Frontend - reinstall dependencies
cd frontend
rm -rf node_modules package-lock.json
npm install
```
