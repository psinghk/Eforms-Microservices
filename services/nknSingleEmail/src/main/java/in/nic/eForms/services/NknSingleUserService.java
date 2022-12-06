package in.nic.eForms.services;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;

import in.nic.eForms.entities.GeneratePdf;
import in.nic.eForms.entities.NknSingleBase;
import in.nic.eForms.entities.NknSingleEmpCoord;
import in.nic.eForms.models.ErrorResponseForOrganizationValidationDto;
import in.nic.eForms.models.FinalAuditTrack;
import in.nic.eForms.models.GeneratePdfBean;
import in.nic.eForms.models.HodDetailsDto;
import in.nic.eForms.models.OrganizationDto;
import in.nic.eForms.models.PreviewFormBean;
import in.nic.eForms.models.ProfileDto;
import in.nic.eForms.models.ResponseBean;
import in.nic.eForms.models.Status;
import in.nic.eForms.models.ValidateFormBean;
import in.nic.eForms.repositories.GeneratePdfRepository;
import in.nic.eForms.repositories.NknSingleBaseRepo;
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
public class NknSingleUserService {
	private final NknSingleBaseRepo nknSingleBaseRepo;
	private final Util utilityService;
	private final GeneratePdfRepository generatePdfRepository;
	@Autowired
	public NknSingleUserService(NknSingleBaseRepo nknSingleBaseRepo, Util utilityService,GeneratePdfRepository generatePdfRepository) {
		super();
		this.nknSingleBaseRepo = nknSingleBaseRepo;
		this.utilityService = utilityService;
		this.generatePdfRepository = generatePdfRepository;

	}
	
	
	public Map<String,Object> validate(ValidateFormBean validateFormBean) {
		Map<String,Object> finalmap = new HashMap<>();
		Map<String,Object> map = new HashMap<>();
		
		boolean inst_name_error = utilityService.nameValidation(validateFormBean.getInst_name());
		boolean inst_id_error = utilityService.institueIdValidation(validateFormBean.getInst_id());
		boolean project_name_error = utilityService.nameValidation(validateFormBean.getNkn_project());
		boolean dob_err = utilityService.dobValidation(validateFormBean.getSingle_dob());
		boolean dor_err = utilityService.dorValidation(validateFormBean.getSingle_dor(), validateFormBean.getSingle_dob());
		boolean pref1_error = utilityService.EmailValidation(validateFormBean.getPreferred_email1());
        boolean pref2_error = utilityService.EmailValidation(validateFormBean.getPreferred_email2());
        System.out.println("form_details.getInst_name()" + validateFormBean.getInst_name());
       
    
        if (inst_name_error == true) {
            System.out.println("inside inst_name_error true");
            map.put("inst_name_error", "Enter Institute Name [Only characters (limit 1-50),whitespace,comma(,) allowed]");
        }
        if (inst_id_error == true) {
        	map.put("inst_id_error", "Enter Institute ID [Alphanumeric (limit 1-50),dot(.),comma(,),hypen(-) allowed]");
        }
        if (project_name_error == true) {
        	map.put("project_name_error", "Enter Name of Project NKN [Only characters,whitespace,comma(,)allowed]");
        }
        if (pref1_error == true) {
        	map.put("pref1_error", "Enter Email Address [e.g: abc.xyz@gov.in]");
        } else {
//            checkAvailableEmail();
        }
        if (pref2_error == true) {
        	map.put("pref2_error", "Enter Email Address [e.g: abc.xyz@gov.in]");
        } else {
//            checkAvailableEmail();
        }

        if (validateFormBean.getPreferred_email1().equals(validateFormBean.getPreferred_email2())) {
        	map.put("pref2_error", "Preferred Email2 cannot be same as Preferred Email1");
        }
        
        if (!dob_err) {
            map.put("dob_err", "minimum age is 18 years and maximum age is 67 years");
        }
        
        if (!dor_err) {
            map.put("dor_err", "year of retirement can not exceed 67 years from the DOB year");
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
			
			System.out.println(":::::11111111111111111111111111111::::");
			for (NknSingleEmpCoord nknSingleEmpCoord : fetchdeails) {
				if (nknSingleEmpCoord.getDomain() != null && !nknSingleEmpCoord.getDomain().isEmpty()
						&& !nknSingleEmpCoord.getDomain().trim().equalsIgnoreCase("null")) {
					
					if (nknSingleEmpCoord.getEmp_mail_acc_cat() != null
							&& nknSingleEmpCoord.getEmp_mail_acc_cat().trim().equalsIgnoreCase("paid")) {
						
						finaldomain.add(nknSingleEmpCoord.getDomain().trim().toLowerCase());
						System.out.println(":::::4::::"+finaldomain);
						
					} else {
						
						finaldomain.add(nknSingleEmpCoord.getDomain().trim().toLowerCase());
						System.out.println(":::::5::::"+finaldomain);
					}
				}
			}
			if (finaldomain.isEmpty()) {
				System.out.println(":::::6::::");
				return getnknDomain();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return finaldomain;
	}

	public Set<String> getnknDomain() {
		Set<String> domains=null;
		try {
			List<String> domain = utilityService.fetchdistDomain();
			 domains = new HashSet<String>(domain);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
		}
		return domains;
	}


	public NknSingleBase fetchDetails(String regNo) {
		return nknSingleBaseRepo.findByRegistrationNo(regNo);
	}

	@Transactional
	public NknSingleBase insert(NknSingleBase ldapBase) {
		if (ldapBase != null) {
			Date date = new Date();
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			String pdate = dateFormat.format(date);
			String oldRegNumber = nknSingleBaseRepo.findLatestRegistrationNo();
			String newRegNumber = "NKN-FORM" + pdate;
			if (oldRegNumber == null || oldRegNumber.isEmpty()) {
				newRegNumber += "0001";
			} else {
				String lastst = oldRegNumber.substring(16, oldRegNumber.length());
				int last = Integer.parseInt(lastst);
				int newrefno = last + 1;
				int len = Integer.toString(newrefno).length();
				if (len == 1) {
					newRegNumber += "000" + newrefno;
				} else if (len == 2) {
					newRegNumber += "00" + newrefno;
				} else if (len == 3) {
					newRegNumber += "0" + newrefno;
				}
			}
			ldapBase.setRegistrationNo(newRegNumber);
			ldapBase.setSupportActionTaken("p");
			return nknSingleBaseRepo.save(ldapBase);
		}
		return null;
	}

	public boolean updatePreviewDetails(String regNumber, PreviewFormBean previewFormBean) {
		NknSingleBase nknBase = nknSingleBaseRepo.findByRegistrationNo(regNumber);
		if (nknBase != null) {
			try {
				BeanUtils.copyProperties(previewFormBean, nknBase);
				LocalDateTime currentTime = LocalDateTime.now();
				// ldapBase.setLastUpdationDateTime(currentTime);
				NknSingleBase nknUpdated = nknSingleBaseRepo.save(nknBase);
				if (nknUpdated.getId() > 0) {
					return true;
				}
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}


	public boolean updateStatusAndFinalAuditTrack(Status status, FinalAuditTrack finalAuditTrack) {
		return utilityService.updateStatusAndFinalAuditTrack(status, finalAuditTrack);
	}

	public ResponseBean submitRequest(PreviewFormBean previewFormBean, String ip, String email, String submissionType,
			ResponseBean responseBean) throws ParseException {
		System.out.println("::::::::1:::::::::::");
		String formType = "nkn";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		ProfileDto profile = utilityService.fetchProfileByEmailInBean(email);
		responseBean.setRequestType("Submission of request");
		Map<String, Object> map = validateRequest(previewFormBean);
		
		
		String pemail1=previewFormBean.getPreferred_email1();
		String pemail2=previewFormBean.getPreferred_email2();
		
		String[] token1=pemail1.split("@");
		String[] token2=pemail2.split("@");

		System.out.println("Map Size:::: " + map.size());
		System.out.println("Map:::: " + map);
		if (map.size() == 0 ) {
			responseBean.setErrors(null);
			System.out.println("responseBean:::: " + responseBean);
			if (profile != null) {
				
				String hmapp = utilityService.checkAvailableEmail(profile.getMobile());
				if ( hmapp.equals("") ) {
					
				System.out.println("profile:::: " + profile);

				status = utilityService.initializeStatusTable(ip, email, formType, previewFormBean.getRemarks(), profile.getMobile(),
						profile.getName(), "user");

				System.out.println("status:::: " + status);

				finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, previewFormBean.getRemarks(),
						profile.getMobile(), profile.getName(), "user","");

				System.out.println("finalAuditTrack:::: " + finalAuditTrack);
				ModelMapper modelMapper = new ModelMapper();
				if (previewFormBean.getEmployment().equalsIgnoreCase("state")
						&& previewFormBean.getState().equalsIgnoreCase("Himachal Pradesh")) {
					System.out.println("Himachal Pradesh:::: ");
					List<String> himachalCoords = utilityService.fetchHimachalCoords(previewFormBean.getDepartment());
					if (himachalCoords != null && himachalCoords.size() > 0) {
						String coordEmail = himachalCoords.get(0);
						if (utilityService.isGovEmployee(coordEmail)) {
							HodDetailsDto roDetails = utilityService.getHodValues(coordEmail);
							System.out.println("roDetails:::: " + roDetails);
							profile.setHodEmail(coordEmail);
							profile.setHodMobile(roDetails.getMobile());
							profile.setHodName(roDetails.getFirstName());
							profile.setHodDesignation(roDetails.getDesignation());
							profile.setHodTelephone(roDetails.getTelephoneNumber());
						}
					}
				}

				NknSingleBase nknSingleBase = modelMapper.map(profile, NknSingleBase.class);
				System.out.println("nknSingleBase:1::: " + nknSingleBase);
				nknSingleBase.setPdfPath(submissionType);
				System.out.println("previewFormBean:1::: " + previewFormBean);
				BeanUtils.copyProperties(previewFormBean, nknSingleBase);
				LocalDateTime currentTime = LocalDateTime.now();
				nknSingleBase.setDatetime(currentTime);
				// ldapBase.setLastUpdationDateTime(currentTime);
				nknSingleBase.setUserIp(ip);
				nknSingleBase.setPreferred_uid1(token1[0]);
				nknSingleBase.setPreferred_uid2(token2[0]);
				nknSingleBase.setRequest_type("nkn_single");
				
				

				for (int i = 0; i < 4; i++) {
					nknSingleBase = insert(nknSingleBase);
					System.out.println("nknSingleBase:2::: " + nknSingleBase);
					if (nknSingleBase.getId() > 0) {
						System.out.println("nknSingleBase:::: " + nknSingleBase.getId());
						break;
					}
				}

				if (nknSingleBase.getId() > 0) {
					if (submissionType.equalsIgnoreCase("online") || submissionType.equalsIgnoreCase("esign")) {
						System.out.println("online:::: ");
						status.setRegistrationNo(nknSingleBase.getRegistrationNo());
						status.setRecipientType(Constants.STATUS_CA_TYPE);
						status.setStatus(Constants.STATUS_CA_PENDING);
						status.setRecipient(profile.getHodEmail());

						finalAuditTrack.setRegistrationNo(nknSingleBase.getRegistrationNo());
						finalAuditTrack.setStatus(Constants.STATUS_CA_PENDING);
						finalAuditTrack.setToEmail(profile.getHodEmail());
						
						if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
							 log.info("{} submitted successfully.", nknSingleBase.getRegistrationNo());
							responseBean.setStatus(
									"Request submitted successfully and forwarded to Reporting/Nodal/Forwarding Officer("
											+ profile.getHodEmail() + ")");
							responseBean.setRegNumber(nknSingleBase.getRegistrationNo());
						} else {
							 log.debug("Something went wrong. Please try again after sometime.");
							responseBean.setStatus("Something went wrong. Please try again after sometime.");
							responseBean.setRegNumber("");
						}
					} else {
						
						
						status.setRegistrationNo(nknSingleBase.getRegistrationNo());
						status.setRecipientType(Constants.STATUS_USER_TYPE);
						status.setStatus(Constants.STATUS_MANUAL_UPLOAD);
						status.setRecipient(email);

						finalAuditTrack.setRegistrationNo(nknSingleBase.getRegistrationNo());
						finalAuditTrack.setStatus(Constants.STATUS_MANUAL_UPLOAD);
						finalAuditTrack.setToEmail(email);

						if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
						 log.info("{} submitted successfully.", nknSingleBase.getRegistrationNo());
							responseBean.setStatus(
									"Request submitted successfully but it is pending with you only as you selected manual upload option to submit the request.");
							responseBean.setRegNumber(nknSingleBase.getRegistrationNo());
						} else {
							 log.debug("Something went wrong. Please try again after sometime.");
							responseBean.setStatus("Something went wrong. Please try again after sometime.");
							responseBean.setRegNumber("");
						}
					}
				} else {
					 log.debug("Something went wrong. Please try again after sometime.");
					responseBean.setStatus("Something went wrong. Please try again after sometime.");
					responseBean.setRegNumber("");
				}
				
				
			}else {
				if (!(hmapp.equals(""))) {
					responseBean.setStatus(hmapp);
				}
				responseBean.setRegNumber("");
				responseBean.setStatus("Application could not be submitted.");
			}	
				
			} else {
				 log.warn("Hey, {}, We do not have your profile in eForms. Please go to profile section and make your profile first");
				responseBean.setStatus(
						"We do not have your profile in eForms. Please go to profile section and make your profile first");
				responseBean.setRegNumber("");
			}
		} else {
			responseBean.setErrors(map);
			responseBean.setRegNumber("");
			responseBean.setStatus("Application could not be submitted.");
		}
		return responseBean;

	}

	public Map<String, Object> validateRequest(PreviewFormBean previewFormBean) throws ParseException {
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> finalmap = new HashMap<>();
		OrganizationDto organizationDetails = new OrganizationDto();

		BeanUtils.copyProperties(previewFormBean, organizationDetails);
		String organizationValidationResponse = utilityService.validateOrganization(organizationDetails);
		ObjectMapper mapper = new ObjectMapper();
		ErrorResponseForOrganizationValidationDto orgError = null;
		
		

		boolean inst_name_error = utilityService.nameValidation(previewFormBean.getInst_name());
		boolean inst_id_error = utilityService.institueIdValidation(previewFormBean.getInst_id());
		boolean project_name_error = utilityService.nameValidation(previewFormBean.getNkn_project());
		boolean dob_err = utilityService.dobValidation(previewFormBean.getSingle_dob());
		boolean dor_err = utilityService.dorValidation(previewFormBean.getSingle_dor(), previewFormBean.getSingle_dob());
		boolean pref1_error = utilityService.EmailValidation(previewFormBean.getPreferred_email1());
        boolean pref2_error = utilityService.EmailValidation(previewFormBean.getPreferred_email2());
        System.out.println("form_details.getInst_name()" + previewFormBean.getInst_name());
       
    
        if (inst_name_error == true) {
            System.out.println("inside inst_name_error true");
            map.put("inst_name_error", "Enter Institute Name [Only characters (limit 1-50),whitespace,comma(,) allowed]");
        }
        if (inst_id_error == true) {
        	map.put("inst_id_error", "Enter Institute ID [Alphanumeric (limit 1-50),dot(.),comma(,),hypen(-) allowed]");
        }
        if (project_name_error == true) {
        	map.put("project_name_error", "Enter Name of Project NKN [Only characters,whitespace,comma(,)allowed]");
        }
        if (pref1_error == true) {
        	map.put("pref1_error", "Enter Email Address [e.g: abc.xyz@gov.in]");
        } else {
//            checkAvailableEmail();
        }
        if (pref2_error == true) {
        	map.put("pref2_error", "Enter Email Address [e.g: abc.xyz@gov.in]");
        } else {
//            checkAvailableEmail();
        }

        if (previewFormBean.getPreferred_email1().equals(previewFormBean.getPreferred_email2())) {
        	map.put("pref2_error", "Preferred Email2 cannot be same as Preferred Email1");
        }
        
        if (!dob_err) {
            map.put("dob_err", "minimum age is 18 years and maximum age is 67 years");
        }
        
        if (!dor_err) {
            map.put("dor_err", "year of retirement can not exceed 67 years from the DOB year");
        }
		

		if (organizationValidationResponse != null && !organizationValidationResponse.isEmpty()) {
		 log.debug("Errors in Organization");
			try {
				orgError = mapper.readValue(organizationValidationResponse,
						ErrorResponseForOrganizationValidationDto.class);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			if (orgError != null)
				map.put("orgError", orgError);
		}
		if (map.size() > 1) {
			finalmap.put("errors", map);
		}
		return map;
	}

	public ResponseBean approve(String regNumber, String ip, String email, String remarks, ResponseBean responseBean) {
		responseBean.setRequestType("Approval of request by user as it was submitted manually");
		String formType = "nkn";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		NknSingleBase nknSingleBase = fetchDetails(regNumber);

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, nknSingleBase.getMobile(), nknSingleBase.getName(),
				"user");

		finalAuditTrack = utilityService.fetchFinalAuditTrack(regNumber);
		
			status.setRegistrationNo(regNumber);
			status.setRecipientType(Constants.STATUS_CA_TYPE);
			status.setStatus(Constants.STATUS_CA_PENDING);
			status.setRecipient(nknSingleBase.getHodEmail());

			finalAuditTrack.setStatus(Constants.STATUS_CA_PENDING);
			finalAuditTrack.setToEmail(nknSingleBase.getHodEmail());

			if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
				 log.info("{} submitted successfully.", regNumber);
				responseBean.setStatus(
						"Request submitted successfully and forwarded to Reporting/Nodal/Forwarding Officer ("
								+ nknSingleBase.getHodEmail() + ")");
				responseBean.setRegNumber(regNumber);
			} else {
				 log.debug("Something went wrong. Please try again after sometime.");
				responseBean.setStatus("Something went wrong. Please try again after sometime.");
				responseBean.setRegNumber("");
			}
		return responseBean;
	}

	public ResponseBean reject(String regNumber, String ip, String email, String remarks, ResponseBean responseBean) {
		responseBean.setRequestType("Cancellation of request by user.");
		String formType = "nkn";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		NknSingleBase nknSingleBase = fetchDetails(regNumber);

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, nknSingleBase.getMobile(), nknSingleBase.getName(),
				"user");

		finalAuditTrack = utilityService.fetchFinalAuditTrack(regNumber);

		status.setRegistrationNo(regNumber);
		status.setRecipientType("");
		status.setStatus(Constants.STATUS_USER_REJECTED);
		status.setRecipient("");

		finalAuditTrack.setStatus(Constants.STATUS_USER_REJECTED);
		finalAuditTrack.setToEmail("");

		if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
			 log.info("{} cancelled successfully.", regNumber);
			responseBean.setStatus(regNumber + " cancelled successfully");
			responseBean.setRegNumber(regNumber);
		} else {
			 log.debug("Something went wrong. Please try again after sometime.");
			responseBean.setStatus("Something went wrong. Please try again after sometime.");
			responseBean.setRegNumber("");
		}

		return responseBean;
	}
	
	public Map<String,Object> preview(String regNumber) {
		//LdapBase ldapBase = ldapBaseRepo.findByRegistrationNo(regNumber);
		System.out.println("preview:::::::::::regNumber::::::"+regNumber);
		  Map<String,Object> data=new HashMap<String, Object>();
		  data.put("preview", nknSingleBaseRepo.findByRegistrationNo(regNumber));
		  return data;
	}
     
	
	public JasperPrint generateFormPdf(String regid)
			throws JRException, IOException, IllegalAccessException, InvocationTargetException {
		JasperPrint jasperPrint = null;
		Optional<GeneratePdf> formDetails = generatePdfRepository.findByRegistrationNo(regid);
		GeneratePdf generatePdfDTO = null;
		if (formDetails.isPresent()) {
			generatePdfDTO = formDetails.orElse(null);
			GeneratePdfBean generatePdfBean = new GeneratePdfBean();
			org.springframework.beans.BeanUtils.copyProperties(generatePdfDTO, generatePdfBean);
			File file = ResourceUtils.getFile("classpath:nknsinglereport.jrxml");
			JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
			JRDataSource datasource = new JREmptyDataSource();
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("regid", generatePdfBean.getRegistrationNo());
			parameters.put("applicant_name", generatePdfBean.getApplicantName());
			parameters.put("applicant_email", generatePdfBean.getApplicantEmail());
			parameters.put("min", generatePdfBean.getMinistry());
			parameters.put("applicant_mobile", generatePdfBean.getApplicantMobile());
			parameters.put("hod_name", generatePdfBean.getHodName());
			parameters.put("hod_email", generatePdfBean.getHodEmail());
			parameters.put("hod_mobile", generatePdfBean.getHodMobile());
			parameters.put("Institute Name", generatePdfBean.getInst_name());
			parameters.put("Institute ID", generatePdfBean.getInst_id());
			parameters.put("NKN Project", generatePdfBean.getNkn_project());
			parameters.put("DOB", generatePdfBean.getSingle_dob());
			parameters.put("DOR", generatePdfBean.getSingle_dor());
			parameters.put("Preferred Email1", generatePdfBean.getPreferred_email1());
			parameters.put("Preferred Email1", generatePdfBean.getPreferred_email2());
			parameters.put("logo", "classpath:static/NIC-Logo.jpg");
			System.out.println(parameters);
			jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, datasource);
		}
		return jasperPrint;
	}

}
