package main.model;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name="posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "is_active")
    private byte isActive;
    @Enumerated(EnumType.STRING) //TODO: По умолчанию поставить NEW
    @Column(name = "moderation_status")
    @NotNull
    private Status moderationStatus;
    @Column(name = "moderator_id")
    private int moderatorId;
    @ManyToOne(cascade = CascadeType.ALL)
    private User user;
    @NotNull
    private Timestamp time;
    @NotNull
    @Type(type = "text")
    private String title;
    @NotNull
    @Column(name="view_count")
    private int viewCount;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name="tag2post", joinColumns = {@JoinColumn(name = "post_id")}, inverseJoinColumns = {@JoinColumn(name = "tag_id")})
    private List<Tag> tagList;




}
