import { useEffect, useState } from 'react';
import {
  approveModificationRequest,
  getPendingModificationRequests,
  rejectModificationRequest,
  type ModificationRequestResponse
} from '../api/modificationRequests';
import { Icon } from '../components/Icon';
import { useMessage } from '../context/MessageContext';

function describeTarget(req: ModificationRequestResponse): string {
  if (req.topicId !== null) return `Topic #${req.topicId}: "${req.proposedTopicName}"`;
  if (req.lessonRecordId !== null) return `Lesson record #${req.lessonRecordId}`;
  if (req.semesterExamGradeId !== null) return `Exam grade #${req.semesterExamGradeId}`;
  return '—';
}

function describeType(req: ModificationRequestResponse): string {
  if (req.topicId !== null) return 'Topic';
  if (req.semesterExamGradeId !== null) return 'Exam Grade';
  if (req.proposedAbsent) return 'Absence';
  return 'Grade';
}

export function ModificationRequestsPage() {
  const { showError } = useMessage();
  const [requests, setRequests] = useState<ModificationRequestResponse[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    void load();
  }, []);

  async function load() {
    setLoading(true);
    try {
      setRequests(await getPendingModificationRequests());
    } catch {
      showError('Could not load modification requests. Is the backend running?');
    } finally {
      setLoading(false);
    }
  }

  async function handleApprove(id: number) {
    try {
      await approveModificationRequest(id);
      await load();
    } catch {
      showError('Could not approve this request.');
    }
  }

  async function handleReject(id: number) {
    try {
      await rejectModificationRequest(id);
      await load();
    } catch {
      showError('Could not reject this request.');
    }
  }

  return (
    <div>
      <div className="breadcrumb">
        Home <span style={{ margin: '0 6px' }}>›</span> <span className="current">Modification Requests</span>
      </div>
      <div className="panel">
        <div className="panel-header">
          <div className="panel-icon"><Icon name="inbox" /></div>
          <div>
            <div className="panel-eyebrow">Pending Approval</div>
            <h3>Modification Requests ({requests.length})</h3>
          </div>
        </div>

        {loading ? (
          <p className="empty-state">Loading…</p>
        ) : requests.length === 0 ? (
          <div className="empty-state">No pending requests — all caught up.</div>
        ) : (
          <table className="data-table">
            <thead>
              <tr>
                <th>#</th>
                <th>Teacher</th>
                <th>Type</th>
                <th>Target</th>
                <th>Explanation</th>
                <th>Submitted</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {requests.map((req, index) => (
                <tr key={req.id}>
                  <td>{index + 1}.</td>
                  <td>{req.requestedByTeacherName}</td>
                  <td><span className="status-pill pending">{describeType(req)}</span></td>
                  <td>{describeTarget(req)}</td>
                  <td style={{ maxWidth: 260 }}>{req.explanation}</td>
                  <td>{new Date(req.createdAt).toLocaleDateString()}</td>
                  <td>
                    <div className="row-actions">
                      <button className="btn-text" onClick={() => handleApprove(req.id)}>Approve</button>
                      <button className="btn-text" style={{ color: 'var(--firebrick)' }} onClick={() => handleReject(req.id)}>Reject</button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
