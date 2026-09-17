import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { assignmentsApi, topicsApi, type Assignment, type Topic } from '../api/system';
import { Icon } from '../components/Icon';
import { FormModal, type FormModalField } from '../components/FormModal';
import { ConfirmModal } from '../components/ConfirmModal';
import { useMessage } from '../context/MessageContext';

const EDIT_WINDOW_MS = 24 * 60 * 60 * 1000;
const withinWindow = (createdAt: string) => Date.now() - new Date(createdAt).getTime() < EDIT_WINDOW_MS;

export function TopicsPage() {
  const { showError, showSuccess } = useMessage();
  const navigate = useNavigate();
  const [assignments, setAssignments] = useState<Assignment[]>([]);
  const [assignmentId, setAssignmentId] = useState('');
  const [topics, setTopics] = useState<Topic[]>([]);
  const [creating, setCreating] = useState(false);
  const [editing, setEditing] = useState<Topic | null>(null);
  const [deleting, setDeleting] = useState<Topic | null>(null);

  useEffect(() => {
    void assignmentsApi.mine().then((a) => { setAssignments(a); if (a[0]) setAssignmentId(String(a[0].id)); });
  }, []);

  const load = async () => {
    if (!assignmentId) { setTopics([]); return; }
    setTopics((await topicsApi.all(Number(assignmentId))).slice().sort((a, b) => b.date.localeCompare(a.date)));
  };
  useEffect(() => { void load(); }, [assignmentId]);

  const fields: FormModalField[] = [
    { key: 'date', label: 'Date', type: 'date', defaultValue: editing?.date ?? new Date().toISOString().slice(0, 10) },
    { key: 'topicName', label: 'Topic', defaultValue: editing?.topicName ?? '' },
    { key: 'description', label: 'Description', type: 'textarea', defaultValue: editing?.description ?? '', optional: true }
  ];

  const submitCreate = async (values: Record<string, string>) => {
    try {
      await topicsApi.create({ teachingAssignmentId: Number(assignmentId), date: values.date, topicName: values.topicName, description: values.description });
      setCreating(false);
      showSuccess('Topic added.');
      await load();
    } catch {
      showError('Topic could not be saved.');
    }
  };

  const submitEdit = async (values: Record<string, string>) => {
    if (!editing) return;
    try {
      await topicsApi.update(editing.id, { date: values.date, topicName: values.topicName, description: values.description });
      setEditing(null);
      showSuccess('Topic updated.');
      await load();
    } catch {
      showError('The 24-hour edit window has passed for this topic; submit a modification request instead.');
    }
  };

  const confirmDelete = async () => {
    if (!deleting) return;
    try {
      await topicsApi.remove(deleting.id);
      setDeleting(null);
      showSuccess('Topic deleted.');
      await load();
    } catch {
      showError('The 24-hour edit window has passed for this topic; submit a modification request instead.');
      setDeleting(null);
    }
  };

  return (
    <div>
      <div className="breadcrumb">Home › <span className="current">Topics</span></div>
      <div className="filter-bar panel">
        <div className="field" style={{ marginBottom: 0 }}>
          <label>Class and subject</label>
          <select value={assignmentId} onChange={(e) => setAssignmentId(e.target.value)}>
            {assignments.map((a) => <option key={a.id} value={a.id}>{a.schoolClassName} · {a.subjectName}</option>)}
          </select>
        </div>
        <div style={{ display: 'flex', alignItems: 'flex-end' }}>
          <button className="btn-primary" onClick={() => setCreating(true)} disabled={!assignmentId}>Add topic</button>
        </div>
      </div>

      <div className="panel">
        <div className="panel-header"><div className="panel-icon"><Icon name="list" /></div><div><div className="panel-eyebrow">Lesson topics</div><h3>All topics for this assignment</h3></div></div>
        <table className="data-table">
          <thead><tr><th>Date</th><th>Topic</th><th>Description</th><th></th></tr></thead>
          <tbody>
            {topics.map((t) => {
              const editable = withinWindow(t.createdAt);
              return (
                <tr key={t.id}>
                  <td>{t.date}</td>
                  <td>{t.topicName}</td>
                  <td>{t.description ?? '—'}</td>
                  <td>
                    <div className="row-actions">
                      <button className="btn-text" onClick={() => navigate(`/registry?assignmentId=${assignmentId}&topicId=${t.id}`)}>Open register</button>
                      <button className="icon-btn" title="Edit" onClick={() => setEditing(t)} disabled={!editable}><Icon name="edit" size={16} /></button>
                      <button className="icon-btn danger" title="Delete" onClick={() => setDeleting(t)} disabled={!editable}><Icon name="trash" size={16} /></button>
                      {!editable && <span className="hint">Window closed — <Link to="/submit-modification">request change</Link></span>}
                    </div>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
        {!topics.length && <div className="empty-state">No topics recorded yet for this assignment.</div>}
      </div>

      {creating && (
        <FormModal
          title="Add a lesson topic"
          fields={fields}
          submitLabel="Save"
          onSubmit={(v) => void submitCreate(v)}
          onCancel={() => setCreating(false)}
        />
      )}

      {editing && (
        <FormModal
          title="Edit topic"
          fields={fields}
          submitLabel="Save changes"
          onSubmit={(v) => void submitEdit(v)}
          onCancel={() => setEditing(null)}
        />
      )}

      {deleting && (
        <ConfirmModal
          title="Delete topic?"
          message={`"${deleting.topicName}" and any grades/absences recorded on it will be removed.`}
          confirmLabel="Delete"
          onConfirm={() => void confirmDelete()}
          onCancel={() => setDeleting(null)}
        />
      )}
    </div>
  );
}
