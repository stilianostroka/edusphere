import { useEffect, useState, type FormEvent } from 'react';
import { activateAcademicYear, createAcademicYear, deactivateAcademicYear, getAllAcademicYears, type AcademicYearResponse } from '../api/academicYears';
import { Icon } from '../components/Icon';
import { useMessage } from '../context/MessageContext';

export function AcademicYearsPage() {
  const { showError } = useMessage();
  const [years, setYears] = useState<AcademicYearResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [formOpen, setFormOpen] = useState(false);
  const [label, setLabel] = useState('');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [period1End, setPeriod1End] = useState('');
  const [period2End, setPeriod2End] = useState('');

  useEffect(() => {
    void load();
  }, []);

  async function load() {
    setLoading(true);
    try {
      setYears(await getAllAcademicYears());
    } catch {
      showError('Could not load academic years. Is the backend running?');
    } finally {
      setLoading(false);
    }
  }

  async function handleCreate(event: FormEvent) {
    event.preventDefault();
    try {
      await createAcademicYear(label, startDate, endDate, period1End, period2End);
      setFormOpen(false);
      setLabel(''); setStartDate(''); setEndDate(''); setPeriod1End(''); setPeriod2End('');
      await load();
    } catch {
      showError('Could not create the academic year - check the dates and try again.');
    }
  }

  async function handleActivate(id: number) {
    try {
      await activateAcademicYear(id);
      await load();
    } catch {
      showError('Could not activate this academic year.');
    }
  }

  async function handleDeactivate(id: number) {
    try { await deactivateAcademicYear(id); await load(); }
    catch { showError('Could not deactivate this academic year.'); }
  }

  return (
    <div>
      <div className="breadcrumb">
        Home <span style={{ margin: '0 6px' }}>›</span> <span className="current">Academic Years</span>
      </div>
      <div className="panel">
        <div className="panel-header">
          <div className="panel-icon"><Icon name="calendar" /></div>
          <div>
            <div className="panel-eyebrow">Academic Years</div>
            <h3>All Academic Years</h3>
          </div>
        </div>

        <div className="filter-bar">
          <div style={{ display: 'flex', alignItems: 'flex-end' }}>
            <button className="btn-primary" onClick={() => setFormOpen(!formOpen)}>
              {formOpen ? 'Cancel' : 'Create Academic Year'}
            </button>
          </div>
        </div>

        {formOpen && (
          <form onSubmit={handleCreate} style={{ padding: '0 22px 22px 22px' }}>
            <div className="filter-bar" style={{ padding: 0, border: 'none' }}>
              <div className="field">
                <label>Label</label>
                <input value={label} onChange={(e) => setLabel(e.target.value)} placeholder="2027-2028" required />
              </div>
              <div className="field">
                <label>Start Date</label>
                <input type="date" value={startDate} onChange={(e) => setStartDate(e.target.value)} required />
              </div>
              <div className="field">
                <label>End Date</label>
                <input type="date" value={endDate} onChange={(e) => setEndDate(e.target.value)} required />
              </div>
              <div className="field">
                <label>Period 1 End</label>
                <input type="date" value={period1End} onChange={(e) => setPeriod1End(e.target.value)} required />
              </div>
              <div className="field">
                <label>Period 2 End</label>
                <input type="date" value={period2End} onChange={(e) => setPeriod2End(e.target.value)} required />
              </div>
            </div>
            <button type="submit" className="btn-primary">Save</button>
          </form>
        )}

        {loading ? (
          <p className="empty-state">Loading…</p>
        ) : (
          <table className="data-table">
            <thead>
              <tr>
                <th>#</th>
                <th>Label</th>
                <th>Start Date</th>
                <th>End Date</th>
                <th>Status</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {years.map((year, index) => (
                <tr key={year.id}>
                  <td>{index + 1}.</td>
                  <td>{year.label}</td>
                  <td>{year.startDate}</td>
                  <td>{year.endDate}</td>
                  <td>
                    <span className={`status-pill ${year.active ? 'active' : 'inactive'}`}>
                      {year.active ? 'Active' : 'Inactive'}
                    </span>
                  </td>
                  <td>
                    {year.active
                      ? <button className="btn-text" onClick={() => handleDeactivate(year.id)}>Deactivate</button>
                      : <button className="btn-text" onClick={() => handleActivate(year.id)}>Activate</button>}
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
