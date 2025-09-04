// src/context/AuthContext.tsx
import { createContext, useState, useEffect } from "react";
import type { ReactNode } from "react";

interface User {
  email: string;
  ts: number; // timestamp para validar sesión
}

interface AuthContextType {
  user: User | null;
  login: (user: User, remember: boolean) => void;
  logout: () => void;
  loading: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  const login = (userData: User, remember: boolean) => {
    setUser(userData);
    const storage = remember ? localStorage : sessionStorage;
    storage.setItem("user", JSON.stringify(userData));
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem("user");
    sessionStorage.removeItem("user");
  };

  useEffect(() => {
    const stored = localStorage.getItem("user") || sessionStorage.getItem("user");
    if (stored) {
      try {
        const parsed = JSON.parse(stored) as User;
        const now = Date.now();
        const oneHour = 1000 * 60 * 60;
        if (now - parsed.ts < oneHour) {
          setUser(parsed);
        } else {
          logout();
        }
      } catch {
        logout();
      }
    }
    setLoading(false);
  }, []);

  const value: AuthContextType = { user, login, logout, loading };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export default AuthContext;
