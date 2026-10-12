package com.techie.mynews.specification;

import com.techie.mynews.dto.request.ArticleCriteriaRequest;
import com.techie.mynews.model.Article;
import com.techie.mynews.model.Source;
import com.techie.mynews.model.User;
import com.techie.mynews.model.UserSource;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ArticleSpecification {
    private ArticleSpecification() {

    }

    public static Specification<Article> articleSpecifications(ArticleCriteriaRequest searchCriteria,String email) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Join<Article, Source> joinSource = root.join("source");

            if (searchCriteria != null && StringUtils.isNotBlank(searchCriteria.getSourceName())) {
                predicates.add(criteriaBuilder.equal(joinSource.get("name"),searchCriteria.getSourceName().strip()));
            }

            Subquery<Integer> subquery = query.subquery(Integer.class);
            Root<UserSource> userSourceRoot = subquery.from(UserSource.class);

            Join<UserSource,User> joinUser = userSourceRoot.join("user");

            Predicate sourceMatch = criteriaBuilder.equal(userSourceRoot.get("source"), joinSource);
            Predicate emailMatch = criteriaBuilder.equal(joinUser.get("email"), email);

            subquery.select(criteriaBuilder.literal(1)).where(criteriaBuilder.and(sourceMatch, emailMatch));

            predicates.add(criteriaBuilder.exists(subquery));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
