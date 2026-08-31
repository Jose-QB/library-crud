import {
  AppBar,
  Toolbar,
  Typography,
  Box,
  Button,
} from "@mui/material";

import MenuBookIcon from "@mui/icons-material/MenuBook";

import {
  Link as RouterLink,
  useNavigate,
} from "react-router-dom";

import { useAuth } from "../auth/AuthContext";

export default function Navbar() {
  const navigate = useNavigate();

  const {
    isAuthenticated,
    username,
    role,
    logout,
  } = useAuth();

  const handleLogout = () => {
    logout();
    navigate("/");
  };

  return (
    <AppBar position="static">
      <Toolbar>
        <MenuBookIcon sx={{ mr: 2 }} />

        {/* BRAND */}
        <Box
          sx={{
            flexGrow: 1,
            cursor: "pointer",
          }}
          onClick={() => navigate("/")}
        >
          <Typography variant="h6">
            Library CRUD
          </Typography>

          <Typography variant="caption">
            React + Spring Boot
          </Typography>
        </Box>

        {/* NAVIGATION */}
        <Box
          display="flex"
          alignItems="center"
          gap={1}
        >
          {/* PUBLIC */}
          <Button
            color="inherit"
            component={RouterLink}
            to="/"
          >
            Books
          </Button>

          {/* AUTHENTICATED */}
          {isAuthenticated && (
            <>
              <Button
                color="inherit"
                component={RouterLink}
                to="/authors"
              >
                Authors
              </Button>

              <Button
                color="inherit"
                component={RouterLink}
                to="/genres"
              >
                Genres
              </Button>

              <Button
                color="inherit"
                component={RouterLink}
                to="/publishers"
              >
                Publishers
              </Button>
            </>
          )}
        </Box>

        {/* AUTHENTICATION */}
        {!isAuthenticated ? (
          <Box
            display="flex"
            gap={1}
            ml={2}
          >
            <Button
              color="inherit"
              component={RouterLink}
              to="/login"
            >
              Login
            </Button>

            <Button
              color="inherit"
              component={RouterLink}
              to="/register"
            >
              Register
            </Button>
          </Box>
        ) : (
          <Box
            display="flex"
            alignItems="center"
            gap={2}
            ml={2}
          >
            <Box textAlign="right">
              <Typography variant="body2">
                {username}
              </Typography>

              <Typography
                variant="caption"
                sx={{
                  opacity: 0.8,
                }}
              >
                {role}
              </Typography>
            </Box>

            <Button
              color="inherit"
              onClick={handleLogout}
            >
              Logout
            </Button>
          </Box>
        )}
      </Toolbar>
    </AppBar>
  );
}