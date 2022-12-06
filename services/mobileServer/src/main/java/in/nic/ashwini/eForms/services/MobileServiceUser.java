package in.nic.ashwini.eForms.services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.nic.ashwini.eForms.controllers.DocsController;
import in.nic.ashwini.eForms.entities.MobileBase;
import in.nic.ashwini.eForms.entities.UpdateMobileOtp;
import in.nic.ashwini.eForms.models.ErrorResponseForOrganizationValidationDto;
import in.nic.ashwini.eForms.models.FinalAuditTrack;
import in.nic.ashwini.eForms.models.HodDetailsDto;
import in.nic.ashwini.eForms.models.NextHopBean;
import in.nic.ashwini.eForms.models.OrganizationDto;
import in.nic.ashwini.eForms.models.PreviewFormBean;
import in.nic.ashwini.eForms.models.ProfileDto;
import in.nic.ashwini.eForms.models.ResponseBean;
import in.nic.ashwini.eForms.models.Status;
import in.nic.ashwini.eForms.models.UploadMultipleFilesBean;
import in.nic.ashwini.eForms.repositories.MobileBaseRepo;
import in.nic.ashwini.eForms.repositories.MobileEmpCoordRepo;
import in.nic.ashwini.eForms.repositories.UpdateMobileOtpRepo;
import in.nic.ashwini.eForms.utils.Constants;
import in.nic.ashwini.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MobileServiceUser {

	private final MobileEmpCoordRepo mobileCoordRepo;
	private final Util utilityService;
	private NextHopBean nextHopBean;
	private final UpdateMobileOtpRepo updateMobileRepo;
	private final ValidationService validationService;
	private final MobileBaseRepo mobileBaseRepo;
	private final DocsController docController;

	@Autowired
	public MobileServiceUser(Util utilityService, UpdateMobileOtpRepo updateMobileRepo,
			ValidationService validationService, MobileBaseRepo mobileBaseRepo, MobileEmpCoordRepo mobileCoordRepo,DocsController docController) {
		super();
		this.mobileCoordRepo = mobileCoordRepo;
		this.mobileBaseRepo = mobileBaseRepo;
		this.utilityService = utilityService;
		this.updateMobileRepo = updateMobileRepo;
		this.validationService = validationService;
		this.docController = docController;
	}

	public ResponseBean OtpGenerateNewmobile(String mobile, String newMobile, String countryCode,
			ResponseBean responseBean) {

		Map<String, Object> map = new HashMap<>();
		UpdateMobileOtp updateMobile = new UpdateMobileOtp();
		if ((!newMobile.contains("+91")) && (countryCode.equals("+91")) && (newMobile.matches("^[+0-9]{10}$"))) {
			newMobile = countryCode + newMobile;
		}

		String newMobile1 = "";
		newMobile1 = countryCode + newMobile;
		boolean mobileInldap = utilityService.IsMobileAvailableInLdap(mobile, newMobile1);

		if (mobileInldap) {
			map.put("mobileError", "please exist in ldap please use another number");
			responseBean.setErrors(map);
			responseBean.setOtp(null);
			responseBean.setRequestType(null);
			responseBean.setStatus(null);
		} else {
			responseBean.setErrors(null);

			String fetch = updateMobileRepo.fetchOtp(newMobile);

			if (fetch == null) {

				LocalDateTime genTime = LocalDateTime.now();

				LocalDateTime expTime = LocalDateTime.now().plusMinutes(30);

				System.out.println("After formatting: " + genTime);
				System.out.println("After formatting: " + expTime);
				Integer otp = 0;
				otp = utilityService.random();
				System.out.println("OTP::::::" + otp);
				responseBean.setOtp(otp);
				responseBean.setStatus("otp sent to new mobile :" + newMobile);
				updateMobile.setOtp(otp);
				updateMobile.setMobile(newMobile);
				updateMobile.setGenerationTimeStamp(genTime);
				updateMobile.setExpiryTimeStamp(expTime);

				updateMobileRepo.save(updateMobile);

			} else {
				responseBean.setStatus("use last otp sent to you it is still valid");
			}
		}
		return responseBean;

	}

	public ResponseBean submitRequest(PreviewFormBean previewFormBean, String ip, String email, String submissionType,
			ResponseBean responseBean) {

		String formType = "mobile";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		ProfileDto profile = utilityService.fetchProfileByEmailInBean(email);
		responseBean.setRequestType("Submission of request");
		Map<String, Object> map = validateRequest(previewFormBean);
		System.out.println("Map Size:::: " + map.size());
		System.out.println("Map:::: " + map);
		if (map.size() == 0) {
			responseBean.setErrors(null);
			if (profile != null) {
				status = utilityService.initializeStatusTable(ip, email, formType, previewFormBean.getRemarks(),
						profile.getMobile(), profile.getName(), "user");
				finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType,
						previewFormBean.getRemarks(), profile.getMobile(), profile.getName(), "user", "");

				ModelMapper modelMapper = new ModelMapper();
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

				MobileBase mobileBase = modelMapper.map(profile, MobileBase.class);
				mobileBase.setPdfPath(submissionType);
				BeanUtils.copyProperties(previewFormBean, mobileBase);
				LocalDateTime currentTime = LocalDateTime.now();
				mobileBase.setDatetime(currentTime);
				// mobileBase.setLastUpdationDateTime(currentTime);
				mobileBase.setUserip(ip);

				for (int i = 0; i < 4; i++) {
					mobileBase = insert(mobileBase);
					if (mobileBase.getId() > 0) {
						break;
					}
				}

				if (mobileBase.getId() > 0) {
					if (submissionType.equalsIgnoreCase("online") || submissionType.equalsIgnoreCase("esign")) {
						if (utilityService.isNicEmployee(email)) {
							status.setRegistrationNo(mobileBase.getRegistrationNo());
							status.setRecipientType(Constants.STATUS_ADMIN_TYPE);
							status.setStatus(Constants.STATUS_MAILADMIN_PENDING);
							status.setRecipient(Constants.MAILADMIN_EMAIL);

							finalAuditTrack.setRegistrationNo(mobileBase.getRegistrationNo());
							finalAuditTrack.setStatus(Constants.STATUS_MAILADMIN_PENDING);
							finalAuditTrack.setToEmail(profile.getHodEmail());

							if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
								log.info("{} submitted successfully.", mobileBase.getRegistrationNo());
								responseBean.setStatus("Request submitted successfully and forwarded to Admin ("
										+ Constants.MAILADMIN_EMAIL + ")");
								responseBean.setRegNumber(mobileBase.getRegistrationNo());
							} else {
								log.debug("Something went wrong. Please try again after sometime.");
								responseBean.setStatus("Something went wrong. Please try again after sometime.");
								responseBean.setRegNumber("");
							}
						} else {
							status.setRegistrationNo(mobileBase.getRegistrationNo());
							status.setRecipientType(Constants.STATUS_CA_TYPE);
							status.setStatus(Constants.STATUS_CA_PENDING);
							status.setRecipient(profile.getHodEmail());

							finalAuditTrack.setRegistrationNo(mobileBase.getRegistrationNo());
							finalAuditTrack.setStatus(Constants.STATUS_CA_PENDING);
							finalAuditTrack.setToEmail(profile.getHodEmail());

							if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
								log.info("{} submitted successfully.", mobileBase.getRegistrationNo());
								responseBean.setStatus(
										"Request submitted successfully and forwarded to Reporting/Nodal/Forwarding Officer ("
												+ profile.getHodEmail() + ")");
								responseBean.setRegNumber(mobileBase.getRegistrationNo());
							} else {
								log.debug("Something went wrong. Please try again after sometime.");
								responseBean.setStatus("Something went wrong. Please try again after sometime.");
								responseBean.setRegNumber("");
							}
						}
					} else {
						status.setRegistrationNo(mobileBase.getRegistrationNo());
						status.setRecipientType(Constants.STATUS_USER_TYPE);
						status.setStatus(Constants.STATUS_MANUAL_UPLOAD);
						status.setRecipient(email);

						finalAuditTrack.setRegistrationNo(mobileBase.getRegistrationNo());
						finalAuditTrack.setStatus(Constants.STATUS_MANUAL_UPLOAD);
						finalAuditTrack.setToEmail(email);

						if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
							log.info("{} submitted successfully.", mobileBase.getRegistrationNo());
							responseBean.setStatus(
									"Request submitted successfully but it is pending with you only as you selected manual upload option to submit the request.");
							responseBean.setRegNumber(mobileBase.getRegistrationNo());
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

	public Map<String, Object> validateRequest(PreviewFormBean previewFormBean) {
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> finalmap = new HashMap<>();
		OrganizationDto organizationDetails = new OrganizationDto();

		boolean mobile_error = validationService.UpdateMobileValidation(previewFormBean.getNew_mobile(),
				previewFormBean.getCountry_code());

		if (mobile_error == true) {
			log.debug("please enter correct mobile number");
			map.put("mobError", "please enter mobile in correct format");
		}

//		if (previewFormBean.getRemarks().isEmpty()) {
//			log.debug("please enter the remarks");
//			map.put("remarksError", "please enter a valid remarks");
//		}

		if (previewFormBean.getRemarks().equalsIgnoreCase("others")) {

			if (previewFormBean.getOther_remarks().isEmpty()) {
				log.debug("please enter a valid reason ");
				map.put("otherRemarksError", "please enter a valid reason");
			}
		}
		if (!previewFormBean.getTnc()) {
			log.debug("Terms and condition is not selected.");
			map.put("tncError", "Please select Terms and Condition to proceed.");
		}
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
				// TODO Auto-generated catch block
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
		String formType = "mobile";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		MobileBase mobileBase = preview(regNumber);

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, mobileBase.getMobile(),
				mobileBase.getName(), "user");

		finalAuditTrack = utilityService.fetchFinalAuditTrack(regNumber);

		if (utilityService.isNicEmployee(email)) {
			status.setRegistrationNo(regNumber);
			status.setRecipientType(Constants.STATUS_ADMIN_TYPE);
			status.setStatus(Constants.STATUS_MAILADMIN_PENDING);
			status.setRecipient(Constants.MAILADMIN_EMAIL);

			finalAuditTrack.setStatus(Constants.STATUS_MAILADMIN_PENDING);
			finalAuditTrack.setToEmail(Constants.MAILADMIN_EMAIL);

			if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
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
			status.setRecipient(mobileBase.getHodEmail());

			finalAuditTrack.setStatus(Constants.STATUS_CA_PENDING);
			finalAuditTrack.setToEmail(mobileBase.getHodEmail());

			if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
				log.info("{} submitted successfully.", regNumber);
				responseBean.setStatus(
						"Request submitted successfully and forwarded to Reporting/Nodal/Forwarding Officer ("
								+ mobileBase.getHodEmail() + ")");
				responseBean.setRegNumber(regNumber);
			} else {
				log.debug("Something went wrong. Please try again after sometime.");
				responseBean.setStatus("Something went wrong. Please try again after sometime.");
				responseBean.setRegNumber("");
			}
		}
		return responseBean;
	}

	public ResponseBean reject(String regNumber, String ip, String email, String remarks, ResponseBean responseBean) {
		responseBean.setRequestType("Cancellation of request by user.");
		String formType = "mobile";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		MobileBase mobileBase = preview(regNumber);

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, mobileBase.getMobile(),
				mobileBase.getName(), "user");

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

	public Map<String, Object> validate(String countryCode, String newMobile, String otp) {
		Map<String, Object> error = new HashMap<>();

		String otp1 = fetchOtp(newMobile);

		if (!otp.equals(otp1)) {
			log.debug("please enter correct otp");
			error.put("otpError", "please enter correct otp");
		}
		return error;
	}

	public NextHopBean findNextHop(PreviewFormBean previewFormBean, String ip, String email, String submissionType) {

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

	public String fetchOtp(String mobile) {

		String fetchOtp = updateMobileRepo.findByMobile(mobile);
		return fetchOtp;
	}

	@Transactional
	public MobileBase insert(MobileBase mobileBase) {
		if (mobileBase != null) {
			Date date = new Date();
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			String pdate = dateFormat.format(date);
			String oldRegNumber = mobileBaseRepo.findLatestRegistrationNo();
			String newRegNumber = "MOBILE-FORM" + pdate;
			if (oldRegNumber == null || oldRegNumber.isEmpty()) {
				newRegNumber += "0001";
			} else {
				String lastst = oldRegNumber.substring(19, oldRegNumber.length());
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
			mobileBase.setRegistrationNo(newRegNumber);
			mobileBase.setSupportActionTaken("p");
			return mobileBaseRepo.save(mobileBase);
		}
		return null;
	}

	public boolean updateStatusAndFinalAuditTrack(Status status, FinalAuditTrack finalAuditTrack) {
		return utilityService.updateStatusAndFinalAuditTrack(status, finalAuditTrack);
	}

	public MobileBase preview(String regNo) {
		return mobileBaseRepo.findByRegistrationNo(regNo);
	}

	
	
	
	
	
	
	
	
	public ResponseBean approve( String regNumber, String ip,String remarks,
			 String email, UploadMultipleFilesBean uploadfiles,ResponseBean responseBean) {
	
		String formType = "mobile";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		MobileBase mobileBase = preview(regNumber);
		uploadfiles.setRegistrationNo(regNumber);
		uploadfiles.setRole("user");
		
		System.out.println("in here");
		HashMap<String, Object> upload_file_responce = new HashMap<>();
		upload_file_responce = docController.userUploadDocx(uploadfiles);
		System.out.println("upload file response::::::::: " + upload_file_responce);
		String res = upload_file_responce.get("status").toString();
		if (!res.equalsIgnoreCase("success")) {
			log.debug("Something went wrong. Please try again after sometime.");
			responseBean.setStatus("Something went wrong. Please try again after sometime.");
			responseBean.setRegNumber("");
		} else {
			
			status = utilityService.initializeStatusTable(ip, email, formType, remarks, mobileBase.getMobile(),
					mobileBase.getName(), "user");

			finalAuditTrack = utilityService.fetchFinalAuditTrack(regNumber);

			if (utilityService.isNicEmployee(email)) {
				status.setRegistrationNo(regNumber);
				status.setRecipientType(Constants.STATUS_ADMIN_TYPE);
				status.setStatus(Constants.STATUS_MAILADMIN_PENDING);
				status.setRecipient(Constants.MAILADMIN_EMAIL);

				finalAuditTrack.setStatus(Constants.STATUS_MAILADMIN_PENDING);
				finalAuditTrack.setToEmail(Constants.MAILADMIN_EMAIL);

				if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
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
				status.setRecipient(mobileBase.getHodEmail());

				finalAuditTrack.setStatus(Constants.STATUS_CA_PENDING);
				finalAuditTrack.setToEmail(mobileBase.getHodEmail());

				if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
					log.info("{} submitted successfully.", regNumber);
					responseBean.setStatus(
							"Request submitted successfully and forwarded to Reporting/Nodal/Forwarding Officer ("
									+ mobileBase.getHodEmail() + ")");
					responseBean.setRegNumber(regNumber);
				} else {
					log.debug("Something went wrong. Please try again after sometime.");
					responseBean.setStatus("Something went wrong. Please try again after sometime.");
					responseBean.setRegNumber("");
				}
			}
		
		}
		return responseBean;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
