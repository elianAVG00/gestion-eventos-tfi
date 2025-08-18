package ar.unla.gestion_eventos.glpi;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "glpi")
public record GlpiProperties(String baseUrl, String appToken, String userToken) {}

