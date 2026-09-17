import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { parentsApi, type Student } from '../api/system';
import { useAuth } from '../context/AuthContext';
import { Icon } from '../components/Icon';
import { useMessage } from '../context/MessageContext';

export function ParentHomePage() {
  const { user } = useAuth();
  const { showError } = useMessage();
  const [children, setChildren] = useState<Student[]>([]);
  const [failed, setFailed] = useState(false);

  useEffect(() => {
    void parentsApi.myChildren().then(setChildren).catch(() => {
      setFailed(true);
      showError('Could not load linked children. Ask the administrator to link the student to this parent account.');
    });
  }, []);

  const initials = (c: Student) => `${c.firstName[0] ?? ''}${c.lastName[0] ?? ''}`.toUpperCase();

  return (
    <div>
      <div className="page-header">
        <p className="eyebrow">Parent Portal</p>
        <h1>Welcome, {user?.name}</h1>
        <p className="page-subtitle">Grades, attendance, schedules and school communication — all in one place.</p>
      </div>

      <div className="card-grid">
        {children.map((c) => (
          <Link to={`/children/${c.id}`} className="child-card" key={c.id}>
            <div className="child-card-top">
              <div className="child-avatar">{initials(c)}</div>
              <span className={`status-pill ${c.status === 'ACTIVE' ? 'active' : 'pending'}`}>{c.status}</span>
            </div>
            <h3>{c.firstName} {c.lastName}</h3>
            <p className="muted">{c.className ?? 'Not enrolled'}</p>
            <span className="child-card-cta">Open student record <Icon name="chevron" size={14} /></span>
          </Link>
        ))}
      </div>

      {!children.length && !failed && <div className="empty-state">No children linked to this account.</div>}
    </div>
  );
}
