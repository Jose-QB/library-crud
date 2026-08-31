import { useEffect, useState } from "react";

import {
  Alert,
  Box,
  Button,
  CircularProgress,
  IconButton,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from "@mui/material";

import {
  Add,
  Delete,
  Edit,
} from "@mui/icons-material";

import { useNavigate } from "react-router-dom";

import {
  deletePublisher,
  getPublishers,
} from "../../api/publisherService";

import { Publisher } from "../../models/Publisher";

export default function Publishers() {
  const navigate = useNavigate();

  const [publishers, setPublishers] =
    useState<Publisher[]>([]);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState<string | null>(null);

  const [deleteLoading, setDeleteLoading] =
    useState<number | null>(null);

  const loadPublishers = async () => {
    try {
      setLoading(true);
      setError(null);

      const data = await getPublishers();

      setPublishers(data);
    } catch {
      setError(
        "Unable to load publishers."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadPublishers();
  }, []);

  const handleDelete = async (
    publisher: Publisher
  ) => {
    const confirmed = window.confirm(
      `Are you sure you want to delete "${publisher.name}"?`
    );

    if (!confirmed) {
      return;
    }

    try {
      setDeleteLoading(publisher.id);
      setError(null);

      await deletePublisher(publisher.id);

      setPublishers((previous) =>
        previous.filter(
          (item) =>
            item.id !== publisher.id
        )
      );
    } catch (error: any) {
      if (error?.response?.status === 409) {
        setError(
          "This publisher cannot be deleted because it is associated with one or more books."
        );
      } else if (
        error?.response?.status === 404
      ) {
        setError(
          "Publisher was not found."
        );
      } else {
        setError(
          "Unable to delete the publisher."
        );
      }
    } finally {
      setDeleteLoading(null);
    }
  };

  return (
    <Box>
      <Box
        display="flex"
        justifyContent="space-between"
        alignItems="center"
        mb={3}
        gap={2}
      >
        <Typography
          variant="h4"
          component="h1"
          fontWeight={600}
        >
          Publishers
        </Typography>

        <Button
          variant="contained"
          startIcon={<Add />}
          onClick={() =>
            navigate("/publishers/new")
          }
        >
          New Publisher
        </Button>
      </Box>

      {error && (
        <Alert
          severity="error"
          sx={{ mb: 2 }}
          onClose={() => setError(null)}
        >
          {error}
        </Alert>
      )}

      {loading ? (
        <Box
          display="flex"
          justifyContent="center"
          py={8}
        >
          <CircularProgress />
        </Box>
      ) : (
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>
                  <strong>Name</strong>
                </TableCell>

                <TableCell>
                  <strong>Country</strong>
                </TableCell>

                <TableCell>
                  <strong>Founded Year</strong>
                </TableCell>

                <TableCell align="right">
                  <strong>Actions</strong>
                </TableCell>
              </TableRow>
            </TableHead>

            <TableBody>
              {publishers.length === 0 ? (
                <TableRow>
                  <TableCell
                    colSpan={4}
                    align="center"
                  >
                    No publishers found.
                  </TableCell>
                </TableRow>
              ) : (
                publishers.map((publisher) => (
                  <TableRow
                    key={publisher.id}
                    hover
                  >
                    <TableCell>
                      {publisher.name}
                    </TableCell>

                    <TableCell>
                      {publisher.country || "—"}
                    </TableCell>

                    <TableCell>
                      {publisher.foundedYear ?? "—"}
                    </TableCell>

                    <TableCell align="right">
                      <IconButton
                        color="primary"
                        onClick={() =>
                          navigate(
                            `/publishers/edit/${publisher.id}`
                          )
                        }
                        disabled={
                          deleteLoading !== null
                        }
                      >
                        <Edit />
                      </IconButton>

                      <IconButton
                        color="error"
                        onClick={() =>
                          handleDelete(publisher)
                        }
                        disabled={
                          deleteLoading !== null
                        }
                      >
                        {deleteLoading === publisher.id ? (
                          <CircularProgress size={24} />
                        ) : (
                          <Delete />
                        )}
                      </IconButton>
                    </TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
        </TableContainer>
      )}
    </Box>
  );
}