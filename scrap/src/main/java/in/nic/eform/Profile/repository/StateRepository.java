package in.nic.eform.Profile.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.nic.eform.Profile.model.State;

@Repository
public interface StateRepository extends CrudRepository<State, Long> {

	// Try to replace this Query with QueryMethod.
	
	@Query(value = "select distinct state.stateName from State state order by state.stateName ")
	List<String> findState();

	@Query(value = "select distinct state.districtName from State state where state.stateName = :stname")
	List<String> findDistrictByState(@Param(value = "stname") String stname);

}
