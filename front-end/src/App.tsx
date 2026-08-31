import {
  BrowserRouter,
} from "react-router-dom";

import {
  Container,
} from "@mui/material";

import Navbar from "./components/Navbar";
import AppRouter from "./routes/AppRouter";

function App() {
  return (
    <BrowserRouter>
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
    </BrowserRouter>
  );
}

export default App;