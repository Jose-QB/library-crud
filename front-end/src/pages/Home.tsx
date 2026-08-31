import { useEffect, useState } from "react";

import {
  Alert,
  Box,
  Button,
  CircularProgress,
  Typography,
} from "@mui/material";

import AddIcon from "@mui/icons-material/Add";

import { useNavigate } from "react-router-dom";

import SearchBar from "../components/SearchBar";
import BookTable from "../components/BookTable";
import ConfirmDialog from "../components/ConfirmDialog";

import {
  Author,
  BookResponse,
  Genre,
} from "../models/Book";

import {
  deleteBook,
  getBooks,
} from "../api/bookService";

import { getAuthors } from "../api/authorService";
import { getGenres } from "../api/genreService";

const MAX_BOOKS = 2600;

export default function Home() {
  const navigate = useNavigate();

  const [books, setBooks] =
    useState<BookResponse[]>([]);

  const [authors, setAuthors] =
    useState<Author[]>([]);

  const [genres, setGenres] =
    useState<Genre[]>([]);

  const [title, setTitle] =
    useState("");

  const [selectedAuthors, setSelectedAuthors] =
    useState<Author[]>([]);

  const [selectedGenres, setSelectedGenres] =
    useState<Genre[]>([]);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState("");

  const [deleteId, setDeleteId] =
    useState<string | null>(null);

  const [libraryFull, setLibraryFull] =
    useState(false);

  /**
   * Loads books using the current search filters.
   */
  const loadBooks = async () => {
    try {
      setLoading(true);
      setError("");

      const data = await getBooks(
        title,
        selectedAuthors.map(
          (author) => author.id
        ),
        selectedGenres.map(
          (genre) => genre.id
        )
      );

      setBooks(data);
    } catch (err) {
      console.error(err);
      setError(
        "Unable to load books."
      );
    } finally {
      setLoading(false);
    }
  };

  /**
   * Loads the complete catalog and updates
   * the library capacity status.
   */
  const loadCompleteCatalog = async () => {
    const data = await getBooks();

    setBooks(data);
    setLibraryFull(
      data.length >= MAX_BOOKS
    );

    return data;
  };

  /**
   * Initial page load.
   */
  useEffect(() => {
    const initialize = async () => {
      setLoading(true);
      setError("");

      try {
        const [
          authorData,
          genreData,
          bookData,
        ] = await Promise.all([
          getAuthors(),
          getGenres(),
          getBooks(),
        ]);

        setAuthors(authorData);
        setGenres(genreData);
        setBooks(bookData);

        setLibraryFull(
          bookData.length >= MAX_BOOKS
        );
      } catch (err) {
        console.error(err);
        setError(
          "Unable to load library information."
        );
      } finally {
        setLoading(false);
      }
    };

    initialize();
  }, []);

  /**
   * Clears all filters and reloads
   * the complete catalog.
   */
  const handleClear = async () => {
    setTitle("");
    setSelectedAuthors([]);
    setSelectedGenres([]);

    try {
      setLoading(true);
      setError("");

      await loadCompleteCatalog();
    } catch (err) {
      console.error(err);
      setError(
        "Unable to load books."
      );
    } finally {
      setLoading(false);
    }
  };

  /**
   * Deletes the selected book.
   */
  const handleDelete = async () => {
    if (!deleteId) {
      return;
    }

    try {
      setLoading(true);
      setError("");

      await deleteBook(deleteId);

      setDeleteId(null);

      /*
       * Reload the current search results.
       */
      await loadBooks();

      /*
       * Check the actual total number of
       * books after the deletion.
       */
      const completeCatalog =
        await getBooks();

      setLibraryFull(
        completeCatalog.length >= MAX_BOOKS
      );
    } catch (err) {
      console.error(err);
      setError(
        "Unable to delete the book."
      );
      setLoading(false);
    }
  };

  /**
   * Opens the book detail page.
   */
  const handleView = (
    id: string
  ) => {
    navigate(`/books/${id}`);
  };

  /**
   * Opens the book edit page.
   */
  const handleEdit = (
    id: string
  ) => {
    navigate(`/books/edit/${id}`);
  };

  /**
   * Opens the delete confirmation dialog.
   */
  const handleDeleteRequest = (
    id: string
  ) => {
    setDeleteId(id);
  };

  return (
    <Box>
      <Box
        display="flex"
        justifyContent="space-between"
        alignItems="center"
        mb={3}
      >
        <Box>
          <Typography
            variant="h4"
            component="h1"
            fontWeight="600"
          >
            Library
          </Typography>

          <Typography color="text.secondary">
            Manage your book catalog
          </Typography>
        </Box>

        {!libraryFull && (
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={() =>
              navigate("/books/new")
            }
          >
            New Book
          </Button>
        )}
      </Box>

      {libraryFull && (
        <Alert
          severity="info"
          sx={{ mb: 3 }}
        >
          The library has reached its
          maximum capacity of{" "}
          {MAX_BOOKS} books. No more
          books can be added.
        </Alert>
      )}

      {error && (
        <Alert
          severity="error"
          sx={{ mb: 3 }}
          onClose={() =>
            setError("")
          }
        >
          {error}
        </Alert>
      )}

      <SearchBar
        title={title}
        authors={authors}
        genres={genres}
        selectedAuthors={
          selectedAuthors
        }
        selectedGenres={
          selectedGenres
        }
        onTitleChange={
          setTitle
        }
        onAuthorsChange={
          setSelectedAuthors
        }
        onGenresChange={
          setSelectedGenres
        }
        onSearch={loadBooks}
        onClear={handleClear}
      />

      {loading ? (
        <Box
          display="flex"
          justifyContent="center"
          py={8}
        >
          <CircularProgress />
        </Box>
      ) : (
        <BookTable
          books={books}
          onView={handleView}
          onEdit={handleEdit}
          onDelete={handleDeleteRequest}
        />
      )}

      <ConfirmDialog
        open={
          deleteId !== null
        }
        title="Delete book"
        message="Are you sure you want to delete this book?"
        onCancel={() =>
          setDeleteId(null)
        }
        onConfirm={
          handleDelete
        }
      />
    </Box>
  );
}
