package ar.unla.gestion_eventos.dto;

import ar.unla.gestion_eventos.Domain.Event;

public record UpdateEventResponse(Event event, boolean updatedInGlpi, boolean refreshPending) {}

