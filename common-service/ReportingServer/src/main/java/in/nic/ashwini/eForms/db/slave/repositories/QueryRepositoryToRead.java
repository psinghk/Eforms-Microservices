package in.nic.ashwini.eForms.db.slave.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.db.slave.entities.QueryFromSlave;

@Repository
public interface QueryRepositoryToRead extends JpaRepository<QueryFromSlave, Long>{
	List<QueryFromSlave> findByRegistrationNoOrderByQueryRaisedTimeDesc(String registrationNo);
}
