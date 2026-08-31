import { describe, it, expect, vi, beforeEach } from "vitest";
import api from "./axios";
import {
  getAuthors,
  getAuthor,
  createAuthor,
  updateAuthor,
  deleteAuthor,
} from "./authorService";

vi.mock("./axios", () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}));

describe("authorService", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("gets all authors", async () => {
    const authors = [
      {
        id: 1,
        name: "Gabriel García Márquez",
        country: "Colombia",
        birthDate: "1927-03-06",
      },
    ];

    vi.mocked(api.get).mockResolvedValue({
      data: authors,
    });

    const result = await getAuthors();

    expect(api.get).toHaveBeenCalledWith("/api/authors");
    expect(result).toEqual(authors);
  });

  it("gets an author by id", async () => {
    const author = {
      id: 1,
      name: "Gabriel García Márquez",
      country: "Colombia",
      birthDate: "1927-03-06",
    };

    vi.mocked(api.get).mockResolvedValue({
      data: author,
    });

    const result = await getAuthor(1);

    expect(api.get).toHaveBeenCalledWith("/api/authors/1");
    expect(result).toEqual(author);
  });

  it("creates an author", async () => {
    const request = {
      name: "Jorge Luis Borges",
      country: "Argentina",
      birthDate: "1899-08-24",
    };

    const author = {
      id: 2,
      ...request,
    };

    vi.mocked(api.post).mockResolvedValue({
      data: author,
    });

    const result = await createAuthor(request);

    expect(api.post).toHaveBeenCalledWith(
      "/api/authors",
      request
    );

    expect(result).toEqual(author);
  });

  it("updates an author", async () => {
    const request = {
      name: "Jorge Luis Borges Updated",
      country: "Argentina",
      birthDate: "1899-08-24",
    };

    const author = {
      id: 2,
      ...request,
    };

    vi.mocked(api.put).mockResolvedValue({
      data: author,
    });

    const result = await updateAuthor(2, request);

    expect(api.put).toHaveBeenCalledWith(
      "/api/authors/2",
      request
    );

    expect(result).toEqual(author);
  });

  it("deletes an author", async () => {
    vi.mocked(api.delete).mockResolvedValue({
      data: undefined,
    });

    await deleteAuthor(2);

    expect(api.delete).toHaveBeenCalledWith(
      "/api/authors/2"
    );
  });
});