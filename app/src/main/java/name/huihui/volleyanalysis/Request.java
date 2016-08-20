package name.huihui.volleyanalysis;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Map;

/**
 * Created by yzh on 16/8/6.
 */
public class Request {
    private static final String DEFAULT_PARAMS_ENCODING = "UTF-8";

    private static final String BODY_CONTENT_TYPE_FORMAT = "application/x-www-form-urlencoded; charset=%s";

    //用Method封装http method, 体会一下这里为何用 interface 而不是 enum
    public interface Method {
        int DEPRECATED_GET_POST = -1;
        int GET = 0;
        int POST = 1;
        int PUT = 2;
        int DELETE = 3;
        int HEAD = 4;
        int OPTIONS = 5;
        int TRACE = 6;
        int PATCH = 7;
    }

    private final int mMethod;

    private final String mUrl;

    private final int mTimeoutMillis;

    private final Map<String, String> mHeaders;

    private final Map<String, String> mPostParams;

    public Request(int httpMethod, String url, int timeoutMillis, Map<String, String> postParams, Map<String, String> headers) {
        mMethod = httpMethod;
        mUrl = url;
        mTimeoutMillis = timeoutMillis;
        mPostParams = postParams;
        mHeaders = headers;
    }

    public int getMethod() {
        return mMethod;
    }

    public String getUrl() {
        return mUrl;
    }

    protected String getParamsEncoding() {
        return DEFAULT_PARAMS_ENCODING;
    }

    public int getTimeoutMillis() {
        return mTimeoutMillis;
    }

    public byte[] getPostBody() {
        if (mPostParams != null && mPostParams.size() > 0) {
            return encodeParameters(mPostParams, getParamsEncoding());
        }

        return null;
    }

    public Map<String, String> getHeaders() {
        return mHeaders == null ? Collections.<String, String>emptyMap() : mHeaders;
    }

    public String getBodyContentType() {
        return String.format(BODY_CONTENT_TYPE_FORMAT, getParamsEncoding());
    }

    private byte[] encodeParameters(Map<String, String> postParams, String encoding) {
        StringBuilder encodeParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : postParams.entrySet()) {
                encodeParams.append(URLEncoder.encode(entry.getKey(), encoding));
                encodeParams.append("=");
                encodeParams.append(URLEncoder.encode(entry.getValue(), encoding));
                encodeParams.append("&");
            }

            return encodeParams.toString().getBytes(encoding);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Encoding not supported" + encoding, e);
        }
    }

}
