package in.nic.ashwini.eForms.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.entities.WifiBase;

@Repository
public interface WifiBaseRepo extends JpaRepository<WifiBase, Long> {
	WifiBase findByRegistrationNo(String registrationNo);

	List<WifiBase> findByDatetimeLike(String datetime);

	@Query(value = "select machine_address from wifi_mac_os where registration_no in (select registration_no from wifi_registration where  registration_no not in ( select registration_no from reporting_service.final_audit_track where registration_no like 'WIFI%' and status like '%rejected%' or status like '%cancel%' or status like '%expired%') and (pdf_path!=null || pdf_path!='') and auth_email in ( :aliases ))", nativeQuery = true)
	List<String> countOfMacWithThisUser(@Param("aliases") List<String> aliases);

	@Query(value = "select auth_email from wifi_registration where registration_no in (select registration_no from wifi_mac_os where machine_address =?) "
			+ " and registration_no not in ( select registration_no from reporting_service.final_audit_track where registration_no like 'WIFI%' and status like '%rejected%' or status like '%cancel%' or status like '%expired%')  and wifi_request = 'request' and (pdf_path!=null || pdf_path!='') ", nativeQuery = true)
	List<String> countOfUserWithThisMac(String mac);

	@Query(value = "select registration_no from wifi_registration where date(datetime)=date(now()) group by registration_no desc limit 1", nativeQuery = true)
	String findLatestRegistrationNo();

	@Query(value = "select machine_address,operating_system, device_type  from wifi_mac_os where registration_no in (select registration_no from wifi_registration where auth_email in ( :aliases ) and wifi_request ='request') and status IS NULL", nativeQuery = true)
	List<Object[]> fetchWifiDataForApplicant(@Param("aliases") List<String> aliases);

	@Query(value = "select machine_address,operating_system from wifi_mac_os where registration_no in (select registration_no from wifi_registration where  registration_no not in (select registration_no from reporting_service.final_audit_track where (status like '%pending%' OR status like '%manual_upload%') and applicant_email in ( :aliases ) and registration_no like 'WIFI-FORM%') and wifi_request='request' and pdf_path!='')", nativeQuery = true)
	List<Object[]> fetchPendingWifiDataForApplicant(@Param("aliases") List<String> aliases);
	
	@Query(value = "select machine_address,operating_system,device_type from wifi_mac_os where registration_no in (select registration_no from wifi_registration where  registration_no  in (select registration_no from reporting_service.final_audit_track where status ='completed' and applicant_email in (:aliases) and registration_no like '%WIFI-FORM%') and wifi_request='request' and pdf_path!='')", nativeQuery = true)
	List<Object[]> fetchCompleteWifiDataForApplicant(@Param("aliases") List<String> aliases);
	
	@Query(value = "select wifibase.wifiRequest from WifiBase wifibase where  wifibase.registrationNo =:registrationNo")
	boolean isRequestForDelete(String registrationNo);

}
