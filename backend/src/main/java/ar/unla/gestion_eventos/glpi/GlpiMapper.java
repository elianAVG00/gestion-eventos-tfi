package ar.unla.gestion_eventos.glpi;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
public class GlpiMapper {

    public record ParsedForm(
            String titulo, String desc, String departamento,
            String responsableNombre, String responsableEmail,
            String espacio, Instant inicio, Instant fin,
            String tipo, Integer asistentes,
            boolean montajeAnticipado, boolean asistenciaEspecializada, boolean recurrente
    ) {}

    public ParsedForm toParsedForm(Map<String,String> qa) {
        // Los labels vienen igual que en la respuesta del glpi; si cambia, ajustar acá.
        String titulo = qa.getOrDefault("1) Nombre/Título del evento :", "");
        String desc   = qa.getOrDefault("2) Descripción breve :", "");
        String depto  = qa.getOrDefault("3) Departamento o Área solicitante :", "");
        String respN  = qa.getOrDefault("4) Nombre :", "");
        String respE  = qa.getOrDefault("5) Correo electrónico :", "");
        String inicio = qa.getOrDefault("7) Fecha y Hora de Inicio solicitada  :", "");
        String fin    = qa.getOrDefault("8) Fecha y Hora de Finalización solicitada :", "");
        String espacio= qa.getOrDefault("9) Espacio físico solicitado :", "");
        String tipo   = qa.getOrDefault("10) Tipo de evento :", "");
        String asistentes = qa.getOrDefault("11) Cantidad estimada de asistentes :", "0");
        String monta = qa.getOrDefault("13) Necesidad de Montaje anticipado :", "No");
        String asiste= qa.getOrDefault("14) Necesidad de Asistencia técnica especializada :", "No");
        String recur = qa.getOrDefault("15) ¿Evento recurrente? :", "No");

        return new ParsedForm(
                titulo, desc, depto,
                respN, respE,
                espacio,
                FormAnswerParser.parseLocalDateTimeToInstant(inicio),
                FormAnswerParser.parseLocalDateTimeToInstant(fin),
                tipo, safeInt(asistentes),
                "Sí".equalsIgnoreCase(monta) || "Si".equalsIgnoreCase(monta),
                "Sí".equalsIgnoreCase(asiste) || "Si".equalsIgnoreCase(asiste),
                "Sí".equalsIgnoreCase(recur) || "Si".equalsIgnoreCase(recur)
        );
    }

    private static int safeInt(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; }
    }
    private static String nonNullStr(Object v, String fallback) {
        return v != null ? String.valueOf(v) : fallback;
    }
}
