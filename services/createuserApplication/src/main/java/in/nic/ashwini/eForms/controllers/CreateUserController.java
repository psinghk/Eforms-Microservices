package in.nic.ashwini.eForms.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import in.nic.ashwini.eForms.dto.AvalableSpace;
import in.nic.ashwini.eForms.dto.ResponseBean;
import in.nic.ashwini.eForms.dto.UserBean;
import in.nic.ashwini.eForms.dto.UserForCreate;
import in.nic.ashwini.eForms.entities.CreatedUser;
import in.nic.ashwini.eForms.entities.EmailCreationTrail;
import in.nic.ashwini.eForms.repositories.EmailCreationTrailRepo;
import in.nic.ashwini.eForms.repositories.CreateUserRepo;
import in.nic.ashwini.eForms.services.CreateUserService;
import in.nic.ashwini.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequestMapping("/create-user")
@RestController
public class CreateUserController {

	@Autowired
	CreateUserService createUserService;
	@Autowired
	Util utilityService;
	@Autowired
	EmailCreationTrailRepo emailCreationTrailRepo;
	@Autowired
	CreateUserRepo userForCreateRepo;

	@RequestMapping("/getCSVData")
	public Map<String, Object> getAttributesCSV(@RequestBody MultipartFile file)
			throws UnsupportedEncodingException, IOException {

		System.out.println("Inside of get csv data :::::::::::::: ");
		Map<String, Object> finalmap = new HashMap<>();

		BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));

		CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT);

		Iterable<CSVRecord> csvRecords = csvParser.getRecords();
		List<UserBean> validusers = new ArrayList<UserBean>();
		List<UserBean> invalidusers = new ArrayList<>();
		List<UserBean> alreadyexists = new ArrayList<>();
		List<List<Map<String, Object>>> errors = new ArrayList<>();
		for (CSVRecord csvRecord : csvRecords) {
			UserBean userbean = new UserBean();
			if (csvRecord.getRecordNumber() != 1) {

				userbean = utilityService.initializebean(csvRecord, userbean);
				List<Map<String, Object>> err = createUserService.validations(userbean, csvRecord.getRecordNumber());
				Map<String, Object> maperr = err.get(0);

				if (maperr.isEmpty()) {
					userbean.setUsername(csvRecord.get(0));
					userbean.setFirstName(csvRecord.get(1));
					userbean.setLastName(csvRecord.get(2));
					userbean.setPassword(csvRecord.get(3));
					userbean.setAccount_type(csvRecord.get(4));
					userbean.setMobile(csvRecord.get(5));
					userbean.setDob(csvRecord.get(6));
					userbean.setDor(csvRecord.get(7));
					userbean.setDesignation(csvRecord.get(8));
					userbean.setDepartment(csvRecord.get(9));
					userbean.setState(csvRecord.get(10));
					userbean.setEmpcode(csvRecord.get(11));
					userbean.setEmail(csvRecord.get(12));

					Boolean val = utilityService.isGovEmployee(csvRecord.get(12));
					if (val) {
						// add to already exists
						alreadyexists.add(userbean);

					} else {
						validusers.add(userbean);
					}
				} else {
					userbean.setUsername(csvRecord.get(0));
					userbean.setFirstName(csvRecord.get(1));
					userbean.setLastName(csvRecord.get(2));
					userbean.setPassword(csvRecord.get(3));
					userbean.setAccount_type(csvRecord.get(4));
					userbean.setMobile(csvRecord.get(5));
					userbean.setDob(csvRecord.get(6));
					userbean.setDor(csvRecord.get(7));
					userbean.setDesignation(csvRecord.get(8));
					userbean.setDepartment(csvRecord.get(9));
					userbean.setState(csvRecord.get(10));
					userbean.setEmpcode(csvRecord.get(11));
					userbean.setEmail(csvRecord.get(12));
					invalidusers.add(userbean);
					errors.add(err);
				}
			}
		}
		finalmap.put("validusers", validusers);
		finalmap.put("invalidusers", invalidusers);
		finalmap.put("alreadyexists", alreadyexists);
		finalmap.put("error", errors);

		csvParser.close();
		fileReader.close();

		return finalmap;
	}

	@RequestMapping("/getPoValues")
	public List<Object> getPo() {
		System.out.println("getPo values are");
		// log.debug("Fetching BOs for base : {}", o);
		String o = "o=nic.in,dc=nic,dc=in";
		// o = "Application Services";
		List<Object> pos = new ArrayList<>();
		pos = utilityService.fetchPO(o);
		System.out.println("size of the list" + pos.size());
		return pos;
	}

	@RequestMapping("/getBoValues")
	public List<Object> getBo(@RequestParam("dn") String dn) {
		System.out.println("getPo values are");
		// String o="o=nic.in,dc=nic,dc=in";
		List<Object> pos = new ArrayList<>();
		pos = utilityService.fetchBos(dn);
		System.out.println("size of the list" + pos.size());
		return pos;
	}

	@RequestMapping("/getAvalableAccountCount")
	public List<Object> getAvalableAccountCount(@RequestParam("bo") String bo) {
		System.out.println("getCounts are");
		List<Object> count = new ArrayList<>();
		// count = utilityService.fetchCount(bo);
		System.out.println("size of the list" + count.size());
		return count;
	}

	@RequestMapping("/createUserId")
	public Map<String, List<UserBean>> createUserId(@RequestBody List<UserBean> userbean, @RequestParam("po") String po,
			@RequestParam("bo") String bo, @RequestParam("clientIp") String ip, @RequestParam("email") String email,
			@RequestParam("formtype") String formtype) {
		EmailCreationTrail creationTrail = new EmailCreationTrail();
		List<UserBean> invalid = new ArrayList<>();
		List<UserBean> valid = new ArrayList<>();
		// List<UserForCreate> userforcreate=new ArrayList<>();

		Map<String, List<UserBean>> response = new HashMap<>();
		Boolean val = false;
		for (UserBean ubean : userbean) {
			val = utilityService.isGovEmployee(ubean.getEmail());
			if (val) {
				invalid.add(ubean);
			}

			else {
				valid.add(ubean);
				Boolean retval = false;
				UserForCreate userForCreate = new UserForCreate();
				userForCreate.setFirstName(ubean.getFirstName());
				userForCreate.setLastName(ubean.getLastName());
				userForCreate.setUsername(ubean.getUsername());
				// if (formtype.equals("createAuthUser")) {

				// } else {
				userForCreate.setNicDateOfBirth(ubean.getDob() + "00:00:00");
				userForCreate.setNicDateOfRetirement(ubean.getDor() + "00:00:00");
				// }
				userForCreate.setState(ubean.getState());
				userForCreate.setEmployeeCode(ubean.getEmpcode());
				userForCreate.setMobile(ubean.getMobile());
				userForCreate.setEmail(ubean.getEmail());

				if (formtype.equalsIgnoreCase("createMailUser")) {

					// with mail box
					retval = utilityService.createMailUsers(userForCreate, po, bo);
				} else {
					// For createApplicationUser
					// without mail box
					retval = utilityService.createAppUsers(userForCreate, po, bo);
				}
				if (retval) {
					creationTrail.setCreatedEmail(ubean.getEmail());
					creationTrail.setCreaterEmail(email);
					creationTrail.setDateOfExpiry(Timestamp.valueOf(ubean.getDor()));
					creationTrail.setIp(ip);
					creationTrail.setFormType(formtype);
					emailCreationTrailRepo.save(creationTrail);

				}
			}
		}

		response.put("valid", valid);
		response.put("invalid-already-created", invalid);

		// creation of id

		return response;
	}

	@RequestMapping("/createAuthUser")
	public Map<String, List<UserBean>> createAuthUser(@RequestParam("clientIp") String ip,
			@RequestParam("email") String email, @RequestParam("authId") String authId,
			@RequestParam("password") String password, @RequestParam("confirmPassword") String confirmPassword,
			@RequestParam("remarks") String remarks) {
		final String formType = "CreateAuthUser";
		EmailCreationTrail creationTrail = new EmailCreationTrail();
		CreatedUser createUser = new CreatedUser();
		List<UserBean> invalid = new ArrayList<>();
		List<UserBean> valid = new ArrayList<>();
		boolean retval = false;
		Map<String, List<UserBean>> response = new HashMap<>();
		Map<String, Object> respons = new HashMap<>();
		Boolean val = false;
		UserForCreate userForCreate = new UserForCreate();
		userForCreate.setDisplayName(
				"uid=" + authId + ",ou=People,o=inoc services,o=Application Services,o=nic.in,dc=nic,dc=in");
		userForCreate.setUsername(authId);
		userForCreate.setPassword(password);

		if (!password.matches("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,100})")) {
			System.out.println("CREATEUSER: " + "passwd in wrng format");
			respons.put("Error",
					"The password you entered does not follow the password creation policy. Please try again.");

		}
		retval = utilityService.createAppUsers(userForCreate, "", "");

		response.put("valid", valid);
		response.put("invalid-already-created", invalid);

		if (retval) {
			creationTrail.setCreatedEmail(authId);
			creationTrail.setCreaterEmail(email);
			creationTrail.setIp(ip);
			creationTrail.setFormType(formType);
			emailCreationTrailRepo.save(creationTrail);

			createUser.setUserType("AUTH USER");
			createUser.setUid(authId);
			createUser.setCreatedBy(email);
			createUser.setIp(ip);
			createUser.setRemarks(remarks);
			userForCreateRepo.save(createUser);
		}

		return response;
	}

	@RequestMapping("/checkAvailableEmailOrSpace")
	public Map<String, Object> checkAvailableEmailOrSpace(@RequestParam("bo") String bo) {
		Map<String, Object> responce = new HashMap<String, Object>();
		try {
			List<AvalableSpace> count = utilityService.fetchCount(bo);
			System.out.println("sun AvailableService" + count.get(0).getSunAvailableServices());
			String[] ss = count.get(0).getSunAvailableServices().split(":");
			for (String s : ss) {
				System.out.println("::::::::Value :::::: " + s);
			}

			System.out.println("Main space " + Integer.parseInt(ss[1]));
			System.out.println("availabale space " + Integer.parseInt(ss[2]));
			// System.out.println("space " + Integer.parseInt(ss[1]) -
			// Integer.parseInt(ss[2]));
			int available_space = Integer.parseInt(ss[1]) - Integer.parseInt(ss[2]);
			if (available_space >= 0) {
				responce.put("Tc", available_space);
				responce.put("ts", ss[1]);

			} else {
				responce.put("Error", "No available space found for creating Email corresponding This Bo = " + bo
						+ " Please change BO or contact to team");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return responce;
	}
	@RequestMapping("/checkCsvFormate")
	public Map<String, Object> checkCsvFormate(@RequestParam("fileName") String fileName) {
		CommenController commenController = new CommenController();
		Map<String, Object> responce =  new HashMap<>();
	    boolean file = 	commenController.isFilenameValid(fileName);
		if(file) {
			responce.put("success", "200");
		}else {
			responce.put("Error", "File Extension not in correct format");
		}
		return responce;
	}
}
