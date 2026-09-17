import { useEffect, useState, type FormEvent } from 'react';
import { createSubject, deleteSubject, getAllSubjects, updateSubject, type SubjectResponse } from '../api/subjects';
import { Icon } from '../components/Icon';
import { ConfirmModal } from '../components/ConfirmModal';
import { useMessage } from '../context/MessageContext';

export function SubjectsPage() {
  const { showError } = useMessage();
  const [subjects, setSubjects] = useState<SubjectResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [formOpen, setFormOpen] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [subjectName, setSubjectName] = useState('');
  const [code, setCode] = useState('');
  const [totalHours, setTotalHours] = useState('');
  const [programYear, setProgramYear] = useState('1');
  const [deleting, setDeleting] = useState<SubjectResponse | null>(null);

  useEffect(() => {
    void load();
  }, []);

  async function load() {
    setLoading(true);
    try {
      setSubjects(await getAllSubjects());
    } catch {
      showError('Could not load subjects. Is the backend running?');
    } finally {
      setLoading(false);
    }
  }

  function openCreate() {
    setEditingId(null);
    setSubjectName(''); setCode(''); setTotalHours(''); setProgramYear('1');
    setFormOpen(true);
  }

  function openEdit(subject: SubjectResponse) {
    setEditingId(subject.id);
    setSubjectName(subject.subjectName);
    setCode(subject.code);
    setTotalHours(String(subject.totalHours));
    setProgramYear(String(subject.programYear));
    setFormOpen(true);
  }

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    try {
      if (editingId) {
        await updateSubject(editingId, subjectName, code, Number(totalHours), Number(programYear));
      } else {
        await createSubject(subjectName, code, Number(totalHours), Number(programYear));
      }
      setFormOpen(false);
      setEditingId(null);
      setSubjectName(''); setCode(''); setTotalHours('');
      await load();
    } catch {
      showError(editingId
        ? 'Could not update the subject - check the code is unique (5 characters).'
        : 'Could not create the subject - check the code is unique (5 characters).');
    }
  }

  async function confirmDelete() {
    if (!deleting) return;
    try {
      await deleteSubject(deleting.id);
      setDeleting(null);
      await load();
    } catch {
      showError(`"${deleting.subjectName}" is used by a teaching assignment and cannot be deleted.`);
      setDeleting(null);
    }
  }

  return (
    <div>
      <div className="breadcrumb">
        Home <span style={{ margin: '0 6px' }}>›</span> <span className="current">Subjects</span>
      </div>
      <div className="panel">
        <div className="panel-header">
          <div className="panel-icon"><Icon name="book-open" /></div>
          <div>
            <div className="panel-eyebrow">Subjects</div>
            <h3>All Subjects</h3>
          </div>
        </div>

        <div className="filter-bar">
          <div style={{ display: 'flex', alignItems: 'flex-end' }}>
            <button className="btn-primary" onClick={() => (formOpen ? setFormOpen(false) : openCreate())}>
              {formOpen ? 'Cancel' : 'Create Subject'}
            </button>
          </div>
        </div>

        {formOpen && (
          <form onSubmit={handleSubmit} style={{ padding: '0 22px 22px 22px' }}>
            <div className="filter-bar" style={{ padding: 0, border: 'none' }}>
              <div className="field">
                <label>Subject Name</label>
                <input value={subjectName} onChange={(e) => setSubjectName(e.target.value)} required />
              </div>
              <div className="field">
                <label>Code (5 characters)</label>
                <input value={code} onChange={(e) => setCode(e.target.value)} maxLength={5} required />
              </div>
              <div className="field">
                <label>Total Hours</label>
                <input type="number" value={totalHours} onChange={(e) => setTotalHours(e.target.value)} required />
              </div>
              <div className="field">
                <label>Program Year</label>
                <select value={programYear} onChange={(e) => setProgramYear(e.target.value)}>
                  <option value="1">Year 1</option>
                  <option value="2">Year 2</option>
                  <option value="3">Year 3</option>
                </select>
              </div>
            </div>
            <button type="submit" className="btn-primary">{editingId ? 'Save changes' : 'Save'}</button>
          </form>
        )}

        {loading ? (
          <p className="empty-state">Loading…</p>
        ) : (
          <table className="data-table">
            <thead>
              <tr>
                <th>#</th>
                <th>Subject</th>
                <th>Code</th>
                <th>Program Year</th>
                <th>Total Hours</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {subjects.map((subject, index) => (
                <tr key={subject.id}>
                  <td>{index + 1}.</td>
                  <td>{subject.subjectName}</td>
                  <td>{subject.code}</td>
                  <td>Year {subject.programYear}</td>
                  <td>{subject.totalHours}h</td>
                  <td>
                    <div className="row-actions">
                      <button className="icon-btn" title="Edit" onClick={() => openEdit(subject)}><Icon name="edit" size={16} /></button>
                      <button className="icon-btn danger" title="Delete" onClick={() => setDeleting(subject)}><Icon name="trash" size={16} /></button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
        {!loading && !subjects.length && <div className="empty-state">No subjects yet.</div>}
      </div>

      {deleting && (
        <ConfirmModal
          title="Delete subject?"
          message={`"${deleting.subjectName}" will be permanently removed. This only works if no teaching assignment uses it yet.`}
          confirmLabel="Delete"
          onConfirm={() => void confirmDelete()}
          onCancel={() => setDeleting(null)}
        />
      )}
    </div>
  );
}
