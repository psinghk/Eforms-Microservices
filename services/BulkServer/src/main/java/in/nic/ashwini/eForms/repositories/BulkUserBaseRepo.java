package in.nic.ashwini.eForms.repositories;




import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.entities.BulkEmailBase;
import in.nic.ashwini.eForms.entities.BulkUsers;

@Repository
public interface BulkUserBaseRepo extends JpaRepository<BulkUsers, Integer>{
	List<BulkUsers> findByRegistrationNo(String registrationNo);
	
}
