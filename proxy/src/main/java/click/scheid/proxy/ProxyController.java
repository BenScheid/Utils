package click.scheid.proxy;


import java.util.Collections;
import java.util.logging.Logger;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/proxy")
public class ProxyController {
	public static final Logger LOGGER = Logger.getLogger(ProxyController.class.getName());
	private final RestTemplate restTemplate = new RestTemplate();

	@RequestMapping(value = "/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<byte[]> proxy(HttpServletRequest request,
                                        @RequestBody(required = false) byte[] body) {
        HttpMethod method = HttpMethod.valueOf(request.getMethod());

        String backendUrl = "http://100.118.20.51:8443" + request.getRequestURI().replaceFirst("/proxy", "");

        HttpHeaders headers = new HttpHeaders();
        Collections.list(request.getHeaderNames())
                   .forEach(h -> headers.add(h, request.getHeader(h)));

        HttpEntity<byte[]> entity = new HttpEntity<>(body, headers);

        try {
        	LOGGER.info("Passing request to: " + backendUrl);
        	ResponseEntity<byte[]> response = restTemplate.exchange(backendUrl, method, entity, byte[].class);
        	return ResponseEntity.status(response.getStatusCode())
                    .headers(response.getHeaders())
                    .body(response.getBody());
        } catch (Exception ex) {
        	LOGGER.severe("Error for request to: " + backendUrl);
        	return ResponseEntity.badRequest().build();
        }        
    }
}
