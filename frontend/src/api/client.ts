import axios from 'axios';

export const API_BASE_URL = (import.meta as unknown as { env?: { VITE_API_URL?: string } }).env?.VITE_API_URL ?? 'http://localhost:8080/api';

const client = axios.create({
  baseURL: API_BASE_URL,
  headers: { 'Content-Type': 'application/json' }
});

client.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export interface DecodedToken {
  sub: string;
  role: string;
  exp: number;
}

export function decodeToken(token: string): DecodedToken {
  const payload = token.split('.')[1];
  const normalized = payload.replace(/-/g, '+').replace(/_/g, '/');
  const json = atob(normalized.padEnd(Math.ceil(normalized.length / 4) * 4, '='));
  return JSON.parse(json) as DecodedToken;
}

export default client;
