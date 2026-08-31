import {
  createContext,
  useContext,
  useEffect,
  useState,
  ReactNode,
} from "react";

import {
  AuthResponse,
  LoginRequest,
  RegisterRequest,
  Role,
} from "../models/Auth";

import {
  login as loginRequest,
  register as registerRequest,
} from "../api/authService";

interface AuthContextType {
  token: string | null;
  username: string | null;
  role: Role | null;
  isAuthenticated: boolean;

  login: (
    request: LoginRequest
  ) => Promise<AuthResponse>;

  register: (
    request: RegisterRequest
  ) => Promise<AuthResponse>;

  logout: () => void;
}

const AuthContext =
  createContext<AuthContextType | undefined>(
    undefined
  );

const TOKEN_KEY = "library_token";
const USERNAME_KEY = "library_username";
const ROLE_KEY = "library_role";

export function AuthProvider({
  children,
}: {
  children: ReactNode;
}) {
  const [token, setToken] = useState<string | null>(
    () => localStorage.getItem(TOKEN_KEY)
  );

  const [username, setUsername] =
    useState<string | null>(
      () =>
        localStorage.getItem(USERNAME_KEY)
    );

  const [role, setRole] =
    useState<Role | null>(() => {
      const storedRole =
        localStorage.getItem(ROLE_KEY);

      if (
        storedRole === "USER" ||
        storedRole === "ADMIN"
      ) {
        return storedRole;
      }

      return null;
    });

  const isAuthenticated =
    token !== null;

  useEffect(() => {
    if (token) {
      localStorage.setItem(
        TOKEN_KEY,
        token
      );
    } else {
      localStorage.removeItem(
        TOKEN_KEY
      );
    }
  }, [token]);

  useEffect(() => {
    if (username) {
      localStorage.setItem(
        USERNAME_KEY,
        username
      );
    } else {
      localStorage.removeItem(
        USERNAME_KEY
      );
    }
  }, [username]);

  useEffect(() => {
    if (role) {
      localStorage.setItem(
        ROLE_KEY,
        role
      );
    } else {
      localStorage.removeItem(
        ROLE_KEY
      );
    }
  }, [role]);

  const setSession = (
    response: AuthResponse
  ) => {
    setToken(response.token);
    setUsername(response.username);
    setRole(response.role);
  };

  const login = async (
    request: LoginRequest
  ): Promise<AuthResponse> => {
    const response =
      await loginRequest(request);

    setSession(response);

    return response;
  };

  const register = async (
    request: RegisterRequest
  ): Promise<AuthResponse> => {
    const response =
      await registerRequest(request);

    setSession(response);

    return response;
  };

  const logout = () => {
    setToken(null);
    setUsername(null);
    setRole(null);
  };

  return (
    <AuthContext.Provider
      value={{
        token,
        username,
        role,
        isAuthenticated,
        login,
        register,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth(): AuthContextType {
  const context =
    useContext(AuthContext);

  if (!context) {
    throw new Error(
      "useAuth must be used within AuthProvider"
    );
  }

  return context;
}