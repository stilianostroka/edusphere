import { useAuth } from '../context/AuthContext';
import { AdminHomePage } from './AdminHomePage';
import { TeacherHomePage } from './TeacherHomePage';
import { ParentHomePage } from './ParentHomePage';

// The "/" route means something different per role - this picks the right
// home page based on who's actually logged in, rather than three separate
// routes all pointing at "/".
export function RoleHomePage() {
  const { user } = useAuth();
  if (user?.role === 'TEACHER') return <TeacherHomePage />;
  if (user?.role === 'PARENT') return <ParentHomePage />;
  return <AdminHomePage />;
}
