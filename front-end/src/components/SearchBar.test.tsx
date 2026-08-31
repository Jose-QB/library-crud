import {
  describe,
  it,
  expect,
  vi,
} from "vitest";

import {
  render,
  screen,
  fireEvent,
} from "@testing-library/react";

import SearchBar from "./SearchBar";

describe("SearchBar", () => {
  const authors = [
    {
      id: 1,
      name: "Gabriel García Márquez",
    },
  ];

  const genres = [
    {
      id: 1,
      name: "Fantasy",
    },
  ];

  it("renders search controls", () => {
    render(
      <SearchBar
        title=""
        authors={authors}
        genres={genres}
        selectedAuthors={[]}
        selectedGenres={[]}
        onTitleChange={vi.fn()}
        onAuthorsChange={vi.fn()}
        onGenresChange={vi.fn()}
        onSearch={vi.fn()}
        onClear={vi.fn()}
      />
    );

    expect(
      screen.getByLabelText("Search by title")
    ).toBeInTheDocument();

    expect(
      screen.getByRole("button", {
        name: /search/i,
      })
    ).toBeInTheDocument();

    expect(
      screen.getByRole("button", {
        name: /clear/i,
      })
    ).toBeInTheDocument();
  });

  it("calls onTitleChange", () => {
    const onTitleChange = vi.fn();

    render(
      <SearchBar
        title=""
        authors={authors}
        genres={genres}
        selectedAuthors={[]}
        selectedGenres={[]}
        onTitleChange={onTitleChange}
        onAuthorsChange={vi.fn()}
        onGenresChange={vi.fn()}
        onSearch={vi.fn()}
        onClear={vi.fn()}
      />
    );

    fireEvent.change(
      screen.getByLabelText("Search by title"),
      {
        target: {
          value: "Clean Code",
        },
      }
    );

    expect(onTitleChange).toHaveBeenCalledWith(
      "Clean Code"
    );
  });

  it("calls onSearch", () => {
    const onSearch = vi.fn();

    render(
      <SearchBar
        title=""
        authors={authors}
        genres={genres}
        selectedAuthors={[]}
        selectedGenres={[]}
        onTitleChange={vi.fn()}
        onAuthorsChange={vi.fn()}
        onGenresChange={vi.fn()}
        onSearch={onSearch}
        onClear={vi.fn()}
      />
    );

    fireEvent.click(
      screen.getByRole("button", {
        name: /search/i,
      })
    );

    expect(onSearch).toHaveBeenCalled();
  });

  it("calls onClear", () => {
    const onClear = vi.fn();

    render(
      <SearchBar
        title="Clean Code"
        authors={authors}
        genres={genres}
        selectedAuthors={[]}
        selectedGenres={[]}
        onTitleChange={vi.fn()}
        onAuthorsChange={vi.fn()}
        onGenresChange={vi.fn()}
        onSearch={vi.fn()}
        onClear={onClear}
      />
    );

    fireEvent.click(
      screen.getByRole("button", {
        name: /clear/i,
      })
    );

    expect(onClear).toHaveBeenCalled();
  });

  it("shows additional filters", () => {
    render(
      <SearchBar
        title=""
        authors={authors}
        genres={genres}
        selectedAuthors={[]}
        selectedGenres={[]}
        onTitleChange={vi.fn()}
        onAuthorsChange={vi.fn()}
        onGenresChange={vi.fn()}
        onSearch={vi.fn()}
        onClear={vi.fn()}
      />
    );

    fireEvent.click(
      screen.getByRole("button", {
        name: /more filters/i,
      })
    );

    expect(
      screen.getByLabelText("Authors")
    ).toBeInTheDocument();

    expect(
      screen.getByLabelText("Genres")
    ).toBeInTheDocument();
  });
});