package ar.unla.gestion_eventos.glpi;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GlpiClient {
    private final GlpiProperties props;
    private final RestClient rest = RestClient.create();
    private volatile String sessionToken;

    private String authHeader() {
        var t = props.userToken();
        return (t != null && t.startsWith("user_token ")) ? t : "user_token " + t;
    }

    /** Abre sesión y guarda Session-Token */
    @SuppressWarnings("unchecked")
    public void openSession() {
        Map<String, Object> resp = rest.get()
                .uri(props.baseUrl() + "/initSession?get_full_session=true") // opcional: info extra de sesión
                .header("Authorization", authHeader())       // <-- igual que Postman
                .header("App-Token", props.appToken())       // <-- igual que Postman
                .header("Accept", "application/json")
                .retrieve()
                .body(Map.class);
        Object token = (resp != null) ? resp.get("session_token") : null;
        if (token == null) {
            throw new IllegalStateException("GLPI no devolvió 'session_token'. Verificá tokens/permisos.");
        }
        this.sessionToken = token.toString();
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
}
