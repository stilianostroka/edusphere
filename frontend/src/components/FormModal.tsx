import { useState } from 'react';

export interface FormModalField {
  key: string;
  label: string;
  type?: 'text' | 'textarea' | 'date' | 'number' | 'select';
  options?: Array<{ value: string; label: string }>;
  defaultValue?: string;
  placeholder?: string;
  optional?: boolean;
}

interface FormModalProps {
  title: string;
  subtitle?: string;
  fields: FormModalField[];
  submitLabel?: string;
  onSubmit: (values: Record<string, string>) => void;
  onCancel: () => void;
}

export function FormModal({ title, subtitle, fields, submitLabel = 'Save', onSubmit, onCancel }: FormModalProps) {
  const [values, setValues] = useState<Record<string, string>>(
    () => Object.fromEntries(fields.map((f) => [f.key, f.defaultValue ?? (f.type === 'select' ? f.options?.[0]?.value ?? '' : '')]))
  );

  const canSubmit = fields.every((f) => f.optional || values[f.key]?.trim());

  return (
    <div className="modal-overlay" onClick={onCancel}>
      <div className="modal-card" onClick={(e) => e.stopPropagation()}>
        <h3>{title}</h3>
        {subtitle && <p>{subtitle}</p>}
        <div className="form-grid" style={{ gridTemplateColumns: '1fr', marginBottom: 8 }}>
          {fields.map((f) => (
            <div className="field" key={f.key}>
              <label>{f.label}</label>
              {f.type === 'textarea' ? (
                <textarea
                  value={values[f.key]}
                  placeholder={f.placeholder}
                  onChange={(e) => setValues({ ...values, [f.key]: e.target.value })}
                />
              ) : f.type === 'select' ? (
                <select value={values[f.key]} onChange={(e) => setValues({ ...values, [f.key]: e.target.value })}>
                  {f.options?.map((o) => <option key={o.value} value={o.value}>{o.label}</option>)}
                </select>
              ) : (
                <input
                  type={f.type ?? 'text'}
                  value={values[f.key]}
                  placeholder={f.placeholder}
                  onChange={(e) => setValues({ ...values, [f.key]: e.target.value })}
                />
              )}
            </div>
          ))}
        </div>
        <div className="modal-actions">
          <button className="btn-outline" onClick={onCancel}>Cancel</button>
          <button className="btn-primary" disabled={!canSubmit} onClick={() => onSubmit(values)}>{submitLabel}</button>
        </div>
      </div>
    </div>
  );
}
