// src/pages/Login.tsx
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { LogIn, Eye, EyeOff, Shield } from "lucide-react";
import "../styles/Login.css";
import { useAuth } from "../hooks/useAuth";
import { validateMockCredentials } from "../utils/auth";

export default function Login() {
  const { login } = useAuth(); 
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPwd, setShowPwd] = useState(false);
  const [remember, setRemember] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    if (!email || !email.includes("@")) {
      setError("Ingrese un correo válido.");
      return;
    }
    if (!password || password.length < 4) {
      setError("Ingrese una contraseña de al menos 4 caracteres.");
      return;
    }

    setSubmitting(true);
    try {
      if (validateMockCredentials(email, password)) {
        const userData = {
          email,
          ts: Date.now()
        };
        login(userData, remember);
        navigate("/landing");
      } else {
        setError("Credenciales inválidas.");
      }
    } catch (err) {
      setError("Error al iniciar sesión.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <main className="login-wrap">
      <header className="login-brand" aria-label="Marca del sistema">
        <div className="login-logo" aria-hidden>ER</div>
        <div className="login-meta">
          <strong className="login-title">Gestión de Eventos</strong>
          <span className="login-badge">TFI · UNLa</span>
        </div>
      </header>

      <section className="login-card" aria-label="Formulario de acceso">
        <div className="login-card-header">
          <Shield size={18} />
          <h1 className="login-card-title">Iniciar sesión</h1>
        </div>

        {error && (
          <div className="login-alert" role="alert" aria-live="assertive">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="login-form" noValidate>
          <div className="form-group">
            <label htmlFor="email" className="form-label">Correo institucional</label>
            <input
              id="email"
              name="email"
              type="email"
              autoComplete="username"
              className="form-input"
              placeholder="usuario@unla.edu.ar"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              aria-invalid={!!error && !email}
            />
          </div>

          <div className="form-group">
            <label htmlFor="password" className="form-label">Contraseña</label>
            <div className="form-input-with-btn">
              <input
                id="password"
                name="password"
                type={showPwd ? "text" : "password"}
                autoComplete="current-password"
                className="form-input"
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                aria-invalid={!!error && !password}
              />
              <button
                type="button"
                className="icon-btn"
                onClick={() => setShowPwd((s) => !s)}
                aria-label={showPwd ? "Ocultar contraseña" : "Mostrar contraseña"}
              >
                {showPwd ? <EyeOff size={16} /> : <Eye size={16} />}
              </button>
            </div>
          </div>

          <div className="form-row-between">
            <label className="checkbox">
              <input
                type="checkbox"
                checked={remember}
                onChange={(e) => setRemember(e.target.checked)}
              />
              <span>Recordarme</span>
            </label>

            <a className="link-ghost" href="#" onClick={(e) => e.preventDefault()}>
              ¿Olvidaste tu contraseña?
            </a>
          </div>

          <button
            type="submit"
            className="btn btn-primary wide"
            disabled={submitting}
          >
            <LogIn size={16} />
            {submitting ? "Ingresando..." : "Ingresar"}
          </button>

          <div className="form-footnote">
            Acceso de demostración. No se realizan validaciones contra un servidor.
          </div>
        </form>
      </section>
    </main>
  );
}
