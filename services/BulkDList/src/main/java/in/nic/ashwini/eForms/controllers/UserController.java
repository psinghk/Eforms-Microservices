package in.nic.ashwini.eForms.controllers;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.nic.ashwini.eForms.entities.BulkDlistBase;
import in.nic.ashwini.eForms.models.ErrorResponseForOrganizationValidationDto;
import in.nic.ashwini.eForms.models.FileUploadPojo;
import in.nic.ashwini.eForms.models.FinalAuditTrack;
import in.nic.ashwini.eForms.models.NextHopBean;
import in.nic.ashwini.eForms.models.OrganizationDto;
import in.nic.ashwini.eForms.models.PreviewFormBean;
import in.nic.ashwini.eForms.models.ProfileDto;
import in.nic.ashwini.eForms.models.ResponseBean;
import in.nic.ashwini.eForms.models.Status;
import in.nic.ashwini.eForms.services.BulkDlistService;
import in.nic.ashwini.eForms.utils.Constants;
import in.nic.ashwini.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequestMapping("/user")
@RestController
public class UserController {
	
	private final BulkDlistService bulkdlistService;
	private final ResponseBean responseBean;
	private final Util utilityService;
	@Autowired
	public UserController(BulkDlistService bulkdlistService, ResponseBean responseBean, Util utilityService) {
		super();
		this.bulkdlistService = bulkdlistService;
		this.responseBean = responseBean;
		this.utilityService = utilityService;
	}
	
	@PostMapping(value="/test", produces = "application/json")
	public String getTest(@RequestParam("excelFile") MultipartFile file1) {
		System.out.println(file1);
		return "Server running";
	}

	@RequestMapping(value = "/submitRequest")
	public ResponseBean submitRequest(@Valid @ModelAttribute("uploadfiles") PreviewFormBean previewFormBean,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email,
			@RequestParam("submissionType") @NotEmpty String submissionType) {
		log.info("Bulk Dlist Submit request called by {}",email);
		String formType = "bulkdlist";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		ProfileDto profile = utilityService.fetchProfileByEmailInBean(email);
		responseBean.setRequestType("Submission of request");

		ModelMapper modelMapper = new ModelMapper();
		BulkDlistBase bulkDlistBase = modelMapper.map(profile, BulkDlistBase.class);
		Map<String,Object> map = validateRequest(previewFormBean);
		System.out.println("Map Size:::: "+map.size());
		System.out.println("Map:::: "+map);
		
		FileUploadPojo fileUploadPojo = null;
		System.out.println("Map Size:::: " + map.size());
		System.out.println("Map:::: " + map);
		responseBean.setErrors(null);
		if(bulkdlistService.isFilenameExcelValid(previewFormBean.getInfile().get(0).getOriginalFilename())) {
			log.info("Validate excel file" +previewFormBean.getInfile().get(0).getOriginalFilename());
			fileUploadPojo = bulkdlistService.checkExcel(previewFormBean.getInfile().get(0));
			if(fileUploadPojo.getErrorMsg() != null) {
				map.put("fileError", fileUploadPojo.getErrorMsg());
			}
			OrganizationDto organizationDetails = new OrganizationDto();
			BeanUtils.copyProperties(previewFormBean, organizationDetails);
			String organizationValidationResponse = utilityService.validateOrganization(organizationDetails);
			ObjectMapper mapper = new ObjectMapper();
			ErrorResponseForOrganizationValidationDto orgError = null;

			if (organizationValidationResponse != null && !organizationValidationResponse.isEmpty()) {
				log.info("Organization name {}",organizationDetails.getOrganization());
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
			map.put("fileError", "file format not excel");
		}		
		
		if (map.size() == 0) {

			if (profile != null) {
				status = utilityService.initializeStatusTable(ip, email, formType, previewFormBean.getRemarks(), profile.getMobile(),
						profile.getName(), "user");
				finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, previewFormBean.getRemarks(),
						profile.getMobile(), profile.getName(), "user", "");

				bulkDlistBase.setPdfPath(submissionType);
				bulkDlistBase.setRenamedFilepath(Constants.LOCAL_FILE_LOCATION+fileUploadPojo.getFileUploadName());
				bulkDlistBase.setUploadedFilename(fileUploadPojo.getFileUploadName());
				BeanUtils.copyProperties(previewFormBean, bulkDlistBase);
				LocalDateTime currentTime = LocalDateTime.now();
				bulkDlistBase.setDatetime(currentTime);
				bulkDlistBase.setUserIp(ip);

				for (int i = 0; i < 4; i++) {
					bulkDlistBase = bulkdlistService.insert(bulkDlistBase);
					if (bulkDlistBase.getId() > 0) {
						break;
					}
				}
			fileUploadPojo = bulkdlistService.dummy(email, fileUploadPojo, bulkDlistBase.getRegistrationNo());
			
		     String regno =bulkDlistBase.getRegistrationNo();
		     ArrayList ExcelfileData= (ArrayList) fileUploadPojo.getBulkDlist();
		     ArrayList ExcelfileOwnerData= (ArrayList) fileUploadPojo.getOwners();
		     ArrayList ExcelfileModeratorData= (ArrayList) fileUploadPojo.getModerators();
		     
			bulkdlistService.insertExcelFileDetails(ExcelfileData, regno);
			bulkdlistService.insertModerator(ExcelfileOwnerData, ExcelfileModeratorData, regno);
			
			Map<String,Object> outputData = new HashMap<>();
			outputData.put("valid1",fileUploadPojo.getBulkDlist());
			outputData.put("error1", fileUploadPojo.getErrorList());
			outputData.put("not-valid1", fileUploadPojo.getNonvalid());
			responseBean.setData(outputData);
				{
					System.out.println("data inserted in bulk dlist base"+bulkDlistBase.getRegistrationNo());
				}
			if (bulkDlistBase.getId() > 0) {
				
				if (submissionType.equalsIgnoreCase("online") || submissionType.equalsIgnoreCase("esign")) {
					if (utilityService.isNicEmployee(email)) {
						status.setRegistrationNo(bulkDlistBase.getRegistrationNo());
						status.setRecipientType(Constants.STATUS_ADMIN_TYPE);
						status.setStatus(Constants.STATUS_MAILADMIN_PENDING);
						status.setRecipient(Constants.MAILADMIN_EMAIL);

						finalAuditTrack.setRegistrationNo(bulkDlistBase.getRegistrationNo());
						finalAuditTrack.setStatus(Constants.STATUS_MAILADMIN_PENDING);
						finalAuditTrack.setToEmail(Constants.MAILADMIN_EMAIL);

						if (bulkdlistService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
							log.info("{} submitted successfully.", bulkDlistBase.getRegistrationNo());
							responseBean.setStatus("Request submitted successfully and forwarded to Admin ("
									+ Constants.MAILADMIN_EMAIL + ")");
							responseBean.setRegNumber(bulkDlistBase.getRegistrationNo());
						} else {
							log.debug("Something went wrong. Please try again after sometime.");
							responseBean.setStatus("Something went wrong. Please try again after sometime.");
							responseBean.setRegNumber("");
						}
					} else {
						status.setRegistrationNo(bulkDlistBase.getRegistrationNo());
						status.setRecipientType(Constants.STATUS_CA_TYPE);
						status.setStatus(Constants.STATUS_CA_PENDING);
						status.setRecipient(profile.getHodEmail());

						finalAuditTrack.setRegistrationNo(bulkDlistBase.getRegistrationNo());
						finalAuditTrack.setStatus(Constants.STATUS_CA_PENDING);
						finalAuditTrack.setToEmail(profile.getHodEmail());

						if (bulkdlistService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
							log.info("{} submitted successfully.", bulkDlistBase.getRegistrationNo());
							responseBean.setStatus(
									"Request submitted successfully and forwarded to Reporting/Nodal/Forwarding Officer ("
											+ profile.getHodEmail() + ")");
							responseBean.setRegNumber(bulkDlistBase.getRegistrationNo());
						} else {
							log.debug("Something went wrong. Please try again after sometime.");
							responseBean.setStatus("Something went wrong. Please try again after sometime.");
							responseBean.setRegNumber("");
						}
					}
				} else {
					status.setRegistrationNo(bulkDlistBase.getRegistrationNo());
					status.setRecipientType(Constants.STATUS_USER_TYPE);
					status.setStatus(Constants.STATUS_MANUAL_UPLOAD);
					status.setRecipient(email);

					finalAuditTrack.setRegistrationNo(bulkDlistBase.getRegistrationNo());
					finalAuditTrack.setStatus(Constants.STATUS_MANUAL_UPLOAD);
					finalAuditTrack.setToEmail(email);

					if (bulkdlistService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
						log.info("{} submitted successfully.", bulkDlistBase.getRegistrationNo());
						responseBean.setStatus(
								"Request submitted successfully but it is pending with you only as you selected manual upload option to submit the request.");
						responseBean.setRegNumber(bulkDlistBase.getRegistrationNo());
					} 
					else {
						log.debug("Something went wrong. Please try again after sometime.");
						responseBean.setStatus("Something went wrong. Please try again after sometime.");
						responseBean.setRegNumber("");
					}
				}
			} 
			else {
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
		}else {
			responseBean.setErrors(map);
			responseBean.setRegNumber("");
			responseBean.setStatus("Application could not be submitted.");
		}
		return responseBean;
	}

	@RequestMapping(value = "/validateRequest")
	public Map<String,Object> validateRequest(@Valid @RequestBody PreviewFormBean previewFormBean) {
		log.info("validate the request");
		Map<String,Object> map = new HashMap<>();
		Map<String,Object> finalmap = new HashMap<>();
		OrganizationDto organizationDetails = new OrganizationDto();

		BeanUtils.copyProperties(previewFormBean, organizationDetails);
		String organizationValidationResponse = utilityService.validateOrganization(organizationDetails);
		ObjectMapper mapper = new ObjectMapper();
		ErrorResponseForOrganizationValidationDto orgError = null;
		
		if(organizationValidationResponse != null && !organizationValidationResponse.isEmpty()) {
			log.debug("Errors in Organization");
			try {
				orgError = mapper.readValue(organizationValidationResponse, ErrorResponseForOrganizationValidationDto.class);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(orgError != null)
			map.put("orgError",orgError);
		}
		if(map.size() > 1) {
			finalmap.put("errors", map);
		}
		return map;
	}
	@PostMapping("/validatefile")
	public Map<String,Object> validatefile(@RequestParam("infile") @NotEmpty List infile) {
		Map<String,Object> error = new HashMap<>();
	
		if (infile.isEmpty()) {
			error.put("error:", "Please Upload excel file:");
		}
		return error;
	}

	@PostMapping("/validate")
	public Map<String,Object> validate(@RequestParam("infile") @NotEmpty List infile, @RequestParam("tnc") @NotEmpty Boolean tnc) {
		Map<String,Object> error = new HashMap<>();
	
		if (infile.isEmpty()) {
			error.put("error:", "Please Upload excel file:");
		}
		
		if (!tnc) {
			log.debug("Terms and condition is not selected.");
			error.put("tncError", "Please select Terms and Condition to proceed.");
		}
		
		return error;
	}

	@RequestMapping(value = "/approve")
	public ResponseBean approve(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, @RequestParam @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {
		log.info("Bulk Dlist form approve by user" +email);
		responseBean.setRequestType("Approval of request by user as it was submitted manually");
		String formType = "bulkdlist";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		BulkDlistBase bulkdlistBase = bulkdlistService.preview(regNumber);

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, bulkdlistBase.getMobile(), bulkdlistBase.getName(), "user");

		finalAuditTrack = utilityService.fetchFinalAuditTrack(regNumber);

		if (utilityService.isNicEmployee(email)) {
			status.setRegistrationNo(regNumber);
			status.setRecipientType(Constants.STATUS_CA_TYPE);
			status.setStatus(Constants.STATUS_CA_PENDING);
			status.setRecipient(bulkdlistBase.getHodEmail());

			finalAuditTrack.setStatus(Constants.STATUS_CA_PENDING);
			finalAuditTrack.setToEmail(bulkdlistBase.getHodEmail());

			if (bulkdlistService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
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
			status.setRecipient(bulkdlistBase.getHodEmail());

			finalAuditTrack.setStatus(Constants.STATUS_CA_PENDING);
			finalAuditTrack.setToEmail(bulkdlistBase.getHodEmail());

			if (bulkdlistService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
				log.info("{} submitted successfully.", regNumber);
				responseBean.setStatus(
						"Request submitted successfully and forwarded to Reporting/Nodal/Forwarding Officer ("
								+ bulkdlistBase.getHodEmail() + ")");
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
			@RequestParam("email") @NotEmpty String email, @RequestParam String remarks) {
		log.info("Bulk Dlist form reject by user" +email);
		responseBean.setRequestType("Cancellation of request by user.");
		String formType = "bulkdlist";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		BulkDlistBase dlistBase = bulkdlistService.preview(regNumber);

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, dlistBase.getMobile(), dlistBase.getName(), "user");

		finalAuditTrack = utilityService.fetchFinalAuditTrack(regNumber);

		status.setRegistrationNo(regNumber);
		status.setRecipientType("");
		status.setStatus(Constants.STATUS_USER_REJECTED);
		status.setRecipient("");

		finalAuditTrack.setStatus(Constants.STATUS_USER_REJECTED);
		finalAuditTrack.setToEmail("");

		if (bulkdlistService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
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

}