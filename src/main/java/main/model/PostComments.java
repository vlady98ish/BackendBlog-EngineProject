package main.model;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "post_comments")
@Getter
@Setter
@EqualsAndHashCode
public class PostComments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "parent_id")
    private Integer parentId;


    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="post_id")
    private Post post;


    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    private User user;

    @NotNull
    private Date time;
    @Type(type = "text")
    @NotNull
    private String text;


}