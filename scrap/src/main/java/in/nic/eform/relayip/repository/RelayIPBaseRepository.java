package in.nic.eform.relayip.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import in.nic.eform.relayip.dto.RelayIPBaseDTO;


@Repository
public interface RelayIPBaseRepository extends CrudRepository<RelayIPBaseDTO, Integer>{
	
	RelayIPBaseDTO findByRegistrationno(String regNo);
	
	int countByregistrationno(String registration_no);
	
	List<RelayIPBaseDTO> findByDatetimeLike(String d);
}
