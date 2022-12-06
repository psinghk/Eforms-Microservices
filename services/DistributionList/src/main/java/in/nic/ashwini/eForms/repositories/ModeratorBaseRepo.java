
package in.nic.ashwini.eForms.repositories;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import in.nic.ashwini.eForms.entities.ModeratorBase;

@Repository
public interface ModeratorBaseRepo extends JpaRepository<ModeratorBase, Long>{

	List<ModeratorBase> findByRegistrationNo(String registrationNo);

	Optional<ModeratorBase> findById(Long id);
	
    List<ModeratorBase> findByOmEmail(String omEmail);

	void deleteById(Long id);

}


