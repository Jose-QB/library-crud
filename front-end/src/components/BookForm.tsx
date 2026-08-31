import { useEffect, useState } from "react";
import {
  Autocomplete,
  Box,
  Button,
  Grid,
  MenuItem,
  Paper,
  TextField,
  Typography,
} from "@mui/material";

import {
  Author,
  BookDetailResponse,
  BookRequest,
  Genre,
  Publisher,
} from "../models/Book";

interface Props {
  initialData?: BookDetailResponse;
  authors: Author[];
  genres: Genre[];
  publishers: Publisher[];
  onSubmit: (book: BookRequest) => void;
  onCancel: () => void;
  loading?: boolean;
}

const emptyBook: BookRequest = {
  title: "",
  isbn: "",
  publicationYear: undefined,
  edition: "",
  language: "",
  pages: undefined,
  stock: 0,
  shelfLocation: "",
  publisherId: 0,
  authorIds: [],
  genreIds: [],
};

export default function BookForm({
  initialData,
  authors,
  genres,
  publishers,
  onSubmit,
  onCancel,
  loading = false,
}: Props) {
  const [book, setBook] = useState<BookRequest>(emptyBook);

  const [errors, setErrors] = useState<Record<string, string>>({});

  /*
   * Load initial data when editing an existing book.
   */
  useEffect(() => {
    if (!initialData) {
      setBook(emptyBook);
      return;
    }

    setBook({
      title: initialData.title ?? "",
      isbn: initialData.isbn ?? "",
      publicationYear: initialData.publicationYear,
      edition: initialData.edition ?? "",
      language: initialData.language ?? "",
      pages: initialData.pages,
      stock: initialData.stock ?? 0,
      shelfLocation: initialData.shelfLocation ?? "",
      publisherId: initialData.publisher?.id ?? 0,
      authorIds: initialData.authors.map((author) => author.id),
      genreIds: initialData.genres.map((genre) => genre.id),
    });
  }, [initialData]);

  /*
   * Generic field update.
   */
  const handleChange = <K extends keyof BookRequest>(
    field: K,
    value: BookRequest[K]
  ) => {
    setBook((previous) => ({
      ...previous,
      [field]: value,
    }));

    // Remove validation error when user modifies the field.
    if (errors[field]) {
      setErrors((previous) => {
        const newErrors = { ...previous };
        delete newErrors[field];
        return newErrors;
      });
    }
  };

  /*
   * Form validation.
   */
  const validate = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!book.title.trim()) {
      newErrors.title = "Title is required";
    } else if (book.title.length > 200) {
      newErrors.title = "Title cannot exceed 200 characters";
    }

    if (book.isbn && book.isbn.length > 17) {
      newErrors.isbn = "ISBN cannot exceed 17 characters";
    }

    if (
      book.publicationYear !== undefined &&
      (book.publicationYear < 1400 ||
        book.publicationYear > 2100)
    ) {
      newErrors.publicationYear =
        "Year must be between 1400 and 2100";
    }

    if (book.edition && book.edition.length > 30) {
      newErrors.edition =
        "Edition cannot exceed 30 characters";
    }

    if (book.language && book.language.length > 40) {
      newErrors.language =
        "Language cannot exceed 40 characters";
    }

    if (
      book.pages !== undefined &&
      book.pages <= 0
    ) {
      newErrors.pages =
        "Pages must be greater than zero";
    }

    if (
      book.stock !== undefined &&
      book.stock < 0
    ) {
      newErrors.stock =
        "Stock cannot be negative";
    }

    if (
      book.shelfLocation &&
      book.shelfLocation.length > 30
    ) {
      newErrors.shelfLocation =
        "Shelf location cannot exceed 30 characters";
    }

    if (!book.publisherId) {
      newErrors.publisherId =
        "Publisher is required";
    }

    if (book.authorIds.length === 0) {
      newErrors.authorIds =
        "At least one author is required";
    }

    if (book.genreIds.length === 0) {
      newErrors.genreIds =
        "At least one genre is required";
    }

    setErrors(newErrors);

    return Object.keys(newErrors).length === 0;
  };

  /*
   * Submit form.
   */
  const handleSubmit = () => {
    if (!validate()) {
      return;
    }

    onSubmit(book);
  };

  /*
   * Selected catalog objects for Autocomplete.
   */
  const selectedAuthors = authors.filter((author) =>
    book.authorIds.includes(author.id)
  );

  const selectedGenres = genres.filter((genre) =>
    book.genreIds.includes(genre.id)
  );

  return (
    <Paper
      elevation={2}
      sx={{
        p: { xs: 2, md: 4 },
      }}
    >
      <Typography
        variant="h5"
        component="h1"
        fontWeight="600"
        mb={3}
      >
        {initialData ? "Edit Book" : "New Book"}
      </Typography>

      <Grid container spacing={2.5}>

        {/* TITLE */}
        <Grid size={{ xs: 12 }}>
          <TextField
            label="Title"
            fullWidth
            required
            value={book.title}
            error={Boolean(errors.title)}
            helperText={errors.title}
            disabled={loading}
            inputProps={{
              maxLength: 200,
            }}
            onChange={(event) =>
              handleChange(
                "title",
                event.target.value
              )
            }
          />
        </Grid>

        {/* ISBN */}
        <Grid size={{ xs: 12, md: 6 }}>
          <TextField
            label="ISBN"
            fullWidth
            value={book.isbn ?? ""}
            error={Boolean(errors.isbn)}
            helperText={
              errors.isbn ?? "Optional"
            }
            disabled={loading}
            inputProps={{
              maxLength: 17,
            }}
            onChange={(event) =>
              handleChange(
                "isbn",
                event.target.value
              )
            }
          />
        </Grid>

        {/* EDITION */}
        <Grid size={{ xs: 12, md: 6 }}>
          <TextField
            label="Edition"
            fullWidth
            value={book.edition ?? ""}
            error={Boolean(errors.edition)}
            helperText={
              errors.edition ?? "Optional"
            }
            disabled={loading}
            inputProps={{
              maxLength: 30,
            }}
            onChange={(event) =>
              handleChange(
                "edition",
                event.target.value
              )
            }
          />
        </Grid>

        {/* PUBLICATION YEAR */}
        <Grid size={{ xs: 12, md: 4 }}>
          <TextField
            label="Publication Year"
            type="number"
            fullWidth
            value={
              book.publicationYear ?? ""
            }
            error={Boolean(
              errors.publicationYear
            )}
            helperText={
              errors.publicationYear ??
              "Between 1400 and 2100"
            }
            disabled={loading}
            slotProps={{
              htmlInput: {
                min: 1400,
                max: 2100,
              },
            }}
            onChange={(event) => {
              const value =
                event.target.value;

              handleChange(
                "publicationYear",
                value === ""
                  ? undefined
                  : Number(value)
              );
            }}
          />
        </Grid>

        {/* LANGUAGE */}
        <Grid size={{ xs: 12, md: 4 }}>
          <TextField
            select
            label="Language"
            fullWidth
            value={book.language ?? ""}
            error={Boolean(errors.language)}
            helperText={errors.language}
            disabled={loading}
            onChange={(event) =>
              handleChange(
                "language",
                event.target.value
              )
            }
          >
            <MenuItem value="">
              Not specified
            </MenuItem>

            <MenuItem value="English">
              English
            </MenuItem>

            <MenuItem value="Spanish">
              Spanish
            </MenuItem>

            <MenuItem value="French">
              French
            </MenuItem>

            <MenuItem value="German">
              German
            </MenuItem>

            <MenuItem value="Italian">
              Italian
            </MenuItem>

            <MenuItem value="Portuguese">
              Portuguese
            </MenuItem>

            <MenuItem value="Japanese">
              Japanese
            </MenuItem>
          </TextField>
        </Grid>

        {/* PAGES */}
        <Grid size={{ xs: 12, md: 4 }}>
          <TextField
            label="Pages"
            type="number"
            fullWidth
            value={book.pages ?? ""}
            error={Boolean(errors.pages)}
            helperText={errors.pages}
            disabled={loading}
            slotProps={{
              htmlInput: {
                min: 1,
              },
            }}
            onChange={(event) => {
              const value =
                event.target.value;

              handleChange(
                "pages",
                value === ""
                  ? undefined
                  : Number(value)
              );
            }}
          />
        </Grid>

        {/* STOCK */}
        <Grid size={{ xs: 12, md: 6 }}>
          <TextField
            label="Stock"
            type="number"
            fullWidth
            value={book.stock ?? ""}
            error={Boolean(errors.stock)}
            helperText={
              errors.stock ??
              "Must be zero or greater"
            }
            disabled={loading}
            slotProps={{
              htmlInput: {
                min: 0,
              },
            }}
            onChange={(event) => {
              const value =
                event.target.value;

              handleChange(
                "stock",
                value === ""
                  ? 0
                  : Number(value)
              );
            }}
          />
        </Grid>

        {/* SHELF */}
        <Grid size={{ xs: 12, md: 6 }}>
          <TextField
            label="Shelf Location"
            fullWidth
            placeholder="A-01"
            value={
              book.shelfLocation ?? ""
            }
            error={Boolean(
              errors.shelfLocation
            )}
            helperText={
              errors.shelfLocation
            }
            disabled={loading}
            inputProps={{
              maxLength: 30,
            }}
            onChange={(event) =>
              handleChange(
                "shelfLocation",
                event.target.value
              )
            }
          />
        </Grid>

        {/* PUBLISHER */}
        <Grid size={{ xs: 12 }}>
          <TextField
            select
            label="Publisher"
            fullWidth
            required
            value={
              book.publisherId || ""
            }
            error={Boolean(
              errors.publisherId
            )}
            helperText={
              errors.publisherId
            }
            disabled={loading}
            onChange={(event) =>
              handleChange(
                "publisherId",
                Number(event.target.value)
              )
            }
          >
            <MenuItem value="">
              Select a publisher
            </MenuItem>

            {publishers.map((publisher) => (
              <MenuItem
                key={publisher.id}
                value={publisher.id}
              >
                {publisher.name}
              </MenuItem>
            ))}
          </TextField>
        </Grid>

        {/* AUTHORS */}
        <Grid size={{ xs: 12 }}>
          <Autocomplete
            multiple
            options={authors}
            value={selectedAuthors}
            disabled={loading}
            getOptionLabel={(author) =>
              author.name
            }
            isOptionEqualToValue={(
              option,
              value
            ) => option.id === value.id}
            onChange={(_, values) => {
              handleChange(
                "authorIds",
                values.map(
                  (author) => author.id
                )
              );
            }}
            renderInput={(params) => (
              <TextField
                {...params}
                label="Authors"
                required
                error={Boolean(
                  errors.authorIds
                )}
                helperText={
                  errors.authorIds
                }
              />
            )}
          />
        </Grid>

        {/* GENRES */}
        <Grid size={{ xs: 12 }}>
          <Autocomplete
            multiple
            options={genres}
            value={selectedGenres}
            disabled={loading}
            getOptionLabel={(genre) =>
              genre.name
            }
            isOptionEqualToValue={(
              option,
              value
            ) => option.id === value.id}
            onChange={(_, values) => {
              handleChange(
                "genreIds",
                values.map(
                  (genre) => genre.id
                )
              );
            }}
            renderInput={(params) => (
              <TextField
                {...params}
                label="Genres"
                required
                error={Boolean(
                  errors.genreIds
                )}
                helperText={
                  errors.genreIds
                }
              />
            )}
          />
        </Grid>
      </Grid>

      {/* ACTIONS */}
      <Box
        mt={4}
        display="flex"
        justifyContent="flex-end"
        gap={2}
      >
        <Button
          variant="outlined"
          onClick={onCancel}
          disabled={loading}
        >
          Cancel
        </Button>

        <Button
          variant="contained"
          onClick={handleSubmit}
          loading={loading}
        >
          {initialData
            ? "Update Book"
            : "Create Book"}
        </Button>
      </Box>
    </Paper>
  );
}