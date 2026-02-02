package pl.marta.ludo.userservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Score {

    @Id
    @GeneratedValue
    private UUID id;

    private int score;

    @Enumerated(EnumType.STRING)
    private Game game;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
