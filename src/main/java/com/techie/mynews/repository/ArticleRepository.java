package com.techie.mynews.repository;

import com.techie.mynews.model.Article;
import com.techie.mynews.model.Source;
import com.techie.mynews.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface ArticleRepository extends JpaRepository<Article,String>, JpaSpecificationExecutor<Article> {
    boolean existsByUrl(String url);

    @Query("SELECT a.url FROM Article a WHERE a.url IN (:incomingUrls)")
    Set<String> findExistingUrls(@Param("incomingUrls") Collection<String> incomingUrls);
}
