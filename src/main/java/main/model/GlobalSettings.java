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
@RestController
public class GlobalSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;


    @NotNull //TODO: Поменять на YES NO

    private boolean multiuserMode;

    @NotNull //TODO: Поменять на YES NO
    private boolean postPreModeration;

    @NotNull //TODO: Поменять на YES NO
    private boolean statisticsIsPublic;

}