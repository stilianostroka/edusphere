import client from './client';

export interface TeacherResponse {
  teacherId: number;
  teacherName: string;
  teacherSurname: string;
  teacherEmail: string;
}

export async function getAllTeachers(): Promise<TeacherResponse[]> {
  const response = await client.get<TeacherResponse[]>('/teachers');
  return response.data;
}

export async function createTeacher(
  name: string, surname: string, email: string, password: string, gender: string
): Promise<TeacherResponse> {
  const response = await client.post<TeacherResponse>('/teachers', {
    name, surname, email, password, gender
  });
  return response.data;
}
