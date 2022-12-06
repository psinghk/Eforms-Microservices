package in.nic.eform.imappop.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import in.nic.eform.imappop.dto.ImappopDocUploadDTO;


@Repository
public interface ImappopDocUploadRepo extends CrudRepository<ImappopDocUploadDTO, Long>,QueryByExampleExecutor<ImappopDocUploadDTO>{

	 List<ImappopDocUploadDTO> findByRegistrationnoAndRole(String regid,String role);
}
