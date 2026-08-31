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

import ConfirmDialog from "./ConfirmDialog";

describe("ConfirmDialog", () => {
  it("renders when open", () => {
    render(
      <ConfirmDialog
        open={true}
        title="Delete book"
        message="Are you sure?"
        onCancel={vi.fn()}
        onConfirm={vi.fn()}
      />
    );

    expect(
      screen.getByText("Delete book")
    ).toBeInTheDocument();

    expect(
      screen.getByText("Are you sure?")
    ).toBeInTheDocument();
  });

  it("calls onCancel", () => {
    const onCancel = vi.fn();

    render(
      <ConfirmDialog
        open={true}
        title="Delete book"
        message="Are you sure?"
        onCancel={onCancel}
        onConfirm={vi.fn()}
      />
    );

    fireEvent.click(
      screen.getByRole("button", {
        name: "Cancel",
      })
    );

    expect(onCancel).toHaveBeenCalled();
  });

  it("calls onConfirm", () => {
    const onConfirm = vi.fn();

    render(
      <ConfirmDialog
        open={true}
        title="Delete book"
        message="Are you sure?"
        onCancel={vi.fn()}
        onConfirm={onConfirm}
      />
    );

    fireEvent.click(
      screen.getByRole("button", {
        name: "Delete",
      })
    );

    expect(onConfirm).toHaveBeenCalled();
  });
});