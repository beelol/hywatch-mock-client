package com.hywatch.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class PocketBaseAuth {
    private static final Gson gson = new Gson();

    public static String authenticate(String pbUrl, String identity, String password) throws Exception {
        String authEndpoint = pbUrl + "/api/collections/bots/auth-with-password";
        URL url = URI.create(authEndpoint).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);

        Map<String, String> creds = new HashMap<>();
        creds.put("identity", identity);
        creds.put("password", password);
        String jsonInputString = gson.toJson(creds);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int code = conn.getResponseCode();
        if (code < 200 || code >= 300) {
            throw new RuntimeException("Auth Failed: HTTP " + code);
        }

        try (InputStreamReader reader = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)) {
            JsonObject response = gson.fromJson(reader, JsonObject.class);
            if (response.has("token")) {
                return response.get("token").getAsString();
            } else {
                throw new RuntimeException("No token in response");
            }
        }
    }
}
