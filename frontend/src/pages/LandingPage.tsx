import "./LandingPage.css";


export default function LandingPage() {
  return (
    <div className="wrap">
      <div className="card">
        <header className="header">
          <div className="logo" aria-hidden>
            ER
          </div>
          <div className="brand">Gestión de Eventos V0.1</div>
          <span className="badge">TFI · UNLa</span>
        </header>

        <section className="hero">
          <h1 className="title">
            Planificá, aprobá y coordiná eventos sin dolores de cabeza
          </h1>
          <p className="subtitle">
            Ingerí solicitudes desde GLPI, validá disponibilidad de espacios y
            recursos, y comunicá aprobaciones con trazabilidad.
          </p>
          <div className="ctaRow">
            <a className="btn btnPrimary" href="/login">
              Ingresar
            </a>
            <a className="btn btnGhost" href="/calendario">
              Ver calendario
            </a>
          </div>
        </section>

        <section className="grid">
          <a
            className="panel panelLink"
            href="/ingesta"
            role="link"
            aria-label="Ir a Ingesta GLPI"
          >
            <h3 className="pTitle">Ingesta GLPI</h3>
            <p className="pText">
              Conectá tickets aprobados y convertí formularios en eventos listos
              para validar.
            </p>
          </a>
          <article className="panel">
            <h3 className="pTitle">Validación de conflictos</h3>
            <p className="pText">
              Detección automática de choques de espacio/horario y recursos
              críticos.
            </p>
          </article>
          <article className="panel">
            <h3 className="pTitle">Calendario & reportes</h3>
            <p className="pText">
              Visualización por áreas, estados y métricas para seguimiento
              institucional.
            </p>
          </article>
        </section>

        <section className="stats">
          <div className="kpi">
            <div>Eventos activos</div>
            <strong>12</strong>
          </div>
          <div className="kpi">
            <div>Espacios</div>
            <strong>8</strong>
          </div>
          <div className="kpi">
            <div>Solicitudes hoy</div>
            <strong>3</strong>
          </div>
        </section>

        <footer className="footer">
          <span>© {new Date().getFullYear()} Gestión de Eventos</span>
          <span>v0.1 · Demo</span>
        </footer>
      </div>
    </div>
  );
}
