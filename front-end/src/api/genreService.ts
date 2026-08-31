import api from "./axios";
import { Genre } from "../models/Genre";

export interface GenreRequest {
  name: string;
  description?: string;
}

export const getGenres = async (): Promise<Genre[]> => {
  const response = await api.get<Genre[]>("/api/genres");

  return response.data;
};

export const getGenre = async (
  id: number
): Promise<Genre> => {
  const response = await api.get<Genre>(
    `/api/genres/${id}`
  );

  return response.data;
};

export const createGenre = async (
  request: GenreRequest
): Promise<Genre> => {
  const response = await api.post<Genre>(
    "/api/genres",
    request
  );

  return response.data;
};

export const updateGenre = async (
  id: number,
  request: GenreRequest
): Promise<Genre> => {
  const response = await api.put<Genre>(
    `/api/genres/${id}`,
    request
  );

  return response.data;
};

export const deleteGenre = async (
  id: number
): Promise<void> => {
  await api.delete(`/api/genres/${id}`);
};