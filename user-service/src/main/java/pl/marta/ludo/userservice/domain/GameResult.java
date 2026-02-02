package pl.marta.ludo.userservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameResult {

    @Id
    @GeneratedValue
    private UUID id;

    @OneToMany(mappedBy = "gameResult", cascade = CascadeType.ALL)
    private List<UserResult> userResults;

    @Enumerated(EnumType.STRING)
    private GameStatus status;

    private Instant startedAt;

    private Instant finishedAt;
}
