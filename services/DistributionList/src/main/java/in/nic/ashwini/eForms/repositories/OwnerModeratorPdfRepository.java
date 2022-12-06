package in.nic.ashwini.eForms.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;
import in.nic.ashwini.eForms.entities.OMGeneratePdf;

@Repository
public interface OwnerModeratorPdfRepository extends JpaRepository<OMGeneratePdf, Long>, QueryByExampleExecutor<OMGeneratePdf>{

	List<OMGeneratePdf> findByRegistrationNo(String registrationNo);
}
