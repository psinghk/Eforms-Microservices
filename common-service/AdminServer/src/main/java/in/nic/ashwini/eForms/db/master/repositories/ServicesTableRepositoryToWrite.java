package in.nic.ashwini.eForms.db.master.repositories;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.db.master.entities.ServicesTable;

@Repository
public interface ServicesTableRepositoryToWrite extends JpaRepository<ServicesTable, Integer> {
	List<ServicesTable> findByIdIn(Collection<Integer> id);
	@Query("select distinct s.keyword from ServicesTable s")
	Set<String> findDistinctServices();
	
	@Query("select s.keyword from ServicesTable s where s.id = :id")
	String findServiceName(@Param("id") Integer id);
	
	List<ServicesTable> findByServiceTypeAndStatusOrderByServiceOrder(String type, int status);
}
