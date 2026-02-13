# Quick Start Guide - Meeting Planner Application

## Prerequisites
- Java 17+
- Node.js 18+
- MySQL 8+
- Maven 3.8+
- Angular CLI 17+

## Setup Instructions

### 1. Database Setup
```bash
# Create database
mysql -u root -p
CREATE DATABASE meeting_planner;
exit;

# Run schema
mysql -u root -p meeting_planner < database/schema.sql

# (Optional) Load sample data
mysql -u root -p meeting_planner < database/sample-data.sql
```

### 2. Backend Setup
```bash
cd backend

# Update application.properties with your MySQL credentials
# Edit: src/main/resources/application.properties

# Build and run
mvn clean install
mvn spring-boot:run

# Backend will start on http://localhost:8080
```

### 3. Frontend Setup
```bash
cd frontend

# Install dependencies
npm install

# Start development server
ng serve

# Frontend will start on http://localhost:4200
```

## Default Access

### Create First Admin User
1. Go to http://localhost:4200/signup
2. Create user with:
   - Name: Admin User
   - Email: admin@example.com
   - Password: admin123
   - Role: ADMIN

### Create Test User
1. Login as admin
2. Go to "New User"
3. Create user with:
   - Name: Test User
   - Email: user@example.com
   - Password: user123
   - Role: USER

## Admin Features

### Login as Admin
- Email: admin@example.com
- Password: admin123

### Admin Menu
- **Users** - View all users
- **New User** - Create new user
- **All Meetings** - View/manage all meetings
- **All Agendas** - View any user's agendas

### Admin Capabilities
✅ Full user management (CRUD)
✅ View all meetings across all users
✅ Create meetings for any user
✅ Edit/delete any meeting
✅ View any user's agendas
✅ Delete any agenda

## User Features

### Login as User
- Email: user@example.com
- Password: user123

### User Menu
- **My Meetings** - View your meetings
- **New Meeting** - Schedule a meeting
- **My Agenda** - View your agenda

### User Capabilities
✅ View own meetings (organizer + participant)
✅ Create meetings (self as organizer)
✅ Select participants
✅ View own agendas
❌ Cannot view other users' data
❌ Cannot manage users

## Common Tasks

### Create a Meeting (Admin)
1. Login as admin
2. Click "All Meetings"
3. Click "Create Meeting"
4. Fill in:
   - Title
   - Description (optional)
   - Start Time
   - End Time
   - Select Organizer
   - Check Participants
5. Click "Create Meeting"
6. Agendas automatically created for all attendees

### Create a Meeting (User)
1. Login as user
2. Click "New Meeting"
3. Fill in:
   - Title
   - Description (optional)
   - Start Time
   - End Time
   - Check Participants
4. Click "Create Meeting"
5. You are automatically the organizer

### View User's Agendas (Admin)
1. Login as admin
2. Click "All Agendas"
3. Select user from dropdown
4. View their complete agenda
5. See meeting associations
6. Delete agenda items if needed

### View Your Agenda (User)
1. Login as user
2. Click "My Agenda"
3. See all your agenda items
4. View meeting details
5. See FREE/BUSY status

## API Testing

### Using cURL

#### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","password":"admin123"}'
```

#### Get All Users
```bash
curl -X GET http://localhost:8080/api/users
```

#### Create Meeting
```bash
curl -X POST http://localhost:8080/api/meetings \
  -H "Content-Type: application/json" \
  -d '{
    "title":"Team Meeting",
    "description":"Weekly sync",
    "startTime":"2026-02-20T10:00:00",
    "endTime":"2026-02-20T11:00:00",
    "organizerId":1,
    "participantIds":[2,3]
  }'
```

#### Get User's Agendas
```bash
curl -X GET http://localhost:8080/api/agendas/user/1
```

## Troubleshooting

### Backend won't start
- Check MySQL is running
- Verify database credentials in application.properties
- Ensure port 8080 is available
- Check Java version: `java -version`

### Frontend won't start
- Run `npm install` again
- Clear node_modules: `rm -rf node_modules && npm install`
- Check Node version: `node -v`
- Ensure port 4200 is available

### CORS errors
- Verify backend is running on port 8080
- Check @CrossOrigin annotations in controllers
- Clear browser cache

### Login fails
- Check user exists in database
- Verify password is correct
- Check browser console for errors
- Verify backend API is accessible

### Meetings not creating
- Check time conflict errors
- Verify organizer exists
- Verify all participants exist
- Check start time < end time

### Agendas not showing
- Verify user has meetings scheduled
- Check agendas were created automatically
- Verify user ID is correct
- Check database for agenda records

## File Structure

```
project/
├── backend/
│   ├── src/main/java/com/example/backend/
│   │   ├── controller/     # REST API endpoints
│   │   ├── service/        # Business logic
│   │   ├── repository/     # Data access
│   │   ├── entity/         # Database models
│   │   ├── dto/            # Data transfer objects
│   │   ├── mapper/         # Entity-DTO conversion
│   │   ├── exception/      # Error handling
│   │   └── config/         # Configuration
│   └── src/main/resources/
│       └── application.properties
├── frontend/
│   └── src/app/
│       ├── components/     # UI components
│       ├── services/       # HTTP services
│       ├── models/         # TypeScript interfaces
│       └── environments/   # Configuration
└── database/
    ├── schema.sql          # Database schema
    └── sample-data.sql     # Sample data
```

## Key URLs

- Frontend: http://localhost:4200
- Backend API: http://localhost:8080/api
- Login: http://localhost:4200/login
- Signup: http://localhost:4200/signup
- Admin Users: http://localhost:4200/users
- Admin Meetings: http://localhost:4200/admin/meetings
- Admin Agendas: http://localhost:4200/admin/agendas
- User Meetings: http://localhost:4200/my-meetings
- User Agendas: http://localhost:4200/my-agendas

## Development Tips

### Hot Reload
- Frontend: Automatic with `ng serve`
- Backend: Use Spring Boot DevTools or restart manually

### Debugging
- Frontend: Browser DevTools (F12)
- Backend: IDE debugger or add logging

### Database Changes
- Backend auto-updates schema with `spring.jpa.hibernate.ddl-auto=update`
- For manual changes, update schema.sql

### Code Changes
- Frontend: Changes auto-reload
- Backend: Restart Spring Boot application

## Next Steps

1. ✅ Start backend and frontend
2. ✅ Create admin user via signup
3. ✅ Login as admin
4. ✅ Create test users
5. ✅ Create test meetings
6. ✅ View agendas
7. ✅ Test all CRUD operations
8. ✅ Test role-based access
9. ✅ Verify time conflict detection
10. ✅ Check automatic agenda creation

## Support Resources

- `COMPLETE_SYSTEM_SUMMARY.md` - Full system documentation
- `ADMIN_ACCESS_COMPLETE.md` - Admin features
- `CRUD_SERVICES_FIX_COMPLETE.md` - CRUD operations
- `TROUBLESHOOTING.md` - Common issues
- `AUTHENTICATION_GUIDE.md` - Auth system
- `ROLE_BASED_ACCESS_GUIDE.md` - Access control

## Success Indicators

✅ Backend starts without errors
✅ Frontend compiles and serves
✅ Can login with created users
✅ Admin sees admin menu
✅ User sees user menu
✅ Can create users (admin)
✅ Can create meetings
✅ Agendas auto-create
✅ Time conflicts detected
✅ Can view and delete data

## Getting Help

If you encounter issues:
1. Check console logs (browser and terminal)
2. Review TROUBLESHOOTING.md
3. Verify all services are running
4. Check database connections
5. Ensure correct ports are used
6. Clear browser cache
7. Restart services

Enjoy using the Meeting Planner Application! 🎉
