import client from './client';

export interface ForumPostResponse {
  id: number;
  authorId: number;
  authorName: string;
  content: string;
  createdAt: string;
}

export async function getAllPosts(): Promise<ForumPostResponse[]> {
  const response = await client.get<ForumPostResponse[]>('/forum');
  return response.data;
}

export async function createPost(content: string): Promise<ForumPostResponse> {
  const response = await client.post<ForumPostResponse>('/forum', { content });
  return response.data;
}

export async function deletePost(id: number): Promise<void> {
  await client.delete(`/forum/${id}`);
}
