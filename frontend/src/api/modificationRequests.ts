import client from './client';

export interface ModificationRequestResponse {
  id: number;
  requestedByTeacherId: number;
  requestedByTeacherName: string;
  topicId: number | null;
  lessonRecordId: number | null;
  semesterExamGradeId: number | null;
  proposedGrade: number | null;
  proposedAbsent: boolean;
  proposedTopicName: string | null;
  proposedDescription: string | null;
  proposedExamGrade: number | null;
  explanation: string;
  status: string;
  createdAt: string;
  reviewedAt: string | null;
}

export async function getPendingModificationRequests(): Promise<ModificationRequestResponse[]> {
  const response = await client.get<ModificationRequestResponse[]>('/modification-requests/pending');
  return response.data;
}

// No body needed - the acting admin's identity comes from the JWT
// (CurrentUserProvider), not a client-supplied id.
export async function approveModificationRequest(id: number): Promise<ModificationRequestResponse> {
  const response = await client.put<ModificationRequestResponse>(`/modification-requests/${id}/approve`);
  return response.data;
}

export async function rejectModificationRequest(id: number): Promise<ModificationRequestResponse> {
  const response = await client.put<ModificationRequestResponse>(`/modification-requests/${id}/reject`);
  return response.data;
}
