package main.model.repository;

import main.model.Tag2Post;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;

@Repository
public interface Tag2PostRepository extends CrudRepository<Tag2Post, Integer> {
}
