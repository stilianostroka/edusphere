interface LogoutConfirmModalProps {
  onConfirm: () => void;
  onCancel: () => void;
}

export function LogoutConfirmModal({ onConfirm, onCancel }: LogoutConfirmModalProps) {
  return (
    <div className="modal-overlay" onClick={onCancel}>
      <div className="modal-card" onClick={(e) => e.stopPropagation()}>
        <h3>Sign out?</h3>
        <p>You'll need to sign in again to access your account.</p>
        <div className="modal-actions">
          <button className="btn-outline" onClick={onCancel}>Cancel</button>
          <button className="btn-danger" onClick={onConfirm}>Sign out</button>
        </div>
      </div>
    </div>
  );
}
