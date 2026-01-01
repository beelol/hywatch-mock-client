package com.hywatch.client;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Main {
    private static final String LOCAL_URL = "http://localhost:4000/events/player-data";
    private static final String PROD_BASE_URL = "https://hywatchbackend.hydmg.com";
    private static final String PROD_EVENT_URL = PROD_BASE_URL + "/events/player-data";
    private static final String AUTH_URL = "https://auth.hydmg.com";
    
    private static final String PLAYER_ID = "PLR-" + UUID.randomUUID().toString().substring(0, 8);
    private static final String PLAYER_NAME = "DevelPlayer";
    // Simulating a server ID
    private static final String SERVER_ID = "DevelServer_" + UUID.randomUUID().toString().substring(0, 8);

    private static String cachedToken = "";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Hywatch Client (" + PLAYER_ID + ")");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 450);
            frame.setLayout(new BorderLayout());

            // Header (Logo + Title)
            JPanel headerPanel = new JPanel(new BorderLayout());
            JLabel titleLabel = new JLabel("Hywatch Client", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
            headerPanel.add(titleLabel, BorderLayout.CENTER);

            // Try to load logo
            URL logoUrl = Main.class.getResource("/logo.png");
            if (logoUrl != null) {
                ImageIcon icon = new ImageIcon(logoUrl);
                JLabel logoLabel = new JLabel(icon);
                headerPanel.add(logoLabel, BorderLayout.NORTH);
            }
            frame.add(headerPanel, BorderLayout.NORTH);

            // Center (Status/Logs)
            JTextArea logArea = new JTextArea();
            logArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(logArea);
            frame.add(scrollPane, BorderLayout.CENTER);

            // Footer (Controls)
            JPanel footerPanel = new JPanel();
            footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));

            // Config Panel (Server Only)
            JPanel configPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            
            JLabel serverLabel = new JLabel("Server:");
            String[] servers = {"Localhost", "Production"};
            JComboBox<String> serverCombo = new JComboBox<>(servers);
            
            configPanel.add(serverLabel);
            configPanel.add(serverCombo);
            
            footerPanel.add(configPanel);

            // Buttons (Grid Layout to prevent hiding on small screens)
            JPanel buttonPanel = new JPanel(new GridLayout(0, 3, 5, 5));
            JButton btnStats = new JButton("Stats");
            JButton btnKill = new JButton("Kill");
            JButton btnDeath = new JButton("Death");
            JButton btnBuild = new JButton("Build");
            JButton btnMine = new JButton("Mine");

            buttonPanel.add(btnStats);
            buttonPanel.add(btnKill);
            buttonPanel.add(btnDeath);
            buttonPanel.add(btnBuild);
            buttonPanel.add(btnMine);
            footerPanel.add(buttonPanel);

            frame.add(footerPanel, BorderLayout.SOUTH);

            // Actions
            btnStats.addActionListener(e -> sendEvent(serverCombo, logArea, "stat_update"));
            btnKill.addActionListener(e -> sendEvent(serverCombo, logArea, "kill"));
            btnDeath.addActionListener(e -> sendEvent(serverCombo, logArea, "death"));
            btnBuild.addActionListener(e -> sendEvent(serverCombo, logArea, "building"));
            btnMine.addActionListener(e -> sendEvent(serverCombo, logArea, "block_break"));

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            log("Client started. Player ID: " + PLAYER_ID, logArea);
            
            // Auto Login
            new Thread(() -> {
                String identity = System.getenv("HYWATCH_BOT_IDENTITY");
                String password = System.getenv("HYWATCH_BOT_PASSWORD");
                
                if (identity != null && password != null && !identity.isEmpty() && !password.isEmpty()) {
                    log("Attempting auto-login against " + AUTH_URL + "...", logArea);
                    try {
                        String token = PocketBaseAuth.authenticate(AUTH_URL, identity, password);
                        cachedToken = token;
                        SwingUtilities.invokeLater(() -> {
                            log("Auto-login successful.", logArea);
                            log("Token: " + (token.length() > 10 ? token.substring(0, 10) + "..." : token), logArea);
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        SwingUtilities.invokeLater(() -> log("Auto-login failed: " + e.getMessage(), logArea));
                    }
                } else {
                    SwingUtilities.invokeLater(() -> log("Env vars HYWATCH_BOT_IDENTITY/PASSWORD not found.", logArea));
                }
            }).start();
        });
    }

    private static void sendEvent(JComboBox<String> serverCombo, JTextArea logArea, String type) {
        String serverSelection = (String) serverCombo.getSelectedItem();
        String url = "Production".equals(serverSelection) ? PROD_EVENT_URL : LOCAL_URL;
        String token = cachedToken;
        
        long now = System.currentTimeMillis();
        
        Map<String, Object> payloadData = new HashMap<>();
        
        // Common generators
        Map<String, Object> pos = new HashMap<>();
        pos.put("x", Math.random() * 100);
        pos.put("y", 64.0);
        pos.put("z", Math.random() * 100);

        Map<String, Object> stats = new HashMap<>();
        stats.put("health", 100);
        stats.put("maxHealth", 100);
        stats.put("level", 5 + (int)(Math.random() * 10));
        stats.put("xp", (int)(Math.random() * 100));

        switch (type) {
            case "stat_update":
                payloadData.put("summary", "Updated statistics");
                payloadData.put("stats", stats);
                payloadData.put("position", pos);
                break;
                
            case "kill":
                payloadData.put("summary", "Slew Mob-" + (int)(Math.random() * 100) + " using diamond_sword");
                payloadData.put("victim", "Mob-" + (int)(Math.random() * 100));
                payloadData.put("weapon", "diamond_sword");
                break;
                
            case "death":
                payloadData.put("summary", "Died in combat");
                payloadData.put("cause", "combat");
                break;
                
            case "building":
                boolean placed = Math.random() > 0.5;
                int count = 1 + (int)(Math.random() * 10);
                payloadData.put("summary", (placed ? "Placed " : "Broke ") + count + " blocks");
                Map<String, Object> buildMeta = new HashMap<>();
                buildMeta.put("action", placed ? "placed" : "broke");
                buildMeta.put("count", count);
                payloadData.put("metadata", buildMeta);
                break;
                
            case "block_break":
                payloadData.put("summary", "Mined block");
                Map<String, Object> breakMeta = new HashMap<>();
                breakMeta.put("count", 1 + (int)(Math.random() * 5));
                payloadData.put("metadata", breakMeta);
                break;
        }

        EventPayload payload = new EventPayload(type, PLAYER_ID, PLAYER_NAME, SERVER_ID, now, payloadData);

        log("Sending " + type + "...", logArea);
        if (token.isEmpty()) log("WARNING: No Auth Token available!", logArea);

        new Thread(() -> {
            try {
                String response = EventSender.sendEvent(url, payload, token);
                SwingUtilities.invokeLater(() -> log("Success (" + type + "): " + response, logArea));
            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> log("Error (" + type + "): " + ex.getMessage(), logArea));
            }
        }).start();
    }

    private static void log(String msg, JTextArea area) {
        System.out.println("[ClientLog] " + msg);
        area.append(msg + "\n");
        area.setCaretPosition(area.getDocument().getLength());
    }
}
