import { useEffect, useState } from "react";
import {
  Alert,
  Box,
  Button,
  Chip,
  CircularProgress,
  Divider,
  Paper,
  Stack,
  Typography,
} from "@mui/material";

import EditIcon from "@mui/icons-material/Edit";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";

import {
  useNavigate,
  useParams,
} from "react-router-dom";

import {
  BookDetailResponse,
} from "../models/Book";

import { getBook } from "../api/bookService";

import { useAuth } from "../auth/AuthContext";

export default function BookDetail() {
  const { id } = useParams();
  const navigate = useNavigate();

  const { isAuthenticated } = useAuth();

  const [book, setBook] =
    useState<BookDetailResponse | null>(null);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState("");

  useEffect(() => {
    if (!id) {
      setError("Book ID is missing.");
      setLoading(false);
      return;
    }

    const loadBook = async () => {
      try {
        setLoading(true);

        const data = await getBook(id);

        setBook(data);
      } catch (err) {
        console.error(err);
        setError(
          "Unable to load book information."
        );
      } finally {
        setLoading(false);
      }
    };

    loadBook();
  }, [id]);

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
      <Box
        display="flex"
        justifyContent="space-between"
        alignItems="center"
        mb={3}
      >
        <Button
          startIcon={<ArrowBackIcon />}
          onClick={() => navigate("/")}
        >
          Back
        </Button>

        {isAuthenticated && (
          <Button
            variant="contained"
            startIcon={<EditIcon />}
            onClick={() =>
              navigate(`/books/edit/${book.id}`)
            }
          >
            Edit
          </Button>
        )}
      </Box>

      <Paper sx={{ p: { xs: 2, md: 4 } }}>
        <Box mb={3}>
          <Typography
            variant="overline"
            color="text.secondary"
          >
            Book ID
          </Typography>

          <Typography
            variant="h6"
            fontWeight="600"
          >
            {book.id}
          </Typography>
        </Box>

        <Typography
          variant="h3"
          component="h1"
          fontWeight="600"
          gutterBottom
        >
          {book.title}
        </Typography>

        <Stack
          direction="row"
          spacing={1}
          flexWrap="wrap"
          useFlexGap
          mb={3}
        >
          {book.genres.map((genre) => (
            <Chip
              key={genre.id}
              label={genre.name}
            />
          ))}
        </Stack>

        <Divider sx={{ mb: 3 }} />

        <Box
          display="grid"
          gridTemplateColumns={{
            xs: "1fr",
            md: "1fr 1fr",
          }}
          gap={3}
        >
          <InfoItem
            label="ISBN"
            value={book.isbn}
          />

          <InfoItem
            label="Publication Year"
            value={book.publicationYear}
          />

          <InfoItem
            label="Edition"
            value={book.edition}
          />

          <InfoItem
            label="Language"
            value={book.language}
          />

          <InfoItem
            label="Pages"
            value={book.pages}
          />

          <InfoItem
            label="Stock"
            value={book.stock}
          />

          <InfoItem
            label="Shelf Location"
            value={book.shelfLocation}
          />

          <InfoItem
            label="Publisher"
            value={book.publisher?.name}
          />
        </Box>

        <Divider sx={{ my: 4 }} />

        <Typography
          variant="h6"
          fontWeight="600"
          mb={2}
        >
          Authors
        </Typography>

        <Stack spacing={1} mb={4}>
          {book.authors.map((author) => (
            <Typography key={author.id}>
              {author.name}
              {author.country &&
                ` — ${author.country}`}
            </Typography>
          ))}
        </Stack>

        <Typography
          variant="h6"
          fontWeight="600"
          mb={2}
        >
          Genres
        </Typography>

        <Stack
          direction="row"
          spacing={1}
          flexWrap="wrap"
          useFlexGap
        >
          {book.genres.map((genre) => (
            <Chip
              key={genre.id}
              label={genre.name}
              variant="outlined"
            />
          ))}
        </Stack>

        <Divider sx={{ my: 4 }} />

        <Typography
          variant="caption"
          color="text.secondary"
        >
          Created:{" "}
          {new Date(
            book.createdAt
          ).toLocaleString()}
        </Typography>

        <br />

        <Typography
          variant="caption"
          color="text.secondary"
        >
          Last updated:{" "}
          {new Date(
            book.updatedAt
          ).toLocaleString()}
        </Typography>
      </Paper>
    </Box>
  );
}

interface InfoItemProps {
  label: string;
  value?: string | number;
}

function InfoItem({
  label,
  value,
}: InfoItemProps) {
  return (
    <Box>
      <Typography
        variant="caption"
        color="text.secondary"
      >
        {label}
      </Typography>

      <Typography variant="body1">
        {value ?? "Not specified"}
      </Typography>
    </Box>
  );
}