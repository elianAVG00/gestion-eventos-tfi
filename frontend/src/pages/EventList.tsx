import React, { useState, useEffect } from 'react';
import axios from 'axios';
import type { Event } from '../interfaces/Event';
import "../styles/EventList.css";
import HomeButton from '../components/HomeButton';

const EventList: React.FC = () => {
  const [events, setEvents] = useState<Event[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [expandedId, setExpandedId] = useState<string | number | null>(null);

  useEffect(() => {
    const fetchEvents = async () => {
      const apiUrl = 'http://localhost:9090/api/events';
      try {
        const response = await axios.get<Event[]>(apiUrl);
        if (response.status === 204) {
          setEvents([]);
        } else {
          setEvents(response.data);
        }
      } catch (err: any) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };
    fetchEvents();
  }, []);

  // fechas legibles
  const formatDateTime = (isoString?: string | null): string => {
    if (!isoString) return '';
    const date = new Date(isoString);
    return date.toLocaleString('es-ES', {
      year: 'numeric',
      month: 'short',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const formatRange = (start?: string | null, end?: string | null): string => {
    const s = formatDateTime(start);
    const e = end ? formatDateTime(end) : '';
    return e ? `${s} — ${e}` : s;
  };

  // Acciones de accesibilidad / mobile
  const handleToggle = (id: string | number) => {
    setExpandedId(prev => (prev === id ? null : id));
  };

  const handleMouseEnter = (id: string | number) => setExpandedId(id);
  const handleMouseLeave = (id: string | number) => {
    // si el usuario abrió por clic, no colapsamos en mouseleave
    if (expandedId !== id) return;
    setExpandedId(null);
  };

  if (loading) {
    return (
      <div className="calendar-container">
        <div className="loading-state">Cargando eventos...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="calendar-container">
        <div className="error-state">Error al cargar los eventos: {error}</div>
      </div>
    );
  }

  return (
    <div className="calendar-container">
      <div className="calendar-header">
        <div className="header-brand">
          <div className="logo">E</div>
          <div>
            <h1 className="brand-title">Eventos UnLa</h1>
            <p className="brand-subtitle">Calendario de actividades</p>
          </div>
        </div>
      </div>

      <div className="calendar-wrapper">
        <div className="calendar-nav">
          <h2 className="nav-title">Eventos Recientes</h2>
          <div className="nav-controls">{/* futuros filtros/orden */}</div>
        </div>

        <div className="event-list-grid">
          {events.length === 0 ? (
            <div className="no-events-state">No hay eventos disponibles.</div>
          ) : (
            events.map((event) => {
              const isExpanded = expandedId === event.id;
              return (
                <article
                  key={event.id}
                  className={`event-card ${isExpanded ? 'expanded' : ''}`}
                  role="button"
                  tabIndex={0}
                  aria-expanded={isExpanded}
                  onClick={() => handleToggle(event.id!)}
                  onKeyDown={(e) => {
                    if (e.key === 'Enter' || e.key === ' ') {
                      e.preventDefault();
                      handleToggle(event.id!);
                    }
                  }}
                  onMouseEnter={() => handleMouseEnter(event.id!)}
                  onMouseLeave={() => handleMouseLeave(event.id!)}
                >
                  {/* Vista mínima */}
                  <header className="event-minimal">
                    <h3 className="event-title">{event.title}</h3>

                    <div className="event-row">
                      <span className="event-label">Espacio:</span>
                      <span className="event-value">{event.physicalSpace || '—'}</span>
                    </div>

                    <div className="event-row">
                      <span className="event-label">Fecha:</span>
                      <span className="event-value">
                        {formatRange(event.startDateTime as any, event.endDateTime as any)}
                      </span>
                    </div>

                    <div className="event-row">
                      <span className="event-label">Responsable:</span>
                      <span className="event-value">{event.responsibleName || '—'}</span>
                    </div>
                  </header>

                  {/* Detalles expandibles */}
                  <section className="event-details-collapsible">
                    <div className="event-divider" />
                    <p className="event-description">{event.description || 'Sin descripción'}</p>

                    <div className="event-details-grid">
                      <div className="detail-item">
                        <span className="detail-label">Tipo:</span>
                        <span className="detail-value">{event.eventType || '—'}</span>
                      </div>
                      <div className="detail-item">
                        <span className="detail-label">Asistentes estimados:</span>
                        <span className="detail-value">
                          {event.estimatedAttendees ?? '—'}
                        </span>
                      </div>
                      <div className="detail-item">
                        <span className="detail-label">Inicio:</span>
                        <span className="detail-value">
                          {formatDateTime(event.startDateTime as any)}
                        </span>
                      </div>
                      <div className="detail-item">
                        <span className="detail-label">Fin:</span>
                        <span className="detail-value">
                          {event.endDateTime ? formatDateTime(event.endDateTime as any) : '—'}
                        </span>
                      </div>
                    </div>
                  </section>
                </article>
              );
            })
          )}
        </div>
      </div>
      <HomeButton />
    </div>
  );
};

export default EventList;
