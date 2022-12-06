package in.nic.eform.updatemobile.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import in.nic.eform.updatemobile.dto.MobileDocUploadDTO;


@Repository
public interface MobileDocUploadRepository extends CrudRepository<MobileDocUploadDTO, Long>,QueryByExampleExecutor<MobileDocUploadDTO>{

	 List<MobileDocUploadDTO> findByRegistrationnoAndRole(String regid,String role);
}
