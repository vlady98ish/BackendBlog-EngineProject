package main.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "post_votes")
public class PostVote {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @NotNull
    @Column(name = "user_id")
    private int userId;
    @NotNull
    @Column(name = "post_id")
    private int post_id;
    @NotNull
    private Date time;
    private boolean value; //TODO: Проверить что значит tinyInt
}
