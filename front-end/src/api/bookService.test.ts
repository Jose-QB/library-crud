import { describe, it, expect, vi, beforeEach } from "vitest";
import api from "./axios";
import {
  getBooks,
  getBook,
  createBook,
  updateBook,
  deleteBook,
} from "./bookService";

vi.mock("./axios", () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}));

describe("bookService", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("gets books without filters", async () => {
    const books = [
      {
        id: "A12",
        title: "Clean Code",
        authors: ["Robert C. Martin"],
        genres: ["Programming"],
        stock: 5,
      },
    ];

    vi.mocked(api.get).mockResolvedValue({
      data: books,
    });

    const result = await getBooks();

    expect(api.get).toHaveBeenCalledWith(
      "/api/books",
      {
        params: {
          title: undefined,
          authorIds: undefined,
          genreIds: undefined,
        },
      }
    );

    expect(result).toEqual(books);
  });

  it("gets books using filters", async () => {
    vi.mocked(api.get).mockResolvedValue({
      data: [],
    });

    await getBooks(
      "Clean",
      [1, 2],
      [3, 4]
    );

    expect(api.get).toHaveBeenCalledWith(
      "/api/books",
      {
        params: {
          title: "Clean",
          authorIds: "1,2",
          genreIds: "3,4",
        },
      }
    );
  });

  it("gets a book by id", async () => {
    const book = {
      id: "A12",
      title: "Clean Code",
    };

    vi.mocked(api.get).mockResolvedValue({
      data: book,
    });

    const result = await getBook("A12");

    expect(api.get).toHaveBeenCalledWith(
      "/api/books/A12"
    );

    expect(result).toEqual(book);
  });

  it("creates a book", async () => {
    const request = {
      title: "Clean Code",
    };

    const book = {
      id: "A12",
      ...request,
    };

    vi.mocked(api.post).mockResolvedValue({
      data: book,
    });

    const result = await createBook(request as any);

    expect(api.post).toHaveBeenCalledWith(
      "/api/books",
      request
    );

    expect(result).toEqual(book);
  });

  it("updates a book", async () => {
    const request = {
      title: "Clean Code Updated",
    };

    const book = {
      id: "A12",
      ...request,
    };

    vi.mocked(api.put).mockResolvedValue({
      data: book,
    });

    const result = await updateBook(
      "A12",
      request as any
    );

    expect(api.put).toHaveBeenCalledWith(
      "/api/books/A12",
      request
    );

    expect(result).toEqual(book);
  });

  it("deletes a book", async () => {
    vi.mocked(api.delete).mockResolvedValue({
      data: undefined,
    });

    await deleteBook("A12");

    expect(api.delete).toHaveBeenCalledWith(
      "/api/books/A12"
    );
  });
});