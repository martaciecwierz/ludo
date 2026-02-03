package pl.marta.ludo.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.marta.ludo.userservice.domain.UserResult;

import java.util.UUID;

@Repository
public interface UserResultRepository extends JpaRepository<UserResult, UUID> {
}
