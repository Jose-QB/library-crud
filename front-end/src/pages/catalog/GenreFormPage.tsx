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

import GenreForm from "../../components/catalog/GenreForm";

import {
  createGenre,
  getGenre,
  updateGenre,
  GenreRequest,
} from "../../api/genreService";

import { Genre } from "../../models/Genre";

export default function GenreFormPage() {
  const navigate = useNavigate();
  const { id } = useParams();

  const isEdit = Boolean(id);

  const [genre, setGenre] =
    useState<Genre | undefined>();

  const [loading, setLoading] =
    useState(isEdit);

  const [saving, setSaving] =
    useState(false);

  const [error, setError] =
    useState<string | null>(null);

  useEffect(() => {
    if (!id) {
      return;
    }

    const loadGenre = async () => {
      try {
        setLoading(true);
        setError(null);

        const data = await getGenre(
          Number(id)
        );

        setGenre(data);
      } catch (error: any) {
        if (
          error?.response?.status === 404
        ) {
          setError("Genre was not found.");
        } else {
          setError(
            "Unable to load the genre."
          );
        }
      } finally {
        setLoading(false);
      }
    };

    loadGenre();
  }, [id]);

  const handleSubmit = async (
    request: GenreRequest
  ) => {
    try {
      setSaving(true);
      setError(null);

      if (isEdit && id) {
        await updateGenre(
          Number(id),
          request
        );
      } else {
        await createGenre(request);
      }

      navigate("/genres");
    } catch (error: any) {
      const status =
        error?.response?.status;

      if (status === 409) {
        setError(
          "A genre with the same name already exists."
        );
      } else if (status === 400) {
        setError(
          "The submitted data is invalid."
        );
      } else if (status === 403) {
        setError(
          "You do not have permission to perform this action."
        );
      } else {
        setError(
          isEdit
            ? "Unable to update the genre."
            : "Unable to create the genre."
        );
      }
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

  if (error && isEdit && !genre) {
    return (
      <Alert severity="error">
        {error}
      </Alert>
    );
  }

  return (
    <Box>
      {error && (
        <Alert
          severity="error"
          sx={{ mb: 2 }}
          onClose={() => setError(null)}
        >
          {error}
        </Alert>
      )}

      <GenreForm
        initialData={genre}
        onSubmit={handleSubmit}
        onCancel={() =>
          navigate("/genres")
        }
        loading={saving}
      />
    </Box>
  );
}