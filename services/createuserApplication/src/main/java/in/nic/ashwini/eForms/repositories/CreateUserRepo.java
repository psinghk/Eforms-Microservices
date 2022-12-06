package in.nic.ashwini.eForms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.entities.CreatedUser;

@Repository
public interface CreateUserRepo extends JpaRepository<CreatedUser, Long> {

}
