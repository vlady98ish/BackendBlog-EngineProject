package main.model;

import lombok.Getter;
import lombok.Setter;


import javax.persistence.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "captcha_code")
@Getter
@Setter
public class CaptchaCode {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;


    @Column(nullable = false, columnDefinition = "TINYTEXT")
    private String code;

    @Column(name = "secret_code", nullable = false, columnDefinition = "TINYTEXT")
    private String secretCode;

    @Column(nullable = false)
    private LocalDateTime time;


}
