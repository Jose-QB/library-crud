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

import BookTable from "./BookTable";

describe("BookTable", () => {
  const books = [
    {
      id: "A12",
      title: "Clean Code",
      authors: ["Robert C. Martin"],
      genres: ["Programming"],
      stock: 5,
    },
  ];

  it("renders books", () => {
    render(
      <BookTable
        books={books}
        onView={vi.fn()}
      />
    );

    expect(
      screen.getByText("Clean Code")
    ).toBeInTheDocument();

    expect(
      screen.getByText("Robert C. Martin")
    ).toBeInTheDocument();

    expect(
      screen.getByText("Programming")
    ).toBeInTheDocument();

    expect(
      screen.getByText("5")
    ).toBeInTheDocument();
  });

  it("shows empty state", () => {
    render(
      <BookTable
        books={[]}
        onView={vi.fn()}
      />
    );

    expect(
      screen.getByText("No books found.")
    ).toBeInTheDocument();
  });

  it("calls onView when view is clicked", () => {
    const onView = vi.fn();

    render(
      <BookTable
        books={books}
        onView={onView}
      />
    );

    fireEvent.click(
      screen.getByRole("button", {
        name: "View book",
      })
    );

    expect(onView).toHaveBeenCalledWith(
      "A12"
    );
  });

  it("calls onEdit when edit is clicked", () => {
    const onEdit = vi.fn();

    render(
      <BookTable
        books={books}
        onView={vi.fn()}
        onEdit={onEdit}
      />
    );

    fireEvent.click(
      screen.getByRole("button", {
        name: "Edit book",
      })
    );

    expect(onEdit).toHaveBeenCalledWith(
      "A12"
    );
  });

  it("calls onDelete when delete is clicked", () => {
    const onDelete = vi.fn();

    render(
      <BookTable
        books={books}
        onView={vi.fn()}
        onDelete={onDelete}
      />
    );

    fireEvent.click(
      screen.getByRole("button", {
        name: "Delete book",
      })
    );

    expect(onDelete).toHaveBeenCalledWith(
      "A12"
    );
  });
});