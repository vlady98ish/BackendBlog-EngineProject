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

    private int id;

    @Column(nullable = false)
    private LocalDateTime time;

    @Column(nullable = false)
    private byte value;


    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


}
