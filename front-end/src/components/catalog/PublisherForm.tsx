import { useEffect, useState } from "react";
import {
  Box,
  Button,
  Paper,
  TextField,
  Typography,
} from "@mui/material";

import {
  Publisher,
} from "../../models/Publisher";

import {
  PublisherRequest,
} from "../../api/publisherService";

interface Props {
  initialData?: Publisher;
  onSubmit: (request: PublisherRequest) => void;
  onCancel: () => void;
  loading?: boolean;
}

const emptyPublisher: PublisherRequest = {
  name: "",
  country: "",
  foundedYear: undefined,
};

export default function PublisherForm({
  initialData,
  onSubmit,
  onCancel,
  loading = false,
}: Props) {
  const [publisher, setPublisher] =
    useState<PublisherRequest>(
      emptyPublisher
    );

  const [errors, setErrors] =
    useState<Record<string, string>>({});

  useEffect(() => {
    if (!initialData) {
      setPublisher(emptyPublisher);
      return;
    }

    setPublisher({
      name: initialData.name ?? "",
      country: initialData.country ?? "",
      foundedYear:
        initialData.foundedYear ??
        undefined,
    });
  }, [initialData]);

  const handleChange = (
    field: keyof PublisherRequest,
    value: string | number | undefined
  ) => {
    setPublisher((previous) => ({
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

    if (!publisher.name.trim()) {
      newErrors.name = "Name is required";
    } else if (publisher.name.length > 120) {
      newErrors.name =
        "Name cannot exceed 120 characters";
    }

    if (
      publisher.country &&
      publisher.country.length > 80
    ) {
      newErrors.country =
        "Country cannot exceed 80 characters";
    }

    if (
      publisher.foundedYear !== undefined &&
      (publisher.foundedYear < 1400 ||
        publisher.foundedYear > 2100)
    ) {
      newErrors.foundedYear =
        "Year must be between 1400 and 2100";
    }

    setErrors(newErrors);

    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = () => {
    if (!validate()) {
      return;
    }

    onSubmit({
      name: publisher.name.trim(),
      country:
        publisher.country?.trim() || undefined,
      foundedYear:
        publisher.foundedYear,
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
        {initialData
          ? "Edit Publisher"
          : "New Publisher"}
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
          value={publisher.name}
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
          value={publisher.country ?? ""}
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
          label="Founded Year"
          type="number"
          fullWidth
          value={
            publisher.foundedYear ?? ""
          }
          error={Boolean(
            errors.foundedYear
          )}
          helperText={
            errors.foundedYear ??
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
              "foundedYear",
              value === ""
                ? undefined
                : Number(value)
            );
          }}
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
            ? "Update Publisher"
            : "Create Publisher"}
        </Button>
      </Box>
    </Paper>
  );
}