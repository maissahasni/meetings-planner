# Meeting Planner Frontend

Angular TypeScript frontend for the Meeting Planner application.

## Features

- User Management (Create, List, Delete)
- Meeting Management (Create, List, Delete, Add/Remove Participants)
- Agenda Management (View by User, Delete)

## Prerequisites

- Node.js (v18 or higher)
- npm (v9 or higher)
- Angular CLI (v17 or higher)

## Installation

```bash
cd frontend
npm install
```

## Running the Application

```bash
npm start
```

The application will be available at `http://localhost:4200`

## Project Structure

```
src/
├── app/
│   ├── components/
│   │   ├── user-list/
│   │   ├── user-form/
│   │   ├── meeting-list/
│   │   ├── meeting-form/
│   │   └── agenda-list/
│   ├── models/
│   │   ├── user.model.ts
│   │   ├── meeting.model.ts
│   │   └── agenda.model.ts
│   ├── services/
│   │   ├── user.service.ts
│   │   ├── meeting.service.ts
│   │   └── agenda.service.ts
│   ├── app.component.ts
│   ├── app.routes.ts
│   └── app.config.ts
├── environments/
│   └── environment.ts
├── index.html
├── main.ts
└── styles.css
```

## API Configuration

The backend API URL is configured in `src/environments/environment.ts`:

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};
```

## Available Routes

- `/users` - List all users
- `/users/new` - Create new user
- `/meetings` - List all meetings
- `/meetings/new` - Create new meeting
- `/agendas` - View agendas by user

## Building for Production

```bash
npm run build
```

The build artifacts will be stored in the `dist/` directory.
