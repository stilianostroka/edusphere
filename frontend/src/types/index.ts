export type Role = 'ADMIN' | 'TEACHER' | 'PARENT';

export interface AuthUser {
  name: string;
  role: Role;
  email?: string;
  profileId?: number | null;
}

export interface SchoolInfo {
  name: string;
  academicYear: string;
  address: string;
}

export interface TeacherRow {
  id: number;
  name: string;
  email: string;
  subjects: string[];
  supervisesClass: string | null;
}

export interface ClassRow {
  id: number;
  className: string;
  classYear: string;
  supervisor: string | null;
  studentCount: number;
  maxStudents: number;
}

export interface AcademicYearRow {
  id: number;
  label: string;
  startDate: string;
  endDate: string;
  active: boolean;
}

export interface SubjectRow {
  id: number;
  subjectName: string;
  code: string;
  programYear: number;
  totalHours: number;
}

export interface StudentRow {
  id: number;
  firstName: string;
  lastName: string;
  className: string | null;
  status: 'ACTIVE' | 'GRADUATED' | 'SUSPENDED' | 'TRANSFERRED' | 'WITHDRAWN';
}

export interface ParentRow {
  id: number;
  name: string;
  email: string;
  children: string[];
}

export interface TeachingAssignmentRow {
  id: number;
  className: string;
  subjectName: string;
  teacherName: string;
}

export interface ModificationRequestRow {
  id: number;
  teacherName: string;
  type: 'Topic' | 'Grade' | 'Absence' | 'Exam Grade';
  target: string;
  explanation: string;
  submittedAt: string;
  status: 'Pending' | 'Approved' | 'Rejected';
}

export interface FinalGradeApprovalRow {
  id: number;
  studentName: string;
  className: string;
  subjectName: string;
  cceg: number;
  cfe: number;
  projectGrade: number;
  finalGrade: number;
}

export interface DiplomaRow {
  id: number;
  studentName: string;
  className: string;
  status: 'Not generated' | 'Generated' | 'Sent to parent';
}

export interface TeacherProfile {
  name: string;
  subjects: string[];
  classesTeaching: string[];
  supervisesClass: string | null;
}

export interface ForumPost {
  id: number;
  author: string;
  time: string;
  body: string;
}

export interface TopicRow {
  id: number;
  date: string;
  topicName: string;
  description: string | null;
  gradedCount: number;
  totalStudents: number;
}

export interface LessonRecordRow {
  studentId: number;
  studentName: string;
  grade: number | null;
  absent: boolean;
  justified: boolean;
}

export interface AnnualGradeRow {
  studentName: string;
  cceg: number | null;
  cfe: number | null;
  projectGrade: number | null;
  finalGrade: number | null;
  status: string;
}

export interface SupervisedStudentRow {
  id: number;
  name: string;
  personalId: string;
  status: string;
}

export interface AbsenceSummaryRow {
  studentName: string;
  justified: number;
  unjustified: number;
  total: number;
}

export interface ParentMeetingRow {
  id: number;
  date: string;
  topicsDiscussed: string;
}

export interface ChildSummary {
  id: number;
  name: string;
  className: string;
  supervisor: string;
}

export interface ChildGradeRow {
  subjectName: string;
  cceg: number | null;
  cfe: number | null;
  projectGrade: number | null;
  finalGrade: number | null;
}

export interface ChildAbsenceRow {
  date: string;
  subjectName: string;
  justified: boolean;
}

export interface NotificationRow {
  id: number;
  from: string;
  message: string;
  sentAt: string;
}

export interface AllSubjectsGradeRow {
  studentName: string;
  subjectName: string;
  teacherName: string;
  cceg: number | null;
  cfe: number | null;
  projectGrade: number | null;
  finalGrade: number | null;
}

export interface DailyAbsenceRow {
  date: string;
  subjectName: string;
  justified: boolean;
}

export interface SyllabusRow {
  subjectName: string;
  className: string;
  fileName: string | null;
  uploadedAt: string | null;
}

export interface SentNotificationRow {
  id: number;
  target: string;
  message: string;
  sentAt: string;
}

export interface ChildMonthlyGradeRow {
  month: string;
  subjectName: string;
  grades: number[];
}

export interface ChildDiplomaInfo {
  status: 'Not generated' | 'Generated' | 'Sent to parent';
  fileName: string | null;
}
