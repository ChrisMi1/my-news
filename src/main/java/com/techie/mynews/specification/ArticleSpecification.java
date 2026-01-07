package com.techie.mynews.specification;

import com.techie.mynews.dto.request.ArticleCriteriaRequest;
import com.techie.mynews.model.Article;
import com.techie.mynews.model.Source;
import com.techie.mynews.model.User;
import com.techie.mynews.model.UserSource;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ArticleSpecification {
    private ArticleSpecification() {

    }

    public static Specification<Article> articleSpecifications(ArticleCriteriaRequest searchCriteria,String email) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<Article, Source> sourceJoin = root.join("source");
            if (searchCriteria.getSourceName() != null) {
                predicates.add(criteriaBuilder.equal(sourceJoin.get("name"),searchCriteria.getSourceName()));
            }
            Join<Source, UserSource> userSourceJoin = sourceJoin.join("userSources");
            Join<UserSource, User> userJoin = userSourceJoin.join("user");
            predicates.add(criteriaBuilder.equal(userJoin.get("email"),email));

            query.distinct(true);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
