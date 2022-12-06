package in.nic.ashwini.eForms.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.entities.DocUpload;

@Repository
	public interface DocsRepository extends CrudRepository<DocUpload, Integer>{
	List<DocUpload> findByRegistrationNoAndRoleAndStatus(String regid,String role, String status);
	DocUpload findById(Long id);
}