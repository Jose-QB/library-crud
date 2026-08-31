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
  deleteGenre,
  getGenres,
} from "../../api/genreService";

import { Genre } from "../../models/Genre";

import { useAuth } from "../../auth/AuthContext";

export default function Genres() {
  const navigate = useNavigate();

  const { role } = useAuth();

  const isAdmin = role === "ADMIN";

  const [genres, setGenres] =
    useState<Genre[]>([]);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState<string | null>(null);

  const [deleteLoading, setDeleteLoading] =
    useState<number | null>(null);

  const loadGenres = async () => {
    try {
      setLoading(true);
      setError(null);

      const data = await getGenres();

      setGenres(data);
    } catch {
      setError(
        "Unable to load genres."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadGenres();
  }, []);

  const handleDelete = async (
    genre: Genre
  ) => {
    const confirmed = window.confirm(
      `Are you sure you want to delete "${genre.name}"?`
    );

    if (!confirmed) {
      return;
    }

    try {
      setDeleteLoading(genre.id);
      setError(null);

      await deleteGenre(genre.id);

      setGenres((previous) =>
        previous.filter(
          (item) => item.id !== genre.id
        )
      );
    } catch (error: any) {
      if (error?.response?.status === 409) {
        setError(
          "This genre cannot be deleted because it is associated with one or more books."
        );
      } else if (
        error?.response?.status === 403
      ) {
        setError(
          "You do not have permission to delete genres."
        );
      } else if (
        error?.response?.status === 404
      ) {
        setError("Genre was not found.");
      } else {
        setError(
          "Unable to delete the genre."
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
          Genres
        </Typography>

        <Button
          variant="contained"
          startIcon={<Add />}
          onClick={() =>
            navigate("/genres/new")
          }
        >
          New Genre
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
                  <strong>Description</strong>
                </TableCell>

                <TableCell align="right">
                  <strong>Actions</strong>
                </TableCell>
              </TableRow>
            </TableHead>

            <TableBody>
              {genres.length === 0 ? (
                <TableRow>
                  <TableCell
                    colSpan={3}
                    align="center"
                  >
                    No genres found.
                  </TableCell>
                </TableRow>
              ) : (
                genres.map((genre) => (
                  <TableRow
                    key={genre.id}
                    hover
                  >
                    <TableCell>
                      {genre.name}
                    </TableCell>

                    <TableCell>
                      {genre.description ||
                        "—"}
                    </TableCell>

                    <TableCell align="right">
                      {/* USER + ADMIN */}
                      <IconButton
                        color="primary"
                        onClick={() =>
                          navigate(
                            `/genres/edit/${genre.id}`
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
                            handleDelete(genre)
                          }
                          disabled={
                            deleteLoading !== null
                          }
                        >
                          {deleteLoading ===
                          genre.id ? (
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