package in.nic.ashwini.eForms.db.slave.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.db.slave.entities.LoginTrailFromSlave;

@Repository
public interface LoginTrailRepositoryToRead extends JpaRepository<LoginTrailFromSlave, Long> {
	
	
	List<LoginTrailFromSlave> findByEmailInAndTokenAndStatus(Set<String> alias,String token,Integer status);
	}
