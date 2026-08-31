import api from "./axios";
import { Publisher } from "../models/Publisher";

export interface PublisherRequest {
  name: string;
  country?: string;
  foundedYear?: number;
}

export const getPublishers = async (): Promise<
  Publisher[]
> => {
  const response = await api.get<Publisher[]>(
    "/api/publishers"
  );

  return response.data;
};

export const getPublisher = async (
  id: number
): Promise<Publisher> => {
  const response = await api.get<Publisher>(
    `/api/publishers/${id}`
  );

  return response.data;
};

export const createPublisher = async (
  request: PublisherRequest
): Promise<Publisher> => {
  const response = await api.post<Publisher>(
    "/api/publishers",
    request
  );

  return response.data;
};

export const updatePublisher = async (
  id: number,
  request: PublisherRequest
): Promise<Publisher> => {
  const response = await api.put<Publisher>(
    `/api/publishers/${id}`,
    request
  );

  return response.data;
};

export const deletePublisher = async (
  id: number
): Promise<void> => {
  await api.delete(`/api/publishers/${id}`);
};