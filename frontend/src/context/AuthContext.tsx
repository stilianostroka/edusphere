import { createContext, useContext, useEffect, useState, type ReactNode } from 'react';
import { login as loginRequest } from '../api/auth';
import { decodeToken } from '../api/client';
import type { AuthUser, Role } from '../types';
import { getMe } from '../api/system';

interface AuthState {
  user: AuthUser | null;
  isAuthenticated: boolean;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthState | undefined>(undefined);

// Reads whatever token is already in localStorage (if any) and rebuilds the
// user object from it - this is what makes a page refresh NOT log you out,
// since the token survives a refresh but React state normally doesn't.
function restoreUserFromStoredToken(): AuthUser | null {
  const token = localStorage.getItem('token');
  if (!token) return null;
  try {
    const decoded = decodeToken(token);
    // If the token has already expired, treat it as not logged in at all,
    // rather than showing a broken session that fails on the first request.
    if (decoded.exp * 1000 < Date.now()) {
      localStorage.removeItem('token');
      return null;
    }
    return { name: decoded.sub, role: decoded.role as Role };
  } catch {
    localStorage.removeItem('token');
    return null;
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(() => restoreUserFromStoredToken());

  useEffect(() => {
    if (!localStorage.getItem('token')) return;
    void getMe().then((profile) => setUser({ name: profile.fullName, email: profile.email, role: profile.role, profileId: profile.profileId }))
      .catch(() => { localStorage.removeItem('token'); setUser(null); });
  }, []);

  async function login(email: string, password: string) {
    const { token } = await loginRequest(email, password);
    localStorage.setItem('token', token);
    const profile = await getMe();
    setUser({ name: profile.fullName, email: profile.email, role: profile.role, profileId: profile.profileId });
  }

  function logout() {
    localStorage.removeItem('token');
    setUser(null);
  }

  const value: AuthState = {
    user,
    isAuthenticated: user !== null,
    login,
    logout
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthState {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
