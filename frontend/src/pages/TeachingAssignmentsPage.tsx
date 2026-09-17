import { useEffect, useState, type FormEvent } from 'react';
import { createTeachingAssignment, getAllTeachingAssignments, type TeachingAssignmentResponse } from '../api/teachingAssignments';
import { getAllClasses, type SchoolClassResponse } from '../api/classes';
import { getAllSubjects, type SubjectResponse } from '../api/subjects';
import { getAllTeachers, type TeacherResponse } from '../api/teachers';
import { useAcademicYear } from '../context/AcademicYearContext';
import { Icon } from '../components/Icon';
import { useMessage } from '../context/MessageContext';

export function TeachingAssignmentsPage() {
  const { selectedYearId, selectedYear } = useAcademicYear();
  const { showError } = useMessage();
  const [assignments, setAssignments] = useState<TeachingAssignmentResponse[]>([]);
  const [classes, setClasses] = useState<SchoolClassResponse[]>([]);
  const [subjects, setSubjects] = useState<SubjectResponse[]>([]);
  const [teachers, setTeachers] = useState<TeacherResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [formOpen, setFormOpen] = useState(false);
  const [classId, setClassId] = useState<number | null>(null);
  const [subjectId, setSubjectId] = useState<number | null>(null);
  const [teacherId, setTeacherId] = useState<number | null>(null);

  useEffect(() => {
    void load();
  }, [selectedYearId]);

  async function load() {
    setLoading(true);
    try {
      const [a, c, s, t] = await Promise.all([
        getAllTeachingAssignments(selectedYearId ?? undefined),
        getAllClasses(selectedYearId ?? undefined),
        getAllSubjects(), getAllTeachers()
      ]);
      setAssignments(a); setClasses(c); setSubjects(s); setTeachers(t);
    } catch {
      showError('Could not load teaching assignments. Is the backend running?');
    } finally {
      setLoading(false);
    }
  }

  async function handleCreate(event: FormEvent) {
    event.preventDefault();
    if (!classId || !subjectId || !teacherId) return;
    try {
      await createTeachingAssignment(classId, subjectId, teacherId);
      setFormOpen(false);
      await load();
    } catch {
      showError('Could not create the assignment - this subject may already be assigned to another teacher in this class.');
    }
  }

  return (
    <div>
      <div className="breadcrumb">
        Home <span style={{ margin: '0 6px' }}>›</span> <span className="current">Teaching Assignments</span>
      </div>
      <div className="panel">
        <div className="panel-header">
          <div className="panel-icon"><Icon name="link" /></div>
          <div>
            <div className="panel-eyebrow">Teaching Assignments — {selectedYear?.label ?? 'no year selected'}</div>
            <h3>Which Teacher Teaches What, Where</h3>
          </div>
        </div>

        <div className="filter-bar">
          <div style={{ display: 'flex', alignItems: 'flex-end' }}>
            <button className="btn-primary" onClick={() => setFormOpen(!formOpen)}>
              {formOpen ? 'Cancel' : 'Create Assignment'}
            </button>
          </div>
        </div>

        {formOpen && (
          <form onSubmit={handleCreate} style={{ padding: '0 22px 22px 22px' }}>
            <div className="filter-bar" style={{ padding: 0, border: 'none' }}>
              <div className="field">
                <label>Class</label>
                <select onChange={(e) => setClassId(Number(e.target.value))} defaultValue="">
                  <option value="" disabled>Select…</option>
                  {classes.map((c) => <option key={c.id} value={c.id}>{c.className}</option>)}
                </select>
              </div>
              <div className="field">
                <label>Subject</label>
                <select onChange={(e) => setSubjectId(Number(e.target.value))} defaultValue="">
                  <option value="" disabled>Select…</option>
                  {subjects.map((s) => <option key={s.id} value={s.id}>{s.subjectName}</option>)}
                </select>
              </div>
              <div className="field">
                <label>Teacher</label>
                <select onChange={(e) => setTeacherId(Number(e.target.value))} defaultValue="">
                  <option value="" disabled>Select…</option>
                  {teachers.map((t) => <option key={t.teacherId} value={t.teacherId}>{t.teacherName} {t.teacherSurname}</option>)}
                </select>
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
              <tr><th>#</th><th>Class</th><th>Subject</th><th>Teacher</th></tr>
            </thead>
            <tbody>
              {assignments.map((row, index) => (
                <tr key={row.id}>
                  <td>{index + 1}.</td>
                  <td>{row.schoolClassName}</td>
                  <td>{row.subjectName}</td>
                  <td>{row.teacherName}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
