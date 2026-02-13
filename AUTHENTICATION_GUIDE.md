# Authentication Guide

## Overview

The Meeting Planner application now includes full authentication functionality with login and signup features.

## Features Added

### Backend (Spring Boot)

1. **User Entity Updated**
   - Added `password` field (with @JsonIgnore to prevent exposure in API responses)

2. **New DTOs**
   - `LoginRequestDTO` - Email and password for login
   - `SignupRequestDTO` - Name, email, password, and role for registration
   - `AuthResponseDTO` - User info returned after successful auth

3. **AuthService**
   - `signup()` - Register new users with duplicate email validation
   - `login()` - Authenticate users with email/password

4. **AuthController**
   - `POST /api/auth/signup` - Create new account
   - `POST /api/auth/login` - Login to existing account

5. **MeetingService Fixed**
   - Added `getAllMeetings()` method

6. **MeetingController Fixed**
   - Added `GET /api/meetings` endpoint to list all meetings

### Frontend (Angular)

1. **New Models**
   - `auth.model.ts` - LoginRequest, SignupRequest, AuthResponse interfaces

2. **AuthService**
   - Manages authentication state
   - Stores user info in localStorage
   - Provides currentUser$ observable
   - Methods: login(), signup(), logout(), isLoggedIn()

3. **Login Component**
   - Beautiful gradient design
   - Email and password fields
   - Link to signup page
   - Error handling

4. **Signup Component**
   - Name, email, password, role fields
   - Password minimum length validation
   - Link to login page
   - Error handling

5. **App Component Updated**
   - Shows navbar only when logged in
   - Displays current user name
   - Logout button
   - Redirects to login on logout

6. **Routes Updated**
   - Default route now redirects to `/login`
   - Added `/login` and `/signup` routes

## How to Use

### 1. Start the Application

**Backend:**
```bash
cd backend
./mvnw spring-boot:run
```

**Frontend:**
```bash
cd frontend
npm install
npm start
```

### 2. Create an Account

1. Open http://localhost:4200
2. You'll see the Login page
3. Click "Sign up" link
4. Fill in:
   - Name
   - Email
   - Password (minimum 6 characters)
   - Role (USER or ADMIN)
5. Click "Sign Up"
6. You'll be automatically logged in and redirected to the Users page

### 3. Login

1. Go to http://localhost:4200/login
2. Enter your email and password
3. Click "Login"
4. You'll be redirected to the Users page

### 4. Use the Application

Once logged in, you can:
- View and manage users
- Create and view meetings
- View agendas
- Your name appears in the top-right corner
- Click "Logout" to sign out

## API Endpoints

### Authentication

**Signup:**
```bash
POST http://localhost:8080/api/auth/signup
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "role": "USER"
}
```

**Login:**
```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "role": "USER",
  "message": "Login successful"
}
```

### Meetings (Fixed)

**Get All Meetings:**
```bash
GET http://localhost:8080/api/meetings
```

## Security Notes

⚠️ **Important:** This is a basic authentication implementation for development/learning purposes.

**For Production, you should:**
1. Hash passwords using BCrypt or similar
2. Implement JWT tokens for stateless authentication
3. Add HTTPS/TLS encryption
4. Implement refresh tokens
5. Add rate limiting
6. Add CSRF protection
7. Implement proper session management
8. Add password strength requirements
9. Add email verification
10. Implement password reset functionality

## Database Schema Update

The `users` table now includes a `password` column:

```sql
ALTER TABLE users ADD COLUMN password VARCHAR(255) NOT NULL;
```

Spring Boot will automatically add this column when you restart the application.

## Troubleshooting

### "Invalid email or password" error
- Check that you're using the correct email and password
- Passwords are case-sensitive
- Make sure you've created an account first

### Can't create meetings
- Make sure you're logged in
- Ensure at least one user exists in the database
- Check that the backend is running on port 8080

### Navbar not showing
- Make sure you're logged in
- Check browser console for errors
- Clear localStorage and login again

## Testing with curl

**Signup:**
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name":"Test User",
    "email":"test@example.com",
    "password":"test123",
    "role":"USER"
  }'
```

**Login:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email":"test@example.com",
    "password":"test123"
  }'
```

**Get All Meetings:**
```bash
curl http://localhost:8080/api/meetings
```

## What's Fixed

1. ✅ Login functionality added
2. ✅ Signup functionality added
3. ✅ User authentication with password
4. ✅ Meeting creation now works (getAllMeetings endpoint added)
5. ✅ Beautiful login/signup UI with gradient design
6. ✅ User session management with localStorage
7. ✅ Protected routes (navbar only shows when logged in)
8. ✅ Logout functionality
9. ✅ Current user display in navbar

The application is now fully functional with authentication! 🎉
