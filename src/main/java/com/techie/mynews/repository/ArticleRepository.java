package com.techie.mynews.repository;

import com.techie.mynews.model.Article;
import com.techie.mynews.model.Source;
import com.techie.mynews.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ArticleRepository extends JpaRepository<Article,String>, JpaSpecificationExecutor<Article> {
    boolean existsByUrl(String url);
}
