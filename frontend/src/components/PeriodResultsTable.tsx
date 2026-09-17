interface PeriodResult {
  sequenceNumber: number;
  ceg: number | null;
  examGrade: number | null;
}

interface PeriodResultsTableProps {
  periods: PeriodResult[];
  highlightSequence?: number;
}

export function PeriodResultsTable({ periods, highlightSequence }: PeriodResultsTableProps) {
  if (!periods.length) return <span className="muted">No periods yet</span>;
  return (
    <table className="period-table">
      <thead>
        <tr>
          <th></th>
          {periods.map((p) => <th key={p.sequenceNumber} className={p.sequenceNumber === highlightSequence ? 'highlight' : ''}>Period {p.sequenceNumber}</th>)}
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>CEG</td>
          {periods.map((p) => <td key={p.sequenceNumber} className={p.sequenceNumber === highlightSequence ? 'highlight' : ''}>{p.ceg?.toFixed(1) ?? '—'}</td>)}
        </tr>
        <tr>
          <td>Exam</td>
          {periods.map((p) => <td key={p.sequenceNumber} className={p.sequenceNumber === highlightSequence ? 'highlight' : ''}>{p.examGrade ?? '—'}</td>)}
        </tr>
      </tbody>
    </table>
  );
}
