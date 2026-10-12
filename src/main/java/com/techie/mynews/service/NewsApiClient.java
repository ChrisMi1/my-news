package com.techie.mynews.service;

import com.techie.mynews.dto.response.NewsApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class NewsApiClient {
    private final WebClient webClient;

    @Value("${my-news.api.key}")
    private String apiKey;

    public NewsApiClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public CompletableFuture<NewsApiResponse> fetchNewsFromNewsApi(String externalIds) {
        log.info("Dispatching non-blocking HTTP request for sources: [{}]", externalIds);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/top-headlines")
                        .queryParam("sources", externalIds)
                        .queryParam("apiKey", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(NewsApiResponse.class)
                .timeout(Duration.ofSeconds(10))
                .filter(response -> response != null && !CollectionUtils.isEmpty(response.getArticles()))
                .toFuture()
                .exceptionally(ex -> {
                    log.error("HTTP call failed for sources [{}]: {}", externalIds, ex.getMessage());
                    return null;
                });
    }

}
