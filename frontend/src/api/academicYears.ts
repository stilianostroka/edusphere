import client from './client';

export interface AcademicYearResponse {
  id: number;
  label: string;
  startDate: string;
  endDate: string;
  active: boolean;
}

export interface GradingPeriodSummary {
  id: number;
  sequenceNumber: number;
  startDate: string;
  endDate: string;
}

export async function getAllAcademicYears(): Promise<AcademicYearResponse[]> {
  const response = await client.get<AcademicYearResponse[]>('/academic-year/all');
  return response.data;
}

export async function createAcademicYear(
  label: string, startDate: string, endDate: string, period1End: string, period2End: string
): Promise<AcademicYearResponse> {
  const response = await client.post<AcademicYearResponse>('/academic-year/create', {
    label, startDate, endDate, period1End, period2End
  });
  return response.data;
}

export async function getGradingPeriods(academicYearId: number): Promise<GradingPeriodSummary[]> {
  const response = await client.get<GradingPeriodSummary[]>(`/academic-year/${academicYearId}/grading-periods`);
  return response.data;
}

export async function activateAcademicYear(id: number): Promise<void> {
  await client.put(`/academic-year/${id}/activate`);
}

export async function deactivateAcademicYear(id: number): Promise<void> {
  await client.put(`/academic-year/${id}/deactivate`);
}
