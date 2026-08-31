export interface BookDetailResponse {
  id: string;
  title: string;
  isbn?: string;
  publicationYear?: number;
  edition?: string;
  language?: string;
  pages?: number;
  stock: number;
  shelfLocation?: string;
  publisher: Publisher;
  authors: Author[];
  genres: Genre[];
  createdAt: string;
  updatedAt: string;
}