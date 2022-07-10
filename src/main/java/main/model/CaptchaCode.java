package main.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "captcha_code")
@Getter
@Setter
public class CaptchaCode {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;


    @NotNull
    private Date time;

    @Type(type = "text")
    @NotNull
    private String code;


    @NotNull
    @Type(type = "text")
    @Column(name = "secret_code")
    private String secretCode;

}
