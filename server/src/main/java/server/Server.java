package server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@RestController
@SpringBootApplication
public class Server {

    private static final Logger log = LoggerFactory.getLogger(Server.class);

    private static String makeString(int size, char c) {
        char[] chars = new char[size];
        Arrays.fill(chars, c);
        return new String(chars);
    }

    public static void main(String[] args) {
        SpringApplication.run(Server.class, args);
    }

    @RequestMapping(value = "/size-bytes/{size}/nchunks/{nchunks}/interval-ms/{interval}/reconcile/{reconcile}")
    public void response(HttpServletResponse response,
                         @PathVariable("size") int size,
                         @PathVariable("nchunks") int nchunks,
                         @PathVariable("interval") int interval) {
        try {
            response.setContentType("txt/plain");
            ServletOutputStream stream = response.getOutputStream();
            log.info("req: size={}, n={}, interval={}", size, nchunks, interval);
            for (int i = 0; i < nchunks; i++) {
                int chunkSize = size / nchunks;
                String chunk = makeString(chunkSize, (char) (97 + i));
                stream.println(chunk);
                stream.flush();
                Thread.sleep(interval);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}

