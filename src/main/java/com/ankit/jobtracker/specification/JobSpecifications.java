package com.ankit.jobtracker.specification;

import com.ankit.jobtracker.entity.Job;
import org.springframework.data.jpa.domain.Specification;

public class JobSpecifications {

    public static Specification<Job> hasLocation(String location) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("location")),
                        location.toLowerCase()
                );
    }

    public static Specification<Job> hasKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            String pattern = "%" + keyword.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), pattern)
            );
        };
    }
}