import client from './client';

export interface SchoolClassResponse {
  id: number;
  className: string;
  classYear: string;
  maxStudents: number;
  academicYearLabel: string;
  supervisorId: number | null;
  supervisorName: string | null;
}

export async function getAllClasses(academicYearId?: number): Promise<SchoolClassResponse[]> {
  const response = await client.get<SchoolClassResponse[]>('/classes', {
    params: academicYearId ? { academicYearId } : {}
  });
  return response.data;
}

export async function createClass(
  academicYearId: number, className: string, classYear: string, maxStudents: number
): Promise<SchoolClassResponse> {
  const response = await client.post<SchoolClassResponse>('/classes/create', {
    academicYearId, className, classYear, maxStudents
  });
  return response.data;
}

export async function assignSupervisor(classId: number, teacherId: number): Promise<SchoolClassResponse> {
  const response = await client.put<SchoolClassResponse>(`/classes/${classId}/assign-supervisor`, { teacherId });
  return response.data;
}
