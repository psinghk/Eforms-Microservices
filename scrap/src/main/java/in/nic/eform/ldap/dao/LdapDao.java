package in.nic.eform.ldap.dao;

import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.nic.eform.ldap.bean.LdapFormBean;
import in.nic.eform.ldap.dto.LdapDocUploadDTO;
import in.nic.eform.ldap.dto.LdapEmpCoordDTO;
import in.nic.eform.ldap.dto.LdapFinalAuditTrackDTO;
import in.nic.eform.ldap.dto.LdapBaseDTO;
import in.nic.eform.ldap.dto.LdapMailadminFormsDTO;
import in.nic.eform.ldap.dto.LdapQueryRaiseDTO;
import in.nic.eform.ldap.dto.LdapStatusDTO;
import in.nic.eform.ldap.repository.LdapDocUploadRepo;
import in.nic.eform.ldap.repository.LdapEmpCoordRepo;
import in.nic.eform.ldap.repository.LdapFinalAuditTrackListRepo;
import in.nic.eform.ldap.repository.LdapFinalAuditTrackRepo;
import in.nic.eform.ldap.repository.LdapBaseRepository;
import in.nic.eform.ldap.repository.LdapMailadminFormsRepo;
import in.nic.eform.ldap.repository.LdapQueryRaiseRepo;
import in.nic.eform.ldap.repository.LdapStatusListRepo;
import in.nic.eform.ldap.repository.LdapStatusRepo;
import in.nic.eform.utility.ApiUtility;
import in.nic.eform.utility.CommonUtility;
import in.nic.eform.utility.Constants;

@Repository
public class LdapDao {
	private static final Logger log = LoggerFactory.getLogger(LdapDao.class);

	// please change its access modifier.
	@Autowired
	LdapBaseRepository ldapBaseRepository;
	@Autowired
	LdapFinalAuditTrackRepo finalAuditTrackRepository;
	@Autowired
	LdapFinalAuditTrackListRepo finalAuditTrackListRepository;
	@Autowired
	LdapStatusRepo statusRepository;
	@Autowired
	LdapStatusListRepo statusListRepository;
	@Autowired
	LdapMailadminFormsRepo mailadminFormsRepository;
	@Autowired
	LdapEmpCoordRepo empCoordRepository;
	@Autowired
	LdapQueryRaiseRepo queryRaiseRepository;
	@Autowired
	LdapDocUploadRepo docUploadRepository;
	@Autowired
	CommonUtility commonUtility;
	@Autowired
	ApiUtility apiUtility;

	// *******************start of submission********************

	// change name of the method
	public String ldap_tab2(LdapBaseDTO ldapBaseDTO) {
		
		// Comment : please shift Reg. no generation in utility package
		String dbrefno = "", newref = "";
		int newrefno;
		Date date1 = new Date();
		DateFormat dateFormat1 = new SimpleDateFormat("yyyyMMdd");
		String pdate1 = dateFormat1.format(date1);

		SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd");
		String datetime = format.format(date1);
		// List<SubmissionBaseDTO>
		// sbd=submissionBaseRepository.findTopByDatetimeOrderByRegistrationnoDesc(datetime);
		List<LdapBaseDTO> sbd = ldapBaseRepository.findByDatetimeLike("%" + datetime + "%");
		log.info("Inside ldap_tab2 method at " + new Date() + "  sbd:::" + sbd);

		for (LdapBaseDTO baseDTO : sbd)
			dbrefno = baseDTO.getRegistrationno();

		log.info("Inside ldap_tab2 method at " + new Date() + "  dbrefno:::" + dbrefno);

		// dbrefno=submissionBaseRepository.fetchRegNo();
		
		// what will happen with registration no if more than thread comes to same request.
		// might possible multiple request have same reg. no.
		if (dbrefno == null || dbrefno.equals("")) {
			newrefno = 1;
			newref = "000" + newrefno;
		} else {
			String lastst = dbrefno.substring(17, dbrefno.length());
			int last = Integer.parseInt(lastst);
			newrefno = last + 1;
			int len = Integer.toString(newrefno).length();
			if (len == 1) {
				newref = "000" + newrefno;
			} else if (len == 2) {
				newref = "00" + newrefno;
			} else if (len == 3) {
				newref = "0" + newrefno;
			}
		}
		newref = "LDAP-FORM" + pdate1 + newref;
		
		// IP should be in request json
		ldapBaseDTO.setRegistrationno(newref);
		ldapBaseDTO.setUserip("0:0:0:0:0:0:0:1");
		ldapBaseDTO.setDatetime(datetime);
		ldapBaseDTO.setSupport_action_taken("p");
		LdapBaseDTO a = ldapBaseRepository.save(ldapBaseDTO);
		if (a.getId() == 0) {
			newref = null;
		}
		
		log.info("Inside ldap_tab2 method at " + new Date() + "  newref:::" + newref);
		return newref;
	}

	public int updateTelnet(String telnet, String ref_num) {
		log.info("Inside updateTelnet method at " + new Date());
		int i = 0;
		LdapBaseDTO ldapBaseDTO = ldapBaseRepository.findByRegistrationno(ref_num);
		ldapBaseDTO.setTelnet_response(telnet);
		LdapBaseDTO id = ldapBaseRepository.save(ldapBaseDTO);
		if (id.getId() > 0) {
			i = 1;
		}
		log.info("Inside updateTelnet method at " + new Date() + "   i:::" + i);
		return i;
	}

	public int insertIntoBaseTbl(Map<String, String> forwardBydata, Map<String, String> forwardToData) {
		log.info("Inside insertIntoBaseTbl method at " + new Date());
		int i = 0, j = 0;
		if (forwardBydata.get("check").equalsIgnoreCase("upload_scanned")) {
			LdapBaseDTO ldapBaseDTO = ldapBaseRepository.findByRegistrationno(forwardBydata.get("regNumber"));
			ldapBaseDTO.setSign_cert(forwardBydata.get("filePath"));
			ldapBaseDTO.setRename_sign_cert(forwardBydata.get("renameFilePath"));
			LdapBaseDTO id = ldapBaseRepository.save(ldapBaseDTO);
			if (id.getId() > 0) {
				i = 1;
			}
			log.info("Inside insertIntoBaseTbl method at " + new Date() + "  i:::" + i);
		} else {
			LdapBaseDTO ldapBaseDTO = ldapBaseRepository.findByRegistrationno(forwardBydata.get("regNumber"));
			ldapBaseDTO.setPdf_path(forwardBydata.get("filePath"));
			LdapBaseDTO id = ldapBaseRepository.save(ldapBaseDTO);
			if (id.getId() > 0) {
				i = 1;
			}
			log.info("Inside insertIntoBaseTbl method at " + new Date() + "  j:::" + j);
		}
		return i;
	}

	public int insertIntoFinalAuditTrack(Map<String, String> forwardBydata, Map<String, String> forwardToData) {
		log.info("Inside insertIntoFinalAuditTrack method at " + new Date());
		int j = 0;
		Date date = new Date();
		DateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String pdate = dt.format(date);
		String ip = "0:0:0:0:0:0:0:1";
		LdapFinalAuditTrackDTO finalAuditTrackDTO = new LdapFinalAuditTrackDTO();
		finalAuditTrackDTO.setRegistrationno(forwardBydata.get("regNumber").toString());
		finalAuditTrackDTO.setApplicantemail(forwardBydata.get("forwardedByEmail"));
		finalAuditTrackDTO.setApplicant_mobile(forwardBydata.get("forwardedByMobile"));
		finalAuditTrackDTO.setApplicant_name(forwardBydata.get("forwardedByName"));
		finalAuditTrackDTO.setApplicant_ip(ip);
		finalAuditTrackDTO.setApplicant_datetime(pdate);
		finalAuditTrackDTO.setStatus(forwardToData.get("status"));
		finalAuditTrackDTO.setForm_name(forwardBydata.get("formType"));
		finalAuditTrackDTO.setToemail(forwardToData.get("forwardedToEmail").replace(",,", ",").replaceFirst("^,", ""));
		finalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
		finalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
		finalAuditTrackDTO.setTo_datetime(pdate);
		finalAuditTrackDTO.setApp_user_type(forwardBydata.get("check"));
		finalAuditTrackDTO.setApp_user_path(forwardBydata.get("filePath"));
		finalAuditTrackDTO.setApp_ca_type(forwardBydata.get(""));
		finalAuditTrackDTO.setApp_ca_path("");
		finalAuditTrackDTO.setOn_hold("n");
		LdapFinalAuditTrackDTO i = finalAuditTrackRepository.save(finalAuditTrackDTO);
		if (i.getTrack_id() > 0) {
			j = 1;
		}
		log.info("Inside insertIntoFinalAuditTrack method at " + new Date() + " j:::" + j);
		return j;
	}

	public int updateBaseTbl(Map<String, String> forwardBydata, Map<String, String> forwardToData) {
		log.info("Inside updateBaseTbl method at " + new Date());
		int i = 0, j = 0;
		Date date = new Date();
		DateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String pdate = dt.format(date);
		if (forwardBydata.get("role").equals(Constants.ROLE_USER)) {
			LdapBaseDTO ldapBaseDTO = ldapBaseRepository.findByRegistrationno(forwardBydata.get("regNumber"));
			ldapBaseDTO.setPdf_path(forwardBydata.get("filePath"));
			LdapBaseDTO id = ldapBaseRepository.save(ldapBaseDTO);
			if (id.getId() > 0) {
				j = 1;
			}
			log.info("Inside updateBaseTbl method at " + new Date() + "  j:::" + j);
		} else if (forwardBydata.get("role").equals(Constants.ROLE_CA)) {
			if (forwardBydata.get("check").equalsIgnoreCase("upload_scanned")) {
				LdapBaseDTO ldapBaseDTO = ldapBaseRepository.findByRegistrationno(forwardBydata.get("regNumber"));
				ldapBaseDTO.setCa_sign_cert(forwardBydata.get("filePath"));
				ldapBaseDTO.setCa_rename_sign_cert(forwardBydata.get("renameFilePath"));
				LdapBaseDTO id = ldapBaseRepository.save(ldapBaseDTO);
				if (id.getId() > 0) {
					i = 1;
				}
				log.info("Inside updateBaseTbl method at " + new Date() + "  i:::" + i);
			}
		}
		return i;
	}

	public int insertIntoStatusTable(Map<String, String> forwardBydata, Map<String, String> forwardToData) {
		log.info("Inside insertIntoStatusTable method at " + new Date());
		int j = 0;
		SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		Date dt = new Date();
		String datetime = format.format(dt);
		String ip = "0.0.0.0.0.0.0.1";
		LdapStatusDTO statusDTO = new LdapStatusDTO();
		try {
			BeanUtils.populate(statusDTO, forwardBydata);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		statusDTO.setStat_form_type(forwardBydata.get("formType"));
		statusDTO.setStatregno(forwardBydata.get("regNumber"));
		statusDTO.setStat_type(forwardToData.get("status"));
		statusDTO.setStatforwardedby(forwardBydata.get("forwardedBy"));
		statusDTO.setStat_forwarded_by_user(forwardBydata.get("forwardedByEmail"));
		statusDTO.setStatforwardedto(forwardToData.get("forwardedTo"));
		statusDTO.setStat_forwarded_to_user(forwardToData.get("forwardedToEmail"));
		statusDTO.setStat_remarks(forwardToData.get("remarks"));
		statusDTO.setStat_ip(ip);
		statusDTO.setStat_forwarded_by_email(forwardBydata.get("forwardedByEmail"));
		statusDTO.setStat_forwarded_by_mobile(forwardBydata.get("forwardedByMobile"));
		statusDTO.setStat_forwarded_by_name(forwardBydata.get("forwardedByName"));
		statusDTO.setStat_forwarded_by_ip(ip);
		statusDTO.setStat_forwarded_by_datetime(datetime);
		statusDTO.setStat_final_id("");
		statusDTO.setStat_on_hold("n");
		LdapStatusDTO i = statusRepository.save(statusDTO);
		if (i.getStatid() > 0) {
			j = 1;
		}
		log.info("Inside insertIntoStatusTable method at " + new Date() + "  j:::" + j);
		return j;
	}
	// *******************End of submission********************

	// *******************start of Preview(after submission)********************//
	public boolean checkStatusForEdit(String registrationNumber, String email, String role, String filter,
			Set<String> s) {
		log.info("Inside checkStatusForEdit method at " + new Date() + " filter:::" + filter + "  role:::" + role);
		boolean isValid = false;
		if (filter.equals("toEdit")) {
			switch (role) {
			case Constants.ROLE_USER:
				for (String emailAddress1 : s) {
					int i = finalAuditTrackRepository
							.countByregistrationnoAndApplicantemailAndStatusOrStatusOrStatusOrStatus(registrationNumber,
									emailAddress1, "ca_pending", "manual_upload", "api", "domainapi");
					log.info("Inside checkStatusForEdit method at " + new Date() + " i:::" + i);
					if (i > 0) {
						isValid = true;
					}
				}
				break;
			case Constants.ROLE_CA:
				for (String emailAddress1 : s) {
					int i = finalAuditTrackRepository.countByregistrationnoAndCaemailAndStatusOrStatusAndToemail(
							registrationNumber, emailAddress1, "coordinator_pending", "ca_pending", emailAddress1);
					log.info("Inside checkStatusForEdit method at " + new Date() + " i:::" + i);
					if (i > 0) {
						isValid = true;
					}
				}
				break;
			case Constants.ROLE_CO:
				for (String emailAddress1 : s) {
					int i = finalAuditTrackRepository
							.countByregistrationnoAndCoordinatoremailAndStatusOrStatusAndToemail(registrationNumber,
									"%" + emailAddress1 + "%", "mail-admin_pending", "coordinator_pending",
									emailAddress1);
					log.info("Inside checkStatusForEdit method at " + new Date() + " i:::" + i);
					if (i > 0) {
						isValid = true;
					}
				}
				break;
			case Constants.ROLE_SUP:
				for (String emailAddress1 : s) {
					int i = finalAuditTrackRepository.countByregistrationnoAndSupportemailAndStatusOrStatus(
							registrationNumber, "%" + emailAddress1 + "%", "coordinator_pending", "support_pending");
					log.info("Inside checkStatusForEdit method at " + new Date() + " i:::" + i);
					if (i > 0) {
						isValid = true;
					}
				}
				break;
			case Constants.ROLE_MAILADMIN:
				int i = finalAuditTrackRepository.countByregistrationnoAndToemailAndStatus(registrationNumber, email,
						"mail-admin_pending");
				log.info("Inside checkStatusForEdit method at " + new Date() + " i:::" + i);
				if (i > 0) {
					isValid = true;
				}
				break;
			default:
			}

		}
		log.info("Inside checkStatusForEdit method at " + new Date() + "  isValid:::" + isValid);
		return isValid;
	}

	public boolean checkStatusForPreview(String registrationNumber, String email, String role, String filter,
			Set<String> s) {
		log.info("Inside checkStatusForPreview method at " + new Date() + "  role:::" + role);
		boolean isValid = false;
		if (!(role.equals("admin") || role.equals("sup"))) {
			for (String emailAddress : s) {
				int i = finalAuditTrackRepository
						.countByregistrationnoAndToemailLikeOrApplicantemailOrCaemailLikeOrCoordinatoremailLike(
								registrationNumber, emailAddress, emailAddress, emailAddress, emailAddress);
				log.info("Inside checkStatusForPreview method at " + new Date() + "  i:::" + i);
				if (i > 0) {
					isValid = true;
				}
			}
		}
		return isValid;
	}

	public Map<String, Object> preview(String regno) {
		log.info("Inside preview method at " + new Date() + "  regno:::" + regno);
		Map<String, Object> previewlist = null;
		LdapBaseDTO previewBaseDTO = ldapBaseRepository.findByRegistrationno(regno);
		log.info("Inside preview method at " + new Date() + "  previewBaseDTO:::" + previewBaseDTO);
		ObjectMapper m = new ObjectMapper();
		previewlist = m.convertValue(previewBaseDTO, Map.class);
		log.info("Inside preview method at " + new Date() + "  previewlist:::" + previewlist);
		return previewlist;
	}

	public Map<String, Object> EditPreview(String regno) {
		log.info("Inside EditPreview method at " + new Date() + "  regno:::" + regno);
		Map<String, Object> previewlist = preview(regno);
		log.info("Inside EditPreview method at " + new Date() + "  previewlist:::" + previewlist);
		return previewlist;
	}

	public boolean UpdatePreviewDetails(LdapFormBean ldapFormBean) {
		log.info("Inside UpdatePreviewDetails method at " + new Date());
		boolean status = false;
		LdapBaseDTO previewBaseDTO = ldapBaseRepository.findByRegistrationno(ldapFormBean.getRegistrationno());
		log.info("Inside UpdatePreviewDetails method at " + new Date() + " previewBaseDTO:::" + previewBaseDTO);
		try {
			BeanUtils.copyProperties(previewBaseDTO, ldapFormBean);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		LdapBaseDTO pbt = ldapBaseRepository.save(previewBaseDTO);
		log.info("Inside UpdatePreviewDetails method at " + new Date() + " pbt:::" + pbt);
		if (pbt.getId() > 0) {
			status = true;
		}
		return status;
	}
	// *******************End of Preview(after submission)********************//

	// *******************Start of forward/approve*********************************
	public boolean validateRefNo(String refNo) {
		int i = ldapBaseRepository.countByregistrationno(refNo);
		log.info("Inside validateRefNo method at " + new Date() + "  i:::" + i);
		if (i > 0) {
			return false;
		} else {
			return true;
		}
	}

	public Map<String, String> fetchFormDetail(String regNo) {
		log.info("Inside fetchFormDetail method at " + new Date());
		Map<String, String> mapbeanMap = null;
		LdapBaseDTO fetchFormDetail = ldapBaseRepository.findByRegistrationno(regNo);
		log.info("Inside fetchFormDetail method at " + new Date() + "  fetchFormDetail:::" + fetchFormDetail);
		ObjectMapper m = new ObjectMapper();
		mapbeanMap = m.convertValue(fetchFormDetail, Map.class);
		log.info("Inside fetchFormDetail method at " + new Date() + "  mapbeanMap:::" + mapbeanMap);
		return mapbeanMap;
	}

	public String fetchToEmail() {
		log.info("Inside fetchToEmail method at " + new Date());
		StringBuilder daEmails = new StringBuilder();
		ArrayList<String> arr = new ArrayList<>();
		// List<String> arr = null;
		String email = "";
		List<LdapMailadminFormsDTO> arr1 = null;
		String prefix = "";
		try {
			// arr = forwardMailadminFormsRepository.findDistinctByMldap('y');
			arr1 = mailadminFormsRepository.findDistinctByMldap("y");

			for (LdapMailadminFormsDTO mailadminFormsDTO : arr1) {
				email = mailadminFormsDTO.getMemail();
				arr.add(email);
			}
			System.out.println("***********************arr**********************************************" + arr);
			log.info("Inside fetchToEmail method at " + new Date() + "  daEmails:::" + daEmails.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return daEmails.toString();
	}

	public Map<String, String> fetchHimachalCoord(String dept) {
		log.info("Inside fetchHimachalCoord method at " + new Date());
		Map<String, String> coords = new HashMap<>();
		List<LdapEmpCoordDTO> coords1 = null;
		coords1 = empCoordRepository.findByEmpcoordemailAndEmpadminemailAndEmpdept(Constants.HIMACHAL_DA_ADMIN,
				Constants.HIMACHAL_DA_ADMIN, dept);
		for (LdapEmpCoordDTO a : coords1) {
			coords.put("emp_coord_email", a.getEmpcoordemail());
			coords.put("emp_coord_name", a.getEmpcoordname());
		}
		log.info("Inside fetchHimachalCoord method at " + new Date() + "  coords:::" + coords);
		// coords.get("emp_coord_email");
		// coords.get("emp_coord_name");
		return coords;
	}

	public String convertSetToString(Set<String> co) {
		log.info("Inside convertSetToString method at " + new Date());
		String string = String.join(", ", co);
		return (string.length() != 0) ? string.toString() : "";
	}

	public Set<String> isRecipientAdmin(String email) {
		log.info("Inside isRecipientAdmin method at " + new Date());
		return mailadminFormsRepository.findFirstDistinctByMemailAndMldap(email, 'y');
	}

	public Set<String> isRecipientCoordinator(String email, Set<String> fetchAliases) {
		Set<String> arr = new HashSet<>();
		Set<String> aliases = null;
		String[] coords;
		if (email.contains(",")) {
			coords = email.split(",");
		} else {
			coords = new String[1];
			coords[0] = email;
		}
		for (String coord : coords) {
			boolean validatetoEmails = apiUtility.validateEmailApi(coord);
			if (validatetoEmails) {
				aliases = fetchAliases;
			} else {
				String[] aliasesInArray = { coord };
				aliases = new HashSet<>(Arrays.asList(aliasesInArray));
			}
			for (String aliase : aliases) {
				LdapEmpCoordDTO empCoordDTO = empCoordRepository.findFirstByEmpcoordemail(email);
				String emp_coord_email = empCoordDTO.getEmpcoordemail();
				arr.add(emp_coord_email);
			}
		}

		log.info("Inside isRecipientCoordinator method at " + new Date());
		return arr;
	}

	public int insertIntoAppType(Map<String, String> forwardBydata, Map<String, String> forwardToData) {
		log.info("Inside insertIntoAppType method at " + new Date());
		int i = 0, j = 0;
		if (forwardBydata.get("check").equalsIgnoreCase("upload_scanned")) {
			LdapBaseDTO forwardBaseDTO = ldapBaseRepository.findByRegistrationno(forwardBydata.get("regNumber"));
			forwardBaseDTO.setSign_cert(forwardBydata.get("filePath"));
			forwardBaseDTO.setRename_sign_cert(forwardBydata.get("renameFilePath"));
			LdapBaseDTO id = ldapBaseRepository.save(forwardBaseDTO);
			if (id.getId() > 0) {
				i = 1;
			}
			log.info("Inside insertIntoAppType method at " + new Date() + " i:::" + i);
		} else {
			LdapBaseDTO forwardBaseDTO = ldapBaseRepository.findByRegistrationno(forwardBydata.get("regNumber"));
			forwardBaseDTO.setPdf_path(forwardBydata.get("filePath"));
			LdapBaseDTO id = ldapBaseRepository.save(forwardBaseDTO);
			if (id.getId() > 0) {
				i = 1;
			}
			log.info("Inside insertIntoAppType method at " + new Date() + " i:::" + i);
		}
		return i;
	}

	public int updateFinalAuditTrack(Map<String, String> forwardBydata, Map<String, String> forwardToData) {
		log.info("Inside updateFinalAuditTrack method at " + new Date());
		int i = 0, j = 0, k = 0;
		SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		Date dt = new Date();
		String datetime = format.format(dt);
		String role = forwardBydata.get("role");
		if (forwardBydata.get("check").equalsIgnoreCase("upload_scanned")) {
			String fwdToEmail = forwardToData.get("forwardedToEmail").replace(",,", ",").replaceFirst("^,", "");
			if (role.equalsIgnoreCase(Constants.ROLE_USER)) {
				LdapFinalAuditTrackDTO finalAuditTrackDTO = finalAuditTrackRepository
						.findByRegistrationno(forwardBydata.get("regNumber"));
				finalAuditTrackDTO.setStatus(forwardToData.get("status"));
				finalAuditTrackDTO.setToemail(fwdToEmail);
				finalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
				finalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
				finalAuditTrackDTO.setTo_datetime(datetime);
				finalAuditTrackDTO.setSign_cert(forwardBydata.get("filePath"));
				finalAuditTrackDTO.setRename_sign_cert(forwardBydata.get("renameFilePath"));
				finalAuditTrackDTO.setApp_ca_type(forwardBydata.get("check"));
				LdapFinalAuditTrackDTO id = finalAuditTrackRepository.save(finalAuditTrackDTO);
				if (id.getTrack_id() > 0) {
					j = 1;
				}
				log.info("Inside updateFinalAuditTrack method at " + new Date() + " j:::" + j);
			} else {
				LdapFinalAuditTrackDTO finalAuditTrackDTO = finalAuditTrackRepository
						.findByRegistrationno(forwardBydata.get("regNumber"));
				finalAuditTrackDTO.setStatus(forwardToData.get("status"));
				finalAuditTrackDTO.setToemail(fwdToEmail);
				finalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
				finalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
				finalAuditTrackDTO.setTo_datetime(datetime);
				finalAuditTrackDTO.setCa_sign_cert(forwardBydata.get("filePath"));
				finalAuditTrackDTO.setCa_rename_sign_cert(forwardBydata.get("renameFilePath"));
				finalAuditTrackDTO.setApp_ca_type(forwardBydata.get("check"));

				LdapFinalAuditTrackDTO id = finalAuditTrackRepository.save(finalAuditTrackDTO);
				if (id.getTrack_id() > 0) {
					j = 1;
				}
				log.info("Inside updateFinalAuditTrack method at " + new Date() + " j:::" + j);
			}
		} else {
			String fwdToEmail = forwardToData.get("forwardedToEmail").replace(",,", ",").replaceFirst("^,", "");
			if (role.equals(Constants.ROLE_CA)) {
				LdapFinalAuditTrackDTO finalAuditTrackDTO = finalAuditTrackRepository
						.findByRegistrationno(forwardBydata.get("regNumber"));
				finalAuditTrackDTO.setCaemail(forwardBydata.get("forwardedByEmail"));
				finalAuditTrackDTO.setCa_mobile(forwardBydata.get("forwardedByMobile"));
				finalAuditTrackDTO.setCa_name(forwardBydata.get("forwardedByName"));
				finalAuditTrackDTO.setCa_ip("0:0:0:0:0:0:0:1");
				finalAuditTrackDTO.setCa_datetime(datetime);
				finalAuditTrackDTO.setCa_remarks(forwardToData.get("remarks"));
				finalAuditTrackDTO.setStatus(forwardToData.get("status"));
				finalAuditTrackDTO.setToemail(fwdToEmail);
				finalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
				finalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
				finalAuditTrackDTO.setApp_ca_type(forwardBydata.get("app_ca_type"));
				finalAuditTrackDTO.setApp_ca_path(forwardBydata.get("app_ca_path"));
				LdapFinalAuditTrackDTO id = finalAuditTrackRepository.save(finalAuditTrackDTO);
				if (id.getTrack_id() > 0) {
					j = 1;
				}
				log.info("Inside updateFinalAuditTrack method at " + new Date() + " j:::" + j);
			} else if (role.equals(Constants.ROLE_US)) {
				LdapFinalAuditTrackDTO finalAuditTrackDTO = finalAuditTrackRepository
						.findByRegistrationno(forwardBydata.get("regNumber"));
				finalAuditTrackDTO.setUs_email(forwardBydata.get("forwardedByEmail"));
				finalAuditTrackDTO.setUs_mobile(forwardBydata.get("forwardedByMobile"));
				finalAuditTrackDTO.setUs_name(forwardBydata.get("forwardedByName"));
				finalAuditTrackDTO.setUs_ip("0:0:0:0:0:0:0:1");
				finalAuditTrackDTO.setUs_datetime(datetime);
				finalAuditTrackDTO.setUs_remarks(forwardToData.get("remarks"));
				finalAuditTrackDTO.setStatus(forwardToData.get("status"));
				finalAuditTrackDTO.setToemail(fwdToEmail);
				finalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
				finalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
				LdapFinalAuditTrackDTO id = finalAuditTrackRepository.save(finalAuditTrackDTO);
				if (id.getTrack_id() > 0) {
					j = 1;
				}
				log.info("Inside updateFinalAuditTrack method at " + new Date() + " j:::" + j);
			} else if (role.equals(Constants.ROLE_CO)) {
				LdapFinalAuditTrackDTO finalAuditTrackDTO = finalAuditTrackRepository
						.findByRegistrationno(forwardBydata.get("regNumber"));
				finalAuditTrackDTO.setCoordinatoremail(forwardBydata.get("forwardedByEmail"));
				finalAuditTrackDTO.setCoordinator_mobile(forwardBydata.get("forwardedByMobile"));
				finalAuditTrackDTO.setCoordinator_name(forwardBydata.get("forwardedByName"));
				finalAuditTrackDTO.setCoordinator_ip("0:0:0:0:0:0:0:1");
				finalAuditTrackDTO.setCoordinator_datetime(datetime);
				finalAuditTrackDTO.setCoordinator_remarks(forwardToData.get("remarks"));
				finalAuditTrackDTO.setStatus(forwardToData.get("status"));
				finalAuditTrackDTO.setToemail(fwdToEmail);
				finalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
				finalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
				LdapFinalAuditTrackDTO id = finalAuditTrackRepository.save(finalAuditTrackDTO);
				if (id.getTrack_id() > 0) {
					j = 1;
				}
				log.info("Inside updateFinalAuditTrack method at " + new Date() + " j:::" + j);
			} else if (role.equals(Constants.ROLE_SUP)) {
				LdapFinalAuditTrackDTO finalAuditTrackDTO = finalAuditTrackRepository
						.findByRegistrationno(forwardBydata.get("regNumber"));
				finalAuditTrackDTO.setSupportemail(forwardBydata.get("forwardedByEmail"));
				finalAuditTrackDTO.setSupport_mobile(forwardBydata.get("forwardedByMobile"));
				finalAuditTrackDTO.setSupport_name(forwardBydata.get("forwardedByName"));
				finalAuditTrackDTO.setSupport_ip("0:0:0:0:0:0:0:1");
				finalAuditTrackDTO.setSupport_datetime(datetime);
				finalAuditTrackDTO.setSupport_remarks(forwardToData.get("remarks"));
				finalAuditTrackDTO.setStatus(forwardToData.get("status"));
				finalAuditTrackDTO.setToemail(fwdToEmail);
				finalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
				finalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
				LdapFinalAuditTrackDTO id = finalAuditTrackRepository.save(finalAuditTrackDTO);
				if (id.getTrack_id() > 0) {
					j = 1;
				}
				log.info("Inside updateFinalAuditTrack method at " + new Date() + " j:::" + j);
			} else if (role.equals(Constants.ROLE_DA)) {
				LdapFinalAuditTrackDTO finalAuditTrackDTO = finalAuditTrackRepository
						.findByRegistrationno(forwardBydata.get("regNumber"));
				finalAuditTrackDTO.setDaemail(forwardBydata.get("forwardedByEmail"));
				finalAuditTrackDTO.setDa_mobile(forwardBydata.get("forwardedByMobile"));
				finalAuditTrackDTO.setDa_name(forwardBydata.get("forwardedByName"));
				finalAuditTrackDTO.setDa_ip("0:0:0:0:0:0:0:1");
				finalAuditTrackDTO.setDa_datetime(datetime);
				finalAuditTrackDTO.setDa_remarks(forwardToData.get("remarks"));
				finalAuditTrackDTO.setStatus(forwardToData.get("status"));
				finalAuditTrackDTO.setToemail(fwdToEmail);
				finalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
				finalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
				LdapFinalAuditTrackDTO id = finalAuditTrackRepository.save(finalAuditTrackDTO);
				if (id.getTrack_id() > 0) {
					j = 1;
				}
				log.info("Inside updateFinalAuditTrack method at " + new Date() + " j:::" + j);
			} else if (role.equals(Constants.ROLE_MAILADMIN)) {
				LdapFinalAuditTrackDTO finalAuditTrackDTO = finalAuditTrackRepository
						.findByRegistrationno(forwardBydata.get("regNumber"));
				finalAuditTrackDTO.setAdminemail(forwardBydata.get("forwardedByEmail"));
				finalAuditTrackDTO.setAdmin_mobile(forwardBydata.get("forwardedByMobile"));
				finalAuditTrackDTO.setAdmin_name(forwardBydata.get("forwardedByName"));
				finalAuditTrackDTO.setAdmin_ip("0:0:0:0:0:0:0:1");
				finalAuditTrackDTO.setAdmin_datetime(datetime);
				finalAuditTrackDTO.setAdmin_remarks(forwardToData.get("remarks"));
				finalAuditTrackDTO.setStatus(forwardToData.get("status"));
				finalAuditTrackDTO.setToemail(fwdToEmail);
				finalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
				finalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
				LdapFinalAuditTrackDTO id = finalAuditTrackRepository.save(finalAuditTrackDTO);
				if (id.getTrack_id() > 0) {
					j = 1;
				}
				log.info("Inside updateFinalAuditTrack method at " + new Date() + " j:::" + j);
			}
		}
		return j;
	}

	public int updateAppType(Map<String, String> forwardBydata, Map<String, String> forwardToData) {
		log.info("Inside updateAppType method at " + new Date());
		int i = 0, j = 0;
		Date date = new Date();
		DateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String pdate = dt.format(date);
		log.info("Inside updateAppType method at " + new Date() + " role:::" + forwardBydata.get("role"));
		if (forwardBydata.get("role").equals(Constants.ROLE_USER)) {
			LdapBaseDTO forwardBaseDTO = ldapBaseRepository.findByRegistrationno(forwardBydata.get("regNumber"));
			log.info("Inside updateAppType method at " + new Date() + " forwardBaseDTO:::" + forwardBaseDTO);
			forwardBaseDTO.setPdf_path(forwardBydata.get("filePath"));
			LdapBaseDTO id = ldapBaseRepository.save(forwardBaseDTO);
			if (id.getId() > 0) {
				i = 1;
			}
			log.info("Inside updateAppType method at " + new Date() + " i:::" + i);
		} else if (forwardBydata.get("role").equals(Constants.ROLE_CA)) {
			if (forwardBydata.get("check").equalsIgnoreCase("upload_scanned")) {
				LdapBaseDTO ldapBaseDTO = ldapBaseRepository.findByRegistrationno(forwardBydata.get("regNumber"));
				ldapBaseDTO.setCa_sign_cert(forwardBydata.get("filePath"));
				ldapBaseDTO.setCa_rename_sign_cert(forwardBydata.get("renameFilePath"));
				LdapBaseDTO id = ldapBaseRepository.save(ldapBaseDTO);
				if (id.getId() > 0) {
					i = 1;
				}
				log.info("Inside updateAppType method at " + new Date() + " i:::" + i);
			}
		}
		return i;
	}

	public int updateStatusTable(Map<String, String> forwardBydata, Map<String, String> forwardToData) {
		log.info("Inside updateStatusTable method at " + new Date());
		int j = 0;
		SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		Date dt = new Date();
		String datetime = format.format(dt);
		String ip = "0.0.0.0.0.0.0.1";
		LdapStatusDTO statusDTO = new LdapStatusDTO();
		try {
			BeanUtils.populate(statusDTO, forwardBydata);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		statusDTO.setStat_form_type(forwardBydata.get("formType"));
		statusDTO.setStatregno(forwardBydata.get("regNumber"));
		statusDTO.setStat_type(forwardToData.get("status"));
		statusDTO.setStatforwardedby(forwardBydata.get("forwardedBy"));
		statusDTO.setStat_forwarded_by_user(forwardBydata.get("forwardedByEmail"));
		statusDTO.setStatforwardedto(forwardToData.get("forwardedTo"));
		statusDTO.setStat_forwarded_to_user(forwardToData.get("forwardedToEmail"));
		statusDTO.setStat_remarks(forwardToData.get("remarks"));
		statusDTO.setStat_ip(ip);
		statusDTO.setStat_forwarded_by_email(forwardBydata.get("forwardedByEmail"));
		statusDTO.setStat_forwarded_by_mobile(forwardBydata.get("forwardedByMobile"));
		statusDTO.setStat_forwarded_by_name(forwardBydata.get("forwardedByName"));
		statusDTO.setStat_forwarded_by_ip(ip);
		statusDTO.setStat_forwarded_by_datetime(datetime);
		statusDTO.setStat_final_id("");
		statusDTO.setStat_on_hold("n");
		LdapStatusDTO i = statusRepository.save(statusDTO);
		if (i.getStatid() > 0) {
			j = 1;
		}
		log.info("Inside updateStatusTable method at " + new Date() + "  j:::" + j);
		return j;
	}

	public String fetchPunjabNodalOfficers(String district) {
		log.info("Inside fetchPunjabNodalOfficers method at " + new Date());
		String co_email = "", email = "";
		// email = forwardBaseRepository.fetchPunjabNodalOfficers(district);
		if (co_email.isEmpty()) {
			co_email = email;
		} else {
			co_email += "," + email;
		}
		log.info("Inside fetchPunjabNodalOfficers method at " + new Date() + "  co_email:::" + co_email);
		return co_email;
	}

	public String fetchPunjabDA(String employment, String ministry, String department) {
		log.info("Inside fetchPunjabDA method at " + new Date());
		String da_email = "";
		da_email = fetchPunjabDA(employment, ministry, department);
		if (!da_email.contains(",")) {
			da_email = da_email;
		} else {
			da_email += "," + da_email;
		}
		if (da_email.isEmpty()) {
			da_email = fetchPunjabDA(employment, ministry, "Government of Punjab");
			if (!da_email.contains(",")) {
				da_email = da_email;
			} else {
				da_email += "," + da_email;
			}
		}
		log.info("Inside fetchPunjabDA method at " + new Date() + " da_email:::" + da_email);
		return da_email;
	}

	public static Set<String> convertStringToSet(String str) {
		log.info("Inside convertStringToSet method at " + new Date());
		Set<String> elements = new HashSet<>();
		if (!str.isEmpty()) {
			String[] arr = null;
			arr = str.split(",");
			for (int i = 0; i < arr.length; i++) {
				elements.add(arr[i]);
			}
		}
		log.info("Inside convertStringToSet method at " + new Date() + " elements:::" + elements);
		return elements;
	}

	public Map<String, String> fetchEmailDetails(String employment, String ministry, String department) {
		log.info("Inside fetchEmailDetails method at " + new Date());
		Map<String, String> map = null;
		LdapEmpCoordDTO beanlist = empCoordRepository
				.findByEmpcategoryAndEmpminstateorgAndEmpdeptAndEmpstatus(employment, ministry, department, "a");
		ObjectMapper m = new ObjectMapper();
		map = m.convertValue(beanlist, Map.class);
		log.info("Inside fetchEmailDetails method at " + new Date() + " map:::" + map);
		return map;
	}

	public Set<String> removeSupportFromSet(Set<String> coordinators) {
		log.info("Inside removeSupportFromSet method at " + new Date());
		Set<String> coords = new HashSet<>();
		for (String coord : coordinators) {
			if (!verifySupport(coord)) {
				coords.add(coord);
			}
		}
		log.info("Inside removeSupportFromSet method at " + new Date() + " coords:::" + coords);
		return coords;
	}

	public boolean verifySupport(String coordinators) {
		log.info("Inside verifySupport method at " + new Date() + " coordinators:::" + coordinators);
		if (coordinators.contains("support@nic.in") || coordinators.contains("support@gov.in")
				|| coordinators.contains("support@dummy.nic.in") || coordinators.contains("vpnsupport@nic.in")
				|| coordinators.contains("smssupport@gov.in")) {
			return true;
		}
		return false;
	}

	public Map<String, String> fetchEmpDetails(String regNo) {
		log.info("Inside fetchEmpDetails method at " + new Date());
		Map<String, String> maplist = null;

		LdapBaseDTO forwardBaseDTO = ldapBaseRepository.findByRegistrationno(regNo);
		ObjectMapper m = new ObjectMapper();
		maplist = m.convertValue(forwardBaseDTO, Map.class);
		log.info("Inside fetchEmpDetails method at " + new Date() + " maplist:::" + maplist);
		return maplist;
	}

	public Set<String> isRecipientDa(String email, Set<String> fetchAliases) {
		Set<String> arr = new HashSet<>();
		Set<String> aliases = null;
		String[] coords;
		if (email.contains(",")) {
			coords = email.split(",");
		} else {
			coords = new String[1];
			coords[0] = email;
		}
		for (String coord : coords) {
			boolean validatetoEmails = apiUtility.validateEmailApi(coord);
			if (validatetoEmails) {
				aliases = fetchAliases;
			} else {
				String[] aliasesInArray = { coord };
				aliases = new HashSet<>(Arrays.asList(aliasesInArray));
			}
			for (String aliase : aliases) {
				LdapEmpCoordDTO forwardEmpCoordDTO = empCoordRepository.findFirstByEmpcoordemailAndEmpadminemail(aliase,
						aliase);
				String emp_coord_email = forwardEmpCoordDTO.getEmpcoordemail();
				arr.add(emp_coord_email);
			}
		}

		return arr;
	}
	// *******************End of forward/approve*********************************

	// *******************Start of Track User************************************
	public HashMap<String, Object> fetchTrackDetails(String registration_no) {
		log.info("Inside fetchTrackDetails method at " + new Date());
		HashMap<String, Object> hmTrack = new HashMap<>();
		List<LdapStatusDTO> arrRoles = statusListRepository.findByStatregno(registration_no);
		List<LdapFinalAuditTrackDTO> currentDetails = finalAuditTrackListRepository
				.findByRegistrationno(registration_no);
		hmTrack.put("arrRoles", arrRoles);
		hmTrack.put("currentDetails", currentDetails);
		log.info("Inside fetchTrackDetails method at " + new Date() + "  hmTrack:::" + hmTrack);
		return hmTrack;
	}

	public Map<String, Object> fetchTrackByRole(String registration_no, String forward, String trole, String srole) {
		log.info("Inside fetchTrackByRole method at " + new Date());
		Map<String, Object> trackData = new HashMap<>();
		Map<String, String> fetchstatus = new HashMap<>();
		List<LdapStatusDTO> iterable = null;
		String recv_date = "";

		log.info("Inside fetchTrackByRole method at " + new Date() + " registration_no:::" + registration_no
				+ " forward:::" + forward + " trole:::" + trole + " srole:::" + srole);

		if (trole.equalsIgnoreCase("undefined")) {
			iterable = statusRepository.findFirstByStatregnoAndStatforwardedtoOrderByStatid(registration_no, trole);
		} else if (srole.equalsIgnoreCase("null") && trole.equalsIgnoreCase("null")) {
			iterable = statusRepository
					.findByStatregnoAndStatforwardedbyIsNullAndStatforwardedtoIsNullOrderByStatid(registration_no);
		} else if (trole.equalsIgnoreCase("null")) {
			iterable = statusRepository
					.findByStatregnoAndStatforwardedbyAndStatforwardedtoIsNullOrderByStatid(registration_no, srole);
		} else {
			iterable = statusRepository.findByStatregnoAndStatforwardedbyAndStatforwardedto(registration_no, srole,
					trole);
		}
		log.info("Inside fetchTrackByRole method at " + new Date() + " iterable:::" + iterable);

		for (LdapStatusDTO trackStatus : iterable) {
			String remarks = trackStatus.getStat_remarks();
			if (remarks == null) {
				remarks = "";
			}
			recv_date = trackStatus.getStat_createdon();
			String current_user = trackStatus.getStat_forwarded_by_email();
			if (current_user == null) {
				current_user = "User Emaillll";
				// current_user = userdata.getEmail();
			}
			String forwarder = commonUtility.findRole(forward);
			String sender_details = forwarder + "&ensp;(" + current_user + ")";
			if (!forward.isEmpty()) {
				fetchstatus = (Map<String, String>) statusRepository
						.findByStatregnoAndStatforwardedbyOrStatforwardedto(registration_no, forward, srole);
				log.info("Inside fetchTrackByRole method at " + new Date() + " fetchstatus:::" + fetchstatus);

//				for(TrackStatusDTO trackStatusDTO:list) {
//					fetchstatus.put("recv_date", trackStatusDTO.get);
//					fetchstatus.put("stat_forwarded_by_email", value);
//				}
				recv_date = fetchstatus.get("recv_date");
				String stat_forwarded_by_email = fetchstatus.get("stat_forwarded_by_email");
				sender_details = forwarder + "&ensp;(" + stat_forwarded_by_email + ")";
			}
			trackData.put("remarks", remarks);
			trackData.put("recv_date", recv_date);
			trackData.put("current_user", current_user);
			trackData.put("recv_email", trackStatus.getStat_forwarded_to_user());
			trackData.put("stat_process", trackStatus.getStat_process());
			trackData.put("stat_type", trackStatus.getStat_type());
			trackData.put("stat_on_hold", trackStatus.getStat_on_hold());
			trackData.put("recv_date", recv_date);
			trackData.put("sender_details", sender_details);
		}
		log.info("Inside fetchTrackByRole method at " + new Date() + " trackData:::" + trackData);
		return trackData;
	}
	// *******************End of Track
	// User******************************************

	// *******************start of Raise Query***********************************
	public Map<String, Object> raiseQuery(String regNo, String role, String uemail, String choose_recp, String to_email,
			String statRemarks) {
		log.info("Inside raiseQuery method at " + new Date());
		int i = 0;
		Map<String, Object> listmap = null;
		ArrayList<String> roles = new ArrayList<>();
		Map<String, Object> map = fetchRaiseQueryData(regNo, role);

		Set<String> recpdata = (Set<String>) map.get("recpdata");
		for (String string : recpdata) {
			if (string.contains("=>")) {
				String arr[] = string.split("=>");
				roles.add(arr[0]);
			}
		}
		roles.add("u");
		String fetchRole = commonUtility.fetchRole(role);
		log.info("Inside raiseQuery method at " + new Date() + "  fetchRole:::" + fetchRole + "  roles:::" + roles);
		if (roles.contains(choose_recp) && !fetchRole.equals(choose_recp)) {
			String qr_form_type = "ldap";
			String qr_reg_no = regNo;
			String qr_forwarded_by = fetchRole;
			String qr_forwarded_by_user = uemail;
			String qr_forwarded_to = choose_recp;
			String qr_forwarded_to_user = to_email;
			String qr_message = statRemarks;

			LdapQueryRaiseDTO queryRaiseDTO = new LdapQueryRaiseDTO();
			queryRaiseDTO.setQr_form_type(qr_form_type);
			queryRaiseDTO.setQrregno(qr_reg_no);
			queryRaiseDTO.setQr_forwarded_by(qr_forwarded_by);
			queryRaiseDTO.setQr_forwarded_by_user(qr_forwarded_by_user);
			queryRaiseDTO.setQr_forwarded_to(qr_forwarded_to);
			queryRaiseDTO.setQr_forwarded_to_user(qr_forwarded_to_user);
			queryRaiseDTO.setQr_message(qr_message);
			LdapQueryRaiseDTO id = queryRaiseRepository.save(queryRaiseDTO);
			if (id.getQr_id() > 0) {
				listmap = fetchRaiseQueryData(qr_reg_no, role);
				i = 1;
			}
			log.info("Inside raiseQuery method at " + new Date() + "  i:::" + i + "  listmap:::" + listmap);
		}
		return listmap;
	}

	Set<String> fetchAllStakeHolders(String regNo, String role) {
		log.info("Inside fetchAllStakeHolders method at " + new Date());
		Set<String> recpdata = new HashSet<>();
		Map<String, String> fetchMap = null;
		List<LdapStatusDTO> statusDTO = statusListRepository.findByStatregno(regNo);
		ObjectMapper m = new ObjectMapper();
		for (LdapStatusDTO fetchStatus1 : statusDTO) {
			String stat_type = fetchStatus1.getStat_type();
			String stat_forwarded_to_user = fetchStatus1.getStat_forwarded_to_user();
			recpdata.add("u");
			log.info("Inside fetchAllStakeHolders method at " + new Date() + "  stat_type:::" + stat_type);
			switch (stat_type) {
			case "ca_pending":
				if (!role.equals(Constants.ROLE_CA)) {
					recpdata.add("ca=>" + stat_forwarded_to_user);
				}
				break;
			case "support_pending":
				if (!role.equals(Constants.ROLE_SUP)) {
					recpdata.add("s=>" + stat_forwarded_to_user);
				}
				break;
			case "co_pending":
				if (!role.equals(Constants.ROLE_CO)) {
					recpdata.add("c=>" + stat_forwarded_to_user);
				}
				break;
			case "da_pending":
				if (!role.equals(Constants.ROLE_DA)) {
					recpdata.add("d=>" + stat_forwarded_to_user);
				}
				break;
			case "mail-admin_pending":
				if (!role.equals(Constants.ROLE_MAILADMIN)) {
					recpdata.add("m=>" + stat_forwarded_to_user);
				}
				break;
			}
			log.info("Inside fetchAllStakeHolders method at " + new Date() + "  recpdata:::" + recpdata);
		}
		return recpdata;

	}

	public Map<String, Object> fetchRaiseQueryData(String regNo, String role) {
		log.info("Inside fetchRaiseQueryData method at " + new Date());
		Map<String, Object> details = new HashMap<>();
		Set<String> recpdata = fetchAllStakeHolders(regNo, role);
		List<String> fetchMap = null;
		List<LdapQueryRaiseDTO> queryRaiseDTO = queryRaiseRepository.findByQrregno(regNo);

		log.info("Inside fetchRaiseQueryData method at " + new Date() + "  recpdata:::" + recpdata);
		log.info("Inside fetchRaiseQueryData method at " + new Date() + "  queryRaiseDTO:::" + queryRaiseDTO);

		details.put("raiseQueryBtn", true);
		details.put("recpdata", recpdata);
		details.put("querydata", queryRaiseDTO);

//			in case of fetchMap
//			 if (qr_forwarded_by_user.equals(uemail)) {
//	             account_holder = true;
//	         }
		return details;
	}
	// *******************End of Raise Query*************************************

	// ******************start of Reject****************************************
	public boolean updateTrackTable(Map<String, String> forwardBydata, Map<String, String> forwardToData) {
		log.info("Inside updateTrackTable method at " + new Date());
		boolean retStatus = false;
		SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		Date dt = new Date();
		String datetime = format.format(dt);
		String role = forwardBydata.get("role");
		String fwdToEmail = forwardToData.get("forwardedToEmail").replace(",,", ",").replaceFirst("^,", "");
		log.info("Inside updateTrackTable method at " + new Date() + "  role:::" + role);
		int i = 0, j = 0, k = 0;
		if (role.equals(Constants.ROLE_USER)) {

			LdapFinalAuditTrackDTO finalAuditTrackDTO = finalAuditTrackRepository
					.findByRegistrationno(forwardBydata.get("regNumber"));
			finalAuditTrackDTO.setApplicantemail(forwardBydata.get("forwardedByEmail"));
			finalAuditTrackDTO.setApplicant_mobile(forwardBydata.get("forwardedByMobile"));
			finalAuditTrackDTO.setApplicant_name(forwardBydata.get("forwardedByName"));
			finalAuditTrackDTO.setApplicant_ip("0:0:0:0:0:0:0:1");
			finalAuditTrackDTO.setApplicant_datetime(datetime);
			finalAuditTrackDTO.setApplicant_remarks(forwardToData.get("remarks"));
			finalAuditTrackDTO.setStatus(forwardToData.get("status"));
			finalAuditTrackDTO.setToemail(fwdToEmail);
			finalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
			finalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
			LdapFinalAuditTrackDTO id = finalAuditTrackRepository.save(finalAuditTrackDTO);
			if (id.getTrack_id() > 0) {
				j = 1;
			}
			log.info("Inside updateTrackTable method at " + new Date() + "  j:::" + j);
		} else if (role.equals(Constants.ROLE_CA)) {
			LdapFinalAuditTrackDTO finalAuditTrackDTO = finalAuditTrackRepository
					.findByRegistrationno(forwardBydata.get("regNumber"));
			finalAuditTrackDTO.setCaemail(forwardBydata.get("forwardedByEmail"));
			finalAuditTrackDTO.setCa_mobile(forwardBydata.get("forwardedByMobile"));
			finalAuditTrackDTO.setCa_name(forwardBydata.get("forwardedByName"));
			finalAuditTrackDTO.setCa_ip("0:0:0:0:0:0:0:1");
			finalAuditTrackDTO.setCa_datetime(datetime);
			finalAuditTrackDTO.setCa_remarks(forwardToData.get("remarks"));
			finalAuditTrackDTO.setStatus(forwardToData.get("status"));
			finalAuditTrackDTO.setToemail(fwdToEmail);
			finalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
			finalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
			LdapFinalAuditTrackDTO id = finalAuditTrackRepository.save(finalAuditTrackDTO);
			if (id.getTrack_id() > 0) {
				j = 1;
			}
			log.info("Inside updateTrackTable method at " + new Date() + "  j:::" + j);
		} else if (role.equals(Constants.ROLE_US)) {
			LdapFinalAuditTrackDTO finalAuditTrackDTO = finalAuditTrackRepository
					.findByRegistrationno(forwardBydata.get("regNumber"));
			finalAuditTrackDTO.setUs_email(forwardBydata.get("forwardedByEmail"));
			finalAuditTrackDTO.setUs_mobile(forwardBydata.get("forwardedByMobile"));
			finalAuditTrackDTO.setUs_name(forwardBydata.get("forwardedByName"));
			finalAuditTrackDTO.setUs_ip("0:0:0:0:0:0:0:1");
			finalAuditTrackDTO.setUs_datetime(datetime);
			finalAuditTrackDTO.setUs_remarks(forwardToData.get("remarks"));
			finalAuditTrackDTO.setStatus(forwardToData.get("status"));
			finalAuditTrackDTO.setToemail(fwdToEmail);
			finalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
			finalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
			LdapFinalAuditTrackDTO id = finalAuditTrackRepository.save(finalAuditTrackDTO);
			if (id.getTrack_id() > 0) {
				j = 1;
			}
			log.info("Inside updateTrackTable method at " + new Date() + "  j:::" + j);
		} else if (role.equals(Constants.ROLE_CO)) {
			LdapFinalAuditTrackDTO finalAuditTrackDTO = finalAuditTrackRepository
					.findByRegistrationno(forwardBydata.get("regNumber"));
			finalAuditTrackDTO.setCoordinatoremail(forwardBydata.get("forwardedByEmail"));
			finalAuditTrackDTO.setCoordinator_mobile(forwardBydata.get("forwardedByMobile"));
			finalAuditTrackDTO.setCoordinator_name(forwardBydata.get("forwardedByName"));
			finalAuditTrackDTO.setCoordinator_ip("0:0:0:0:0:0:0:1");
			finalAuditTrackDTO.setCoordinator_datetime(datetime);
			finalAuditTrackDTO.setCoordinator_remarks(forwardToData.get("remarks"));
			finalAuditTrackDTO.setStatus(forwardToData.get("status"));
			finalAuditTrackDTO.setToemail(fwdToEmail);
			finalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
			finalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
			LdapFinalAuditTrackDTO id = finalAuditTrackRepository.save(finalAuditTrackDTO);
			if (id.getTrack_id() > 0) {
				j = 1;
			}
			log.info("Inside LdapDao: updateTrackTable method at " + new Date() + "  j:::" + j);
		} else if (role.equals(Constants.ROLE_SUP)) {

			LdapFinalAuditTrackDTO finalAuditTrackDTO = finalAuditTrackRepository
					.findByRegistrationno(forwardBydata.get("regNumber"));
			finalAuditTrackDTO.setSupportemail(forwardBydata.get("forwardedByEmail"));
			finalAuditTrackDTO.setSupport_mobile(forwardBydata.get("forwardedByMobile"));
			finalAuditTrackDTO.setSupport_name(forwardBydata.get("forwardedByName"));
			finalAuditTrackDTO.setSupport_ip("0:0:0:0:0:0:0:1");
			finalAuditTrackDTO.setSupport_datetime(datetime);
			finalAuditTrackDTO.setSupport_remarks(forwardToData.get("remarks"));
			finalAuditTrackDTO.setStatus(forwardToData.get("status"));
			finalAuditTrackDTO.setToemail(fwdToEmail);
			finalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
			finalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
			LdapFinalAuditTrackDTO id = finalAuditTrackRepository.save(finalAuditTrackDTO);
			if (id.getTrack_id() > 0) {
				j = 1;
			}
			log.info("Inside updateTrackTable method at " + new Date() + "  j:::" + j);
		} else if (role.equals(Constants.ROLE_DA)) {

			LdapFinalAuditTrackDTO finalAuditTrackDTO = finalAuditTrackRepository
					.findByRegistrationno(forwardBydata.get("regNumber"));
			finalAuditTrackDTO.setDaemail(forwardBydata.get("forwardedByEmail"));
			finalAuditTrackDTO.setDa_mobile(forwardBydata.get("forwardedByMobile"));
			finalAuditTrackDTO.setDa_name(forwardBydata.get("forwardedByName"));
			finalAuditTrackDTO.setDa_ip("0:0:0:0:0:0:0:1");
			finalAuditTrackDTO.setDa_datetime(datetime);
			finalAuditTrackDTO.setDa_remarks(forwardToData.get("remarks"));
			finalAuditTrackDTO.setStatus(forwardToData.get("status"));
			finalAuditTrackDTO.setToemail(fwdToEmail);
			finalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
			finalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
			LdapFinalAuditTrackDTO id = finalAuditTrackRepository.save(finalAuditTrackDTO);
			if (id.getTrack_id() > 0) {
				j = 1;
			}
			log.info("Inside updateTrackTable method at " + new Date() + "  j:::" + j);
		} else if (role.equals(Constants.ROLE_MAILADMIN)) {
			LdapFinalAuditTrackDTO finalAuditTrackDTO = finalAuditTrackRepository
					.findByRegistrationno(forwardBydata.get("regNumber"));
			finalAuditTrackDTO.setAdminemail(forwardBydata.get("forwardedByEmail"));
			finalAuditTrackDTO.setAdmin_mobile(forwardBydata.get("forwardedByMobile"));
			finalAuditTrackDTO.setAdmin_name(forwardBydata.get("forwardedByName"));
			finalAuditTrackDTO.setAdmin_ip("0:0:0:0:0:0:0:1");
			finalAuditTrackDTO.setAdmin_datetime(datetime);
			finalAuditTrackDTO.setAdmin_remarks(forwardToData.get("remarks"));
			finalAuditTrackDTO.setStatus(forwardToData.get("status"));
			finalAuditTrackDTO.setToemail(fwdToEmail);
			finalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
			finalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
			LdapFinalAuditTrackDTO id = finalAuditTrackRepository.save(finalAuditTrackDTO);
			if (id.getTrack_id() > 0) {
				j = 1;
			}
			log.info("Inside updateTrackTable method at " + new Date() + "  j:::" + j);
		}
		LdapStatusDTO statusDTO = new LdapStatusDTO();
		statusDTO.setStat_forwarded_by_email(forwardBydata.get("forwardedByEmail"));
		statusDTO.setStat_forwarded_by_mobile(forwardBydata.get("forwardedByMobile"));
		statusDTO.setStat_forwarded_by_name(forwardBydata.get("forwardedByName"));
		statusDTO.setStat_ip("0:0:0:0:0:0:0:1");
		statusDTO.setStat_createdon(datetime);
		statusDTO.setStat_remarks(forwardToData.get("remarks"));
		statusDTO.setStat_type(forwardToData.get("status"));
		statusDTO.setStatforwardedto(commonUtility.findRole(role));
		statusDTO.setStat_forwarded_to_user(fwdToEmail);
		statusDTO.setStatregno(forwardBydata.get("regNumber"));
		statusDTO.setStat_on_hold("n");
		LdapStatusDTO id = statusRepository.save(statusDTO);
		if (id.getStatid() > 0) {
			k = 1;
		}
		log.info("Inside updateTrackTable method at " + new Date() + "  k:::" + k);
		if (j > 0) {
			retStatus = true;
		}
		return retStatus;
	}
	// ******************End of Reject******************************************

	// ***********************start of Put on/off Hold**************************
	public boolean putOnHold(String regNo, String role, String on_hold, String statRemarks) {
		log.info("Inside putOnHold method at " + new Date());
		int i = 0, j = 0;
		boolean flag = false;
		LdapStatusDTO statusDTO = statusRepository.findByStatregnoAndStatforwardedto(regNo, role);
		statusDTO.setStat_on_hold(on_hold);
		LdapStatusDTO id1 = statusRepository.save(statusDTO);
		if (id1.getStatid() > 0) {
			i = 1;
		}
		log.info("Inside putOnHold method at " + new Date() + " i:::" + i);
		LdapFinalAuditTrackDTO finalAuditTrackDTO = finalAuditTrackRepository.findByRegistrationno(regNo);
		finalAuditTrackDTO.setOn_hold(on_hold);
		finalAuditTrackDTO.setHold_remarks(statRemarks);
		LdapFinalAuditTrackDTO id2 = finalAuditTrackRepository.save(finalAuditTrackDTO);
		if (id2.getTrack_id() > 0) {
			j = 1;
		}
		log.info("Inside putOnHold method at " + new Date() + " j:::" + j);
		if (i > 0 && j > 0) {
			flag = true;
		}
		return flag;
	}
	// ***********************End of put on/off hold********************************

	// ***********************Start of Upload Docs*********************************
	public LdapDocUploadDTO saveDocx(LdapDocUploadDTO uploadfiles) {
		log.info("Inside saveDocx method at " + new Date());
		LdapDocUploadDTO uploadfilesdto = docUploadRepository.save(uploadfiles);
		log.info("Inside saveDocx method at " + new Date() + " uploadfilesdto:::" + uploadfilesdto);
		return uploadfilesdto;
	}
	// ***********************End of Upload
	// Docs***************************************

	// ********************start of Generate PDF**********************************
	public LdapBaseDTO findByRegno(String regid) {
		log.info("Inside findByRegno method at " + new Date());
		LdapBaseDTO iterable = ldapBaseRepository.findByRegistrationno(regid);
		if (iterable.getId() > 1) {
			return iterable;
		}
		return null;
	}
	// ********************End of Generate PDF***********************************

}
