package in.nic.eForms.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import in.nic.eForms.entities.GeneratePdf;

@Repository
public interface GeneratePdfRepository extends JpaRepository<GeneratePdf, Long>, QueryByExampleExecutor<GeneratePdf>{
	
	Optional<GeneratePdf> findByRegistrationNo(String registrationNo);
	
}
