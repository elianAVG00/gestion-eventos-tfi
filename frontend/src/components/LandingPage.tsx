import "../styles/LandingPage.css";

export default function LandingPage() {
  return (
    <main className="lp-wrap">
      {/* Header: marca a la izquierda, Ingresar arriba a la derecha */}
      <header className="lp-header" aria-label="Encabezado del sitio">
        <div className="lp-brand-left">
          <div className="lp-logo" aria-hidden>ER</div>
          <div className="lp-brand-text">
            <strong className="lp-brand-title">Gestión de Eventos</strong>
            <span className="lp-brand-badge">TFI · UNLa</span>
          </div>
        </div>

        <nav className="lp-header-actions" aria-label="Acción principal">
          <a className="lp-btn lp-btn--primary" href="/login">Ingresar</a>
        </nav>
      </header>

      {/* Hero con CTA centrada */}
      <section className="lp-hero" aria-labelledby="lp-hero-title">
        <h1 id="lp-hero-title" className="lp-hero-title">
          Eventos de la Universidad de Lanús
        </h1>
                <div className="lp-hero-cta">
          <a className="lp-btn lp-btn--primary" href="/calendar">📅 Ver calendario</a>
        </div>
        <p className="lp-hero-subtitle">
          Lorem ipsum dolor sit amet, consectetur adipisicing elit. Aspernatur expedita nobis ab rerum, dicta sit corrupti est dolores? Ullam eum possimus veniam atque impedit quis inventore necessitatibus fuga omnis delectus.
        </p>

      </section>

      {/* Features: columna vertical alineada a la derecha */}
      <section className="lp-features" aria-label="Características">
        <a className="lp-card lp-card--link" href="/ingesta" aria-label="Ir a Ingesta GLPI">
          <h3 className="lp-card-title">Ingesta GLPI</h3>
          <p className="lp-card-text">
            Conectá tickets aprobados y convertí formularios en eventos listos para validar.
          </p>
        </a>

         <a className="lp-card lp-card--link" href="/events" aria-label="Ver lista de Eventos">
          <h3 className="lp-card-title">Lista Eventos</h3>
          <p className="lp-card-text">
            Visualización de eventos tentativos. 
          </p>
        </a>

        <article className="lp-card">
          <h3 className="lp-card-title">Validación de conflictos</h3>
          <p className="lp-card-text">
            Detección automática de choques por espacio/horario y recursos críticos.
          </p>
        </article>

      </section>

      <footer className="lp-footer" aria-label="Información del sitio">
        <span>© {new Date().getFullYear()} Gestión de Eventos</span>
        <span className="lp-dot">•</span>
        <span>v0.1 · Demo</span>
      </footer>
    </main>
  );
}
