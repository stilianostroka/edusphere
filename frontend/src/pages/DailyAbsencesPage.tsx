import { useEffect, useMemo, useState } from 'react';
import { classesApi, lessonRecordsApi, studentsApi, topicsApi, type ClassAbsenceEntry, type Student } from '../api/system';
import { Icon } from '../components/Icon';
import { FormModal } from '../components/FormModal';
import { useMessage } from '../context/MessageContext';
import { useAcademicYear } from '../context/AcademicYearContext';

const currentMonth = new Date().toISOString().slice(0, 7);

export function DailyAbsencesPage() {
  const { showError } = useMessage();
  const { selectedYearId } = useAcademicYear();
  const [month, setMonth] = useState(currentMonth);
  const [className, setClassName] = useState('');
  const [unauthorized, setUnauthorized] = useState(false);
  const [students, setStudents] = useState<Student[]>([]);
  const [absences, setAbsences] = useState<ClassAbsenceEntry[]>([]);
  const [dayDetail, setDayDetail] = useState<{ student: Student; day: number; entries: ClassAbsenceEntry[] } | null>(null);
  const [justifying, setJustifying] = useState<ClassAbsenceEntry | null>(null);

  const load = async () => {
    try {
      const c = await classesApi.supervised(selectedYearId);
      setClassName(c.className);
      const [s, a] = await Promise.all([studentsApi.byClass(c.id), lessonRecordsApi.classAbsences(c.id)]);
      setStudents(s.slice().sort((x, y) => `${x.firstName} ${x.lastName}`.localeCompare(`${y.firstName} ${y.lastName}`)));
      setAbsences(a);
      setUnauthorized(false);
    } catch {
      setStudents([]); setAbsences([]);
      setUnauthorized(true);
      showError('You do not supervise a class.');
    }
  };
  useEffect(() => { void load(); }, [selectedYearId]);

  const [year, monthNum] = month.split('-').map(Number);
  const daysInMonth = new Date(year, monthNum, 0).getDate();
  const days = Array.from({ length: daysInMonth }, (_, i) => i + 1);

  const monthAbsences = useMemo(() => absences.filter((a) => a.date.startsWith(month)), [absences, month]);

  const entriesFor = (studentId: number, day: number) => {
    const dateStr = `${month}-${String(day).padStart(2, '0')}`;
    return monthAbsences.filter((a) => a.studentId === studentId && a.date === dateStr);
  };

  const totalsFor = (studentId: number) => {
    const rows = monthAbsences.filter((a) => a.studentId === studentId);
    const justified = rows.filter((r) => r.justified).length;
    return { justified, unjustified: rows.length - justified, total: rows.length };
  };

  const submitJustify = async (values: Record<string, string>) => {
    if (!justifying) return;
    await topicsApi.justify(justifying.id, values.note);
    setJustifying(null);
    await load();
  };

  return (
    <div>
      <div className="breadcrumb">Home › <span className="current">Daily absences</span></div>
      <div className="panel">
        <div className="panel-header">
          <div className="panel-icon"><Icon name="calendar-x" /></div>
          <div><div className="panel-eyebrow">Supervisor view · {className}</div><h3>Monthly absence register</h3></div>
        </div>
        <div className="filter-bar">
          <div className="field" style={{ marginBottom: 0 }}>
            <label>Month</label>
            <input type="month" value={month} onChange={(e) => setMonth(e.target.value)} />
          </div>
        </div>

        {unauthorized && <div className="empty-state">You do not supervise a class.</div>}

        {!unauthorized && (
          <div className="absence-grid-wrap">
            <table className="absence-grid">
              <thead>
                <tr>
                  <th className="student-col">Student</th>
                  {days.map((d) => <th key={d}>{d}</th>)}
                  <th className="summary-col justified">J</th>
                  <th className="summary-col unjustified">A</th>
                  <th className="total-col">Total</th>
                </tr>
              </thead>
              <tbody>
                {students.map((s) => {
                  const totals = totalsFor(s.id);
                  return (
                    <tr key={s.id}>
                      <td className="student-col">{s.firstName} {s.lastName}</td>
                      {days.map((d) => {
                        const entries = entriesFor(s.id, d);
                        const hasUnjustified = entries.some((e) => !e.justified);
                        return (
                          <td key={d} className={`day-cell ${entries.length ? 'clickable' : ''}`} onClick={() => entries.length && setDayDetail({ student: s, day: d, entries })}>
                            {entries.length > 0 && (
                              <span className={`day-mark ${hasUnjustified ? 'unjustified' : 'justified'}`}>
                                {entries.length > 1 ? entries.length : (hasUnjustified ? 'A' : 'J')}
                              </span>
                            )}
                          </td>
                        );
                      })}
                      <td className="summary-col justified">{totals.justified}</td>
                      <td className="summary-col unjustified">{totals.unjustified}</td>
                      <td className="total-col">{totals.total}</td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
        {!unauthorized && !students.length && <div className="empty-state">No students in this class.</div>}
      </div>

      {dayDetail && (
        <div className="modal-overlay" onClick={() => setDayDetail(null)}>
          <div className="modal-card" onClick={(e) => e.stopPropagation()}>
            <h3>{dayDetail.student.firstName} {dayDetail.student.lastName}</h3>
            <p>{month}-{String(dayDetail.day).padStart(2, '0')}</p>
            <div className="day-detail-list">
              {dayDetail.entries.map((entry) => (
                <div className="day-detail-row" key={entry.id}>
                  <div>
                    <strong>{entry.subjectName}</strong>
                    <div className="muted">{entry.justified ? (entry.justificationNote ?? 'Justified') : 'Unjustified'}</div>
                  </div>
                  {!entry.justified && (
                    <button className="btn-text" onClick={() => { setJustifying(entry); setDayDetail(null); }}>Justify</button>
                  )}
                </div>
              ))}
            </div>
            <div className="modal-actions">
              <button className="btn-outline" onClick={() => setDayDetail(null)}>Close</button>
            </div>
          </div>
        </div>
      )}

      {justifying && (
        <FormModal
          title={`Justify absence — ${justifying.studentFirstName} ${justifying.studentLastName}`}
          subtitle={`${justifying.subjectName} · ${justifying.date}`}
          fields={[{ key: 'note', label: 'Justification note', type: 'textarea' }]}
          submitLabel="Justify"
          onSubmit={(v) => void submitJustify(v)}
          onCancel={() => setJustifying(null)}
        />
      )}
    </div>
  );
}
