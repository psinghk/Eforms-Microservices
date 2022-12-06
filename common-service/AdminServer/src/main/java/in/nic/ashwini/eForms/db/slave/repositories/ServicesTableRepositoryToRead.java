package in.nic.ashwini.eForms.db.slave.repositories;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.db.slave.entities.ServicesTableFromSlave;

@Repository
public interface ServicesTableRepositoryToRead extends JpaRepository<ServicesTableFromSlave, Integer> {
	List<ServicesTableFromSlave> findByIdIn(Collection<Integer> id);
	@Query("select distinct s.keyword from ServicesTableFromSlave s")
	Set<String> findDistinctServices();
	
	@Query("select s.keyword from ServicesTableFromSlave s where s.id = :id")
	String findServiceName(@Param("id") Integer id);
	
	List<ServicesTableFromSlave> findByServiceTypeAndStatusOrderByServiceOrder(String type, int status);
}
