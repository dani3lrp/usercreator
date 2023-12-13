package cl.petrasic.usercreator.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.petrasic.usercreator.domain.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
