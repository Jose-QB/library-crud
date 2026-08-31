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

export default function Navbar() {
  const navigate = useNavigate();

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
          <Button
            color="inherit"
            component={RouterLink}
            to="/"
          >
            Books
          </Button>

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
        </Box>
      </Toolbar>
    </AppBar>
  );
}