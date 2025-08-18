package ar.unla.gestion_eventos;

import ar.unla.gestion_eventos.glpi.GlpiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableConfigurationProperties(GlpiProperties.class)

@SpringBootApplication
public class GestionEventosApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionEventosApplication.class, args);
	}

}
