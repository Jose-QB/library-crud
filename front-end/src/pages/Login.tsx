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
  useLocation,
  useNavigate,
} from "react-router-dom";

import { useAuth } from "../auth/AuthContext";

export default function Login() {
  const navigate = useNavigate();
  const location = useLocation();

  const { login } = useAuth();

  const [username, setUsername] =
    useState("");

  const [password, setPassword] =
    useState("");

  const [error, setError] =
    useState("");

  const [loading, setLoading] =
    useState(false);

  const handleSubmit = async (
    event: FormEvent
  ) => {
    event.preventDefault();

    try {
      setLoading(true);
      setError("");

      await login({
        username,
        password,
      });

      const from =
        (location.state as {
          from?: string;
        })?.from || "/";

      navigate(from, {
        replace: true,
      });
    } catch (err) {
      console.error(err);

      setError(
        "Invalid username or password."
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
          Login
        </Typography>

        <Typography
          color="text.secondary"
          mb={3}
        >
          Sign in to manage the library.
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
          />

          <Button
            fullWidth
            variant="contained"
            type="submit"
            disabled={loading}
            sx={{ mt: 3 }}
          >
            {loading
              ? "Signing in..."
              : "Sign in"}
          </Button>
        </Box>

        <Box mt={3} textAlign="center">
          <Typography
            variant="body2"
            color="text.secondary"
          >
            Don't have an account?{" "}
            <Link
              component={RouterLink}
              to="/register"
            >
              Register
            </Link>
          </Typography>
        </Box>
      </Paper>
    </Box>
  );
}