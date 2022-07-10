package main.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "tags")
@Getter
@Setter
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotNull
    private String name;


    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "tag2post", joinColumns = {@JoinColumn(name="tag_id")}, inverseJoinColumns = {@JoinColumn(name = "post_id")})
    private List<Post> postList;
}
