package in.nic.ashwini.eForms.db.slave.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.db.slave.entities.StateFromSlave;

@Repository
public interface StateRepositoryToRead extends JpaRepository<StateFromSlave, Long> {

	@Query(value = "select distinct state.stateName from StateFromSlave state order by state.stateName ")
	List<String> getDistinctStateName();
	
	@Query(value = "select distinct state.districtName from StateFromSlave state where state.stateName = :stname")
	List<String> getDistinctDistrictNameByStateName(@Param(value = "stname") String stateName);
}
