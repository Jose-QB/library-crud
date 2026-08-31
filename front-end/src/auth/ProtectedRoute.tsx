import {
  Navigate,
  Outlet,
  useLocation,
} from "react-router-dom";

import { useAuth } from "./AuthContext";
import { Role } from "../models/Auth";

interface ProtectedRouteProps {
  roles?: Role[];
}

export default function ProtectedRoute({
  roles,
}: ProtectedRouteProps) {
  const {
    isAuthenticated,
    role,
  } = useAuth();

  const location =
    useLocation();

  if (!isAuthenticated) {
    return (
      <Navigate
        to="/login"
        replace
        state={{
          from: location.pathname,
        }}
      />
    );
  }

  if (
    roles &&
    (!role || !roles.includes(role))
  ) {
    return (
      <Navigate
        to="/"
        replace
      />
    );
  }

  return <Outlet />;
}