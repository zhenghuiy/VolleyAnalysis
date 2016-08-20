package name.huihui.volleyanalysis;

import android.net.http.AndroidHttpClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.util.Map;

/**
 * Created by yzh on 16/8/19.
 */
public class HttpClientStack implements HttpStack {
    private static final String HEADER_CONOTENT_TYPE = "Content-Type";

    protected final HttpClient mClient;



    public HttpClientStack(HttpClient client) {
        mClient = client;
    }

    private static void addHeaders(HttpUriRequest httpRequest, Map<String, String> headers) {
        for (String key : headers.keySet()) {
            httpRequest.setHeader(key, headers.get(key));
        }
    }


    @Override
    public HttpResponse performRequest(Request request) throws IOException {
        HttpUriRequest httpRequest = createHttpRequest(request);
        addHeaders(httpRequest, request.getHeaders());
        HttpParams httpParams = httpRequest.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
        HttpConnectionParams.setSoTimeout(httpParams, request.getTimeoutMillis());

        return mClient.execute(httpRequest);
    }

    private HttpUriRequest createHttpRequest(Request request) {
        switch (request.getMethod()) {
            case Request.Method.DEPRECATED_GET_POST:
                //没有选定http method时,根据body来判断该使用get还是post
                byte[] postBody = request.getPostBody();
                if (postBody != null) {
                    HttpPost postRequest = new HttpPost(request.getUrl());
                    postRequest.addHeader(HEADER_CONOTENT_TYPE, request.getBodyContentType());
                    setEntityIfNonEmptyBody(postRequest, request);
                    return postRequest;
                } else {
                    return new HttpGet(request.getUrl());
                }

            case Request.Method.GET:
                return new HttpGet(request.getUrl());

            case Request.Method.DELETE:
                return new HttpDelete(request.getUrl());

            case Request.Method.POST:
                HttpPost postRequest = new HttpPost(request.getUrl());
                postRequest.addHeader(HEADER_CONOTENT_TYPE, request.getBodyContentType());
                setEntityIfNonEmptyBody(postRequest, request);
                return postRequest;

            case Request.Method.PUT:
                HttpPut putRequest = new HttpPut(request.getUrl());
                putRequest.addHeader(HEADER_CONOTENT_TYPE, request.getBodyContentType());
                setEntityIfNonEmptyBody(putRequest, request);
                return putRequest;

            case Request.Method.HEAD:
                return new HttpHead(request.getUrl());

            case Request.Method.OPTIONS:
                return new HttpOptions(request.getUrl());

            case Request.Method.TRACE:
                return new HttpTrace(request.getUrl());

            case Request.Method.PATCH:
                HttpPut patchRequest = new HttpPut(request.getUrl());
                patchRequest.addHeader(HEADER_CONOTENT_TYPE, request.getBodyContentType());
                setEntityIfNonEmptyBody(patchRequest, request);
                return patchRequest;

            default:
                //这里与volley不大一样,个人觉得这样兼容下比较好
                byte[] pBody = request.getPostBody();
                if (pBody != null) {
                    HttpPost pRequest = new HttpPost(request.getUrl());
                    pRequest.addHeader(HEADER_CONOTENT_TYPE, request.getBodyContentType());
                    setEntityIfNonEmptyBody(pRequest, request);
                    return pRequest;
                } else {
                    return new HttpGet(request.getUrl());
                }
        }
    }

    private void setEntityIfNonEmptyBody(HttpEntityEnclosingRequestBase httpRequet, Request request) {
        byte[] body = request.getPostBody();
        if (body != null) {
            HttpEntity entity = new ByteArrayEntity(body);
            httpRequet.setEntity(entity);
        }
    }
}
