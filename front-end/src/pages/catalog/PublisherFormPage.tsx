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

import PublisherForm from "../../components/catalog/PublisherForm";

import {
  createPublisher,
  getPublisher,
  updatePublisher,
  PublisherRequest,
} from "../../api/publisherService";

import { Publisher } from "../../models/Publisher";

export default function PublisherFormPage() {
  const navigate = useNavigate();
  const { id } = useParams();

  const isEdit = Boolean(id);

  const [publisher, setPublisher] =
    useState<Publisher | undefined>();

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

    const loadPublisher = async () => {
      try {
        setLoading(true);
        setError(null);

        const data = await getPublisher(
          Number(id)
        );

        setPublisher(data);
      } catch (error: any) {
        if (
          error?.response?.status === 404
        ) {
          setError(
            "Publisher was not found."
          );
        } else {
          setError(
            "Unable to load the publisher."
          );
        }
      } finally {
        setLoading(false);
      }
    };

    loadPublisher();
  }, [id]);

  const handleSubmit = async (
    request: PublisherRequest
  ) => {
    try {
      setSaving(true);
      setError(null);

      if (isEdit && id) {
        await updatePublisher(
          Number(id),
          request
        );
      } else {
        await createPublisher(request);
      }

      navigate("/publishers");
    } catch (error: any) {
      const status =
        error?.response?.status;

      if (status === 409) {
        setError(
          "A publisher with the same name already exists."
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
            ? "Unable to update the publisher."
            : "Unable to create the publisher."
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

  if (
    error &&
    isEdit &&
    !publisher
  ) {
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

      <PublisherForm
        initialData={publisher}
        onSubmit={handleSubmit}
        onCancel={() =>
          navigate("/publishers")
        }
        loading={saving}
      />
    </Box>
  );
}