import type { ReactNode } from 'react';

const GRADES = [4, 5, 6, 7, 8, 9, 10];

interface GradeSelectorModalProps {
  studentName: string;
  currentGrade?: number | null;
  extra?: ReactNode;
  removeLabel?: string;
  onSelect: (grade: number) => void;
  onRemove?: () => void;
  onCancel: () => void;
}

export function GradeSelectorModal({ studentName, currentGrade, extra, removeLabel = 'Remove grade', onSelect, onRemove, onCancel }: GradeSelectorModalProps) {
  return (
    <div className="modal-overlay" onClick={onCancel}>
      <div className="modal-card" onClick={(e) => e.stopPropagation()}>
        <h3>Grade {studentName}</h3>
        <p>Select a grade from 4 to 10.</p>
        {extra}
        <div className="grade-grid">
          {GRADES.map((grade) => (
            <button
              key={grade}
              className={`grade-btn ${grade === currentGrade ? 'selected' : ''}`}
              onClick={() => onSelect(grade)}
            >
              {grade}
            </button>
          ))}
        </div>
        <div className="modal-actions">
          {onRemove && <button className="btn-danger" style={{ marginRight: 'auto' }} onClick={onRemove}>{removeLabel}</button>}
          <button className="btn-outline" onClick={onCancel}>Cancel</button>
        </div>
      </div>
    </div>
  );
}
