import {
  describe,
  it,
  expect,
  vi,
  beforeEach,
} from "vitest";

import {
  render,
  screen,
  fireEvent,
  waitFor,
} from "@testing-library/react";

import { MemoryRouter } from "react-router-dom";

import Home from "./Home";

import {
  getBooks,
  deleteBook,
} from "../api/bookService";

import { getAuthors } from "../api/authorService";
import { getGenres } from "../api/genreService";

vi.mock("../api/bookService", () => ({
  getBooks: vi.fn(),
  deleteBook: vi.fn(),
}));

vi.mock("../api/authorService", () => ({
  getAuthors: vi.fn(),
}));

vi.mock("../api/genreService", () => ({
  getGenres: vi.fn(),
}));

const mockBooks = [
  {
    id: "A12",
    title: "Clean Code",
    authors: ["Robert C. Martin"],
    genres: ["Programming"],
    stock: 5,
  },
];

const mockAuthors = [
  {
    id: 1,
    name: "Robert C. Martin",
    country: "United States",
    birthDate: "1952-12-05",
  },
];

const mockGenres = [
  {
    id: 1,
    name: "Programming",
    description: "Programming books",
  },
];

function renderHome() {
  return render(
    <MemoryRouter>
      <Home />
    </MemoryRouter>
  );
}

describe("Home", () => {
  beforeEach(() => {
    vi.clearAllMocks();

    vi.mocked(getAuthors).mockResolvedValue(
      mockAuthors
    );

    vi.mocked(getGenres).mockResolvedValue(
      mockGenres
    );

    vi.mocked(getBooks).mockResolvedValue(
      mockBooks
    );

    vi.mocked(deleteBook).mockResolvedValue(
      undefined
    );
  });

  it("loads the library information on initial render", async () => {
    renderHome();

    await waitFor(() => {
      expect(
        screen.getByText("Clean Code")
      ).toBeInTheDocument();
    });

    expect(getAuthors).toHaveBeenCalledTimes(1);
    expect(getGenres).toHaveBeenCalledTimes(1);
    expect(getBooks).toHaveBeenCalledTimes(1);
  });

  it("shows the loading indicator while loading", () => {
    vi.mocked(getAuthors).mockReturnValue(
      new Promise(() => {})
    );

    vi.mocked(getGenres).mockReturnValue(
      new Promise(() => {})
    );

    vi.mocked(getBooks).mockReturnValue(
      new Promise(() => {})
    );

    renderHome();

    expect(
      screen.getByRole("progressbar")
    ).toBeInTheDocument();
  });

  it("shows an error when initial loading fails", async () => {
    vi.mocked(getAuthors).mockRejectedValue(
      new Error("Network error")
    );

    renderHome();

    await waitFor(() => {
      expect(
        screen.getByText(
          "Unable to load library information."
        )
      ).toBeInTheDocument();
    });
  });

  it("shows the New Book button when the library is not full", async () => {
    renderHome();

    await waitFor(() => {
      expect(
        screen.getByRole("button", {
          name: /new book/i,
        })
      ).toBeInTheDocument();
    });
  });

  it("navigates to the new book page", async () => {
    renderHome();

    await waitFor(() => {
      expect(
        screen.getByRole("button", {
          name: /new book/i,
        })
      ).toBeInTheDocument();
    });

    fireEvent.click(
      screen.getByRole("button", {
        name: /new book/i,
      })
    );

    expect(
      window.location.pathname
    ).toBe("/");
  });

  it("renders the empty catalog message", async () => {
    vi.mocked(getBooks).mockResolvedValue([]);

    renderHome();

    await waitFor(() => {
      expect(
        screen.getByText("No books found.")
      ).toBeInTheDocument();
    });
  });

  it("deletes a book after confirmation", async () => {
    renderHome();

    await waitFor(() => {
      expect(
        screen.getByText("Clean Code")
      ).toBeInTheDocument();
    });

    fireEvent.click(
      screen.getByRole("button", {
        name: "Delete book",
      })
    );

    expect(
      screen.getByText(
        "Are you sure you want to delete this book?"
      )
    ).toBeInTheDocument();

    fireEvent.click(
      screen.getByRole("button", {
        name: "Delete",
      })
    );

    await waitFor(() => {
      expect(deleteBook).toHaveBeenCalledWith(
        "A12"
      );
    });
  });

  it("can close the delete confirmation dialog", async () => {
    renderHome();

    await waitFor(() => {
      expect(
        screen.getByText("Clean Code")
      ).toBeInTheDocument();
    });

    fireEvent.click(
      screen.getByRole("button", {
        name: "Delete book",
      })
    );

    expect(
      screen.getByText(
        "Are you sure you want to delete this book?"
      )
    ).toBeInTheDocument();

    fireEvent.click(
      screen.getByRole("button", {
        name: "Cancel",
      })
    );

    await waitFor(() => {
      expect(
        screen.queryByText(
          "Are you sure you want to delete this book?"
        )
      ).not.toBeInTheDocument();
    });

    expect(deleteBook).not.toHaveBeenCalled();
  });
});