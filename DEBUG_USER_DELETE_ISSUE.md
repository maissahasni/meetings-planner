# Debug: User Delete Not Working

## Quick Diagnosis

### Most Likely Causes:

1. **Backend not restarted** after code changes
2. **Frontend cache** showing old data
3. **Database connection** issue
4. **Transaction not committing**

## Immediate Fix Steps

### Step 1: Restart Everything

```bash
# Stop backend if running (Ctrl+C)

# Clean and rebuild backend
cd backend
mvn clean install

# Start backend
mvn spring-boot:run

# In another terminal, restart frontend
cd frontend
ng serve --open
```

### Step 2: Clear Browser Cache

1. Open DevTools (F12)
2. Right-click refresh button
3. Select "Empty Cache and Hard Reload"
4. Or use Ctrl+Shift+Delete to clear cache

### Step 3: Test Delete

1. Login as admin
2. Go to Users page
3. Try to delete a user
4. Watch both:
   - Browser console (F12 → Console tab)
   - Backend terminal output

## What to Look For

### In Browser Console:

**Success:**
```
DELETE http://localhost:8080/api/users/1 204 (No Content)
```

**Failure:**
```
DELETE http://localhost:8080/api/users/1 500 (Internal Server Error)
```

### In Backend Console:

**Success:**
```
Hibernate: delete from meeting_participants where user_id=?
Hibernate: delete from agendas where user_id=?
Hibernate: delete from meetings where organizer_id=?
Hibernate: delete from users where id=?
```

**Failure (Foreign Key Error):**
```
java.sql.SQLIntegrityConstraintViolationException: Cannot delete or update a parent row: 
a foreign key constraint fails
```

**Failure (Lazy Loading Error):**
```
org.hibernate.LazyInitializationException: could not initialize proxy - no Session
```

## Specific Error Solutions

### Error 1: "Cannot delete or update a parent row"

**Cause**: Database foreign key constraint
**Solution**: The code should handle this, but verify:

```java
// Check UserService.deleteUser() has all 4 steps:
@Transactional
public void deleteUser(Long id) {
    User user = getUserById(id);
    
    // Step 1: Remove from participants
    List<Meeting> participatingMeetings = meetingRepository.findByParticipantsId(id);
    for (Meeting meeting : participatingMeetings) {
        meeting.getParticipants().remove(user);
        meetingRepository.save(meeting);
    }
    
    // Step 2: Delete agendas
    List<Agenda> userAgendas = agendaRepository.findByUserId(id);
    agendaRepository.deleteAll(userAgendas);
    
    // Step 3: Delete organized meetings
    List<Meeting> organizedMeetings = meetingRepository.findByOrganizerId(id);
    meetingRepository.deleteAll(organizedMeetings);
    
    // Step 4: Delete user
    userRepository.delete(user);
}
```

### Error 2: "LazyInitializationException"

**Cause**: Trying to access lazy-loaded relationships outside transaction
**Solution**: Already fixed with explicit queries

### Error 3: Frontend shows "Failed to delete user"

**Possible Causes:**
1. Backend not running
2. CORS issue
3. Network error
4. Backend error

**Check:**
```bash
# Is backend running?
curl http://localhost:8080/api/users

# Try delete directly
curl -X DELETE http://localhost:8080/api/users/1 -v
```

### Error 4: User disappears then reappears

**Cause**: Frontend cache or optimistic update
**Solution**: Check if `loadUsers()` is called after delete

```typescript
deleteUser(id: number | undefined): void {
  if (!id) return;
  
  if (confirm('Are you sure you want to delete this user?')) {
    this.userService.deleteUser(id).subscribe({
      next: () => {
        this.loadUsers(); // ← This must be here
      },
      error: (err) => {
        this.error = 'Failed to delete user';
        console.error('Error deleting user:', err);
      }
    });
  }
}
```

## Manual Database Check

### Check Current State:
```sql
-- Count users
SELECT COUNT(*) FROM users;

-- List all users
SELECT id, name, email, role FROM users;

-- Check user's relationships
SELECT 
    u.id,
    u.name,
    COUNT(DISTINCT m1.id) as organized_meetings,
    COUNT(DISTINCT mp.meeting_id) as participating_meetings,
    COUNT(DISTINCT a.id) as agendas
FROM users u
LEFT JOIN meetings m1 ON u.id = m1.organizer_id
LEFT JOIN meeting_participants mp ON u.id = mp.user_id
LEFT JOIN agendas a ON u.id = a.user_id
WHERE u.id = 1
GROUP BY u.id, u.name;
```

### Manual Delete (if needed):
```sql
-- Delete in correct order
DELETE FROM meeting_participants WHERE user_id = 1;
DELETE FROM agendas WHERE user_id = 1;
DELETE FROM meetings WHERE organizer_id = 1;
DELETE FROM users WHERE id = 1;
```

## Code Verification

### Verify UserService.java:
```bash
cat backend/src/main/java/com/example/backend/service/UserService.java | grep -A 25 "public void deleteUser"
```

Should show the complete 4-step delete method.

### Verify User.java:
```bash
cat backend/src/main/java/com/example/backend/entity/User.java | grep -A 2 "cascade"
```

Should show:
```java
cascade = CascadeType.ALL, orphanRemoval = true
```

## Test with Simple User

### Create a test user with no relationships:
```sql
INSERT INTO users (name, email, password, role) 
VALUES ('Test Delete', 'testdelete@example.com', 'password', 'USER');
```

### Try to delete this user:
1. Refresh users list
2. Find "Test Delete" user
3. Click delete
4. Should work immediately (no relationships to clean up)

If this works, the issue is with relationship cleanup.
If this doesn't work, the issue is with the delete endpoint itself.

## Enable Detailed Logging

### Add to application.properties:
```properties
# SQL logging
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Transaction logging
logging.level.org.springframework.transaction=DEBUG

# Hibernate logging
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# Your service logging
logging.level.com.example.backend.service=DEBUG
```

### Restart backend and try delete again

Watch the console output carefully - it will show:
- Every SQL query executed
- Transaction boundaries
- Any errors or exceptions

## Frontend Network Tab Analysis

### Open DevTools → Network Tab

1. Click delete button
2. Look for DELETE request to `/api/users/{id}`
3. Check:
   - **Status**: Should be 204
   - **Response**: Should be empty
   - **Time**: Should be < 1 second

### If Status is 500:
Click on the request → Preview tab → See error message

### If Status is 404:
User doesn't exist or wrong ID

### If Status is 403:
Authorization issue (shouldn't happen with current setup)

### If No Request Appears:
JavaScript error preventing request - check Console tab

## Common Mistakes

### 1. Wrong User ID
```typescript
// Make sure user.id is defined
deleteUser(user.id)  // ✅ Correct
deleteUser(undefined)  // ❌ Wrong
```

### 2. Not Waiting for Backend
```bash
# Start backend
mvn spring-boot:run

# Wait for this message before testing:
# "Started BackendApplication in X seconds"
```

### 3. Wrong Port
```typescript
// Check environment.ts
apiUrl: 'http://localhost:8080/api'  // ✅ Correct
apiUrl: 'http://localhost:3000/api'  // ❌ Wrong (unless you changed it)
```

### 4. Database Not Running
```bash
# Check MySQL is running
mysql -u root -p -e "SELECT 1"

# Check database exists
mysql -u root -p -e "SHOW DATABASES LIKE 'meeting_planner'"
```

## Still Not Working?

### Collect This Information:

1. **Backend Console Output** (last 100 lines):
```bash
# Copy from terminal or
mvn spring-boot:run > backend.log 2>&1
```

2. **Browser Console Errors**:
- F12 → Console tab
- Copy any red errors

3. **Network Request Details**:
- F12 → Network tab
- Click DELETE request
- Copy Request Headers and Response

4. **Database State**:
```sql
SELECT COUNT(*) FROM users;
SELECT COUNT(*) FROM meetings;
SELECT COUNT(*) FROM agendas;
SELECT COUNT(*) FROM meeting_participants;
```

5. **Code Verification**:
```bash
# Show deleteUser method
grep -A 30 "public void deleteUser" backend/src/main/java/com/example/backend/service/UserService.java
```

With this information, we can identify the exact issue.

## Nuclear Option: Fresh Start

If nothing works:

```bash
# 1. Stop everything
# Ctrl+C on backend and frontend

# 2. Clean database
mysql -u root -p meeting_planner < database/schema.sql

# 3. Clean backend
cd backend
mvn clean
rm -rf target/

# 4. Rebuild
mvn install -DskipTests

# 5. Start fresh
mvn spring-boot:run

# 6. In another terminal
cd frontend
rm -rf node_modules/.cache
ng serve

# 7. Test with fresh data
```

This ensures no cached data or stale state is causing issues.
