import { useEffect, useState } from "react";
import {
  Alert,
  Box,
  CircularProgress,
} from "@mui/material";
import {
  useNavigate,
  useParams,
} from "react-router-dom";

import BookForm from "../components/BookForm";

import {
  Author,
  Genre,
  Publisher,
  BookDetailResponse,
  BookRequest,
} from "../models/Book";

import {
  getBook,
  updateBook,
} from "../api/bookService";

import { getAuthors } from "../api/authorService";
import { getGenres } from "../api/genreService";
import { getPublishers } from "../api/publisherService";

export default function EditBook() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [book, setBook] =
    useState<BookDetailResponse | null>(null);

  const [authors, setAuthors] =
    useState<Author[]>([]);

  const [genres, setGenres] =
    useState<Genre[]>([]);

  const [publishers, setPublishers] =
    useState<Publisher[]>([]);

  const [loading, setLoading] =
    useState(true);

  const [saving, setSaving] =
    useState(false);

  const [error, setError] =
    useState("");

  useEffect(() => {
    if (!id) {
      setError("Book ID is missing.");
      setLoading(false);
      return;
    }

    const loadData = async () => {
      try {
        setLoading(true);

        const [
          bookData,
          authorsData,
          genresData,
          publishersData,
        ] = await Promise.all([
          getBook(id),
          getAuthors(),
          getGenres(),
          getPublishers(),
        ]);

        setBook(bookData);
        setAuthors(authorsData);
        setGenres(genresData);
        setPublishers(publishersData);
      } catch (err) {
        console.error(err);
        setError(
          "Unable to load book information."
        );
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, [id]);

  const handleSubmit = async (
    data: BookRequest
  ) => {
    if (!id) return;

    try {
      setSaving(true);
      setError("");

      const updated = await updateBook(
        id,
        data
      );

      navigate(`/books/${updated.id}`);
    } catch (err) {
      console.error(err);
      setError(
        "Unable to update the book."
      );
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <Box
        display="flex"
        justifyContent="center"
        py={8}
      >
        <CircularProgress />
      </Box>
    );
  }

  if (!book) {
    return (
      <Alert severity="error">
        {error || "Book not found."}
      </Alert>
    );
  }

  return (
    <Box>
      {error && (
        <Alert
          severity="error"
          sx={{ mb: 3 }}
          onClose={() => setError("")}
        >
          {error}
        </Alert>
      )}

      <BookForm
        initialData={book}
        authors={authors}
        genres={genres}
        publishers={publishers}
        onSubmit={handleSubmit}
        onCancel={() =>
          navigate(`/books/${id}`)
        }
        loading={saving}
      />
    </Box>
  );
}