package main.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@Table(name = "global_settings")
public class GlobalSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;


    @Column(nullable = false) //TODO: Поменять на YES NO

    private boolean multiuserMode;

    @Column(nullable = false) //TODO: Поменять на YES NO
    private boolean postPreModeration;

    @Column(nullable = false) //TODO: Поменять на YES NO
    private boolean statisticsIsPublic;

}