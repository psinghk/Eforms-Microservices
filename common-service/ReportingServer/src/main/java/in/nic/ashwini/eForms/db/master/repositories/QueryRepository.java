package in.nic.ashwini.eForms.db.master.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.db.master.entities.Query;
@Repository
public interface QueryRepository extends JpaRepository<Query, Long>{
	List<Query> findByRegistrationNoOrderByQueryRaisedTimeDesc(String registrationNo);
}
