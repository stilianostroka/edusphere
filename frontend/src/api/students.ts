import client from './client';

export interface StudentResponse {
  id: number;
  schoolClassId: number | null;
  className: string | null;
  firstName: string;
  lastName: string;
  status: string;
}

export async function getAllStudents(academicYearId?: number): Promise<StudentResponse[]> {
  const response = await client.get<StudentResponse[]>('/students', {
    params: academicYearId ? { academicYearId } : {}
  });
  return response.data;
}

export async function getStudentsByClass(classId: number): Promise<StudentResponse[]> {
  const response = await client.get<StudentResponse[]>(`/students/class/${classId}`);
  return response.data;
}

export async function enrollStudent(studentId: number, classId: number): Promise<StudentResponse> {
  const response = await client.post<StudentResponse>(`/students/${studentId}/enroll`, { classId });
  return response.data;
}
