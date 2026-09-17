import { useEffect, useState } from 'react';
import { getAllAcademicYears, type AcademicYearResponse } from '../api/academicYears';
import { getAllStudents } from '../api/students';
import { getAllTeachers } from '../api/teachers';
import { getAllClasses } from '../api/classes';
import { getPendingModificationRequests } from '../api/modificationRequests';
import { useMessage } from '../context/MessageContext';

export function AdminHomePage() {
  const { showError } = useMessage();
  const [loading, setLoading] = useState(true);
  const [activeYear, setActiveYear] = useState<AcademicYearResponse | null>(null);
  const [counts, setCounts] = useState({ students: 0, teachers: 0, classes: 0, pendingRequests: 0 });

  useEffect(() => {
    void load();
  }, []);

  async function load() {
    setLoading(true);
    try {
      const [years, students, teachers, classes, requests] = await Promise.all([
        getAllAcademicYears(), getAllStudents(), getAllTeachers(), getAllClasses(), getPendingModificationRequests()
      ]);
      setActiveYear(years.find((y) => y.active) ?? null);
      setCounts({
        students: students.length,
        teachers: teachers.length,
        classes: classes.length,
        pendingRequests: requests.length
      });
    } catch {
      showError('Could not load school data. Is the backend running?');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div>
      <div className="breadcrumb">
        Home <span style={{ margin: '0 6px' }}>›</span> <span className="current">School Overview</span>
      </div>

      {loading ? (
        <p className="empty-state">Loading…</p>
      ) : (
        <>
          <div className="stat-cards">
            <div className="stat-card">
              <div className="stat-label">Students</div>
              <div className="stat-value accent-blue">{counts.students}</div>
            </div>
            <div className="stat-card">
              <div className="stat-label">Teachers</div>
              <div className="stat-value accent-blue">{counts.teachers}</div>
            </div>
            <div className="stat-card">
              <div className="stat-label">Classes</div>
              <div className="stat-value accent-blue">{counts.classes}</div>
            </div>
            <div className="stat-card">
              <div className="stat-label">Pending Requests</div>
              <div className="stat-value accent-green">{counts.pendingRequests}</div>
            </div>
          </div>

          <div className="info-panel">
            <h3>Active Academic Year</h3>
            {activeYear ? (
              <div className="info-grid">
                <div className="info-item">
                  <span className="label">Label</span>
                  <span className="value">{activeYear.label}</span>
                </div>
                <div className="info-item">
                  <span className="label">Start Date</span>
                  <span className="value">{activeYear.startDate}</span>
                </div>
                <div className="info-item">
                  <span className="label">End Date</span>
                  <span className="value">{activeYear.endDate}</span>
                </div>
              </div>
            ) : (
              <p style={{ color: 'var(--gray-500)', margin: 0 }}>
                No academic year is active yet — create one and activate it.
              </p>
            )}
          </div>
        </>
      )}
    </div>
  );
}
