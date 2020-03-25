package router;

import com.netflix.zuul.context.RequestContext;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class Interceptor extends BufferedInputStream {

    private static final Logger log = LoggerFactory.getLogger(Interceptor.class);
    private static final String RECONCILER_URI = "http://localhost:8095/reconcile";

    private PipedInputStream in;
    private PipedOutputStream out;

    public Interceptor(RequestContext ctx) {
        super(ctx.getResponseDataStream());
        try {
            in = new PipedInputStream();
            out = new PipedOutputStream(in);
            String uri = ctx.getRequest().getRequestURI();
            out.write(("uri=" + uri + "&").getBytes());
            // rest of the body will be writen in read method
            out.write("body=".getBytes());
            HttpWrapper http = new HttpWrapper(uri, in);
            new Thread(http).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
        }).start();
    }

    @Override
    public synchronized int read(byte[] bytes, int i, int i1) throws IOException {
        int len = super.read(bytes, i, i1);
        if (len < 0) {
            log.info("close http post stream");
            out.close();
        } else {
            out.write(bytes, i, len);
        }
        return len;
    }

    @Override
    public void close() throws IOException {
        log.info("close service input stream");
        super.close();
    }

    private static class HttpWrapper implements Runnable {
        private String uri;
        private InputStream in;

        public HttpWrapper(String uri, InputStream in) {
            this.uri = uri;
            this.in = in;
        }

        @Override
        public void run() {
            try {
                CloseableHttpClient client = HttpClientBuilder.create().build();
                HttpPost post = new HttpPost(RECONCILER_URI);
                post.setEntity(new InputStreamEntity(in));
                HttpResponse response = client.execute(post);
                log.info("http reconcile response: {}", response.getStatusLine().getStatusCode());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

