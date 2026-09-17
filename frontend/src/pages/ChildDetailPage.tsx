import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { assignmentsApi, diplomasApi, fileUrl, gradesApi, notificationsApi, studentsApi, timetableApi,
  type Assignment, type Diploma, type GradeRoster, type LessonRecord, type Notification, type Student, type TimetableSlot } from '../api/system';
import { PeriodResultsTable } from '../components/PeriodResultsTable';
import { Icon } from '../components/Icon';

const TABS = [
  { key: 'overview', label: 'Overview', icon: 'file' },
  { key: 'grades', label: 'Grades', icon: 'bar-chart' },
  { key: 'absences', label: 'Absences', icon: 'calendar-x' },
  { key: 'timetable', label: 'Timetable', icon: 'calendar-grid' },
  { key: 'notifications', label: 'Notifications', icon: 'bell' },
  { key: 'diplomas', label: 'Diplomas', icon: 'award' }
] as const;

export function ChildDetailPage() {
  const { id } = useParams();
  const studentId = Number(id);
  const [child, setChild] = useState<Student | null>(null);
  const [grades, setGrades] = useState<Array<{ assignment: Assignment; row: GradeRoster }>>([]);
  const [lessonGrades, setLessonGrades] = useState<LessonRecord[]>([]);
  const [absences, setAbsences] = useState<LessonRecord[]>([]);
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [diplomas, setDiplomas] = useState<Diploma[]>([]);
  const [slots, setSlots] = useState<TimetableSlot[]>([]);
  const [tab, setTab] = useState<typeof TABS[number]['key']>('overview');

  useEffect(() => { void (async () => {
    const [student, allAssignments, absenceRows, lessonGradeRows, inbox, diplomaRows] = await Promise.all([
      studentsApi.one(studentId), assignmentsApi.all(), studentsApi.absences(studentId), studentsApi.grades(studentId),
      notificationsApi.inbox(), diplomasApi.student(studentId)
    ]);
    setChild(student);
    const own = allAssignments.filter(a => a.schoolClassId === student.schoolClassId);
    setAbsences(absenceRows);
    setLessonGrades(lessonGradeRows);
    setNotifications(inbox.filter(n => !n.targetStudentId || n.targetStudentId === studentId));
    setDiplomas(diplomaRows);
    const gradeRows = await Promise.all(own.map(async assignment => ({
      assignment, row: (await gradesApi.roster(assignment.id)).find(row => row.studentId === studentId)
    })));
    setGrades(gradeRows.filter((item): item is {assignment: Assignment; row: GradeRoster} => Boolean(item.row)));
    setSlots((await Promise.all(own.map(a => timetableApi.assignment(a.id)))).flat());
  })(); }, [studentId]);

  if (!child) return <div className="empty-state">Loading student record…</div>;

  const initials = `${child.firstName[0] ?? ''}${child.lastName[0] ?? ''}`.toUpperCase();

  return (
    <div>
      <div className="page-header">
        <div className="child-header">
          <div className="child-avatar large">{initials}</div>
          <div>
            <p className="eyebrow">Student record</p>
            <h1>{child.firstName} {child.lastName}</h1>
            <p className="page-subtitle">
              {child.className ?? 'Not enrolled'} · <span className={`status-pill ${child.status === 'ACTIVE' ? 'active' : 'pending'}`}>{child.status}</span>
            </p>
          </div>
        </div>
      </div>

      <div className="tab-segmented">
        {TABS.map(({ key, label, icon }) => (
          <button key={key} className={tab === key ? 'active' : ''} onClick={() => setTab(key)}>
            <Icon name={icon} size={15} /> {label}
          </button>
        ))}
      </div>

      {tab === 'overview' && (
        <div className="panel" style={{ padding: 24 }}>
          <div className="details-grid">
            <div><span>Personal ID</span><strong>{child.personalId}</strong></div>
            <div><span>Date of birth</span><strong>{child.dateOfBirth}</strong></div>
            <div><span>Gender</span><strong>{child.gender}</strong></div>
            <div><span>Address</span><strong>{child.address ?? '—'}</strong></div>
            <div><span>Enrolled</span><strong>{child.enrollmentDate}</strong></div>
          </div>
        </div>
      )}

      {tab === 'grades' && (
        <>
          <div className="panel" style={{ marginBottom: 18 }}>
            <div className="panel-header"><div className="panel-icon"><Icon name="bar-chart" /></div><div><div className="panel-eyebrow">By subject</div><h3>Final grades</h3></div></div>
            <table className="data-table">
              <thead><tr><th>Subject</th><th>Period results</th><th>CCEG</th><th>CFE</th><th>Project</th><th>Final</th><th>Status</th></tr></thead>
              <tbody>
                {grades.map(({ assignment, row }) => (
                  <tr key={assignment.id}>
                    <td>{assignment.subjectName}</td>
                    <td><PeriodResultsTable periods={row.periods} /></td>
                    <td>{row.cceg ?? '—'}</td>
                    <td>{row.cfe ?? '—'}</td>
                    <td>{row.projectGrade ?? '—'}</td>
                    <td><strong>{row.finalGrade ?? '—'}</strong></td>
                    <td>{row.status}</td>
                  </tr>
                ))}
              </tbody>
            </table>
            {!grades.length && <div className="empty-state">No final grades yet.</div>}
          </div>
          <div className="panel">
            <div className="panel-header"><div className="panel-icon"><Icon name="clipboard" /></div><div><div className="panel-eyebrow">Per lesson</div><h3>Topic grades</h3></div></div>
            <table className="data-table">
              <thead><tr><th>Date</th><th>Subject</th><th>Topic</th><th>Grade</th></tr></thead>
              <tbody>
                {lessonGrades.slice().sort((a, b) => a.date.localeCompare(b.date)).map((row) => (
                  <tr key={row.id}><td>{row.date}</td><td>{row.subjectName}</td><td>{row.topicName}</td><td><strong>{row.grade}</strong></td></tr>
                ))}
              </tbody>
            </table>
            {!lessonGrades.length && <div className="empty-state">No lesson grades recorded yet.</div>}
          </div>
        </>
      )}

      {tab === 'absences' && (
        <div className="panel">
          <div className="panel-header"><div className="panel-icon"><Icon name="calendar-x" /></div><div><div className="panel-eyebrow">All subjects</div><h3>Absence history</h3></div></div>
          <table className="data-table">
            <thead><tr><th>Date</th><th>Subject</th><th>Topic</th><th>Status</th><th>Justification</th></tr></thead>
            <tbody>
              {absences.map((row) => (
                <tr key={row.id}>
                  <td>{row.date}</td><td>{row.subjectName}</td><td>{row.topicName}</td>
                  <td><span className={`status-pill ${row.justified ? 'active' : 'inactive'}`}>{row.justified ? 'Justified' : 'Unjustified'}</span></td>
                  <td>{row.justificationNote ?? '—'}</td>
                </tr>
              ))}
            </tbody>
          </table>
          {!absences.length && <div className="empty-state">No absences recorded.</div>}
        </div>
      )}

      {tab === 'timetable' && (
        <div className="panel">
          <div className="panel-header"><div className="panel-icon"><Icon name="calendar-grid" /></div><div><div className="panel-eyebrow">Weekly schedule</div><h3>Timetable</h3></div></div>
          <table className="data-table">
            <thead><tr><th>Day</th><th>Time</th><th>Subject</th><th>Teacher</th></tr></thead>
            <tbody>
              {slots.map((slot) => (
                <tr key={slot.id}><td>{slot.dayOfWeek}</td><td>{slot.startTime}–{slot.endTime}</td><td>{slot.subjectName}</td><td>{slot.teacherName}</td></tr>
              ))}
            </tbody>
          </table>
          {!slots.length && <div className="empty-state">No timetable slots yet.</div>}
        </div>
      )}

      {tab === 'notifications' && (
        <div className="panel">
          <div className="panel-header"><div className="panel-icon"><Icon name="bell" /></div><div><div className="panel-eyebrow">From teachers</div><h3>Notifications</h3></div></div>
          {notifications.map((row) => (
            <div className="forum-post" key={row.id}>
              <strong>{row.sentByTeacherName}</strong>
              <div className="muted">{new Date(row.sentAt).toLocaleString()}</div>
              <p>{row.message}</p>
            </div>
          ))}
          {!notifications.length && <div className="empty-state">No notifications yet.</div>}
        </div>
      )}

      {tab === 'diplomas' && (
        <div className="panel">
          <div className="panel-header"><div className="panel-icon"><Icon name="award" /></div><div><div className="panel-eyebrow">Completion documents</div><h3>Diplomas</h3></div></div>
          {diplomas.map((row) => (
            <div className="list-row" key={row.id}>
              <span>Diploma · {row.academicYearLabel}</span>
              <a className="btn-gold" href={fileUrl(row.fileUrl)} target="_blank">Download PDF</a>
            </div>
          ))}
          {!diplomas.length && <div className="empty-state">No diploma generated yet.</div>}
        </div>
      )}
    </div>
  );
}
