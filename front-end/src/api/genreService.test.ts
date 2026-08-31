import { describe, it, expect, vi, beforeEach } from "vitest";
import api from "./axios";
import {
  getGenres,
  getGenre,
  createGenre,
  updateGenre,
  deleteGenre,
} from "./genreService";

vi.mock("./axios", () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}));

describe("genreService", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("gets genres", async () => {
    const genres = [
      { id: 1, name: "Fantasy", description: "Fantasy books" },
    ];

    vi.mocked(api.get).mockResolvedValue({
      data: genres,
    });

    expect(await getGenres()).toEqual(genres);
    expect(api.get).toHaveBeenCalledWith("/api/genres");
  });

  it("gets a genre by id", async () => {
    const genre = {
      id: 1,
      name: "Fantasy",
      description: "Fantasy books",
    };

    vi.mocked(api.get).mockResolvedValue({
      data: genre,
    });

    expect(await getGenre(1)).toEqual(genre);
    expect(api.get).toHaveBeenCalledWith("/api/genres/1");
  });

  it("creates a genre", async () => {
    const request = {
      name: "Fantasy",
      description: "Fantasy books",
    };

    const genre = {
      id: 1,
      ...request,
    };

    vi.mocked(api.post).mockResolvedValue({
      data: genre,
    });

    expect(await createGenre(request)).toEqual(genre);

    expect(api.post).toHaveBeenCalledWith(
      "/api/genres",
      request
    );
  });

  it("updates a genre", async () => {
    const request = {
      name: "Fantasy Updated",
      description: "Updated",
    };

    vi.mocked(api.put).mockResolvedValue({
      data: {
        id: 1,
        ...request,
      },
    });

    await updateGenre(1, request);

    expect(api.put).toHaveBeenCalledWith(
      "/api/genres/1",
      request
    );
  });

  it("deletes a genre", async () => {
    vi.mocked(api.delete).mockResolvedValue({
      data: undefined,
    });

    await deleteGenre(1);

    expect(api.delete).toHaveBeenCalledWith(
      "/api/genres/1"
    );
  });
});