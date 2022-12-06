package in.nic.eform.updatemobile.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import in.nic.eform.updatemobile.dto.MobileStatusDTO;

@Repository
public interface MobileStatusRepository extends CrudRepository<MobileStatusDTO, Integer>{
	//MobileStatusDTO findByStatregno(String regNo);
	MobileStatusDTO findByStatregnoAndStatforwardedto(String regNo,String role);
	
	//List<TrackStatusDTO> findByStatregno(String registration_no);

	List<MobileStatusDTO> findFirstByStatregnoAndStatforwardedtoOrderByStatid(String registration_no,String trole);

	List<MobileStatusDTO> findByStatregnoAndStatforwardedbyIsNullAndStatforwardedtoIsNullOrderByStatid(String registration_no);
	
	List<MobileStatusDTO> findByStatregnoAndStatforwardedbyAndStatforwardedtoIsNullOrderByStatid(String registration_no,String srole);
	
	List<MobileStatusDTO> findByStatregnoAndStatforwardedbyAndStatforwardedto(String registration_no,String trole,String srole);
	
	List<MobileStatusDTO> findByStatregnoAndStatforwardedbyOrStatforwardedto(String registration_no, String srole,String trole);

}
