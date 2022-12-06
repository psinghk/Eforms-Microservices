package in.nic.ashwini.eForms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.entities.Sms;

@Repository
public interface SmsRepo extends JpaRepository<Sms, Long> {

}
