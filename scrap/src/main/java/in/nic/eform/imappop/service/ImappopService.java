package in.nic.eform.imappop.service;

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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import in.nic.eform.imappop.bean.ForwardBean;
import in.nic.eform.imappop.bean.GeneratePdfBean;
import in.nic.eform.imappop.bean.PreviewFormBean;
import in.nic.eform.imappop.bean.SubmissionBean;
import in.nic.eform.imappop.bean.SubmissionFormBean;
import in.nic.eform.imappop.bean.UploadMultipleFilesBean;
import in.nic.eform.imappop.dao.ImappopDao;
import in.nic.eform.imappop.dto.ImappopEmpCoordDTO;
import in.nic.eform.imappop.dto.ImappopFinalAuditTrackDTO;
import in.nic.eform.imappop.dto.ImappopStatusDTO;
import in.nic.eform.imappop.dto.ImappopBaseDTO;
import in.nic.eform.imappop.dto.ImappopDocUploadDTO;
import in.nic.eform.utility.ApiUtility;
import in.nic.eform.utility.Constants;
import in.nic.eform.utility.GetProfileInfo;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@Service
public class ImappopService {
	
	@Value("${fileBasePath}")
	private String EXTERNAL_FILE_PATH;
	@Value("${fileBasePath}")
	private String fileBasePath;
	@Autowired
	ImappopDao imappopDao;
	@Autowired
	GetProfileInfo getProfileInfo;
	@Autowired
	ApiUtility apiUtility;
	
	
	public HttpServletResponse downloadFiles(String filename,HttpServletResponse response)  {
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
	
	
	
	public boolean validateRefNo(String refNo) {
		return imappopDao.validateRefNo(refNo);
	}
	
	
	public Map<String, String> approve(ForwardBean forwardBean) {
		Map<String, String> form_details = null;
		// HashMap<String, Object> profileList = new HashMap<String, Object>();
		Map<String, String> forwardToData = new HashMap<>();
		boolean isPostedInDelhi = false;
		String fetchToEmail = "";
		 boolean isDaAvailable = false;
		System.out.println("regno::::qqqqqqqqqqqq:::::"+forwardBean.getRef_num());
		form_details = imappopDao.fetchFormDetail(forwardBean.getRef_num());
		System.out.println("form_details::::qqqqqqqqqqqqq:::::"+form_details);
		String currentLevel = forwardBean.getRole();
		String submissionType = forwardBean.getCheck();
		
		String ref_num = forwardBean.getRef_num();
		String statRemarks = forwardBean.getStatRemarks();
		String file = forwardBean.getFile();
		String rename_file = forwardBean.getRename_file();
		String app_ca_type = forwardBean.getApp_ca_type();
		String app_ca_path = forwardBean.getApp_ca_path();
		String role = currentLevel;
		String employment = form_details.get("user_employment");
		String state = form_details.get("add_state");
		HashMap<String, Object> profileList = getProfileInfo.fetchprofile();
		
		String applicant_email = forwardBean.getUemail();
		String applicant_name = (String) profileList.get("applicant_name");
		String applicant_mobile = (String) profileList.get("applicant_mobile");
		String hod_email = (String) profileList.get("hod_email");
		String hod_name = (String) profileList.get("hod_name");
		String hod_mobile = (String) profileList.get("hod_mobile");
		
		System.out.println("applicant_email:::::::::"+applicant_email);
		System.out.println("applicant_name:::::::::"+applicant_name);
		System.out.println("applicant_mobile:::::::::"+applicant_mobile);
		System.out.println("hod_email:::::::::"+hod_email);
		System.out.println("hod_name:::::::::"+hod_name);
		System.out.println("hod_mobile:::::::::"+hod_mobile);
		
		boolean isNicEmployee = apiUtility.isNICEmployeeApi(applicant_email);
		boolean isDaEmailASupportID = false;
		String daEmail = "";
		
		if (employment.equals("state") && state.contains("delhi")) {
			isPostedInDelhi = true;
		}
		forwardToData.put("currentlevel", currentLevel);
		
		switch (currentLevel) {
		case Constants.ROLE_USER:
			System.out.println("approve::::ROLE_USER::");
			if(submissionType.equals("online") || submissionType.contains("esign")){
				if (isNicEmployee) {
//                    forwardToData.put("nextlevel", Constants.ROLE_MAILADMIN);
//                    forwardToData.put("toemail", fetchToEmail);
					forwardToData.put("nextlevel", Constants.ROLE_CA);
	                forwardToData.put("toemail", hod_email);
                }else if (employment.equalsIgnoreCase("state") && state.equalsIgnoreCase("Himachal Pradesh")) {
                    Map<String, String> himachalCoordsAsRO = imappopDao.fetchHimachalCoord(form_details.get("department"));
                    forwardToData.put("nextlevel", Constants.ROLE_CA);
                    forwardToData.put("toemail", hod_email);
                    if (himachalCoordsAsRO.size() > 0) {
                        form_details.put("hod_email", himachalCoordsAsRO.get("email"));
                        form_details.put("hod_mobile", apiUtility.findMobilebyId(himachalCoordsAsRO.get("email")));
                        form_details.put("hod_name", himachalCoordsAsRO.get("name"));
                        forwardToData.put("toemail", himachalCoordsAsRO.get("email"));
                    }
                }else {
                    forwardToData.put("nextlevel", Constants.ROLE_CA);
                    forwardToData.put("toemail", hod_email);
                } 
			}
			else if (submissionType.equals("manual_upload")) {
				forwardToData.put("nextlevel", Constants.ROLE_USER);
				forwardToData.put("toemail", applicant_email);
			} else if (submissionType.equals("upload_scanned")) {
				if (form_details != null) {
					if (isNicEmployee) {
//						forwardToData.put("nextlevel", Constants.ROLE_MAILADMIN);
//						forwardToData.put("toemail", forwardDAO.fetchToEmail());
						forwardToData.put("nextlevel", Constants.ROLE_CA);
		                forwardToData.put("toemail", hod_email);
					} else if (employment.equalsIgnoreCase("state") && state.equalsIgnoreCase("Himachal Pradesh")) {
						Map<String, String> himachalCoordsAsRO = imappopDao
								.fetchHimachalCoord(form_details.get("department"));
						forwardToData.put("nextlevel", Constants.ROLE_CA);
						forwardToData.put("toemail", hod_email);
						if (himachalCoordsAsRO.size() > 0) {
							
							//UserForSearch userForSearch = findMobile(himachalCoordsAsRO.get("email"));
							String userForSearch = apiUtility.findMobilebyId(himachalCoordsAsRO.get("email"));
							form_details.put("hod_email", himachalCoordsAsRO.get("email"));
							form_details.put("hod_mobile", userForSearch);
							form_details.put("hod_name", himachalCoordsAsRO.get("name"));
							forwardToData.put("toemail", himachalCoordsAsRO.get("email"));
						}
					} else {
						forwardToData.put("nextlevel", Constants.ROLE_CA);
						forwardToData.put("toemail", hod_email);
					}
				}
			} else {
				System.out.println("approve::::::"+hod_email);
				forwardToData.put("nextlevel", Constants.ROLE_CA);
				forwardToData.put("toemail", hod_email);
			}

			break;
		case Constants.ROLE_CA:
			try {
			Set<String> fetchAliases = new HashSet<String>(apiUtility.fetchAliases(applicant_email));
			Map<String, Object> coordinatorDetails = fetchCoordinators(form_details, role,applicant_email, fetchAliases);
			Set<String> coordinators = (Set<String>) coordinatorDetails.get("coordinators");
			String coemails = imappopDao.convertSetToString(coordinators);
			boolean isApplicantCoordinator = (boolean) coordinatorDetails.get("isApplicantCoordinator");
			boolean isRoCoordinator = (boolean) coordinatorDetails.get("isRoCoordinator");
			boolean isRONICEmployee = apiUtility.isNICEmployeeApi(hod_email);
			boolean isUserNICOutsourced = apiUtility.isNICOutSourcedApi(applicant_email);
			boolean isRoNICOutsourced = apiUtility.isNICOutSourcedApi(hod_email);

			isDaAvailable = (boolean) coordinatorDetails.get("isDaAvailable");
			System.out.println("isDaAvailable::::::::::::::::::::::::::::"+isDaAvailable);
			isDaEmailASupportID = (boolean) coordinatorDetails.get("isDaEmailASupportID");
			System.out.println("isDaEmailASupportID::::::::::::::::::::::::::::"+isDaEmailASupportID);
			daEmail = (String) coordinatorDetails.get("daEmail");
			System.out.println("daEmail::::::::::::::::::::::::::::"+daEmail);
			fetchToEmail = imappopDao.fetchToEmail();
			System.out.println("fetchToEmail::::::::::::::::::::::::::::"+fetchToEmail);
			String dn=apiUtility.getUserDn(applicant_email);
			System.out.println("dn::::::::::::::::::::::::::::"+dn);
			
			   if (submissionType.equals("upload_scanned")) {
                   forwardToData.put("nextlevel", Constants.ROLE_CA);
                   forwardToData.put("toemail", hod_email);
               } else if (dn.contains("gem.gov.in")) {
                   forwardToData.put("nextlevel", Constants.ROLE_DA);
                   forwardToData.put("toemail", Constants.GEM_DA_ADMIN);
               } else if (dn.contains("gem-paid.gov.in")) {
                   forwardToData.put("nextlevel", Constants.ROLE_DA);
                   forwardToData.put("toemail", Constants.GEM_DA_ADMIN_PAID);
               } 
               else if (employment.equalsIgnoreCase("state") && state.equalsIgnoreCase("Himachal Pradesh")) {
                   if (isDaAvailable) {
                       forwardToData.put("nextlevel", Constants.ROLE_DA);
                       forwardToData.put("toemail", daEmail);
                   } else if (isApplicantCoordinator || isRoCoordinator) {
                       forwardToData.put("nextlevel", Constants.ROLE_MAILADMIN);
                       forwardToData.put("toemail", fetchToEmail);
                   } else {
                       forwardToData.put("nextlevel", Constants.ROLE_CO);
                       forwardToData.put("toemail", coemails);
                   }
               }
               else if (employment.equalsIgnoreCase("state") && state.equalsIgnoreCase("Punjab")) {
                   if (coemails.isEmpty()) {
                       if (!daEmail.isEmpty()) {
                           forwardToData.put("nextlevel", Constants.ROLE_DA);
                           forwardToData.put("toemail", daEmail);
                       } else {
                           forwardToData.put("nextlevel", Constants.ROLE_SUP);
                           forwardToData.put("toemail", Constants.INOC_SUPPORT_EMAIL);
                       }
                   } else {
                       forwardToData.put("nextlevel", Constants.ROLE_CO);
                       forwardToData.put("toemail", coemails);
                   }
               }
               else if (isNicEmployee) {
                   forwardToData.put("nextlevel", Constants.ROLE_MAILADMIN);
                   forwardToData.put("toemail", fetchToEmail);
               } else if (isRONICEmployee && isUserNICOutsourced && isPostedInDelhi) {
                   forwardToData.put("nextlevel", Constants.ROLE_MAILADMIN);
                   forwardToData.put("toemail", fetchToEmail);
               } else if (isRoNICOutsourced) {
                   forwardToData.put("nextlevel", Constants.ROLE_SUP);
                   forwardToData.put("toemail", Constants.INOC_SUPPORT_EMAIL);
               }
               else if (isDaAvailable && !isDaEmailASupportID) {
                   forwardToData.put("nextlevel", Constants.ROLE_DA);
                   forwardToData.put("toemail", daEmail);
               } else if (isDaEmailASupportID) {
                   forwardToData.put("nextlevel", Constants.ROLE_SUP);
                   forwardToData.put("toemail", Constants.INOC_SUPPORT_EMAIL);
               } 
               else if (isApplicantCoordinator || isRoCoordinator) {
                   forwardToData.put("nextlevel", Constants.ROLE_MAILADMIN);
                   forwardToData.put("toemail", fetchToEmail);
               } else {
                   forwardToData.put("nextlevel", Constants.ROLE_CO);
                   forwardToData.put("toemail", coemails);
               }
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			break;
		case Constants.ROLE_CO:
			   Set<String> fetchAliases = new HashSet<String>(apiUtility.fetchAliases(applicant_email));
			   Map<String, Object> coordinatorDetails = fetchCoordinators(form_details, role, applicant_email, fetchAliases);
			   daEmail = (String) coordinatorDetails.get("daEmail");
			   isDaAvailable = (boolean) coordinatorDetails.get("isDaAvailable");
               if (isDaAvailable) {
                   forwardToData.put("nextlevel", Constants.ROLE_DA);
                   forwardToData.put("toemail", daEmail);
               } else {
                   forwardToData.put("nextlevel", Constants.ROLE_MAILADMIN);
                   forwardToData.put("toemail", fetchToEmail);
               }
			break;
		case Constants.ROLE_SUP:
			String adminType = forwardBean.getChoose_da_type();
			String toEmails = forwardBean.getToEmailFromSuppportConsole();
			Set<String> correctRecpEmails =new HashSet<>();
			Set<ImappopEmpCoordDTO> correctRecpEmails1 = null;
			String correctRecpEmailsInString = "";
			boolean errorFlag = false;
			if (adminType.equals("da")) {
				correctRecpEmails = imappopDao.isRecipientAdmin(toEmails);
				if (!correctRecpEmails.isEmpty()) {
					correctRecpEmailsInString = imappopDao.convertSetToString(correctRecpEmails);
					forwardToData.put("nextlevel", Constants.ROLE_MAILADMIN);
				} else {
					errorFlag = true;
				}
			} else if (adminType.equals("m")) {
				correctRecpEmails = imappopDao.isRecipientAdmin(toEmails);
				if (!correctRecpEmails.isEmpty()) {
					correctRecpEmailsInString = imappopDao.convertSetToString(correctRecpEmails);
					forwardToData.put("nextlevel", Constants.ROLE_MAILADMIN);
				} else {
					errorFlag = true;
				}
			} else if (adminType.equals("c")) {
				correctRecpEmails1 = imappopDao.isRecipientCoordinator(toEmails);
				for(ImappopEmpCoordDTO forwardEmpCoordDTO : correctRecpEmails1) {
					correctRecpEmails.add(forwardEmpCoordDTO.getEmpcoordemail());
					}
				
				
				if (!correctRecpEmails.isEmpty()) {
					correctRecpEmailsInString = imappopDao.convertSetToString(correctRecpEmails);
					if (correctRecpEmails.contains("support@gov.in") || correctRecpEmails.contains("support@nic.in")
							|| correctRecpEmails.contains("support@dummy.nic.in")) {
						forwardToData.put("nextlevel", Constants.ROLE_MAILADMIN);
						correctRecpEmailsInString = imappopDao.fetchToEmail();
					} else {
						forwardToData.put("nextlevel", Constants.ROLE_CO);
					}
				} else {
					errorFlag = true;
				}
			}
			if (errorFlag) {
				forwardToData.put("nextlevel", "");
				forwardToData.put("toemail", "");
				forwardToData.put("error", "Invalid recipients, Please check and try again");
			} else {
				forwardToData.put("toemail", correctRecpEmailsInString);
			}
			break;
//			String adminType = forwardBean.getChoose_da_type();
//			String toEmails = forwardBean.getToEmailFromSuppportConsole();
//			Set<String> correctRecpEmails = null;
//			String correctRecpEmailsInString = "";
//			boolean errorFlag = false;
//			if (adminType.equals("da")) {
//				correctRecpEmails = forwardDAO.isRecipientAdmin(toEmails);
//				if (!correctRecpEmails.isEmpty()) {
//					correctRecpEmailsInString = forwardDAO.convertSetToString(correctRecpEmails);
//					forwardToData.put("nextlevel", Constants.ROLE_MAILADMIN);
//				} else {
//					errorFlag = true;
//				}
//			} else if (adminType.equals("m")) {
//				correctRecpEmails = forwardDAO.isRecipientAdmin(toEmails);
//				if (!correctRecpEmails.isEmpty()) {
//					correctRecpEmailsInString = forwardDAO.convertSetToString(correctRecpEmails);
//					forwardToData.put("nextlevel", Constants.ROLE_MAILADMIN);
//				} else {
//					errorFlag = true;
//				}
//			} else if (adminType.equals("c")) {
//				correctRecpEmails = forwardDAO.isRecipientCoordinator(toEmails);
//				if (!correctRecpEmails.isEmpty()) {
//					correctRecpEmailsInString = forwardDAO.convertSetToString(correctRecpEmails);
//					if (correctRecpEmails.contains("support@gov.in") || correctRecpEmails.contains("support@nic.in")
//							|| correctRecpEmails.contains("support@dummy.nic.in")) {
//						forwardToData.put("nextlevel", Constants.ROLE_MAILADMIN);
//						correctRecpEmailsInString = forwardDAO.fetchToEmail();
//					} else {
//						forwardToData.put("nextlevel", Constants.ROLE_CO);
//					}
//				} else {
//					errorFlag = true;
//				}
//			}
//
//			if (errorFlag) {
//				forwardToData.put("nextlevel", "");
//				forwardToData.put("toemail", "");
//				forwardToData.put("error", "Invalid recipients, Please check and try again");
//			} else {
//				forwardToData.put("toemail", correctRecpEmailsInString);
//			}
//			break;
		case Constants.ROLE_MAILADMIN:
			forwardToData.put("nextlevel", Constants.COMPLETED);
			forwardToData.put("toemail", "");
			break;
		}
		
		if (updateStatus(ref_num, role, submissionType, statRemarks, file, rename_file, app_ca_type, app_ca_path,
				forwardToData,applicant_name,applicant_email,applicant_mobile,hod_email,hod_name,hod_mobile)) {
			return forwardToData;
		} else {
			return null;
		}

	}

	public boolean updateStatus(String ref_num, String role, String submissionType, String statRemarks, String file,
			String rename_file, String app_ca_type, String app_ca_path, Map<String, String> forwardToData,
			String applicant_name,String applicant_email,String applicant_mobile,String hod_email,String hod_name,String hod_mobile) {
		boolean status = false;
		Map<String, String> forwardBydata = new HashMap<>();
		forwardBydata.put("regNumber", ref_num);
		forwardBydata.put("formType", "imappop");
		forwardBydata.put("forwardedByName", applicant_name);
		forwardBydata.put("forwardedByMobile", applicant_mobile);
		forwardBydata.put("forwardedByEmail", applicant_email);
		forwardBydata.put("role", role);
		String currentLevel = forwardToData.get("currentlevel");
		forwardToData.put("forwardedToName", "");
		forwardToData.put("forwardedToMobile", "");
		forwardToData.put("forwardedToEmail", forwardToData.get("toemail"));
		forwardBydata.put("check", submissionType);
		forwardBydata.put("app_ca_path", app_ca_path);
		forwardBydata.put("app_ca_type", app_ca_type);
		
		System.out.println("---------------------*****************************"+forwardBydata);
		if (statRemarks != null) {
			forwardToData.put("remarks", statRemarks);
		}
		//boolean caIdAvailable = false;
		//int id = 0;
		switch (currentLevel) {

		case Constants.ROLE_USER:
			forwardBydata.put("forwardedBy", "a");

			forwardBydata.put("check", submissionType);
			forwardBydata.put("filePath", file);
			if (submissionType.toString().equals("upload_scanned")) {
				forwardBydata.put("renameFilePath", rename_file);
			}

			break;
		case Constants.ROLE_CA:
			forwardBydata.put("forwardedBy", "ca");
			forwardBydata.put("check", app_ca_type);
			if (submissionType.equals("upload_scanned")) {
				forwardBydata.put("renameFilePath", rename_file);
				forwardBydata.put("filePath", file);
				forwardBydata.put("check", "upload_scanned");
			} else {
				forwardBydata.put("filePath", app_ca_path);
				forwardBydata.put("renameFilePath", rename_file);
			}

			break;
		case Constants.ROLE_CO:
			forwardBydata.put("forwardedBy", "c");
			forwardBydata.put("check", "online");
			forwardBydata.put("filePath", "");

			break;
		case Constants.ROLE_SUP:
			forwardBydata.put("forwardedBy", "s");
			forwardBydata.put("check", "online");
			forwardBydata.put("filePath", "");

			break;
		case Constants.ROLE_DA:
			forwardBydata.put("forwardedBy", "da");
			forwardBydata.put("check", "online");
			forwardBydata.put("filePath", "");

			break;
		case Constants.ROLE_MAILADMIN:
			forwardBydata.put("forwardedBy", "m");
			forwardBydata.put("check", "online");
			forwardBydata.put("filePath", "");

			break;
		}

		switch (forwardToData.get("nextlevel")) {
		case Constants.ROLE_USER:
			forwardToData.put("forwardedTo", "a");
			forwardToData.put("forwardedToName", applicant_name);
			forwardToData.put("forwardedToMobile", applicant_mobile);
			forwardToData.put("forwardedToEmail", applicant_email);
			forwardToData.put("status", Constants.USER_PENDING_STATUS);

			break;
		case Constants.ROLE_CA:
			forwardToData.put("forwardedTo", "ca");
			forwardToData.put("forwardedToName", hod_name);
			forwardToData.put("forwardedToMobile", hod_mobile);
			forwardToData.put("forwardedToEmail", hod_email);
			//caIdAvailable = forwardDAO.isCaIdAlreadyAvailable(hod_email, hod_mobile);
//			if (!caIdAvailable) {
//				id = forwardDAO.insertIntoCompAuth(hod_name, hod_email, hod_mobile);
//				caIdAvailable = true;
//			}
			forwardToData.put("status", Constants.RO_PENDING_STATUS);

			break;
		case Constants.ROLE_CO:
			forwardToData.put("forwardedTo", "c");
			forwardToData.put("status", Constants.CO_PENDING_STATUS);
			break;
		case Constants.ROLE_SUP:
			forwardToData.put("forwardedTo", "s");
			forwardToData.put("forwardedToEmail", Constants.INOC_SUPPORT_EMAIL);
			forwardToData.put("status", Constants.SUPPORT_PENDING_STATUS);

			break;
		case Constants.ROLE_DA:
			forwardToData.put("forwardedTo", "da");
			forwardToData.put("status", Constants.DA_PENDING_STATUS);

			break;
		case Constants.ROLE_MAILADMIN:
			forwardToData.put("forwardedTo", "m");
			forwardToData.put("status", Constants.MAILADMIN_PENDING_STATUS);

			break;
		default:
			forwardToData.put("forwardedTo", "");
			forwardToData.put("status", Constants.COMPLETED);

			break;
		}
		if (updateUsrTrackTable(forwardBydata, forwardToData) > 0) {
			status = true;
		}
		return status;
	}

	
	public int updateUsrTrackTable(Map<String, String> forwardBydata, Map<String, String> forwardToData) {
		String role = forwardBydata.get("role");
		int i = 0, j = 0, k = 0;
//		if (role.equals(Constants.ROLE_USER)) {
//			//i = forwardDAO.insertIntoAppType(forwardBydata, forwardToData);
//			if (forwardBydata.get("check").equalsIgnoreCase("upload_scanned")) {
//				j = forwardDAO.updateFinalAuditTrack(forwardBydata, forwardToData);
//			} else {
//				j = forwardDAO.insertIntoFinalAuditTrack(forwardBydata, forwardToData);
//				System.out.println("j:::::::::"+j);
//			}
//		} else 
			
			if (role.equals(Constants.ROLE_CA)) {
		//	i = forwardDAO.updateAppType(forwardBydata, forwardToData);
			j = imappopDao.updateFinalAuditTrack(forwardBydata, forwardToData);
		} else {
			j = imappopDao.updateFinalAuditTrack(forwardBydata, forwardToData);
		}

		
		 if (j > 0) {
				k = imappopDao.updateStatusTable(forwardBydata, forwardToData);
			}
			 
//		if (role.equals(Constants.ROLE_USER) || role.equals(Constants.ROLE_CA)) {
//			if (i > 0 && j > 0) {
//				System.out.println("condition:::::::::"+j);
//				k = forwardDAO.updateStatusTable(forwardBydata, forwardToData);
//			}
//		} else if (j > 0) {
//			k = forwardDAO.updateStatusTable(forwardBydata, forwardToData);
//		}
		System.out.println("k:::::::::"+k);
		return k;
	}

	public Set<String> isRecipientCoordinator(String email, String table_name) {
		Set<String> arr = new HashSet<>();
		String[] coords;
		if (email.contains(",")) {
			coords = email.split(",");
		} else {
			coords = new String[1];
			coords[0] = email;
		}
		Set<String> aliases = null;
		for (String coord : coords) {
			if (apiUtility.validateEmailApi(coord)) {
				 aliases = new HashSet<String>(apiUtility.fetchAliases(coord));
			} else {
				String[] aliasesInArray = { coord };
				aliases = new HashSet<>(Arrays.asList(aliasesInArray));
			}
			for (String aliase : aliases) {
				Set<ImappopEmpCoordDTO> correctRecpEmails1 = imappopDao.isRecipientCoordinator(aliase);
				for(ImappopEmpCoordDTO forwardEmpCoordDTO : correctRecpEmails1) {
					arr.add(forwardEmpCoordDTO.getEmpcoordemail());
					}
				
			}
		}
		return arr;
	}
	
	
	public Map<String, Object> fetchCoordinators(Map<String, String> form_details, String role, String uemail,
			Set<String> beanAliase) {
		HashMap<String, Object> empCoords = new HashMap<>();
		String punjabCoords = "";
		String punjabDA = "";

		empCoords.put("isDaAvailable", false);
		empCoords.put("isApplicantCoordinator", false);
		empCoords.put("isRoCoordinator", false);
		empCoords.put("isDaEmailASupportID", false);
		Set<String> coordinators = new HashSet<>();
		Set<String> admins = new HashSet<>();
		Set<String> da = new HashSet<>();
		Map<String, String> fetchcoords = new HashMap<>();

		if (form_details.get("user_employment").equalsIgnoreCase("state")
				&& form_details.get("add_state").equalsIgnoreCase("Punjab")) {

			punjabCoords = imappopDao.fetchPunjabNodalOfficers(form_details.get("applicant_posting_city"));
			punjabDA = imappopDao.fetchPunjabDA(form_details.get("user_employment"), form_details.get("min"),
					form_details.get("applicant_department"));
			empCoords.put("coordinators", imappopDao.convertStringToSet(punjabCoords));
			empCoords.put("daEmail", punjabDA);
			empCoords.put("isDaAvailable", true);
		} else {
			fetchcoords = imappopDao.fetchEmailDetails(form_details.get("user_employment"), form_details.get("min"),
					form_details.get("dept"));
			coordinators.add(fetchcoords.get("empcoordemail"));
			admins.add(fetchcoords.get("empadminemail"));
			System.err.println("After Aliases::::::"+fetchcoords);
			/*
			 * Aliases of logged in users fetching from session Logged in user can have any
			 * role. This role is basically the admin role(panel) on which s/he is taking
			 * action
			 */
			Set<String> aliases = beanAliase;
			System.err.println("After Aliases");

			// Aliases of Applicant fetching from ldap
			// Set<String> userAliases = fetchAliases(uemail);
			Set<String> userAliases = new HashSet<String>(apiUtility.fetchAliases(uemail));
			System.err.println("After email");
			// remove support id from coordinators
			Set<String> finalCoords = imappopDao.removeSupportFromSet(coordinators);

			// remove support id from admins
			Set<String> finalAdmins = imappopDao.removeSupportFromSet(admins);
			empCoords.put("coordinators", finalCoords);
			System.err.println("After coordinators");
			/*
			 * Checking if Ro is same as coordinator Setting it true when any of the
			 * multiple coordinators is RO
			 */
			for (String aliase : aliases) {
				if (role.equals("ca")) {
					if (finalCoords.contains(aliase)) {
						empCoords.put("isRoCoordinator", true);
						break;
					}
				}
			}
			System.err.println("After isRoCoordinator alias");
			// Checking if Applicant is same as coordinator
			for (String aliase : userAliases) {
				if (finalCoords.contains(aliase)) {
					empCoords.put("isApplicantCoordinator", true);
					break;
				}
			}
			System.err.println("isApplicantCoordinator");
			if (!finalCoords.isEmpty() && !finalAdmins.isEmpty()) {
				if (finalAdmins.equals(finalCoords)) {
					empCoords.put("isDaAvailable", true);
					empCoords.put("daEmail", imappopDao.convertSetToString(finalAdmins));
				} else {
					boolean flagForDA = false;
					if (form_details.get("user_employment").equalsIgnoreCase("state")
							&& form_details.get("add_state").equalsIgnoreCase("Himachal Pradesh")
							&& finalAdmins.contains(Constants.HIMACHAL_DA_ADMIN)) {
						empCoords.put("daEmail", Constants.HIMACHAL_DA_ADMIN);
						empCoords.put("isDaAvailable", true);
					} else {
						for (String coord : finalCoords) {
							Set<String> alias = new HashSet<String>(apiUtility.fetchAliases(uemail));
							// Set<String> alias = fetchAliases(coord);
							for (String alia : alias) {
								if (finalAdmins.contains(alia)) {
									flagForDA = true;
									da.add(alia);
								}
							}
						}
						if (flagForDA) {
							empCoords.put("isDaAvailable", true);
							empCoords.put("daEmail", imappopDao.convertSetToString(da));
						}
					}
				}
			} else {
				empCoords.put("isDaAvailable", false);
				empCoords.put("isDaEmailASupportID", true);
				empCoords.put("daEmail", "");
			}
		}
		return empCoords;
	}

	public Map<String, Object> validateForward(String stat_type, String srole, String current_user, String recv_date,
			String role) {
		String reqProcessedAs = "", app_date_text = "", status = "";
		Map<String, Object> forwardList = new HashMap<>();
		if (stat_type.toLowerCase().contains("pending") && srole.equalsIgnoreCase("a")) {
			reqProcessedAs = "Approved";
			app_date_text = "Submission Date->" + recv_date;
			status = "Form has been successfully submitted by you (" + current_user + ") and forwarded to (" + role
					+ ")";
			recv_date = "";
		} else if (stat_type.toLowerCase().contains("manual") && srole.equalsIgnoreCase("a")) {
			reqProcessedAs = "Pending";
			app_date_text = "Submission Date->" + recv_date;
			status = "You have chosen manual option to submit. Hence, request is pending with you only. ("
					+ current_user + ")";
			recv_date = "";
		} else if (stat_type.toLowerCase().contains("cancel") && srole.equalsIgnoreCase("a")) {
			reqProcessedAs = "Cancelled";
			app_date_text = "Cancellation Date->" + recv_date;
			status = "Request has been cancelled by you (" + current_user + ").";
			recv_date = "";
		}
		forwardList.put("reqProcessedAs", reqProcessedAs);
		forwardList.put("app_date_text", app_date_text);
		forwardList.put("status", status);
		forwardList.put("recv_date", recv_date);
		return forwardList;
	}

	public Map<String, Object> validateforwardBy(String stat_type, String stat_on_hold, String recv_date, String role) {
		String reqProcessedAs = "", app_date_text = "", status = "";
		Map<String, Object> forwardByList = new HashMap<>();

		if (stat_type.toLowerCase().contains("pending")) {
			if (stat_on_hold.toLowerCase().equalsIgnoreCase("y")) {
				reqProcessedAs = "On Hold";
				app_date_text = "On Hold Since->" + recv_date;
			} else {
				status = "Pending with " + role;
				app_date_text = "Pending Since->" + recv_date;
			}
		} else if (stat_type.toLowerCase().contains("rejected")) {
			status = "Rejected by " + role;
			app_date_text = "Rejection Date->" + recv_date;
		} else if (stat_type.toLowerCase().equalsIgnoreCase("completed")) {
			status = "Completed by " + role;
			app_date_text = "Completion Date->" + recv_date;
		}
		forwardByList.put("reqProcessedAs", reqProcessedAs);
		forwardByList.put("app_date_text", app_date_text);
		forwardByList.put("status", status);
		return forwardByList;
	}

	public Map<String, Object> validateStatus(String stat_process, String forward, String srole, String current_user,
			String recv_date, String s_role, String t_role) {
		String app_date_text = "", status = "";
		Map<String, Object> forwardStatusList = new HashMap<>();
		if (!(stat_process == null || stat_process.isEmpty())) {
			String[] aa = stat_process.split("_");
			String process = aa[0];
			String actionBy = aa[1];
			String actionFor = aa[2];

			if (process.equalsIgnoreCase("pulled")) {
				status = "Pulled by " + findRole(actionBy) + " from " + findRole(actionFor);
			} else if (process.equalsIgnoreCase("reverted")) {
				status = "Reverted by " + findRole(actionBy) + " to " + findRole(actionFor);
			} else if (process.equalsIgnoreCase("forwarded")) {
				status = "Forwarded by " + findRole(actionBy) + " to " + findRole(actionFor);
			}
		} else if (forward.equalsIgnoreCase("a") && srole.equalsIgnoreCase("a")) {
			status = "Submitted by " + s_role + "(" + current_user + ") and forwarded to (" + t_role + ")";
			app_date_text = "Submission Date->" + recv_date;
		} else {
			status = "Approved by " + s_role + "(" + current_user + ") and forwarded to (" + t_role + ")";
			app_date_text = "Approving Date->" + recv_date;
		}

		forwardStatusList.put("status", status);
		forwardStatusList.put("app_date_text", app_date_text);
		return forwardStatusList;
	}

	public Map<String, String> validateRoles(List<ImappopStatusDTO> arrRoles) {
		int i = 0;
		Map<String, String> fetchdetails = new HashMap<>();
		List<String> fetchRoles = new ArrayList<>();
		for (ImappopStatusDTO trackStatus : arrRoles) {
			fetchRoles.add(trackStatus.getStatforwardedby() + "=>" + trackStatus.getStatforwardedto());
			if (i++ == arrRoles.size() - 1) {
				fetchRoles.add(trackStatus.getStatforwardedto());
			}
			Collections.reverse(fetchRoles);

			if (trackStatus.getStat_forwarded_by_email() == null) {
				// fetchdetails.put("senderEmail", userdata.getEmail());
				fetchdetails.put("senderEmail", "BY USERRRRR");
			}
			fetchdetails.put("senderEmail", trackStatus.getStat_forwarded_by_email());
			fetchdetails.put("senderMobile", trackStatus.getStat_forwarded_by_mobile());
			fetchdetails.put("senderName", trackStatus.getStat_forwarded_by_name());
			if (trackStatus.getStat_forwarded_by_name() == null) {
				fetchdetails.put("senderName", "Yourself ");
			}
			fetchdetails.put("submitTime", trackStatus.getStat_forwarded_by_datetime());
		}
		return fetchdetails;
	}

	public Map<String, String> validateDetails(List<ImappopFinalAuditTrackDTO> currentDetails) {
		Map<String, String> details = new HashMap<>();
		String statusVal = "";
		String reqProcessedAs = "";
		String app_date_text = "";
		for (ImappopFinalAuditTrackDTO trackdto : currentDetails) {
			if (trackdto.getStatus().toLowerCase().contains("pending")
					|| trackdto.getStatus().toLowerCase().contains("manual")) {
				details.put("status", "pending");
			} else if (trackdto.getStatus().toLowerCase().contains("rejected")
					|| trackdto.getStatus().toLowerCase().contains("cancel")
					|| trackdto.getStatus().toLowerCase().contains("us_expired")) {
				details.put("status", "reject");
			} else if (trackdto.getStatus().toLowerCase().contains("completed")) {
				details.put("status", "active");
			}
			details.put("applicantname", trackdto.getApplicant_name());
			details.put("applicant_mob", trackdto.getApplicant_mobile());
			details.put("app_email", trackdto.getApplicant_name());
			details.put("form_name", trackdto.getForm_name());
			details.put("app_sub_date", trackdto.getApplicant_datetime());
			details.put("recv_date", trackdto.getTo_datetime());
			details.put("recv_email", trackdto.getToemail());

			if (trackdto.getStatus().toLowerCase().equals("manual_upload")) {
				statusVal = "Pending with User";
			} else if (trackdto.getStatus().toLowerCase().equals("ca_pending")) {
				statusVal = " Pending with RO/Nodal/FO";
			} else if (trackdto.getStatus().toLowerCase().equals("support_pending")) {
				statusVal = " Pending with Support";
			} else if (trackdto.getStatus().toLowerCase().equals("coordinator_pending")) {
				statusVal = " Pending with coordinator";
			} else if (trackdto.getStatus().toLowerCase().equals("mail-admin_pending")) {
				statusVal = " Pending with Admin";
			} else if (trackdto.getStatus().toLowerCase().equals("da_pending")) {
				statusVal = "Pending with DA";
			} else if (trackdto.getStatus().toLowerCase().equals("us_pending")) {
				statusVal = "Pending with US";
			}
			details.put("status_msg", statusVal + "(" + trackdto.getToemail() + ")");
			if (trackdto.getStatus().toLowerCase().contains("pending")) {
				reqProcessedAs = "Pending";
				app_date_text = "Pending Since ";
			} else if (trackdto.getStatus().toLowerCase().contains("rejected")) {
				reqProcessedAs = "Rejected";
				app_date_text = "Rejection Date";
			} else if (trackdto.getStatus().toLowerCase().contains("cancel")) {
				reqProcessedAs = "Cancelled";
				app_date_text = "Cancellation Date";
			} else if (trackdto.getStatus().toLowerCase().contains("completed")) {
				reqProcessedAs = "Completed";
				app_date_text = "Completion Date";
			} else {
				app_date_text = "Submission Date";
			}
			details.put("reqProcessedAs", reqProcessedAs);
			details.put("app_date_text", app_date_text);
		}
		return details;
	}

	public String findRole(String forward) {
		if (forward.equals("")) {
			return "yourself";
		} else if (forward.equals("a")) {
			return "Applicant";
		} else if (forward.matches("c")) {
			return "Coordinator";
		} else if (forward.matches("ca")) {
			return "Reporting/Forwarding/Nodal Officer";
		} else if (forward.equals("d")) {
			return "DA-Admin";
		} else if (forward.equals("m")) {
			return "Admin";
		} else if (forward.equals("s")) {
			return "Support";
		} else if (forward.equals("us")) {
			return "Under Secretary";
		} else {
			return "";
		}
	}

	public String fetchRole(String role) {
		String qr_forwarded_by = "";
		switch (role) {
		case Constants.ROLE_CA:
			qr_forwarded_by = "ca";
			break;
		case Constants.ROLE_CO:
			qr_forwarded_by = "c";
			break;
		case Constants.ROLE_SUP:
			qr_forwarded_by = "s";
			break;
		case Constants.ROLE_MAILADMIN:
			qr_forwarded_by = "m";
			break;
		case Constants.ROLE_USER:
			qr_forwarded_by = "u";
			break;
		}
		return qr_forwarded_by;
	}
	
	
	public JasperPrint generateFormPdf(String regid) throws JRException, IOException, IllegalAccessException, InvocationTargetException {
		JasperPrint jasperPrint=null;
		ImappopBaseDTO generatePdfDTO = imappopDao.findByRegno(regid);
		if (generatePdfDTO!=null) {
			GeneratePdfBean generatePdfBean = new GeneratePdfBean();
			org.springframework.beans.BeanUtils.copyProperties(generatePdfDTO, generatePdfBean);
			File file = ResourceUtils.getFile("classpath:imapreport.jrxml");
			JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
			JRDataSource datasource = new JREmptyDataSource();
			Map<String,Object> parameters = new HashMap<>();
			parameters.put("regid", generatePdfBean.getRegistrationno());
			parameters.put("applicant_name", generatePdfBean.getApplicant_name());
			parameters.put("applicant_email", generatePdfBean.getApplicant_email());
			parameters.put("min", generatePdfBean.getMin());
			parameters.put("applicant_mobile", generatePdfBean.getApplicant_mobile());
			parameters.put("hod_name", generatePdfBean.getHod_name());
			parameters.put("hod_email", generatePdfBean.getHod_email());
			parameters.put("hod_mobile", generatePdfBean.getHod_mobile());
			parameters.put("protocol", generatePdfBean.getProtocol());
			parameters.put("logo", "classpath:static/NIC-Logo.jpg");
			System.out.println(parameters);
			jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,datasource);
		} 
		return jasperPrint;
	
	}
	
	
//	public boolean validateRefNo(String refNo) {
//		return previewDAO.validateRefNo(refNo);
//	}
	
	public Map<String, Object> preview(String regno,String role,String applicant_email) {
		Map<String, Object> map=null;
		System.out.println("preview::::::"+applicant_email);
		Set<String> fetchAliases =new HashSet<String>(apiUtility.fetchAliases(applicant_email));
		System.out.println("fetchAliases::::::"+fetchAliases);
		 boolean isEditable = imappopDao.checkStatusForPreview(regno, applicant_email, role, "toPreview", fetchAliases);
		 System.out.println("isEditable::::::"+isEditable);
		  if (isEditable) {
			  map=imappopDao.preview(regno);
		  }
		  System.out.println("map::::::"+map);
		  return map;
	}
	
	
	
	public Map<String, Object> EditpreviewDetails(String regno,String role,String applicant_email) {
		Map<String, Object> map=null;
		System.out.println("preview::::::"+applicant_email);
		Set<String> fetchAliases =new HashSet<String>(apiUtility.fetchAliases(applicant_email));
		System.out.println("fetchAliases::::::"+fetchAliases);
		 boolean isEditable = imappopDao.checkStatusForEdit(regno, applicant_email, role, "toEdit", fetchAliases);
		 System.out.println("isEditable::::::"+isEditable);
		  if (isEditable) {
			  map= imappopDao.EditPreview(regno);
		  }
		  System.out.println("map::::::"+map);
		  return map;
	}
	
	
	public Map<String, Object> UpdatePreviewDetails(PreviewFormBean previewFormBean) {
		Map<String, Object> map=new HashMap<String, Object>();
	boolean status=false;
	status= imappopDao.UpdatePreviewDetails(previewFormBean);
	 System.out.println("status::::::"+status);
	if(status) {
			  map.put("status", "Preview Updated successfully!!");
			  map.put("RegistrationNo", previewFormBean.getRegistrationno());
		  }else {
			  map.put("status", "Something went wrong!!");
			  map.put("RegistrationNo", previewFormBean.getRegistrationno());
	}
		  System.out.println("map::::::"+map);
		  return map;
	}
	
	
	
	public Map<String, Object> putOnHold(String regNo, String type, String role, String statRemarks) {
		String on_hold="",message="";
		boolean flag=false;
		Map<String, Object> hmTrack = new HashMap<>();

		 if (type.equalsIgnoreCase("true")) {
             on_hold = "y";
             message = "On";
         } else {
             on_hold = "n";
             message = "Off";
         }
		 flag = imappopDao.putOnHold( regNo, role, on_hold,  statRemarks);
         if (flag) {
        	 hmTrack.put("status", "Application put " + message + " Hold successfully");
        	 hmTrack.put("Registration No.", regNo);
         } else {
        	 hmTrack.put("status", "Application could not be put " + message + " Hold");
        	 hmTrack.put("Registration No.", regNo);
         }
		return hmTrack;

	}
	
	
	public  Map<String, Object> raiseQuery(String regid, String role, String uemail,String choose_recp,String to_email,String statRemarks) {
		 Map<String, Object> map=null;
		map = imappopDao.raiseQuery(regid, role, uemail,choose_recp,to_email,statRemarks);
		return map;
	}


	public ArrayList<String> fetchRecps(String regNo, String role) {
		Map<String, Object> map = new HashMap<>();
		 ArrayList<String> roles = new ArrayList<>();
		map=imappopDao.fetchRaiseQueryData(regNo,role);
		 Set<String> recpdata = (Set<String>) map.get("recpdata");
	        
	        for (String string : recpdata) {
	            if (string.contains("=>")) {
	                String arr[] = string.split("=>");
	                roles.add(arr[0]);
	            }
	        }
	        roles.add("u");
		return roles;
	}
	
	
	
	public boolean reject(String reg_no, String role, String statRemarks, String check, String email,
			String mobile, String name) {
		Map<String, Object> hmTrack = new HashMap<>();

		Map<String, String> forwardToData = new HashMap<>();
		forwardToData.put("currentlevel", role);
		forwardToData.put("nextlevel", role);
		forwardToData.put("toemail", email);

		boolean status = false;
		Map<String, String> forwardBydata = new HashMap<>();
		forwardBydata.put("regNumber", reg_no);
		forwardBydata.put("forwardedByName", name);
		forwardBydata.put("forwardedByMobile", mobile);
		forwardBydata.put("forwardedByEmail", email);
		forwardBydata.put("role", role);
		String currentLevel = role;
		forwardToData.put("forwardedToName", "");
		forwardToData.put("forwardedToMobile", "");
		forwardToData.put("forwardedToEmail", email);
		forwardBydata.put("check", check);
		if (statRemarks != null) {
			forwardToData.put("remarks", statRemarks);
		}
		switch (currentLevel) {
		case Constants.ROLE_USER:
			forwardBydata.put("forwardedBy", "a");
			break;
		case Constants.ROLE_CA:
			forwardBydata.put("forwardedBy", "ca");
			break;
		case Constants.ROLE_CO:
			forwardBydata.put("forwardedBy", "c");
			break;
		case Constants.ROLE_SUP:
			forwardBydata.put("forwardedBy", "s");
			break;
		case Constants.ROLE_DA:
			forwardBydata.put("forwardedBy", "da");
			break;
		case Constants.ROLE_MAILADMIN:
			forwardBydata.put("forwardedBy", "m");
			break;
		}

		switch (forwardToData.get("nextlevel")) {
		case Constants.ROLE_USER:
			forwardToData.put("forwardedTo", "a");
			forwardToData.put("forwardedToName", name);
			forwardToData.put("forwardedToMobile", mobile);
			forwardToData.put("forwardedToEmail", email);
			forwardToData.put("status", Constants.CANCELBYUSER);
			forwardToData.put("remarks", Constants.CANCELREMARKSBYUSER);

			break;
		case Constants.ROLE_CA:
			forwardToData.put("forwardedTo", "ca");
			forwardToData.put("forwardedToName", name);
			forwardToData.put("forwardedToMobile", mobile);
			forwardToData.put("forwardedToEmail", email);
			forwardToData.put("status", Constants.STATUS_CA_REJECTED);

			break;
		case Constants.ROLE_CO:
			forwardToData.put("forwardedTo", "c");
			forwardToData.put("status", Constants.STATUS_CO_REJECTED);

			break;
		case Constants.ROLE_SUP:
			forwardToData.put("forwardedTo", "s");
			forwardToData.put("forwardedToEmail", Constants.INOC_SUPPORT_EMAIL);
			forwardToData.put("status", Constants.STATUS_SUP_REJECTED);

			break;
		case Constants.ROLE_DA:
			forwardToData.put("forwardedTo", "da");
			forwardToData.put("status", Constants.STATUS_DA_REJECTED);

			break;
		case Constants.ROLE_MAILADMIN:
			forwardToData.put("forwardedTo", "m");
			forwardToData.put("status", Constants.STATUS_ADMIN_REJECTED);

			break;
		default:
			forwardToData.put("forwardedTo", "");
			forwardToData.put("status", Constants.STATUS_ADMIN_REJECTED);

			break;
		}
		if (imappopDao.updateTrackTable(forwardBydata, forwardToData)) {
			status = true;
		}

		return status;

	}
	
	
	
	public HashMap<String, Object> imappoptab2(SubmissionFormBean submissionFormBean) throws IllegalAccessException, InvocationTargetException {
		HashMap<String, Object> map = new HashMap<>();
		ImappopBaseDTO submissionBaseDTO = new ImappopBaseDTO();
		
		HashMap<String, Object> profileList = getProfileInfo.fetchprofile();
		System.out.println(profileList);
		BeanUtils.populate(submissionFormBean, profileList);
		System.out.println("------------"+profileList);
		System.out.println(submissionBaseDTO.getApplicant_email());

		BeanUtils.copyProperties(submissionBaseDTO, submissionFormBean);
		System.out.println(submissionBaseDTO.getUser_employment());

		String regid = imappopDao.imappoptab2(submissionBaseDTO);
		if(regid!=null) {
			map.put("regid", regid);
			map.put("status", "ok");
			}
			else {
				map.put("regid", "");
				map.put("status", "Something went Wrong");
			}
		return map;
	}
	
	
	
	public Map<String, Object> consent(String regno,String check,String formtype,String consent,String email) {
		Map<String, Object> map = new HashMap<String, Object>();
		   Map<String, Object> consentreturn = new HashMap<>();
		   HashMap<String, Object> profileList = new HashMap<String, Object>();
		   SubmissionBean submissionBean=new SubmissionBean();
		   profileList=getProfileInfo.getInfo(email);
		   
		
	    if (consent.equals("user")) {
            String ref_num =regno;
            
            consentreturn.put("checkreturn", check);
            consentreturn.put("Ref_string", "Your form has been submitted and your Registration number is " + ref_num + ".");
            String file = "";
            if (check.equals("online")) {
                file = "online processing";
            } else if (check.equals("manual_upload") || check.equals("esign")) {
                file = "/eForms/PDF/" + formtype + "/" + ref_num + ".pdf";
            } else if (check.equals("esign_update")) {
//               String email=;
//               String isHog=isHogApi(email);
//               String isHod=isHodApi(email);
              //  file = "/eForms/PDF/esigned/" + (String) session.get("esign_file_path");
            }

            if (!check.equals("esign")) {
            	submissionBean.setCheck(check);
            	submissionBean.setRef_num(ref_num);
            	submissionBean.setFormtype(formtype);
            	submissionBean.setFile(file);
            	submissionBean.setRename_file(file);
                //approveBean.setHodEmails(profileList.get("hod_email").toString());
                
                
            	submissionBean.setHodEmails(profileList.get("hod_email").toString());
            	submissionBean.setUemail(profileList.get("applicant_email").toString());
            	submissionBean.setUmobile(profileList.get("applicant_mobile").toString());
            	submissionBean.setUname(profileList.get("applicant_name").toString());
                
                
                //approveBean.setRole(check);
            	submissionBean.setRole(consent);
            	finalSubmission(submissionBean);
            }
        } else if (consent.equals("RO")) {
//            session.put("ref_num", ref_num);
//            session.put("form_type", formtype);
//            session.put("moduleEsign", "online");
//            session.put("admin_role", "ca");
        }
	 
		
		return consentreturn;
	}
	
	
	
	
	
	
	
	public Map<String, String> finalSubmission(SubmissionBean submissionBean) {
		Map<String, String> form_details = null;
		 HashMap<String, Object> profileList = null;
		Map<String, String> forwardToData = new HashMap<>();
		boolean isPostedInDelhi = false;
		String fetchToEmail = "";
		 boolean isDaAvailable = false;
		System.out.println("regno::::qqqqqqqqqqqq:::::"+submissionBean.getRef_num());
		form_details = imappopDao.fetchFormDetail(submissionBean.getRef_num());
		System.out.println("form_details::::qqqqqqqqqqqqq:::::"+form_details);
		String currentLevel = submissionBean.getRole();
		String submissionType = submissionBean.getCheck();
		
		String ref_num = submissionBean.getRef_num();
		String statRemarks = submissionBean.getStatRemarks();
		String file = submissionBean.getFile();
		String rename_file = submissionBean.getRename_file();
		String app_ca_type = submissionBean.getApp_ca_type();
		String app_ca_path = submissionBean.getApp_ca_path();
		String role = currentLevel;
		String employment = form_details.get("user_employment");
		String state = form_details.get("add_state");
		 profileList=getProfileInfo.fetchprofile();
		
		String applicant_email = submissionBean.getUemail();
		String applicant_name = submissionBean.getUname();
		String applicant_mobile = submissionBean.getUmobile();
		String hod_email = submissionBean.getHodEmails();
		String hod_name =profileList.get("hod_name").toString();
		String hod_mobile =profileList.get("hod_mobile").toString();
		
		System.out.println("applicant_email:::::::::"+applicant_email);
		System.out.println("applicant_name:::::::::"+applicant_name);
		System.out.println("applicant_mobile:::::::::"+applicant_mobile);
		System.out.println("hod_email:::::::::"+hod_email);
		System.out.println("hod_name:::::::::"+hod_name);
		
		boolean isNicEmployee = apiUtility.isNICEmployeeApi(applicant_email);
		boolean isDaEmailASupportID = false;
		String daEmail = "";
		
		if (employment.equals("state") && state.contains("delhi")) {
			isPostedInDelhi = true;
		}
		forwardToData.put("currentlevel", currentLevel);
		
		switch (currentLevel) {
		case Constants.ROLE_USER:
			System.out.println("approve::::ROLE_USER::");
			if(submissionType.equals("online") || submissionType.contains("esign")){
				if (isNicEmployee) {
//                    forwardToData.put("nextlevel", Constants.ROLE_MAILADMIN);
//                    forwardToData.put("toemail", fetchToEmail);
					forwardToData.put("nextlevel", Constants.ROLE_CA);
	                forwardToData.put("toemail", hod_email);
                }
				
				
				else if (employment.equalsIgnoreCase("state") && state.equalsIgnoreCase("Himachal Pradesh")) {
                    Map<String, String> himachalCoordsAsRO = imappopDao.fetchHimachalCoord(form_details.get("department"));
                    forwardToData.put("nextlevel", Constants.ROLE_CA);
                    forwardToData.put("toemail", hod_email);
                    if (himachalCoordsAsRO.size() > 0) {
                        form_details.put("hod_email", himachalCoordsAsRO.get("email"));
                        form_details.put("hod_mobile", apiUtility.findMobilebyId(himachalCoordsAsRO.get("email")));
                        form_details.put("hod_name", himachalCoordsAsRO.get("name"));
                        forwardToData.put("toemail", himachalCoordsAsRO.get("email"));
                    }
                }else {
                    forwardToData.put("nextlevel", Constants.ROLE_CA);
                    forwardToData.put("toemail", hod_email);
                } 
			}
			else if (submissionType.equals("manual_upload")) {
				forwardToData.put("nextlevel", Constants.ROLE_USER);
				forwardToData.put("toemail", applicant_email);
			} else if (submissionType.equals("upload_scanned")) {
				if (form_details != null) {
					if (isNicEmployee) {
//						forwardToData.put("nextlevel", Constants.ROLE_MAILADMIN);
//						forwardToData.put("toemail", submissionDAO.fetchToEmail());
						forwardToData.put("nextlevel", Constants.ROLE_CA);
		                forwardToData.put("toemail", hod_email);
					} else if (employment.equalsIgnoreCase("state") && state.equalsIgnoreCase("Himachal Pradesh")) {
						Map<String, String> himachalCoordsAsRO = imappopDao
								.fetchHimachalCoord(form_details.get("department"));
						forwardToData.put("nextlevel", Constants.ROLE_CA);
						forwardToData.put("toemail", hod_email);
						if (himachalCoordsAsRO.size() > 0) {
							
							//UserForSearch userForSearch = findMobile(himachalCoordsAsRO.get("email"));
							String userForSearch = apiUtility.findMobilebyId(himachalCoordsAsRO.get("email"));
							form_details.put("hod_email", himachalCoordsAsRO.get("email"));
							form_details.put("hod_mobile", userForSearch);
							form_details.put("hod_name", himachalCoordsAsRO.get("name"));
							forwardToData.put("toemail", himachalCoordsAsRO.get("email"));
						}
					} else {
						forwardToData.put("nextlevel", Constants.ROLE_CA);
						forwardToData.put("toemail", hod_email);
					}
				}
			} else {
				System.out.println("approve::::::"+hod_email);
				forwardToData.put("nextlevel", Constants.ROLE_CA);
				forwardToData.put("toemail", hod_email);
			}
			break;
		}
		if (updateStatus(ref_num, role, submissionType, statRemarks, file, rename_file, app_ca_type, app_ca_path,
				forwardToData, form_details,applicant_name,applicant_email,applicant_mobile,hod_email,hod_name,hod_mobile)) {
			return forwardToData;
		} else {
			return null;
		}
	}
	
	
	public boolean updateStatus(String ref_num, String role, String submissionType, String statRemarks, String file,
			String rename_file, String app_ca_type, String app_ca_path, Map<String, String> forwardToData,
			Map<String, String> form_details,String applicant_name,String applicant_email,String applicant_mobile,String hod_email,String hod_name,String hod_mobile) {
		boolean status = false;
		Map<String, String> forwardBydata = new HashMap<>();
		forwardBydata.put("regNumber", ref_num);
		forwardBydata.put("formType", "imappop");
		forwardBydata.put("forwardedByName", applicant_name);
		forwardBydata.put("forwardedByMobile", applicant_mobile);
		forwardBydata.put("forwardedByEmail", applicant_email);
		forwardBydata.put("role", role);
		String currentLevel = forwardToData.get("currentlevel");
		forwardToData.put("forwardedToName", "");
		forwardToData.put("forwardedToMobile", "");
		forwardToData.put("forwardedToEmail", forwardToData.get("toemail"));
		forwardBydata.put("check", submissionType);
		if (statRemarks != null) {
			forwardToData.put("remarks", statRemarks);
		}
		switch (currentLevel) {

		case Constants.ROLE_USER:
			forwardBydata.put("forwardedBy", "a");

			forwardBydata.put("check", submissionType);
			forwardBydata.put("filePath", file);
			if (submissionType.toString().equals("upload_scanned")) {
				forwardBydata.put("renameFilePath", rename_file);
			}

			break;
		case Constants.ROLE_CA:
			forwardBydata.put("forwardedBy", "ca");
			forwardBydata.put("check", app_ca_type);
			if (submissionType.equals("upload_scanned")) {
				forwardBydata.put("renameFilePath", rename_file);
				forwardBydata.put("filePath", file);
				forwardBydata.put("check", "upload_scanned");
			} else {
				forwardBydata.put("filePath", app_ca_path);
				forwardBydata.put("renameFilePath", rename_file);
			}

			break;
		case Constants.ROLE_CO:
			forwardBydata.put("forwardedBy", "c");
			forwardBydata.put("check", "online");
			forwardBydata.put("filePath", "");

			break;
		case Constants.ROLE_SUP:
			forwardBydata.put("forwardedBy", "s");
			forwardBydata.put("check", "online");
			forwardBydata.put("filePath", "");

			break;
		case Constants.ROLE_DA:
			forwardBydata.put("forwardedBy", "da");
			forwardBydata.put("check", "online");
			forwardBydata.put("filePath", "");

			break;
		case Constants.ROLE_MAILADMIN:
			forwardBydata.put("forwardedBy", "m");
			forwardBydata.put("check", "online");
			forwardBydata.put("filePath", "");

			break;
		}

		switch (forwardToData.get("nextlevel")) {
		case Constants.ROLE_USER:
			forwardToData.put("forwardedTo", "a");
			forwardToData.put("forwardedToName", applicant_name);
			forwardToData.put("forwardedToMobile", applicant_mobile);
			forwardToData.put("forwardedToEmail", applicant_email);
			forwardToData.put("status", Constants.USER_PENDING_STATUS);

			break;
		case Constants.ROLE_CA:
			forwardToData.put("forwardedTo", "ca");
			forwardToData.put("forwardedToName", hod_name);
			forwardToData.put("forwardedToMobile", hod_mobile);
			forwardToData.put("forwardedToEmail", hod_email);
			forwardToData.put("status", Constants.RO_PENDING_STATUS);
			break;
		case Constants.ROLE_CO:
			forwardToData.put("forwardedTo", "c");
			forwardToData.put("status", Constants.CO_PENDING_STATUS);
			break;
		case Constants.ROLE_SUP:
			forwardToData.put("forwardedTo", "s");
			forwardToData.put("forwardedToEmail", Constants.INOC_SUPPORT_EMAIL);
			forwardToData.put("status", Constants.SUPPORT_PENDING_STATUS);

			break;
		case Constants.ROLE_DA:
			forwardToData.put("forwardedTo", "da");
			forwardToData.put("status", Constants.DA_PENDING_STATUS);

			break;
		case Constants.ROLE_MAILADMIN:
			forwardToData.put("forwardedTo", "m");
			forwardToData.put("status", Constants.MAILADMIN_PENDING_STATUS);

			break;
		default:
			forwardToData.put("forwardedTo", "");
			forwardToData.put("status", Constants.COMPLETED);

			break;
		}
		if (updateTrackTable(forwardBydata, forwardToData) > 0) {
			status = true;
		}
		return status;
	}
	
	
	
	
//public int updateTrackTable(Map<String, String> forwardBydata, Map<String, String> forwardToData) {
//		String role = forwardBydata.get("role");
//		int i = 0, j = 0, k = 0;
//		if (role.equals(Constants.ROLE_USER)) {
//			i = submissionDAO.insertIntoAppType(forwardBydata, forwardToData);
//			if (forwardBydata.get("check").equalsIgnoreCase("upload_scanned")) {
//				j = submissionDAO.updateFinalAuditTrack(forwardBydata, forwardToData);
//			} else {
//				j = submissionDAO.insertIntoFinalAuditTrack(forwardBydata, forwardToData);
//			}
//		} else if (role.equals(Constants.ROLE_CA)) {
//			i = submissionDAO.updateAppType(forwardBydata, forwardToData);
//			j = submissionDAO.updateFinalAuditTrack(forwardBydata, forwardToData);
//		} else {
//			j = submissionDAO.updateFinalAuditTrack(forwardBydata, forwardToData);
//		}
//
//		if (role.equals(Constants.ROLE_USER) || role.equals(Constants.ROLE_CA)) {
//			if (i > 0 && j > 0) {
//				k = submissionDAO.updateStatusTable(forwardBydata, forwardToData);
//			}
//		} else if (j > 0) {
//			k = submissionDAO.updateStatusTable(forwardBydata, forwardToData);
//		}
//		return k;
//	}

	
public int updateTrackTable(Map<String, String> forwardBydata, Map<String, String> forwardToData) {
		
		String role = forwardBydata.get("role");
	System.out.println("role *************"+role);
		int i = 0, j = 0, k = 0;
		if (role.equals(Constants.ROLE_USER)) {
			i = imappopDao.insertIntoAppType(forwardBydata, forwardToData);
			if (forwardBydata.get("check").equalsIgnoreCase("upload_scanned")) {
				j = imappopDao.updateFinalAuditTrack(forwardBydata, forwardToData);
			} else {
				j = imappopDao.insertIntoFinalAuditTrack(forwardBydata, forwardToData);
				System.out.println("j:::::::::"+j);
			}
		} else if (role.equals(Constants.ROLE_CA)) {
			i = imappopDao.updateAppType(forwardBydata, forwardToData);
			j = imappopDao.updateFinalAuditTrack(forwardBydata, forwardToData);
		} else {
			j = imappopDao.updateFinalAuditTrack(forwardBydata, forwardToData);
		}

		if (role.equals(Constants.ROLE_USER) || role.equals(Constants.ROLE_CA)) {
			if (i > 0 && j > 0) {
				System.out.println("condition:::::::::"+j);
				k = imappopDao.updateStatusTable(forwardBydata, forwardToData);
			}
		} else if (j > 0) {
			System.out.println("");
			k = imappopDao.updateStatusTable(forwardBydata, forwardToData);
		}
		System.out.println("i=****"+i+"j=****"+j);
		System.out.println("k:::::::::"+k);
		return k;
	}



//to view RO details if form details not pass
public Map<String, Object> imappop()  {
	 Map<String, Object> imapBeanList = new HashMap<String, Object>();
	 HashMap<String, Object> profileList = new HashMap<String, Object>();
	 profileList=getProfileInfo.fetchprofile();
	 imapBeanList.put("hod_name", profileList.get("hod_name")) ;
	 imapBeanList.put("hod_email",  profileList.get("hod_email"));
	 imapBeanList.put("hod_mobile", profileList.get("hod_mobile"));
return imapBeanList;
}


public HashMap<String, Object> fetchTrackDetails(String reg_no) {
	int i=0;
	HashMap<String, Object> hmTrack = new HashMap<>();
	HashMap<String, Object> fetchDaoList = new HashMap<>();
	
	fetchDaoList=imappopDao.fetchTrackDetails(reg_no);
	
	List<ImappopStatusDTO> arrRoles = (List<ImappopStatusDTO>) fetchDaoList.get("arrRoles");
	Map<String, String> fetchdetails =validateRolesDtl(arrRoles);
	
	List<ImappopFinalAuditTrackDTO> currentDetails = (List<ImappopFinalAuditTrackDTO>) fetchDaoList.get("currentDetails");
	Map<String, String> details =validateDtls(currentDetails);
	
	String msg = "Status->" + details.get("status_msg") + "=>" + details.get("app_date_text") + "->"
			+ details.get("recv_date") + "=>Sender Details->" + fetchdetails.get("senderName") + "("
			+ fetchdetails.get("senderEmail") + ")";
	hmTrack.put("roles", arrRoles);
	hmTrack.put("status", details.get("status"));
	hmTrack.put("name", details.get("applicantname"));
	hmTrack.put("mob", details.get("applicant_mob"));
	hmTrack.put("email", details.get("app_email"));
	hmTrack.put("form_name", details.get("form_name"));
	hmTrack.put("forword_date", details.get("app_sub_date"));
	hmTrack.put("msg", msg);
	hmTrack.put("reg_no", reg_no);
	return hmTrack;
}



public Map<String, Object> fetchTrackByRole(String reg_no, String forward, String trole, String srole) {
	Map<String, Object> hmTrack = new HashMap<>();
	String reqProcessedAs = "",app_date_text="",status="";
	Map<String, Object> getDaoVal = imappopDao.fetchTrackByRole(reg_no, forward, trole, srole);
	System.out.println("-------------------"+getDaoVal);
	String stat_type = (String) getDaoVal.get("stat_type");
	String stat_on_hold = (String) getDaoVal.get("stat_on_hold");
	String current_user = (String) getDaoVal.get("current_user");
	String stat_process = (String) getDaoVal.get("stat_process");
	String remarks = (String) getDaoVal.get("remarks");
	String recv_date = (String) getDaoVal.get("recv_date");
	String sender_details = (String) getDaoVal.get("sender_details");
	
	if (forward.equalsIgnoreCase("")) {
		String role=findRole(trole);
		Map<String, Object> forwardList =validateForward(stat_type,srole,current_user,recv_date,role);
		reqProcessedAs=(String) forwardList.get("reqProcessedAs");
		app_date_text=(String) forwardList.get("app_date_text");
		status=(String) forwardList.get("status");
		recv_date=(String) forwardList.get("recv_date");
		
	} else if (trole.equalsIgnoreCase("undefined") || trole.isEmpty()) {
		String role=findRole(srole);
		Map<String, Object> forwardByList =validateforwardBy(stat_type,stat_on_hold,recv_date,role);
		reqProcessedAs=(String) forwardByList.get("reqProcessedAs");
		app_date_text=(String) forwardByList.get("app_date_text");
		status=(String) forwardByList.get("status");
		
	} else if (stat_type.toLowerCase().contains("pending")) {
		String s_role=findRole(srole);
		String t_role=findRole(trole);
		Map<String, Object> forwardStatusList =validateStatus(stat_process,forward,srole,current_user,recv_date,s_role,t_role);
		status=(String) forwardStatusList.get("status");
		app_date_text=(String) forwardStatusList.get("app_date_text");
	}
	
	String msg = "Status->" + status + "=>Receiving Date->" + recv_date + "=>" + app_date_text + "=>Remarks->"
			+ remarks + "=>Sender Details->" + sender_details;
	hmTrack.put("msg", msg);
	hmTrack.put("reg_no", reg_no);

	return hmTrack;

}

//public Map<String, Object> validateForward(String stat_type,String srole,String current_user,String recv_date,String role){
//	String reqProcessedAs = "",app_date_text="",status="";
//	Map<String, Object> forwardList = new HashMap<>();
//	if (stat_type.toLowerCase().contains("pending") && srole.equalsIgnoreCase("a")) {
//		reqProcessedAs = "Approved";
//		app_date_text = "Submission Date->" + recv_date;
//		status = "Form has been successfully submitted by you (" + current_user + ") and forwarded to ("
//				+ role + ")";
//		recv_date = "";
//	} else if (stat_type.toLowerCase().contains("manual") && srole.equalsIgnoreCase("a")) {
//		reqProcessedAs = "Pending";
//		app_date_text = "Submission Date->" + recv_date;
//		status = "You have chosen manual option to submit. Hence, request is pending with you only. ("
//				+ current_user + ")";
//		recv_date = "";
//	} else if (stat_type.toLowerCase().contains("cancel") && srole.equalsIgnoreCase("a")) {
//		reqProcessedAs = "Cancelled";
//		app_date_text = "Cancellation Date->" + recv_date;
//		status = "Request has been cancelled by you (" + current_user + ").";
//		recv_date = "";
//	}
//	forwardList.put("reqProcessedAs", reqProcessedAs);
//	forwardList.put("app_date_text", app_date_text);
//	forwardList.put("status", status);
//	forwardList.put("recv_date", recv_date);
//	return forwardList;
//}


//public Map<String, Object> validateforwardBy(String stat_type,String stat_on_hold,String recv_date,String role){
//	String reqProcessedAs = "",app_date_text="",status="";
//	Map<String, Object> forwardByList = new HashMap<>();
//	
//	if (stat_type.toLowerCase().contains("pending")) {
//		if (stat_on_hold.toLowerCase().equalsIgnoreCase("y")) {
//			reqProcessedAs = "On Hold";
//			app_date_text = "On Hold Since->" + recv_date;
//		} else {
//			status = "Pending with " + role;
//			app_date_text = "Pending Since->" + recv_date;
//		}
//	} else if (stat_type.toLowerCase().contains("rejected")) {
//		status = "Rejected by " + role;
//		app_date_text = "Rejection Date->" + recv_date;
//	} else if (stat_type.toLowerCase().equalsIgnoreCase("completed")) {
//		status = "Completed by " + role;
//		app_date_text = "Completion Date->" + recv_date;
//	}
//	forwardByList.put("reqProcessedAs", reqProcessedAs);
//	forwardByList.put("app_date_text", app_date_text);
//	forwardByList.put("status", status);
//	return forwardByList;
//}


//public Map<String, Object> validateStatus(String stat_process,String forward,String srole,String current_user,String recv_date,String s_role,String t_role){
//	String app_date_text="",status="";
//	Map<String, Object> forwardStatusList = new HashMap<>();
//	if (!(stat_process == null || stat_process.isEmpty())) {
//		String[] aa = stat_process.split("_");
//		String process = aa[0];
//		String actionBy = aa[1];
//		String actionFor = aa[2];
//
//		if (process.equalsIgnoreCase("pulled")) {
//			status = "Pulled by " + findRole(actionBy) + " from "
//					+ findRole(actionFor);
//		} else if (process.equalsIgnoreCase("reverted")) {
//			status = "Reverted by " + findRole(actionBy) + " to "
//					+ findRole(actionFor);
//		} else if (process.equalsIgnoreCase("forwarded")) {
//			status = "Forwarded by " + findRole(actionBy) + " to "
//					+ findRole(actionFor);
//		}
//	} else if (forward.equalsIgnoreCase("a") && srole.equalsIgnoreCase("a")) {
//		status = "Submitted by " + s_role + "(" + current_user + ") and forwarded to ("
//				+ t_role + ")";
//		app_date_text = "Submission Date->" + recv_date;
//	} else {
//		status = "Approved by " + s_role + "(" + current_user + ") and forwarded to ("
//				+t_role + ")";
//		app_date_text = "Approving Date->" + recv_date;
//	}
//	
//	forwardStatusList.put("status", status);
//	forwardStatusList.put("app_date_text", app_date_text);
//	return forwardStatusList;
//}


public Map<String, String> validateRolesDtl(List<ImappopStatusDTO> arrRoles){
	int i=0;
	Map<String, String> fetchdetails = new HashMap<>();
	List<String> fetchRoles = new ArrayList<>();
	for (ImappopStatusDTO trackStatus : arrRoles) {
		fetchRoles.add(trackStatus.getStatforwardedby() + "=>" + trackStatus.getStatforwardedto());
		if (i++ == arrRoles.size() - 1) {
			fetchRoles.add(trackStatus.getStatforwardedto());
		}
		Collections.reverse(fetchRoles);

		if (trackStatus.getStat_forwarded_by_email() == null) {
			// fetchdetails.put("senderEmail", userdata.getEmail());
			fetchdetails.put("senderEmail", "BY USERRRRR");
		}
		fetchdetails.put("senderEmail", trackStatus.getStat_forwarded_by_email());
		fetchdetails.put("senderMobile", trackStatus.getStat_forwarded_by_mobile());
		fetchdetails.put("senderName", trackStatus.getStat_forwarded_by_name());
		if (trackStatus.getStat_forwarded_by_name() == null) {
			fetchdetails.put("senderName", "Yourself ");
		}
		fetchdetails.put("submitTime", trackStatus.getStat_forwarded_by_datetime());
	}
	return fetchdetails;
}


public Map<String, String> validateDtls(List<ImappopFinalAuditTrackDTO> currentDetails){
	Map<String, String> details = new HashMap<>();
	String statusVal = "";
	String reqProcessedAs = "";
	String app_date_text = "";
	for (ImappopFinalAuditTrackDTO trackdto : currentDetails) {
		if (trackdto.getStatus().toLowerCase().contains("pending")
				|| trackdto.getStatus().toLowerCase().contains("manual")) {
			details.put("status", "pending");
		} else if (trackdto.getStatus().toLowerCase().contains("rejected")
				|| trackdto.getStatus().toLowerCase().contains("cancel")
				|| trackdto.getStatus().toLowerCase().contains("us_expired")) {
			details.put("status", "reject");
		} else if (trackdto.getStatus().toLowerCase().contains("completed")) {
			details.put("status", "active");
		}
		details.put("applicantname", trackdto.getApplicant_name());
		details.put("applicant_mob", trackdto.getApplicant_mobile());
		details.put("app_email", trackdto.getApplicant_name());
		details.put("form_name", trackdto.getForm_name());
		details.put("app_sub_date", trackdto.getApplicant_datetime());
		details.put("recv_date", trackdto.getTo_datetime());
		details.put("recv_email", trackdto.getToemail());

		if (trackdto.getStatus().toLowerCase().equals("manual_upload")) {
			statusVal = "Pending with User";
		} else if (trackdto.getStatus().toLowerCase().equals("ca_pending")) {
			statusVal = " Pending with RO/Nodal/FO";
		} else if (trackdto.getStatus().toLowerCase().equals("support_pending")) {
			statusVal = " Pending with Support";
		} else if (trackdto.getStatus().toLowerCase().equals("coordinator_pending")) {
			statusVal = " Pending with coordinator";
		} else if (trackdto.getStatus().toLowerCase().equals("mail-admin_pending")) {
			statusVal = " Pending with Admin";
		} else if (trackdto.getStatus().toLowerCase().equals("da_pending")) {
			statusVal = "Pending with DA";
		} else if (trackdto.getStatus().toLowerCase().equals("us_pending")) {
			statusVal = "Pending with US";
		}
		details.put("status_msg", statusVal + "(" + trackdto.getToemail() + ")");
		if (trackdto.getStatus().toLowerCase().contains("pending")) {
			reqProcessedAs = "Pending";
			app_date_text = "Pending Since ";
		} else if (trackdto.getStatus().toLowerCase().contains("rejected")) {
			reqProcessedAs = "Rejected";
			app_date_text = "Rejection Date";
		} else if (trackdto.getStatus().toLowerCase().contains("cancel")) {
			reqProcessedAs = "Cancelled";
			app_date_text = "Cancellation Date";
		} else if (trackdto.getStatus().toLowerCase().contains("completed")) {
			reqProcessedAs = "Completed";
			app_date_text = "Completion Date";
		} else {
			app_date_text = "Submission Date";
		}
		details.put("reqProcessedAs", reqProcessedAs);
		details.put("app_date_text", app_date_text);
	}
	return details;
}

//public String findRole(String forward) {
//	if (forward.equals("")) {
//		return "yourself";
//	} else if (forward.equals("a")) {
//		return "Applicant";
//	} else if (forward.matches("c")) {
//		return "Coordinator";
//	} else if (forward.matches("ca")) {
//		return "Reporting/Forwarding/Nodal Officer";
//	} else if (forward.equals("d")) {
//		return "DA-Admin";
//	} else if (forward.equals("m")) {
//		return "Admin";
//	} else if (forward.equals("s")) {
//		return "Support";
//	} else if (forward.equals("us")) {
//		return "Under Secretary";
//	} else {
//		return "";
//	}
//}

//public String fetchRole(String role) {
//	 String qr_forwarded_by = "";
//    switch (role) {
//    case Constants.ROLE_CA:
//        qr_forwarded_by = "ca";
//        break;
//    case Constants.ROLE_CO:
//        qr_forwarded_by = "c";
//        break;
//    case Constants.ROLE_SUP:
//        qr_forwarded_by = "s";
//        break;
//    case Constants.ROLE_MAILADMIN:
//        qr_forwarded_by = "m";
//        break;
//    case Constants.ROLE_USER:
//        qr_forwarded_by = "u";
//        break;
//}
//	return qr_forwarded_by;
//}


public ArrayList<UploadMultipleFilesBean> saveDocx(UploadMultipleFilesBean uploadfiles) {
	
    ArrayList<UploadMultipleFilesBean> list = new ArrayList<>();
	List<MultipartFile> infile = uploadfiles.getInfile();
	String registrationno=uploadfiles.getRegistrationno();
	String role=uploadfiles.getRole();
	UploadMultipleFilesBean response;
	for (MultipartFile f : infile) {
		if (!f.isEmpty()) {
			String[] contenttype = f.getContentType().split("/");
			String ext = contenttype[1];
			try {
				String outputfile = new StringBuilder().append(registrationno).append("_")
						.append(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
						.append(".").append(ext).toString();
				byte[] bytes = f.getBytes();
				Path path = Paths.get(fileBasePath + outputfile);
				ImappopDocUploadDTO uploadfilesdto = new ImappopDocUploadDTO();
				//BeanUtils.copyProperties(uploadfiles, uploadfilesdto);
				uploadfilesdto.setDoc(outputfile);
				uploadfilesdto.setDocpath(fileBasePath);
				uploadfilesdto.setExtension(ext);
				uploadfilesdto.setOriginal_filename(f.getOriginalFilename());
				uploadfilesdto.setRegistrationno(registrationno);
				uploadfilesdto.setRole(role);
				uploadfilesdto = imappopDao.saveDocx(uploadfilesdto);
				
				System.out.println("::::::::::::" + uploadfilesdto.getId());
				if (uploadfilesdto.getId() == 0) {
				    response=new UploadMultipleFilesBean();
					response.setDoc(f.getOriginalFilename());
					response.setStatus("failed");
					list.add(response);
				}
				else {
					Files.write(path, bytes);
					response=new UploadMultipleFilesBean();
					response.setDoc(f.getOriginalFilename());
					response.setStatus("success");
					list.add(response);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	return list;
}


public Map<String, Map<String, String>> viewDocx(String regid, String role) {
	
	Map<String, Map<String, String>> alldocx = null;
	Map<String, Map<String, String>> files = new HashMap<>();
	role = role.toLowerCase();
	switch (role) {

	case "sup":
		alldocx = imappopDao.viewDocx(regid, "sup");
		alldocx = imappopDao.viewDocx(regid, "admin");
		alldocx = imappopDao.viewDocx(regid, "da");
	case "admin":
	case "da":
		if (!role.equals("sup")) {
			  if (role.equals("da")) {
				  imappopDao.viewDocx(regid, "da");
              } else {
            	  imappopDao.viewDocx(regid, "admin");
              }
			  files.putAll(alldocx);
		}
	case "co":
		alldocx = imappopDao.viewDocx(regid, "co");
		files.putAll(alldocx);
	case "ca":
		alldocx = imappopDao.viewDocx(regid, "ca");
		files.putAll(alldocx);
	case "user":
		alldocx = imappopDao.viewDocx(regid, "user");
		files.putAll(alldocx);
		break;
	}
	System.out.println(files);
	return files;
}
}
