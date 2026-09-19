# EduSphere

A school management system built to replace the spreadsheet-and-paper workflow a real school was using for grading, attendance, and parent communication. Role-based access for administrators, teachers, and parents, backed by a JWT-secured REST API.

## Stack

**Backend** — Java 17, Spring Boot 4 (Web MVC, Data JPA, Security, Validation, Mail, Actuator), MySQL, JWT (jjwt), OpenPDF for diploma generation, Lombok.

**Frontend** — React 18 + TypeScript, Vite, React Router v6, Axios.

No ORM-agnostic abstraction, no microservices — one Spring Boot monolith, one MySQL database, one SPA. That's the right amount of architecture for the scale this is at.

## What it does

**Administrators** manage academic years, classes, subjects, teachers, students, parents, teaching assignments, and the school timetable. They review and approve teacher-submitted final grades and modification requests, and generate diploma PDFs.

**Teachers** run their own classes: record lesson topics and per-lesson grades/attendance (locked to a 24-hour edit window after entry, then routed through an approval-based modification request), manage project/exam/final grades through a draft → submitted → approved lifecycle, view their supervised class as a homeroom teacher (roster, notifications, parent meetings, status changes), see a monthly attendance grid, upload syllabi, and post to a teacher forum.

**Parents** see their linked children's grades, attendance, timetable, notifications, and diplomas — read-only.

Every list that's scoped to a school year (assignments, classes, timetables, grade registers) respects the academic year selected in the top bar, not just whichever year happens to be active.

## Project layout

```
backend/    Spring Boot API (Maven)
frontend/   React SPA (Vite)
```

Two independent apps talking over HTTP — no shared build tooling, no monorepo package manager.

## Running it locally

### Prerequisites

- Java 17
- Node 18+
- MySQL 8

### Database

```sql
CREATE DATABASE edusphere;
CREATE USER 'edusphere'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON edusphere.* TO 'edusphere'@'localhost';
```

Hibernate handles the schema (`ddl-auto=update`) — no manual migrations to run.

### Backend

Set these environment variables before starting:

| Variable | Purpose |
|---|---|
| `DB_PASSWORD` | MySQL password for the `edusphere` user |
| `JWT_SECRET` | Signing key for auth tokens |
| `MAIL_USERNAME` | Gmail address used to send password-reset emails |
| `MAIL_PASSWORD` | Gmail **app password** (not your account password — regular passwords are rejected by Gmail's SMTP) |
| `FRONTEND_URL` | Optional, defaults to `http://localhost:5173`. Used to build the reset-password link in emails. |

```bash
cd backend
./mvnw spring-boot:run
```

Runs on `http://localhost:8080`.

There's no seed data and no default admin account. Create the first one directly:

```bash
curl -X POST http://localhost:8080/api/admin \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@example.com", "password": "changeme"}'
```

Everything else (teachers, students, parents, classes) is created through the app once you're logged in as that admin.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Runs on `http://localhost:5174` and expects the API at `http://localhost:8080/api` by default — override with a `VITE_API_URL` env var if your backend lives elsewhere.

The backend's CORS config only allows `http://localhost:5174` right now, so if you change the frontend's port, update `SecurityConfig.java` to match.
