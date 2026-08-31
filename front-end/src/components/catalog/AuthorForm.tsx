import { useEffect, useState } from "react";
import {
  Box,
  Button,
  Paper,
  TextField,
  Typography,
} from "@mui/material";

import {
  Author,
} from "../../models/Author";

import {
  AuthorRequest,
} from "../../api/authorService";

interface Props {
  initialData?: Author;
  onSubmit: (request: AuthorRequest) => void;
  onCancel: () => void;
  loading?: boolean;
}

const emptyAuthor: AuthorRequest = {
  name: "",
  country: "",
  birthDate: "",
};

export default function AuthorForm({
  initialData,
  onSubmit,
  onCancel,
  loading = false,
}: Props) {
  const [author, setAuthor] =
    useState<AuthorRequest>(emptyAuthor);

  const [errors, setErrors] =
    useState<Record<string, string>>({});

  useEffect(() => {
    if (!initialData) {
      setAuthor(emptyAuthor);
      return;
    }

    setAuthor({
      name: initialData.name ?? "",
      country: initialData.country ?? "",
      birthDate: initialData.birthDate ?? "",
    });
  }, [initialData]);

  const handleChange = (
    field: keyof AuthorRequest,
    value: string
  ) => {
    setAuthor((previous) => ({
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

    if (!author.name.trim()) {
      newErrors.name = "Name is required";
    } else if (author.name.length > 120) {
      newErrors.name =
        "Name cannot exceed 120 characters";
    }

    if (
      author.country &&
      author.country.length > 80
    ) {
      newErrors.country =
        "Country cannot exceed 80 characters";
    }

    setErrors(newErrors);

    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = () => {
    if (!validate()) {
      return;
    }

    onSubmit({
      name: author.name.trim(),
      country: author.country?.trim() || undefined,
      birthDate: author.birthDate || undefined,
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
        {initialData ? "Edit Author" : "New Author"}
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
          value={author.name}
          error={Boolean(errors.name)}
          helperText={errors.name}
          disabled={loading}
          inputProps={{
            maxLength: 120,
          }}
          onChange={(event) =>
            handleChange(
              "name",
              event.target.value
            )
          }
        />

        <TextField
          label="Country"
          fullWidth
          value={author.country ?? ""}
          error={Boolean(errors.country)}
          helperText={
            errors.country ?? "Optional"
          }
          disabled={loading}
          inputProps={{
            maxLength: 80,
          }}
          onChange={(event) =>
            handleChange(
              "country",
              event.target.value
            )
          }
        />

        <TextField
          label="Birth Date"
          type="date"
          fullWidth
          value={author.birthDate ?? ""}
          disabled={loading}
          slotProps={{
            inputLabel: {
              shrink: true,
            },
          }}
          onChange={(event) =>
            handleChange(
              "birthDate",
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
            ? "Update Author"
            : "Create Author"}
        </Button>
      </Box>
    </Paper>
  );
}