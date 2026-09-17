import client from './client';

export async function login(email: string, password: string): Promise<{ token: string }> {
  const response = await client.post<{ token: string }>('/auth/login', { email, password });
  return response.data;
}
