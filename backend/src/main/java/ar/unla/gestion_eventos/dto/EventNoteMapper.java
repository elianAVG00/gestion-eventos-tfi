package ar.unla.gestion_eventos.dto;

import ar.unla.gestion_eventos.Domain.EventNote;

public class EventNoteMapper {
    private EventNoteMapper() {}

    public static EventNoteDto toDto(EventNote note) {
        if (note == null) return null;
        return EventNoteDto.builder()
                .id(note.getId())
                .glpiFollowupId(note.getGlpiFollowupId())
                .glpiTicketId(note.getGlpiTicketId())
                .content(note.getContent())
                .createdAt(note.getCreatedAt())
                .author(note.getAuthor())
                .build();
    }
}