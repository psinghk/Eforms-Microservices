package in.nic.ashwini.eForms.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import in.nic.ashwini.eForms.entities.BulkDlist;

@Repository
public interface BulkDListRepo extends JpaRepository<BulkDlist, Long>{

	List<BulkDlist> findByRegistrationNo(String registrationNo);

}
