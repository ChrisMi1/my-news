package com.techie.mynews.specification;

import com.techie.mynews.dto.request.SourceCriteriaRequest;
import com.techie.mynews.model.Source;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;


public class SourceSpecification {
    private SourceSpecification() {

    }
    public static Specification<Source> sourceSpecifications(SourceCriteriaRequest searchCriteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (searchCriteria.getName() != null) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.upper(root.get("name")),
                        "%" + searchCriteria.getName().toUpperCase() + "%"
                ));
            }
            if (searchCriteria.getCountry() != null) {
                predicates.add(criteriaBuilder.equal(root.get("country"),searchCriteria.getCountry()));
            }

            return  criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
