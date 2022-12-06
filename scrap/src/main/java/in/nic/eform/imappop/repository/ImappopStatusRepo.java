package in.nic.eform.imappop.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import in.nic.eform.imappop.dto.ImappopStatusDTO;

@Repository
public interface ImappopStatusRepo extends CrudRepository<ImappopStatusDTO, Integer>{
	
	ImappopStatusDTO findByStatregno(String regNo);
	ImappopStatusDTO findByStatregnoAndStatforwardedto(String regNo,String role);
	
	List<ImappopStatusDTO> findFirstByStatregnoAndStatforwardedtoOrderByStatid(String registration_no,String trole);

	List<ImappopStatusDTO> findByStatregnoAndStatforwardedbyIsNullAndStatforwardedtoIsNullOrderByStatid(String registration_no);
	
	List<ImappopStatusDTO> findByStatregnoAndStatforwardedbyAndStatforwardedtoIsNullOrderByStatid(String registration_no,String srole);
	
	List<ImappopStatusDTO> findByStatregnoAndStatforwardedbyAndStatforwardedto(String registration_no,String trole,String srole);
	
	List<ImappopStatusDTO> findByStatregnoAndStatforwardedbyOrStatforwardedto(String registration_no, String srole,String trole);
	
	
}
