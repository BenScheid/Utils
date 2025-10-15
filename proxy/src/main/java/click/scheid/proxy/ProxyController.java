package click.scheid.proxy;

import java.io.IOException;
import java.util.Collections;
import java.util.logging.Logger;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/proxy")
@CrossOrigin(origins = "127.0.0.1:5500")
public class ProxyController {
	public static final Logger LOGGER = Logger.getLogger(ProxyController.class.getName());
	private final RestTemplate restTemplate = new RestTemplate();

	@RequestMapping(value = "/**", method = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
			RequestMethod.DELETE, RequestMethod.OPTIONS })
	public ResponseEntity<byte[]> proxy(HttpServletRequest request) throws IOException {
		System.out.println("proxy ---");
		byte[] body = request.getInputStream().readAllBytes();
		HttpMethod method = HttpMethod.valueOf(request.getMethod());

		String backendUrl = "http://100.118.20.51:8443" + request.getRequestURI().replaceFirst("/proxy", "");

		HttpHeaders headers = new HttpHeaders();
		Collections.list(request.getHeaderNames()).forEach(h -> {
			if (!h.equalsIgnoreCase("host")) { // exclude Host header
				headers.add(h, request.getHeader(h));
			}
		});

		HttpEntity<byte[]> entity = (body.length > 0) ? new HttpEntity<>(body, headers) : new HttpEntity<>(headers);

		try {
			LOGGER.info("Passing " + method.toString() + " request to: " + backendUrl);
			ResponseEntity<byte[]> response = restTemplate.exchange(backendUrl, method, entity, byte[].class);
			return ResponseEntity.status(response.getStatusCode()).headers(response.getHeaders())
					.body(response.getBody());
		} catch (Exception ex) {
			LOGGER.severe("Error for " + method.toString() + " request to: " + backendUrl);
			return ResponseEntity.badRequest().build();
		}
	}
}
