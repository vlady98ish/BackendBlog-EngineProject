package main.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Email;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)

    private int id;


    @Column(name = "is_moderator", nullable = false)
    private byte isModerator;


    @Column(name = "reg_time", nullable = false)
    @DateTimeFormat(pattern = "YYYY-MM-dd HH:mm:ss")
    private LocalDateTime regTime;
    @Column(nullable = false)
    private String name;
    @Email
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;

    private String code;
    @Column(columnDefinition = "TEXT")
    private String photo;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Post> posts;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostComments> postComments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostVote> postVotes;


}
