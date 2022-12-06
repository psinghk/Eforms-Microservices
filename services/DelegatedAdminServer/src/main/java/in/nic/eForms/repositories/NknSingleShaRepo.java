package in.nic.eForms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.eForms.entities.NknSingleSha;

@Repository
public interface NknSingleShaRepo extends JpaRepository<NknSingleSha, Long>{
	
}
