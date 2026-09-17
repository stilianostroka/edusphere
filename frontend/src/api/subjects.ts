import client from './client';

export interface SubjectResponse {
  id: number;
  subjectName: string;
  code: string;
  totalHours: number;
  programYear: number;
}

export async function getAllSubjects(): Promise<SubjectResponse[]> {
  const response = await client.get<SubjectResponse[]>('/subjects');
  return response.data;
}

export async function createSubject(
  subjectName: string, code: string, totalHours: number, programYear: number
): Promise<SubjectResponse> {
  const response = await client.post<SubjectResponse>('/subjects', {
    subjectName, code, totalHours, programYear
  });
  return response.data;
}

export async function updateSubject(
  id: number, subjectName: string, code: string, totalHours: number, programYear: number
): Promise<SubjectResponse> {
  const response = await client.put<SubjectResponse>(`/subjects/${id}`, {
    subjectName, code, totalHours, programYear
  });
  return response.data;
}

export async function deleteSubject(id: number): Promise<void> {
  await client.delete(`/subjects/${id}`);
}
