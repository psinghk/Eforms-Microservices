package in.nic.ashwini.eForms.services;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import in.nic.ashwini.eForms.entities.GeneratePdf;
import in.nic.ashwini.eForms.entities.UidCheck;
import in.nic.ashwini.eForms.entities.BulkEmailBase;
import in.nic.ashwini.eForms.entities.BulkUsers;
import in.nic.ashwini.eForms.models.FileUploadPojo;
import in.nic.ashwini.eForms.models.FinalAuditTrack;
import in.nic.ashwini.eForms.models.GeneratePdfBean;
import in.nic.ashwini.eForms.models.PreviewFormBean;
import in.nic.ashwini.eForms.models.ProfileDto;
import in.nic.ashwini.eForms.models.Status;
import in.nic.ashwini.eForms.repositories.GeneratePdfRepository;
import in.nic.ashwini.eForms.repositories.UidCheckRepo;
import in.nic.ashwini.eForms.repositories.BulkEmailBaseRepo;
import in.nic.ashwini.eForms.repositories.BulkUserBaseRepo;
import in.nic.ashwini.eForms.utils.BulkValidation;
import in.nic.ashwini.eForms.utils.Constants;
import in.nic.ashwini.eForms.utils.Util;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@Service
public class BulkEmailService {
	@Value("${fileBasePath}")
	private String EXTERNAL_FILE_PATH;
	@Value("${fileBasePath}")
	private String fileBasePath;

	private final GeneratePdfRepository generatePdfRepository;
	private final BulkEmailBaseRepo bulkEmailBaseRepo;
	private final UidCheckRepo uidCheckRepo;
	private final Util utilityService;
	private final BulkUserBaseRepo bulkUserBaseRepo;

	@Autowired
	public BulkEmailService(GeneratePdfRepository generatePdfRepository, BulkEmailBaseRepo bulkEmailBaseRepo,UidCheckRepo uidCheckRepo,
			Util utilityService,BulkUserBaseRepo bulkUserBaseRepo) {
		super();
		this.generatePdfRepository = generatePdfRepository;
		this.bulkEmailBaseRepo = bulkEmailBaseRepo;
		this.utilityService = utilityService;
		this.uidCheckRepo= uidCheckRepo;
		this.bulkUserBaseRepo=bulkUserBaseRepo;

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
			File file = ResourceUtils.getFile("classpath:imapreport.jrxml");
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
			parameters.put("protocol", generatePdfBean.getProtocol());
			parameters.put("logo", "classpath:static/NIC-Logo.jpg");
			System.out.println(parameters);
			jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, datasource);
		}
		return jasperPrint;
	}

	public HttpServletResponse downloadFiles(String filename, HttpServletResponse response) {
		File f = new File(EXTERNAL_FILE_PATH + filename);
		if (f.exists()) {
			String mimeType = URLConnection.guessContentTypeFromName(f.getName());
			System.out.println(mimeType);
			if (mimeType == null) {
				mimeType = "application/octet-stream";
			}
			response.setContentType(mimeType);
			response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + f.getName() + "\""));
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

	public BulkEmailBase preview(String regNo) {
		return bulkEmailBaseRepo.findByRegistrationNo(regNo);
	}
	
	@Transactional
	public BulkEmailBase insert(BulkEmailBase bulkEmailBase) {
		if (bulkEmailBase != null) {
			Date date = new Date();
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			String pdate = dateFormat.format(date);
			String oldRegNumber = bulkEmailBaseRepo.findLatestRegistrationNo();
			
			
			String newRegNumber = "BULKUSER-FORM" + pdate;
			if (oldRegNumber == null || oldRegNumber.isEmpty()) {
				newRegNumber += "0001";
			} else {
				
				String lastst = oldRegNumber.substring(21, oldRegNumber.length());
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
			bulkEmailBase.setRegistrationNo(newRegNumber);
			bulkEmailBase.setSupportActionTaken("p");
			return bulkEmailBaseRepo.save(bulkEmailBase);
		}
		return null;
	}

	public boolean updatePreviewDetails(String regNumber, PreviewFormBean previewFormBean) {
		BulkEmailBase bulkEmailBase = bulkEmailBaseRepo.findByRegistrationNo(regNumber);
		if (bulkEmailBase != null) {
			try {
				BeanUtils.copyProperties(previewFormBean, bulkEmailBase);
				
				LocalDateTime currentTime = LocalDateTime.now();
				//bulkEmailBase.setLastUpdationDateTime(currentTime);
				BulkEmailBase bulkUpdated = bulkEmailBaseRepo.save(bulkEmailBase);
				if (bulkUpdated.getId() > 0) {
					return true;
				}
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	public boolean updateStatusTable(String regNumber, String currentRole, PreviewFormBean previewFormBean) {
		BulkEmailBase bulkEmailBase = bulkEmailBaseRepo.findByRegistrationNo(regNumber);
		if (bulkEmailBase != null) {
			try {
				BeanUtils.copyProperties(previewFormBean, bulkEmailBase);
				LocalDateTime currentTime = LocalDateTime.now();
				//bulkEmailBase.setLastUpdationDateTime(currentTime);
				BulkEmailBase bulkUpdated = bulkEmailBaseRepo.save(bulkEmailBase);
				if (bulkUpdated.getId() > 0) {
					return true;
				}
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	public boolean updateStatusAndFinalAuditTrack(Status status, FinalAuditTrack finalAuditTrack) {
		return utilityService.updateStatusAndFinalAuditTrack(status,finalAuditTrack);
	}
	
	public FileUploadPojo dummy(String email, FileUploadPojo fileUploadPojo, String registrationNo) {

		
		
		ProfileDto profile = utilityService.fetchProfileByEmailInBean(email);
		System.out.println("inside dummy");

		String csvFile = Constants.LOCAL_FILE_LOCATION + fileUploadPojo.getFileUploadName();
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
					bulkUsers.setRegistrationNo(registrationNo);
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

			bulkUserBaseRepo.saveAll(bulkUsersLIst);

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
	public boolean isFilenameCSVValid(String file) {
		try {
			String fileNameWithOutExt = FilenameUtils.removeExtension(file);
			if (!fileNameWithOutExt.contains(".")) {
				if ((fileNameWithOutExt.matches("^[^*&%]+$")) && (file.endsWith(".csv") || file.endsWith(".CSV"))) {
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

	public Boolean checkCsvSize(File destFile) {
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
	
	public FileUploadPojo checkCSV(MultipartFile file) {
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
		File savedFile = Paths.get(loc + fileName).toFile();
		if (!checkCsvSize(savedFile)) {
			// have to set server file download path pennnnnding.
			fileUploadPojo.setFileUploadName(savedFile.getName());
		} else {
			fileUploadPojo.setErrorMsg("Upload file in .csv format with maxmimum 3000 rows.");
		}
		return fileUploadPojo;

	}
	public List<BulkUsers> fetchBulkUserTrack(String regNumber) {
		List<BulkUsers> bulkUsers = bulkUserBaseRepo.findByRegistrationNo(regNumber);
		return bulkUsers;
	}
	
}
