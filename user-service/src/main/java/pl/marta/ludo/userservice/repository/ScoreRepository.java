package pl.marta.ludo.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.marta.ludo.userservice.domain.Score;

import java.util.UUID;

@Repository
public interface ScoreRepository extends JpaRepository<UUID, Score> {
}
