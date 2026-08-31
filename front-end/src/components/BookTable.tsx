import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  IconButton,
  Chip,
  Paper,
  TableContainer,
} from "@mui/material";

import VisibilityIcon from "@mui/icons-material/Visibility";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";

import { BookResponse } from "../models/Book";

interface Props {
  books: BookResponse[];

  onView: (id: string) => void;

  onEdit?: (id: string) => void;

  onDelete?: (id: string) => void;
}

export default function BookTable({
  books,
  onView,
  onEdit,
  onDelete,
}: Props) {
  return (
    <TableContainer component={Paper}>
      <Table>
        <TableHead>
          <TableRow>
            <TableCell>ID</TableCell>
            <TableCell>Title</TableCell>
            <TableCell>Authors</TableCell>
            <TableCell>Genres</TableCell>
            <TableCell align="center">
              Stock
            </TableCell>
            <TableCell align="center">
              Actions
            </TableCell>
          </TableRow>
        </TableHead>

        <TableBody>
          {books.map((book) => (
            <TableRow
              key={book.id}
              hover
            >
              <TableCell>
                {book.id}
              </TableCell>

              <TableCell>
                {book.title}
              </TableCell>

              <TableCell>
                {book.authors.join(", ")}
              </TableCell>

              <TableCell>
                {book.genres.map((genre) => (
                  <Chip
                    key={genre}
                    label={genre}
                    size="small"
                    sx={{ mr: 0.5 }}
                  />
                ))}
              </TableCell>

              <TableCell align="center">
                {book.stock}
              </TableCell>

              <TableCell align="center">
                <IconButton
                  color="primary"
                  onClick={() =>
                    onView(book.id)
                  }
                  aria-label="View book"
                >
                  <VisibilityIcon />
                </IconButton>

                {onEdit && (
                  <IconButton
                    color="warning"
                    onClick={() =>
                      onEdit(book.id)
                    }
                    aria-label="Edit book"
                  >
                    <EditIcon />
                  </IconButton>
                )}

                {onDelete && (
                  <IconButton
                    color="error"
                    onClick={() =>
                      onDelete(book.id)
                    }
                    aria-label="Delete book"
                  >
                    <DeleteIcon />
                  </IconButton>
                )}
              </TableCell>
            </TableRow>
          ))}

          {books.length === 0 && (
            <TableRow>
              <TableCell
                colSpan={6}
                align="center"
              >
                No books found.
              </TableCell>
            </TableRow>
          )}
        </TableBody>
      </Table>
    </TableContainer>
  );
}