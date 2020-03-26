package reconciler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

@RestController
@SpringBootApplication
public class Reconciler {

    private static final Logger log = LoggerFactory.getLogger(Reconciler.class);
    private static final String SERVICE_ADDR = "http://localhost:8090";

    public static void main(String[] args) {
        SpringApplication.run(Reconciler.class, args);
    }

    @PostMapping(value = "/reconcile")
    public void reconcile(HttpServletRequest request) throws IOException {
        InputStream in = request.getInputStream();
        int cnt = 0;
        byte[] buf = new byte[8124];
        int len = 0;
        while ((len = in.read(buf)) > -1) {
            cnt++;
        }
        log.info("buf count = {}", cnt);
//        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity<String> response = restTemplate.getForEntity(SERVICE_ADDR + uri, String.class);
//        String dstData = response.getBody();
//        log.info("reconcile src(len={}) dst(len={}) equals: {}",
//                srcData.length(), dstData.length(), srcData.equals(dstData));
    }

}
