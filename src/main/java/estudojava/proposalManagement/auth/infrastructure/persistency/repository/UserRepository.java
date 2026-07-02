package estudojava.proposalManagement.auth.infrastructure.persistency.repository;

import estudojava.proposalManagement.auth.infrastructure.persistency.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<User, UUID> {
    Optional<User> findByUsername(String username);
}
