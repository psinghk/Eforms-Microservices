package in.nic.ashwini.eForms.db.slave.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.db.slave.entities.AdminsFromSlave;

@Repository
public interface AdminsRepositoryToRead extends JpaRepository<AdminsFromSlave, Integer>{
	Optional<AdminsFromSlave> findByEmailAndVpnIpOrDesktopIp(String email, String vpnIp, String desktopIp);
	Optional<AdminsFromSlave> findByMobileAndVpnIpOrDesktopIp(String mobile, String vpnIp, String desktopIp);
	Optional<AdminsFromSlave> findByEmailAndVpnIp(String email, String vpnIp);
	Optional<AdminsFromSlave> findByMobileAndVpnIp(String mobile, String vpnIp);
	Optional<AdminsFromSlave> findByMobile(String mobile);
	Optional<AdminsFromSlave> findByEmail(String email);
}
