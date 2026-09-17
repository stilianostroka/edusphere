import client from './client';

export async function login(email: string, password: string): Promise<{ token: string }> {
  const response = await client.post<{ token: string }>('/auth/login', { email, password });
  return response.data;
}

export async function forgotPassword(email: string): Promise<void> {
  await client.post('/auth/forgot-password', { email });
}

export async function resetPassword(token: string, newPassword: string): Promise<void> {
  await client.post('/auth/reset-password', { token, newPassword });
}
