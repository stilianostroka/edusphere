import { useState, type FormEvent } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useMessage } from '../context/MessageContext';
import { resetPassword } from '../api/auth';

export function ResetPasswordPage() {
  const [searchParams] = useSearchParams();
  const token = searchParams.get('token') ?? '';
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const { showError, showSuccess } = useMessage();
  const navigate = useNavigate();

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    if (password !== confirmPassword) {
      showError('Passwords do not match.');
      return;
    }
    setSubmitting(true);
    try {
      await resetPassword(token, password);
      showSuccess('Password reset. You can now sign in with your new password.');
      navigate('/login');
    } catch {
      showError('This reset link is invalid or has expired. Please request a new one.');
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="login-page">
      <div className="login-blob blob-1" />
      <div className="login-blob blob-2" />
      <div className="login-blob blob-3" />

      <div className="login-shell">
        <div className="login-brand">
          <div className="brand-mark">E</div>
          <span>EduSphere</span>
        </div>

        <div className="login-card">
          <h1>Reset password</h1>
          <p className="subtitle">Choose a new password for your account.</p>

          {!token ? (
            <p className="empty-state">This reset link is missing its token. Please request a new one from the login page.</p>
          ) : (
            <form onSubmit={handleSubmit}>
              <div className="field">
                <label htmlFor="password">New password</label>
                <input
                  id="password"
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="••••••••"
                  required
                  minLength={8}
                />
              </div>
              <div className="field">
                <label htmlFor="confirmPassword">Confirm new password</label>
                <input
                  id="confirmPassword"
                  type="password"
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  placeholder="••••••••"
                  required
                  minLength={8}
                />
              </div>
              <button type="submit" className="btn-primary" style={{ width: '100%' }} disabled={submitting}>
                {submitting ? 'Resetting…' : 'Reset password'}
              </button>
            </form>
          )}
        </div>

        <p className="login-footnote">© 2026 EduSphere · Privacy · Terms</p>
      </div>
    </div>
  );
}
