package reconciler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@RestController
@SpringBootApplication
public class Reconciler {

    private static final Logger log = LoggerFactory.getLogger(Reconciler.class);
    private static final String SERVICE_ADDR = "http://localhost:8090";

    public static void main(String[] args) {
        SpringApplication.run(Reconciler.class, args);
    }

    @PostMapping(value = "/reconcile")
    public void reconcile(@RequestBody String body) throws IOException {
        log.info("new req {}", body.length());
        String[] req = body.split("&");
        String uri = req[0].split("=")[1];
        uri = uri.replaceFirst("/server", "");
        String srcData = req[1].split("=")[1];

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(SERVICE_ADDR + uri, String.class);
        String dstData = response.getBody();
        log.info("reconcile src(len={}) dst(len={}) equals: {}",
                srcData.length(), dstData.length(), srcData.equals(dstData));
    }

}
