package in.nic.ashwini.eForms.db.master.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import in.nic.ashwini.eForms.db.master.entities.MailAdminForms;

@Repository
public interface MailAdminRepositoryToWrite extends JpaRepository<MailAdminForms, Integer> {


	@Query(value = "select m_email from mailadmin_forms where :formtype = 'y' group by m_email order by m_email asc", nativeQuery = true)
	List<String> fetchMailAdmins(@Param("formtype") String formtype);
	
	@Query(value = "select m_email from mailadmin_forms where m_single = 'y' group by m_email order by m_email asc", nativeQuery = true)
	List<String> single(@Param("formtype") String formtype);
	
	@Query(value = "select m_email from mailadmin_forms where m_bulk = 'y' group by m_email order by m_email asc", nativeQuery = true)
	List<String> bulk(@Param("formtype") String formtype);
	
	
	@Query(value = "select m_email from mailadmin_forms where m_nkn = 'y' group by m_email order by m_email asc", nativeQuery = true)
	List<String> nkn(@Param("formtype") String formtype);
	
	@Query(value = "select m_email from mailadmin_forms where m_relay = 'y' group by m_email order by m_email asc", nativeQuery = true)
	List<String> relay(@Param("formtype") String formtype);
	
	@Query(value = "select m_email from mailadmin_forms where m_ldap = 'y' group by m_email order by m_email asc", nativeQuery = true)
	List<String> ldap(@Param("formtype") String formtype);
	
	@Query(value = "select m_email from mailadmin_forms where m_dlist = 'y' group by m_email order by m_email asc", nativeQuery = true)
	List<String> dlist(@Param("formtype") String formtype);
	
	@Query(value = "select m_email from mailadmin_forms where m_sms = 'y' group by m_email order by m_email asc", nativeQuery = true)
	List<String> sms(@Param("formtype") String formtype);
	
	@Query(value = "select m_email from mailadmin_forms where m_ip = 'y' group by m_email order by m_email asc", nativeQuery = true)
	List<String> ip(@Param("formtype") String formtype);
	
	@Query(value = "select m_email from mailadmin_forms where m_imappop = 'y' group by m_email order by m_email asc", nativeQuery = true)
	List<String> imappop(@Param("formtype") String formtype);
	
	@Query(value = "select m_email from mailadmin_forms where m_gem = 'y' group by m_email order by m_email asc", nativeQuery = true)
	List<String> gem(@Param("formtype") String formtype);
	
	@Query(value = "select m_email from mailadmin_forms where m_mobile = 'y' group by m_email order by m_email asc", nativeQuery = true)
	List<String> mobile(@Param("formtype") String formtype);
	
	@Query(value = "select m_email from mailadmin_forms where m_dns = 'y' group by m_email order by m_email asc", nativeQuery = true)
	List<String> dns(@Param("formtype") String formtype);
	
	@Query(value = "select m_email from mailadmin_forms where m_wifi = 'y' group by m_email order by m_email asc", nativeQuery = true)
	List<String> wifi(@Param("formtype") String formtype);
	
	@Query(value = "select m_email from mailadmin_forms where m_vpn = 'y' group by m_email order by m_email asc", nativeQuery = true)
	List<String> vpn(@Param("formtype") String formtype);
	
	@Query(value = "select m_email from mailadmin_forms where m_cloud = 'y' group by m_email order by m_email asc", nativeQuery = true)
	List<String> cloud(@Param("formtype") String formtype);
	
	@Query(value = "select m_email from mailadmin_forms where m_centralum = 'y' group by m_email order by m_email asc", nativeQuery = true)
	List<String> centralum(@Param("formtype") String formtype);
	
	@Query(value = "select m_email from mailadmin_forms where m_webcast = 'y' group by m_email order by m_email asc", nativeQuery = true)
	List<String> webcast(@Param("formtype") String formtype);
	
	@Query(value = "select m_email from mailadmin_forms where m_email_act = 'y' group by m_email order by m_email asc", nativeQuery = true)
	List<String> emailAct(@Param("formtype") String formtype);
	
	@Query(value = "select m_email from mailadmin_forms where m_email_deact = 'y' group by m_email order by m_email asc", nativeQuery = true)
	List<String> emailDeact(@Param("formtype") String formtype);
	
	@Query(value = "select m_email from mailadmin_forms where m_profile = 'y' group by m_email order by m_email asc", nativeQuery = true)
	List<String> profile(@Param("formtype") String formtype);
	
	
	
}
