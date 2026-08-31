import {
  useState,
  FormEvent,
} from "react";

import {
  Alert,
  Box,
  Button,
  Paper,
  TextField,
  Typography,
  Link,
} from "@mui/material";

import {
  Link as RouterLink,
  useNavigate,
} from "react-router-dom";

import { useAuth } from "../auth/AuthContext";

export default function Register() {
  const navigate = useNavigate();

  const { register } = useAuth();

  const [username, setUsername] =
    useState("");

  const [password, setPassword] =
    useState("");

  const [confirmPassword, setConfirmPassword] =
    useState("");

  const [error, setError] =
    useState("");

  const [loading, setLoading] =
    useState(false);

  const handleSubmit = async (
    event: FormEvent
  ) => {
    event.preventDefault();

    setError("");

    if (username.trim().length < 4) {
      setError(
        "Username must contain at least 4 characters."
      );
      return;
    }

    if (password.length < 8) {
      setError(
        "Password must contain at least 8 characters."
      );
      return;
    }

    if (password !== confirmPassword) {
      setError(
        "Passwords do not match."
      );
      return;
    }

    try {
      setLoading(true);

      await register({
        username: username.trim(),
        password,
      });

      navigate("/", {
        replace: true,
      });
    } catch (err) {
      console.error(err);

      setError(
        "Unable to create the account. The username may already exist."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box
      display="flex"
      justifyContent="center"
      py={6}
    >
      <Paper
        elevation={3}
        sx={{
          width: "100%",
          maxWidth: 420,
          p: 4,
        }}
      >
        <Typography
          variant="h4"
          component="h1"
          fontWeight="600"
          gutterBottom
        >
          Register
        </Typography>

        <Typography
          color="text.secondary"
          mb={3}
        >
          Create an account to manage the library.
        </Typography>

        {error && (
          <Alert
            severity="error"
            sx={{ mb: 3 }}
          >
            {error}
          </Alert>
        )}

        <Box
          component="form"
          onSubmit={handleSubmit}
        >
          <TextField
            fullWidth
            label="Username"
            value={username}
            onChange={(event) =>
              setUsername(event.target.value)
            }
            margin="normal"
            required
            inputProps={{
              minLength: 4,
              maxLength: 100,
            }}
            autoFocus
          />

          <TextField
            fullWidth
            label="Password"
            type="password"
            value={password}
            onChange={(event) =>
              setPassword(event.target.value)
            }
            margin="normal"
            required
            inputProps={{
              minLength: 8,
              maxLength: 100,
            }}
          />

          <TextField
            fullWidth
            label="Confirm password"
            type="password"
            value={confirmPassword}
            onChange={(event) =>
              setConfirmPassword(
                event.target.value
              )
            }
            margin="normal"
            required
          />

          <Button
            fullWidth
            variant="contained"
            type="submit"
            disabled={loading}
            sx={{ mt: 3 }}
          >
            {loading
              ? "Creating account..."
              : "Register"}
          </Button>
        </Box>

        <Box mt={3} textAlign="center">
          <Typography
            variant="body2"
            color="text.secondary"
          >
            Already have an account?{" "}
            <Link
              component={RouterLink}
              to="/login"
            >
              Sign in
            </Link>
          </Typography>
        </Box>
      </Paper>
    </Box>
  );
}