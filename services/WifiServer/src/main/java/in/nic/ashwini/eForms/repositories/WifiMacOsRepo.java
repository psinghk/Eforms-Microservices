package in.nic.ashwini.eForms.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import in.nic.ashwini.eForms.entities.GeneratePdf;
import in.nic.ashwini.eForms.entities.WifiBase;
import in.nic.ashwini.eForms.entities.WifiMacOs;

public interface WifiMacOsRepo extends JpaRepository<WifiMacOs, Long>{
	
	@Query(value = "update WifiMacOs macos set macos.status='deleted' where macos.registrationNo = :registrationNo")
	boolean updateWifiValue(String registrationNo);
	
	Optional<WifiMacOs> findByRegistrationNoAndMachineAddress(String registrationNo, String machineAddress);
	
	List findByRegistrationNo(String registrationNo);
	
	//WifiMacOs findByRegistrationNo(String registrationNo);

}
