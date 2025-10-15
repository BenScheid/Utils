package click.scheid.proxy.config;

import java.util.logging.Logger;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class WebMvcConfig {

	@Bean
	public WebMvcConfigurer corsConfig() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry cr) {
				Logger.getLogger(WebMvcConfig.class.getName()).info("Creating cors config");
				cr.addMapping("/proxy/**")
				.allowedOrigins("http://192.168.178.95:5500", "http://127.0.0.1:5500", "http://localhost:5500")
				.allowedMethods("*")
				.allowCredentials(true)
				.maxAge(3600);
			}
		};
	}

}
