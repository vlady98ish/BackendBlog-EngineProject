package main.model;


import lombok.Getter;
import lombok.Setter;


import javax.persistence.*;


@Entity
@Getter
@Setter
@Table(name = "global_settings")
public class GlobalSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;


    @Column(nullable = false)

    private boolean multiuserMode;

    @Column(nullable = false)
    private boolean postPreModeration;

    @Column(nullable = false)
    private boolean statisticsIsPublic;

}