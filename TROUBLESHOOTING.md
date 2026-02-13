# Troubleshooting Guide

## Signup Failed Error

If you're getting "Signup failed" error, follow these steps:

### 1. Check Backend is Running

Make sure the Spring Boot backend is running:
```bash
cd backend
./mvnw spring-boot:run
```

Look for this line in the console:
```
Started BackendApplication in X.XXX seconds
```

### 2. Check Database Connection

The backend should automatically create the database and tables. Check the console for:
```
Hibernate: create table users ...
```

If you see database connection errors:
1. Make sure MySQL is running
2. Check credentials in `backend/src/main/resources/application.properties`
3. Ensure MySQL user has CREATE DATABASE permission

### 3. Add Password Column Manually (if needed)

If the password column is missing, run this SQL:

```sql
USE meeting_planner;
ALTER TABLE users ADD COLUMN password VARCHAR(255) NOT NULL DEFAULT 'password123';
```

Or run the provided script:
```bash
mysql -u root -p < database/add_password_column.sql
```

### 4. Check Browser Console

Open browser DevTools (F12) and check the Console tab for errors:

**Common errors:**

**CORS Error:**
```
Access to XMLHttpRequest at 'http://localhost:8080/api/auth/signup' from origin 'http://localhost:4200' has been blocked by CORS policy
```

**Solution:** 
- Restart the backend server
- Clear browser cache
- Make sure WebConfig.java has CORS configured

**Network Error:**
```
Http failure response for http://localhost:8080/api/auth/signup: 0 Unknown Error
```

**Solution:**
- Backend is not running
- Wrong port (should be 8080)
- Firewall blocking connection

**400 Bad Request:**
```
Http failure response for http://localhost:8080/api/auth/signup: 400 Bad Request
```

**Solution:**
- Check all required fields are filled
- Password must be at least 6 characters
- Email must be valid format

**409 Conflict:**
```
User with email xxx@example.com already exists
```

**Solution:**
- Use a different email
- Or delete the existing user from database

### 5. Check Backend Logs

Look at the backend console for detailed error messages. With the updated AuthService, you should see logs like:

```
INFO  - Signup attempt for email: test@example.com
INFO  - User created successfully with id: 1
```

Or error messages:
```
WARN  - Signup failed: Email test@example.com already exists
```

### 6. Test Backend Directly with curl

Test if the backend is working:

```bash
# Test signup
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name":"Test User",
    "email":"test@example.com",
    "password":"test123",
    "role":"USER"
  }'
```

Expected response:
```json
{
  "id": 1,
  "name": "Test User",
  "email": "test@example.com",
  "role": "USER",
  "message": "User registered successfully"
}
```

### 7. Clear Browser Data

Sometimes cached data causes issues:
1. Open DevTools (F12)
2. Go to Application tab
3. Clear Storage → Clear site data
4. Refresh the page

### 8. Check Frontend Environment

Make sure the API URL is correct in `frontend/src/environments/environment.ts`:

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};
```

### 9. Restart Everything

If nothing works, restart both servers:

**Backend:**
```bash
# Stop with Ctrl+C
cd backend
./mvnw clean
./mvnw spring-boot:run
```

**Frontend:**
```bash
# Stop with Ctrl+C
cd frontend
rm -rf node_modules package-lock.json
npm install
npm start
```

### 10. Check Database Manually

Connect to MySQL and verify the table structure:

```sql
USE meeting_planner;
DESCRIBE users;
```

You should see:
```
+----------+--------------+------+-----+---------+----------------+
| Field    | Type         | Null | Key | Default | Extra          |
+----------+--------------+------+-----+---------+----------------+
| id       | bigint       | NO   | PRI | NULL    | auto_increment |
| name     | varchar(255) | NO   |     | NULL    |                |
| email    | varchar(255) | NO   | UNI | NULL    |                |
| role     | varchar(50)  | NO   |     | NULL    |                |
| password | varchar(255) | NO   |     | NULL    |                |
+----------+--------------+------+-----+---------+----------------+
```

If `password` column is missing, run:
```sql
ALTER TABLE users ADD COLUMN password VARCHAR(255) NOT NULL;
```

## Common Error Messages

### "User with email xxx already exists"
**Cause:** Email is already registered
**Solution:** Use a different email or delete the existing user

### "Invalid email or password"
**Cause:** Wrong credentials during login
**Solution:** Check email and password are correct

### "Failed to fetch"
**Cause:** Backend is not running or wrong URL
**Solution:** Start backend on port 8080

### "CORS policy error"
**Cause:** CORS not configured properly
**Solution:** Check WebConfig.java and restart backend

### "Column 'password' cannot be null"
**Cause:** Database schema not updated
**Solution:** Run the SQL script to add password column

## Still Not Working?

1. Check all ports:
   - Backend: http://localhost:8080
   - Frontend: http://localhost:4200
   - MySQL: localhost:3306

2. Check firewall settings

3. Try a different browser

4. Check if another application is using port 8080 or 4200

5. Look for detailed error messages in:
   - Backend console
   - Browser DevTools Console
   - Browser DevTools Network tab

## Quick Test Checklist

- [ ] MySQL is running
- [ ] Backend is running on port 8080
- [ ] Frontend is running on port 4200
- [ ] Can access http://localhost:8080/api/users (should return [])
- [ ] Can access http://localhost:4200 (should show login page)
- [ ] Browser console shows no errors
- [ ] Backend console shows no errors
- [ ] Database `meeting_planner` exists
- [ ] Table `users` has `password` column

If all checkboxes are checked and it still doesn't work, please share:
1. Backend console output
2. Browser console errors
3. Network tab request/response details
