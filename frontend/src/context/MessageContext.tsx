import { createContext, useCallback, useContext, useEffect, useState, type ReactNode } from 'react';
import { Icon } from '../components/Icon';

type MessageType = 'error' | 'success';
interface Toast { id: number; type: MessageType; text: string }

interface MessageContextValue {
  showError: (text: string) => void;
  showSuccess: (text: string) => void;
}

const MessageContext = createContext<MessageContextValue | undefined>(undefined);
const AUTO_DISMISS_MS = 5000;

export function MessageProvider({ children }: { children: ReactNode }) {
  const [toast, setToast] = useState<Toast | null>(null);

  const show = useCallback((type: MessageType, text: string) => {
    setToast({ id: Date.now(), type, text });
  }, []);

  useEffect(() => {
    if (!toast) return;
    const id = toast.id;
    const timer = setTimeout(() => setToast((current) => (current?.id === id ? null : current)), AUTO_DISMISS_MS);
    return () => clearTimeout(timer);
  }, [toast]);

  const value: MessageContextValue = {
    showError: (text) => show('error', text),
    showSuccess: (text) => show('success', text)
  };

  return (
    <MessageContext.Provider value={value}>
      {children}
      {toast && (
        <div className={`toast-window ${toast.type}`} onClick={() => setToast(null)} role="alert">
          <div className="toast-icon"><Icon name={toast.type === 'success' ? 'check' : 'x'} size={14} /></div>
          <p>{toast.text}</p>
        </div>
      )}
    </MessageContext.Provider>
  );
}

export function useMessage(): MessageContextValue {
  const ctx = useContext(MessageContext);
  if (!ctx) throw new Error('useMessage must be used within a MessageProvider');
  return ctx;
}
