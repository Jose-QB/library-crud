export interface BookRequest {
  title: string;
  isbn?: string;
  publicationYear?: number;
  edition?: string;
  language?: string;
  pages?: number;
  stock?: number;
  shelfLocation?: string;
  publisherId: number;
  authorIds: number[];
  genreIds: number[];
}