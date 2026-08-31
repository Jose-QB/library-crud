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
  waitFor,
  fireEvent,
} from "@testing-library/react";

import {
  MemoryRouter,
  Routes,
  Route,
} from "react-router-dom";

import BookDetail from "./BookDetail";

import { getBook } from "../api/bookService";

vi.mock("../api/bookService", () => ({
  getBook: vi.fn(),
}));

const mockBook = {
  id: "A12",
  title: "Clean Code",
  isbn: "9780132350884",
  publicationYear: 2008,
  edition: "1st",
  language: "English",
  pages: 464,
  stock: 5,
  shelfLocation: "A-12",
  publisher: {
    id: 1,
    name: "Prentice Hall",
    country: "United States",
    foundedYear: 1913,
  },
  authors: [
    {
      id: 1,
      name: "Robert C. Martin",
      country: "United States",
      birthDate: "1952-12-05",
    },
  ],
  genres: [
    {
      id: 1,
      name: "Programming",
      description: "Programming books",
    },
  ],
  createdAt: "2026-08-01T10:00:00",
  updatedAt: "2026-08-02T10:00:00",
};

function renderBookDetail(id = "A12") {
  return render(
    <MemoryRouter initialEntries={[`/books/${id}`]}>
      <Routes>
        <Route
          path="/books/:id"
          element={<BookDetail />}
        />

        <Route
          path="/"
          element={<div>Home Page</div>}
        />

        <Route
          path="/books/edit/:id"
          element={<div>Edit Book Page</div>}
        />
      </Routes>
    </MemoryRouter>
  );
}

describe("BookDetail", () => {
  beforeEach(() => {
    vi.clearAllMocks();

    vi.mocked(getBook).mockResolvedValue(
      mockBook
    );
  });

  it("loads and displays the book information", async () => {
    renderBookDetail();

    await waitFor(() => {
      expect(
        screen.getByRole("heading", {
          name: "Clean Code",
        })
      ).toBeInTheDocument();
    });

    expect(
      screen.getByText("A12")
    ).toBeInTheDocument();

    expect(
      screen.getByText("9780132350884")
    ).toBeInTheDocument();

    expect(
      screen.getByText("2008")
    ).toBeInTheDocument();

    expect(
      screen.getByText("Prentice Hall")
    ).toBeInTheDocument();

    expect(
      screen.getByText(/Robert C\. Martin/)
    ).toBeInTheDocument();

    expect(
      screen.getAllByText("Programming")
    ).toHaveLength(2);

    expect(getBook).toHaveBeenCalledWith(
      "A12"
    );
  });

  it("shows loading state", () => {
    vi.mocked(getBook).mockReturnValue(
      new Promise(() => {})
    );

    renderBookDetail();

    expect(
      screen.getByRole("progressbar")
    ).toBeInTheDocument();
  });

  it("shows an error when the book cannot be loaded", async () => {
    vi.mocked(getBook).mockRejectedValue(
      new Error("Network error")
    );

    renderBookDetail();

    await waitFor(() => {
      expect(
        screen.getByText(
          "Unable to load book information."
        )
      ).toBeInTheDocument();
    });
  });

  it("navigates back to home", async () => {
    renderBookDetail();

    await waitFor(() => {
      expect(
        screen.getByRole("heading", {
          name: "Clean Code",
        })
      ).toBeInTheDocument();
    });

    fireEvent.click(
      screen.getByRole("button", {
        name: /back/i,
      })
    );

    expect(
      screen.getByText("Home Page")
    ).toBeInTheDocument();
  });

  it("navigates to edit page", async () => {
    renderBookDetail();

    await waitFor(() => {
      expect(
        screen.getByRole("heading", {
          name: "Clean Code",
        })
      ).toBeInTheDocument();
    });

    fireEvent.click(
      screen.getByRole("button", {
        name: /edit/i,
      })
    );

    expect(
      screen.getByText("Edit Book Page")
    ).toBeInTheDocument();
  });
});