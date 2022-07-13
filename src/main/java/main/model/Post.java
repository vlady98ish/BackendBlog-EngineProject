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
    private Integer id;
    @Column(name = "is_active")
    private Byte isActive;
    @Enumerated(EnumType.STRING) //TODO: По умолчанию поставить NEW
    @Column(name = "moderation_status")
    @NotNull
    private Status moderationStatus;
    @Column(name = "moderator_id")
    private Integer moderatorId;
    @ManyToOne(cascade = CascadeType.ALL)
    private User user;
    @NotNull
    private Timestamp time;
    @NotNull
    @Type(type = "text")
    private String title;
    @NotNull
    @Type(type = "text")
    private String text;
    @Column(name="view_count")
    private Integer viewCount;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name="tag2post", joinColumns = {@JoinColumn(name = "post_id")}, inverseJoinColumns = {@JoinColumn(name = "tag_id")})
    private List<Tag> tagList;

    @OneToMany
    @JoinColumn(name = "post_id")
    private List<PostComments> postComments;
    @OneToMany
    @JoinColumn(name = "post_id")
    private List<PostVote> postVotes;




}
