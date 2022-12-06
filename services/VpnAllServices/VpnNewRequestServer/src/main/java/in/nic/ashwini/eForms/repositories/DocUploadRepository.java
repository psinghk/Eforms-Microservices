package in.nic.ashwini.eForms.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.entities.DocUpload;


@Repository
public interface DocUploadRepository extends JpaRepository<DocUpload, Long>,QueryByExampleExecutor<DocUpload>{
	 List<DocUpload> findByRegistrationNoAndRole(String registrationNo,String role);
}
