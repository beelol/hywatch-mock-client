package com.hywatch.client;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class EventPayload {
    @SerializedName("event_type")
    private String eventType;
    
    @SerializedName("player_id")
    private String playerId;
    
    @SerializedName("player_name")
    private String playerName;

    @SerializedName("server_id")
    private String serverId;
    
    @SerializedName("timestamp_ms")
    private long timestamp;
    
    @SerializedName("payload")
    private Map<String, Object> metadata;

    public EventPayload(String eventType, String playerId, String playerName, String serverId, long timestamp, Map<String, Object> metadata) {
        this.eventType = eventType;
        this.playerId = playerId;
        this.playerName = playerName;
        this.serverId = serverId;
        this.timestamp = timestamp;
        this.metadata = metadata;
    }

    // Getters and Setters (if needed, Gson uses fields)
}
