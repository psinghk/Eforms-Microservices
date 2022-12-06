package in.nic.eForms.services;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import in.nic.eForms.entities.BulkUsers;
import in.nic.eForms.entities.DelegatedAdminBase;
import in.nic.eForms.entities.SingleEmailBase;
import in.nic.eForms.entities.UidCheck;
import in.nic.eForms.models.ErrorResponseForOrganizationValidationDto;
import in.nic.eForms.models.FileUploadPojo;
import in.nic.eForms.models.FinalAuditTrack;
import in.nic.eForms.models.OrganizationDto;
import in.nic.eForms.models.PreviewFormBean;
import in.nic.eForms.models.ProfileDto;
import in.nic.eForms.models.ResponseBean;
import in.nic.eForms.models.Status;
import in.nic.eForms.repositories.NknSingleEmpCoordRepo;
import in.nic.eForms.repositories.SingleBaseRepo;
import in.nic.eForms.repositories.UidCheckRepo;
import in.nic.eForms.utils.BulkValidation;
import in.nic.eForms.utils.Constants;
import in.nic.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CreateAccountThrowFileUploadService {
	
	@Value("${fileBasePath}")
	private String EXTERNAL_FILE_PATH;
	@Value("${fileBasePath}")
	private String fileBasePath;
	private final UidCheckRepo uidCheckRepo;
	private final Util utilityService;
	private final ResponseBean responseBean;

	@Autowired
	public CreateAccountThrowFileUploadService(UidCheckRepo uidCheckRepo,Util utilityService,ResponseBean responseBean) {
		super();
		
		this.uidCheckRepo = uidCheckRepo;
		this.utilityService = utilityService;
		this.responseBean=responseBean;

	}
	
	public List<String> DomainFromLdap(String bo) {
		List<String> domains = new ArrayList<>();
		List<String> domain = utilityService.findDomains(bo);
		if (domain.size() > 0) {
			domains.addAll(domain);
		}
		return domains;
	}
	
	
	public String getSearchUserInCompleteRepositoryFromLdap(String email) {
		 String ldapValues = utilityService.allLdapValues(email)
;
		return ldapValues;
		
	}
	
	//--------------------------------support method-----------------checkXLSXSize---------------------------------------
	
	
//	public Boolean checkCsvSize(File destFile) {
//        boolean wrongfileflag = false;
//        int RowCount = 0;
//        try {
//            InputStream inputstream = new FileInputStream(destFile);
//            //org.apache.poi.ss.usermodel.Workbook workbook = WorkbookFactory.create(inputstream);
//           org.apache.poi.ss.usermodel.Workbook work= WorkbookFactory.create(inputstream);
//            RowCount = sheet.getPhysicalNumberOfRows();
//            if (RowCount > 3000) {
//                wrongfileflag = true;
//            }
//            inputstream.close();
//        } catch (Exception e) {
//            wrongfileflag = true;
//        }
//        System.out.println("RowCount value :::" + RowCount);
//        return wrongfileflag;
//    }
	public Boolean checkCsvSize2(String destFile) {
		boolean wrongfileflag = false;
		try {
			 File file = new File(destFile);   
         FileInputStream fis = new FileInputStream(file);  
         XSSFWorkbook wb = new XSSFWorkbook(fis);
         XSSFSheet sheet = wb.getSheetAt(0); 
         int lastRow=sheet.getLastRowNum();
         
         if (lastRow > 3000) {
				wrongfileflag = true;
			}
		}
		catch(Exception e){
			wrongfileflag = true;
			System.out.println(e);
		}
		return wrongfileflag;
    }
	
	//--------------------------------support method-----------------checkCSVSize---------------------------------------
	public Boolean checkCsvSize1(File destFile) {
		boolean wrongfileflag = false;
		CSVReader reader = null;
		try {
			reader = new CSVReader(new FileReader(destFile));
			String[] line1;
			int length = 0;
			while ((line1 = reader.readNext()) != null) {
				length = length++;
			}
			if (length > 3000) {
				wrongfileflag = true;
			}
			reader.close();
		} catch (IOException e) {
			wrongfileflag = true;
			System.out.println(e.getMessage());
		} catch (CsvValidationException e) {
			wrongfileflag = true;
			System.out.println(e.getMessage());
		}
		return wrongfileflag;
	}
	
	//-------------------------------support method------------------File Nams is valid or NOT---------------------------------------
	public boolean isFileNameXLSValid(String file) {
		try {
			String fileNameWithOutExt = FilenameUtils.removeExtension(file);
			if (!fileNameWithOutExt.contains(".")) {
				if ((fileNameWithOutExt.matches("^[^*&%]+$")) && (file.endsWith(".xls") || file.endsWith(".XLS")|| file.endsWith(".xlsx"))) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
	
	//-------------------------------------support method-----------------upload----------------------csv---------------------
	public FileUploadPojo checkXLS(MultipartFile file) {
		FileUploadPojo fileUploadPojo = new FileUploadPojo();

		Date date = new Date();
		DateFormat dt = new SimpleDateFormat("ddMMyyyyHHmmss");
		String pdate = dt.format(date);
		fileUploadPojo.setFileNamePattern(pdate);
		String loc = Constants.LOCAL_FILE_LOCATION;
		String fileName = fileUploadPojo.getFileNamePattern() + ".csv";
		try {
			Files.copy(file.getInputStream(), Paths.get(loc + fileName), StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception e) {
			fileUploadPojo.setErrorMsg("Unable to Save File " + e.getMessage());
			e.getMessage();
			// we will use the exception here also.
		}
		//File savedFile = Paths.get(loc + fileName).toFile();
		File savedFile = Paths.get(loc + fileName).toFile();
		fileUploadPojo.setFileUploadName(savedFile.getName());
		System.out.println("save file="+savedFile);
//		if (!checkCsvSize(savedFile.toString())) {
//			// have to set server file download path pennnnnding.
//			fileUploadPojo.setFileUploadName(savedFile.getName());
//		} 
//		else {
//			fileUploadPojo.setErrorMsg("Upload file in .xlsx format with maxmimum 3000 rows.");
//		}
		return fileUploadPojo;

	}
public FileUploadPojo dummy(String email, FileUploadPojo fileUploadPojo) {

		
		
		ProfileDto profile = utilityService.fetchProfileByEmailInBean(email);
		System.out.println("inside dummy");
		String csvFile = Constants.LOCAL_FILE_LOCATION + fileUploadPojo.getFileUploadName();
		
		System.out.println(fileUploadPojo.getFileUploadName());

		//String xlsFile = Constants.LOCAL_FILE_LOCATION + fileUploadPojo.getFileUploadName();
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		BulkValidation bulkValidation = new BulkValidation();
		StringBuilder validContent = new StringBuilder();
		StringBuilder notValidContent = new StringBuilder();
		StringBuilder errorMsg = new StringBuilder();

		errorMsg.append("ErrorList").append("\n");

		String notValidLloc = Constants.LOCAL_FILE_LOCATION + fileUploadPojo.getFileNamePattern() + "notValid.csv";
		String validLloc = Constants.LOCAL_FILE_LOCATION + fileUploadPojo.getFileNamePattern() + "valid.csv";
		String errormsgloc = Constants.LOCAL_FILE_LOCATION + fileUploadPojo.getFileNamePattern() + "errorMsg.csv";
		List<BulkUsers> bulkUsersLIst = new ArrayList<>();

		try {

			br = new BufferedReader(new FileReader(new File(csvFile)));
			while ((line = br.readLine()) != null) {
				BulkUsers bulkUsers = new BulkUsers();
				//bulkUsers.setRegistrationno(registrationno);
				boolean check = false;
				// use comma as separator
				String[] array = line.split(cvsSplitBy);
				HashMap<String, String> temp = null;
				for (int i = 0; i < array.length; i++) {
					//
					System.out.println("hello");
					temp = bulkValidation.Fname(array[0]);
					check = Boolean.valueOf(temp.get("valid").toString());
					bulkUsers.setFname(array[0]);
					System.out.println(check + " --> " + bulkValidation.Fname(array[0]));

					if (check) {
						temp = bulkValidation.Lname(array[1]);
						check = Boolean.valueOf(temp.get("valid").toString());
						bulkUsers.setLname(array[1]);
						System.out.println(check + " --> " + bulkValidation.Lname(array[1]));

					}

					if (check) {
						temp = bulkValidation.Designation(array[2]);
						check = Boolean.valueOf(temp.get("valid").toString());
						bulkUsers.setDesignation(array[2]);
						System.out.println(check + " --> " + bulkValidation.Designation(array[2]));

					}

					if (check) {
						temp = bulkValidation.Department(array[3]);
						check = Boolean.valueOf(temp.get("valid").toString());
						bulkUsers.setDepartment(array[3]);
						System.out.println(check + " --> " + bulkValidation.Department(array[3]));

					}
					if (check) {

						temp = bulkValidation.State(array[4]);
						check = Boolean.valueOf(temp.get("valid").toString());
						bulkUsers.setState(array[4]);
						System.out.println(check + " --> " + bulkValidation.State(array[4]));

					}

					if (check) {
						temp = bulkValidation.Mobile(array[5], array[6]);
						check = Boolean.valueOf(temp.get("valid").toString());
						bulkUsers.setMobile("+" + array[6] + array[5]);
						System.out.println(check + " --> " + bulkValidation.Mobile(array[5], array[6]));

					}

					if (check) {
						temp = bulkValidation.MobileAdmin(array[6]);

						check = Boolean.valueOf(temp.get("valid").toString());

						System.out.println(check + " --> " + bulkValidation.MobileAdmin(array[6]));
					}

					if (check) {
						temp = bulkValidation.DOR(array[7]);

						check = Boolean.valueOf(temp.get("valid").toString());
						bulkUsers.setDor(array[7]);
						System.out.println(check + " --> " + bulkValidation.DOR(array[7]));
					}

					String boname = "id_name";
					//uid retrieve from the findByUid
					///utilityService.findByUid(bulkValidation.)

					/*
					 * if (check) { temp = bulkValidation.Uid(array[8], boname,
					 * bulkRegistrationService, restApi);
					 * 
					 * check = Boolean.valueOf(temp.get("valid").toString());
					 * bulkUsers.setUid(array[8]); System.out.println(check + " --> " +
					 * bulkValidation.Uid(array[8], boname, bulkRegistrationService, restApi));
					 * 
					 * }
					 * 
					 * // getting all domains //Set<String> allDomains =
					 * employmentCoordinatorService.findAllDomains(); List<String> allDomains =
					 * utilityService.fetchDomainsByCatAndMinAndDep(profile.getEmployment(),
					 * profile.getMinistry(), profile.getDepartment()); if (check) { temp =
					 * bulkValidation.Mail(array[9], array[8], allDomains, bulkRegistrationService,
					 * restApi, employmentCoordinatorService); check =
					 * Boolean.valueOf(temp.get("valid").toString()); bulkUsers.setMail(array[9]);
					 * System.out.println(bulkValidation.Mail(check + " --> " + array[9], array[8],
					 * allDomains, bulkRegistrationService, restApi, employmentCoordinatorService));
					 * 
					 * }
					 */

					if (check) {
						temp = bulkValidation.onlyUID(array[8]);
						check = Boolean.valueOf(temp.get("valid").toString());
						bulkUsers.setUid(array[8]);
						System.out.println(bulkValidation.onlyUID(array[8]));

					}

					if (check) {
						temp = bulkValidation.onlyMail(array[9]);
						check = Boolean.valueOf(temp.get("valid").toString());
						bulkUsers.setMail(array[9]);
						System.out.println(check + " --> " + bulkValidation.onlyMail(array[9]));

					}
					if (check) {
						if (array.length > 10) {
							temp = bulkValidation.DOB(array[10]);
							check = Boolean.valueOf(temp.get("valid").toString());
							bulkUsers.setDob(array[10]);
							System.out.println(check + " --> " + bulkValidation.DOB(array[10]));

						}
					}

					if (check) {
						if (array.length > 11) {
							temp = bulkValidation.EMPNUMBER(array[11]);
							check = Boolean.valueOf(temp.get("valid").toString());
							bulkUsers.setEmpcode(array[11]);
							System.out.println(check + " --> " + bulkValidation.EMPNUMBER(array[11]));
						}

					}

					if (check) {
						temp = bulkValidation.Email(array[9]);
						check = Boolean.valueOf(temp.get("valid").toString());
						bulkUsers.setMail(array[9]);
						System.out.println(check + " --> " + bulkValidation.Email(array[9]));

					}

					if (check) {
						if (array.length > 15) {
							temp = bulkValidation.Address(array[15]); // not in the list
							check = Boolean.valueOf(temp.get("valid").toString());
							System.out.println(check + " --> " + bulkValidation.Address(array[15])); // not in the list
						}

					}

					if (check) {
						temp = bulkValidation.countrycode(array[5]);
						check = Boolean.valueOf(temp.get("valid").toString());

						System.out.println(check + " --> " + bulkValidation.countrycode(array[5]));
					}
				}
				if (check) {
					validContent.append(line).append("\n");
					
					// check condition .sunny
					bulkUsers.setIscreated("n");
					bulkUsers.setIsrejected("n");
					bulkUsers.setUpdatedon(new java.sql.Date(System.currentTimeMillis()));
					bulkUsersLIst.add(bulkUsers);
				} else {
					notValidContent.append(line).append("\n");
					errorMsg.append(temp.get("errorMsg").toString()).append("\n");
				}

			}

			java.nio.file.Path filePath = Files.write(Paths.get(validLloc),
					validContent.toString().getBytes(StandardCharsets.UTF_8));
			if (Files.exists(filePath)) {
				fileUploadPojo.setFileUploadValidName(filePath.getFileName().toString());
			}

			filePath = Files.write(Paths.get(notValidLloc),
					notValidContent.toString().getBytes(StandardCharsets.UTF_8));
			if (Files.exists(filePath)) {
				fileUploadPojo.setFileUploadNotValidName(filePath.getFileName().toString());
			}

			filePath = Files.write(Paths.get(errormsgloc), errorMsg.toString().getBytes(StandardCharsets.UTF_8));
			if (Files.exists(filePath)) {
				fileUploadPojo.setFileUploadErrorName(filePath.getFileName().toString());
			}

			//bulkUserBaseRepo.saveAll(bulkUsersLIst);

			System.out.println(fileUploadPojo.toString());

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("completed dumy");
		return fileUploadPojo;
	}
	
	
	
	//-----------------------------------submit request------------------------
		public ResponseBean submitRequest(PreviewFormBean previewFormBean, String email) throws ParseException {
			ProfileDto profile = utilityService.fetchProfileByEmailInBean(email);
			ModelMapper modelMapper = new ModelMapper();
			DelegatedAdminBase delegatedAdminBase = modelMapper.map(profile, DelegatedAdminBase.class);
			Map<String, Object> map = new HashMap<>();
			responseBean.setRequestType("Submission of request");
			
			
			
			if (!previewFormBean.getType().equalsIgnoreCase("mail") && !previewFormBean.getType().equalsIgnoreCase("app")) {
				log.debug("Selected typeOfMailID is not mail or app or eoffice.");
				map.put("typeOfMailIDError", "Selected typeOfMailID is not mail or app or eoffice.");
			}
			
			// emptype=employee description
			if (!previewFormBean.getDuplicateCheck().equalsIgnoreCase("check_duplicate")
					&& !previewFormBean.getDuplicateCheck().equalsIgnoreCase("without_duplicate")) {
				log.debug("Selected employeeDescription is not emp_regular or emp_contract or consultant.");
				map.put("employeeDescriptionError",
						"Selected employeeDescription is not emp_regular or emp_contract or consultant.");
			}
			//List<String> finaldomain = new ArrayList<>();
			FileUploadPojo fileUploadPojo = null;
			
			System.out.println("Map Size:::: " + map.size());
			System.out.println("Map:::: " + map);
			
			responseBean.setErrors(null);
			if(isFileNameXLSValid(previewFormBean.getInfile().get(0).getOriginalFilename())) {
				fileUploadPojo = checkXLS(previewFormBean.getInfile().get(0));
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
				map.put("fileError", "file format not xls");
			}		
			if (map.size() != 0) {
			//if (map.size() == 0) {
				if (profile != null) {
					//BulkUsers bulkUsers = modelMapper.map(profile, BulkUsers.class);//for users
					//delegatedAdminBase.setPdfPath(submissionType);
					delegatedAdminBase.setRenamedFilepath(Constants.LOCAL_FILE_LOCATION+fileUploadPojo.getFileUploadName());
					delegatedAdminBase.setUploadedFilename(fileUploadPojo.getFileUploadName());
					BeanUtils.copyProperties(previewFormBean, delegatedAdminBase);
					
					LocalDateTime currentTime = LocalDateTime.now();
					delegatedAdminBase.setDatetime(currentTime);
				fileUploadPojo = dummy(email, fileUploadPojo);
				Map<String,Object> outputData = new HashMap<>();
				
				outputData.put("validFile",fileUploadPojo.getFileUploadValidName());
				outputData.put("errorFile", fileUploadPojo.getFileUploadErrorName());
				outputData.put("invalidFile", fileUploadPojo.getFileUploadNotValidName());
				responseBean.setData(outputData);
					{
						System.out.println("data inserted in bulk  users users"+delegatedAdminBase.getRegistrationNo());
					}
									
					if (delegatedAdminBase.getId() > 0) {} 
				} 
			}
			return responseBean; 
		
		}

}
