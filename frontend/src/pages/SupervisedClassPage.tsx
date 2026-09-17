import { useEffect, useState } from 'react';
import {
  classesApi, gradesApi, meetingsApi, notificationsApi, studentsApi,
  type Meeting, type SchoolClass, type Student, type SubjectClassRoster
} from '../api/system';
import { Icon } from '../components/Icon';
import { FormModal, type FormModalField } from '../components/FormModal';
import { useMessage } from '../context/MessageContext';

const STATUS_OPTIONS = ['ACTIVE', 'INACTIVE', 'GRADUATED', 'SUSPENDED', 'TRANSFERRED', 'WITHDRAWN'];

export function SupervisedClassPage() {
  const { showError } = useMessage();
  const [schoolClass, setClass] = useState<SchoolClass | null>(null);
  const [students, setStudents] = useState<Student[]>([]);
  const [meetings, setMeetings] = useState<Meeting[]>([]);
  const [finalRoster, setFinalRoster] = useState<SubjectClassRoster[]>([]);
  const [tab, setTab] = useState<'roster' | 'finals' | 'meetings'>('roster');
  const [unauthorized, setUnauthorized] = useState(false);

  const [notifyTarget, setNotifyTarget] = useState<Student | 'class' | null>(null);
  const [statusTarget, setStatusTarget] = useState<Student | null>(null);
  const [meetingOpen, setMeetingOpen] = useState(false);

  const load = async () => {
    try {
      const c = await classesApi.supervised();
      setClass(c);
      const [s, m, f] = await Promise.all([studentsApi.byClass(c.id), meetingsApi.byClass(c.id), gradesApi.classRoster(c.id)]);
      setStudents(s.slice().sort((a, b) => `${a.firstName} ${a.lastName}`.localeCompare(`${b.firstName} ${b.lastName}`)));
      setMeetings(m); setFinalRoster(f);
      setUnauthorized(false);
    } catch {
      setUnauthorized(true);
      showError('You are not assigned as supervisor of a class.');
    }
  };
  useEffect(() => { void load(); }, []);

  const notifyFields: FormModalField[] = [{ key: 'message', label: 'Notification message', type: 'textarea' }];
  const submitNotify = async (values: Record<string, string>) => {
    if (!schoolClass) return;
    if (notifyTarget === 'class') await notificationsApi.schoolClass(schoolClass.id, values.message);
    else if (notifyTarget) await notificationsApi.student(notifyTarget.id, values.message);
    setNotifyTarget(null);
  };

  const submitStatus = async (values: Record<string, string>) => {
    if (!statusTarget) return;
    await studentsApi.status(statusTarget.id, values.status);
    setStatusTarget(null);
    await load();
  };

  const meetingFields: FormModalField[] = [
    { key: 'date', label: 'Meeting date', type: 'date' },
    { key: 'topic', label: 'Topics discussed', type: 'textarea' }
  ];
  const submitMeeting = async (values: Record<string, string>) => {
    if (!schoolClass) return;
    await meetingsApi.create(schoolClass.id, values.date, values.topic);
    setMeetingOpen(false);
    await load();
  };

  return (
    <div>
      <div className="breadcrumb">Home › <span className="current">Supervised class</span></div>
      {unauthorized && <div className="info-panel"><p>You are not assigned as supervisor of a class.</p></div>}
      {schoolClass && (
        <>
          <div className="info-panel">
            <h3>{schoolClass.className}</h3>
            <p>Supervisor workspace · {students.length}/{schoolClass.maxStudents} students</p>
          </div>
          <div className="tab-buttons" style={{ marginBottom: 16 }}>
            <button className={tab === 'roster' ? 'btn-primary' : 'btn-outline'} onClick={() => setTab('roster')}>Roster</button>
            <button className={tab === 'finals' ? 'btn-primary' : 'btn-outline'} onClick={() => setTab('finals')}>Final grades</button>
            <button className={tab === 'meetings' ? 'btn-primary' : 'btn-outline'} onClick={() => setTab('meetings')}>Parent meetings</button>
          </div>

          {tab === 'roster' && (
            <div className="panel">
              <div className="panel-header"><div className="panel-icon"><Icon name="users" /></div><div><div className="panel-eyebrow">Roster</div><h3>Class students</h3></div></div>
              <div className="filter-bar"><button className="btn-primary" onClick={() => setNotifyTarget('class')}>Notify whole class</button></div>
              <table className="data-table">
                <thead><tr><th>Student</th><th>Personal ID</th><th>Status</th><th></th></tr></thead>
                <tbody>
                  {students.map((s) => (
                    <tr key={s.id}>
                      <td>{s.firstName} {s.lastName}</td>
                      <td>{s.personalId}</td>
                      <td>{s.status}</td>
                      <td>
                        <div className="row-actions">
                          <button className="btn-text" onClick={() => setNotifyTarget(s)}>Notify</button>
                          <button className="btn-text" onClick={() => setStatusTarget(s)}>Change status</button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}

          {tab === 'finals' && (
            <div>
              {finalRoster.length === 0 && <div className="panel"><div className="empty-state">No teaching assignments recorded for this class yet.</div></div>}
              {finalRoster.map((subject) => (
                <div className="panel" key={subject.teachingAssignmentId} style={{ marginBottom: 18 }}>
                  <div className="panel-header">
                    <div className="panel-icon"><Icon name="bar-chart" /></div>
                    <div><div className="panel-eyebrow">{subject.teacherName}</div><h3>{subject.subjectName}</h3></div>
                  </div>
                  <table className="data-table">
                    <thead><tr><th>Student</th><th>CCEG</th><th>CFE</th><th>Project</th><th>Final</th><th>Status</th></tr></thead>
                    <tbody>
                      {subject.roster.map((r) => (
                        <tr key={r.studentId}>
                          <td>{r.studentFirstName} {r.studentLastName}</td>
                          <td>{r.cceg ?? '—'}</td>
                          <td>{r.cfe ?? '—'}</td>
                          <td>{r.projectGrade ?? '—'}</td>
                          <td>{r.finalGrade ?? '—'}</td>
                          <td><span className={`status-pill ${r.status === 'APPROVED' ? 'active' : r.status === 'DRAFT' ? 'pending' : 'inactive'}`}>{r.status}</span></td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              ))}
            </div>
          )}

          {tab === 'meetings' && (
            <div className="panel">
              <div className="panel-header"><div className="panel-icon"><Icon name="calendar" /></div><div><div className="panel-eyebrow">Meetings</div><h3>Parent meetings</h3></div></div>
              <div className="filter-bar"><button className="btn-primary" onClick={() => setMeetingOpen(true)}>Record meeting</button></div>
              <table className="data-table">
                <thead><tr><th>Date</th><th>Topics</th><th></th></tr></thead>
                <tbody>
                  {meetings.map((m) => (
                    <tr key={m.id}>
                      <td>{m.meetingTime}</td>
                      <td>{m.Topic}</td>
                      <td><button className="btn-text danger" onClick={async () => { await meetingsApi.remove(m.id); await load(); }}>Delete</button></td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </>
      )}

      {notifyTarget && (
        <FormModal
          title={notifyTarget === 'class' ? 'Notify the whole class' : `Notify ${notifyTarget.firstName} ${notifyTarget.lastName}`}
          fields={notifyFields}
          submitLabel="Send"
          onSubmit={(v) => void submitNotify(v)}
          onCancel={() => setNotifyTarget(null)}
        />
      )}

      {statusTarget && (
        <FormModal
          title={`Change status — ${statusTarget.firstName} ${statusTarget.lastName}`}
          fields={[{ key: 'status', label: 'Status', type: 'select', defaultValue: statusTarget.status, options: STATUS_OPTIONS.map((s) => ({ value: s, label: s })) }]}
          submitLabel="Update"
          onSubmit={(v) => void submitStatus(v)}
          onCancel={() => setStatusTarget(null)}
        />
      )}

      {meetingOpen && (
        <FormModal
          title="Record a parent meeting"
          fields={meetingFields}
          submitLabel="Save"
          onSubmit={(v) => void submitMeeting(v)}
          onCancel={() => setMeetingOpen(false)}
        />
      )}
    </div>
  );
}
