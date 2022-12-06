package in.nic.eform.repository;

import java.util.List;

import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import in.nic.eform.dto.DocUpload;

@Repository
	public interface DocsRepository extends CrudRepository<DocUpload, Integer>{


	//@Query(value = "select * from doc_upload u where u.registration_no=?1 and u.role=?2",nativeQuery = true)
	//public List<ViewDocxDTO> viewDocxListByRoleAndReg(String regid,String role);
	
	List<DocUpload> findByRegnoAndRole(String regid,String role);
	
	@Transactional
	@Modifying
	@Query(value = "delete from doc_upload where id=?1",nativeQuery = true)
	public int deleteId(Long id);
	
	DocUpload findById(Long id);
	
}