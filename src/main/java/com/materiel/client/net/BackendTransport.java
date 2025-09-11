package com.materiel.client.net;

import com.materiel.client.backend.invoker.ApiClient;
import com.materiel.client.config.AppConfig;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

/**
 * Configure le transport HTTP pour le SDK backend.
 */
public class BackendTransport {
    private final ApiClient apiClient;

    public BackendTransport(AppConfig config) {
        this.apiClient = new ApiClient();
        this.apiClient.setBasePath(config.getApiBaseUrl());

        OkHttpClient.Builder builder = apiClient.getHttpClient().newBuilder()
                .readTimeout(Duration.ofSeconds(30))
                .callTimeout(Duration.ofSeconds(30));

        final String token = config.getApiToken();
        final String user = config.getApiBasicUser();
        final String pass = config.getApiBasicPass();

        if (token != null && !token.isEmpty()) {
            builder.addInterceptor(chain -> addHeader(chain, "Authorization", "Bearer " + token));
        } else if (user != null && pass != null) {
            String basic = Base64.getEncoder().encodeToString((user + ":" + pass).getBytes(StandardCharsets.UTF_8));
            builder.addInterceptor(chain -> addHeader(chain, "Authorization", "Basic " + basic));
        }

        this.apiClient.setHttpClient(builder.build());
    }

    private Response addHeader(Interceptor.Chain chain, String name, String value) throws IOException {
        Request request = chain.request().newBuilder().addHeader(name, value).build();
        return chain.proceed(request);
    }

    public ApiClient getApiClient() {
        return apiClient;
    }
}
