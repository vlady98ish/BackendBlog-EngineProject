package main.model.repository;

import main.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface TagRepository extends JpaRepository<Tag,Integer> {
    @Query("FROM Tag as tag where tag.name Like ?1%")
    List<Tag> findAllByName(String name);

    Optional<Tag> findByName(String name);




}
