package name.huihui.volleyanalysis;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by yzh on 16/8/5.
 */
public class HurlStack implements HttpStack {
    private static final String HEADER_CONOTENT_TYPE = "Content-Type";

    public HurlStack() {}

    @Override
    public HttpResponse performRequest(Request request) throws IOException {
        URL parsedUrl = new URL(request.getUrl());
        HttpURLConnection connection = openConnection(parsedUrl, request);
        Map<String, String> headers = request.getHeaders();

        for (String headerName : headers.keySet()) {
            connection.addRequestProperty(headerName, headers.get(headerName));
        }
        setConnectionParametersForRequest(connection, request);

        int responseCode = connection.getResponseCode();
        if (responseCode == -1) {
            throw new IOException("Could not retrieve response code from HttpUrlConnection.");
        }
        //特别备注,以下这几个类已过时,这里为了跟Volley库保持一致仍然保留,如果在你的开发环境里没法找到这个类,请将gradle中 compileSdkVersion 降到22.
        ProtocolVersion protocolVersion = new ProtocolVersion("HTTP", 1, 1);
        StatusLine reponseStatus = new BasicStatusLine(protocolVersion, connection.getResponseCode(), connection.getResponseMessage());
        BasicHttpResponse response = new BasicHttpResponse(reponseStatus);
        for (Map.Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
            if (header.getKey() != null) {
                Header h = new BasicHeader(header.getKey(), header.getValue().get(0));
                response.addHeader(h);
            }
        }

        return response;
    }

    private static void setConnectionParametersForRequest(HttpURLConnection connection,
                                                                Request request) throws IOException {
        switch (request.getMethod()) {
            case Request.Method.DEPRECATED_GET_POST:
                byte[] postBody = request.getPostBody();
                if (postBody != null) {
                    connection.setDoOutput(true);
                    connection.setRequestMethod("POST");
                    connection.addRequestProperty(HEADER_CONOTENT_TYPE,
                            request.getBodyContentType());
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    out.write(postBody);
                    out.close();
                }
                break;
            case Request.Method.GET:
                // Not necessary to set the request method because connection defaults to GET but
                // being explicit here.
                connection.setRequestMethod("GET");
                break;
            case Request.Method.DELETE:
                connection.setRequestMethod("DELETE");
                break;
            case Request.Method.POST:
                connection.setRequestMethod("POST");
                addBodyIfExists(connection, request);
                break;
            case Request.Method.PUT:
                connection.setRequestMethod("PUT");
                addBodyIfExists(connection, request);
                break;
            case Request.Method.HEAD:
                connection.setRequestMethod("HEAD");
                break;
            case Request.Method.OPTIONS:
                connection.setRequestMethod("OPTIONS");
                break;
            case Request.Method.TRACE:
                connection.setRequestMethod("TRACE");
                break;
            case Request.Method.PATCH:
                connection.setRequestMethod("PATCH");
                addBodyIfExists(connection, request);
                break;
            default:
                throw new IllegalStateException("Unknown method type.");
        }
    }

    private static void addBodyIfExists(HttpURLConnection connection, Request request)
            throws IOException{
        byte[] body = request.getPostBody();
        if (body != null) {
            connection.setDoOutput(true);
            connection.addRequestProperty(HEADER_CONOTENT_TYPE, request.getBodyContentType());
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.write(body);
            out.close();
        }
    }

    private HttpURLConnection openConnection(URL url, Request request) throws IOException {
        HttpURLConnection connection = createConnection(url);

        connection.setConnectTimeout(request.getTimeoutMillis());
        connection.setReadTimeout(request.getTimeoutMillis());
        connection.setUseCaches(false);
        connection.setDoInput(true);

        return connection;
    }

    private HttpURLConnection createConnection(URL url) throws IOException {
        return (HttpURLConnection) url.openConnection();
    }
}
