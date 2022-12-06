package in.nic.ashwini.eForms.services;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.nic.ashwini.eForms.entities.GeneratePdf;
import in.nic.ashwini.eForms.entities.LdapBase;
import in.nic.ashwini.eForms.models.ErrorResponseForOrganizationValidationDto;
import in.nic.ashwini.eForms.models.FinalAuditTrack;
import in.nic.ashwini.eForms.models.GeneratePdfBean;
import in.nic.ashwini.eForms.models.HodDetailsDto;
import in.nic.ashwini.eForms.models.ManualUploadBean;
import in.nic.ashwini.eForms.models.OrganizationBean;
import in.nic.ashwini.eForms.models.OrganizationDto;
import in.nic.ashwini.eForms.models.PreviewFormBean;
import in.nic.ashwini.eForms.models.ProfileDto;
import in.nic.ashwini.eForms.models.ResponseBean;
import in.nic.ashwini.eForms.models.Status;
import in.nic.ashwini.eForms.models.ValidateFormBean;
import in.nic.ashwini.eForms.repositories.GeneratePdfRepository;
import in.nic.ashwini.eForms.repositories.LdapBaseRepo;
import in.nic.ashwini.eForms.utils.Constants;
import in.nic.ashwini.eForms.utils.Util;
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
public class LdapUserService {
	private final LdapBaseRepo ldapBaseRepo;
	private final Util utilityService;
	@Value("${fileBasePath}")
	private String fileBasePath;
	private final GeneratePdfRepository generatePdfRepository;
	@Autowired
	public LdapUserService( LdapBaseRepo ldapBaseRepo,GeneratePdfRepository generatePdfRepository,
			Util utilityService) {
		super();
		this.ldapBaseRepo = ldapBaseRepo;
		this.utilityService = utilityService;
		this.generatePdfRepository = generatePdfRepository;
	}

	public LdapBase fetchDetails(String regNo) {
		return ldapBaseRepo.findByRegistrationNo(regNo);
	}
	
	public ResponseBean updatePreviewDetails(String regNumber, PreviewFormBean previewFormBean,ResponseBean responseBean)  {
		
		Map<String,Object> map = new HashMap<>();
		Map<String,Object> finalmap = new HashMap<>();
		OrganizationDto organizationDetails = new OrganizationDto();
		String refFilename="";
		BeanUtils.copyProperties(previewFormBean, organizationDetails);
		String organizationValidationResponse = utilityService.validateOrganization(organizationDetails);
		ObjectMapper mapper = new ObjectMapper();
		ErrorResponseForOrganizationValidationDto orgError = null;
		
		 MultipartFile uploadedFileName = previewFormBean.getUploadedFilename();
		 boolean app_name_error = utilityService.nameValidation(previewFormBean.getAppName());
         boolean app_url_error = utilityService.pullurlValidation(previewFormBean.getAppUrl());
         boolean ip1_error = utilityService.baseipValidation(previewFormBean.getBaseIp());
         boolean ip2_error = utilityService.serviceipValidation(previewFormBean.getServiceIp());
         boolean domain_error = utilityService.addValidation(previewFormBean.getDomain());
		
         if (app_name_error) {
        	 map.put("app_name_error", "Enter Name of the Applicaion [characters,dot(.) and whitespaces],limit 1-50");
         }
         if (app_url_error) {
        	 map.put("app_url_error", "Enter Application URL [e.g: (https://abc.com)]");
         }
         if (ip1_error) {
        	 map.put("ip1_error", "Enter Application IP1 [e.g: 10.10.10.10]");
         }
         if (ip2_error) {
        	 map.put("ip2_error", "Enter Application IP2 [e.g: 10.10.10.10]");
         }
         if (domain_error) {
        	 map.put("domain_error", "Enter Domain/Group Of People who will access this application,[Alphanumeric,dot(.),comma(,),hyphen(-),slash(/) and whitespaces] allowed");
         }
         
		
         if (previewFormBean.getServerLoc().equals("Other")) {
             boolean server_txt_error = utilityService.addValidation(previewFormBean.getServer_location_txt());
             if (server_txt_error) {
            	 map.put("server_txt_error", "Enter Server Location [Alphanumeric,dot(.),comma(,),hyphen(-),slash(/) and whitespaces] allowed");
             }
         } else {
             boolean server_loc_error = utilityService.addValidation(previewFormBean.getServerLoc());
             if (server_loc_error) {
            	 map.put("SercerLocationError", "Enter Server Location [Alphanumeric,dot(.),comma(,),hyphen(-),slash(/) and whitespaces] allowed");
             }
         }
		
		if (previewFormBean.getAudit().equals("yes") && previewFormBean.getHttps().equals("yes")) {
			if(uploadedFileName==null || uploadedFileName.isEmpty()) {
				System.out.println("audit yes http yes");
				map.put("FileError","Please upload Security audit clearance certificate in PDF format only");
				
			}else if(!isFileSizeValid(uploadedFileName)==true) {
				System.out.println("*********2***********");
				map.put("FileError","File size should be less than 1 mb");
			}else if (!isFilenameCSVValid(uploadedFileName.getOriginalFilename()) == true) {
					System.out.println("*********1************");
					map.put("FileError","Upload only PDF file");
			}
				
			String[] contenttype = previewFormBean.getUploadedFilename().getContentType().split("/");
			String ext = contenttype[1];
			
			String fileNameWithOutExt = FilenameUtils
					.removeExtension(previewFormBean.getUploadedFilename().getOriginalFilename());
			System.out.println("*******************fileNameWithOutExt**" + fileNameWithOutExt);
			
			String outputfile = new StringBuilder().append(fileNameWithOutExt).append("_")
					.append(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
					.append(".").append(ext).toString();
			
			byte[] bytes;
			try {
				bytes = previewFormBean.getUploadedFilename().getBytes();
				Path path = Paths.get(fileBasePath + outputfile);
				Files.write(path, bytes);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			refFilename = fileBasePath+outputfile;
			  //refFilename = fileBasePath+outputfile.substring(0, outputfile.indexOf("."));
			 System.out.println("refFilename:::::::::::"+refFilename);
			
		}
		
		else if(previewFormBean.getAudit().equals("no") && previewFormBean.getHttps().equals("yes")){
              boolean email1_error = utilityService.EmailValidation(previewFormBean.getLdap_id1());
              if (email1_error) {
                  map.put("ldapId1_Error", "Enter Email Address [e.g: abc.xyz@zxc.com]");
              }
              if (previewFormBean.getLdap_id2().equals("") || previewFormBean.getLdap_id2()== null) {

              } else {
                  boolean email2_error = utilityService.EmailValidation(previewFormBean.getLdap_id2());
                  if (email2_error) {
                      map.put("Ldapid2_Error", "Enter Email Address [e.g: abc.xyz@zxc.com]");
                  }
              }
          }
		
		else if(previewFormBean.getAudit().equals("no") && previewFormBean.getHttps().equals("no")){
			map.put("Note","Application should be enabled over https"); 
			System.out.println("audit no http no");
	}
		else if(previewFormBean.getAudit().equals("yes") && previewFormBean.getHttps().equals("no")){
			map.put("Note","Application should be enabled over https"); 
			System.out.println("audit no http no");
	}
		
		
		if(organizationValidationResponse != null && !organizationValidationResponse.isEmpty()) {
			log.debug("Errors in Organization");
			try {
				orgError = mapper.readValue(organizationValidationResponse, ErrorResponseForOrganizationValidationDto.class);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			if(orgError != null)
			map.put("orgError",orgError);
		}
		if(map.size() > 1) {
			finalmap.put("errors", map);
		}
		
		if(map.isEmpty()) {
		
		//**************************************************************
		LdapBase ldapBase = ldapBaseRepo.findByRegistrationNo(regNumber);
		if (ldapBase != null) {
			try {
				BeanUtils.copyProperties(previewFormBean, ldapBase);
				LocalDateTime currentTime = LocalDateTime.now();
				//ldapBase.setLastUpdationDateTime(currentTime);
				ldapBase.setUploadedFilename(previewFormBean.getUploadedFilename().getOriginalFilename());
				ldapBase.setRenamedFilepath(refFilename);
				LdapBase ldapUpdated = ldapBaseRepo.save(ldapBase);
				if (ldapUpdated.getId() > 0) {
					//return true;
					responseBean.setStatus("Details updated successfully!!");
					responseBean.setRegNumber(regNumber);
					responseBean.setErrors(null);
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//******************************************************************
		}else {
			responseBean.setErrors(map);
			responseBean.setStatus(null);
			responseBean.setRegNumber(regNumber);
		}
		
		return responseBean;
	}
	
	
	public Map<String,Object> validate(ValidateFormBean validateFormBean) {
		Map<String,Object> finalmap = new HashMap<>();
		Map<String,Object> map = new HashMap<>();
		 MultipartFile uploadedFileName = validateFormBean.getUploadedFilename();
		 boolean app_name_error = utilityService.nameValidation(validateFormBean.getAppName());
         boolean app_url_error = utilityService.pullurlValidation(validateFormBean.getAppUrl());
         boolean ip1_error = utilityService.baseipValidation(validateFormBean.getBaseIp());
         boolean ip2_error = utilityService.serviceipValidation(validateFormBean.getServiceIp());
         boolean domain_error = utilityService.addValidation(validateFormBean.getDomain());
		
         if (app_name_error == true) {
        	 map.put("app_name_error", "Enter Name of the Applicaion [characters,dot(.) and whitespaces],limit 1-50");
         }
         if (app_url_error == true) {
        	 map.put("app_url_error", "Enter Application URL [e.g: (https://abc.com)]");
         }
         if (ip1_error == true) {
        	 map.put("ip1_error", "Enter Application IP1 [e.g: 10.10.10.10]");
         }
         if (ip2_error == true) {
        	 map.put("ip2_error", "Enter Application IP2 [e.g: 10.10.10.10]");
         }
         if (domain_error == true) {
        	 map.put("domain_error", "Enter Domain/Group Of People who will access this application,[Alphanumeric,dot(.),comma(,),hyphen(-),slash(/) and whitespaces] allowed");
         }
         
		
         if (validateFormBean.getServerLoc().equals("Other")) {
             boolean server_txt_error = utilityService.addValidation(validateFormBean.getServer_location_txt());
             if (server_txt_error == true) {
            	 map.put("server_txt_error", "Enter Server Location [Alphanumeric,dot(.),comma(,),hyphen(-),slash(/) and whitespaces] allowed");
             }
         } else {
             boolean server_loc_error = utilityService.addValidation(validateFormBean.getServerLoc());
             if (server_loc_error == true) {
            	 map.put("SercerLocationError", "Enter Server Location [Alphanumeric,dot(.),comma(,),hyphen(-),slash(/) and whitespaces] allowed");
             }
         }
		
		if (validateFormBean.getAudit().equals("yes") && validateFormBean.getHttps().equals("yes")) {
			if(uploadedFileName==null || uploadedFileName.isEmpty()) {
				System.out.println("audit yes http yes");
				map.put("FileError","Please upload Security audit clearance certificate in PDF format only");
				
			}else if(!isFileSizeValid(uploadedFileName)==true) {
				System.out.println("*********2***********");
				map.put("FileError","File size should be less than 1 mb");
			}else if (!isFilenameCSVValid(uploadedFileName.getOriginalFilename()) == true) {
					System.out.println("*********1************");
					map.put("FileError","Upload only PDF file");
			}
		}
		
		else if(validateFormBean.getAudit().equals("no") && validateFormBean.getHttps().equals("yes")){
			System.out.println("getAudit::::::::::"+validateFormBean.getAudit());
			System.out.println("getHttps::::::::"+validateFormBean.getHttps());
			System.out.println("getLdap_id1::::::::"+validateFormBean.getLdap_id1());
			System.out.println("getLdap_id2::::::::"+validateFormBean.getLdap_id2());
              boolean email1_error = utilityService.EmailValidation(validateFormBean.getLdap_id1());
              if (email1_error) {
                  map.put("ldapId1_Error", "Enter Email Address [e.g: abc.xyz@zxc.com]");
              }
              if (validateFormBean.getLdap_id2().equals("") || validateFormBean.getLdap_id2()== null) {

              } else {
                  boolean email2_error = utilityService.EmailValidation(validateFormBean.getLdap_id2());
                  if (email2_error) {
                      map.put("Ldapid2_Error", "Enter Email Address [e.g: abc.xyz@zxc.com]");
                  }
                  if(validateFormBean.getLdap_id1().equalsIgnoreCase(validateFormBean.getLdap_id2())) {
                	  map.put("Ldapid2_Error", "Ldapid1 and Ldapid2 can not be same");
                  }
              }
          }
		
		else if(validateFormBean.getAudit().equals("no") && validateFormBean.getHttps().equals("no")){
			map.put("Note","Application should be enabled over https"); 
			System.out.println("audit no http no");
	}
		else if(validateFormBean.getAudit().equals("yes") && validateFormBean.getHttps().equals("no")){
			map.put("Note","Application should be enabled over https"); 
			System.out.println("audit no http no");
	}
		if(map.size() > 1) {
			finalmap.put("errors", map);
		}
		return map;
	}
	
	public boolean isFilenameCSVValid(String filename) {
		System.out.println("*********isFilenameCSVValid**********filename**" + filename);
		try {
			String fileNameWithOutExt = FilenameUtils.removeExtension(filename);
		
				if ((fileNameWithOutExt.matches("^[^*&%]+$"))
						&& (filename.endsWith(".pdf") || filename.endsWith(".PDF"))) {
					System.out.println("*********isFilenameCSVValid is valid************"+filename);
					return true;
				} else {
					System.out.println("*********isFilenameCSVValid is not valid************"+filename);
					return false;
				}
			
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean isFileSizeValid(MultipartFile filename) {
		System.out.println("*********isFileSizeValid**********filename**" + filename);
	
			String[] contenttype = filename.getContentType().split("/");
			String ext = contenttype[1];
			
				 long bytes = filename.getSize();
				System.out.println("*********isFileSizeValid bytes************"+bytes);
				
				if (bytes<=1000000) {
					System.out.println("*********isFileSizeValid is  valid************"+filename);
					return true;
				} else {
					System.out.println("*********isFileSizeValid is not valid************"+filename);
					return false;
				}
	}
	
	public ResponseBean submitRequest(PreviewFormBean previewFormBean, String ip, String email,	String submissionType,ResponseBean responseBean) throws IOException {

		String formType = "ldap";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		ProfileDto profile = utilityService.fetchProfileByEmailInBean(email);
		responseBean.setRequestType("Submission of request");
		Map<String,Object> map = validateRequest(previewFormBean);
		System.out.println("Map Size:::: "+map.size());
		System.out.println("Map:::: "+map);
		if(map.size() == 0) {
			responseBean.setErrors(null);
		if (profile != null) {
			status = utilityService.initializeStatusTable(ip, email, formType, previewFormBean.getRemarks(), profile.getMobile(),
					profile.getName(),"user");
			finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, previewFormBean.getRemarks(),
					profile.getMobile(), profile.getName(), "user","");
		
			ModelMapper modelMapper = new ModelMapper();
			LdapBase ldapBase = modelMapper.map(profile, LdapBase.class);
			ldapBase.setPdfPath(submissionType);
			if (previewFormBean.getUploadedFilename() != null) {
				System.out.println(
						"HardwareFILEEEe:::::::::::" + previewFormBean.getUploadedFilename().getOriginalFilename());
				ldapBase.setUploadedFilename(previewFormBean.getUploadedFilename().getOriginalFilename());
				ldapBase.setRenamedFilepath(Constants.LOCAL_FILE_LOCATION
						+ previewFormBean.getUploadedFilename().getOriginalFilename());
				byte[] bytes1 = previewFormBean.getUploadedFilename().getBytes();
				Path path1 = Paths.get(Constants.LOCAL_FILE_LOCATION
						+ previewFormBean.getUploadedFilename().getOriginalFilename());

				Files.write(path1, bytes1);
			}
			System.out.println("RenamedFilePath ::::::::::" + ldapBase.getRenamedFilepath());
		
			BeanUtils.copyProperties(previewFormBean, ldapBase);
			LocalDateTime currentTime = LocalDateTime.now();
			ldapBase.setDatetime(currentTime);
			ldapBase.setUserIp(ip);
			

			for (int i = 0; i < 4; i++) {
				ldapBase = insert(ldapBase);
				if (ldapBase.getId() > 0) {
					break;
				}
			}

			if (ldapBase.getId() > 0) {
				if (submissionType.equalsIgnoreCase("online") || submissionType.equalsIgnoreCase("esign")) {
						status.setRegistrationNo(ldapBase.getRegistrationNo());
						status.setRecipientType(Constants.STATUS_CA_TYPE);
						status.setStatus(Constants.STATUS_CA_PENDING);
						status.setRecipient(profile.getHodEmail());

						finalAuditTrack.setRegistrationNo(ldapBase.getRegistrationNo());
						finalAuditTrack.setStatus(Constants.STATUS_CA_PENDING);
						finalAuditTrack.setToEmail(profile.getHodEmail());

						if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
							log.info("{} submitted successfully.", ldapBase.getRegistrationNo());
							responseBean.setStatus("Request submitted successfully and forwarded to Reporting/Nodal/Forwarding Officer("
									+ profile.getHodEmail() + ")");
							responseBean.setRegNumber(ldapBase.getRegistrationNo());
							System.out.println("REGISTRATION NUMBER" + ldapBase.getRegistrationNo());
						} else {
							log.debug("Something went wrong. Please try again after sometime.");
							responseBean.setStatus("Something went wrong. Please try again after sometime.");
							responseBean.setRegNumber("");
						}
				} else {
					status.setRegistrationNo(ldapBase.getRegistrationNo());
					status.setRecipientType(Constants.STATUS_USER_TYPE);
					status.setStatus(Constants.STATUS_MANUAL_UPLOAD);
					status.setRecipient(email);

					finalAuditTrack.setRegistrationNo(ldapBase.getRegistrationNo());
					finalAuditTrack.setStatus(Constants.STATUS_MANUAL_UPLOAD);
					finalAuditTrack.setToEmail(email);

					if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
						log.info("{} submitted successfully.", ldapBase.getRegistrationNo());
						responseBean.setStatus(
								"Request submitted successfully but it is pending with you only as you selected manual upload option to submit the request.");
						responseBean.setRegNumber(ldapBase.getRegistrationNo());
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
			log.warn("Hey, {},  We do not have your profile in eForms. Please go to profile section and make your profile first");
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
	
	@Transactional
	public LdapBase insert(LdapBase ldapBase) {
		if (ldapBase != null) {
			System.out.println("checkkkkPath:::::" + ldapBase.getRenamedFilepath());
			Date date = new Date();
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			String pdate = dateFormat.format(date);
			String oldRegNumber = ldapBaseRepo.findLatestRegistrationNo();
			String newRegNumber = "LDAP-FORM" + pdate;
			if (oldRegNumber == null || oldRegNumber.isEmpty()) {
				newRegNumber += "0001";
			} else {
				String lastst = oldRegNumber.substring(17, oldRegNumber.length());
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
			return ldapBaseRepo.save(ldapBase);
		}
		return null;
	}

	
	public boolean updateStatusAndFinalAuditTrack(Status status, FinalAuditTrack finalAuditTrack) {
		return utilityService.updateStatusAndFinalAuditTrack(status,finalAuditTrack);
	}
	
	
	
	public Map<String,Object> validateRequest(PreviewFormBean previewFormBean) {
		Map<String,Object> map = new HashMap<>();
		Map<String,Object> finalmap = new HashMap<>();
		OrganizationDto organizationDetails = new OrganizationDto();

		BeanUtils.copyProperties(previewFormBean, organizationDetails);
		String organizationValidationResponse = utilityService.validateOrganization(organizationDetails);
		ObjectMapper mapper = new ObjectMapper();
		ErrorResponseForOrganizationValidationDto orgError = null;
		
		 MultipartFile uploadedFileName = previewFormBean.getUploadedFilename();
		 boolean app_name_error = utilityService.nameValidation(previewFormBean.getAppName());
         boolean app_url_error = utilityService.pullurlValidation(previewFormBean.getAppUrl());
         boolean ip1_error = utilityService.baseipValidation(previewFormBean.getBaseIp());
         boolean ip2_error = utilityService.serviceipValidation(previewFormBean.getServiceIp());
         boolean domain_error = utilityService.addValidation(previewFormBean.getDomain());
		
         if (app_name_error == true) {
        	 map.put("app_name_error", "Enter Name of the Applicaion [characters,dot(.) and whitespaces],limit 1-50");
         }
         if (app_url_error == true) {
        	 map.put("app_url_error", "Enter Application URL [e.g: (https://abc.com)]");
         }
         if (ip1_error == true) {
        	 map.put("ip1_error", "Enter Application IP1 [e.g: 10.10.10.10]");
         }
         if (ip2_error == true) {
        	 map.put("ip2_error", "Enter Application IP2 [e.g: 10.10.10.10]");
         }
         if (domain_error == true) {
        	 map.put("domain_error", "Enter Domain/Group Of People who will access this application,[Alphanumeric,dot(.),comma(,),hyphen(-),slash(/) and whitespaces] allowed");
         }
         
		
         if (previewFormBean.getServerLoc().equals("Other")) {
             boolean server_txt_error = utilityService.addValidation(previewFormBean.getServer_location_txt());
             if (server_txt_error == true) {
            	 map.put("server_txt_error", "Enter Server Location [Alphanumeric,dot(.),comma(,),hyphen(-),slash(/) and whitespaces] allowed");
             }
         } else {
             boolean server_loc_error = utilityService.addValidation(previewFormBean.getServerLoc());
             if (server_loc_error == true) {
            	 map.put("SercerLocationError", "Enter Server Location [Alphanumeric,dot(.),comma(,),hyphen(-),slash(/) and whitespaces] allowed");
             }
         }
		
		if (previewFormBean.getAudit().equals("yes") && previewFormBean.getHttps().equals("yes")) {
			if(uploadedFileName==null || uploadedFileName.isEmpty()) {
				System.out.println("audit yes http yes");
				map.put("FileError","Please upload Security audit clearance certificate in PDF format only");
				
			}else if(!isFileSizeValid(uploadedFileName)==true) {
				System.out.println("*********2***********");
				map.put("FileError","File size should be less than 1 mb");
			}else if (!isFilenameCSVValid(uploadedFileName.getOriginalFilename()) == true) {
					System.out.println("*********1************");
					map.put("FileError","Upload only PDF file");
			}
				
			
		}
		
		else if(previewFormBean.getAudit().equals("no") && previewFormBean.getHttps().equals("yes")){
              boolean email1_error = utilityService.EmailValidation(previewFormBean.getLdap_id1());
              if (email1_error) {
                  map.put("ldapId1_Error", "Enter Email Address [e.g: abc.xyz@zxc.com]");
              }
              if (previewFormBean.getLdap_id2().equals("") || previewFormBean.getLdap_id2()== null) {

              } else {
                  boolean email2_error = utilityService.EmailValidation(previewFormBean.getLdap_id2());
                  if (email2_error) {
                      map.put("Ldapid2_Error", "Enter Email Address [e.g: abc.xyz@zxc.com]");
                  }
              }
          }
		
		else if(previewFormBean.getAudit().equals("no") && previewFormBean.getHttps().equals("no")){
			map.put("Note","Application should be enabled over https"); 
			System.out.println("audit no http no");
	}
		else if(previewFormBean.getAudit().equals("yes") && previewFormBean.getHttps().equals("no")){
			map.put("Note","Application should be enabled over https"); 
			System.out.println("audit no http no");
	}
		
		
		if(organizationValidationResponse != null && !organizationValidationResponse.isEmpty()) {
			log.debug("Errors in Organization");
			try {
				orgError = mapper.readValue(organizationValidationResponse, ErrorResponseForOrganizationValidationDto.class);
			} catch (JsonProcessingException e) {
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
	
	
	
	public ResponseBean approve( String regNumber, String ip,String email, String remarks,ResponseBean responseBean) {
		responseBean.setRequestType("Approval of request by user as it was submitted manually");
		String formType = "ldap";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		LdapBase ldapBase = fetchDetails(regNumber);

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, ldapBase.getMobile(), ldapBase.getName(),"user");
		finalAuditTrack = utilityService.fetchFinalAuditTrack(regNumber);
		
			status.setRegistrationNo(regNumber);
			status.setRecipientType(Constants.STATUS_CA_TYPE);
			status.setStatus(Constants.STATUS_CA_PENDING);
			status.setRecipient(ldapBase.getHodEmail());

			finalAuditTrack.setStatus(Constants.STATUS_CA_PENDING);
			finalAuditTrack.setToEmail(ldapBase.getHodEmail());

			if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
				log.info("{} submitted successfully.", regNumber);
				responseBean.setStatus(
						"Request submitted successfully and forwarded to Reporting/Nodal/Forwarding Officer ("
								+ ldapBase.getHodEmail() + ")");
				responseBean.setRegNumber(regNumber);
			} else {
				log.debug("Something went wrong. Please try again after sometime.");
				responseBean.setStatus("Something went wrong. Please try again after sometime.");
				responseBean.setRegNumber("");
			}
		
		return responseBean;
	}
	
	
	public ResponseBean reject(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, @RequestParam @NotEmpty @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks,ResponseBean responseBean) {
		responseBean.setRequestType("Cancellation of request by user.");
		String formType = "ldap";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		LdapBase ldapBase = fetchDetails(regNumber);

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, ldapBase.getMobile(), ldapBase.getName(),"user");

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
		System.out.println("fetchAllBulkUsers:::::::::::regNumber::::::"+regNumber);
		  Map<String,Object> data=new HashMap<String, Object>();
		  data.put("fetchBulkUsers", ldapBaseRepo.findByRegistrationNo(regNumber));
		  
		  return data;
		
	}
	
	public HttpServletResponse downloadFiles(String filename, HttpServletResponse response) {
		File f = new File(fileBasePath + filename);
		System.out.println(f + " *********************");
		if (f.exists()) {
			String mimeType = URLConnection.guessContentTypeFromName(f.getName());
			System.out.println(mimeType);
			if (mimeType == null) {
				mimeType = "application/octet-stream";
			}
			response.setContentType(mimeType);
			response.setHeader("Content-Disposition", String.format("inline; filename=\"" + f.getName() + "\""));
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
		System.out.println(response.getStatus() + "    " + response.getHeader("status"));
		return response;
	}
	
	
	public JasperPrint generateFormPdf(String regid)
			throws JRException, IOException, IllegalAccessException, InvocationTargetException {
		JasperPrint jasperPrint = null;
		Optional<GeneratePdf> formDetails = generatePdfRepository.findByRegistrationNo(regid);
		System.out.println("formDetails:::::::::"+formDetails);
		
		GeneratePdf generatePdfDTO = null;
		if (formDetails.isPresent()) {
			generatePdfDTO = formDetails.orElse(null);
			GeneratePdfBean generatePdfBean = new GeneratePdfBean();
			org.springframework.beans.BeanUtils.copyProperties(generatePdfDTO, generatePdfBean);
			System.out.println("generatePdfBean:::::::::"+generatePdfBean);
			System.out.println("generatePdfDTO:::::::::"+generatePdfDTO);
			
			File file = ResourceUtils.getFile("classpath:ldapreport.jrxml");
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
			parameters.put("appName", generatePdfBean.getAppName());
			parameters.put("appUrl", generatePdfBean.getAppUrl());
			parameters.put("domain", generatePdfBean.getDomain());
			parameters.put("baseIp", generatePdfBean.getBaseIp());
			parameters.put("serviceIp", generatePdfBean.getServiceIp());
			parameters.put("serverLoc", generatePdfBean.getServerLoc());
			parameters.put("https", generatePdfBean.getHttps());
			parameters.put("audit", generatePdfBean.getAudit());
			parameters.put("logo", "classpath:static/NIC-Logo.jpg");
			System.out.println(parameters);
			jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, datasource);
		}
		return jasperPrint;
	}
	public LdapBase preview1(String regNo) {
		return ldapBaseRepo.findByRegistrationNo(regNo);
	}
	public ResponseBean manualupload(@ModelAttribute("manualUploadBean") ManualUploadBean manualUploadBean,HttpServletRequest request, ResponseBean responseBean) throws IOException {
		
		manualUploadBean.setEmail(request.getParameter("email"));
		manualUploadBean.setClientIp(request.getParameter("clientIp"));
		
		responseBean.setRequestType("Forwarding of request by user");
		String formType = "ldap";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		LdapBase ldapBase = preview1(manualUploadBean.getRegNumber());
//		//fileadd
//		//update in base table the file in pdf_path column
		String[] contenttype = manualUploadBean.getInfile().getContentType().split("/");
		String ext = contenttype[1];
		String outputfile = new StringBuilder().append(manualUploadBean.getRegNumber()).append("_")
				.append(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
				.append(".").append(ext).toString();
		
		ldapBase.setPdfPath(fileBasePath+outputfile);
		byte[] bytes = manualUploadBean.getInfile().getBytes();
		Path path = Paths.get(fileBasePath + outputfile);
		boolean stat = updatemobilebase(ldapBase);
		if(stat) {
			
			Files.write(path, bytes);
		}
		else {
			
			responseBean.setStatus("File failed to upload");
			responseBean.setRegNumber(manualUploadBean.getRegNumber());
   		return responseBean;
		}
//		//EO fileadd
		String dn = utilityService.findDn(ldapBase.getEmail());
		String roDn = utilityService.findDn(manualUploadBean.getEmail());
		List<String> aliases = utilityService.aliases(manualUploadBean.getEmail());
		String daEmail = "";
		String toWhom = "";
		String recipientType = "";
		String nextStatus = "";
		
		finalAuditTrack = utilityService.initializeFinalAuditTrackTable(manualUploadBean.getClientIp(), manualUploadBean.getEmail(), formType, manualUploadBean.getRemarks(), ldapBase.getHodMobile(),
				ldapBase.getHodName(),"usermanual" ,manualUploadBean.getRegNumber());
		status = utilityService.initializeStatusTable(manualUploadBean.getClientIp(), manualUploadBean.getEmail(), formType, manualUploadBean.getRemarks(), ldapBase.getHodMobile(), ldapBase.getHodName(),"user");
		
		if (ldapBase.getEmployment().equalsIgnoreCase("state")
				&& ldapBase.getPostingState().equalsIgnoreCase("Assam")) {
			toWhom = "Coordinator";
			recipientType = Constants.STATUS_COORDINATOR_TYPE;
			nextStatus = Constants.STATUS_COORDINATOR_PENDING;
		}else if (ldapBase.getEmployment().equalsIgnoreCase("State") && ldapBase.getState().equalsIgnoreCase("punjab")
				&& ldapBase.getPostingState().equalsIgnoreCase("punjab")) {
			toWhom = "Coordinator";
			List<String> punjabCoords = utilityService.fetchPunjabCoords(ldapBase.getCity());
			daEmail = String.join(",", punjabCoords);
			recipientType = Constants.STATUS_COORDINATOR_TYPE;
			nextStatus = Constants.STATUS_COORDINATOR_PENDING;
		} else if (utilityService.isNicEmployee(manualUploadBean.getEmail()) && (ldapBase.getPostingState().equalsIgnoreCase("delhi")
				&& ldapBase.getEmployment().equalsIgnoreCase("central")
				&& (dn.contains("nic support outsourced") || dn.contains("nationalknowledgenetwork")))) {
			toWhom = "Admin";
			daEmail = Constants.MAILADMIN_EMAIL;
			recipientType = Constants.STATUS_ADMIN_TYPE;
			nextStatus = Constants.STATUS_MAILADMIN_PENDING;
		} else if (utilityService.isNicEmployee(ldapBase.getEmail()) || utilityService.isNicEmployee(manualUploadBean.getEmail())) {
			toWhom = "ro";
			//daEmail = Constants.NKN_SUPPORT_EMAIL;
			recipientType = Constants.STATUS_CA_TYPE;
			nextStatus = Constants.STATUS_CA_PENDING;	
			
		} else if (dn!=null && ((dn.contains("nic support outsourced") || dn.contains("nationalknowledgenetwork")))
				&& (roDn.contains("nic support outsourced") || roDn.contains("nationalknowledgenetwork"))) {
			toWhom = "ro";
			//daEmail = Constants.NKN_SUPPORT_EMAIL;  
			recipientType = Constants.STATUS_CA_TYPE;
			nextStatus = Constants.STATUS_CA_PENDING;
			
		} else if (ldapBase.getEmployment().equalsIgnoreCase("Others")
				&& ldapBase.getOrganization().equalsIgnoreCase("DataCentre and Webservices")
				&& ldapBase.getPostingState().equalsIgnoreCase("maharashtra")
				&& ldapBase.getCity().equalsIgnoreCase("pune") && (ldapBase.getAddress().toLowerCase().contains("ndc")
						|| ldapBase.getAddress().toLowerCase().contains("national data center"))) {
			toWhom = "Coordinator";
			daEmail = utilityService.fetchNdcPuneCoord();

			if (aliases.contains(daEmail)) {
				recipientType = Constants.STATUS_ADMIN_TYPE;
				nextStatus = Constants.STATUS_MAILADMIN_PENDING;
			} else {
				recipientType = Constants.STATUS_COORDINATOR_TYPE;
				nextStatus = Constants.STATUS_COORDINATOR_PENDING;
			}
		}  else {
			ModelMapper modelMapper = new ModelMapper();
			OrganizationBean org = modelMapper.map(ldapBase, OrganizationBean.class);
				toWhom = "Reporting officer";
				daEmail = ldapBase.getHodEmail();
				recipientType = Constants.STATUS_CA_TYPE;
				nextStatus = Constants.STATUS_CA_PENDING;
		}
		status.setRegistrationNo(manualUploadBean.getRegNumber());
		status.setRecipientType(recipientType);
		status.setStatus(nextStatus);
		status.setRecipient(daEmail);

		finalAuditTrack.setStatus(nextStatus);
		finalAuditTrack.setToEmail(daEmail);

		if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
			log.info("Application (" + manualUploadBean.getRegNumber() + ")  Forwarded Successfully to " + toWhom + "(" + daEmail
					+ ")");
			responseBean.setStatus("Application (" + manualUploadBean.getRegNumber() + ") Forwarded Successfully to " + toWhom
					+ "(" + daEmail + ")");
			responseBean.setRegNumber(manualUploadBean.getRegNumber());
		} else {
			log.debug(Constants.ERROR_MESSAGE);
			responseBean.setStatus(Constants.ERROR_MESSAGE);
			responseBean.setRegNumber(manualUploadBean.getRegNumber());
		}
		return responseBean;
	}
	public boolean updatemobilebase(LdapBase ldapBase) {
		LdapBase ldapedetails = ldapBaseRepo.save(ldapBase);
		if (ldapedetails.getId() > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	
}
