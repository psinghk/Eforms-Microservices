package in.nic.ashwini.eForms.db.slave.repositories;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.db.slave.entities.ServiceAdminFromSlave;

@Repository
public interface ServiceAdminRepositoryToRead extends CrudRepository<ServiceAdminFromSlave, Long> {
	@Query("select distinct s.serviceId from ServiceAdminFromSlave s where s.adminEmail = :adminEmail and s.status= 'a'")
	Set<Integer> findByAdminEmail(@Param("adminEmail") String adminEmail);
}
