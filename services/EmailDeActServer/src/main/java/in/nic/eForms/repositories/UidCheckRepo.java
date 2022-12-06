package in.nic.eForms.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.eForms.entities.UidCheck;



@Repository
public interface UidCheckRepo extends JpaRepository<UidCheck, String>{
	

}
