package main.model;

import lombok.Getter;

import lombok.Setter;

import javax.persistence.*;


import java.time.LocalDateTime;


@Entity
@Setter
@Getter
@Table(name = "post_votes")
public class PostVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Integer id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;
    @Column(nullable = false)
    private LocalDateTime time;
    @Column(nullable = false)
    private byte value;
}
