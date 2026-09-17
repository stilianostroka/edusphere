import { useState, type ReactNode } from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { LogoutConfirmModal } from './LogoutConfirmModal';
import { useAcademicYear } from '../context/AcademicYearContext';
import { Icon } from './Icon';

interface NavItem {
  to: string;
  label: string;
  icon: string;
}

const ADMIN_NAV: NavItem[] = [
  { to: '/', label: 'School Overview', icon: 'school' },
  { to: '/academic-years', label: 'Academic Years', icon: 'calendar' },
  { to: '/classes', label: 'Classes', icon: 'book' },
  { to: '/subjects', label: 'Subjects', icon: 'book-open' },
  { to: '/teachers', label: 'Teachers', icon: 'teacher' },
  { to: '/students', label: 'Students', icon: 'graduation-cap' },
  { to: '/parents', label: 'Parents', icon: 'family' },
  { to: '/teaching-assignments', label: 'Teaching Assignments', icon: 'link' },
  { to: '/timetable', label: 'Timetable', icon: 'calendar-grid' },
  { to: '/modification-requests', label: 'Modification Requests', icon: 'inbox' },
  { to: '/final-grades', label: 'Final Grade Approvals', icon: 'flag' },
  { to: '/diplomas', label: 'Diplomas', icon: 'award' }
];

const TEACHER_NAV: NavItem[] = [
  { to: '/', label: 'Home', icon: 'home' },
  { to: '/topics', label: 'Topics', icon: 'list' },
  { to: '/registry', label: 'Registry', icon: 'clipboard' },
  { to: '/final-grade-register', label: 'Final Grades', icon: 'bar-chart' },
  { to: '/supervised-class', label: 'Supervised Class', icon: 'users' },
  { to: '/daily-absences', label: 'Daily Absences', icon: 'calendar-x' },
  { to: '/syllabus', label: 'Syllabus', icon: 'file' },
  { to: '/sent-notifications', label: 'Sent Notifications', icon: 'send' },
  { to: '/submit-modification', label: 'Modification Requests', icon: 'mail' },
  { to: '/timetable', label: 'Timetable', icon: 'calendar-grid' }
];

const PARENT_NAV: NavItem[] = [
  { to: '/', label: 'My Children', icon: 'family' }
];

export function AppShell({ children }: { children: ReactNode }) {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [confirmingLogout, setConfirmingLogout] = useState(false);
  const [profileOpen, setProfileOpen] = useState(false);
  const { years, selectedYearId, setSelectedYearId } = useAcademicYear();

  const navItems = user?.role === 'ADMIN' ? ADMIN_NAV : user?.role === 'PARENT' ? PARENT_NAV : TEACHER_NAV;
  const showYearSelector = user?.role === 'ADMIN' || user?.role === 'TEACHER';

  function handleConfirmLogout() {
    logout();
    navigate('/login');
  }

  const initials = user?.name
    .split(' ')
    .map((part) => part[0])
    .join('')
    .slice(0, 2)
    .toUpperCase();

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="sidebar-brand">
          <div className="brand-mark">E</div>
          <span>EduSphere</span>
        </div>
        <nav className="sidebar-nav">
          {navItems.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              end={item.to === '/'}
              className={({ isActive }) => `sidebar-item ${isActive ? 'active' : ''}`}
            >
              <span className="icon"><Icon name={item.icon} /></span>
              {item.label}
            </NavLink>
          ))}
        </nav>
      </aside>

      <div className="main-area">
        <header className="topbar">
          <div className="topbar-chips">
            <div className="topbar-chip">
              <span className="chip-icon"><Icon name="calendar" size={16} /></span>
              <div>
                <span className="chip-label">Academic Year</span>
                {showYearSelector ? (
                  <select
                    value={selectedYearId ?? ''}
                    onChange={(e) => setSelectedYearId(Number(e.target.value))}
                  >
                    {years.length === 0 && <option value="">No years yet</option>}
                    {years.map((y) => (
                      <option key={y.id} value={y.id}>{y.label}{y.active ? ' (active)' : ''}</option>
                    ))}
                  </select>
                ) : (
                  <span className="chip-value">{years.find((y) => y.id === selectedYearId)?.label ?? '—'}</span>
                )}
              </div>
            </div>
          </div>

          <div style={{ position: 'relative' }}>
            <div className="topbar-user profile-trigger" onClick={() => setProfileOpen(!profileOpen)}>
              <div className="topbar-avatar">{initials}</div>
              <div>
                <div className="topbar-user-name">{user?.name}</div>
                <div className="topbar-user-role">{user?.role}</div>
              </div>
            </div>

            {profileOpen && (
              <div className="profile-dropdown">
                <h4>My Profile</h4>
                <div className="profile-row"><span>Name</span><span>{user?.name}</span></div>
                <div className="profile-row"><span>Role</span><span>{user?.role}</span></div>
                <div className="profile-row"><span>Email</span><span>{user?.email ?? '—'}</span></div>
                {user?.profileId && <div className="profile-row"><span>Profile ID</span><span>{user.profileId}</span></div>}
                <button className="btn-danger" style={{ width: '100%', marginTop: 14 }} onClick={() => { setProfileOpen(false); setConfirmingLogout(true); }}>
                  Sign out
                </button>
              </div>
            )}
          </div>
        </header>

        <div className="content">{children}</div>
      </div>

      {confirmingLogout && (
        <LogoutConfirmModal
          onConfirm={handleConfirmLogout}
          onCancel={() => setConfirmingLogout(false)}
        />
      )}
    </div>
  );
}
