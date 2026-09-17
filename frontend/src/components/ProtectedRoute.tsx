import { Navigate } from 'react-router-dom';
import type { ReactNode } from 'react';
import type { Role } from '../types';
import { useAuth } from '../context/AuthContext';
import { AppShell } from './AppShell';

export function ProtectedRoute({
                                 children,
                                 roles
                               }: {
  children: ReactNode;
  roles?: Role[];
}) {
  const { isAuthenticated, user } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (roles && user && !roles.includes(user.role)) {
    return <Navigate to="/" replace />;
  }

  return <AppShell>{children}</AppShell>;
}