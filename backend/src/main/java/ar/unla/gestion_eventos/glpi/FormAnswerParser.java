package ar.unla.gestion_eventos.glpi;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.time.*;
import java.time.format.DateTimeFormatter;
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
    public static Instant parseLocalDateTimeToInstant(String value) {
        if (value == null || value.isBlank()) return null;
        LocalDateTime ldt = LocalDateTime.parse(value.trim(), FMT);
        return ldt.atZone(AR).toInstant();
    }
}
