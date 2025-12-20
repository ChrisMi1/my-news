package com.techie.mynews.repository;

import com.techie.mynews.model.Article;
import com.techie.mynews.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article,String> {
    boolean existsByUrl(String url);
}
