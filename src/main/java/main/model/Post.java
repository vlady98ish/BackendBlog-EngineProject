package main.model;

import lombok.*;


import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter

@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "is_active")
    private byte isActive;
     //TODO: По умолчанию поставить NEW

    @Column(name = "moderation_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status moderationStatus;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String text;

    @Column(nullable = false)
    private LocalDateTime time;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String title;

    @Column(name = "view_count")
    private int viewCount;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "moderator_id", referencedColumnName = "id")
    private User moderatedBy;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "tag2post", joinColumns = {@JoinColumn(name = "post_id")}
            , inverseJoinColumns = {@JoinColumn(name = "tag_id")})
    private List<Tag> tagList;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostComments> postComments;
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostVote> postVotes;


}
