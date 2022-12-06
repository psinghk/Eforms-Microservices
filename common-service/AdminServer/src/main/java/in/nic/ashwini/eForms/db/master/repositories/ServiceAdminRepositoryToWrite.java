package in.nic.ashwini.eForms.db.master.repositories;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.db.master.entities.ServiceAdmin;

@Repository
public interface ServiceAdminRepositoryToWrite extends CrudRepository<ServiceAdmin, Long> {
	@Query("select distinct s.serviceId from ServiceAdmin s where s.adminEmail = :adminEmail and s.status= 'a'")
	Set<Integer> findByAdminEmail(@Param("adminEmail") String adminEmail);
}
