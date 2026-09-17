import client from './client';

export interface TeachingAssignmentResponse {
  id: number;
  schoolClassId: number;
  schoolClassName: string;
  subjectId: number;
  subjectName: string;
  subjectCode: string;
  teacherId: number;
  teacherName: string;
}

export async function getAllTeachingAssignments(academicYearId?: number): Promise<TeachingAssignmentResponse[]> {
  const response = await client.get<TeachingAssignmentResponse[]>('/teaching-assignments', {
    params: academicYearId ? { academicYearId } : {}
  });
  return response.data;
}

export async function createTeachingAssignment(
  classId: number, subjectId: number, teacherId: number
): Promise<TeachingAssignmentResponse> {
  const response = await client.post<TeachingAssignmentResponse>('/teaching-assignments', {
    classId, subjectId, teacherId
  });
  return response.data;
}
