# Testing User Delete Functionality

## Steps to Test

### 1. Start the Backend
```bash
cd backend
mvn spring-boot:run
```

Wait for the message: "Started BackendApplication"

### 2. Start the Frontend
```bash
cd frontend
ng serve
```

Wait for: "Compiled successfully"

### 3. Test Delete Operation

#### Option A: Using the UI
1. Open browser: http://localhost:4200
2. Login as admin
3. Go to "Users" page
4. Click "Delete" on a user
5. Confirm the deletion
6. Check if user is removed from list

#### Option B: Using cURL (Direct API Test)
```bash
# First, get list of users
curl -X GET http://localhost:8080/api/users

# Note a user ID, then delete it
curl -X DELETE http://localhost:8080/api/users/1

# Verify it's deleted
curl -X GET http://localhost:8080/api/users
```

### 4. Check Backend Logs

Look for any errors in the backend console:
- Foreign key constraint violations
- Transaction errors
- NullPointerException
- LazyInitializationException

## Common Issues and Solutions

### Issue 1: Backend Not Running
**Symptom**: Frontend shows "Failed to delete user" or network error
**Solution**: Start the backend with `mvn spring-boot:run`

### Issue 2: Changes Not Applied
**Symptom**: Still getting old errors
**Solution**: 
1. Stop backend (Ctrl+C)
2. Clean: `mvn clean`
3. Rebuild: `mvn compile`
4. Restart: `mvn spring-boot:run`

### Issue 3: Database Constraint Error
**Symptom**: Backend logs show "foreign key constraint fails"
**Solution**: The code should handle this, but if not:
1. Check if user has meetings as organizer
2. Check if user is in meeting_participants table
3. Check if user has agendas

### Issue 4: Transaction Rollback
**Symptom**: Partial deletion or no deletion
**Solution**: Check backend logs for transaction errors

### Issue 5: Lazy Loading Error
**Symptom**: "could not initialize proxy - no Session"
**Solution**: Already handled with explicit queries in deleteUser()

## Debug Steps

### 1. Enable SQL Logging
In `application.properties`:
```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

### 2. Add Debug Logging to UserService
Add this to `deleteUser()` method:
```java
@Transactional
public void deleteUser(Long id) {
    System.out.println("=== Starting user deletion for ID: " + id + " ===");
    
    User user = getUserById(id);
    System.out.println("Found user: " + user.getName());
    
    // Step 1
    List<Meeting> participatingMeetings = meetingRepository.findByParticipantsId(id);
    System.out.println("Found " + participatingMeetings.size() + " meetings where user is participant");
    
    for (Meeting meeting : participatingMeetings) {
        meeting.getParticipants().remove(user);
        meetingRepository.save(meeting);
    }
    System.out.println("Removed user from participant lists");
    
    // Step 2
    List<Agenda> userAgendas = agendaRepository.findByUserId(id);
    System.out.println("Found " + userAgendas.size() + " agendas to delete");
    agendaRepository.deleteAll(userAgendas);
    System.out.println("Deleted agendas");
    
    // Step 3
    List<Meeting> organizedMeetings = meetingRepository.findByOrganizerId(id);
    System.out.println("Found " + organizedMeetings.size() + " organized meetings to delete");
    meetingRepository.deleteAll(organizedMeetings);
    System.out.println("Deleted organized meetings");
    
    // Step 4
    userRepository.delete(user);
    System.out.println("=== User deleted successfully ===");
}
```

### 3. Check Browser Console
Open browser DevTools (F12) and check:
- Network tab: Is the DELETE request being sent?
- Console tab: Any JavaScript errors?
- Response: What status code? (204 = success, 500 = server error)

### 4. Check Database Directly
```sql
-- Check if user still exists
SELECT * FROM users WHERE id = 1;

-- Check related records
SELECT * FROM meetings WHERE organizer_id = 1;
SELECT * FROM meeting_participants WHERE user_id = 1;
SELECT * FROM agendas WHERE user_id = 1;
```

## Expected Behavior

### Successful Delete:
1. Frontend sends DELETE request to `/api/users/{id}`
2. Backend receives request
3. UserService.deleteUser() executes:
   - Removes user from participant lists
   - Deletes user's agendas
   - Deletes organized meetings
   - Deletes user
4. Backend returns 204 No Content
5. Frontend refreshes user list
6. User no longer appears in list

### Failed Delete:
1. Frontend sends DELETE request
2. Backend encounters error
3. Transaction rolls back
4. Backend returns 500 Internal Server Error
5. Frontend shows error message
6. User still appears in list

## Verification Checklist

- [ ] Backend is running on port 8080
- [ ] Frontend is running on port 4200
- [ ] Can login as admin
- [ ] Can see users list
- [ ] Delete button is visible
- [ ] Clicking delete shows confirmation
- [ ] Confirming sends DELETE request
- [ ] Backend processes request without errors
- [ ] User is removed from list
- [ ] Database no longer has user record
- [ ] No orphaned records in database

## If Still Not Working

### Check These Files Were Saved:
1. `backend/src/main/java/com/example/backend/service/UserService.java`
2. `backend/src/main/java/com/example/backend/entity/User.java`

### Verify Code Changes:
```bash
# Check UserService has the new deleteUser method
grep -A 20 "public void deleteUser" backend/src/main/java/com/example/backend/service/UserService.java

# Check User entity has cascade settings
grep -A 2 "cascade = CascadeType.ALL" backend/src/main/java/com/example/backend/entity/User.java
```

### Rebuild from Scratch:
```bash
cd backend
mvn clean
mvn compile
mvn spring-boot:run
```

## Contact Points

If delete still fails, provide:
1. Backend console output (last 50 lines)
2. Browser console errors
3. Network tab response
4. Database state (user count before/after)

This will help identify the exact issue.
