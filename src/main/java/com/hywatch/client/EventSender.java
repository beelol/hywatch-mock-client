package com.hywatch.client;

import com.google.gson.Gson;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class EventSender {
    private static final Gson gson = new Gson();

    public static String sendEvent(String baseUrl, EventPayload event, String authToken) throws Exception {
        URL url = URI.create(baseUrl).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        if (authToken != null && !authToken.isEmpty()) {
            conn.setRequestProperty("Authorization", "Bearer " + authToken);
        }
        conn.setDoOutput(true);

        String jsonInputString = gson.toJson(event);
        System.out.println("Sending to: " + url);
        System.out.println("Payload: " + jsonInputString);
        if (authToken != null) System.out.println("Auth: " + (authToken.length() > 10 ? authToken.substring(0, 10) + "..." : authToken));

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int code = conn.getResponseCode();
        System.out.println("Response Code: " + code);
        
        try (java.util.Scanner s = new java.util.Scanner(
                code >= 200 && code < 300 ? conn.getInputStream() : conn.getErrorStream()).useDelimiter("\\A")) {
            String response = s.hasNext() ? s.next() : "";
            System.out.println("Response Body: " + response);
            
            if (code < 200 || code >= 300) {
                throw new RuntimeException("HTTP Error " + code + ": " + response);
            }
            return response;
        }
    }
}
