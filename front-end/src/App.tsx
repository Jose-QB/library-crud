import {
  BrowserRouter,
} from "react-router-dom";

import {
  Container,
} from "@mui/material";

import Navbar from "./components/Navbar";
import AppRouter from "./routes/AppRouter";

import {
  AuthProvider,
} from "./auth/AuthContext";

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Navbar />

        <Container
          maxWidth="lg"
          sx={{
            mt: 4,
            mb: 4,
          }}
        >
          <AppRouter />
        </Container>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;