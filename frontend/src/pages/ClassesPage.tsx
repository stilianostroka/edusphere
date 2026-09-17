import { useEffect, useState, type FormEvent } from 'react';
import React from 'react';
import { assignSupervisor, createClass, getAllClasses, type SchoolClassResponse } from '../api/classes';
import { getAllTeachers, type TeacherResponse } from '../api/teachers';
import { useAcademicYear } from '../context/AcademicYearContext';
import { Icon } from '../components/Icon';
import { useMessage } from '../context/MessageContext';

export function ClassesPage() {
  const { selectedYearId, selectedYear } = useAcademicYear();
  const { showError } = useMessage();
  const [classes, setClasses] = useState<SchoolClassResponse[]>([]);
  const [teachers, setTeachers] = useState<TeacherResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [formOpen, setFormOpen] = useState(false);
  const [assigningClassId, setAssigningClassId] = useState<number | null>(null);
  const [className, setClassName] = useState('');
  const [classYear, setClassYear] = useState('');
  const [maxStudents, setMaxStudents] = useState('30');
  const [selectedTeacherId, setSelectedTeacherId] = useState<number | null>(null);

  useEffect(() => {
    void load();
    // Re-fetch whenever the selected academic year changes - this is the
    // actual point of the year selector: switching years shows that year's
    // classes, not just a label change.
  }, [selectedYearId]);

  async function load() {
    setLoading(true);
    try {
      const [classesData, teachersData] = await Promise.all([
        getAllClasses(selectedYearId ?? undefined), getAllTeachers()
      ]);
      setClasses(classesData);
      setTeachers(teachersData);
    } catch {
      showError('Could not load classes. Is the backend running?');
    } finally {
      setLoading(false);
    }
  }

  async function handleCreate(event: FormEvent) {
    event.preventDefault();
    if (!selectedYearId) return;
    try {
      await createClass(selectedYearId, className, classYear, Number(maxStudents));
      setFormOpen(false);
      setClassName(''); setClassYear('');
      await load();
    } catch {
      showError('Could not create the class - check the name is unique for this academic year.');
    }
  }

  async function handleAssignSupervisor(classId: number) {
    if (!selectedTeacherId) return;
    try {
      await assignSupervisor(classId, selectedTeacherId);
      setAssigningClassId(null);
      setSelectedTeacherId(null);
      await load();
    } catch {
      showError('Could not assign supervisor - this teacher may already supervise a class this year.');
    }
  }

  return (
    <div>
      <div className="breadcrumb">
        Home <span style={{ margin: '0 6px' }}>›</span> <span className="current">Classes</span>
      </div>
      <div className="panel">
        <div className="panel-header">
          <div className="panel-icon"><Icon name="book" /></div>
          <div>
            <div className="panel-eyebrow">Classes — {selectedYear?.label ?? 'no year selected'}</div>
            <h3>All Classes</h3>
          </div>
        </div>

        <div className="filter-bar">
          <div style={{ display: 'flex', alignItems: 'flex-end' }}>
            <button className="btn-primary" onClick={() => setFormOpen(!formOpen)}>
              {formOpen ? 'Cancel' : 'Create Class'}
            </button>
          </div>
        </div>

        {formOpen && (
          <form onSubmit={handleCreate} style={{ padding: '0 22px 22px 22px' }}>
            <div className="filter-bar" style={{ padding: 0, border: 'none' }}>
              <div className="field">
                <label>Academic Year</label>
                <input value={selectedYear?.label ?? ''} disabled />
                <span style={{ fontSize: 11, color: 'var(--gray-500)' }}>
                  Set via the selector in the top bar
                </span>
              </div>
              <div className="field">
                <label>Class Name</label>
                <input value={className} onChange={(e) => setClassName(e.target.value)} placeholder="10-A" required />
              </div>
              <div className="field">
                <label>Class Year</label>
                <input value={classYear} onChange={(e) => setClassYear(e.target.value)} placeholder="Year 1" required />
              </div>
              <div className="field">
                <label>Max Students</label>
                <input type="number" value={maxStudents} onChange={(e) => setMaxStudents(e.target.value)} required />
              </div>
            </div>
            <button type="submit" className="btn-primary">Save</button>
          </form>
        )}

        {loading ? (
          <p className="empty-state">Loading…</p>
        ) : (
          <table className="data-table">
            <thead>
              <tr>
                <th>#</th>
                <th>Class</th>
                <th>Year</th>
                <th>Academic Year</th>
                <th>Supervisor</th>
                <th>Max Students</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {classes.map((c, index) => (
                <React.Fragment key={c.id}>
                  <tr>
                    <td>{index + 1}.</td>
                    <td>{c.className}</td>
                    <td>{c.classYear}</td>
                    <td>{c.academicYearLabel}</td>
                    <td>
                      {c.supervisorName ?? <span className="status-pill pending">Unassigned</span>}
                    </td>
                    <td>{c.maxStudents}</td>
                    <td>
                      <button className="btn-text" onClick={() => setAssigningClassId(assigningClassId === c.id ? null : c.id)}>
                        {c.supervisorName ? 'Change supervisor' : 'Assign supervisor'}
                      </button>
                    </td>
                  </tr>
                  {assigningClassId === c.id && (
                    <tr>
                      <td colSpan={7} style={{ background: 'var(--card-tint)', padding: 16 }}>
                        <div style={{ display: 'flex', gap: 10, alignItems: 'center' }}>
                          <select onChange={(e) => setSelectedTeacherId(Number(e.target.value))} defaultValue="">
                            <option value="" disabled>Select a teacher…</option>
                            {teachers.map((t) => (
                              <option key={t.teacherId} value={t.teacherId}>{t.teacherName} {t.teacherSurname}</option>
                            ))}
                          </select>
                          <button className="btn-primary" onClick={() => handleAssignSupervisor(c.id)}>Confirm</button>
                        </div>
                      </td>
                    </tr>
                  )}
                </React.Fragment>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
