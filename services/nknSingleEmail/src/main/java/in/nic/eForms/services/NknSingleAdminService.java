package in.nic.eForms.services;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.codec.digest.DigestUtils;
import org.modelmapper.ModelMapper;

import in.nic.eForms.controllers.AdminController;
import in.nic.eForms.entities.GeneratePdf;
import in.nic.eForms.entities.NknSingleBase;
import in.nic.eForms.entities.NknSingleEmpCoord;
import in.nic.eForms.entities.NknSingleSha;
import in.nic.eForms.models.AdminFormBean;
import in.nic.eForms.models.ErrorResponseForOrganizationValidationDto;
import in.nic.eForms.models.FinalAuditTrack;
import in.nic.eForms.models.GeneratePdfBean;
import in.nic.eForms.models.HodDetailsDto;
import in.nic.eForms.models.MobileAndName;
import in.nic.eForms.models.OrganizationBean;
import in.nic.eForms.models.OrganizationDto;
import in.nic.eForms.models.PreviewFormBean;
import in.nic.eForms.models.ProfileDto;
import in.nic.eForms.models.ResponseBean;
import in.nic.eForms.models.Status;
import in.nic.eForms.models.UserForCreate;
import in.nic.eForms.repositories.GeneratePdfRepository;
import in.nic.eForms.repositories.NknSingleBaseRepo;
import in.nic.eForms.repositories.NknSingleEmpCoordRepo;
import in.nic.eForms.repositories.NknSingleShaRepo;
import in.nic.eForms.utils.Constants;
import in.nic.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
@Slf4j
@Service
public class NknSingleAdminService {

	private final NknSingleBaseRepo nknSingleBaseRepo;
	private final NknSingleShaRepo nknSingleShaRepo;
	private final NknSingleEmpCoordRepo nknSingleEmpCoordRepo;
	private final Util utilityService;

	@Autowired
	public NknSingleAdminService(NknSingleBaseRepo nknSingleBaseRepo,
			NknSingleShaRepo nknSingleShaRepo, NknSingleEmpCoordRepo nknSingleEmpCoordRepo, Util utilityService) {
		super();
		this.nknSingleBaseRepo = nknSingleBaseRepo;
		this.utilityService = utilityService;
		this.nknSingleShaRepo = nknSingleShaRepo;
		this.nknSingleEmpCoordRepo = nknSingleEmpCoordRepo;

	}
	
	public NknSingleBase fetchDetails(String regNo) {
		return nknSingleBaseRepo.findByRegistrationNo(regNo);
	}
	
	public Map<String,Object> validate(AdminFormBean adminBean) {
		Map<String,Object> finalmap = new HashMap<>();
		Map<String,Object> map = new HashMap<>();
		
		boolean po_error = utilityService.po(adminBean.getPo());
		boolean bo_error = utilityService.bo(adminBean.getBo());
		boolean domain_error = utilityService.domain(adminBean.getDomain());
		String finalId_error = utilityService.isValid(adminBean.getDomain());
		boolean primaryId_error = utilityService.primaryidValid(adminBean.getPrimaryId());
		
        if (po_error) {
            map.put("po_error", "Enter valid PO");
        }
        if (bo_error) {
        	map.put("bo_error", "Enter valid BO");
        }
        if (domain_error) {
        	map.put("domain_error", "Enter Domain/Group Of People who will access this application,[Alphanumeric,dot(.),comma(,),hyphen(-),slash(/) and whitespaces] allowed");
        }
        if (!primaryId_error) {
        	map.put("primaryId_error", "Please enter valid Primary ID");
        }
        if(!finalId_error.equals("")) {
        	map.put("finalId_error", finalId_error);
        }
		if(map.size() > 1) {
			finalmap.put("errors", map);
		}
		return map;
	}

	public Set<String> getDomain(ProfileDto profile) {
		TreeSet<String> finaldomain = new TreeSet<>();
		Set<NknSingleEmpCoord> fetchdeails = new HashSet<>();
		try {
			if (profile.getEmployment().equalsIgnoreCase("central") || profile.getEmployment().equalsIgnoreCase("ut")) {
				System.out.println(":::::1::::"+profile.getEmployment()+":::::::::::"+profile.getMinistry()+"::::::::::"+profile.getDepartment());
				List<String> temp = utilityService.fetchDomainsByCatAndMinAndDep(profile.getEmployment(),
						profile.getMinistry(), profile.getDepartment());
				for (String string : temp) {
					if (string!=null) {
						finaldomain.add(string);
						System.out.println(":::::1::::"+finaldomain);
					}
				}
			} else if (profile.getEmployment().equalsIgnoreCase("state")) {
				System.out.println(":::::2::::"+profile.getEmployment()+":::::::::::"+profile.getState()+"::::::::::"+profile.getDepartment());
				List<String> temp = utilityService.fetchDomainsByCatAndMinAndDep(profile.getEmployment(),
						profile.getState(), profile.getDepartment());
				
				for (String string : temp) {
					if (!string.equals("null")) {
						finaldomain.add(string);
						System.out.println(":::::2::::"+finaldomain);
					}
				}
			} else {
				List<String> temp = utilityService.fetchDomainsByCatAndMin(profile.getEmployment(),
						profile.getMinistry());
				System.out.println(":::::3::::"+profile.getEmployment()+":::::::::::"+profile.getOrganization());
				for (String string : temp) {
					if (!string.equals("null")) {
						finaldomain.add(string);
						System.out.println(":::::3::::"+finaldomain);
					}
				}
			}
			for (NknSingleEmpCoord nknSingleEmpCoord : fetchdeails) {
				if (nknSingleEmpCoord.getDomain() != null && !nknSingleEmpCoord.getDomain().isEmpty()
						&& !nknSingleEmpCoord.getDomain().trim().equalsIgnoreCase("null")) {
					if (nknSingleEmpCoord.getEmp_mail_acc_cat() != null
							&& nknSingleEmpCoord.getEmp_mail_acc_cat().trim().equalsIgnoreCase("paid")) {
						finaldomain.add(nknSingleEmpCoord.getDomain().trim().toLowerCase());
					} else {
						finaldomain.add(nknSingleEmpCoord.getDomain().trim().toLowerCase());
					}
				}
			}
			if (finaldomain.isEmpty()) {
				return getnknDomain();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return finaldomain;
	}

	public Set<String> getnknDomain() {
		Set<String> domains = new HashSet<>();
		try {
			domains = nknSingleEmpCoordRepo.fetchdistDomain();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
		}
		return domains;
	}

	

	public boolean updateStatusAndFinalAuditTrack(Status status, FinalAuditTrack finalAuditTrack) {
		return utilityService.updateStatusAndFinalAuditTrack(status, finalAuditTrack);
	}


	public Set<String> findBo(NknSingleBase nknSingleBase) {
		Set<String> bos = new HashSet<>();
		if (nknSingleBase.getEmployment().trim().equalsIgnoreCase("central")
				|| nknSingleBase.getEmployment().trim().equalsIgnoreCase("ut")) {

			bos = nknSingleEmpCoordRepo.fetchByMinistry(nknSingleBase.getEmployment(), nknSingleBase.getMinistry(),
					nknSingleBase.getDepartment());
		} else if (nknSingleBase.getEmployment().trim().equalsIgnoreCase("state")) {
			bos = nknSingleEmpCoordRepo.fetchByState(nknSingleBase.getEmployment(), nknSingleBase.getState(),
					nknSingleBase.getDepartment());
		} else {
			bos = nknSingleEmpCoordRepo.fetchByOrg(nknSingleBase.getEmployment(), nknSingleBase.getOrganization());
		}
		return bos;
	}

	public Boolean checkMobileDuplicate(String regNumber, String mobile, String allow_creation) {
		System.out.println("Inside checkMobileDuplicate");
		Boolean allow = false;
		String search = utilityService.checkAvailableEmail(mobile);
		System.out.println("Inside checkMobileDuplicate search:::::::"+search);
			if(search.equals("")) {
				allow=true;
				}
			return allow;
			}

	public String fetchRandomPassword() {
		StringBuilder password = new StringBuilder();
		int j = 0;
		for (int i = 0; i < 10; i++) {
			password.append(fetchRandomPasswordCharacters(j));
			j++;
			if (j == 4) {
				j = 0;
			}
		}
		return password.toString();
	}

	private String fetchRandomPasswordCharacters(int pos) {
		Random randomNum = new Random();
		StringBuilder randomChar = new StringBuilder();
		switch (pos) {
		case 0:
			// randomChar.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(randomNum.nextInt("ABCDEFGHIJKLMNOPQRSTUVWXYZ".length()
			// - 1)));
			randomChar.append(
					"ABCDEFGHJKMNOPQRSTUVWXYZ".charAt(randomNum.nextInt("ABCDEFGHJKMNOPQRSTUVWXYZ".length() - 1)));
			break;
		case 1:
			randomChar.append("0123456789".charAt(randomNum.nextInt("0123456789".length() - 1)));
			break;
		case 2:
			randomChar.append("@#$%&*".charAt(randomNum.nextInt("@#$%&*".length() - 1)));
			break;
		case 3:
			// randomChar.append("abcdefghijklmnopqrstuvwxyz".charAt(randomNum.nextInt("abcdefghijklmnopqrstuvwxyz".length()
			// - 1)));
			randomChar.append(
					"abcdefghjkmnopqrstuvwxyz".charAt(randomNum.nextInt("abcdefghjkmnopqrstuvwxyz".length() - 1)));// line
		}
		return randomChar.toString();
	}

	public boolean createMailUsers(UserForCreate user, String po, String bo, String domain) throws ParseException {
		boolean status = false;
		System.out.println("Inside createMailUser");
		 status =  utilityService.createMailUsers(user, po, bo);
		System.out.println("Inside createMailUser status:::"+status);
		return status;
	}
	

	public boolean isIdCreated(String regNumber, String po, String bo, String domain, String email, String finalId,
			String primaryId, String remarks) throws ParseException {
		System.out.println("Inside isIdCreated1");
		boolean isIdCreated = false;
		String allow_creation = "";
		UserForCreate userForCreate = new UserForCreate();
		String admin_token = "";

		NknSingleBase nknSingleBase = fetchDetails(regNumber);
		Boolean isAllowCreation = checkMobileDuplicate(regNumber, nknSingleBase.getMobile(), allow_creation);
		System.out.println("Inside isIdCreated1 isAllowCreation:::::::;"+isAllowCreation);
//			 if (!isAllowCreation) {
//				 
//			 }
//			 try {
//	            URL url = new URL("https://100.80.17.250:7071/service/admin/soap/");
//	             admin_token = getAdminAuthToken("suppapp.admin.legacy@legacyaccount.admin", "#ZyPE@8xR#H$TJxT9^H=CTZ@#43f#Dg4dV#Xz@", url);
//	         } catch (Exception ex) {
//	             System.out.println("Exception in admin URL hit ::::::::::::::::::");
//	         }

		admin_token = "valid";

		String fname = "", lname = "";
		if (!nknSingleBase.getName().equals("")) {
			String[] splited = nknSingleBase.getName().split("\\s+");
			System.out.println("Inside isIdCreated1 splited::::"+splited);
			if (splited.length > 0) {
				fname = splited[0];
			}
			if (splited.length > 1) {
				lname = splited[1];
			}
		}
		// String dn_default = "o=" + bo.trim() + ",o=" + po.trim() +
		// ",o=nic.in,dc=nic,dc=in";

		userForCreate.setCn(fname + " " + lname);
		userForCreate.setDisplayName(nknSingleBase.getName());
		userForCreate.setDescription(nknSingleBase.getDescription());
		userForCreate.setFirstName(fname);
		userForCreate.setLastName(lname);
		userForCreate.setEmail(nknSingleBase.getEmail());
		userForCreate.setEmployeeCode(nknSingleBase.getEmpCode());
		userForCreate.setMobile(nknSingleBase.getMobile());
		userForCreate.setOfficeAddress(nknSingleBase.getAddress());
		userForCreate.setPassword(fetchRandomPassword());
		userForCreate.setPostingLocation(nknSingleBase.getCity());
		userForCreate.setState(nknSingleBase.getPostingState());
		userForCreate.setTelephoneNumber(nknSingleBase.getOfficePhone());
		userForCreate.setUsername(nknSingleBase.getPreferred_uid1());
		userForCreate.setNicDateOfBirth(nknSingleBase.getSingle_dob());
		userForCreate.setNicDateOfRetirement(nknSingleBase.getSingle_dor());

		if (!admin_token.isEmpty()) {
			System.out.println("admin_token:");
			if (createMailUsers(userForCreate, po, bo, domain)) {
				System.out.println("createMailUsers Done");
				// createZimEmailId(formName, po, bo, domain, final_sms_id, primaryEmail,
				// reg_no, pass, "", "", admin_token, url);
				isIdCreated = true;
			}
		}
		return isIdCreated;
	}

	public ResponseBean rejectAdmin(String regNumber, String ip, String email, String remarks,
			ResponseBean responseBean) {
		responseBean.setRequestType("Rejection of request by Admin.");
		String formType = "nkn";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;

		MobileAndName mobileAndName = null;
		if (!utilityService.isSupportEmail(email)) {
			mobileAndName = utilityService.fetchMobileAndNameFromLdap(email);
		}
		if (mobileAndName != null) {
			status = utilityService.initializeStatusTable(ip, email, formType, remarks, mobileAndName.getMobile(),
					mobileAndName.getName(), "admin");
			finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks,
					mobileAndName.getMobile(), mobileAndName.getName(), "admin", regNumber);
		} else {
			status = utilityService.initializeStatusTable(ip, email, formType, remarks, "", "iNOC Support", "admin");
			finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, "", "iNOC Support", "admin",
					regNumber);
		}

		status.setRegistrationNo(regNumber);
		status.setRecipientType("");
		status.setStatus(Constants.MAIL_ADMIN_REJECTED);
		status.setRecipient("");

		finalAuditTrack.setStatus(Constants.MAIL_ADMIN_REJECTED);
		finalAuditTrack.setToEmail("");

		if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
			 log.info("{} rejected successfully.", regNumber);
			responseBean.setStatus(regNumber + " rejected successfully");
			responseBean.setRegNumber(regNumber);
		} else {
			 log.debug("Something went wrong. Please try again after sometime.");
			responseBean.setStatus("Something went wrong. Please try again after sometime.");
			responseBean.setRegNumber("");
		}

		return responseBean;
	}

	
	public ResponseBean approveAdmin(String regNumber, String ip, String po, String bo, String domain, String email,
			String finalId, String primaryId, String remarks, ResponseBean responseBean) throws ParseException {
		
		System.out.println("Inside approve admin1");
		
		responseBean.setRequestType("Completion of request by Admin");

		Boolean isIdCreated = false;
		String formType = "nkn";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		MobileAndName mobileAndName = null;
			responseBean.setErrors(null);
			System.out.println("Inside approve admin2");
			isIdCreated = isIdCreated(regNumber, po, bo, domain, email, finalId, primaryId, remarks);
			
			System.out.println("Inside Admin isIdCreated:::::"+isIdCreated);
			
//		if (!utilityService.isSupportEmail(email)) {
//			mobileAndName = utilityService.fetchMobileAndNameFromLdap(email);
//		}
		String daEmail = "";
		String recipientType = "";

		if (isIdCreated) {
			System.out.println("1 isIdCreated:::::"+isIdCreated);
			System.out.println("1 ip:::::"+ip);
			System.out.println("1 email:::::"+email);
			
			System.out.println("1 formType:::::"+formType);
			System.out.println("1 remarks:::::"+remarks);
			
			
			if (!utilityService.isSupportEmail(email)) {
				mobileAndName = utilityService.fetchMobileAndNameFromLdap(email);
			status = utilityService.initializeStatusTable(ip, email, formType, remarks, mobileAndName.getMobile(),
					mobileAndName.getName(), "admin");
			
			System.out.println("1 isIdCreated:::::"+isIdCreated);
			finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks,
					mobileAndName.getMobile(), mobileAndName.getName(), "admin", regNumber);
			}else {
				status = utilityService.initializeStatusTable(ip, email, formType, remarks, "",
						"", "admin");
				
				System.out.println("2 isIdCreated:::::"+isIdCreated);
				finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks,
						"", "", "admin", regNumber);
			}
			
			
			status.setRegistrationNo(regNumber);
			status.setRecipientType(recipientType);
			status.setStatus(Constants.STATUS_COMPLETED);
			status.setRecipient(daEmail);

			finalAuditTrack.setStatus(Constants.STATUS_COMPLETED);
			finalAuditTrack.setToEmail(daEmail);
		} else {
			responseBean.setRegNumber("");
			responseBean.setStatus("ID could not be created.");
		}

		if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
			 log.info("Application (" + regNumber + ") completed Successfully.");
			responseBean.setStatus("Application (" + regNumber + ") completed Successfully.");
			responseBean.setRegNumber(regNumber);
		} else {
			 log.debug("Something went wrong. Please try again after sometime.");
			responseBean.setStatus("Something went wrong. Please try again after sometime.");
			responseBean.setRegNumber(regNumber);
		}
		return responseBean;
	}

	public ResponseBean forwardToDaAdmin(String regNumber, String ip, String email, String remarks,
			ResponseBean responseBean) {
		responseBean.setRequestType("Admin is forwarding the request to DA");
		String adminRemarks = "Forwarded by Admin " + email + " to Delegated Admin.";
		if (!remarks.isEmpty()) {
			adminRemarks += "~User Remarks - " + remarks;
		}
		String formType = "nkn";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		NknSingleBase nknSingleBase = fetchDetails(regNumber);
		ModelMapper modelMapper = new ModelMapper();
		OrganizationBean org = modelMapper.map(nknSingleBase, OrganizationBean.class);
		MobileAndName mobileAndName = null;
		if (!utilityService.isSupportEmail(email)) {
			mobileAndName = utilityService.fetchMobileAndNameFromLdap(email);
		}
		String daEmail = "";
		String toWhom = "";
		String recipientType = "";
		String nextStatus = "";

		LocalDateTime currentTime = null;
		status = new Status();
		finalAuditTrack = utilityService.fetchFinalAuditTrack(regNumber);
		if (mobileAndName != null) {
			status.setSenderName(mobileAndName.getName());
			status.setSenderMobile(mobileAndName.getMobile());
			finalAuditTrack.setAdminMobile(mobileAndName.getMobile());
			finalAuditTrack.setAdminName(mobileAndName.getName());
		} else {
			status.setSenderName("iNOC Support");
			status.setSenderMobile("");
			finalAuditTrack.setAdminMobile("");
			finalAuditTrack.setAdminName("iNOC Support");
		}

		
		status = utilityService.initializeStatusTable(ip, email, formType, remarks, mobileAndName.getMobile(), mobileAndName.getName(),"admin");
		finalAuditTrack = utilityService.initializeFinalAuditTrackTableForReverting(ip, email, formType, remarks, mobileAndName.getMobile(), mobileAndName.getName(),"support", regNumber);
		status.setSubmissionType("forwarded_m_d");
		status.setRemarks(adminRemarks);
		
		Set<String> da = utilityService.fetchDAs(org);

		if (da != null && da.size() > 0) {
			toWhom = "Delegated Admin";
			recipientType = Constants.STATUS_DA_TYPE;
			nextStatus = Constants.STATUS_DA_PENDING;
			daEmail = String.join(",", da);

			status.setRegistrationNo(regNumber.toUpperCase());
			status.setRecipientType(recipientType);
			status.setStatus(nextStatus);
			status.setRecipient(daEmail);

			finalAuditTrack.setStatus(nextStatus);
			finalAuditTrack.setToEmail(daEmail);

			if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
				 log.info("Application (" + regNumber + ") has been pulled Successfully to " +
				 toWhom + "(" + daEmail + ")");
				responseBean.setStatus("Application (" + regNumber + ") has been pulled Successfully to " + toWhom + "("
						+ daEmail + ")");
				responseBean.setRegNumber(regNumber);
			} else {
				 log.debug("Something went wrong. Please try again after sometime.");
				responseBean.setStatus("Something went wrong. Please try again after sometime.");
				responseBean.setRegNumber(regNumber);
			}
		} else {
			 log.debug("There are no DAs for this request(Organization Details)");
			responseBean.setStatus("There are no DAs for this request(Organization Details)");
			responseBean.setRegNumber(regNumber);
		}
		return responseBean;
	}

	public ResponseBean pullAdmin(String regNumber, String ip, String email, String remarks,
			ResponseBean responseBean) {
		responseBean.setRequestType("Admin is reverting the request to support.");
		String adminRemarks = "Reverted by admin " + email + " to iNOC support.";
		if (!remarks.isEmpty()) {
			adminRemarks += "~User Remarks - " + remarks;
		}
		String formType = "nkn";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		MobileAndName mobileAndName = null;
		if (!utilityService.isSupportEmail(email)) {
			mobileAndName = utilityService.fetchMobileAndNameFromLdap(email);
		}
		String daEmail = "";
		String toWhom = "";
		String recipientType = "";
		String nextStatus = "";

		LocalDateTime currentTime = null;
		status = new Status();
		finalAuditTrack = utilityService.fetchFinalAuditTrack(regNumber);
		if (mobileAndName != null) {
			status.setSenderName(mobileAndName.getName());
			status.setSenderMobile(mobileAndName.getMobile());
			finalAuditTrack.setAdminMobile(mobileAndName.getMobile());
			finalAuditTrack.setAdminName(mobileAndName.getName());
		} else {
			status.setSenderName("iNOC Support");
			status.setSenderMobile("");
			finalAuditTrack.setAdminMobile("");
			finalAuditTrack.setAdminName("iNOC Support");
		}

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, mobileAndName.getMobile(), mobileAndName.getName(),"admin");
		finalAuditTrack = utilityService.initializeFinalAuditTrackTableForReverting(ip, email, formType, remarks, mobileAndName.getMobile(), mobileAndName.getName(),"support", regNumber);
		status.setSubmissionType("reverted_m_s");
		status.setRemarks(adminRemarks);

		toWhom = "Support";
		daEmail = Constants.MAILADMIN_EMAIL;
		recipientType = Constants.STATUS_SUPPORT_TYPE;
		nextStatus = Constants.STATUS_SUPPORT_PENDING;

		status.setRegistrationNo(regNumber.toUpperCase());
		status.setRecipientType(recipientType);
		status.setStatus(nextStatus);
		status.setRecipient(daEmail);

		finalAuditTrack.setStatus(nextStatus);
		finalAuditTrack.setToEmail(daEmail);

		if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
			 log.info("Application (" + regNumber + ") has been pulled Successfully to " +
			 toWhom + "(" + daEmail + ")");
			responseBean.setStatus(
					"Application (" + regNumber + ") has been pulled Successfully to " + toWhom + "(" + daEmail + ")");
			responseBean.setRegNumber(regNumber);
		} else {
			 log.debug("Something went wrong. Please try again after sometime.");
			responseBean.setStatus("Something went wrong. Please try again after sometime.");
			responseBean.setRegNumber(regNumber);
		}
		return responseBean;
	}

	
}
