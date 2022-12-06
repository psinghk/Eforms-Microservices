package in.nic.eform.ldap.service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.nic.eform.ldap.bean.ForwardBean;
import in.nic.eform.ldap.bean.GeneratePdfBean;
import in.nic.eform.ldap.bean.LdapFormBean;
import in.nic.eform.ldap.bean.UploadMultipleFilesBean;
import in.nic.eform.ldap.dao.LdapDao;
import in.nic.eform.ldap.dto.LdapDocUploadDTO;
import in.nic.eform.ldap.dto.LdapFinalAuditTrackDTO;
import in.nic.eform.ldap.dto.LdapBaseDTO;
import in.nic.eform.ldap.dto.LdapStatusDTO;
import in.nic.eform.utility.ApiUtility;
import in.nic.eform.utility.Constants;
import in.nic.eform.utility.GetProfileInfo;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@Service
public class LdapService {
	private static final Logger log = LoggerFactory.getLogger(LdapService.class);
	
	//remove use of global varibles
	// use Environment property reading mechanism 
	@Value("${fileBasePath}")
	private String EXTERNAL_FILE_PATH;
	@Value("${fileBasePath}")
	private String fileBasePath;
	
	@Autowired
	LdapDao ldapDao;
	@Autowired
	GetProfileInfo getProfileInfo;
	@Autowired
	ApiUtility apiUtility;

	// *******************start of submission********************
	
	
	// Where is the submission of user request 
	// here just enriching the LdapFormBean with Profile
	public Map<String, Object> submissionPreview(LdapFormBean ldapFormBean) {

		Map<String, Object> ldapBeanList = new HashMap<String, Object>();
		HashMap<String, Object> profileList = new HashMap<String, Object>();
		
		// Meenaxi already completed the profile service, please integrate that.
		profileList = getProfileInfo.fetchprofile();
		
		// Change log info as stated on controller
		log.info("Inside submissionPreview method at " + new Date() + " profileList:::" + profileList);
		try {
			BeanUtils.populate(ldapFormBean, profileList);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		ObjectMapper m = new ObjectMapper();
		ldapBeanList = m.convertValue(ldapFormBean, Map.class);
		
		// No need here 
		log.info("Inside submissionPreview method at " + new Date() + " ldapBeanList:::" + ldapBeanList);
		
		
		// Always try to Return only json string to the controller
		return ldapBeanList;
	}

//		//for tab2 action_type=validate
//			public Map<String, Object> submissionRoDetails(SubmissionFormBean submissionBean)  {
//				 Map<String, Object> ldapBeanList = new HashMap<String, Object>();
//				 HashMap<String, Object> profileList = new HashMap<String, Object>();
//				 profileList=getProfileInfo.fetchprofile();
//				 ldapBeanList.put("hod_name", profileList.get("hod_name")) ;
//				 ldapBeanList.put("hod_email",  profileList.get("hod_email"));
//				 ldapBeanList.put("hod_mobile", profileList.get("hod_mobile"));
//			return ldapBeanList;
//			}

	public Map<String, Object> submissionRoDetails() {
		
		
		log.info("Inside submissionRoDetails method at " + new Date());
		
		
		Map<String, Object> ldapBeanList = new HashMap<String, Object>();
		HashMap<String, Object> profileList = new HashMap<String, Object>();
		
		// Meenaxi already completed the profile service, please integrate that.
		profileList = getProfileInfo.fetchprofile();
		ldapBeanList.put("hod_name", profileList.get("hod_name"));
		ldapBeanList.put("hod_email", profileList.get("hod_email"));
		ldapBeanList.put("hod_mobile", profileList.get("hod_mobile"));
		
		// No need here 
		log.info("Inside submissionRoDetails method at " + new Date() + " ldapBeanList:::" + ldapBeanList);
		
		// Always try to Return only json string to the controller
		return ldapBeanList;
	}

	// change method name with genregistrationno
	public Map<String, Object> generateRefNo(LdapFormBean ldapFormBean) {
		log.info("Inside generateRefNo method at " + new Date());
		Map<String, Object> map = new HashMap<String, Object>();
		HashMap<String, Object> profileList = new HashMap<String, Object>();
		
		// Meenaxi already completed the profile service, please integrate that.
		profileList = getProfileInfo.fetchprofile();
		try {
			BeanUtils.populate(ldapFormBean, profileList);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		LdapBaseDTO ldapbaseDTO = new LdapBaseDTO();
		try {
			BeanUtils.copyProperties(ldapbaseDTO, ldapFormBean);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		String ref_num = ldapDao.ldap_tab2(ldapbaseDTO);
		// int i = userSubmissionDao.update_profile(ldapFormDto);
		if (ref_num != null) {
			map.put("status", "Registration number generated successfully!!");
			map.put("ref_num", ref_num);
		} else {
			
			// message should be precise and clear
			// Something went wrong!! is vage message
			map.put("status", "Something went wrong!!"); 
			map.put("ref_num", ref_num);
		}
		log.info("Inside generateRefNo method at " + new Date() + " map:::" + map);
		return map;
	}

	// for Pre-requisite for integration and checking
	public Map<String, Object> submissionTelnetDtl(String telnet, String ref_num) {
		log.info("Inside submissionTelnetDtl method at " + new Date());
		Map<String, Object> map = new HashMap<String, Object>();
		if (telnet.equals("y") || telnet.equals("n")) {
			int i = ldapDao.updateTelnet(telnet, ref_num);
			if (i > 0) {
				map.put("status", "Telnet Updated successfully!!");
				map.put("ref_num", ref_num);
			} else {
				map.put("status", "Something went wrong!!");
				map.put("ref_num", ref_num);
			}
		}
		log.info("Inside submissionTelnetDtl method at " + new Date() + " map::" + map);
		return map;
	}

	public Map<String, Object> consent(String regno, String check, String formtype, String consent, String email) {
		log.info("Inside consent method at " + new Date());
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> consentreturn = new HashMap<>();
		HashMap<String, Object> profileList = new HashMap<String, Object>();
		ForwardBean forwardBean = new ForwardBean();
		profileList = getProfileInfo.fetchprofile();
		log.info("Entering consent method at " + new Date() + "  profileList:::" + profileList);
		log.info("Entering consent method at " + new Date() + "  consent:::" + consent);
		if (consent.equals("user")) {
			String ref_num = regno;

			consentreturn.put("ref_num", ref_num);
			consentreturn.put("status",
					"Your form has been submitted and your Registration number is " + ref_num + ".");
			String file = "";
			if (check.equals("online")) {
				file = "online processing";
			} else if (check.equals("manual_upload") || check.equals("esign")) {
				file = "/eForms/PDF/" + formtype + "/" + ref_num + ".pdf";
			} else if (check.equals("esign_update")) {
//		               String email=;
//		               String isHog=isHogApi(email);
//		               String isHod=isHodApi(email);
				// file = "/eForms/PDF/esigned/" + (String) session.get("esign_file_path");
			}

			if (!check.equals("esign")) {
				forwardBean.setCheck(check);
				forwardBean.setRef_num(ref_num);
				forwardBean.setFormtype(formtype);
				forwardBean.setFile(file);
				forwardBean.setRename_file(file);
				forwardBean.setHodEmails(profileList.get("hod_email").toString());
				forwardBean.setUemail(profileList.get("applicant_email").toString());
				forwardBean.setUmobile(profileList.get("applicant_mobile").toString());
				forwardBean.setUname(profileList.get("applicant_name").toString());
				forwardBean.setRole(consent);
				finalSubmission(forwardBean);
			}
		}
		return consentreturn;
	}

	public Map<String, String> finalSubmission(ForwardBean forwardBean) {
		log.info("Inside finalSubmission method at " + new Date());
		Map<String, String> form_details = null;
		Map<String, String> forwardToData = new HashMap<>();
		form_details = ldapDao.fetchFormDetail(forwardBean.getRef_num());
		log.info("Inside finalSubmission method at " + new Date() + " form_details:::" + form_details);
		String currentLevel = forwardBean.getRole();
		String submissionType = forwardBean.getCheck();

		String ref_num = forwardBean.getRef_num();
		String employment = form_details.get("applicant_employment");
		String state = form_details.get("add_state");

		HashMap<String, Object> profileList = getProfileInfo.fetchprofile1(forwardBean.getUemail());
		log.info("Inside finalSubmission method at " + new Date() + " profileList:::" + profileList);
		String applicant_email = forwardBean.getUemail();
		String applicant_name = (String) profileList.get("applicant_name");
		String applicant_mobile = (String) profileList.get("applicant_mobile");
		String hod_email = (String) profileList.get("hod_email");
		String hod_name = (String) profileList.get("hod_name");
		String hod_mobile = (String) profileList.get("hod_mobile");

		boolean isNicEmployee = apiUtility.isNICEmployeeApi(applicant_email);
		log.info("Inside LdapService: finalSubmission method at " + new Date() + " isNicEmployee:::" + isNicEmployee
				+ "  currentLevel::::" + currentLevel);
		forwardToData.put("currentlevel", currentLevel);

		switch (currentLevel) {
		case Constants.ROLE_USER:
			if (submissionType.equals("manual_upload")) {
				forwardToData.put("nextlevel", Constants.ROLE_USER);
				forwardToData.put("toemail", applicant_email);
			} else if (submissionType.equals("upload_scanned")) {
				if (form_details != null) {
					if (isNicEmployee) {
						forwardToData.put("nextlevel", Constants.ROLE_MAILADMIN);
						forwardToData.put("toemail", ldapDao.fetchToEmail());
					} else if (employment.equalsIgnoreCase("state") && state.equalsIgnoreCase("Himachal Pradesh")) {
						Map<String, String> himachalCoordsAsRO = ldapDao
								.fetchHimachalCoord(form_details.get("department"));
						forwardToData.put("nextlevel", Constants.ROLE_CA);
						forwardToData.put("toemail", hod_email);
						if (himachalCoordsAsRO.size() > 0) {
							String userForSearch = apiUtility.findMobilebyId(himachalCoordsAsRO.get("email"));
							form_details.put("hod_email", himachalCoordsAsRO.get("email"));
							form_details.put("hod_mobile", userForSearch);
							form_details.put("hod_name", himachalCoordsAsRO.get("name"));
							forwardToData.put("toemail", himachalCoordsAsRO.get("email"));
						}
					} else {
						forwardToData.put("nextlevel", Constants.ROLE_CA);
						forwardToData.put("toemail", hod_email);
					}
				}
			} else {
				forwardToData.put("nextlevel", Constants.ROLE_CA);
				forwardToData.put("toemail", hod_email);
			}

			break;
		}
		if (updateStatus(ref_num, currentLevel, submissionType, forwardBean.getStatRemarks(), forwardBean.getFile(),
				forwardBean.getRename_file(), forwardBean.getApp_ca_type(), forwardBean.getApp_ca_path(), forwardToData,
				applicant_name, applicant_email, applicant_mobile, hod_email, hod_name, hod_mobile,
				forwardBean.getFormtype())) {
			return forwardToData;
		} else {
			return null;
		}

	}

	public boolean updateStatus(String ref_num, String role, String submissionType, String statRemarks, String file,
			String rename_file, String app_ca_type, String app_ca_path, Map<String, String> forwardToData,
			String applicant_name, String applicant_email, String applicant_mobile, String hod_email, String hod_name,
			String hod_mobile, String formType) {
		log.info("Inside updateStatus method at " + new Date());
		boolean status = false;
		Map<String, String> forwardBydata = new HashMap<>();
		forwardBydata.put("regNumber", ref_num);
		forwardBydata.put("formType", formType);
		forwardBydata.put("forwardedByName", applicant_name);
		forwardBydata.put("forwardedByMobile", applicant_mobile);
		forwardBydata.put("forwardedByEmail", applicant_email);
		forwardBydata.put("role", role);
		String currentLevel = forwardToData.get("currentlevel");
		forwardToData.put("forwardedToName", "");
		forwardToData.put("forwardedToMobile", "");
		forwardToData.put("forwardedToEmail", forwardToData.get("toemail"));
		forwardBydata.put("check", submissionType);
		if (statRemarks != null) {
			forwardToData.put("remarks", statRemarks);
		}
		log.info("Inside updateStatus method at " + new Date() + " currentLevel:::" + currentLevel);
		forwardBydata.put("forwardedBy", "a");
		forwardBydata.put("check", submissionType);
		forwardBydata.put("filePath", file);
		log.info("Inside updateStatus method at " + new Date() + " nextlevel:::" + forwardToData.get("nextlevel"));
		switch (forwardToData.get("nextlevel")) {
		case Constants.ROLE_USER:
			forwardToData.put("forwardedTo", "a");
			forwardToData.put("forwardedToName", applicant_name);
			forwardToData.put("forwardedToMobile", applicant_mobile);
			forwardToData.put("forwardedToEmail", applicant_email);
			forwardToData.put("status", Constants.USER_PENDING_STATUS);
			break;
		case Constants.ROLE_CA:
			forwardToData.put("forwardedTo", "ca");
			forwardToData.put("forwardedToName", hod_name);
			forwardToData.put("forwardedToMobile", hod_mobile);
			forwardToData.put("forwardedToEmail", hod_email);
			forwardToData.put("status", Constants.RO_PENDING_STATUS);
			break;
		case Constants.ROLE_CO:
			forwardToData.put("forwardedTo", "c");
			forwardToData.put("status", Constants.CO_PENDING_STATUS);
			break;
		case Constants.ROLE_SUP:
			forwardToData.put("forwardedTo", "s");
			forwardToData.put("forwardedToEmail", Constants.INOC_SUPPORT_EMAIL);
			forwardToData.put("status", Constants.SUPPORT_PENDING_STATUS);
			break;
		case Constants.ROLE_DA:
			forwardToData.put("forwardedTo", "da");
			forwardToData.put("status", Constants.DA_PENDING_STATUS);
			break;
		case Constants.ROLE_MAILADMIN:
			forwardToData.put("forwardedTo", "m");
			forwardToData.put("status", Constants.MAILADMIN_PENDING_STATUS);
			break;
		default:
			forwardToData.put("forwardedTo", "");
			forwardToData.put("status", Constants.COMPLETED);
			break;
		}
		if (insertTrackTable(forwardBydata, forwardToData) > 0) {
			status = true;
		}
		return status;
	}

	public int insertTrackTable(Map<String, String> forwardBydata, Map<String, String> forwardToData) {
		log.info("Inside insertTrackTable method at " + new Date());
		String role = forwardBydata.get("role");
		log.info("Inside insertTrackTable method at " + new Date() + "  role:::" + role);
		int i = 0, j = 0, k = 0;
		i = ldapDao.insertIntoBaseTbl(forwardBydata, forwardToData);
		log.info("Inside insertTrackTable method at " + new Date() + "  i:::" + i);
		// if (forwardBydata.get("check").equalsIgnoreCase("upload_scanned")) {
		// j = submissionDAO.updateFinalAuditTrack(forwardBydata, forwardToData);
		// log.info("Inside SubmissionService: updateTrackTable method at " + new
		// Date()+" j:::"+j);
		// } else {
		j = ldapDao.insertIntoFinalAuditTrack(forwardBydata, forwardToData);
		log.info("Inside insertTrackTable method at " + new Date() + "  j:::" + j);
		// }
		if (i > 0 && j > 0) {
			k = ldapDao.insertIntoStatusTable(forwardBydata, forwardToData);
			log.info("Inside insertTrackTable method at " + new Date() + "  k:::" + k);
		}
		return k;
	}

	public static String audit(String empno) throws ParserConfigurationException, SAXException, IOException,
			ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
		log.info("Inside audit method at " + new Date());
		URL url;
		String ret_val = "";
		// String http =
		// "https://personnel.nic.in/webservice_ldap/emdmaster.php?processid=EmployeeMaster&var="
		// + empno;
		String http = "https://personnel.nic.in/wcarws/wcar_data_service.php?format=xml&url=" + empno;
		try {
			SSLContext ssl_ctx = SSLContext.getInstance("TLS");
			TrustManager[] trust_mgr = get_trust_mgr();
			ssl_ctx.init(null, trust_mgr, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(ssl_ctx.getSocketFactory());

			url = new URL(http);
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			// con.setRequestMethod("HEAD");
			con.setConnectTimeout(10000); // set timeout to 10 seconds
			String res = print_content(con);

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			InputSource is;
			try {
				builder = factory.newDocumentBuilder();
				is = new InputSource(new StringReader(res));
				Document doc = builder.parse(is);
				NodeList audit_status = doc.getElementsByTagName("audit_status");
				if (audit_status.item(0).getTextContent() != null) {
					ret_val = audit_status.item(0).getTextContent();
				}
			} catch (ParserConfigurationException e) {
			} catch (SAXException e) {
			} catch (IOException e) {
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
		log.info("Inside audit method at " + new Date() + "  ret_val::::" + ret_val);
		return ret_val;
	}

	private static TrustManager[] get_trust_mgr() {
		log.info("Inside get_trust_mgr method at " + new Date());
		TrustManager[] certs = new TrustManager[] { new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs, String t) {
			}

			public void checkServerTrusted(X509Certificate[] certs, String t) {
			}
		} };
		log.info("Insideget_trust_mgr method at " + new Date() + "  certs:::" + certs);
		return certs;
	}

	private static String print_content(HttpsURLConnection con) {
		log.info("Inside print_content method at " + new Date());
		String res = "";
		String input;
		if (con != null) {
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
				while ((input = br.readLine()) != null) {
					res += input;
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		log.info("Inside print_content method at " + new Date() + "  res:::" + res);
		return res;
	}
	// *******************End of submission********************

	// *******************start of Preview(after submission)********************//
	public Map<String, Object> preview(String regno, String role, String applicant_email) {
		log.info("Inside preview method at  " + new Date());
		Map<String, Object> map = null;
		Set<String> fetchAliases = new HashSet<String>(apiUtility.fetchAliases(applicant_email));
		log.info("Inside preview method at  " + new Date() + " fetchAliases:::" + fetchAliases);
		
		//change with proper name 
		boolean isEditable = ldapDao.checkStatusForPreview(regno, applicant_email, role, "toPreview", fetchAliases);
		log.info("Inside preview method at  " + new Date() + " isEditable:::" + isEditable);
		if (isEditable) {
			map = ldapDao.preview(regno);
		}
		log.info("Inside preview method at  " + new Date() + " map:::" + map);
		return map;
	}

	public Map<String, Object> EditpreviewDetails(String regno, String role, String applicant_email) {
		log.info("Inside EditpreviewDetails method at  " + new Date());
		Map<String, Object> map = null;

		Set<String> fetchAliases = new HashSet<String>(apiUtility.fetchAliases(applicant_email));
		log.info("Inside EditpreviewDetails method at  " + new Date() + " fetchAliases:::" + fetchAliases);
		boolean isEditable = ldapDao.checkStatusForEdit(regno, applicant_email, role, "toEdit", fetchAliases);
		log.info("Inside EditpreviewDetails method at  " + new Date() + " isEditable:::" + isEditable);
		if (isEditable) {
			map = ldapDao.EditPreview(regno);
		}
		log.info("Inside EditpreviewDetails method at  " + new Date() + " map:::" + map);
		return map;
	}

	public Map<String, Object> UpdatePreviewDetails(LdapFormBean ldapFormBean) {
		log.info("Inside LdapService: UpdatePreviewDetails method at  " + new Date());
		Map<String, Object> map = new HashMap<String, Object>();
		boolean status = false;
		status = ldapDao.UpdatePreviewDetails(ldapFormBean);
		log.info("Inside UpdatePreviewDetails method at  " + new Date() + "  status:::" + status);
		if (status) {
			map.put("status", "Preview Updated successfully!!");
			map.put("RegistrationNo", ldapFormBean.getRegistrationno());
		} else {
			map.put("status", "Something went wrong!!");
			map.put("RegistrationNo", ldapFormBean.getRegistrationno());
		}
		log.info("Inside UpdatePreviewDetails method at  " + new Date() + "  map:::" + map);
		return map;
	}

	// *******************End of Preview(after submission)********************//

	// *****************start of forward/approve****************************

	public boolean validateRefNo(String refNo) {
		return ldapDao.validateRefNo(refNo);
	}

	public Map<String, String> approve(ForwardBean forwardBean) {
		log.info("Inside approve method at " + new Date());
		Map<String, String> form_details = null;
		Map<String, String> forwardToData = new HashMap<>();
		boolean isPostedInDelhi = false;
		String fetchToEmail = "";
		form_details = ldapDao.fetchFormDetail(forwardBean.getRef_num());
		log.info("Inside approve method at " + new Date() + " form_details:::" + form_details);
		String currentLevel = forwardBean.getRole();
		String submissionType = forwardBean.getCheck();

		String ref_num = forwardBean.getRef_num();
		String statRemarks = forwardBean.getStatRemarks();
		String file = forwardBean.getFile();
		String rename_file = forwardBean.getRename_file();
		String app_ca_type = forwardBean.getApp_ca_type();
		String app_ca_path = forwardBean.getApp_ca_path();
		String role = currentLevel;
		String employment = form_details.get("applicant_employment");
		String state = form_details.get("add_state");

		HashMap<String, Object> profileList = getProfileInfo.fetchprofile1(forwardBean.getUemail());

		String applicant_email = forwardBean.getUemail();
		String applicant_name = (String) profileList.get("applicant_name");
		String applicant_mobile = (String) profileList.get("applicant_mobile");
		String hod_email = (String) profileList.get("hod_email");
		String hod_name = (String) profileList.get("hod_name");
		String hod_mobile = (String) profileList.get("hod_mobile");

		boolean isNicEmployee = apiUtility.isNICEmployeeApi(applicant_email);
		log.info("Inside approve method at " + new Date() + " isNicEmployee:::" + isNicEmployee);
		log.info("Inside approve method at " + new Date() + " currentLevel:::" + currentLevel);

		forwardToData.put("currentlevel", currentLevel);

		switch (currentLevel) {
		case Constants.ROLE_USER:
			if (submissionType.equals("manual_upload")) {
				forwardToData.put("nextlevel", Constants.ROLE_USER);
				forwardToData.put("toemail", applicant_email);
			} else if (submissionType.equals("upload_scanned")) {
				if (form_details != null) {
					if (isNicEmployee) {
						forwardToData.put("nextlevel", Constants.ROLE_CA);
						forwardToData.put("toemail", hod_email);
					} else if (employment.equalsIgnoreCase("state") && state.equalsIgnoreCase("Himachal Pradesh")) {
						Map<String, String> himachalCoordsAsRO = ldapDao
								.fetchHimachalCoord(form_details.get("department"));
						forwardToData.put("nextlevel", Constants.ROLE_CA);
						forwardToData.put("toemail", hod_email);
						if (himachalCoordsAsRO.size() > 0) {
							String userForSearch = apiUtility.findMobilebyId(himachalCoordsAsRO.get("email"));
							form_details.put("hod_email", himachalCoordsAsRO.get("email"));
							form_details.put("hod_mobile", userForSearch);
							form_details.put("hod_name", himachalCoordsAsRO.get("name"));
							forwardToData.put("toemail", himachalCoordsAsRO.get("email"));
						}
					} else {
						forwardToData.put("nextlevel", Constants.ROLE_CA);
						forwardToData.put("toemail", hod_email);
					}
				}
			} else {
				forwardToData.put("nextlevel", Constants.ROLE_CA);
				forwardToData.put("toemail", hod_email);
			}

			break;
		case Constants.ROLE_CA:
			try {
				if (employment.equals("state") && state.contains("Delhi")) {
					isPostedInDelhi = true;
				}
				Set<String> fetchAliases = new HashSet<String>(apiUtility.fetchAliases(applicant_email));
				log.info("Inside approve method at " + new Date() + " fetchAliases:::" + fetchAliases);
				Map<String, Object> coordinatorDetails = fetchCoordinators(form_details, role, applicant_email,
						fetchAliases);
				log.info("Inside approve method at " + new Date() + " coordinatorDetails:::" + coordinatorDetails);
				Set<String> coordinators = (Set<String>) coordinatorDetails.get("coordinators");
				String coemails = ldapDao.convertSetToString(coordinators);
				boolean isApplicantCoordinator = (boolean) coordinatorDetails.get("isApplicantCoordinator");
				boolean isRoCoordinator = (boolean) coordinatorDetails.get("isRoCoordinator");
				boolean isRONICEmployee = apiUtility.isNICEmployeeApi(hod_email);
				// boolean isUserNICOutsourced = isNICOutSourced(uemail);
				boolean isUserNICOutsourced = apiUtility.isNICOutSourcedApi(applicant_email);
				// boolean isRoNICOutsourced = isNICOutSourced(hodEmails);
				boolean isRoNICOutsourced = apiUtility.isNICOutSourcedApi(hod_email);
				/// boolean isDaEmailASupportID = (boolean)
				/// coordinatorDetails.get("isDaEmailASupportID");
				fetchToEmail = ldapDao.fetchToEmail();
				log.info("Inside approve method at " + new Date() + " fetchToEmail:::" + fetchToEmail);
				// HashMap hm =
				// forwardUtility.fetchEmpCoordsAtCA(forwardBean.getRef_num(),forwardBean.getFormtype());
				log.info("Inside approve method at " + new Date() + " submissionType:::" + submissionType);
				String val = "";
				if (submissionType.equals("upload_scanned")) {
					System.err.println("***************1***********");
					forwardToData.put("nextlevel", Constants.ROLE_CA);
					forwardToData.put("toemail", hod_email);
				} else if (isNicEmployee) {
					System.err.println("***************2***********");
					forwardToData.put("nextlevel", Constants.ROLE_MAILADMIN);
					forwardToData.put("toemail", Constants.RAJESH_SINGH_EMAIL);
				} else if (isRONICEmployee && isUserNICOutsourced && isPostedInDelhi) {
					System.err.println("***************3***********");
					forwardToData.put("nextlevel", Constants.ROLE_MAILADMIN);
					forwardToData.put("toemail", Constants.RAJESH_SINGH_EMAIL);
				} else if (isRoNICOutsourced && isUserNICOutsourced) {
					System.err.println("***************4***********");
					forwardToData.put("nextlevel", Constants.ROLE_CO);
					forwardToData.put("toemail", coemails);
				} else if (isApplicantCoordinator || isRoCoordinator) {
					System.err.println("***************5***********");
					forwardToData.put("nextlevel", Constants.ROLE_MAILADMIN);
					forwardToData.put("toemail", fetchToEmail);
				} else {
					System.err.println("***************6***********");
					forwardToData.put("nextlevel", Constants.ROLE_CO);
					forwardToData.put("toemail", coemails);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case Constants.ROLE_CO:
			forwardToData.put("nextlevel", Constants.ROLE_MAILADMIN);
			forwardToData.put("toemail", Constants.RAJESH_SINGH_EMAIL);
			break;
		case Constants.ROLE_SUP:
			String adminType = forwardBean.getChoose_da_type();
			String toEmails = forwardBean.getToEmailFromSuppportConsole();
			Set<String> fetchAliases1 = new HashSet<String>(apiUtility.fetchAliases(applicant_email));
			Set<String> correctRecpEmails = null;
			String correctRecpEmailsInString = "";
			boolean errorFlag = false;
			if (adminType.equals("da")) {
				correctRecpEmails = ldapDao.isRecipientDa(toEmails, fetchAliases1);
				if (!correctRecpEmails.isEmpty()) {
					correctRecpEmailsInString = ldapDao.convertSetToString(correctRecpEmails);
					if (correctRecpEmails.contains("support@gov.in") || correctRecpEmails.contains("support@nic.in")
							|| correctRecpEmails.contains("support@dummy.nic.in")) {
						forwardToData.put("nextlevel", Constants.ROLE_MAILADMIN);
						correctRecpEmailsInString = fetchToEmail;
					} else {
						forwardToData.put("nextlevel", Constants.ROLE_DA);
					}
				} else {
					errorFlag = true;
				}
			} else if (adminType.equals("m")) {
				correctRecpEmails = ldapDao.isRecipientAdmin(toEmails);
				if (!correctRecpEmails.isEmpty()) {
					correctRecpEmailsInString = ldapDao.convertSetToString(correctRecpEmails);
					forwardToData.put("nextlevel", Constants.ROLE_MAILADMIN);
				} else {
					errorFlag = true;
				}
			} else if (adminType.equals("c")) {
				correctRecpEmails = ldapDao.isRecipientCoordinator(toEmails, fetchAliases1);
				if (!correctRecpEmails.isEmpty()) {
					correctRecpEmailsInString = ldapDao.convertSetToString(correctRecpEmails);
					if (correctRecpEmails.contains("support@gov.in") || correctRecpEmails.contains("support@nic.in")
							|| correctRecpEmails.contains("support@dummy.nic.in")) {
						forwardToData.put("nextlevel", Constants.ROLE_MAILADMIN);
						correctRecpEmailsInString = fetchToEmail;
					} else {
						forwardToData.put("nextlevel", Constants.ROLE_CO);
					}
				} else {
					errorFlag = true;
				}
			}

			if (errorFlag) {
				forwardToData.put("nextlevel", "");
				forwardToData.put("toemail", "");
				forwardToData.put("error", "Invalid recipients, Please check and try again");
			} else {
				forwardToData.put("toemail", correctRecpEmailsInString);
			}
			break;
		case Constants.ROLE_DA:
			forwardToData.put("nextlevel", Constants.ROLE_DA);
			forwardToData.put("toemail", Constants.ROLE_DA);
			break;
		case Constants.ROLE_MAILADMIN:
			forwardToData.put("nextlevel", Constants.COMPLETED);
			forwardToData.put("toemail", "");
			// String zmsg=changeipdao.markAsDone(data);
			// forwardToData.put("zimramsg", zmsg);
			// call createID here

			break;
		}
		if (updateStatus(ref_num, role, submissionType, statRemarks, file, rename_file, app_ca_type, app_ca_path,
				forwardToData, applicant_name, applicant_email, applicant_mobile, hod_email, hod_name, hod_mobile)) {
			return forwardToData;
		} else {
			return null;
		}

	}

	public boolean updateStatus(String ref_num, String role, String submissionType, String statRemarks, String file,
			String rename_file, String app_ca_type, String app_ca_path, Map<String, String> forwardToData,
			String applicant_name, String applicant_email, String applicant_mobile, String hod_email, String hod_name,
			String hod_mobile) {
		log.info("Inside updateStatus method at " + new Date());
		boolean status = false;
		Map<String, String> forwardBydata = new HashMap<>();
		forwardBydata.put("regNumber", ref_num);
		forwardBydata.put("formType", "ldap");
		forwardBydata.put("forwardedByName", applicant_name);
		forwardBydata.put("forwardedByMobile", applicant_mobile);
		forwardBydata.put("forwardedByEmail", applicant_email);
		forwardBydata.put("role", role);
		String currentLevel = forwardToData.get("currentlevel");
		forwardToData.put("forwardedToName", "");
		forwardToData.put("forwardedToMobile", "");
		forwardToData.put("forwardedToEmail", forwardToData.get("toemail"));
		forwardBydata.put("check", submissionType);
		forwardBydata.put("app_ca_path", app_ca_path);
		forwardBydata.put("app_ca_type", app_ca_type);
		if (statRemarks != null) {
			forwardToData.put("remarks", statRemarks);
		}
		log.info("Inside updateStatus method at " + new Date() + "  currentLevel:::" + currentLevel);
		switch (currentLevel) {
		case Constants.ROLE_USER:
			forwardBydata.put("forwardedBy", "a");
			forwardBydata.put("check", submissionType);
			forwardBydata.put("filePath", file);
			if (submissionType.toString().equals("upload_scanned")) {
				forwardBydata.put("renameFilePath", rename_file);
			}
			break;
		case Constants.ROLE_CA:
			forwardBydata.put("forwardedBy", "ca");
			forwardBydata.put("check", app_ca_type);
			if (submissionType.equals("upload_scanned")) {
				forwardBydata.put("renameFilePath", rename_file);
				forwardBydata.put("filePath", file);
				forwardBydata.put("check", "upload_scanned");
			} else {
				forwardBydata.put("filePath", app_ca_path);
				forwardBydata.put("renameFilePath", rename_file);
			}
			break;
		case Constants.ROLE_CO:
			forwardBydata.put("forwardedBy", "c");
			forwardBydata.put("check", "online");
			forwardBydata.put("filePath", "");
			break;
		case Constants.ROLE_SUP:
			forwardBydata.put("forwardedBy", "s");
			forwardBydata.put("check", "online");
			forwardBydata.put("filePath", "");
			break;
		case Constants.ROLE_DA:
			forwardBydata.put("forwardedBy", "da");
			forwardBydata.put("check", "online");
			forwardBydata.put("filePath", "");
			break;
		case Constants.ROLE_MAILADMIN:
			forwardBydata.put("forwardedBy", "m");
			forwardBydata.put("check", "online");
			forwardBydata.put("filePath", "");
			break;
		}
		log.info("Inside updateStatus method at " + new Date() + "  nextlevel:::" + forwardToData.get("nextlevel"));
		switch (forwardToData.get("nextlevel")) {
		case Constants.ROLE_USER:
			forwardToData.put("forwardedTo", "a");
			forwardToData.put("forwardedToName", applicant_name);
			forwardToData.put("forwardedToMobile", applicant_mobile);
			forwardToData.put("forwardedToEmail", applicant_email);
			forwardToData.put("status", Constants.USER_PENDING_STATUS);
			break;
		case Constants.ROLE_CA:
			forwardToData.put("forwardedTo", "ca");
			forwardToData.put("forwardedToName", hod_name);
			forwardToData.put("forwardedToMobile", hod_mobile);
			forwardToData.put("forwardedToEmail", hod_email);
			forwardToData.put("status", Constants.RO_PENDING_STATUS);
			break;
		case Constants.ROLE_CO:
			forwardToData.put("forwardedTo", "c");
			forwardToData.put("status", Constants.CO_PENDING_STATUS);
			break;
		case Constants.ROLE_SUP:
			forwardToData.put("forwardedTo", "s");
			forwardToData.put("forwardedToEmail", Constants.INOC_SUPPORT_EMAIL);
			forwardToData.put("status", Constants.SUPPORT_PENDING_STATUS);
			break;
		case Constants.ROLE_DA:
			forwardToData.put("forwardedTo", "da");
			forwardToData.put("status", Constants.DA_PENDING_STATUS);
			break;
		case Constants.ROLE_MAILADMIN:
			forwardToData.put("forwardedTo", "m");
			forwardToData.put("status", Constants.MAILADMIN_PENDING_STATUS);
			break;
		default:
			forwardToData.put("forwardedTo", "");
			forwardToData.put("status", Constants.COMPLETED);
			break;
		}
		if (updateTrackTable(forwardBydata, forwardToData) > 0) {
			status = true;
		}
		return status;
	}

	public int updateTrackTable(Map<String, String> forwardBydata, Map<String, String> forwardToData) {
		String role = forwardBydata.get("role");
		log.info("Inside updateTrackTable method at " + new Date() + " role:::" + role);
		int i = 0, j = 0, k = 0;
		if (role.equals(Constants.ROLE_CA)) {
			j = ldapDao.updateFinalAuditTrack(forwardBydata, forwardToData);
			log.info("Inside updateTrackTable method at " + new Date() + " j:::" + j);
		} else {
			j = ldapDao.updateFinalAuditTrack(forwardBydata, forwardToData);
			log.info("Inside updateTrackTable method at " + new Date() + " j:::" + j);
		}
		if (j > 0) {
			k = ldapDao.updateStatusTable(forwardBydata, forwardToData);
			log.info("Inside updateTrackTable method at " + new Date() + " k:::" + k);
		}
		return k;
	}

	public Map<String, Object> fetchCoordinators(Map<String, String> form_details, String role, String uemail,
			Set<String> beanAliase) {
		log.info("Inside fetchCoordinators method at " + new Date() + " form_details:::" + form_details);
		HashMap<String, Object> empCoords = new HashMap<>();
		String punjabCoords = "";
		String punjabDA = "";
		empCoords.put("isDaAvailable", false);
		empCoords.put("isApplicantCoordinator", false);
		empCoords.put("isRoCoordinator", false);
		empCoords.put("isDaEmailASupportID", false);
		Set<String> coordinators = new HashSet<>();
		Set<String> admins = new HashSet<>();
		Set<String> da = new HashSet<>();
		Map<String, String> fetchcoords = new HashMap<>();

		if (form_details.get("applicant_employment").equalsIgnoreCase("state")
				&& form_details.get("add_state").equalsIgnoreCase("Punjab")) {

			punjabCoords = ldapDao.fetchPunjabNodalOfficers(form_details.get("applicant_posting_city"));
			punjabDA = ldapDao.fetchPunjabDA(form_details.get("applicant_employment"),
					form_details.get("applicant_ministry"), form_details.get("applicant_department"));
			empCoords.put("coordinators", ldapDao.convertStringToSet(punjabCoords));
			empCoords.put("daEmail", punjabDA);
			empCoords.put("isDaAvailable", true);
		} else {
			fetchcoords = ldapDao.fetchEmailDetails(form_details.get("applicant_employment"),
					form_details.get("applicant_ministry"), form_details.get("applicant_department"));
			coordinators.add(fetchcoords.get("empcoordemail"));
			admins.add(fetchcoords.get("empadminemail"));
			System.err.println("After Aliases::::::" + fetchcoords);
			/*
			 * Aliases of logged in users fetching from session Logged in user can have any
			 * role. This role is basically the admin role(panel) on which s/he is taking
			 * action
			 */
			Set<String> aliases = beanAliase;
			System.err.println("After Aliases");

			// Aliases of Applicant fetching from ldap
			// Set<String> userAliases = fetchAliases(uemail);
			Set<String> userAliases = new HashSet<String>(apiUtility.fetchAliases(uemail));
			System.err.println("After email");
			// remove support id from coordinators
			Set<String> finalCoords = ldapDao.removeSupportFromSet(coordinators);

			// remove support id from admins
			Set<String> finalAdmins = ldapDao.removeSupportFromSet(admins);
			empCoords.put("coordinators", finalCoords);
			System.err.println("After coordinators");
			/*
			 * Checking if Ro is same as coordinator Setting it true when any of the
			 * multiple coordinators is RO
			 */
			for (String aliase : aliases) {
				if (role.equals("ca")) {
					if (finalCoords.contains(aliase)) {
						empCoords.put("isRoCoordinator", true);
						break;
					}
				}
			}
			System.err.println("After isRoCoordinator alias");
			// Checking if Applicant is same as coordinator
			for (String aliase : userAliases) {
				if (finalCoords.contains(aliase)) {
					empCoords.put("isApplicantCoordinator", true);
					break;
				}
			}
			System.err.println("isApplicantCoordinator");
			if (!finalCoords.isEmpty() && !finalAdmins.isEmpty()) {
				if (finalAdmins.equals(finalCoords)) {
					empCoords.put("isDaAvailable", true);
					empCoords.put("daEmail", ldapDao.convertSetToString(finalAdmins));
				} else {
					boolean flagForDA = false;
					if (form_details.get("applicant_employment").equalsIgnoreCase("state")
							&& form_details.get("add_state").equalsIgnoreCase("Himachal Pradesh")
							&& finalAdmins.contains(Constants.HIMACHAL_DA_ADMIN)) {
						empCoords.put("daEmail", Constants.HIMACHAL_DA_ADMIN);
						empCoords.put("isDaAvailable", true);
					} else {
						for (String coord : finalCoords) {
							Set<String> alias = new HashSet<String>(apiUtility.fetchAliases(uemail));
							// Set<String> alias = fetchAliases(coord);
							for (String alia : alias) {
								if (finalAdmins.contains(alia)) {
									flagForDA = true;
									da.add(alia);
								}
							}
						}
						if (flagForDA) {
							empCoords.put("isDaAvailable", true);
							empCoords.put("daEmail", ldapDao.convertSetToString(da));
						}
					}
				}
			} else {
				empCoords.put("isDaAvailable", false);
				empCoords.put("isDaEmailASupportID", true);
				empCoords.put("daEmail", "");
			}
		}
		log.info("Inside fetchCoordinators method at " + new Date() + " empCoords:::" + empCoords);
		return empCoords;
	}

	// *****************End of forward/approve******************************

	// ************************Start of Track User****************************
	public HashMap<String, Object> fetchTrackDetails(String reg_no) {
		log.info("Inside fetchTrackDetails method at " + new Date());
		int i = 0;
		HashMap<String, Object> hmTrack = new HashMap<>();
		HashMap<String, Object> fetchDaoList = new HashMap<>();

		fetchDaoList = ldapDao.fetchTrackDetails(reg_no);
		log.info("Inside fetchTrackDetails method at " + new Date() + "  fetchDaoList:::" + fetchDaoList);
		List<LdapStatusDTO> arrRoles = (List<LdapStatusDTO>) fetchDaoList.get("arrRoles");
		log.info("Inside fetchTrackDetails method at " + new Date() + "  arrRoles:::" + arrRoles);
		Map<String, String> fetchdetails = validateRoles(arrRoles);
		log.info("Inside fetchTrackDetails method at " + new Date() + "  fetchdetails:::" + fetchdetails);
		List<LdapFinalAuditTrackDTO> currentDetails = (List<LdapFinalAuditTrackDTO>) fetchDaoList.get("currentDetails");
		Map<String, String> details = validateDetails(currentDetails);
		log.info("Inside fetchTrackDetails method at " + new Date() + "  details:::" + details);
		String msg = "Status->" + details.get("status_msg") + "=>" + details.get("app_date_text") + "->"
				+ details.get("recv_date") + "=>Sender Details->" + fetchdetails.get("senderName") + "("
				+ fetchdetails.get("senderEmail") + ")";
		hmTrack.put("roles", arrRoles);
		hmTrack.put("status", details.get("status"));
		hmTrack.put("name", details.get("applicantname"));
		hmTrack.put("mob", details.get("applicant_mob"));
		hmTrack.put("email", details.get("app_email"));
		hmTrack.put("form_name", details.get("form_name"));
		hmTrack.put("forword_date", details.get("app_sub_date"));
		hmTrack.put("msg", msg);
		hmTrack.put("reg_no", reg_no);
		log.info("Inside fetchTrackDetails method at " + new Date() + "  hmTrack:::" + hmTrack);
		return hmTrack;
	}

	public Map<String, Object> fetchTrackByRole(String reg_no, String forward, String trole, String srole) {
		log.info("Inside fetchTrackByRole method at " + new Date() + " reg_no:::" + reg_no + "  forward:::" + forward
				+ "  trole:::" + "  srole:::" + srole);
		Map<String, Object> hmTrack = new HashMap<>();
		String reqProcessedAs = "", app_date_text = "", status = "";
		Map<String, Object> getDaoVal = ldapDao.fetchTrackByRole(reg_no, forward, trole, srole);

		String stat_type = (String) getDaoVal.get("stat_type");
		String stat_on_hold = (String) getDaoVal.get("stat_on_hold");
		String current_user = (String) getDaoVal.get("current_user");
		String stat_process = (String) getDaoVal.get("stat_process");
		String remarks = (String) getDaoVal.get("remarks");
		String recv_date = (String) getDaoVal.get("recv_date");
		String sender_details = (String) getDaoVal.get("sender_details");

		if (forward.equalsIgnoreCase("")) {
			String role = findRole(trole);
			Map<String, Object> forwardList = validateForward(stat_type, srole, current_user, recv_date, role);
			reqProcessedAs = (String) forwardList.get("reqProcessedAs");
			app_date_text = (String) forwardList.get("app_date_text");
			status = (String) forwardList.get("status");
			recv_date = (String) forwardList.get("recv_date");

		} else if (trole.equalsIgnoreCase("undefined") || trole.isEmpty()) {
			String role = findRole(srole);
			Map<String, Object> forwardByList = validateforwardBy(stat_type, stat_on_hold, recv_date, role);
			reqProcessedAs = (String) forwardByList.get("reqProcessedAs");
			app_date_text = (String) forwardByList.get("app_date_text");
			status = (String) forwardByList.get("status");

		} else if (stat_type.toLowerCase().contains("pending")) {
			String s_role = findRole(srole);
			String t_role = findRole(trole);
			Map<String, Object> forwardStatusList = validateStatus(stat_process, forward, srole, current_user,
					recv_date, s_role, t_role);
			status = (String) forwardStatusList.get("status");
			app_date_text = (String) forwardStatusList.get("app_date_text");
		}

		String msg = "Status->" + status + "=>Receiving Date->" + recv_date + "=>" + app_date_text + "=>Remarks->"
				+ remarks + "=>Sender Details->" + sender_details;
		hmTrack.put("msg", msg);
		hmTrack.put("reg_no", reg_no);
		log.info("Inside fetchTrackByRole method at " + new Date() + " hmTrack:::" + hmTrack);
		return hmTrack;

	}

	public Map<String, Object> validateForward(String stat_type, String srole, String current_user, String recv_date,
			String role) {
		log.info("Inside validateForward method at " + new Date());
		String reqProcessedAs = "", app_date_text = "", status = "";
		Map<String, Object> forwardList = new HashMap<>();
		log.info("Inside validateForward method at " + new Date() + " stat_type:::" + stat_type);
		if (stat_type.toLowerCase().contains("pending") && srole.equalsIgnoreCase("a")) {
			reqProcessedAs = "Approved";
			app_date_text = "Submission Date->" + recv_date;
			status = "Form has been successfully submitted by you (" + current_user + ") and forwarded to (" + role
					+ ")";
			recv_date = "";
		} else if (stat_type.toLowerCase().contains("manual") && srole.equalsIgnoreCase("a")) {
			reqProcessedAs = "Pending";
			app_date_text = "Submission Date->" + recv_date;
			status = "You have chosen manual option to submit. Hence, request is pending with you only. ("
					+ current_user + ")";
			recv_date = "";
		} else if (stat_type.toLowerCase().contains("cancel") && srole.equalsIgnoreCase("a")) {
			reqProcessedAs = "Cancelled";
			app_date_text = "Cancellation Date->" + recv_date;
			status = "Request has been cancelled by you (" + current_user + ").";
			recv_date = "";
		}
		forwardList.put("reqProcessedAs", reqProcessedAs);
		forwardList.put("app_date_text", app_date_text);
		forwardList.put("status", status);
		forwardList.put("recv_date", recv_date);
		log.info("Inside validateForward method at " + new Date() + " forwardList:::" + forwardList);
		return forwardList;
	}

	public Map<String, Object> validateforwardBy(String stat_type, String stat_on_hold, String recv_date, String role) {
		log.info("Inside validateforwardBy method at " + new Date());
		String reqProcessedAs = "", app_date_text = "", status = "";
		Map<String, Object> forwardByList = new HashMap<>();
		log.info("Inside validateforwardBy method at " + new Date() + " stat_type:::" + stat_type);
		if (stat_type.toLowerCase().contains("pending")) {
			if (stat_on_hold.toLowerCase().equalsIgnoreCase("y")) {
				reqProcessedAs = "On Hold";
				app_date_text = "On Hold Since->" + recv_date;
			} else {
				status = "Pending with " + role;
				app_date_text = "Pending Since->" + recv_date;
			}
		} else if (stat_type.toLowerCase().contains("rejected")) {
			status = "Rejected by " + role;
			app_date_text = "Rejection Date->" + recv_date;
		} else if (stat_type.toLowerCase().equalsIgnoreCase("completed")) {
			status = "Completed by " + role;
			app_date_text = "Completion Date->" + recv_date;
		}
		forwardByList.put("reqProcessedAs", reqProcessedAs);
		forwardByList.put("app_date_text", app_date_text);
		forwardByList.put("status", status);
		log.info("Inside validateforwardBy method at " + new Date() + " forwardByList:::" + forwardByList);
		return forwardByList;
	}

	public Map<String, Object> validateStatus(String stat_process, String forward, String srole, String current_user,
			String recv_date, String s_role, String t_role) {
		log.info("Inside validateStatus method at " + new Date());
		String app_date_text = "", status = "";
		Map<String, Object> forwardStatusList = new HashMap<>();
		if (!(stat_process == null || stat_process.isEmpty())) {
			String[] aa = stat_process.split("_");
			String process = aa[0];
			String actionBy = aa[1];
			String actionFor = aa[2];

			if (process.equalsIgnoreCase("pulled")) {
				status = "Pulled by " + findRole(actionBy) + " from " + findRole(actionFor);
			} else if (process.equalsIgnoreCase("reverted")) {
				status = "Reverted by " + findRole(actionBy) + " to " + findRole(actionFor);
			} else if (process.equalsIgnoreCase("forwarded")) {
				status = "Forwarded by " + findRole(actionBy) + " to " + findRole(actionFor);
			}
		} else if (forward.equalsIgnoreCase("a") && srole.equalsIgnoreCase("a")) {
			status = "Submitted by " + s_role + "(" + current_user + ") and forwarded to (" + t_role + ")";
			app_date_text = "Submission Date->" + recv_date;
		} else {
			status = "Approved by " + s_role + "(" + current_user + ") and forwarded to (" + t_role + ")";
			app_date_text = "Approving Date->" + recv_date;
		}

		forwardStatusList.put("status", status);
		forwardStatusList.put("app_date_text", app_date_text);
		log.info("Inside validateStatus method at " + new Date() + "  forwardStatusList:::" + forwardStatusList);
		return forwardStatusList;
	}

	public Map<String, String> validateRoles(List<LdapStatusDTO> arrRoles) {
		log.info("Inside validateRoles method at " + new Date());
		int i = 0;
		Map<String, String> fetchdetails = new HashMap<>();
		List<String> fetchRoles = new ArrayList<>();
		for (LdapStatusDTO trackStatus : arrRoles) {
			fetchRoles.add(trackStatus.getStatforwardedby() + "=>" + trackStatus.getStatforwardedto());
			if (i++ == arrRoles.size() - 1) {
				fetchRoles.add(trackStatus.getStatforwardedto());
			}
			Collections.reverse(fetchRoles);

			if (trackStatus.getStat_forwarded_by_email() == null) {
				// fetchdetails.put("senderEmail", userdata.getEmail());
				fetchdetails.put("senderEmail", "BY USERRRRR");
			}
			fetchdetails.put("senderEmail", trackStatus.getStat_forwarded_by_email());
			fetchdetails.put("senderMobile", trackStatus.getStat_forwarded_by_mobile());
			fetchdetails.put("senderName", trackStatus.getStat_forwarded_by_name());
			if (trackStatus.getStat_forwarded_by_name() == null) {
				fetchdetails.put("senderName", "Yourself ");
			}
			fetchdetails.put("submitTime", trackStatus.getStat_forwarded_by_datetime());
		}
		log.info("Entering validateRoles method at info " + new Date() + "  fetchdetails:::" + fetchdetails);
		return fetchdetails;
	}

	public Map<String, String> validateDetails(List<LdapFinalAuditTrackDTO> currentDetails) {
		log.info("Inside validateDetails method at " + new Date() + "  currentDetails:::" + currentDetails);
		Map<String, String> details = new HashMap<>();
		String statusVal = "";
		String reqProcessedAs = "";
		String app_date_text = "";
		for (LdapFinalAuditTrackDTO trackdto : currentDetails) {
			if (trackdto.getStatus().toLowerCase().contains("pending")
					|| trackdto.getStatus().toLowerCase().contains("manual")) {
				details.put("status", "pending");
			} else if (trackdto.getStatus().toLowerCase().contains("rejected")
					|| trackdto.getStatus().toLowerCase().contains("cancel")
					|| trackdto.getStatus().toLowerCase().contains("us_expired")) {
				details.put("status", "reject");
			} else if (trackdto.getStatus().toLowerCase().contains("completed")) {
				details.put("status", "active");
			}
			details.put("applicantname", trackdto.getApplicant_name());
			details.put("applicant_mob", trackdto.getApplicant_mobile());
			details.put("app_email", trackdto.getApplicant_name());
			details.put("form_name", trackdto.getForm_name());
			details.put("app_sub_date", trackdto.getApplicant_datetime());
			details.put("recv_date", trackdto.getTo_datetime());
			details.put("recv_email", trackdto.getToemail());

			if (trackdto.getStatus().toLowerCase().equals("manual_upload")) {
				statusVal = "Pending with User";
			} else if (trackdto.getStatus().toLowerCase().equals("ca_pending")) {
				statusVal = " Pending with RO/Nodal/FO";
			} else if (trackdto.getStatus().toLowerCase().equals("support_pending")) {
				statusVal = " Pending with Support";
			} else if (trackdto.getStatus().toLowerCase().equals("coordinator_pending")) {
				statusVal = " Pending with coordinator";
			} else if (trackdto.getStatus().toLowerCase().equals("mail-admin_pending")) {
				statusVal = " Pending with Admin";
			} else if (trackdto.getStatus().toLowerCase().equals("da_pending")) {
				statusVal = "Pending with DA";
			} else if (trackdto.getStatus().toLowerCase().equals("us_pending")) {
				statusVal = "Pending with US";
			}
			details.put("status_msg", statusVal + "(" + trackdto.getToemail() + ")");
			if (trackdto.getStatus().toLowerCase().contains("pending")) {
				reqProcessedAs = "Pending";
				app_date_text = "Pending Since ";
			} else if (trackdto.getStatus().toLowerCase().contains("rejected")) {
				reqProcessedAs = "Rejected";
				app_date_text = "Rejection Date";
			} else if (trackdto.getStatus().toLowerCase().contains("cancel")) {
				reqProcessedAs = "Cancelled";
				app_date_text = "Cancellation Date";
			} else if (trackdto.getStatus().toLowerCase().contains("completed")) {
				reqProcessedAs = "Completed";
				app_date_text = "Completion Date";
			} else {
				app_date_text = "Submission Date";
			}
			details.put("reqProcessedAs", reqProcessedAs);
			details.put("app_date_text", app_date_text);
		}
		log.info("Inside validateDetails method at " + new Date() + "  details:::" + details);
		return details;
	}

	public String findRole(String forward) {
		log.info("Inside LdapService: findRole method at " + new Date() + "  forward:::" + forward);
		if (forward.equals("")) {
			return "yourself";
		} else if (forward.equals("a")) {
			return "Applicant";
		} else if (forward.matches("c")) {
			return "Coordinator";
		} else if (forward.matches("ca")) {
			return "Reporting/Forwarding/Nodal Officer";
		} else if (forward.equals("d")) {
			return "DA-Admin";
		} else if (forward.equals("m")) {
			return "Admin";
		} else if (forward.equals("s")) {
			return "Support";
		} else if (forward.equals("us")) {
			return "Under Secretary";
		} else {
			return "";
		}
	}

	public String fetchRole(String role) {
		log.info("Inside LdapService: fetchRole method at " + new Date() + "  role:::" + role);
		String qr_forwarded_by = "";
		switch (role) {
		case Constants.ROLE_CA:
			qr_forwarded_by = "ca";
			break;
		case Constants.ROLE_CO:
			qr_forwarded_by = "c";
			break;
		case Constants.ROLE_SUP:
			qr_forwarded_by = "s";
			break;
		case Constants.ROLE_MAILADMIN:
			qr_forwarded_by = "m";
			break;
		case Constants.ROLE_USER:
			qr_forwarded_by = "u";
			break;
		}
		return qr_forwarded_by;
	}
	// ************************End of Track User****************************

	// ************************Start of put on/hold****************************
	public Map<String, Object> putOnHold(String regNo, String type, String role, String statRemarks) {
		log.info("Inside putOnHold method at " + new Date());
		String on_hold = "", message = "";
		boolean flag = false;
		Map<String, Object> hmTrack = new HashMap<>();

		if (type.equalsIgnoreCase("true")) {
			on_hold = "y";
			message = "On";
		} else {
			on_hold = "n";
			message = "Off";
		}
		flag = ldapDao.putOnHold(regNo, role, on_hold, statRemarks);
		if (flag) {
			hmTrack.put("status", "Application put " + message + " Hold successfully");
			hmTrack.put("Registration No.", regNo);
		} else {
			hmTrack.put("status", "Application could not be put " + message + " Hold");
			hmTrack.put("Registration No.", regNo);
		}
		log.info("Inside putOnHold method at " + new Date() + "  hmTrack:::" + hmTrack);
		return hmTrack;

	}
	// *****************End of put on/off Hold****************************

	// *****************Start of Raise Query********************************
	public Map<String, Object> raiseQuery(String regid, String role, String uemail, String choose_recp, String to_email,
			String statRemarks) {
		log.info("Inside raiseQuery method at " + new Date());
		Map<String, Object> map = null;
		map = ldapDao.raiseQuery(regid, role, uemail, choose_recp, to_email, statRemarks);
		log.info("Inside raiseQuery method at " + new Date() + "  map:::" + map);
		return map;
	}

	public ArrayList<String> fetchRecps(String regNo, String role) {
		log.info("Inside fetchRecps method at " + new Date());
		Map<String, Object> map = new HashMap<>();
		ArrayList<String> roles = new ArrayList<>();
		map = ldapDao.fetchRaiseQueryData(regNo, role);
		Set<String> recpdata = (Set<String>) map.get("recpdata");

		for (String string : recpdata) {
			if (string.contains("=>")) {
				String arr[] = string.split("=>");
				roles.add(arr[0]);
			}
		}
		roles.add("u");
		log.info("Inside fetchRecps method at " + new Date() + "  roles:::" + roles);
		return roles;
	}
	// *****************End of Raise Query**********************************

	// *****************Start of Reject************************************

	public boolean reject(String reg_no, String role, String statRemarks, String check, String email, String mobile,
			String name) {
		log.info("Inside reject method at " + new Date());
		Map<String, Object> hmTrack = new HashMap<>();
		Map<String, String> forwardToData = new HashMap<>();
		forwardToData.put("currentlevel", role);
		forwardToData.put("nextlevel", role);
		forwardToData.put("toemail", email);

		boolean status = false;
		Map<String, String> forwardBydata = new HashMap<>();
		forwardBydata.put("regNumber", reg_no);
		forwardBydata.put("forwardedByName", name);
		forwardBydata.put("forwardedByMobile", mobile);
		forwardBydata.put("forwardedByEmail", email);
		forwardBydata.put("role", role);
		String currentLevel = role;
		forwardToData.put("forwardedToName", "");
		forwardToData.put("forwardedToMobile", "");
		forwardToData.put("forwardedToEmail", email);
		forwardBydata.put("check", check);
		if (statRemarks != null) {
			forwardToData.put("remarks", statRemarks);
		}
		log.info("Inside reject method at " + new Date() + " currentLevel:::" + currentLevel);
		switch (currentLevel) {
		case Constants.ROLE_USER:
			forwardBydata.put("forwardedBy", "a");
			break;
		case Constants.ROLE_CA:
			forwardBydata.put("forwardedBy", "ca");
			break;
		case Constants.ROLE_CO:
			forwardBydata.put("forwardedBy", "c");
			break;
		case Constants.ROLE_SUP:
			forwardBydata.put("forwardedBy", "s");
			break;
		case Constants.ROLE_DA:
			forwardBydata.put("forwardedBy", "da");
			break;
		case Constants.ROLE_MAILADMIN:
			forwardBydata.put("forwardedBy", "m");
			break;
		}
		log.info("Inside reject method at " + new Date() + " nextlevel:::" + forwardToData.get("nextlevel"));
		switch (forwardToData.get("nextlevel")) {
		case Constants.ROLE_USER:
			forwardToData.put("forwardedTo", "a");
			forwardToData.put("forwardedToName", name);
			forwardToData.put("forwardedToMobile", mobile);
			forwardToData.put("forwardedToEmail", email);
			forwardToData.put("status", Constants.CANCELBYUSER);
			forwardToData.put("remarks", Constants.CANCELREMARKSBYUSER);
			break;
		case Constants.ROLE_CA:
			forwardToData.put("forwardedTo", "ca");
			forwardToData.put("forwardedToName", name);
			forwardToData.put("forwardedToMobile", mobile);
			forwardToData.put("forwardedToEmail", email);
			forwardToData.put("status", Constants.STATUS_CA_REJECTED);
			break;
		case Constants.ROLE_CO:
			forwardToData.put("forwardedTo", "c");
			forwardToData.put("status", Constants.STATUS_CO_REJECTED);
			break;
		case Constants.ROLE_SUP:
			forwardToData.put("forwardedTo", "s");
			forwardToData.put("forwardedToEmail", Constants.INOC_SUPPORT_EMAIL);
			forwardToData.put("status", Constants.STATUS_SUP_REJECTED);
			break;
		case Constants.ROLE_DA:
			forwardToData.put("forwardedTo", "da");
			forwardToData.put("status", Constants.STATUS_DA_REJECTED);
			break;
		case Constants.ROLE_MAILADMIN:
			forwardToData.put("forwardedTo", "m");
			forwardToData.put("status", Constants.STATUS_ADMIN_REJECTED);
			break;
		default:
			forwardToData.put("forwardedTo", "");
			forwardToData.put("status", Constants.STATUS_ADMIN_REJECTED);
			break;
		}
		if (ldapDao.updateTrackTable(forwardBydata, forwardToData)) {
			status = true;
		}
		log.info("Inside reject method at " + new Date() + " status:::" + status);
		return status;
	}
	// ****************End of Reject*************************************

	// *****************Start of Generate PDF********************************
	public JasperPrint generatePdf(String regid)
			throws JRException, IOException, IllegalAccessException, InvocationTargetException {
		log.info("Inside generatePdf method at " + new Date());
		JasperPrint jasperPrint = null;
		LdapBaseDTO ldapBaseDTO = ldapDao.findByRegno(regid);
		log.info("Inside generatePdf method at " + new Date() + "  ldapBaseDTO:::" + ldapBaseDTO);
		if (ldapBaseDTO != null) {
			GeneratePdfBean generatePdfBean = new GeneratePdfBean();
			org.springframework.beans.BeanUtils.copyProperties(ldapBaseDTO, generatePdfBean);
			File file = ResourceUtils.getFile("classpath:ldapreport.jrxml");
			JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
			JRDataSource datasource = new JREmptyDataSource();
			Map<String, Object> parameters = new HashMap<>();

			parameters.put("regid", generatePdfBean.getRegistrationno());
			parameters.put("applicant_name", generatePdfBean.getApplicant_name());
			parameters.put("applicant_email", generatePdfBean.getApplicant_email());
			parameters.put("min", generatePdfBean.getMin());
			parameters.put("applicant_mobile", generatePdfBean.getApplicant_mobile());
			parameters.put("hod_name", generatePdfBean.getHod_name());
			parameters.put("hod_email", generatePdfBean.getHod_email());
			parameters.put("hod_mobile", generatePdfBean.getHod_mobile());
			parameters.put("protocol", "ldap");
			parameters.put("logo", "classpath:static/NIC-Logo.jpg");
			jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, datasource);
		}
		return jasperPrint;
	}
	// ****************End of Generate PDF**********************************

	// *****************Start of Upload
	// Docs*****************************************

	public ArrayList<UploadMultipleFilesBean> saveDocx(UploadMultipleFilesBean uploadfiles) {
		log.info("Inside saveDocx method at " + new Date());
		ArrayList<UploadMultipleFilesBean> list = new ArrayList<>();
		List<MultipartFile> infile = uploadfiles.getInfile();
		String registrationno = uploadfiles.getRegno();
		String role = uploadfiles.getRole();
		UploadMultipleFilesBean response;
		for (MultipartFile f : infile) {
			if (!f.isEmpty()) {
				String[] contenttype = f.getContentType().split("/");
				String ext = contenttype[1];
				try {
					String outputfile = new StringBuilder().append(registrationno).append("_")
							.append(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
							.append(".").append(ext).toString();
					byte[] bytes = f.getBytes();
					Path path = Paths.get(fileBasePath + outputfile);
					LdapDocUploadDTO uploadfilesdto = new LdapDocUploadDTO();
					// BeanUtils.copyProperties(uploadfiles, uploadfilesdto);
					uploadfilesdto.setDoc(outputfile);
					uploadfilesdto.setDocpath(fileBasePath);
					uploadfilesdto.setExtension(ext);
					uploadfilesdto.setOriginal_filename(f.getOriginalFilename());
					uploadfilesdto.setRegistrationno(registrationno);
					uploadfilesdto.setRole(role);
					uploadfilesdto = ldapDao.saveDocx(uploadfilesdto);
					if (uploadfilesdto.getId() == 0) {
						response = new UploadMultipleFilesBean();
						response.setDoc(f.getOriginalFilename());
						response.setStatus("failed");
						list.add(response);
					} else {
						Files.write(path, bytes);
						response = new UploadMultipleFilesBean();
						response.setDoc(f.getOriginalFilename());
						response.setStatus("success");
						list.add(response);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		log.info("Inside saveDocx method at " + new Date() + " list:::" + list);
		return list;
	}
	// ******************End of Upload
	// Docs********************************************

	// ************************start of download
	// docs**************************************//
	public HttpServletResponse downloadFiles(String filename, HttpServletResponse response) {
		log.info("Inside downloadFiles method at info " + new Date());
		File f = new File(EXTERNAL_FILE_PATH + filename);
		if (f.exists()) {
			String mimeType = URLConnection.guessContentTypeFromName(f.getName());
			if (mimeType == null) {
				mimeType = "application/octet-stream";
			}
			response.setContentType(mimeType);
			response.setHeader("Content-Disposition", String.format("inline; filename=\"" + f.getName() + "\""));
			response.setContentLength((int) f.length());
			response.addHeader("status", "success");
			InputStream inputStream;
			try {
				inputStream = new BufferedInputStream(new FileInputStream(f));
				FileCopyUtils.copy(inputStream, response.getOutputStream());
			} catch (IOException e) {
				response.addHeader("status", e.getMessage());
			}
		} else {
			response.addHeader("status", "failed");
		}
		return response;
	}
	// ******************************End of download
	// docs*********************************//
}
