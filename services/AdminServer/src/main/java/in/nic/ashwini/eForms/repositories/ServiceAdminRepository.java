package in.nic.ashwini.eForms.repositories;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import in.nic.ashwini.eForms.entities.ServiceAdmin;

public interface ServiceAdminRepository extends CrudRepository<ServiceAdmin, Long> {
	@Query("select distinct s.serviceId from ServiceAdmin s where s.adminEmail = :adminEmail and s.status= 'a'")
	Set<Integer> findByAdminEmail(@Param("adminEmail") String adminEmail);
}
