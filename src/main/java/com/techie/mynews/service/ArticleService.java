package com.techie.mynews.service;

import com.techie.mynews.model.Article;
import com.techie.mynews.repository.ArticleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;

    @Transactional
    public void persistArticleBatch(List<Article> articlesToSave) {
        if (articlesToSave.isEmpty()) return;

        articleRepository.saveAll(articlesToSave);
    }
}
