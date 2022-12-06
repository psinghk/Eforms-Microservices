package in.nic.ashwini.eForms.db.master.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.db.master.entities.State;

@Repository
public interface StateRepository extends JpaRepository<State, Long> {

	@Query(value = "select distinct state.stateName from State state order by state.stateName ")
	List<String> getDistinctStateName();
	
	@Query(value = "select distinct state.districtName from State state where state.stateName = :stname")
	List<String> getDistinctDistrictNameByStateName(@Param(value = "stname") String stateName);
}
