package com.desbloqueocelusa.front;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpClient {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    // Crea un cliente estático para reutilizar conexiones
    private static final OkHttpClient client = new OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)   // timeout de conexión
        .writeTimeout(15, TimeUnit.SECONDS)     // timeout al escribir
        .readTimeout(30, TimeUnit.SECONDS)      // timeout al leer respuesta
        .build();

    public static void postJson(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request req = new Request.Builder()
            .url(url)
            .post(body)
            .build();

        try (Response resp = client.newCall(req).execute()) {
            if (!resp.isSuccessful()) {
                throw new IOException("HTTP " + resp.code() + ": " + resp.message());
            }
        }
    }
}
