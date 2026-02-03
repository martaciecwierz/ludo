package pl.marta.ludo.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.marta.ludo.userservice.domain.Gameplay;

import java.util.UUID;

@Repository
public interface GameplayRepository extends JpaRepository<Gameplay, UUID> {
}
