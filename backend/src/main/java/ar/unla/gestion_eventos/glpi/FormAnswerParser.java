package ar.unla.gestion_eventos.glpi;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

public class FormAnswerParser {

    private static final ZoneId AR = ZoneId.of("America/Argentina/Buenos_Aires");
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /** Devuelve un map label->valor (ej: "1) Nombre/Título del evento :" -> "Evento prueba") */
    public static Map<String, String> extractRespuestaFormPares(String html) {
        Map<String, String> out = new HashMap<>();
        Document doc = Jsoup.parse(html);

        // Patrón del Formcreator que mostrás: <div><b>Pregunta :</b>Valor (o <p>Valor)</div>
        for (Element div : doc.select("div:has(> b)")) {
            Element b = div.selectFirst("b");
            if (b == null) continue;
            String label = b.text().trim();                 // e.g. "7) Fecha y Hora de Inicio solicitada  :"
            String value = div.ownText().trim();            // texto directo en el div (después del </b>)
            if (value.isBlank()) {
                // algunos campos vienen en <p> después del <b>
                Element p = div.selectFirst("p");
                if (p != null) value = p.text().trim();
            }
            out.put(label, value);
        }
        return out;
    }

    /** Helpers de conversión: fecha/hora local //hay q revisar estop */
    public static Instant parseLocalDateTimeToInstant(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isBlank()) return null;

        String s = dateTimeStr.trim().replace('\u00A0', ' '); // limpia NBSP

        DateTimeFormatter isoWithSpace = new java.time.format.DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd HH:mm")
                .optionalStart().appendPattern(":ss").optionalEnd()
                .toFormatter();

        DateTimeFormatter dmySlash = new java.time.format.DateTimeFormatterBuilder()
                .appendPattern("dd/MM/yyyy HH:mm")
                .optionalStart().appendPattern(":ss").optionalEnd()
                .toFormatter();

        DateTimeFormatter dmyDash = new java.time.format.DateTimeFormatterBuilder()
                .appendPattern("dd-MM-yyyy HH:mm")
                .optionalStart().appendPattern(":ss").optionalEnd()
                .toFormatter();

        DateTimeFormatter[] formatters = new DateTimeFormatter[] {
                isoWithSpace,                 // "2025-08-21 12:00" o "2025-08-21 12:00:00"
                DateTimeFormatter.ISO_LOCAL_DATE_TIME, // "2025-08-21T12:00"
                dmySlash,                     // "21/08/2025 12:00"
                dmyDash                       // "21-08-2025 12:00"
        };

        for (DateTimeFormatter f : formatters) {
            try {
                LocalDateTime ldt = LocalDateTime.parse(s, f);
                return ldt.atZone(AR).toInstant(); // usa la zona AR que ya definiste
            } catch (java.time.format.DateTimeParseException ignore) {}
        }

        System.err.println("No se pudo parsear la fecha: " + s);
        return null;
    }
}
