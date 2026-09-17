import { useEffect, useState } from 'react';
import { isAxiosError } from 'axios';
import {
  assignmentsApi, gradesApi, modificationsApi, studentsApi, topicsApi,
  type Assignment, type LessonRecord, type Modification, type SemesterExamGrade, type Student, type Topic
} from '../api/system';
import { useAuth } from '../context/AuthContext';
import { Icon } from '../components/Icon';
import { useMessage } from '../context/MessageContext';

type RequestType = 'topic' | 'grade' | 'absence' | 'exam';

export function SubmitModificationRequestPage() {
  const { user } = useAuth();
  const { showSuccess, showError } = useMessage();
  const [type, setType] = useState<RequestType>('topic');
  const [assignments, setAssignments] = useState<Assignment[]>([]);
  const [assignmentId, setAssignmentId] = useState('');
  const [topics, setTopics] = useState<Topic[]>([]);
  const [topicId, setTopicId] = useState('');
  const [students, setStudents] = useState<Student[]>([]);
  const [studentId, setStudentId] = useState('');
  const [records, setRecords] = useState<LessonRecord[]>([]);
  const [examGrades, setExamGrades] = useState<SemesterExamGrade[]>([]);
  const [examGradeId, setExamGradeId] = useState('');
  const [value, setValue] = useState('');
  const [description, setDescription] = useState('');
  const [explanation, setExplanation] = useState('');
  const [history, setHistory] = useState<Modification[]>([]);

  const assignment = assignments.find((a) => a.id === Number(assignmentId));
  const needsTopic = type === 'topic' || type === 'grade' || type === 'absence';
  const needsStudent = type === 'grade' || type === 'absence' || type === 'exam';
  const currentRecord = records.find((r) => r.studentId === Number(studentId));

  const load = async () => {
    const a = await assignmentsApi.mine();
    setAssignments(a);
    if (user?.profileId) setHistory(await modificationsApi.mine(user.profileId));
  };
  useEffect(() => { void load(); }, [user?.profileId]);

  useEffect(() => {
    setTopicId(''); setStudentId(''); setRecords([]); setExamGrades([]); setExamGradeId('');
    if (!assignmentId) { setTopics([]); setStudents([]); return; }
    if (needsTopic) void topicsApi.all(Number(assignmentId)).then(setTopics);
    if (needsStudent && assignment) void studentsApi.byClass(assignment.schoolClassId).then((s) =>
      setStudents(s.slice().sort((a, b) => `${a.firstName} ${a.lastName}`.localeCompare(`${b.firstName} ${b.lastName}`)))
    );
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [assignmentId, type]);

  useEffect(() => {
    if (topicId && (type === 'grade' || type === 'absence')) void topicsApi.records(Number(topicId)).then(setRecords);
  }, [topicId, type]);

  useEffect(() => {
    if (type === 'exam' && studentId && assignmentId) {
      void gradesApi.examsFor(Number(studentId), Number(assignmentId)).then((g) => { setExamGrades(g); setExamGradeId(''); });
    }
  }, [type, studentId, assignmentId]);

  const canSubmit =
    type === 'topic' ? !!topicId && !!value :
    type === 'grade' ? !!currentRecord && !!value :
    type === 'absence' ? !!currentRecord :
    !!examGradeId && !!value;

  const submit = async () => {
    try {
      if (type === 'topic') await modificationsApi.topic({ topicId: Number(topicId), proposedTopicName: value, proposedDescription: description, explanation });
      if (type === 'grade' && currentRecord) await modificationsApi.grade({ lessonRecordId: currentRecord.id, proposedGrade: Number(value), explanation });
      if (type === 'absence' && currentRecord) await modificationsApi.absence({ lessonRecordId: currentRecord.id, explanation });
      if (type === 'exam') await modificationsApi.exam({ semesterExamGradeId: Number(examGradeId), proposedExamGrade: Number(value), explanation });
      showSuccess('Request submitted.');
      setTopicId(''); setStudentId(''); setExamGradeId(''); setValue(''); setDescription(''); setExplanation('');
      await load();
    } catch (e) {
      showError(isAxiosError<{ message?: string }>(e) && e.response?.data?.message
        ? e.response.data.message
        : 'Request could not be submitted.');
    }
  };

  return (
    <div>
      <div className="breadcrumb">Home › <span className="current">Modification requests</span></div>
      <div className="split-view">
        <div className="panel" style={{ padding: 22 }}>
          <h3>Submit a change request</h3>

          <div className="field">
            <label>Type</label>
            <select value={type} onChange={(e) => setType(e.target.value as RequestType)}>
              <option value="topic">Lesson topic</option>
              <option value="grade">Lesson grade</option>
              <option value="absence">Absence</option>
              <option value="exam">Exam grade</option>
            </select>
          </div>

          <div className="field">
            <label>Teaching assignment</label>
            <select value={assignmentId} onChange={(e) => setAssignmentId(e.target.value)}>
              <option value="">Select</option>
              {assignments.map((a) => <option key={a.id} value={a.id}>{a.subjectName} · {a.schoolClassName}</option>)}
            </select>
          </div>

          {needsTopic && assignmentId && (
            <div className="field">
              <label>Topic</label>
              <select value={topicId} onChange={(e) => setTopicId(e.target.value)}>
                <option value="">Select topic</option>
                {topics.map((t) => <option key={t.id} value={t.id}>{t.date} · {t.topicName}</option>)}
              </select>
            </div>
          )}

          {needsStudent && assignmentId && (type === 'exam' || topicId) && (
            <div className="field">
              <label>Student</label>
              <select value={studentId} onChange={(e) => setStudentId(e.target.value)}>
                <option value="">Select student</option>
                {students.map((s) => <option key={s.id} value={s.id}>{s.firstName} {s.lastName}</option>)}
              </select>
            </div>
          )}

          {type === 'grade' && studentId && (
            <p className="hint">{currentRecord ? `Current grade: ${currentRecord.grade ?? '—'}` : 'No lesson record exists for this student on this topic yet.'}</p>
          )}
          {type === 'absence' && studentId && (
            <p className="hint">{currentRecord ? (currentRecord.absent ? 'Currently marked absent.' : 'This student is not currently marked absent on this topic.') : 'No lesson record exists for this student on this topic yet.'}</p>
          )}

          {type === 'exam' && studentId && (
            <div className="field">
              <label>Exam grade record</label>
              <select value={examGradeId} onChange={(e) => setExamGradeId(e.target.value)}>
                <option value="">Select</option>
                {examGrades.map((g) => <option key={g.id} value={g.id}>Period {g.gradingPeriodSequenceNumber} — current grade {g.examGrade}</option>)}
              </select>
              {examGrades.length === 0 && <p className="hint">No exam grades recorded yet for this student and subject.</p>}
            </div>
          )}

          {(type === 'topic' || type === 'grade' || type === 'exam') && (
            <div className="field">
              <label>{type === 'topic' ? 'Proposed topic name' : 'Proposed grade'}</label>
              <input type={type === 'topic' ? 'text' : 'number'} value={value} onChange={(e) => setValue(e.target.value)} />
            </div>
          )}

          {type === 'topic' && (
            <div className="field"><label>Proposed description</label><textarea value={description} onChange={(e) => setDescription(e.target.value)} /></div>
          )}

          <div className="field"><label>Reason</label><textarea value={explanation} onChange={(e) => setExplanation(e.target.value)} /></div>

          <button className="btn-primary" disabled={!canSubmit || !explanation} onClick={() => void submit()}>Submit request</button>
        </div>

        <div className="panel">
          <div className="panel-header"><div className="panel-icon"><Icon name="mail" /></div><div><div className="panel-eyebrow">My requests</div><h3>History</h3></div></div>
          <table className="data-table">
            <thead><tr><th>Target</th><th>Requested value</th><th>Status</th></tr></thead>
            <tbody>
              {history.map((r) => (
                <tr key={r.id}>
                  <td>{r.topicId ? `Topic #${r.topicId}` : r.lessonRecordId ? `Record #${r.lessonRecordId}` : `Exam #${r.semesterExamGradeId}`}</td>
                  <td>{r.proposedTopicName ?? r.proposedGrade ?? r.proposedExamGrade ?? 'Absence change'}</td>
                  <td><span className={`status-pill ${r.status === 'APPROVED' ? 'active' : r.status === 'REJECTED' ? 'inactive' : 'pending'}`}>{r.status}</span></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
