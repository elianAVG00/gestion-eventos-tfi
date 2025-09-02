import React, { useEffect, useMemo, useState } from "react";
import axios from "axios";
import { Download, Plus, Trash2, X } from "lucide-react";
import "../styles/Calendar.css";
import HomeButton from "./HomeButton";
import type { Event as DomainEvent } from "../interfaces/Event";
import SyncButton from "./SyncButton";

/** ===================== Tipos UI (presentación) ===================== **/
type CalendarEvent = {
  id: string;
  title: string;
  start: string;        // ISO local o UTC, se normaliza en getEventsForDay
  end?: string;
  description?: string;
  location?: string;
  color: string;        // requerido en la capa UI
};

/** ===================== Utilidades de fecha ===================== **/
function pad(n: number) {
  return n < 10 ? `0${n}` : `${n}`;
}

// Clave YYYY-MM-DD en horario LOCAL (para grid)
function dateKeyLocal(input: Date | string) {
  const d = typeof input === "string" ? new Date(input) : input;
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
}

// Formato hora corta (para el chip del evento)
function formatTime(dateStr: string) {
  return new Date(dateStr).toLocaleTimeString("es-AR", { hour: "2-digit", minute: "2-digit" });
}

/** ===================== Paleta & Hash para colores ===================== **/
const PALETTE = [
  "#3b82f6", "#10b981", "#f59e0b", "#ef4444",
  "#8b5cf6", "#06b6d4", "#84cc16", "#f97316"
];

// Hash simple y estable (djb2 simplificado)
function hashStr(s: string) {
  let h = 5381;
  for (let i = 0; i < s.length; i++) h = (h * 33) ^ s.charCodeAt(i);
  return Math.abs(h);
}

function colorForEvent(e: DomainEvent): string {
  // Priorizamos tipo; si no hay, usamos espacio; sino id.
  const base = e.eventType?.trim() || e.physicalSpace?.trim() || e.id;
  const idx = hashStr(base) % PALETTE.length;
  return PALETTE[idx];
}

/** ===================== Calendario ===================== **/
export default function InteractiveCalendar() {
  const [events, setEvents] = useState<CalendarEvent[]>([]);
  const [currentDate, setCurrentDate] = useState(new Date());
  const [showEventModal, setShowEventModal] = useState(false);
  const [eventForm, setEventForm] = useState<CalendarEvent>({
    id: "",
    title: "",
    start: "",
    end: "",
    description: "",
    location: "",
    color: PALETTE[0]
  });

  // Carga desde API
  useEffect(() => {
    (async () => {
      try {
        const { data } = await axios.get<DomainEvent[]>("http://localhost:9090/api/events");
        const mapped: CalendarEvent[] = data.map((ev) => ({
          id: ev.id,
          title: ev.title,
          start: ev.startDateTime,
          end: ev.endDateTime || undefined,
          description: ev.description || undefined,
          location: ev.physicalSpace || undefined,
          // Si en el futuro tu backend envía un color opcional, úsalo aquí:
          color: colorForEvent(ev)
        }));
        setEvents(mapped);
      } catch (err) {
        console.error("Error cargando eventos:", err);
        setEvents([]); // estado seguro
      }
    })();
  }, []);

  // Días del mes (42 celdas)
  const days = useMemo(() => {
    const year = currentDate.getFullYear();
    const month = currentDate.getMonth();
    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);
    const daysInMonth = lastDay.getDate();
    const startingDayOfWeek = firstDay.getDay(); // 0=Dom

    const out: { date: Date; isCurrentMonth: boolean }[] = [];
    for (let i = startingDayOfWeek - 1; i >= 0; i--) {
      out.push({ date: new Date(year, month, -i), isCurrentMonth: false });
    }
    for (let day = 1; day <= daysInMonth; day++) {
      out.push({ date: new Date(year, month, day), isCurrentMonth: true });
    }
    const remaining = 42 - out.length;
    for (let day = 1; day <= remaining; day++) {
      out.push({ date: new Date(year, month + 1, day), isCurrentMonth: false });
    }
    return out;
  }, [currentDate]);

  const todayKey = dateKeyLocal(new Date());
  const isToday = (d: Date) => dateKeyLocal(d) === todayKey;

  // Eventos por día (usando claves locales YYYY-MM-DD)
  const getEventsForDay = (date: Date) => {
    const key = dateKeyLocal(date);
    return events.filter((e) => {
      // soporta strings ISO con TZ; si no hay 10 chars, fallback a dateKeyLocal
      const startKey = e.start?.length >= 10 ? e.start.slice(0, 10) : dateKeyLocal(e.start);
      const endKey = e.end && e.end.length >= 10 ? e.end.slice(0, 10) : startKey;
      return key >= startKey && key <= endKey;
    });
  };

  /** ===== Modal (alta/edición local, opcional si mantienes mock) ===== **/
  const openEventModal = (event: CalendarEvent | null = null) => {
    if (event) {
      setEventForm({
        id: event.id,
        title: event.title,
        start: event.start.slice(0, 16),
        end: event.end ? event.end.slice(0, 16) : "",
        description: event.description || "",
        location: event.location || "",
        color: event.color
      });
    } else {
      setEventForm({ id: "", title: "", start: "", end: "", description: "", location: "", color: PALETTE[0] });
    }
    setShowEventModal(true);
  };

  const saveEvent = () => {
    if (!eventForm.title || !eventForm.start) return;

    const eventData: CalendarEvent = {
      ...eventForm,
      id: eventForm.id || String(Date.now()),
      start: eventForm.start + ":00",
      end: eventForm.end ? eventForm.end + ":00" : eventForm.start + ":00"
    };

    setEvents((prev) => (eventForm.id ? prev.map((e) => (e.id === eventForm.id ? eventData : e)) : [...prev, eventData]));
    setShowEventModal(false);
    setEventForm({ id: "", title: "", start: "", end: "", description: "", location: "", color: PALETTE[0] });
  };

  const deleteEvent = (eventId: string) => {
    setEvents((prev) => prev.filter((e) => e.id !== eventId));
    setShowEventModal(false);
  };

  const exportToICS = () => {
    const icsEvents = events.map((event) => {
      const start = new Date(event.start);
      const end = new Date(event.end || event.start);
      const dt = (d: Date) => d.toISOString().replace(/[-:]/g, "").split(".")[0] + "Z";
      return [
        "BEGIN:VEVENT",
        `UID:${event.id}`,
        `DTSTART:${dt(start)}`,
        `DTEND:${dt(end)}`,
        `SUMMARY:${event.title}`,
        event.description ? `DESCRIPTION:${event.description}` : "",
        event.location ? `LOCATION:${event.location}` : "",
        "END:VEVENT"
      ].filter(Boolean).join("\n");
    });

    const icsContent = ["BEGIN:VCALENDAR", "VERSION:2.0", "PRODID:-//Gestión de Eventos//ES", ...icsEvents, "END:VCALENDAR"].join("\n");
    const blob = new Blob([icsContent], { type: "text/calendar;charset=utf-8" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = "eventos.ics";
    a.click();
    URL.revokeObjectURL(url);
  };

  const navigateMonth = (direction: number) => {
    setCurrentDate((prev) => {
      const d = new Date(prev);
      d.setMonth(prev.getMonth() + direction);
      return d;
    });
  };

  return (
    <div className="calendar-container">
      {/* Header */}
      <div className="calendar-wrapper">
        <div className="calendar-header">
          <div className="header-brand">
            <div className="logo">ER</div>
            <div className="brand-info">
              <h1 className="brand-title">Gestión de Eventos</h1>
              <p className="brand-subtitle">Calendario Interactivo</p>
            </div>
          </div>
          <div className="header-actions">
            <button onClick={() => openEventModal()} className="btn btn-primary">
              <Plus size={16} /> Nuevo Evento
            </button>
            <button onClick={exportToICS} className="btn btn-success">
              <Download size={16} /> Exportar ICS
            </button>
          </div>
        </div>
      </div>

      {/* Nav */}
      <div className="calendar-wrapper">
        <div className="calendar-nav">
          <div className="nav-controls">
            <button onClick={() => navigateMonth(-1)} className="nav-btn">←</button>
            <h2 className="nav-title">
              {currentDate.toLocaleDateString("es-AR", { month: "long", year: "numeric" })}
            </h2>
            <button onClick={() => navigateMonth(1)} className="nav-btn">→</button>
          </div>
          <button onClick={() => setCurrentDate(new Date())} className="btn btn-secondary">Hoy</button>
        </div>
      </div>

      {/* Grid */}
      <div className="calendar-wrapper">
        <div className="calendar-grid">
          <div className="calendar-header-days">
            {["Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb"].map((d) => (
              <div key={d} className="header-day">{d}</div>
            ))}
          </div>

          <div className="calendar-days">
            {days.map((dayInfo, idx) => {
              const dayEvents = getEventsForDay(dayInfo.date);
              const isCurrentDay = isToday(dayInfo.date);

              return (
                <div
                  key={idx}
                  className={`calendar-day ${!dayInfo.isCurrentMonth ? "other-month" : ""} ${isCurrentDay ? "today" : ""}`}
                >
                  <div className={`day-number ${!dayInfo.isCurrentMonth ? "other-month-text" : ""} ${isCurrentDay ? "today-text" : ""}`}>
                    {dayInfo.date.getDate()}
                  </div>

                  <div className="day-events">
                    {dayEvents.slice(0, 2).map((event) => (
                      <div
                        key={event.id}
                        onClick={() => openEventModal(event)}
                        className="event-item"
                        style={{ backgroundColor: event.color }}
                      >
                        <div className="event-title">{event.title}</div>
                        {event.start && <div className="event-time">{formatTime(event.start)}</div>}
                      </div>
                    ))}
                    {dayEvents.length > 2 && <div className="events-more">+{dayEvents.length - 2} más</div>}
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      </div>

      {/* Modal (edición local) */}
      {showEventModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div className="modal-header">
              <h3 className="modal-title">{eventForm.id ? "Editar Evento" : "Nuevo Evento"}</h3>
              <button onClick={() => setShowEventModal(false)} className="modal-close"><X size={20} /></button>
            </div>

            <div className="modal-body">
              <div className="form-group">
                <label className="form-label">Título</label>
                <input
                  type="text"
                  value={eventForm.title}
                  onChange={(e) => setEventForm((p) => ({ ...p, title: e.target.value }))}
                  className="form-input"
                  placeholder="Título del evento"
                />
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label className="form-label">Inicio</label>
                  <input
                    type="datetime-local"
                    value={eventForm.start}
                    onChange={(e) => setEventForm((p) => ({ ...p, start: e.target.value }))}
                    className="form-input"
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Fin</label>
                  <input
                    type="datetime-local"
                    value={eventForm.end}
                    onChange={(e) => setEventForm((p) => ({ ...p, end: e.target.value }))}
                    className="form-input"
                  />
                </div>
              </div>

              <div className="form-group">
                <label className="form-label">Ubicación</label>
                <input
                  type="text"
                  value={eventForm.location}
                  onChange={(e) => setEventForm((p) => ({ ...p, location: e.target.value }))}
                  className="form-input"
                  placeholder="Ubicación del evento"
                />
              </div>

              <div className="form-group">
                <label className="form-label">Descripción</label>
                <textarea
                  value={eventForm.description}
                  onChange={(e) => setEventForm((p) => ({ ...p, description: e.target.value }))}
                  className="form-textarea"
                  placeholder="Descripción del evento"
                />
              </div>

              {/* Picker de color local (solo UI) */}
              <div className="form-group">
                <label className="form-label">Color</label>
                <div className="color-picker">
                  {PALETTE.map((c) => (
                    <button
                      key={c}
                      onClick={() => setEventForm((p) => ({ ...p, color: c }))}
                      className={`color-option ${eventForm.color === c ? "selected" : ""}`}
                      style={{ backgroundColor: c }}
                    />
                  ))}
                </div>
              </div>
            </div>

            <div className="modal-footer">
              <div className="modal-footer-left">
                {eventForm.id && (
                  <button onClick={() => deleteEvent(eventForm.id)} className="btn btn-danger">
                    <Trash2 size={16} /> Eliminar
                  </button>
                )}
              </div>
              <div className="modal-footer-right">
                <button onClick={() => setShowEventModal(false)} className="btn btn-ghost">Cancelar</button>
                <button onClick={saveEvent} className="btn btn-primary">
                  {eventForm.id ? "Actualizar" : "Crear"}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      <HomeButton />
      <SyncButton />
    </div>
  );
}
