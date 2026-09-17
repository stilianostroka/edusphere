import { useEffect, useState } from 'react';
import { assignmentsApi, gradesApi, type Assignment, type GradeRoster } from '../api/system';
import { getGradingPeriods, type GradingPeriodSummary } from '../api/academicYears';
import { useAcademicYear } from '../context/AcademicYearContext';
import { Icon } from '../components/Icon';
import { GradeSelectorModal } from '../components/GradeSelectorModal';
import { PeriodResultsTable } from '../components/PeriodResultsTable';
import { ConfirmModal } from '../components/ConfirmModal';
import { useMessage } from '../context/MessageContext';

export function FinalGradeRegisterPage() {
  const { selectedYearId } = useAcademicYear();
  const { showSuccess } = useMessage();
  const [assignments, setAssignments] = useState<Assignment[]>([]);
  const [assignmentId, setAssignmentId] = useState('');
  const [roster, setRoster] = useState<GradeRoster[]>([]);
  const [periods, setPeriods] = useState<GradingPeriodSummary[]>([]);
  const [examPeriodId, setExamPeriodId] = useState('');
  const [cegPeriodId, setCegPeriodId] = useState('');

  const [gradingRoster, setGradingRoster] = useState<GradeRoster | null>(null);
  const [examMode, setExamMode] = useState(false);
  const [withdrawing, setWithdrawing] = useState<GradeRoster | null>(null);

  useEffect(() => {
    void assignmentsApi.mine().then((a) => { setAssignments(a); if (a[0]) setAssignmentId(String(a[0].id)); });
  }, []);
  useEffect(() => { if (selectedYearId) void getGradingPeriods(selectedYearId).then((p) => {
    setPeriods(p); if (p[0]) { setExamPeriodId(String(p[0].id)); setCegPeriodId(String(p[0].id)); }
  }); }, [selectedYearId]);

  const assignment = assignments.find((a) => a.id === Number(assignmentId));

  const load = async () => {
    if (!assignment) return;
    setRoster(await gradesApi.roster(assignment.id));
  };
  useEffect(() => { void load(); }, [assignmentId, assignments.length]);

  const calculateCeg = async () => {
    if (!cegPeriodId) return;
    await load();
    const period = periods.find((p) => String(p.id) === cegPeriodId);
    showSuccess(`CEG recalculated for Period ${period?.sequenceNumber ?? cegPeriodId}.`);
  };

  const currentExamEntry = (r: GradeRoster) => r.periods.find((p) => String(periods.find((pd) => pd.sequenceNumber === p.sequenceNumber)?.id) === examPeriodId);

  const applyProjectGrade = async (grade: number) => {
    if (!gradingRoster || !assignment) return;
    await gradesApi.project(assignment.id, gradingRoster.studentId, grade);
    setGradingRoster(null);
    await load();
  };

  const removeProjectGrade = async () => {
    if (!gradingRoster?.finalGradeId) return;
    await gradesApi.removeProject(gradingRoster.finalGradeId);
    setGradingRoster(null);
    await load();
  };

  const applyExamGrade = async (grade: number) => {
    if (!gradingRoster || !assignment || !examPeriodId) return;
    await gradesApi.exam(assignment.id, gradingRoster.studentId, Number(examPeriodId), grade);
    setGradingRoster(null);
    await load();
  };

  const removeExamGrade = async () => {
    if (!gradingRoster) return;
    const entry = currentExamEntry(gradingRoster);
    if (!entry?.examGradeId) return;
    await gradesApi.removeExam(entry.examGradeId);
    setGradingRoster(null);
    await load();
  };

  const confirmWithdraw = async () => {
    if (!withdrawing?.finalGradeId) return;
    await gradesApi.withdraw(withdrawing.finalGradeId);
    setWithdrawing(null);
    await load();
  };

  return (
    <div>
      <div className="breadcrumb">Home › <span className="current">Final grade register</span></div>
      <div className="filter-bar panel">
        <div className="field" style={{ marginBottom: 0 }}>
          <label>Class and subject</label>
          <select value={assignmentId} onChange={(e) => setAssignmentId(e.target.value)}>
            {assignments.map((a) => <option key={a.id} value={a.id}>{a.schoolClassName} · {a.subjectName}</option>)}
          </select>
        </div>
        <div className="field" style={{ marginBottom: 0 }}>
          <label>CEG period</label>
          <select value={cegPeriodId} onChange={(e) => setCegPeriodId(e.target.value)}>
            {periods.map((p) => <option key={p.id} value={p.id}>Period {p.sequenceNumber}</option>)}
          </select>
        </div>
        <div style={{ display: 'flex', alignItems: 'flex-end' }}>
          <button className="btn-outline" onClick={() => void calculateCeg()} disabled={!cegPeriodId}>Calculate CEG</button>
        </div>
      </div>

      <div className="panel">
        <div className="panel-header"><div className="panel-icon"><Icon name="bar-chart" /></div><div><div className="panel-eyebrow">Calculated by backend</div><h3>Final grade register</h3></div></div>
        <table className="data-table">
          <thead><tr><th>Student</th><th>Periods</th><th>CCEG</th><th>CFE</th><th>Project</th><th>Final</th><th>Status</th><th></th></tr></thead>
          <tbody>
            {roster.map((r) => (
              <tr key={r.studentId}>
                <td>{r.studentFirstName} {r.studentLastName}</td>
                <td><PeriodResultsTable periods={r.periods} highlightSequence={periods.find((p) => String(p.id) === cegPeriodId)?.sequenceNumber} /></td>
                <td>{r.cceg ?? '—'}</td>
                <td>{r.cfe ?? '—'}</td>
                <td>{r.projectGrade ?? '—'}</td>
                <td>{r.finalGrade ?? '—'}</td>
                <td>{r.status}</td>
                <td>
                  <div className="row-actions">
                    <button className="btn-text" onClick={() => { setExamMode(true); setGradingRoster(r); }}>Exam</button>
                    <button className="btn-text" onClick={() => { setExamMode(false); setGradingRoster(r); }}>Project</button>
                    {r.finalGradeId && r.status === 'DRAFT' && (
                      <button className="btn-success" onClick={async () => { await gradesApi.submit(r.finalGradeId!); await load(); }}>Submit</button>
                    )}
                    {r.finalGradeId && r.status === 'SUBMITTED' && (
                      <button className="btn-outline" onClick={() => setWithdrawing(r)}>Withdraw</button>
                    )}
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {!roster.length && <div className="empty-state">No students in this class yet.</div>}
      </div>

      {gradingRoster && !examMode && (
        <GradeSelectorModal
          studentName={`${gradingRoster.studentFirstName} ${gradingRoster.studentLastName} — Project grade`}
          currentGrade={gradingRoster.projectGrade}
          onSelect={(g) => void applyProjectGrade(g)}
          onRemove={gradingRoster.projectGrade != null && gradingRoster.status === 'DRAFT' ? () => void removeProjectGrade() : undefined}
          removeLabel="Remove project grade"
          onCancel={() => setGradingRoster(null)}
        />
      )}

      {gradingRoster && examMode && (
        <GradeSelectorModal
          studentName={`${gradingRoster.studentFirstName} ${gradingRoster.studentLastName} — Exam grade`}
          currentGrade={currentExamEntry(gradingRoster)?.examGrade ?? undefined}
          onSelect={(g) => void applyExamGrade(g)}
          onRemove={currentExamEntry(gradingRoster)?.examGradeId ? () => void removeExamGrade() : undefined}
          removeLabel="Remove exam grade"
          onCancel={() => setGradingRoster(null)}
          extra={
            <div className="field">
              <label>Grading period</label>
              <select value={examPeriodId} onChange={(e) => setExamPeriodId(e.target.value)}>
                {periods.map((p) => <option key={p.id} value={p.id}>Period {p.sequenceNumber} ({p.startDate} – {p.endDate})</option>)}
              </select>
            </div>
          }
        />
      )}

      {withdrawing && (
        <ConfirmModal
          title="Withdraw submission?"
          message={`${withdrawing.studentFirstName} ${withdrawing.studentLastName}'s final grade will go back to DRAFT so you can adjust it before resubmitting.`}
          confirmLabel="Withdraw"
          onConfirm={() => void confirmWithdraw()}
          onCancel={() => setWithdrawing(null)}
        />
      )}
    </div>
  );
}
