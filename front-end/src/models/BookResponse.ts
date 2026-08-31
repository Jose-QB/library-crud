export interface BookResponse {
  id: string;
  title: string;
  isbn?: string;
  publicationYear?: number;
  publisher?: string;
  authors: string[];
  genres: string[];
  stock: number;
  shelfLocation?: string;
}