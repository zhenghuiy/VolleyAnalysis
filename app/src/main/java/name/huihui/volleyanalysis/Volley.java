package name.huihui.volleyanalysis;

import android.net.http.AndroidHttpClient;
import android.os.Build;
import android.text.TextUtils;

import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.Map;

/**
 * Created by yzh on 16/8/20.
 */
public class Volley {
    private static final int TIMEOUT_MILLIS = 10 * 1000;

    public static HttpResponse performRequest(String url) {
        return performRequest(url, Request.Method.DEPRECATED_GET_POST, null);
    }

    public static HttpResponse performRequest(String url, int method, Map<String, String> body) {
        return performRequest(url, method, body, null, null);
    }

    public static HttpResponse performRequest(String url, int method, Map<String, String> body, Map<String, String> headers, HttpStack httpStack){
        if (TextUtils.isEmpty(url)) {
            return null;
        }

        if (httpStack == null) {
            httpStack = Build.VERSION.SDK_INT >= 9 ? new HurlStack() : new HttpClientStack(AndroidHttpClient.newInstance(null));
        }
        Request request = new Request(method, url, TIMEOUT_MILLIS, body, headers);

        try {
            return httpStack.performRequest(request);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
