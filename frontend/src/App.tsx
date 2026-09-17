import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { AcademicYearProvider } from './context/AcademicYearContext';
import { MessageProvider } from './context/MessageContext';
import { LoginPage } from './components/LoginPage';
import { ProtectedRoute } from './components/ProtectedRoute';
import { RoleHomePage } from './pages/RoleHomePage';
import { AcademicYearsPage } from './pages/AcademicYearsPage';
import { SubjectsPage } from './pages/SubjectsPage';
import { TeachersPage } from './pages/TeachersPage';
import { StudentsPage } from './pages/StudentsPage';
import { ParentsPage } from './pages/ParentsPage';
import { ClassesPage } from './pages/ClassesPage';
import { TeachingAssignmentsPage } from './pages/TeachingAssignmentsPage';
import { AdminTimetablePage } from './pages/AdminTimetablePage';
import { ModificationRequestsPage } from './pages/ModificationRequestsPage';
import { FinalGradesApprovalPage } from './pages/FinalGradesApprovalPage';
import { DiplomasPage } from './pages/DiplomasPage';
import { RegistryPage } from './pages/RegistryPage';
import { TopicsPage } from './pages/TopicsPage';
import { FinalGradeRegisterPage } from './pages/FinalGradeRegisterPage';
import { SupervisedClassPage } from './pages/SupervisedClassPage';
import { DailyAbsencesPage } from './pages/DailyAbsencesPage';
import { SyllabusPage } from './pages/SyllabusPage';
import { SentNotificationsPage } from './pages/SentNotificationsPage';
import { SubmitModificationRequestPage } from './pages/SubmitModificationRequestPage';
import { TeacherTimetablePage } from './pages/TeacherTimetablePage';
import { ChildDetailPage } from './pages/ChildDetailPage';

function TimetableRouter() {
  const { user } = useAuth();
  return user?.role === 'TEACHER' ? <TeacherTimetablePage /> : <AdminTimetablePage />;
}

export default function App() {
  return (
    <MessageProvider>
    <AuthProvider>
      <AcademicYearProvider>
        <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/" element={<ProtectedRoute><RoleHomePage /></ProtectedRoute>} />

          {/* Admin */}
          <Route path="/academic-years" element={<ProtectedRoute roles={['ADMIN']}><AcademicYearsPage /></ProtectedRoute>} />
          <Route path="/subjects" element={<ProtectedRoute roles={['ADMIN']}><SubjectsPage /></ProtectedRoute>} />
          <Route path="/teachers" element={<ProtectedRoute roles={['ADMIN']}><TeachersPage /></ProtectedRoute>} />
          <Route path="/students" element={<ProtectedRoute roles={['ADMIN']}><StudentsPage /></ProtectedRoute>} />
          <Route path="/parents" element={<ProtectedRoute roles={['ADMIN']}><ParentsPage /></ProtectedRoute>} />
          <Route path="/classes" element={<ProtectedRoute roles={['ADMIN']}><ClassesPage /></ProtectedRoute>} />
          <Route path="/teaching-assignments" element={<ProtectedRoute roles={['ADMIN']}><TeachingAssignmentsPage /></ProtectedRoute>} />
          <Route path="/modification-requests" element={<ProtectedRoute roles={['ADMIN']}><ModificationRequestsPage /></ProtectedRoute>} />
          <Route path="/final-grades" element={<ProtectedRoute roles={['ADMIN']}><FinalGradesApprovalPage /></ProtectedRoute>} />
          <Route path="/diplomas" element={<ProtectedRoute roles={['ADMIN']}><DiplomasPage /></ProtectedRoute>} />

          {/* Teacher */}
          <Route path="/topics" element={<ProtectedRoute roles={['TEACHER']}><TopicsPage /></ProtectedRoute>} />
          <Route path="/registry" element={<ProtectedRoute roles={['TEACHER']}><RegistryPage /></ProtectedRoute>} />
          <Route path="/final-grade-register" element={<ProtectedRoute roles={['TEACHER']}><FinalGradeRegisterPage /></ProtectedRoute>} />
          <Route path="/supervised-class" element={<ProtectedRoute roles={['TEACHER']}><SupervisedClassPage /></ProtectedRoute>} />
          <Route path="/daily-absences" element={<ProtectedRoute roles={['TEACHER']}><DailyAbsencesPage /></ProtectedRoute>} />
          <Route path="/syllabus" element={<ProtectedRoute roles={['TEACHER']}><SyllabusPage /></ProtectedRoute>} />
          <Route path="/sent-notifications" element={<ProtectedRoute roles={['TEACHER']}><SentNotificationsPage /></ProtectedRoute>} />
          <Route path="/submit-modification" element={<ProtectedRoute roles={['TEACHER']}><SubmitModificationRequestPage /></ProtectedRoute>} />

          {/* Shared - resolves per role inside TimetableRouter */}
          <Route path="/timetable" element={<ProtectedRoute roles={['ADMIN','TEACHER']}><TimetableRouter /></ProtectedRoute>} />

          {/* Parent */}
          <Route path="/children/:id" element={<ProtectedRoute roles={['PARENT']}><ChildDetailPage /></ProtectedRoute>} />

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
      </AcademicYearProvider>
    </AuthProvider>
    </MessageProvider>
  );
}
