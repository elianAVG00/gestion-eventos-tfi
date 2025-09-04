import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import LandingPage from "./pages/LandingPage";
import InteractiveCalendar from "./pages/Calendar";
import EventList from "./pages/EventList";
import Login from "./pages/Login";
import PrivateRoute from "./components/PrivateRoute";
import { AuthProvider } from "./context/AuthContext";

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          {/* Página pública */}
          <Route path="/login" element={<Login />} />

          {/* Redirección del home "/" → solo si estás logueado */}
          <Route
            path="/"
            element={
              <PrivateRoute>
                <Navigate to="/landing" replace />
              </PrivateRoute>
            }
          />

          {/* Rutas protegidas */}
          <Route
            path="/landing"
            element={
              <PrivateRoute>
                <LandingPage />
              </PrivateRoute>
            }
          />

          <Route
            path="/calendar"
            element={
              <PrivateRoute>
                <InteractiveCalendar />
              </PrivateRoute>
            }
          />

          <Route
            path="/events"
            element={
              <PrivateRoute>
                <EventList />
              </PrivateRoute>
            }
          />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}
