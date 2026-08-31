import { useEffect, useState } from "react";
import {
  Alert,
  Box,
  CircularProgress,
} from "@mui/material";
import { useNavigate } from "react-router-dom";

import BookForm from "../components/BookForm";

import {
  Author,
  Genre,
  Publisher,
  BookRequest,
} from "../models/Book";

import { createBook } from "../api/bookService";
import { getAuthors } from "../api/authorService";
import { getGenres } from "../api/genreService";
import { getPublishers } from "../api/publisherService";

export default function CreateBook() {
  const navigate = useNavigate();

  const [authors, setAuthors] = useState<Author[]>([]);
  const [genres, setGenres] = useState<Genre[]>([]);
  const [publishers, setPublishers] = useState<Publisher[]>([]);

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const [error, setError] = useState("");

  useEffect(() => {
    const loadCatalogs = async () => {
      try {
        setLoading(true);

        const [
          authorsData,
          genresData,
          publishersData,
        ] = await Promise.all([
          getAuthors(),
          getGenres(),
          getPublishers(),
        ]);

        setAuthors(authorsData);
        setGenres(genresData);
        setPublishers(publishersData);
      } catch (err) {
        console.error(err);
        setError(
          "Unable to load catalog information."
        );
      } finally {
        setLoading(false);
      }
    };

    loadCatalogs();
  }, []);

  const handleSubmit = async (
    book: BookRequest
  ) => {
    try {
      setSaving(true);
      setError("");

      const created = await createBook(book);

      navigate(`/books/${created.id}`);
    } catch (err) {
      console.error(err);
      setError(
        "Unable to create the book."
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
        authors={authors}
        genres={genres}
        publishers={publishers}
        onSubmit={handleSubmit}
        onCancel={() => navigate("/")}
        loading={saving}
      />
    </Box>
  );
}