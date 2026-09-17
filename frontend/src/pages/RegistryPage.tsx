import { useEffect, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { isAxiosError } from 'axios';
import {
  assignmentsApi, studentsApi, topicsApi,
  type Assignment, type LessonRecord, type Student, type Topic
} from '../api/system';
import { Icon } from '../components/Icon';
import { GradeSelectorModal } from '../components/GradeSelectorModal';
import { useMessage } from '../context/MessageContext';

const EDIT_WINDOW_MS = 24 * 60 * 60 * 1000;
const withinWindow = (createdAt: string) => Date.now() - new Date(createdAt).getTime() < EDIT_WINDOW_MS;

export function RegistryPage() {
  const { showError } = useMessage();
  const [searchParams, setSearchParams] = useSearchParams();
  const [assignments, setAssignments] = useState<Assignment[]>([]);
  const [assignmentId, setAssignmentId] = useState(searchParams.get('assignmentId') ?? '');
  const [students, setStudents] = useState<Student[]>([]);
  const [topics, setTopics] = useState<Topic[]>([]);
  const [topicId, setTopicId] = useState(searchParams.get('topicId') ?? '');
  const [records, setRecords] = useState<LessonRecord[]>([]);

  const [gradingStudent, setGradingStudent] = useState<Student | null>(null);

  useEffect(() => {
    void assignmentsApi.mine().then((a) => {
      setAssignments(a);
      if (!assignmentId && a[0]) setAssignmentId(String(a[0].id));
    });
  }, []);

  const assignment = assignments.find((a) => a.id === Number(assignmentId));

  const loadAssignment = async () => {
    if (!assignment) return;
    const [s, t] = await Promise.all([
      studentsApi.byClass(assignment.schoolClassId), topicsApi.all(assignment.id)
    ]);
    setStudents(s.slice().sort((a, b) => `${a.firstName} ${a.lastName}`.localeCompare(`${b.firstName} ${b.lastName}`)));
    setTopics(t.slice().sort((a, b) => b.date.localeCompare(a.date)));
    if (t[0] && !topicId) setTopicId(String(t[0].id));
  };
  useEffect(() => { void loadAssignment(); }, [assignmentId, assignments.length]);
  useEffect(() => { if (topicId) void topicsApi.records(Number(topicId)).then(setRecords); else setRecords([]); }, [topicId]);
  useEffect(() => { setSearchParams(assignmentId ? { assignmentId, ...(topicId ? { topicId } : {}) } : {}, { replace: true }); }, [assignmentId, topicId]);

  const recordFor = (studentId: number) => records.find((r) => r.studentId === studentId);
  const refreshRecords = () => topicId ? topicsApi.records(Number(topicId)).then(setRecords) : Promise.resolve();
  const errorMessage = (e: unknown, fallback: string) =>
    isAxiosError<{ message?: string }>(e) && e.response?.data?.message ? e.response.data.message : fallback;

  const applyGrade = async (grade: number) => {
    if (!gradingStudent || !topicId) return;
    try {
      const existing = recordFor(gradingStudent.id);
      existing ? await topicsApi.updateGrade(existing.id, grade) : await topicsApi.grade(Number(topicId), gradingStudent.id, grade);
      setGradingStudent(null);
      await refreshRecords();
    } catch (e) {
      showError(errorMessage(e, 'Grade could not be saved; the 24-hour window may have expired.'));
    }
  };

  const removeRecord = async () => {
    if (!gradingStudent) return;
    const existing = recordFor(gradingStudent.id);
    if (!existing) return;
    try {
      await topicsApi.removeRecord(existing.id);
      setGradingStudent(null);
      await refreshRecords();
    } catch (e) {
      showError(errorMessage(e, 'This record can no longer be removed; the 24-hour window has passed.'));
    }
  };

  const markAbsent = async (s: Student) => {
    if (!topicId) return;
    try {
      const existing = recordFor(s.id);
      existing ? await topicsApi.markAbsent(existing.id) : await topicsApi.absent(Number(topicId), s.id);
      await refreshRecords();
    } catch (e) {
      showError(errorMessage(e, 'Absence could not be saved; the 24-hour window may have expired.'));
    }
  };

  return (
    <div>
      <div className="breadcrumb">Home › <span className="current">Electronic register</span></div>
      <div className="filter-bar panel">
        <div className="field" style={{ marginBottom: 0 }}>
          <label>Class and subject</label>
          <select value={assignmentId} onChange={(e) => { setAssignmentId(e.target.value); setTopicId(''); }}>
            {assignments.map((a) => <option key={a.id} value={a.id}>{a.schoolClassName} · {a.subjectName}</option>)}
          </select>
        </div>
        <div className="field" style={{ marginBottom: 0 }}>
          <label>Topic</label>
          <select value={topicId} onChange={(e) => setTopicId(e.target.value)}>
            <option value="">Select topic</option>
            {topics.map((t) => <option key={t.id} value={t.id}>{t.date} · {t.topicName}</option>)}
          </select>
        </div>
        <div style={{ display: 'flex', alignItems: 'flex-end' }}>
          <Link to="/topics" className="btn-outline">Manage topics</Link>
        </div>
      </div>

      <div className="panel">
        <div className="panel-header"><div className="panel-icon"><Icon name="clipboard" /></div><div><div className="panel-eyebrow">Selected lesson</div><h3>Student register</h3></div></div>
        <table className="data-table">
          <thead><tr><th>Student</th><th>Grade</th><th>Attendance</th><th></th></tr></thead>
          <tbody>
            {students.map((s) => {
              const r = recordFor(s.id);
              const editable = !r || withinWindow(r.createdAt);
              const active = s.status === 'ACTIVE';
              return (
                <tr key={s.id}>
                  <td>{s.firstName} {s.lastName}{!active && <span className="hint" style={{ marginLeft: 6 }}>({s.status})</span>}</td>
                  <td>{r?.grade ?? '—'}</td>
                  <td>{r?.absent ? (r.justified ? 'Justified absence' : 'Absent') : 'Present'}</td>
                  <td>
                    <div className="row-actions">
                      <button className="btn-text" onClick={() => setGradingStudent(s)} disabled={!topicId || !active}>Grade</button>
                      {(!r || !r.absent) && (
                        <button className="btn-text danger" onClick={() => void markAbsent(s)} disabled={!topicId || !active || (!!r && !editable)}>Mark absent</button>
                      )}
                      {r && !editable && <span className="hint">Window closed — <Link to="/submit-modification">request change</Link></span>}
                      {!active && <span className="hint">Only active students can be graded</span>}
                    </div>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
        {!topicId && <div className="empty-state">Select a topic above (or <Link to="/topics">manage topics</Link>) to grade this lesson.</div>}
      </div>

      {gradingStudent && (
        <GradeSelectorModal
          studentName={`${gradingStudent.firstName} ${gradingStudent.lastName}`}
          currentGrade={recordFor(gradingStudent.id)?.grade}
          removeLabel={recordFor(gradingStudent.id)?.absent ? 'Remove absence' : 'Remove grade'}
          onSelect={(g) => void applyGrade(g)}
          onRemove={recordFor(gradingStudent.id) && withinWindow(recordFor(gradingStudent.id)!.createdAt) ? () => void removeRecord() : undefined}
          onCancel={() => setGradingStudent(null)}
        />
      )}
    </div>
  );
}
