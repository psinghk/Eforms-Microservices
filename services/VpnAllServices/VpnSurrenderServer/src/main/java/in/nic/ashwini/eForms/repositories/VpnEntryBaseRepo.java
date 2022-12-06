package in.nic.ashwini.eForms.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.entities.VpnEntryBase;

@Repository
public interface VpnEntryBaseRepo extends JpaRepository<VpnEntryBase, Long> {

	//VpnEntryBase save(VpnEntryBase vpnEnty);
	//List<VpnEntryBase> save(VpnEntryBase vpnEntryBase);
	
	

}
