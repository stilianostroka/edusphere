# EduSphere frontend

React + TypeScript frontend for the EduSphere Spring Boot API.

## Run

```bash
npm install
npm run dev
```

The frontend runs at `http://localhost:5174`. The default backend is
`http://localhost:8080/api`. Override it with `VITE_API_URL` when required.

## Implemented profiles

- Administrator: academic years, classes, supervisors, subjects, teacher CRUD,
  student CRUD/enrollment, parent CRUD and child linking, teaching assignments,
  timetable editing, modification approval, final-grade approval, and diploma
  PDF generation.
- Teacher: own assignments, forum, timetable, lesson topics, per-lesson grades
  and attendance, exam/project/final grades, supervised class, student status,
  absence justification, parent meetings, notifications, syllabus upload, and
  modification requests.
- Parent: linked children, personal record, subject grades, absences, timetable,
  notifications, and diploma download.

No production screen reads from `mockData.ts`; the legacy data file can be
deleted after you confirm that no custom design work still needs it.
