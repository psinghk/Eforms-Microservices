
package in.nic.ashwini.eForms.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import in.nic.ashwini.eForms.entities.ModeratorBase;

@Repository
public interface ModeratorBaseRepo extends JpaRepository<ModeratorBase, Long>{
	

}


