import client from './client';

export interface ParentResponse {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  gender: string | null;
  address: string | null;
  phoneNumber: string | null;
}

export async function getAllParents(): Promise<ParentResponse[]> {
  const response = await client.get<ParentResponse[]>('/parents');
  return response.data;
}

export async function createParent(
  email: string, password: string, firstName: string, lastName: string, gender: string
): Promise<ParentResponse> {
  const response = await client.post<ParentResponse>('/parents', {
    email, password, firstName, lastName, gender
  });
  return response.data;
}
