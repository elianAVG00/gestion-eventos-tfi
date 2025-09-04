package ar.unla.gestion_eventos.glpi;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GlpiClient {

    private static final Logger log = LoggerFactory.getLogger(GlpiClient.class);

    private final GlpiProperties props;
    private final RestClient rest = RestClient.create();
    private volatile String sessionToken;
    private static final DateTimeFormatter GLPI_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    private String authHeader() {
        var t = props.userToken();
        return (t != null && t.startsWith("user_token ")) ? t : "user_token " + t;
    }

    @SuppressWarnings("unchecked")
    public void openSession() {
        // Validaciones mínimas
        if (props.baseUrl() == null || props.baseUrl().isBlank())
            throw new IllegalStateException("glpi.base-url no configurada.");
        if (props.appToken() == null || props.appToken().isBlank())
            throw new IllegalStateException("glpi.app-token vacío.");
        if (props.userToken() == null || props.userToken().isBlank())
            throw new IllegalStateException("glpi.user-token vacío.");

        final String base = trimTrailingSlash(props.baseUrl().trim());
        final String url  = base + "/initSession?get_full_session=true";

        Map<String, Object> resp;

        // 1) Intento principal: GET + Authorization (igual que el equipo)
        try {
            log.debug("GLPI initSession (GET+Authorization) URL: {}", url);
            resp = rest.get()
                    .uri(URI.create(url))
                    .header("Authorization", "user_token " + props.userToken()) // ← formato exacto
                    .header("App-Token", props.appToken())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(Map.class);

        } catch (HttpClientErrorException.BadRequest e) {
            // 2) Fallback: si el servidor NO recibió Authorization → usar query param
            final String body = e.getResponseBodyAsString();
            if (body != null && body.contains("ERROR_LOGIN_PARAMETERS_MISSING")) {
                final String urlWithToken = url + "&user_token=" +
                        URLEncoder.encode(props.userToken(), StandardCharsets.UTF_8);
                log.warn("Authorization no llegó a GLPI. Reintentando (GET+user_token en query).");
                resp = rest.get()
                        .uri(URI.create(urlWithToken))
                        .header("App-Token", props.appToken())
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .body(Map.class);
            } else {
                throw e;
            }
        }

        Object token = (resp != null) ? resp.get("session_token") : null;
        if (token == null) {
            throw new IllegalStateException("GLPI no devolvió 'session_token'. Revise credenciales/permisos.");
        }
        this.sessionToken = token.toString();
    }

    private static String trimTrailingSlash(String s) {
        return s.endsWith("/") ? s.substring(0, s.length() - 1) : s;
    }
    /** Une base y path evitando dobles barras. Admite base con o sin barra final. */
    private static String joinPath(String base, String path) {
        String b = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
        String p = path.startsWith("/") ? path : "/" + path;
        return b + p;
    }

    /** Cierra sesión si está abierta */
    public void closeSession() {
        if (sessionToken == null) return;
        rest.get()
                .uri(props.baseUrl() + "/killSession")
                .header("App-Token", props.appToken())
                .header("Session-Token", sessionToken)
                .retrieve().toBodilessEntity();
        sessionToken = null;
    }

    public String getSessionToken() {

        return sessionToken;
    }

    /** Hago el get del ticket por id  */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getTicket(long id, boolean expandDropdowns, boolean rawHtml) {
        var uri = UriComponentsBuilder.fromHttpUrl(props.baseUrl())
                .path("/Ticket/{id}")
                .queryParam("expand_dropdowns", expandDropdowns ? "true" : "false")
                .buildAndExpand(id)
                .toUri();
        return rest.get()
                .uri(uri)
                .header("App-Token", props.appToken())
                .header("Session-Token", sessionToken)
                .header("X-GLPI-Sanitized-Content", rawHtml ? "false" : "true")
                .retrieve()
                .body(Map.class);
    }

    /** Resultado del update de ticket en GLPI */
    public record GlpiUpdateResult(boolean success, String message) {}

    /** Realiza el update de un ticket en GLPI */
    @SuppressWarnings("unchecked")
    public GlpiUpdateResult updateTicket(String sessionToken, long glpiTicketId, Map<String, Object> input) {
        var uri = UriComponentsBuilder.fromHttpUrl(props.baseUrl())
                .path("/Ticket/{id}")
                .queryParam("session_write", "true")
                .buildAndExpand(glpiTicketId)
                .toUri();

        List<Map<String, Object>> body = rest.put()
                .uri(uri)
                .header("App-Token", props.appToken())
                .header("Session-Token", sessionToken)
                .header("X-GLPI-Sanitized-Content", "false")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("input", input))
                .retrieve()
                .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});

        if (body == null || body.isEmpty()) {
            return new GlpiUpdateResult(false, "Respuesta vacía de GLPI");
        }
        Map<String, Object> first = body.get(0);
        Object ok = first.get(String.valueOf(glpiTicketId));
        boolean success = ok instanceof Boolean b && b;
        String message = String.valueOf(first.getOrDefault("message", ""));
        return new GlpiUpdateResult(success, message);
    }

    @SuppressWarnings("unchecked")
    public TicketSearchOptions getTicketSearchOptions(String sessionToken) {
        Map<String, Object> resp = rest.get()
                .uri(props.baseUrl() + "/listSearchOptions/Ticket")
                .header("App-Token", props.appToken())
                .header("Session-Token", sessionToken)
                .retrieve()
                .body(Map.class);
        int id = -1;
        int dateMod = -1;
        Integer name = null;
        Integer status = null;
        if (resp != null) {
            for (Map.Entry<String, Object> e : resp.entrySet()) {
                Object val = e.getValue();
                if (!(val instanceof Map)) continue;
                Map<String, Object> m = (Map<String, Object>) val;
                String field = String.valueOf(m.get("field"));
                int key;
                try { key = Integer.parseInt(e.getKey()); } catch (Exception ex) { continue; }
                if ("id".equals(field) && "ID".equals(String.valueOf(m.get("name")))) id = key;
                else if ("date_mod".equals(field)) dateMod = key;
                else if ("name".equals(field)  && "Título".equals(String.valueOf(m.get("name")))) name = key;
                else if ("status".equals(field) && "Ticket.status".equals(String.valueOf(m.get("uid")))) status = key; // aca con este filtramos por estado, chequear desp para traerse los approved
            }
        }
        if (id < 0 || dateMod < 0) {
            throw new IllegalStateException("No se encontraron search options requeridos");
        }
        return new TicketSearchOptions(id, dateMod, name, status);
    }

    public record SearchResponse(List<Map<String, Object>> items, boolean hasMore) {}

    @SuppressWarnings("unchecked")
    public SearchResponse searchTickets(String sessionToken, TicketSearchOptions so,
                                        Instant lastDateMod, long lastId,
                                        int start, int end) {
        var dateStr = GLPI_FMT.format(lastDateMod);
        var uri = UriComponentsBuilder.fromHttpUrl(props.baseUrl())
                .path("/search/Ticket")
                .query(
                        "criteria[0][field]={dateField}&criteria[0][searchtype]=morethan&criteria[0][value]={date}" +
                                "&forcedisplay[0]={idField}&forcedisplay[1]={dateField}" +
                                "&range=0-99"+
                                "&sort={dateField}&order=ASC"
                ).encode() // <-- esto escapa los corchetes a %5B %5D y encodea los valores
                .buildAndExpand(Map.of(
                        "dateField",  so.dateMod(),
                        "idField",    so.id(),
                        "date",       dateStr
                ))
                .toUri();

        ResponseEntity<Map<String, Object>> resp = rest.get()
                .uri(uri)
                .header("App-Token", props.appToken())
                .header("Session-Token", sessionToken)
                .header("Range", start + "-" + end)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<Map<String, Object>>() {});

        Map<String, Object> body = resp.getBody();
        List<Map<String, Object>> data = new ArrayList<>();
        if (body != null && body.get("data") instanceof List list) {
            data = (List<Map<String, Object>>) list;
        }
        boolean hasMore = false;
        String cr = resp.getHeaders().getFirst("Content-Range");
        if (cr != null) {
            try {
                String[] parts = cr.split("[/-]");
                int endIdx = Integer.parseInt(parts[1]);
                int total = Integer.parseInt(parts[2]);
                hasMore = endIdx + 1 < total;
            } catch (Exception ignore) {}
        }
        return new SearchResponse(data, hasMore);
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getMultipleTickets(String sessionToken, List<Long> ids, boolean expandDropdowns) {
        StringBuilder q = new StringBuilder("expand_dropdowns={expand}");
        Map<String, Object> vars = new HashMap<>();
        vars.put("expand", expandDropdowns ? "true" : "false"); // si preferís: "1"/"0"

        for (int i = 0; i < ids.size(); i++) {
            q.append("&items[").append(i).append("][itemtype]={type").append(i).append("}")
                    .append("&items[").append(i).append("][items_id]={id").append(i).append("}");
            vars.put("type" + i, "Ticket");
            vars.put("id"   + i, ids.get(i));
        }

        var uri = UriComponentsBuilder.fromHttpUrl(props.baseUrl())
                .path("/getMultipleItems")
                .query(q.toString())
                .encode()               // <-- escapa [] a %5B %5D, y valores especiales
                .buildAndExpand(vars)   // <-- reemplaza {expand}, {type0}, {id0}, ...
                .toUri();

        List<Map<String, Object>> body = rest.get()
                .uri(uri)
                .header("App-Token", props.appToken())
                .header("Session-Token", sessionToken)
                .header("X-GLPI-Sanitized-Content", "false")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .getBody();

        return body != null ? body : List.of();
    }

    /**
     * Simple DTO q representa un folllowup de GLPI.
     */
    public record GlpiFollowup(long id, long glpiTicketId, String content, Instant createdAt, String author) {
        public GlpiFollowup(long id, long glpiTicketId, String content, Instant createdAt) {
            this(id, glpiTicketId, content, createdAt, null);
        }
    }

    @SuppressWarnings("unchecked")
    public GlpiFollowup createEventNote(String sessionToken,
                                        long glpiTicketId,
                                        String content,
                                        String visibility) {
        Map<String, Object> body = Map.of(
                "input", Map.of(
                        "itemtype", "Ticket",
                        "items_id", glpiTicketId,
                        "content", content
                )
        );

        Map<String, Object> resp = rest.post()
                .uri(props.baseUrl() + "/ITILFollowup?session_write=true")
                .header("Content-Type", "application/json")
                .header("Session-Token", sessionToken)
                .header("App-Token", props.appToken())
                .header("X-GLPI-Sanitized-Content", "false")
                .body(body)
                .retrieve()
                .body(Map.class);

        long id = 0L;
        if (resp != null) {
            Object obj = resp.get("id");
            if (obj instanceof Number n) id = n.longValue();
        }
        return new GlpiFollowup(id, glpiTicketId, content, Instant.now());
    }
}
