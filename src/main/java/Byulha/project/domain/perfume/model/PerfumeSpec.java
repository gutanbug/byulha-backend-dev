package Byulha.project.domain.perfume.model;

import Byulha.project.domain.perfume.model.entity.Perfume;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;

public class PerfumeSpec {

    public static Specification<Perfume> withForGender(String forGender) {
        if (forGender == null || forGender.equals("null")) {
            return Specification.where(null);
        }

        return (root, query, builder) ->
                builder.equal(root.get("forGender"), ForGender.valueOf(forGender));
    }

    public static Specification<Perfume> withSilage(String sillage) {
        if (sillage == null || sillage.equals("null")) {
            return Specification.where(null);
        }

        return (root, query, builder) ->
                builder.equal(root.get("sillage"), Sillage.valueOf(sillage));

    }


    public static Specification<Perfume> withLongevity(String longevity) {
        if (longevity == null || longevity.equals("null")) {
            return Specification.where(null);
        }

        return (root, query, builder) ->
                builder.equal(root.get("longevity"), Longevity.valueOf(longevity));
    }

    public static Specification<Perfume> withIsDesc(boolean isDesc) {
        return (root, query, builder) -> {
            if(isDesc){
                query.orderBy(builder.desc(root.get("rating")));
            }
            return builder.conjunction();
        };
    }
}
