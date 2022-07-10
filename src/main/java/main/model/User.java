package main.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)

    private int id;

    @NotNull
    @Column(name = "is_moderator")
    private byte isModerator;

    @NotNull
    @Column(name = "reg_time")
    @DateTimeFormat(pattern = "YYYY-MM-dd HH:mm:ss")
    private Timestamp regTime;
    @NotNull
    private String name;
    @NotNull
    @Email

    private String email;
    @NotNull
    private String password;

    private String code;

    private String photo;


}
