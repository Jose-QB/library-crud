import api from "./axios";

import {
  BookResponse,
  BookDetailResponse,
  BookRequest,
} from "../models/Book";

export const getBooks = async (
  title?: string,
  authorIds?: number[],
  genreIds?: number[]
): Promise<BookResponse[]> => {
  const response = await api.get<BookResponse[]>("/api/books", {
    params: {
      title: title || undefined,
      authorIds: authorIds?.length
        ? authorIds.join(",")
        : undefined,
      genreIds: genreIds?.length
        ? genreIds.join(",")
        : undefined,
    },
  });

  return response.data;
};

export const getBook = async (
  id: string
): Promise<BookDetailResponse> => {
  const response = await api.get<BookDetailResponse>(
    `/api/books/${id}`
  );

  return response.data;
};

export const createBook = async (
  book: BookRequest
): Promise<BookDetailResponse> => {
  const response = await api.post<BookDetailResponse>(
    "/api/books",
    book
  );

  return response.data;
};

export const updateBook = async (
  id: string,
  book: BookRequest
): Promise<BookDetailResponse> => {
  const response = await api.put<BookDetailResponse>(
    `/api/books/${id}`,
    book
  );

  return response.data;
};

export const deleteBook = async (
  id: string
): Promise<void> => {
  await api.delete(`/api/books/${id}`);
};