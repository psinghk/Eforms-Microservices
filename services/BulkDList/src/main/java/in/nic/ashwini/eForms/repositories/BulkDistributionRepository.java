package in.nic.ashwini.eForms.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.entities.GenerateBulkPdf;


@Repository
public interface BulkDistributionRepository extends JpaRepository<GenerateBulkPdf, Long>, QueryByExampleExecutor<GenerateBulkPdf>{

	List<GenerateBulkPdf> findByRegistrationNo(String registrationNo);
}
