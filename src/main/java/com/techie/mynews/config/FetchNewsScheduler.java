package com.techie.mynews.config;

import com.techie.mynews.repository.SourceRepository;
import com.techie.mynews.service.FetchNewsService;
import com.techie.mynews.service.NewsApiClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
@Slf4j
@AllArgsConstructor
public class FetchNewsScheduler {

    private final SourceRepository sourceRepository;
    private final NewsApiClient newsApiClient;
    private final FetchNewsService fetchNewsService;
    @Scheduled(cron = "${cron.job}")
    private void fetchNews() {
        LocalDateTime startedAt = LocalDateTime.now();
        log.info("News fetch scheduler triggered");
        fetchNewsService.fetchAndStoreNews();
        log.info("Scheduler started at: {}", startedAt);
        log.info("Scheduler finished at: {}", LocalDateTime.now());
    }
}
