package router;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostFilter extends ZuulFilter {
    private static final Logger log = LoggerFactory.getLogger(PostFilter.class);

    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        return ctx.getRequest().getRequestURI().endsWith("/reconcile/true");
    }

    @Override
    public Object run() throws ZuulException {
        log.info("start interceptor filter");
        RequestContext ctx = RequestContext.getCurrentContext();
        Interceptor interceptor = new Interceptor(ctx);
        ctx.setResponseDataStream(interceptor);
        return null;
    }
}

