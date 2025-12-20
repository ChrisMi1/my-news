package com.techie.mynews.service;

import com.techie.mynews.dto.response.NewsApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class NewsApiClient {
    private final WebClient webClient;

    @Value("${my-news.api.key}")
    private String apiKey;

    public NewsApiClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public NewsApiResponse fetchNewsFromNewsApi(String externalIds) {
        log.info("Starting fetch news with externalIds: " + externalIds);
        try{
            NewsApiResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/top-headlines")
                            .queryParam("sources", externalIds)
                            .queryParam("apiKey", apiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(NewsApiResponse.class)
                    .block();
            if (response == null || CollectionUtils.isEmpty(response.getArticles())) return null;
            return response;
        } catch (Exception ex) {
            log.info("Unable to fetch news due to the following error: " + ex.getMessage());
            return null;
        }
    }

}
