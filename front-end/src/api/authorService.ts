import api from "./axios";
import { Author } from "../models/Author";

export interface AuthorRequest {
  name: string;
  country?: string;
  birthDate?: string;
}

export const getAuthors = async (): Promise<Author[]> => {
  const response = await api.get<Author[]>("/api/authors");

  return response.data;
};

export const getAuthor = async (
  id: number
): Promise<Author> => {
  const response = await api.get<Author>(
    `/api/authors/${id}`
  );

  return response.data;
};

export const createAuthor = async (
  request: AuthorRequest
): Promise<Author> => {
  const response = await api.post<Author>(
    "/api/authors",
    request
  );

  return response.data;
};

export const updateAuthor = async (
  id: number,
  request: AuthorRequest
): Promise<Author> => {
  const response = await api.put<Author>(
    `/api/authors/${id}`,
    request
  );

  return response.data;
};

export const deleteAuthor = async (
  id: number
): Promise<void> => {
  await api.delete(`/api/authors/${id}`);
};