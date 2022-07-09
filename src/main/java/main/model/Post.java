package main.model;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name="posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "is_active")
    private boolean isActive;
    @Enumerated(EnumType.STRING) //TODO: По умолчанию поставить NEW
    @Column(name = "moderation_status")
    @NotNull
    private Status moderationStatus;
    @Column(name = "moderator_id")
    private int moderatorId;
    @NotNull
    private Date time;
    @NotNull
    @Type(type = "text")
    private String title;
    @NotNull
    @Column(name="view_count")
    private int viewCount;




}
