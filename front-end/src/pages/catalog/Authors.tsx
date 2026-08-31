import { useEffect, useState } from "react";
import {
  Alert,
  Box,
  Button,
  CircularProgress,
  IconButton,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from "@mui/material";

import {
  Add,
  Delete,
  Edit,
} from "@mui/icons-material";

import { useNavigate } from "react-router-dom";

import {
  deleteAuthor,
  getAuthors,
} from "../../api/authorService";

import { Author } from "../../models/Author";

import { useAuth } from "../../auth/AuthContext";

export default function Authors() {
  const navigate = useNavigate();

  const { role } = useAuth();

  const isAdmin = role === "ADMIN";

  const [authors, setAuthors] =
    useState<Author[]>([]);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState<string | null>(null);

  const [deleteLoading, setDeleteLoading] =
    useState<number | null>(null);

  const loadAuthors = async () => {
    try {
      setLoading(true);
      setError(null);

      const data = await getAuthors();

      setAuthors(data);
    } catch {
      setError(
        "Unable to load authors."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadAuthors();
  }, []);

  const handleDelete = async (
    author: Author
  ) => {
    const confirmed = window.confirm(
      `Are you sure you want to delete "${author.name}"?`
    );

    if (!confirmed) {
      return;
    }

    try {
      setDeleteLoading(author.id);
      setError(null);

      await deleteAuthor(author.id);

      setAuthors((previous) =>
        previous.filter(
          (item) => item.id !== author.id
        )
      );
    } catch (error: any) {
      if (error?.response?.status === 409) {
        setError(
          "This author cannot be deleted because it is associated with one or more books."
        );
      } else if (
        error?.response?.status === 403
      ) {
        setError(
          "You do not have permission to delete authors."
        );
      } else if (
        error?.response?.status === 404
      ) {
        setError("Author was not found.");
      } else {
        setError(
          "Unable to delete the author."
        );
      }
    } finally {
      setDeleteLoading(null);
    }
  };

  return (
    <Box>
      <Box
        display="flex"
        justifyContent="space-between"
        alignItems="center"
        mb={3}
        gap={2}
      >
        <Typography
          variant="h4"
          component="h1"
          fontWeight={600}
        >
          Authors
        </Typography>

        <Button
          variant="contained"
          startIcon={<Add />}
          onClick={() =>
            navigate("/authors/new")
          }
        >
          New Author
        </Button>
      </Box>

      {error && (
        <Alert
          severity="error"
          sx={{ mb: 2 }}
          onClose={() => setError(null)}
        >
          {error}
        </Alert>
      )}

      {loading ? (
        <Box
          display="flex"
          justifyContent="center"
          py={8}
        >
          <CircularProgress />
        </Box>
      ) : (
        <TableContainer
          component={Paper}
        >
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>
                  <strong>Name</strong>
                </TableCell>

                <TableCell>
                  <strong>Country</strong>
                </TableCell>

                <TableCell>
                  <strong>Birth Date</strong>
                </TableCell>

                <TableCell align="right">
                  <strong>Actions</strong>
                </TableCell>
              </TableRow>
            </TableHead>

            <TableBody>
              {authors.length === 0 ? (
                <TableRow>
                  <TableCell
                    colSpan={4}
                    align="center"
                  >
                    No authors found.
                  </TableCell>
                </TableRow>
              ) : (
                authors.map((author) => (
                  <TableRow
                    key={author.id}
                    hover
                  >
                    <TableCell>
                      {author.name}
                    </TableCell>

                    <TableCell>
                      {author.country || "—"}
                    </TableCell>

                    <TableCell>
                      {author.birthDate || "—"}
                    </TableCell>

                    <TableCell align="right">
                      {/* USER + ADMIN */}
                      <IconButton
                        color="primary"
                        onClick={() =>
                          navigate(
                            `/authors/edit/${author.id}`
                          )
                        }
                        disabled={
                          deleteLoading !== null
                        }
                      >
                        <Edit />
                      </IconButton>

                      {/* ADMIN ONLY */}
                      {isAdmin && (
                        <IconButton
                          color="error"
                          onClick={() =>
                            handleDelete(author)
                          }
                          disabled={
                            deleteLoading !== null
                          }
                        >
                          {deleteLoading ===
                          author.id ? (
                            <CircularProgress
                              size={24}
                            />
                          ) : (
                            <Delete />
                          )}
                        </IconButton>
                      )}
                    </TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
        </TableContainer>
      )}
    </Box>
  );
}
