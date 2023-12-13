package cl.petrasic.usercreator.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.petrasic.usercreator.domain.Phone;
import cl.petrasic.usercreator.domain.User;

import java.util.List;
import java.util.UUID;

public interface PhoneRepository extends JpaRepository<Phone, UUID> {
    List<Phone> findByUser(User user);
}
