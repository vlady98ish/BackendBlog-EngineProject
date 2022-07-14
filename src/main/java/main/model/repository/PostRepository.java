package main.model.repository;


import main.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;




@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
}
