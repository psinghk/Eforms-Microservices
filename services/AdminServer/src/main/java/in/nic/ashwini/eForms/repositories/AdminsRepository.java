package in.nic.ashwini.eForms.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.entities.Admins;

@Repository
public interface AdminsRepository extends JpaRepository<Admins, Integer>{
	Optional<Admins> findByEmailAndVpnIpOrDesktopIp(String email, String vpnIp, String desktopIp);
	Optional<Admins> findByMobileAndVpnIpOrDesktopIp(String mobile, String vpnIp, String desktopIp);
	Optional<Admins> findByEmailAndVpnIp(String email, String vpnIp);
	Optional<Admins> findByMobileAndVpnIp(String mobile, String vpnIp);
	Optional<Admins> findByMobile(String mobile);
	Optional<Admins> findByEmail(String email);
}
