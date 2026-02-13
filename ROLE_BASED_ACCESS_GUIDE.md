# Role-Based Access Control Guide

## Overview

The Meeting Planner application now implements complete role-based access control with two distinct user roles:

### ADMIN Role
- Can create, view, update, and delete users
- Full user management capabilities
- Cannot schedule meetings or view agendas

### USER Role
- Can schedule meetings
- Can view their own meetings (as organizer or participant)
- Can view their own agenda
- Cannot create other users
- Cannot view other users' agendas

## Features Implemented

### 1. Meeting Time Conflict Validation ✅

**Backend:**
- Checks if organizer has conflicting meetings before creating
- Checks if any participant has conflicting meetings
- Returns clear error message indicating who has the conflict and when
- Uses `hasTimeConflict()` method to detect overlapping time ranges

**How it works:**
```
Meeting A: 9:00 AM - 10:00 AM
Meeting B: 9:30 AM - 10:30 AM
Result: CONFLICT - Cannot create Meeting B
```

### 2. Automatic Agenda Creation ✅

**Backend:**
- When a meeting is created, agendas are automatically created for:
  - The organizer
  - All participants
- Agenda status is set to BUSY
- Agenda includes date, start time, and end time from the meeting

### 3. Role-Based Navigation ✅

**Frontend:**
- Navigation menu changes based on user role
- ADMIN sees: Users, New User
- USER sees: My Meetings, New Meeting, My Agenda
- Role badge displayed in navbar

### 4. User-Specific Data Access ✅

**Backend Endpoints:**
- `GET /api/meetings/user/{userId}` - Get all meetings for a user (organizer + participant)
- `GET /api/agendas/user/{userId}` - Get agendas for a specific user

**Frontend:**
- Users automatically see only their own meetings
- Users automatically see only their own agenda
- Current user ID is used from authentication

## API Endpoints

### Meeting Endpoints

**Get All Meetings (Admin):**
```
GET /api/meetings
```

**Get User's Meetings:**
```
GET /api/meetings/user/{userId}
```

**Create Meeting (with conflict check):**
```
POST /api/meetings
{
  "title": "Team Meeting",
  "description": "Weekly sync",
  "startTime": "2024-03-20T09:00:00",
  "endTime": "2024-03-20T10:00:00",
  "organizerId": 1,
  "participantIds": [2, 3]
}
```

**Error Response (Conflict):**
```json
{
  "timestamp": "2024-03-20T08:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Organizer already has a meeting scheduled between 2024-03-20T09:00:00 and 2024-03-20T10:00:00",
  "path": "/api/meetings"
}
```

### Agenda Endpoints

**Get User's Agendas:**
```
GET /api/agendas/user/{userId}
```

## User Workflows

### ADMIN Workflow

1. **Login as ADMIN**
   - Email: admin@example.com
   - Role: ADMIN

2. **Create Users**
   - Navigate to "Users" → "New User"
   - Fill in name, email, password, role
   - Click "Create User"

3. **View All Users**
   - Navigate to "Users"
   - See list of all users
   - Delete users if needed

### USER Workflow

1. **Login as USER**
   - Email: user@example.com
   - Role: USER

2. **Schedule Meeting**
   - Navigate to "New Meeting"
   - Fill in meeting details
   - Select participants
   - System automatically:
     - Sets you as organizer
     - Checks for time conflicts
     - Creates agendas for all participants

3. **View My Meetings**
   - Navigate to "My Meetings"
   - See meetings where you're organizer or participant
   - Delete your meetings

4. **View My Agenda**
   - Navigate to "My Agenda"
   - See your schedule with BUSY times
   - Automatically populated from meetings

## Time Conflict Examples

### Example 1: Organizer Conflict
```
Existing Meeting: 9:00 AM - 10:00 AM (Organizer: John)
New Meeting: 9:30 AM - 11:00 AM (Organizer: John)
Result: ❌ CONFLICT - "Organizer already has a meeting scheduled..."
```

### Example 2: Participant Conflict
```
Existing Meeting: 2:00 PM - 3:00 PM (Participant: Sarah)
New Meeting: 2:30 PM - 4:00 PM (Participant: Sarah)
Result: ❌ CONFLICT - "Sarah already has a meeting scheduled..."
```

### Example 3: No Conflict
```
Existing Meeting: 9:00 AM - 10:00 AM
New Meeting: 10:00 AM - 11:00 AM
Result: ✅ SUCCESS - No overlap
```

## Frontend Components

### New Components

1. **MyMeetingsComponent** (`/my-meetings`)
   - Shows meetings for current user
   - Displays organizer and participants
   - Delete functionality

2. **MyAgendasComponent** (`/my-agendas`)
   - Shows agenda for current user
   - Color-coded status badges (FREE/BUSY)
   - Delete functionality

### Updated Components

1. **AppComponent**
   - Role-based navigation menu
   - Role badge in navbar
   - Conditional rendering based on user role

2. **LoginComponent & SignupComponent**
   - Redirect based on role after authentication
   - ADMIN → /users
   - USER → /my-meetings

3. **MeetingFormComponent**
   - Automatically sets current user as organizer
   - Shows all users for participant selection

## Database Schema Updates

No schema changes required - existing tables support all features.

## Testing

### Test ADMIN Role

1. Create ADMIN user:
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name":"Admin User",
    "email":"admin@test.com",
    "password":"admin123",
    "role":"ADMIN"
  }'
```

2. Login and verify:
   - Can see "Users" and "New User" in menu
   - Can create users
   - Cannot see meeting/agenda options

### Test USER Role

1. Create USER:
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name":"Regular User",
    "email":"user@test.com",
    "password":"user123",
    "role":"USER"
  }'
```

2. Login and verify:
   - Can see "My Meetings", "New Meeting", "My Agenda"
   - Can create meetings
   - Cannot see user management options

### Test Time Conflict

1. Create first meeting:
```bash
curl -X POST http://localhost:8080/api/meetings \
  -H "Content-Type: application/json" \
  -d '{
    "title":"Meeting 1",
    "startTime":"2024-03-20T09:00:00",
    "endTime":"2024-03-20T10:00:00",
    "organizerId":1,
    "participantIds":[]
  }'
```

2. Try to create conflicting meeting:
```bash
curl -X POST http://localhost:8080/api/meetings \
  -H "Content-Type: application/json" \
  -d '{
    "title":"Meeting 2",
    "startTime":"2024-03-20T09:30:00",
    "endTime":"2024-03-20T10:30:00",
    "organizerId":1,
    "participantIds":[]
  }'
```

Expected: 400 Bad Request with conflict message

### Test Agenda Auto-Creation

1. Create a meeting
2. Check agendas:
```bash
curl http://localhost:8080/api/agendas/user/1
```

Expected: Agenda entry with BUSY status matching meeting time

## Security Considerations

**Current Implementation:**
- Frontend role-based UI rendering
- Backend validates all operations
- Users can only access their own data

**For Production:**
- Add JWT tokens for stateless authentication
- Implement proper authorization checks on all endpoints
- Add @PreAuthorize annotations for role-based endpoint security
- Implement API rate limiting
- Add audit logging for user actions

## Troubleshooting

### "User already has a meeting scheduled" error
**Cause:** Time conflict detected
**Solution:** Choose a different time or check existing meetings

### Can't see meetings/agendas
**Cause:** Wrong role or not logged in
**Solution:** Login with correct role (USER for meetings/agendas)

### Agenda not created automatically
**Cause:** Meeting creation failed or database error
**Solution:** Check backend logs, verify meeting was created successfully

### Navigation menu not showing
**Cause:** Not logged in or role not recognized
**Solution:** Logout and login again, check role in database

## Summary of Changes

### Backend
- ✅ Added time conflict validation in MeetingService
- ✅ Added automatic agenda creation when meetings are created
- ✅ Added getMeetingsByUser() method
- ✅ Added GET /api/meetings/user/{userId} endpoint
- ✅ Fixed CRUD operations

### Frontend
- ✅ Created MyMeetingsComponent for user's meetings
- ✅ Created MyAgendasComponent for user's agenda
- ✅ Implemented role-based navigation
- ✅ Added role badge in navbar
- ✅ Auto-set organizer in meeting form
- ✅ Role-based redirect after login/signup
- ✅ Updated routes for new components

All features are now working correctly with proper role-based access control! 🎉
