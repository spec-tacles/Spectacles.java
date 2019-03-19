package com.spectacles.entities.gateway;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.spectacles.entities.Routes;
import com.spectacles.gateway.Shard;
import okhttp3.*;

import java.io.IOException;

/**
 * Manages the GatewayBot url and caches it after initialization
 */
public class GatewayBot {

    private String url;
    private int shards;
    @JsonProperty("session_start_limit")
    private SessionStartLimit sessionStartLimit;

    private GatewayBot() {
    }

    /**
     * Gets the gateway details (for bots) asynchronously
     *
     * @param shard  the shard to fetch for
     * @param client the http client
     * @return the gateway details (for bots)
     */
    public static ListenableFuture<GatewayBot> getAsync(Shard shard, OkHttpClient client) {
        SettableFuture<GatewayBot> future = SettableFuture.create();
        Request request = new Request.Builder()
                .get()
                .url(Routes.API_BASE_URL.concat(Routes.GATEWAYBOT))
                .addHeader("Authorization", Routes.getAuthorizationHeader(shard.getToken()))
                .addHeader("User-Agent", Routes.USERAGENT)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                future.setException(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody body = response.body()) {
                    if (body == null) {
                        future.setException(new IOException("Response body is empty!"));
                        return;
                    }
                    ObjectMapper om = new ObjectMapper();
                    future.set(om.readValue(body.charStream(), GatewayBot.class));
                }
            }
        });

        return future;

    }

    public String getUrl() {
        return url;
    }

    public int getShards() {
        return shards;
    }

    public SessionStartLimit getSessionStartLimit() {
        return sessionStartLimit;
    }

    public class SessionStartLimit {

        private int total;

        private int remaining;

        @JsonProperty("reset_after")
        private int resetAfter;

        public int getTotal() {
            return total;
        }

        public int getRemaining() {
            return remaining;
        }

        public int getResetAfter() {
            return resetAfter;
        }
    }
}
