import {
  Routes,
  Route,
} from "react-router-dom";

import Home from "../pages/Home";
import CreateBook from "../pages/CreateBook";
import EditBook from "../pages/EditBook";
import BookDetail from "../pages/BookDetail";

import Authors from "../pages/catalog/Authors";
import AuthorFormPage from "../pages/catalog/AuthorFormPage";

import Genres from "../pages/catalog/Genres";
import GenreFormPage from "../pages/catalog/GenreFormPage";

import Publishers from "../pages/catalog/Publishers";
import PublisherFormPage from "../pages/catalog/PublisherFormPage";

export default function AppRouter() {
  return (
    <Routes>
      {/* Books */}
      <Route
        path="/"
        element={<Home />}
      />

      <Route
        path="/books/new"
        element={<CreateBook />}
      />

      <Route
        path="/books/:id"
        element={<BookDetail />}
      />

      <Route
        path="/books/edit/:id"
        element={<EditBook />}
      />

      {/* Authors */}
      <Route
        path="/authors"
        element={<Authors />}
      />

      <Route
        path="/authors/new"
        element={<AuthorFormPage />}
      />

      <Route
        path="/authors/edit/:id"
        element={<AuthorFormPage />}
      />

      {/* Genres */}
      <Route
        path="/genres"
        element={<Genres />}
      />

      <Route
        path="/genres/new"
        element={<GenreFormPage />}
      />

      <Route
        path="/genres/edit/:id"
        element={<GenreFormPage />}
      />

      {/* Publishers */}
      <Route
        path="/publishers"
        element={<Publishers />}
      />

      <Route
        path="/publishers/new"
        element={<PublisherFormPage />}
      />

      <Route
        path="/publishers/edit/:id"
        element={<PublisherFormPage />}
      />
    </Routes>
  );
}