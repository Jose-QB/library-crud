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

import AuthorForm from "../../components/catalog/AuthorForm";

import {
  createAuthor,
  getAuthor,
  updateAuthor,
  AuthorRequest,
} from "../../api/authorService";

import { Author } from "../../models/Author";

export default function AuthorFormPage() {
  const navigate = useNavigate();
  const { id } = useParams();

  const isEdit = Boolean(id);

  const [author, setAuthor] =
    useState<Author | undefined>();

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

    const loadAuthor = async () => {
      try {
        setLoading(true);
        setError(null);

        const data = await getAuthor(
          Number(id)
        );

        setAuthor(data);
      } catch (error: any) {
        if (
          error?.response?.status === 404
        ) {
          setError("Author was not found.");
        } else {
          setError(
            "Unable to load the author."
          );
        }
      } finally {
        setLoading(false);
      }
    };

    loadAuthor();
  }, [id]);

  const handleSubmit = async (
    request: AuthorRequest
  ) => {
    try {
      setSaving(true);
      setError(null);

      if (isEdit && id) {
        await updateAuthor(
          Number(id),
          request
        );
      } else {
        await createAuthor(request);
      }

      navigate("/authors");
    } catch (error: any) {
      const status =
        error?.response?.status;

      if (status === 409) {
        setError(
          "An author with the same name already exists."
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
            ? "Unable to update the author."
            : "Unable to create the author."
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

  if (error && isEdit && !author) {
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

      <AuthorForm
        initialData={author}
        onSubmit={handleSubmit}
        onCancel={() =>
          navigate("/authors")
        }
        loading={saving}
      />
    </Box>
  );
}