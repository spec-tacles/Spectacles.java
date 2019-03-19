package com.spectacles.entities;

public class Routes {
    public final static String API_BASE_URL = "https://discordapp.com/api";
    public final static String USERAGENT = "DiscordBot (https://github.com/spec-tacles, v1)";
    public final static String GATEWAYBOT = "/gateway/bot";

    public static String getAuthorizationHeader(String token) {
        return String.format("Bot %s", token);
    }
}
