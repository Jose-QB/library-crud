import { useEffect, useState } from "react";
import {
  Box,
  Button,
  Paper,
  TextField,
  Typography,
} from "@mui/material";

import {
  Genre,
} from "../../models/Genre";

import {
  GenreRequest,
} from "../../api/genreService";

interface Props {
  initialData?: Genre;
  onSubmit: (request: GenreRequest) => void;
  onCancel: () => void;
  loading?: boolean;
}

const emptyGenre: GenreRequest = {
  name: "",
  description: "",
};

export default function GenreForm({
  initialData,
  onSubmit,
  onCancel,
  loading = false,
}: Props) {
  const [genre, setGenre] =
    useState<GenreRequest>(emptyGenre);

  const [errors, setErrors] =
    useState<Record<string, string>>({});

  useEffect(() => {
    if (!initialData) {
      setGenre(emptyGenre);
      return;
    }

    setGenre({
      name: initialData.name ?? "",
      description:
        initialData.description ?? "",
    });
  }, [initialData]);

  const handleChange = (
    field: keyof GenreRequest,
    value: string
  ) => {
    setGenre((previous) => ({
      ...previous,
      [field]: value,
    }));

    if (errors[field]) {
      setErrors((previous) => {
        const newErrors = { ...previous };
        delete newErrors[field];
        return newErrors;
      });
    }
  };

  const validate = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!genre.name.trim()) {
      newErrors.name = "Name is required";
    } else if (genre.name.length > 60) {
      newErrors.name =
        "Name cannot exceed 60 characters";
    }

    setErrors(newErrors);

    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = () => {
    if (!validate()) {
      return;
    }

    onSubmit({
      name: genre.name.trim(),
      description:
        genre.description?.trim() || undefined,
    });
  };

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
        fontWeight={600}
        mb={3}
      >
        {initialData ? "Edit Genre" : "New Genre"}
      </Typography>

      <Box
        display="flex"
        flexDirection="column"
        gap={2.5}
      >
        <TextField
          label="Name"
          fullWidth
          required
          value={genre.name}
          error={Boolean(errors.name)}
          helperText={errors.name}
          disabled={loading}
          inputProps={{
            maxLength: 60,
          }}
          onChange={(event) =>
            handleChange(
              "name",
              event.target.value
            )
          }
        />

        <TextField
          label="Description"
          fullWidth
          multiline
          minRows={4}
          value={genre.description ?? ""}
          disabled={loading}
          inputProps={{
            maxLength: 255,
          }}
          helperText="Optional"
          onChange={(event) =>
            handleChange(
              "description",
              event.target.value
            )
          }
        />
      </Box>

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
            ? "Update Genre"
            : "Create Genre"}
        </Button>
      </Box>
    </Paper>
  );
}