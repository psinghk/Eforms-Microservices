package in.nic.ashwini.eForms.controllers;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.nic.ashwini.eForms.entities.NknBulkEmailBase;
import in.nic.ashwini.eForms.models.ErrorResponseForOrganizationValidationDto;
import in.nic.ashwini.eForms.models.FileUploadPojo;
import in.nic.ashwini.eForms.models.FinalAuditTrack;
import in.nic.ashwini.eForms.models.HodDetailsDto;
import in.nic.ashwini.eForms.models.NextHopBean;
import in.nic.ashwini.eForms.models.OrganizationDto;
import in.nic.ashwini.eForms.models.PreviewFormBean;
import in.nic.ashwini.eForms.models.ProfileDto;
import in.nic.ashwini.eForms.models.ResponseBean;
import in.nic.ashwini.eForms.models.Status;
import in.nic.ashwini.eForms.services.NknBulkEmailService;
import in.nic.ashwini.eForms.utils.NknBulkValidation;
import in.nic.ashwini.eForms.utils.Constants;
import in.nic.ashwini.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequestMapping("/user")
@RestController
public class UserController {
	
	private final ResponseBean responseBean;
	private final Util utilityService;
	private NextHopBean nextHopBean;
	private final NknBulkEmailService nknBulkEmailService;
	private final NknBulkValidation nknBulkValidation;
	
	@Autowired
	public UserController(NknBulkEmailService nknBulkEmailService, ResponseBean responseBean, Util utilityService, NknBulkValidation nknBulkValidation) {
		super();
		this.nknBulkEmailService = nknBulkEmailService;
		this.responseBean = responseBean;
		this.utilityService = utilityService;
		this.nknBulkValidation=nknBulkValidation;
		
	}
	
	
	@PostMapping(value="/test", produces = "application/json")
	public String getTest(@RequestParam("csvFile") MultipartFile file1) {
		System.out.println(file1);
		return "Server running";
	}
	
	

	@RequestMapping(value = "/submitRequest",  method = RequestMethod.POST)
	public ResponseBean submitRequest(@Valid @ModelAttribute("uploadfiles") PreviewFormBean previewFormBean,
			@RequestParam("clientIp") String ip, @RequestParam("email") String email,
			@RequestParam("submissionType")String submissionType) {

		String formType = "nkn_bulk";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		ProfileDto profile = utilityService.fetchProfileByEmailInBean(email);
		ModelMapper modelMapper = new ModelMapper();
		NknBulkEmailBase nknBulkEmailBase = modelMapper.map(profile, NknBulkEmailBase.class);
		Map<String, Object> map = new HashMap<>();
		responseBean.setRequestType("Submission of request");
		
		if (!previewFormBean.getInstName().equalsIgnoreCase("mail") && !previewFormBean.getInstName().equalsIgnoreCase("app")
				&& !previewFormBean.getInstName().equalsIgnoreCase("eoffice")) {
			log.debug("Selected typeOfMailID is not mail or app or eoffice.");
			map.put("typeOfMailIDError", "Selected typeOfMailID is not mail or app or eoffice.");
		}
		if (!previewFormBean.getInstId().equalsIgnoreCase("id_name")
				&& !previewFormBean.getInstId().equalsIgnoreCase("id_desig")) {
			log.debug("Selected emailAddressPreference is neither id_name nor id_desig.");
			map.put("emailAddressPreferenceError", "Selected emailAddressPreference is neither id_name nor id_desig.");
		}
		// emptype=employee description
		if (!previewFormBean.getNknProject().equalsIgnoreCase("emp_regular")
				&& !previewFormBean.getNknProject().equalsIgnoreCase("emp_contract")
				&& !previewFormBean.getNknProject().equalsIgnoreCase("consultant")) {
			log.debug("Selected employeeDescription is not emp_regular or emp_contract or consultant.");
			map.put("employeeDescriptionError",
					"Selected employeeDescription is not emp_regular or emp_contract or consultant.");
		}
		
		if (!previewFormBean.getTnc()) {
			log.debug("Terms and condition is not selected.");
			map.put("tncError", "Please select Terms and Condition to proceed.");
		}
		
		
		
	
		//List<String> finaldomain = new ArrayList<>();
		FileUploadPojo fileUploadPojo = null;
		
		System.out.println("Map Size:::: " + map.size());
		System.out.println("Map:::: " + map);
		
		responseBean.setErrors(null);
		if(nknBulkEmailService.isFilenameCSVValid(previewFormBean.getInfile().get(0).getOriginalFilename())) {
			fileUploadPojo = nknBulkEmailService.checkCSV(previewFormBean.getInfile().get(0));
			if(fileUploadPojo.getErrorMsg() != null) {
				map.put("fileError", fileUploadPojo.getErrorMsg());
			}
			OrganizationDto organizationDetails = new OrganizationDto();
			//
			BeanUtils.copyProperties(previewFormBean, organizationDetails);
			String organizationValidationResponse = utilityService.validateOrganization(organizationDetails);
			ObjectMapper mapper = new ObjectMapper();
			ErrorResponseForOrganizationValidationDto orgError = null;

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
		
		}else {
			map.put("fileError", "file format not csv");
		}		
		
		if (map.size() == 0) {

			if (profile != null) {
				status = initializeStatusTable(ip, email, formType, previewFormBean.getRemarks(), profile.getMobile(),
						profile.getName());
				finalAuditTrack = initializeFinalAuditTrackTable(ip, email, formType, previewFormBean.getRemarks(),
						profile.getMobile(), profile.getName());

				
				if (previewFormBean.getEmployment().equalsIgnoreCase("state")
						&& previewFormBean.getState().equalsIgnoreCase("Himachal Pradesh")) {
					List<String> himachalCoords = utilityService.fetchHimachalCoords(previewFormBean.getDepartment());
					if (himachalCoords != null && himachalCoords.size() > 0) {
						String coordEmail = himachalCoords.get(0);
						if (utilityService.isGovEmployee(coordEmail)) {
							HodDetailsDto roDetails = utilityService.getHodValues(coordEmail);
							profile.setHodEmail(coordEmail);
							profile.setHodMobile(roDetails.getMobile());
							profile.setHodName(roDetails.getFirstName());
							profile.setHodDesignation(roDetails.getDesignation());
							profile.setHodTelephone(roDetails.getTelephoneNumber());
						}
					}
				}

				//BulkUsers bulkUsers = modelMapper.map(profile, BulkUsers.class);//for users
				nknBulkEmailBase.setPdfPath(submissionType);
				nknBulkEmailBase.setRenamedFilepath(Constants.LOCAL_FILE_LOCATION+fileUploadPojo.getFileUploadName());
				nknBulkEmailBase.setUploadedFilename(fileUploadPojo.getFileUploadName());
				BeanUtils.copyProperties(previewFormBean, nknBulkEmailBase);
				
				LocalDateTime currentTime = LocalDateTime.now();
				nknBulkEmailBase.setDatetime(currentTime);
				// nknBulkEmailBase.setLastUpdationDateTime(currentTime);
				nknBulkEmailBase.setUserIp(ip);

				for (int i = 0; i < 4; i++) {
					nknBulkEmailBase = nknBulkEmailService.insert(nknBulkEmailBase);
					if (nknBulkEmailBase.getId() > 0) {
						break;
					}
					
				}
			fileUploadPojo = nknBulkEmailService.dummy(email, fileUploadPojo, nknBulkEmailBase.getRegistrationNo());
			Map<String,Object> outputData = new HashMap<>();
			outputData.put("validFile",fileUploadPojo.getFileUploadValidName());
			outputData.put("errorFile", fileUploadPojo.getFileUploadErrorName());
			outputData.put("invalidFile", fileUploadPojo.getFileUploadNotValidName());
			responseBean.setData(outputData);
				{
					System.out.println("data inserted in bulk  users users"+nknBulkEmailBase.getRegistrationNo());
				}
								
				if (nknBulkEmailBase.getId() > 0) {
					if (submissionType.equalsIgnoreCase("online") || submissionType.equalsIgnoreCase("esign")) {
						if (utilityService.isNicEmployee(email)) {
							status.setRegistrationNo(nknBulkEmailBase.getRegistrationNo());
							status.setRecipientType(Constants.STATUS_CA_TYPE);
							status.setStatus(Constants.STATUS_CA_PENDING);
							status.setRecipient(profile.getHodEmail());

							finalAuditTrack.setRegistrationNo(nknBulkEmailBase.getRegistrationNo());
							finalAuditTrack.setStatus(Constants.STATUS_CA_PENDING);
							finalAuditTrack.setToEmail(profile.getHodEmail());

							if (nknBulkEmailService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
								log.info("{} submitted successfully.", nknBulkEmailBase.getRegistrationNo());
								responseBean.setStatus(
										"Request submitted successfully and forwarded to Reporting/Nodal/Forwarding Officer ("
												+ profile.getHodEmail() + ")");
								responseBean.setRegNumber(nknBulkEmailBase.getRegistrationNo());
							} else {
								log.debug("Something went wrong. Please try again after sometime.");
								responseBean.setStatus("Something went wrong. Please try again after sometime.");
								responseBean.setRegNumber("");
							}
						} else {
							status.setRegistrationNo(nknBulkEmailBase.getRegistrationNo());
							status.setRecipientType(Constants.STATUS_CA_TYPE);
							status.setStatus(Constants.STATUS_CA_PENDING);
							status.setRecipient(profile.getHodEmail());

							finalAuditTrack.setRegistrationNo(nknBulkEmailBase.getRegistrationNo());
							finalAuditTrack.setStatus(Constants.STATUS_CA_PENDING);
							finalAuditTrack.setToEmail(profile.getHodEmail());

							if (nknBulkEmailService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
								log.info("{} submitted successfully.", nknBulkEmailBase.getRegistrationNo());
								responseBean.setStatus(
										"Request submitted successfully and forwarded to Reporting/Nodal/Forwarding Officer ("
												+ profile.getHodEmail() + ")");
								responseBean.setRegNumber(nknBulkEmailBase.getRegistrationNo());
							} else {
								log.debug("Something went wrong. Please try again after sometime.");
								responseBean.setStatus("Something went wrong. Please try again after sometime.");
								responseBean.setRegNumber("");
							}
						}
					} else {
						status.setRegistrationNo(nknBulkEmailBase.getRegistrationNo());
						status.setRecipientType(Constants.STATUS_USER_TYPE);
						status.setStatus(Constants.STATUS_MANUAL_UPLOAD);
						status.setRecipient(email);

						finalAuditTrack.setRegistrationNo(nknBulkEmailBase.getRegistrationNo());
						finalAuditTrack.setStatus(Constants.STATUS_MANUAL_UPLOAD);
						finalAuditTrack.setToEmail(email);

						if (nknBulkEmailService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
							log.info("{} submitted successfully.", nknBulkEmailBase.getRegistrationNo());
							responseBean.setStatus(
									"Request submitted successfully but it is pending with you only as you selected manual upload option to submit the request.");
							responseBean.setRegNumber(nknBulkEmailBase.getRegistrationNo());
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
			} else {
				log.warn(
						"Hey, {},  We do not have your profile in eForms. Please go to profile section and make your profile first");
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
	
	
	@GetMapping("/download/{fileName:.+}")
	public ResponseEntity downloadFileFromLocal(@PathVariable String fileName) {
		Path path = Paths.get(Constants.LOCAL_FILE_LOCATION + fileName);
		Resource resource = null;
		try {
			resource = new UrlResource(path.toUri());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType("application/octet"))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	 * 
	 * @PostMapping(value = "/validateRequest") public Map<String, Object>
	 * validateRequest(PreviewFormBean previewFormBean, String email,MultipartFile
	 * file) { Map<String, Object> map = new HashMap<>(); Map<String, Object>
	 * finalmap = new HashMap<>(); OrganizationDto organizationDetails = new
	 * OrganizationDto();
	 * 
	 * System.out.println(); ProfileDto profile =
	 * utilityService.fetchProfileByEmailInBean(email); List<String> finaldomain =
	 * new ArrayList<>(); FileUploadPojo fileUploadPojo=new FileUploadPojo();
	 * if(nknBulkEmailService.isFilenameCSVValid(file.getOriginalFilename())) {
	 * 
	 * fileUploadPojo=nknBulkEmailService.checkCSV(file); } if
	 * (previewFormBean.getEmpType().equals("emp_contract")) {
	 * finaldomain.add("supportgov.in"); } else if
	 * (previewFormBean.getEmpType().equals("consultant")) {
	 * finaldomain.add("govcontractor.in"); } else { if
	 * (profile.getEmployment().equalsIgnoreCase("central") ||
	 * profile.getEmployment().equalsIgnoreCase("ut")) { List<String> temp =
	 * utilityService.fetchDomainsByCatAndMinAndDep(profile.getEmployment(),
	 * profile.getMinistry(), profile.getDepartment()); for (String string : temp) {
	 * if (string!=null) { finaldomain.add(string); } } } else if
	 * (profile.getEmployment().equalsIgnoreCase("state")) { List<String> temp =
	 * utilityService.fetchDomainsByCatAndMinAndDep(profile.getEmployment(),
	 * profile.getState(), profile.getDepartment()); for (String string : temp) { if
	 * (!string.equals("null")) { finaldomain.add(string); } } } else { List<String>
	 * temp = utilityService.fetchDomainsByCatAndMin(profile.getEmployment(),
	 * profile.getMinistry()); for (String string : temp) { if
	 * (!string.equals("null")) { finaldomain.add(string); } } } }
	 * 
	 * 
	 * if (!previewFormBean.getType().equalsIgnoreCase("mail") &&
	 * !previewFormBean.getType().equalsIgnoreCase("app") &&
	 * !previewFormBean.getType().equalsIgnoreCase("eoffice")) {
	 * log.debug("Selected typeOfMailID is not mail or app or eoffice.");
	 * map.put("typeOfMailIDError",
	 * "Selected typeOfMailID is not mail or app or eoffice."); } if
	 * (!previewFormBean.getIdType().equalsIgnoreCase("id_name") &&
	 * !previewFormBean.getIdType().equalsIgnoreCase("id_desig")) {
	 * log.debug("Selected emailAddressPreference is neither id_name nor id_desig."
	 * ); map.put("emailAddressPreferenceError",
	 * "Selected emailAddressPreference is neither id_name nor id_desig."); } //
	 * emptype=employee description if
	 * (!previewFormBean.getEmpType().equalsIgnoreCase("emp_regular") &&
	 * !previewFormBean.getEmpType().equalsIgnoreCase("emp_contract") &&
	 * !previewFormBean.getEmpType().equalsIgnoreCase("consultant")) { log.
	 * debug("Selected employeeDescription is not emp_regular or emp_contract or consultant."
	 * ); map.put("employeeDescriptionError",
	 * "Selected employeeDescription is not emp_regular or emp_contract or consultant."
	 * ); }
	 * 
	 * 
	 * if (!previewFormBean.getTnc()) {
	 * log.debug("Terms and condition is not selected."); map.put("tncError",
	 * "Please select Terms and Condition to proceed."); }
	 * 
	 * BeanUtils.copyProperties(previewFormBean, organizationDetails); String
	 * organizationValidationResponse =
	 * utilityService.validateOrganization(organizationDetails); ObjectMapper mapper
	 * = new ObjectMapper(); ErrorResponseForOrganizationValidationDto orgError =
	 * null;
	 * 
	 * if (organizationValidationResponse != null &&
	 * !organizationValidationResponse.isEmpty()) {
	 * log.debug("Errors in Organization"); try { orgError =
	 * mapper.readValue(organizationValidationResponse,
	 * ErrorResponseForOrganizationValidationDto.class); } catch
	 * (JsonProcessingException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } if (orgError != null) map.put("orgError", orgError); }
	 * if (map.size() > 1) { finalmap.put("errors", map); }
	 * 
	 * return map; }
	 */
	@RequestMapping(value = "/approve")
	public ResponseBean approve(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, @RequestParam String remarks) {
		responseBean.setRequestType("Approval of request by user as it was submitted manually");
		String formType = "nkn_bulk";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		NknBulkEmailBase nknBulkEmailBase = nknBulkEmailService.preview(regNumber);

		status = initializeStatusTable(ip, email, formType, remarks, nknBulkEmailBase.getMobile(),
				nknBulkEmailBase.getName());

		finalAuditTrack = utilityService.fetchFinalAuditTrack(regNumber);

		if (utilityService.isNicEmployee(email)) {
			status.setRegistrationNo(regNumber);
			status.setRecipientType(Constants.STATUS_ADMIN_TYPE);
			status.setStatus(Constants.STATUS_MAILADMIN_PENDING);
			status.setRecipient(Constants.MAILADMIN_EMAIL);

			finalAuditTrack.setStatus(Constants.STATUS_MAILADMIN_PENDING);
			finalAuditTrack.setToEmail(Constants.MAILADMIN_EMAIL);

			if (nknBulkEmailService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
				log.info("{} submitted successfully.", regNumber);
				responseBean.setStatus(
						"Request submitted successfully and forwarded to Admin (" + Constants.MAILADMIN_EMAIL + ")");
				responseBean.setRegNumber(regNumber);
			} else {
				log.debug("Something went wrong. Please try again after sometime.");
				responseBean.setStatus("Something went wrong. Please try again after sometime.");
				responseBean.setRegNumber("");
			}
		} else {
			status.setRegistrationNo(regNumber);
			status.setRecipientType(Constants.STATUS_CA_TYPE);
			status.setStatus(Constants.STATUS_CA_PENDING);
			status.setRecipient(nknBulkEmailBase.getHodEmail());

			finalAuditTrack.setStatus(Constants.STATUS_CA_PENDING);
			finalAuditTrack.setToEmail(nknBulkEmailBase.getHodEmail());

			if (nknBulkEmailService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
				log.info("{} submitted successfully.", regNumber);
				responseBean.setStatus(
						"Request submitted successfully and forwarded to Reporting/Nodal/Forwarding Officer ("
								+ nknBulkEmailBase.getHodEmail() + ")");
				responseBean.setRegNumber(regNumber);
			} else {
				log.debug("Something went wrong. Please try again after sometime.");
				responseBean.setStatus("Something went wrong. Please try again after sometime.");
				responseBean.setRegNumber("");
			}
		}
		return responseBean;
	}

	@RequestMapping(value = "/reject")
	public ResponseBean reject(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email,
			@RequestParam @NotEmpty @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {
		responseBean.setRequestType("Cancellation of request by user.");
		String formType = "nkn_bulk";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		NknBulkEmailBase nknBulkEmailBase = nknBulkEmailService.preview(regNumber);

		status = initializeStatusTable(ip, email, formType, remarks, nknBulkEmailBase.getMobile(),
				nknBulkEmailBase.getName());

		finalAuditTrack = utilityService.fetchFinalAuditTrack(regNumber);

		status.setRegistrationNo(regNumber);
		status.setRecipientType("");
		status.setStatus(Constants.STATUS_USER_REJECTED);
		status.setRecipient("");

		finalAuditTrack.setStatus(Constants.STATUS_USER_REJECTED);
		finalAuditTrack.setToEmail("");

		if (nknBulkEmailService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
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

	private Status initializeStatusTable(String ip, String email, String formType, String remarks, String mobile,
			String name) {
		Status status = new Status();
		status.setFormType(formType);
		status.setIp(ip);
		status.setSenderIp(ip);
		status.setOnholdStatus("n");
		status.setFinalId("");
		status.setRemarks(remarks);
		status.setSenderType(Constants.STATUS_USER_TYPE);
		status.setSender(email);
		status.setSenderEmail(email);
		LocalDateTime currentTime = LocalDateTime.now();
		status.setCreatedon(currentTime);
		status.setSenderDatetime(currentTime);
		status.setSenderName(name);
		status.setSenderMobile(mobile);
		return status;
	}

	

	private FinalAuditTrack initializeFinalAuditTrackTable(String ip, String email, String formType, String remarks,
			String mobile, String name) {
		FinalAuditTrack finalAuditTrack = new FinalAuditTrack();
		finalAuditTrack.setFormName(formType);
		finalAuditTrack.setApplicantIp(ip);
		finalAuditTrack.setOnHold("n");
		finalAuditTrack.setApplicantRemarks(remarks);
		finalAuditTrack.setApplicantMobile(mobile);
		finalAuditTrack.setApplicantName(name);
		finalAuditTrack.setApplicantEmail(email);
		LocalDateTime currentTime = LocalDateTime.now();
		finalAuditTrack.setApplicantDatetime(currentTime);
		finalAuditTrack.setToDatetime(currentTime);
		return finalAuditTrack;
	}

	@GetMapping("/fetchNextHop")
	public NextHopBean findNextHop(@Valid @RequestBody PreviewFormBean previewFormBean,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email,
			@RequestParam("submissionType") @NotEmpty String submissionType) {

		ProfileDto profile = utilityService.fetchProfileByEmailInBean(email);
		if (profile != null) {
			if (previewFormBean.getEmployment().equalsIgnoreCase("state")
					&& previewFormBean.getState().equalsIgnoreCase("Himachal Pradesh")) {
				List<String> himachalCoords = utilityService.fetchHimachalCoords(previewFormBean.getDepartment());
				if (himachalCoords != null && himachalCoords.size() > 0) {
					String coordEmail = himachalCoords.get(0);
					if (utilityService.isGovEmployee(coordEmail)) {
						HodDetailsDto roDetails = utilityService.getHodValues(coordEmail);
						profile.setHodEmail(coordEmail);
						profile.setHodMobile(roDetails.getMobile());
						profile.setHodName(roDetails.getFirstName());
						profile.setHodDesignation(roDetails.getDesignation());
						profile.setHodTelephone(roDetails.getTelephoneNumber());
					}
				}
			}

			if (submissionType.equalsIgnoreCase("online") || submissionType.equalsIgnoreCase("esign")) {
				if (utilityService.isNicEmployee(email)) {
					nextHopBean.setEmail(Constants.MAILADMIN_EMAIL);
					nextHopBean.setName("iNOC Support");
					nextHopBean.setDesignation("support");
					nextHopBean.setMobile("");
					nextHopBean.setRole("Admin");
					nextHopBean.setStatus("Request is getting forwarded to admin.");
				} else {
					nextHopBean.setEmail(profile.getHodEmail());
					nextHopBean.setName(profile.getHodName());
					nextHopBean.setDesignation(profile.getHodDesignation());
					nextHopBean.setMobile(profile.getHodMobile());
					nextHopBean.setRole("Reporting/Forwarding/Nodal Officer");
					nextHopBean.setStatus("Request is getting forwarded to your reporting/forwarding/nodal officer.");
				}
			} else {
				nextHopBean.setEmail(email);
				nextHopBean.setName(profile.getName());
				nextHopBean.setDesignation(profile.getDesignation());
				nextHopBean.setMobile(profile.getMobile());
				nextHopBean.setRole("self");
				nextHopBean.setStatus("Request is pending with you only as you opted for manual submission.");
			}

		} else {
			log.warn(
					"Hey, {},  We do not have your profile in eForms. Please go to profile section and make your profile first");
			nextHopBean.setStatus(
					"We do not have your profile in eForms. Please go to profile section and make your profile first");
		}
		return nextHopBean;
	}
}
