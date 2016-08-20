package name.huihui.volleyanalysis;

import org.apache.http.HttpResponse;

import java.io.IOException;

/**
 * Created by yzh on 16/8/6.
 */
public interface HttpStack {
    public HttpResponse performRequest(Request request) throws IOException;
}
