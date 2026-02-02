package pl.marta.ludo.userservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String username;

    private String hashedPassword;

    @OneToMany(mappedBy = "user")
    private List<Score> score;

    @Enumerated(EnumType.STRING)
    private UserRole role;
}
