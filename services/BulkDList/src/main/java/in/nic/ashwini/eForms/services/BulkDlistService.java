package in.nic.ashwini.eForms.services;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import in.nic.ashwini.eForms.entities.ModeratorBase;
import in.nic.ashwini.eForms.entities.BulkDlist;
import in.nic.ashwini.eForms.entities.BulkDlistBase;
import in.nic.ashwini.eForms.entities.GenerateBulkPdf;
import in.nic.ashwini.eForms.entities.GeneratePdf;
import in.nic.ashwini.eForms.models.FileUploadPojo;
import in.nic.ashwini.eForms.models.FinalAuditTrack;
import in.nic.ashwini.eForms.models.GeneratePdfBean;
import in.nic.ashwini.eForms.models.Moderators;
import in.nic.ashwini.eForms.models.Owners;
import in.nic.ashwini.eForms.models.PreviewFormBean;
import in.nic.ashwini.eForms.models.ProfileDto;
import in.nic.ashwini.eForms.models.Status;
import in.nic.ashwini.eForms.repositories.GeneratePdfRepository;
import in.nic.ashwini.eForms.repositories.ModeratorBaseRepo;
import in.nic.ashwini.eForms.repositories.BulkDListRepo;
import in.nic.ashwini.eForms.repositories.BulkDistributionRepository;
import in.nic.ashwini.eForms.repositories.BulkDlistBaseRepo;
import in.nic.ashwini.eForms.utils.Constants;
import in.nic.ashwini.eForms.utils.DlistBulkValidation;
import in.nic.ashwini.eForms.utils.Util;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.*;

@Service
public class BulkDlistService {
	@Value("${fileBasePath}")
	private String EXTERNAL_FILE_PATH;
	@Value("${fileBasePath}")
	private String fileBasePath;

	private final GeneratePdfRepository generatePdfRepository;
	private final BulkDlistBaseRepo bulkdlistBaseRepo;
	private final Util utilityService;
	@Autowired
	BulkDListRepo bulkdlistRepo;
	@Autowired
	ModeratorBaseRepo moderatorBaseRepo;
	@Autowired
	BulkDistributionRepository bulkDistributionRepository;

	@Autowired
	public BulkDlistService(GeneratePdfRepository generatePdfRepository, BulkDlistBaseRepo bulkdlistBaseRepo, 
			Util utilityService) {
		super();
		this.generatePdfRepository = generatePdfRepository;
		this.bulkdlistBaseRepo = bulkdlistBaseRepo;
		//this.bulkdlistRepo = bulkdlistRepo;
		this.utilityService = utilityService;
	}

	public JasperPrint generateFormPdf(String regid)
			throws JRException, IOException, IllegalAccessException, InvocationTargetException {
		JasperPrint jasperPrint = null;
		Optional<GeneratePdf> formDetails = generatePdfRepository.findByRegistrationNo(regid);
		
		List<GenerateBulkPdf> formDetails1 = bulkDistributionRepository.findByRegistrationNo(regid);
		
		GeneratePdf generatePdfDTO = null;
		GenerateBulkPdf generateBulkPdf=null;
		if (formDetails.isPresent()) {
			generatePdfDTO = formDetails.orElse(null);

			GeneratePdfBean generatePdfBean = new GeneratePdfBean();
			
			org.springframework.beans.BeanUtils.copyProperties(generatePdfDTO, generatePdfBean);
			File file = ResourceUtils.getFile("classpath:dlistreport.jrxml");
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
			
			parameters.put("bulkData", formDetails1);
//			int count=0;
//			
//			for(GenerateBulkPdf fd1:formDetails1) {
//				parameters.put("list_name",fd1.getList_name());
//				parameters.put("description_list",fd1.getDescription_list());
//				parameters.put("list_mod",fd1.getList_mod());
//				parameters.put("allowed_member",fd1.getAllowed_member());
//				parameters.put("mail_acceptance",fd1.getNon_nicnet());
//				parameters.put("list_temp",fd1.getList_temp());              //relevent code
//				parameters.put("owner_name",fd1.getOwner_name());
//				parameters.put("owner_email",fd1.getOwner_email());
//				parameters.put("owner_mobile",fd1.getOwner_mobile());
//				parameters.put("t_off_name",fd1.getT_off_name());
//				parameters.put("tauth_email",fd1.getTauth_email());
//				parameters.put("tmobile",fd1.getTmobile());
//				parameters.put("owner_Admin",fd1.getOwner_Admin());
//				parameters.put("moderator_Admin",fd1.getModerator_Admin());
//		    count++;
//			}
//			
//			parameters.put("count", count);

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

	public BulkDlistBase preview(String regNo) {
		return bulkdlistBaseRepo.findByRegistrationNo(regNo);
	}
	
	public List<BulkDlist> previewBulk(String regNo) {
		return bulkdlistRepo.findByRegistrationNo(regNo);
	}
	
	@Transactional
	public BulkDlistBase insert(BulkDlistBase dlistBase) {
		if (dlistBase != null) {
			Date date = new Date();
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			String pdate = dateFormat.format(date);
			String oldRegNumber = bulkdlistBaseRepo.findLatestRegistrationNo();
			String newRegNumber = "BULKDLIST-FORM" + pdate;
			if (oldRegNumber == null || oldRegNumber.isEmpty()) {
				newRegNumber += "0001";
			} else {
				String lastst = oldRegNumber.substring(22, oldRegNumber.length());
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
			dlistBase.setRegistrationNo(newRegNumber);
			dlistBase.setSupportActionTaken("p");
			
			BulkDlistBase dbase = bulkdlistBaseRepo.save(dlistBase);
			
			return dbase;
		}
		return null;
	}

  // for dlist_Moderator table(Owner, Moderator Details)

	public void insertModerator(ArrayList excelfileOwnerData,ArrayList excelfileModeratorData, String regno) {
		
       if(!excelfileOwnerData.isEmpty()) {
			
			String formtype= "owner";
     
			 List<Owners> own = (ArrayList) excelfileOwnerData;
             for (Owners ownerdata  : own) {
            	 ModeratorBase moderatorBase= new ModeratorBase ();
            	 moderatorBase.setRegistrationNo(regno);
            	 moderatorBase.setFormType(formtype);
            	 moderatorBase.setOmName(ownerdata.getOwner_name());
            	 moderatorBase.setOmEmail(ownerdata.getOwner_email());
            	 moderatorBase.setOmMobile(ownerdata.getOwner_mobile());
            	 moderatorBaseRepo.save(moderatorBase);
             }
       }
              if(!excelfileModeratorData.isEmpty()) {
     			
     			String formtype= "moderator";
               
     			 List<Moderators> mod = (ArrayList) excelfileModeratorData;
                  for (Moderators moderatordata  : mod) {
                	  ModeratorBase moderatorBase= new ModeratorBase ();
                 	 moderatorBase.setRegistrationNo(regno);
                 	 moderatorBase.setFormType(formtype);
                 	 moderatorBase.setOmName(moderatordata.getT_off_name());
                 	 moderatorBase.setOmEmail(moderatordata.getTauth_email());
                 	 moderatorBase.setOmMobile(moderatordata.getTmobile());
                 	 
                 	moderatorBaseRepo.save(moderatorBase);
                  } 
		    }
	}

	public boolean updatePreviewDetails(String regNumber, PreviewFormBean previewFormBean) {
		BulkDlistBase dlistBase = bulkdlistBaseRepo.findByRegistrationNo(regNumber);
		if (dlistBase != null) {
			try {
				BeanUtils.copyProperties(previewFormBean, dlistBase);
				LocalDateTime currentTime = LocalDateTime.now();
				BulkDlistBase dlistUpdated = bulkdlistBaseRepo.save(dlistBase);
				if (dlistUpdated.getId() > 0) {
					return true;
				}
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	public boolean updateStatusTable(String regNumber, String currentRole, PreviewFormBean previewFormBean) {
		BulkDlistBase dlistBase = bulkdlistBaseRepo.findByRegistrationNo(regNumber);
		if (dlistBase != null) {
			try {
				BeanUtils.copyProperties(previewFormBean, dlistBase);
				LocalDateTime currentTime = LocalDateTime.now();
			
				BulkDlistBase dlistUpdated = bulkdlistBaseRepo.save(dlistBase);
				if (dlistUpdated.getId() > 0) {
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
		
	
@SuppressWarnings("unchecked")
public FileUploadPojo dummy(String email, FileUploadPojo fileUploadPojo, String registrationNo) {

		ProfileDto profile = utilityService.fetchProfileByEmailInBean(email);
		System.out.println("inside dummy method:");

		String excelFile = Constants.LOCAL_FILE_LOCATION + fileUploadPojo.getFileUploadName();
			
		StringBuilder validContent = new StringBuilder();
		StringBuilder notValidContent = new StringBuilder();
		StringBuilder errorMsg = new StringBuilder();
	    errorMsg.append("ErrorData").append("\n");

		String notValidLloc = Constants.LOCAL_FILE_LOCATION + fileUploadPojo.getFileNamePattern() + "notValid.xls";
		String validLloc = Constants.LOCAL_FILE_LOCATION + fileUploadPojo.getFileNamePattern() + "valid.xls";
		String errormsgloc = Constants.LOCAL_FILE_LOCATION + fileUploadPojo.getFileNamePattern() + "errorMsg.xls";
		//List<BulkDlist> bulkDlist = new ArrayList<>();
		List<Moderators> moderators = new ArrayList<>();
		List<Owners> owners = new ArrayList<>();
		List<BulkDlist> validData = new ArrayList();
		 ArrayList errorList = new ArrayList();
         ArrayList nonvalid = new ArrayList();
         
         Map values = new HashMap();

         List a = new ArrayList();
         a.add("NO DATA FOUND");
	
		try {	
			
	    File file = new File(excelFile);   //creating a new file instance  
            FileInputStream fis = new FileInputStream(file);   //obtaining bytes from the file  
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            XSSFSheet sheet = wb.getSheetAt(0);     //creating a Sheet object to retrieve object  
           int lastRow=sheet.getLastRowNum();
                 
                     int k;
                    for ( k = 1; k <= lastRow; k++) {
                    
                    	boolean flag = true;
                        Cell c0 = wb.getSheetAt(0).getRow(k).getCell(0);  
                        Cell c1 = wb.getSheetAt(0).getRow(k).getCell(1);
                        Cell c2 = wb.getSheetAt(0).getRow(k).getCell(2);
                        Cell c3 = wb.getSheetAt(0).getRow(k).getCell(3);
                        Cell c4 = wb.getSheetAt(0).getRow(k).getCell(4);
                        Cell c5 = wb.getSheetAt(0).getRow(k).getCell(5);
                        Cell c6 = wb.getSheetAt(0).getRow(k).getCell(6);
                        Cell c7 = wb.getSheetAt(0).getRow(k).getCell(7);
                        Cell c8 = wb.getSheetAt(0).getRow(k).getCell(8);
                        Cell c9 = wb.getSheetAt(0).getRow(k).getCell(9);
                        Cell c10 = wb.getSheetAt(0).getRow(k).getCell(10);
                        Cell c11 = wb.getSheetAt(0).getRow(k).getCell(11);
                        Cell c12 = wb.getSheetAt(0).getRow(k).getCell(12);
                        Cell c13 = wb.getSheetAt(0).getRow(k).getCell(13);
 
                        BulkDlist bulkDlist1 = new BulkDlist();
                        Owners owners1 = new Owners();
                        Moderators moderators1 = new Moderators();

                        bulkDlist1.setList_name(c0.toString());
                        bulkDlist1.setDescription_list(c1.toString());
                        if(c2==null){
                        bulkDlist1.setList_mod("");
                        }
                        else{
                        bulkDlist1.setList_mod(c2.toString());
                        }
                        bulkDlist1.setAllowed_member(c3.toString());
                        bulkDlist1.setList_temp(c4.toString());
                        bulkDlist1.setNon_nicnet(c5.toString());
                      
                        
                      if(c6==null){
                          bulkDlist1.setOwner_name("");
                      }
                      else{
                        bulkDlist1.setOwner_name(c6.toString());
                        owners1.setOwner_name(c6.toString());
                      }
                      if(c7==null){
                          bulkDlist1.setOwner_email("");
                      }
                      else{
                        bulkDlist1.setOwner_email(c7.toString());
                        owners1.setOwner_email(c7.toString());
                      }
                      if(c8==null){
                          bulkDlist1.setOwner_mobile("");
                      }
                      else{
                        bulkDlist1.setOwner_mobile(c8.toString());
                        owners1.setOwner_mobile(c8.toString());
                      }
                        if(c9==null)
                        {
                         bulkDlist1.setT_off_name("");
                        }
                        else{
                        bulkDlist1.setT_off_name(c9.toString());
                        moderators1.setT_off_name(c9.toString());
                        }
                        if(c10==null)
                        {
                         bulkDlist1.setTauth_email("");
                        }
                        else{
                        bulkDlist1.setTauth_email(c10.toString());
                        moderators1.setTauth_email(c10.toString());
                        }
                        if(c11==null)
                        {
                         bulkDlist1.setTmobile("");
                        }
                        else{
                        bulkDlist1.setTmobile(c11.toString());
                        moderators1.setTmobile(c11.toString());
                        }      
                        bulkDlist1.setOwner_Admin(c12.toString());
                        bulkDlist1.setModerator_Admin(c13.toString());
                        moderators.add(moderators1);  // for seperate owners details
                        owners.add(owners1);          // for seperate modederators details

                        DlistBulkValidation valid = new DlistBulkValidation();
                        
                        if (bulkDlist1.getList_name() == null) {
                            errorList.add("List name can not be blank. Where row is:" + k + " and column is:1");
                            flag = false;
                        }
                        else 
                        {
                                String msg = valid.listValidation(bulkDlist1.getList_name().toLowerCase().trim());
                            if (!msg.equals("")) {
                                errorList.add(msg + " Where row is:" + k + " and column is:1");
                                flag = false;
                            }
                        }
                        if (bulkDlist1.getDescription_list() == null) {
                            errorList.add("List Discription can not be blank. Where row is:" + k + " and column is:2");
                            flag = false;
                        } else {
                           boolean listdesc=valid.dnstxtValidation(bulkDlist1.getDescription_list().trim());
                            if (listdesc) {
                                errorList.add("Please Enter list decription In correct Formate "+ " Where row is:" + k + " and column is:2");
                                flag = false;
                            }
                        }

                        if (bulkDlist1.getList_mod() == null) {
                            errorList.add("List Moderated can not be blank. Where row is:" + k + " and column is:3");
                            flag = false;
                        } else {
                           boolean listmod = valid.checkradioValidation(bulkDlist1.getList_mod().trim());
                            if (listmod) {
                                errorList.add("Please Enter List Moderated in correct format" + " Where row is:" + k + " and column is:3");
                                flag = false;
                            }
                        }

                        if (bulkDlist1.getAllowed_member() == null) {
                            errorList.add("Is Member Allowed can not be blank. Where row is:" + k + " and column is:4");
                            flag = false;
                        } else {
                            boolean memberallow=valid.checkradioValidation(bulkDlist1.getAllowed_member().trim());
                            if (memberallow) {
                                errorList.add("Please Enter no of allowed members "+ " Where row is:" + k + " and column is:4");
                                flag = false;
                            }
                        }

                        if (bulkDlist1.getList_temp() == null) {
                            errorList.add("Is ListTemp can not be blank. Where row is:" +k + " and column is:5");
                            flag = false;
                        } else {
                            boolean listTemp=valid.checkradioValidation(bulkDlist1.getList_temp().trim());
                            if (listTemp) {
                                errorList.add("Please Enter List temp in correct format" + " Where row is:" + k + " and column is:5");
                                flag = false;
                            }
                        }
                        if (bulkDlist1.getNon_nicnet() == null) {
                            errorList.add("Mail Acceptance can not be blank. Where row is:" + k + " and column is:6");
                            flag = false;
                        } else {
                        	boolean mailact = valid.checkradioValidation(bulkDlist1.getNon_nicnet().trim());
                            if(mailact) {
                                errorList.add("Please Enter Mail Acceptance in correct format" + " Where row is:" + k + " and column is:5");
                                flag = false;
                            }
                        }

                       String email1="";
                          if(!bulkDlist1.getOwner_email().trim().isEmpty()) {
                          HashMap<String, String> valowner = valid.onlyMail(bulkDlist1.getOwner_email().trim());
                            if (valowner.containsValue(errorMsg)) {
                                errorList.add("Please Enter Owner email in correct format" + " Where row is:" + k + " and column is:9");
                                flag = false;
                                email1="ownnotvalid";
                            }
                        }
                        if(bulkDlist1.getTauth_email() != ""||bulkDlist1.getTauth_email() != null||!bulkDlist1.getTauth_email().isEmpty()) {
                          HashMap<String, String> valmod = valid.onlyMail(bulkDlist1.getTauth_email().trim());
                            if (valmod.containsValue(errorMsg)) {
                                errorList.add("Please Enter Moderator email in correct format" + " Where row is:" + k + " and column is:13");
                                flag = false;
                                email="modnotvalid";
                            }
                        }
                      // Email validation error check
                        if (email1!="") {
                        HashMap hm = new LinkedHashMap();
                        hm.clear();   
                        hm.put("List Name", bulkDlist1.getList_name());
                        hm.put("List Discription", bulkDlist1.getDescription_list());
                        hm.put("List Moderated", bulkDlist1.getList_mod());
                        hm.put("IsMemberAllowed", bulkDlist1.getAllowed_member());
                        hm.put("Is ListTemp", bulkDlist1.getList_temp());
                        hm.put("Mail Acceptance", bulkDlist1.getNon_nicnet());
                        hm.put("Owner Name", bulkDlist1.getOwner_name());
                        hm.put("Owner Email", bulkDlist1.getOwner_email());
                        hm.put("Owner Mobile", bulkDlist1.getOwner_mobile());
                        hm.put("Moderator Name", bulkDlist1.getT_off_name());
                        hm.put("Moderator Email", bulkDlist1.getTauth_email());
                        hm.put("Moderator Mobile", bulkDlist1.getTmobile());
                        hm.put("Owner Admin", bulkDlist1.getOwner_Admin());
                        hm.put("Moderator Admin", bulkDlist1.getModerator_Admin());
                        
                            nonvalid.add(hm);
                        }
                       if (flag) {
                    	   BulkDlist bl = new BulkDlist();
                      
                        bl.setList_name(bulkDlist1.getList_name());
                        bl.setDescription_list(bulkDlist1.getDescription_list());
                        bl.setList_mod(bulkDlist1.getList_mod());
                        bl.setAllowed_member(bulkDlist1.getAllowed_member());
                        bl.setList_temp(bulkDlist1.getList_temp());
                        bl.setNon_nicnet(bulkDlist1.getNon_nicnet());
                        bl.setOwner_name(bulkDlist1.getOwner_name());
                        bl.setOwner_email(bulkDlist1.getOwner_email());
                        bl.setOwner_mobile(bulkDlist1.getOwner_mobile());
                        bl.setT_off_name(bulkDlist1.getT_off_name());
                        bl.setTauth_email(bulkDlist1.getTauth_email());
                        bl.setTmobile(bulkDlist1.getTmobile());
                        bl.setOwner_Admin(bulkDlist1.getOwner_Admin());
                        bl.setModerator_Admin(bulkDlist1.getModerator_Admin());
 
                        validData.add(bl);
                        }
                       else 
                        {      // row has error
                        HashMap hm = new HashMap();
                        hm.clear();   
                        hm.put("List Name", bulkDlist1.getList_name());
                        hm.put("List Discription", bulkDlist1.getDescription_list());
                        hm.put("List Moderated", bulkDlist1.getList_mod());
                        hm.put("IsMemberAllowed", bulkDlist1.getAllowed_member());
                        hm.put("Is ListTemp", bulkDlist1.getList_temp());
                        hm.put("Mail Acceptance", bulkDlist1.getNon_nicnet());
                        hm.put("Owner Name", bulkDlist1.getOwner_name());
                        hm.put("Owner Email", bulkDlist1.getOwner_email());
                        hm.put("Owner Mobile", bulkDlist1.getOwner_mobile());
                        hm.put("Moderator Name", bulkDlist1.getT_off_name());
                        hm.put("Moderator Email", bulkDlist1.getTauth_email());
                        hm.put("Moderator Mobile", bulkDlist1.getTmobile());
                        hm.put("Owner Admin", bulkDlist1.getOwner_Admin());
                        hm.put("Moderator Admin", bulkDlist1.getModerator_Admin());
                        
                            errorList.add(hm);
                            
                        }
                    }

			if(!validData.isEmpty()) {
				
				fileUploadPojo.setBulkDlist(validData);
				validContent.append(validData.toString()).append("\n");
			}
			else {
				fileUploadPojo.setBulkDlist(a);
				validContent.append(a.toString()).append("\n");
			}
			
           if(!nonvalid.isEmpty()) {
				
				fileUploadPojo.setNonvalid(nonvalid);
				notValidContent.append(nonvalid.toString()).append("\n");
			}
			
           else {
        	   fileUploadPojo.setNonvalid(a);
        	   notValidContent.append(a.toString()).append("\n");
           }

	        if(!errorList.isEmpty()) {
				
	        	fileUploadPojo.setErrorList(errorList);
	        	errorMsg.append(errorList).append("\n");
			}
	        
	        else {
	        	   fileUploadPojo.setErrorList(a);
	        	   errorMsg.append(a.toString()).append("\n");
	           }
			
			if(!moderators.isEmpty())
			{
				fileUploadPojo.setModerators(moderators);
			}
			if(!owners.isEmpty())
			{
				fileUploadPojo.setOwners(owners);
			}	
			
			fileUploadPojo.setErrorMsg(errormsgloc);
                   
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

			System.out.println(fileUploadPojo);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			
		}
		System.out.println("completed");
		return fileUploadPojo;
	}
	public boolean isFilenameExcelValid(String file) {
		try {
			String fileNameWithOutExt = FilenameUtils.removeExtension(file);
			if (!fileNameWithOutExt.contains(".")) {
				if ((fileNameWithOutExt.matches("^[^*&%]+$")) && (file.endsWith(".xls") || file.endsWith(".xlsx"))) {
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

	public Boolean checkExcelFileSize(String destFile)
	{
		
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
	
	public FileUploadPojo checkExcel(MultipartFile file) {
		FileUploadPojo fileUploadPojo = new FileUploadPojo();

		Date date = new Date();
		DateFormat dt = new SimpleDateFormat("ddMMyyyyHHmmss");
		String pdate = dt.format(date);
		fileUploadPojo.setFileNamePattern(pdate);
		String loc = Constants.LOCAL_FILE_LOCATION;
		String fileName = fileUploadPojo.getFileNamePattern() + ".xls";
		try {
			Files.copy(file.getInputStream(), Paths.get(loc + fileName), StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception e) {
			fileUploadPojo.setErrorMsg("Unable to Save File " + e.getMessage());
			e.getMessage();
		}
		File savedFile = Paths.get(loc + fileName).toFile();
		if (!checkExcelFileSize(savedFile.toString())) {
			
			fileUploadPojo.setFileUploadName(savedFile.getName());
		} else {
			fileUploadPojo.setErrorMsg("Upload file in .xls format with maxmimum 3000 rows.");
		}
		return fileUploadPojo;

	}
	
    // Store Excel file Details in Table
	public void insertExcelFileDetails(ArrayList excelfileData, String regno) {   
	
		BulkDlistBase dlistBase =new BulkDlistBase();
		BulkDlist dl =new BulkDlist();

		if(!excelfileData.isEmpty()) {
     
			 List<BulkDlist> bd = (List<BulkDlist>) excelfileData;
             for (BulkDlist excelfileData1  : bd) {
            	 excelfileData1.setRegistrationNo(regno);
            	 bulkdlistRepo.save(excelfileData1);
             }
		}		
	}

}
