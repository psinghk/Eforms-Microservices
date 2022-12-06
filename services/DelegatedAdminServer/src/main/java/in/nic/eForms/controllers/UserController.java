package in.nic.eForms.controllers;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.nic.eForms.models.PersonalDetailsBean;
import in.nic.eForms.models.PreviewFormBean;
import in.nic.eForms.models.Quota;
import in.nic.eForms.models.ResponseBean;
import in.nic.eForms.services.CreateAccountThrowFileUploadService;
import in.nic.eForms.services.DelegatedAdminService;
import in.nic.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequestMapping("/delegated-admin")
@RestController
public class UserController {
	private final DelegatedAdminService delegatedAdminService;
	private final CreateAccountThrowFileUploadService createAccountThrowFileUploadService;

	@Autowired
	public UserController(DelegatedAdminService delegatedAdminService,
			CreateAccountThrowFileUploadService createAccountThrowFileUploadService) {
		super();
		this.delegatedAdminService = delegatedAdminService;
		this.createAccountThrowFileUploadService = createAccountThrowFileUploadService;
	}

	@RequestMapping(value = "/getDomainFromLdap")
	public List<String> getDomainFromLdap(@Valid @RequestParam String bo) throws ParseException {
		return delegatedAdminService.DomainFromLdap(bo);
	}

	@RequestMapping(value = "/getServicePackageFromLdap")
	public List<Quota> getServicePackageFromLdap(@Valid @RequestParam String bo) throws ParseException {
		return delegatedAdminService.fetchServicePackageFromLdap(bo);
	}

	@RequestMapping(value = "/getSearchUserInCompleteRepositoryFromLdap")
	public String getSearchUserInCompleteRepositoryFromLdap(@Valid @RequestParam String email) throws ParseException {
		return delegatedAdminService.getSearchUserInCompleteRepositoryFromLdap(email);
	}

	@PostMapping("/createAccountThroughFileUpload")
	public ResponseBean submitRequest(@Valid @ModelAttribute("uploadfiles") PreviewFormBean previewFormBean,
			@RequestParam("email") String email) throws ParseException {
		return createAccountThrowFileUploadService.submitRequest(previewFormBean, email);
	}

	                                                              /* ----------- Author: Sachin Malik, Start Work Date : 18-09-2021 ------------------ */
	
	/* Edit Personal Details Of User, Created By : Sachin Malik on 18-09-2021 */
	@RequestMapping(value = "/getEditPersonalDetailsFromLdap")
	public String getEditPersonalDetails(@Valid @RequestParam String uid, @RequestBody PersonalDetailsBean newJsonData, @RequestParam("clientIp") String ip,
			@RequestParam("email") String email) throws Exception {
		    log.info("Updating date of expiry for user : "+ uid +" with : "+ newJsonData +" by : "+ email );
			return delegatedAdminService.editProfile(uid, newJsonData, email, ip);
	}

	/* Reset/Update Account Password Of User, Created By : Sachin Malik on 21-09-2021 */
	@PostMapping(value = "/resetPassword", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> changePassword(@RequestParam @NotBlank final String userId, @RequestParam @NotBlank final String remark, @RequestParam("email") String loginEmail, @RequestParam("clientIp") String clientIP) throws Exception{
		log.info("Delegated Admin : Reset Password for User : " + userId + " Remarks :" + remark);
		String body = delegatedAdminService.resetPassword( userId, remark, loginEmail, clientIP);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("key1", "value1");
		return new ResponseEntity<String>(body, httpHeaders, HttpStatus.OK);
	}
	
	/* Update Account Date Of Expiry Of User, Created By : Sachin Malik on 22-09-2021 */
	@RequestMapping(value = "/updateDateOfExpiry")
	public String updateDateOfExpiry(@Valid @RequestParam String uid , @Valid @RequestParam String dateOfExpiry , @Valid @RequestParam String remarks, @Valid @RequestParam("email") String loginEmail, @Valid @RequestParam("clientIp") String clientIP) throws Exception {
		log.info("Delegated Admin : Updating date of expiry for user "+ uid +" by : "+loginEmail );	
		if (dateOfExpiry == null) {
			log.info("Delegated Admin : Updating date of expiry : But Expiry date is missing");
			return "Expiry date is missing";
		}
		if (dateOfExpiry.isEmpty()) {
			log.info("Delegated Admin : Updating date of expiry : But Expiry date is blank");
			return "Expiry date can not be blank";
		}
		return delegatedAdminService.updateDateOfExpiry(uid, dateOfExpiry, remarks, loginEmail, clientIP); 
	}
	
	/* Delete User Account, Created By : Sachin Malik on 22-09-2021 */
	@RequestMapping(value = "/deleteUserAccount")
	public String deleteUser(@Valid @RequestParam String uid , @Valid @RequestParam String remark , @Valid @RequestParam("email") String loginEmail, @Valid @RequestParam("clientIp") String clientIP) throws Exception {
		log.info("Delegated Admin : Delete Single User Account, User : "+ uid +" by : "+loginEmail );	
		if (uid == null) {
			log.info("Delegated Admin : Delete User Account : But User Id is missing");
			return "User id is missing";
		}
		if (uid.isEmpty()) {
			log.info("Delegated Admin : Delete User Account : But User Id is blank");
			return "User id can not be blank";
		}
		return delegatedAdminService.deleteUserAccount(uid, remark, loginEmail, clientIP); 
	}
	
	/* Move User Account To Retired BO, Created By : Sachin Malik on 24-09-2021 */
	@RequestMapping(value = "/moveToRetiredBoUserAccount")
	public String moveToRetiredBo(@Valid @RequestParam String uid ,@Valid @RequestParam String remarks , @Valid @RequestParam("email") String loginEmail, @Valid @RequestParam("clientIp") String clientIP) throws Exception {
		log.info("Delegated Admin : Move To Retired BO, User : "+ uid +" by : "+loginEmail );	
		if (uid == null) {
			log.info("Delegated Admin : Move To Retired BO, User : But User Id is missing");
			return "User id is missing";
		}
		if (uid.isEmpty()) {
			log.info("Delegated Admin : Move To Retired BO, User : But User Id is blank");
			return "User id can not be blank";
		}
		return delegatedAdminService.moveToRetiredBoUserAccount(uid, remarks, loginEmail, clientIP); 
	}
	
	/* Add Alias on User Account, Created By : Sachin Malik on 24-09-2021 */
	@RequestMapping(value = "/addAliasOnUserAccount")
	public String addAlias(@Valid @RequestParam String uid , @Valid @RequestParam String alias , @Valid @RequestParam("email") String loginEmail, @Valid @RequestParam("clientIp") String clientIP) throws Exception {
		log.info("Delegated Admin : Add Alias, User : "+ uid +" by : "+loginEmail );	
		if (uid == null) {
			log.info("Delegated Admin : Add Alies, User : But User Id is missing");
			return "User id is missing";
		}
		if (uid.isEmpty()) {
			log.info("Delegated Admin : Add Alies, User : But User Id is blank");
			return "User id can not be blank";
		}
		return delegatedAdminService.addAliasOnUserAccount(uid, alias, loginEmail, clientIP); 
	}
	
	/* Deactivate User Account, Created By : Sachin Malik on 28-09-2021 */
	@RequestMapping(value = "/Deactivate")
	public String Deactivate(@Valid @RequestParam String uid ,@Valid @RequestParam String remarks , @Valid @RequestParam("email") String loginEmail, @Valid @RequestParam("clientIp") String clientIP) throws Exception {
		log.info("Delegated Admin : De-Activate, User : "+ uid +" by : "+loginEmail );	
		if (uid == null) {
			log.info("Delegated Admin : De-Activate, User : But User Id is missing");
			return "User id is missing";
		}
		if (uid.isEmpty()) {
			log.info("Delegated Admin : De-Activate, User : But User Id is blank");
			return "User id can not be blank";
		}
		return delegatedAdminService.deactivateUserAccount(uid, remarks, loginEmail, clientIP); 
	}
	
	/* Exchange with Primary Equivalent, Created By : Sachin Malik on 28-09-2021 */
	@RequestMapping(value = "/exchangePrimaryWithEquivalent")
	public String ExchangePrimaryWithEquivalent(@Valid @RequestParam String uid ,@Valid @RequestParam String newPrimaryEquivalent ,@Valid @RequestParam String remarks, @Valid @RequestParam("email") String loginEmail, @Valid @RequestParam("clientIp") String clientIP) throws Exception {
		log.info("Delegated Admin : Exchange with Primary Equivalent, User : "+ uid +" by : "+loginEmail );	
		if (uid == null) {
			log.info("Delegated Admin : Exchange with Primary Equivalent, User : But User Id is missing");
			return "User id is missing";
		}
		if (uid.isEmpty()) {
			log.info("Delegated Admin : Exchange with Primary Equivalent, User : But User Id is blank");
			return "User id can not be blank";
		}
		return delegatedAdminService.exchangePrimaryWithEquivalent(uid, newPrimaryEquivalent, remarks, loginEmail, clientIP); 
	}
	
	/* Swap to supportgov/govcontractor, Created By : Sachin Malik on 29-09-2021 */
	@RequestMapping(value = "/swapSupportContractor")
	public String SwapToSupportgovGovcontractor(@Valid @RequestParam String uid ,@Valid @RequestParam String newDominForSwap, @Valid @RequestParam("email") String loginEmail, @Valid @RequestParam("clientIp") String clientIP) throws Exception {
		log.info("Delegated Admin :  Swap to supportgov/govcontractor, User : "+ uid +" by : "+loginEmail );	
		if (uid == null) {
			log.info("Delegated Admin :  Swap to supportgov/govcontractor, User : But User Id is missing");
			return "User id is missing";
		}
		if (uid.isEmpty()) {
			log.info("Delegated Admin :  Swap to Supportgov/Govcontractor, User : But User Id is blank");
			return "User id can not be blank";
		}
		return delegatedAdminService.swapDomainInPrimary(uid, newDominForSwap, loginEmail, clientIP); 
	}
	
	/* Change IMAP/POP, Created By : Sachin Malik on 29-09-2021 */
	@RequestMapping(value = "/changeIMAPandPOP")
	public String ChangeIMAPandPOP(@Valid @RequestParam String uid, @Valid @RequestParam String imapPOP,@Valid @RequestParam String remarks, @Valid @RequestParam("email") String loginEmail, @Valid @RequestParam("clientIp") String clientIP) throws Exception {
		log.info("Delegated Admin :  Change IMAP/POP, User : "+ uid +" by : "+loginEmail );	
		if (uid == null) {
			log.info("Delegated Admin :  Change IMAP/POP, User : But User Id is missing");
			return "User id is missing";
		}
		if (uid.isEmpty()) {
			log.info("Delegated Admin :  Change IMAP/POP, User : But User Id is blank");
			return "User id can not be blank";
		}
		return delegatedAdminService.changeIMAPandPOP(uid, imapPOP, remarks, loginEmail, clientIP); 
	}

}
