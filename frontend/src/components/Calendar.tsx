import React, { useState } from "react";
import { Download, Plus, Trash2, X } from "lucide-react";
import "./Calendar.css"; 

type EventItem = {
  id: string;
  title: string;
  start: string;       // ISO local, ej: 2025-08-26T14:00:00
  end?: string;        // ISO local
  description?: string;
  location?: string;
  color?: string;
};

const mockEvents: EventItem[] = [
  { id: "1", title: "Acto de colación", start: "2025-09-03T10:00:00", end: "2025-09-03T12:00:00", description: "Ceremonia de graduación de la promoción 2025", location: "Aula Magna", color: "#3b82f6" },
  { id: "2", title: "Seminario Ingeniería", start: "2025-09-08T09:00:00", end: "2025-09-10T17:00:00", description: "Seminario con invitados externos sobre nuevas tecnologías", location: "Laboratorio A", color: "#10b981" },
  { id: "3", title: "Reunión de equipo", start: "2025-08-26T14:00:00", end: "2025-08-26T15:30:00", description: "Revisión semanal del proyecto", location: "Sala de conferencias", color: "#f59e0b" },
  { id: "4", title: "Presentación final", start: "2025-08-28T16:00:00", end: "2025-08-28T18:00:00", description: "Presentación del proyecto final", location: "Auditorio principal", color: "#ef4444" },
];

function pad(n: number) {
  return n < 10 ? `0${n}` : `${n}`;
}

// ✅ Obtiene clave de fecha LOCAL (YYYY-MM-DD)
function dateKeyLocal(input: Date | string) {
  const d = typeof input === "string" ? new Date(input) : input;
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
}

export default function InteractiveCalendar() {
  const [events, setEvents] = useState<EventItem[]>(mockEvents);
  const [currentDate, setCurrentDate] = useState(new Date());
  const [showEventModal, setShowEventModal] = useState(false);
  const [eventForm, setEventForm] = useState<EventItem>({
    id: "",
    title: "",
    start: "",
    end: "",
    description: "",
    location: "",
    color: "#3b82f6"
  });

  const colors = ["#3b82f6", "#10b981", "#f59e0b", "#ef4444", "#8b5cf6", "#06b6d4", "#84cc16", "#f97316"];

  const formatTime = (dateStr: string) => {
    return new Date(dateStr).toLocaleTimeString("es-ES", { hour: "2-digit", minute: "2-digit" });
  };

  const getDaysInMonth = (date: Date) => {
    const year = date.getFullYear();
    const month = date.getMonth();
    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);
    const daysInMonth = lastDay.getDate();
    const startingDayOfWeek = firstDay.getDay(); // 0=Dom

    const days: { date: Date; isCurrentMonth: boolean }[] = [];

    // Días del mes anterior
    for (let i = startingDayOfWeek - 1; i >= 0; i--) {
      days.push({ date: new Date(year, month, -i), isCurrentMonth: false });
    }
    // Días del mes actual
    for (let day = 1; day <= daysInMonth; day++) {
      days.push({ date: new Date(year, month, day), isCurrentMonth: true });
    }
    // Completar hasta 6x7=42
    const remaining = 42 - days.length;
    for (let day = 1; day <= remaining; day++) {
      days.push({ date: new Date(year, month + 1, day), isCurrentMonth: false });
    }
    return days;
  };

  // ✅ Sin UTC shift: compara YYYY-MM-DD locales
  const getEventsForDay = (date: Date) => {
    const key = dateKeyLocal(date);
    return events.filter(e => {
      const startKey = e.start ? (e.start.length >= 10 ? e.start.slice(0, 10) : dateKeyLocal(e.start)) : key;
      const endKey = e.end ? (e.end.length >= 10 ? e.end.slice(0, 10) : dateKeyLocal(e.end)) : startKey;
      return key >= startKey && key <= endKey;
    });
  };

  const openEventModal = (event: EventItem | null = null) => {
    if (event) {
      setEventForm({
        id: event.id,
        title: event.title,
        start: event.start.slice(0, 16),
        end: event.end ? event.end.slice(0, 16) : "",
        description: event.description || "",
        location: event.location || "",
        color: event.color || "#3b82f6"
      });
    } else {
      setEventForm({ id: "", title: "", start: "", end: "", description: "", location: "", color: "#3b82f6" });
    }
    setShowEventModal(true);
  };

  const saveEvent = () => {
    if (!eventForm.title || !eventForm.start) return;

    const eventData: EventItem = {
      ...eventForm,
      id: eventForm.id || (crypto as any).randomUUID?.() || String(Date.now()),
      start: eventForm.start + ":00",
      end: eventForm.end ? eventForm.end + ":00" : eventForm.start + ":00"
    };

    setEvents(prev => eventForm.id ? prev.map(e => (e.id === eventForm.id ? eventData : e)) : [...prev, eventData]);
    setShowEventModal(false);
    setEventForm({ id: "", title: "", start: "", end: "", description: "", location: "", color: "#3b82f6" });
  };

  const deleteEvent = (eventId: string) => {
    setEvents(prev => prev.filter(e => e.id !== eventId));
    setShowEventModal(false);
  };

  const exportToICS = () => {
    const icsEvents = events.map(event => {
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
    setCurrentDate(prev => {
      const d = new Date(prev);
      d.setMonth(prev.getMonth() + direction);
      return d;
    });
  };

  const days = getDaysInMonth(currentDate);
  const today = new Date();
  const isToday = (date: Date) => dateKeyLocal(date) === dateKeyLocal(today);

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

      {/* Calendar Navigation */}
      <div className="calendar-wrapper">
        <div className="calendar-nav">
          <div className="nav-controls">
            <button onClick={() => navigateMonth(-1)} className="nav-btn">←</button>
            <h2 className="nav-title">
              {currentDate.toLocaleDateString("es-ES", { month: "long", year: "numeric" })}
            </h2>
            <button onClick={() => navigateMonth(1)} className="nav-btn">→</button>
          </div>
          <button onClick={() => setCurrentDate(new Date())} className="btn btn-secondary">Hoy</button>
        </div>
      </div>

      {/* Calendar Grid */}
      <div className="calendar-wrapper">
        <div className="calendar-grid">
          <div className="calendar-header-days">
            {["Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb"].map(d => (
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
                    {dayEvents.slice(0, 2).map(event => (
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

      {/* Event Modal */}
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
                  onChange={(e) => setEventForm(prev => ({ ...prev, title: e.target.value }))}
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
                    onChange={(e) => setEventForm(prev => ({ ...prev, start: e.target.value }))}
                    className="form-input"
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Fin</label>
                  <input
                    type="datetime-local"
                    value={eventForm.end}
                    onChange={(e) => setEventForm(prev => ({ ...prev, end: e.target.value }))}
                    className="form-input"
                  />
                </div>
              </div>

              <div className="form-group">
                <label className="form-label">Ubicación</label>
                <input
                  type="text"
                  value={eventForm.location}
                  onChange={(e) => setEventForm(prev => ({ ...prev, location: e.target.value }))}
                  className="form-input"
                  placeholder="Ubicación del evento"
                />
              </div>

              <div className="form-group">
                <label className="form-label">Descripción</label>
                <textarea
                  value={eventForm.description}
                  onChange={(e) => setEventForm(prev => ({ ...prev, description: e.target.value }))}
                  className="form-textarea"
                  placeholder="Descripción del evento"
                />
              </div>

              <div className="form-group">
                <label className="form-label">Color</label>
                <div className="color-picker">
                  {colors.map(color => (
                    <button
                      key={color}
                      onClick={() => setEventForm(prev => ({ ...prev, color }))}
                      className={`color-option ${eventForm.color === color ? "selected" : ""}`}
                      style={{ backgroundColor: color }}
                    />
                  ))}
                </div>
              </div>
            </div>

            <div className="modal-footer">
              <div className="modal-footer-left">
                {eventForm.id && (
                  <button onClick={() => deleteEvent(eventForm.id!)} className="btn btn-danger">
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
    </div>
  );
}
