import { createContext, useContext, useEffect, useState, type ReactNode } from 'react';
import { getAllAcademicYears, type AcademicYearResponse } from '../api/academicYears';
import { useAuth } from './AuthContext';

interface AcademicYearState {
  years: AcademicYearResponse[];
  selectedYearId: number | null;
  selectedYear: AcademicYearResponse | null;
  setSelectedYearId: (id: number) => void;
  loading: boolean;
  refresh: () => Promise<void>;
}

const AcademicYearContext = createContext<AcademicYearState | undefined>(undefined);

// Shared across every admin/teacher page so "which year is selected" is one
// real piece of state, not a decorative dropdown each page ignores.
export function AcademicYearProvider({ children }: { children: ReactNode }) {
  const { isAuthenticated } = useAuth();
  const [years, setYears] = useState<AcademicYearResponse[]>([]);
  const [selectedYearId, setSelectedYearId] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (isAuthenticated) void refresh();
    else { setYears([]); setSelectedYearId(null); setLoading(false); }
  }, [isAuthenticated]);

  async function refresh() {
    setLoading(true);
    try {
      const data = await getAllAcademicYears();
      setYears(data);
      setSelectedYearId((current) => current ?? data.find((y) => y.active)?.id ?? data[0]?.id ?? null);
    } catch {
      // Left for the pages themselves to surface - this context only tracks
      // selection state, not error display.
    } finally {
      setLoading(false);
    }
  }

  const selectedYear = years.find((y) => y.id === selectedYearId) ?? null;

  return (
    <AcademicYearContext.Provider value={{ years, selectedYearId, selectedYear, setSelectedYearId, loading, refresh }}>
      {children}
    </AcademicYearContext.Provider>
  );
}

export function useAcademicYear(): AcademicYearState {
  const context = useContext(AcademicYearContext);
  if (!context) {
    throw new Error('useAcademicYear must be used within an AcademicYearProvider');
  }
  return context;
}
