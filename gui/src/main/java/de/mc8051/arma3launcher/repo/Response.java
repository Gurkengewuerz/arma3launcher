package de.mc8051.arma3launcher.repo;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Created by gurkengewuerz.de on 27.03.2020.
 */
public class Response {

    private int statusCode;
    private HttpResponse<String> response;

    public Response(HttpResponse<String> response) {
        this.response = response;
        statusCode = response.statusCode();
    }

    public boolean isSuccessful() {
        return statusCode >= 200 && statusCode <= 299;
    }

    public HttpRequest request() {
        return response.request();
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getBody() {
        return response.body();
    }
}
