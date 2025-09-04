import { useAuth } from "../hooks/useAuth";
import { useNavigate } from "react-router-dom";

export default function Logout() {
  const { logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = (e: React.MouseEvent) => {
    e.preventDefault();        // Evita comportamiento del <a>
    logout();                  // Limpia user + storage
    navigate("/login");        // Redirige sin recarga
  };

  return (
    <nav className="lp-header-actions" aria-label="Cerrar sesión">
      <a className="lp-btn lp-btn--primary" href="/login" onClick={handleLogout}>
        Cerrar sesión
      </a>
    </nav>
  );
}
