package main.model;


import lombok.Getter;
import lombok.Setter;


import javax.persistence.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "post_comments")
@Getter
@Setter
public class PostComments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(nullable = false)
    private LocalDateTime time;

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private PostComments parentComment;


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id")
    private Post post;


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;


}
