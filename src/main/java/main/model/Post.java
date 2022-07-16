package main.model;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "is_active")
    private byte isActive;
    @Enumerated(EnumType.STRING) //TODO: По умолчанию поставить NEW

    @Column(name = "moderation_status", nullable = false, columnDefinition = "enum('NEW, 'ACCEPTED','DECLINED') default 'NEW'")
    private Status moderationStatus;
    @Column(name = "moderator_id")
    private int moderatorId;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;
    @Column(nullable = false)
    private LocalDateTime time;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String text;
    @Column(name = "view_count")
    private int viewCount;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "tag2post", joinColumns = {@JoinColumn(name = "post_id")}, inverseJoinColumns = {@JoinColumn(name = "tag_id")})
    private List<Tag> tagList;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostComments> postComments;
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostVote> postVotes;


}
