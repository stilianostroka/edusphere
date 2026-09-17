import { useState, type FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useMessage } from '../context/MessageContext';
import { forgotPassword } from '../api/auth';
import { FormModal } from './FormModal';

export function LoginPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [showForgot, setShowForgot] = useState(false);
  const { login } = useAuth();
  const { showError, showSuccess } = useMessage();
  const navigate = useNavigate();

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setSubmitting(true);
    try {
      await login(email, password);
      navigate('/');
    } catch {
      showError('Email or password is incorrect.');
    } finally {
      setSubmitting(false);
    }
  }

  async function handleForgotPassword(values: Record<string, string>) {
    setShowForgot(false);
    try {
      await forgotPassword(values.email);
      showSuccess('If an account exists for that email, a reset link has been sent.');
    } catch {
      showError('Could not send the reset email. Please try again later.');
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
          <h1>Welcome back</h1>
          <p className="subtitle">Sign in to manage grades, attendance, and communication — all in one place.</p>

          <form onSubmit={handleSubmit}>
            <div className="field">
              <label htmlFor="email">Email</label>
              <input
                id="email"
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="you@edusphere.com"
                required
              />
            </div>
            <div className="field">
              <label htmlFor="password">Password</label>
              <input
                id="password"
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                required
              />
            </div>
            <div className="login-row-between">
              <label style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
                <input type="checkbox" /> Remember me
              </label>
              <a href="#" onClick={(e) => { e.preventDefault(); setShowForgot(true); }}>Forgot password?</a>
            </div>
            <button type="submit" className="btn-primary" style={{ width: '100%' }} disabled={submitting}>
              {submitting ? 'Signing in…' : 'Sign in'}
            </button>
          </form>
        </div>

        <p className="login-footnote">© 2026 EduSphere · Privacy · Terms</p>
      </div>

      {showForgot && (
        <FormModal
          title="Forgot password"
          subtitle="Enter your account email and we'll send you a link to reset your password."
          fields={[{ key: 'email', label: 'Email', type: 'text', placeholder: 'you@edusphere.com' }]}
          submitLabel="Send reset link"
          onSubmit={handleForgotPassword}
          onCancel={() => setShowForgot(false)}
        />
      )}
    </div>
  );
}
