package in.nic.eform.imappop.dao;

import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.nic.eform.imappop.bean.PreviewFormBean;
import in.nic.eform.imappop.dto.ImappopBaseDTO;
import in.nic.eform.imappop.dto.ImappopEmpCoordDTO;
import in.nic.eform.imappop.dto.ImappopFinalAuditTrackDTO;
import in.nic.eform.imappop.dto.ImappopStatusDTO;
import in.nic.eform.imappop.dto.GeneratePdfBaseDTO;
import in.nic.eform.imappop.dto.ImappopQueryRaiseDTO;
import in.nic.eform.imappop.dto.ImappopDocUploadDTO;
import in.nic.eform.imappop.repository.ImappopBaseListRepo;
import in.nic.eform.imappop.repository.ImappopBaseRepo;
import in.nic.eform.imappop.repository.ImappopEmpCoordRepo;
import in.nic.eform.imappop.repository.ImappopFinalAuditTrackListRepo;
import in.nic.eform.imappop.repository.ImappopFinalAuditTrackRepo;
import in.nic.eform.imappop.repository.ImappopMailadminFormsRepo;
import in.nic.eform.imappop.repository.ImappopStatusRepo;
import in.nic.eform.imappop.repository.ImappopQueryRaiseRepo;
import in.nic.eform.imappop.repository.ImappopStatusListRepo;
import in.nic.eform.imappop.repository.ImappopDocUploadRepo;
import in.nic.eform.utility.CommonUtility;
import in.nic.eform.utility.Constants;

@Repository
public class ImappopDao {
	@Autowired
	CommonUtility commonUtility;
	@Autowired
	ImappopBaseRepo imappopBaseRepo;
	@Autowired
	ImappopBaseListRepo imappopBaseListRepo;
	@Autowired
	ImappopFinalAuditTrackRepo imappopFinalAuditTrackRepo;
	@Autowired
	ImappopFinalAuditTrackListRepo imappopFinalAuditTrackListRepo;
	@Autowired
	ImappopStatusRepo imappopStatusRepo;
	@Autowired
	ImappopMailadminFormsRepo imappopMailadminFormsRepo;
	@Autowired
	ImappopEmpCoordRepo imappopEmpCoordRepo;

	@Autowired
	ImappopStatusListRepo imappopStatusListRepo;
	// @Autowired
	// GeneratePdfStatusRepository generatePdfStatusRepository;
	// @Autowired
	// PreviewBaseRepository previewBaseRepository;
	// @Autowired
	// PreviewFinalAuditTrackRepository previewFinalAuditTrackRepository;
	// @Autowired
	// PutOnHoldStatusRepository putOnHoldStatusRepo;
	// @Autowired
	// PutOnHoldFinalAuditTrackRepository putOnHoldFinalAuditTrackRepo;
	@Autowired
	ImappopQueryRaiseRepo imappopQueryRaiseRepo;
	// @Autowired
	// QueryRaiseStatusRepository queryRaiseStatusRepository;
	// @Autowired
	// RejectFinalAuditTrackRepository rejectFinalAuditTrackRepository;
	// @Autowired
	// RejectStatusRepository rejectStatusRepository;
	// @Autowired
	// SubmissionBaseRepository submissionBaseRepository;
	// @Autowired
	// SubmissionFinalAuditTrackRepository submissionFinalAuditTrackRepository;
	/// @Autowired
	// SubmissionStatusRepository submissionStatusRepository;
	// @Autowired
//	SubmissionMailadminFormsRepository submissionMailadminFormsRepository;
//	@Autowired
//	SubmissionEmpCoordRepository submissionEmpCoordRepository;
//	@Autowired
//	TrackStatusRepository trackStatusRepository;
//	@Autowired
//	TrackFinalAuditTrackRepository trackFinalAuditTrackRepository;
//	@Autowired
//	UploadMultiplefilesRepository uploadMultiplefilesRepository;
//	@Autowired
//	ViewPdfFinalAuditTrackRepository viewPdfFinalAuditTrackRepository;
//	@Autowired
//	ViewPdfBaseRepository viewPdfBaseRepository;
	@Autowired
	ImappopDocUploadRepo imappopDocUploadRepo;

//	public boolean validateRefNo(String refNo) {
//		int i=forwardBaseRepository.countByregistrationno(refNo);
//		System.out.println("i::::::"+i);
//		if(i>0) {
//			return false;
//		}else {
//			return true;
//		}
//	}

	public Map<String, String> fetchFormDetail(String regNo) {
		System.out.println("regNo::::::" + regNo);
		Map<String, String> mapbeanMap = null;
		ImappopBaseDTO fetchFormDetail = imappopBaseRepo.findByRegistrationno(regNo);
		ObjectMapper m = new ObjectMapper();
		mapbeanMap = m.convertValue(fetchFormDetail, Map.class);
		System.out.println("mapbeanMap::::::" + mapbeanMap.get("app_name"));

		return mapbeanMap;
	}

	public String fetchToEmail() {
		StringBuilder daEmails = new StringBuilder();
		ArrayList<String> arr = new ArrayList<>();
		String prefix = "";
		try {
			arr = imappopMailadminFormsRepo.findDistinctByMimappop();
			System.out.println("fetchToEmail  arr::::::" + arr);
			for (int i = 0; i < arr.size(); i++) {
				daEmails.append(prefix);
				prefix = ",";
				daEmails.append(prefix);
				daEmails.append(arr.get(i));
			}
			System.out.println("fetchToEmail" + daEmails.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return daEmails.toString();
	}

	public Map<String, String> fetchHimachalCoord(String dept) {
		Map<String, String> coords = new HashMap<>();
		List<ImappopEmpCoordDTO> coords1 = null;
		coords = (Map<String, String>) imappopEmpCoordRepo.findByEmpcoordemailAndEmpadminemailAndEmpdept(
				Constants.HIMACHAL_DA_ADMIN, Constants.HIMACHAL_DA_ADMIN, dept);
		coords.get("emp_coord_email");
		coords.get("emp_coord_name");
		return coords;
	}

	public String convertSetToString(Set<String> co) {
		String string = String.join(", ", co);
		return (string.length() != 0) ? string.toString() : "";
	}

	public Set<String> isRecipientAdmin(String email) {
		return imappopMailadminFormsRepo.findFirstDistinctByMemailAndMimappop(email);
	}

	public Set<ImappopEmpCoordDTO> isRecipientCoordinator(String email) {
		return imappopEmpCoordRepo.findFirstByEmpcoordemail(email);
	}

	public int insertIntoAppType(Map<String, String> forwardBydata, Map<String, String> forwardToData) {
		int i = 0, j = 0;
		if (forwardBydata.get("check").equalsIgnoreCase("upload_scanned")) {
			ImappopBaseDTO forwardBaseDTO = imappopBaseRepo.findByRegistrationno(forwardBydata.get("regNumber"));
			forwardBaseDTO.setSign_cert(forwardBydata.get("filePath"));
			forwardBaseDTO.setRename_sign_cert(forwardBydata.get("renameFilePath"));
			ImappopBaseDTO id = imappopBaseRepo.save(forwardBaseDTO);
			if (id.getId() > 0) {
				i = 1;
			}
			System.out.println("i::::::::::" + i);
		} else {
			ImappopBaseDTO forwardBaseDTO = imappopBaseRepo.findByRegistrationno(forwardBydata.get("regNumber"));
			forwardBaseDTO.setPdf_path(forwardBydata.get("filePath"));
			ImappopBaseDTO id = imappopBaseRepo.save(forwardBaseDTO);
			if (id.getId() > 0) {
				i = 1;
			}
			System.out.println("j::::::::::" + j);
//			if(j>0) {
//				ForwardAppTypeDTO forwardAppTypeDTO=new ForwardAppTypeDTO();
//				forwardAppTypeDTO.setApp_form_type(forwardBydata.get("formType"));
//				forwardAppTypeDTO.setAppregno(forwardBydata.get("regNumber"));
//				forwardAppTypeDTO.setApp_user_type(forwardBydata.get("check"));
//				forwardAppTypeDTO.setApp_user_path(forwardBydata.get("filePath"));
//				forwardAppTypeDTO.setApp_ca_type(forwardBydata.get("check"));
//				forwardAppTypeDTO.setApp_ca_path("");
//				ForwardAppTypeDTO id1=forwardAppTypeRepository.save(forwardAppTypeDTO);
//				if(id1.getApp_id()>0) {
//					i=1;
//				}
//		}
		}
		return i;
	}

	public int updateFinalAuditTrack(Map<String, String> forwardBydata, Map<String, String> forwardToData) {
		int i = 0, j = 0, k = 0;
		SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		Date dt = new Date();
		String datetime = format.format(dt);
		String role = forwardBydata.get("role");
		if (forwardBydata.get("check").equalsIgnoreCase("upload_scanned")) {
			String fwdToEmail = forwardToData.get("forwardedToEmail").replace(",,", ",").replaceFirst("^,", "");
			if (role.equalsIgnoreCase(Constants.ROLE_USER)) {
				ImappopFinalAuditTrackDTO forwardFinalAuditTrackDTO = imappopFinalAuditTrackRepo
						.findByRegistrationno(forwardBydata.get("regNumber"));
				forwardFinalAuditTrackDTO.setStatus(forwardToData.get("status"));
				forwardFinalAuditTrackDTO.setToemail(fwdToEmail);
				forwardFinalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
				forwardFinalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
				forwardFinalAuditTrackDTO.setTo_datetime(datetime);
				forwardFinalAuditTrackDTO.setSign_cert(forwardBydata.get("filePath"));
				forwardFinalAuditTrackDTO.setRename_sign_cert(forwardBydata.get("renameFilePath"));
				forwardFinalAuditTrackDTO.setApp_ca_type(forwardBydata.get("check"));
				ImappopFinalAuditTrackDTO id = imappopFinalAuditTrackRepo.save(forwardFinalAuditTrackDTO);
				if (id.getTrack_id() > 0) {
					j = 1;
				}
				System.out.println("J::::::::::" + j);
			} else {
				ImappopFinalAuditTrackDTO forwardFinalAuditTrackDTO = imappopFinalAuditTrackRepo
						.findByRegistrationno(forwardBydata.get("regNumber"));
				forwardFinalAuditTrackDTO.setStatus(forwardToData.get("status"));
				forwardFinalAuditTrackDTO.setToemail(fwdToEmail);
				forwardFinalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
				forwardFinalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
				forwardFinalAuditTrackDTO.setTo_datetime(datetime);
				forwardFinalAuditTrackDTO.setCa_sign_cert(forwardBydata.get("filePath"));
				forwardFinalAuditTrackDTO.setCa_rename_sign_cert(forwardBydata.get("renameFilePath"));
				forwardFinalAuditTrackDTO.setApp_ca_type(forwardBydata.get("check"));

				ImappopFinalAuditTrackDTO id = imappopFinalAuditTrackRepo.save(forwardFinalAuditTrackDTO);
				if (id.getTrack_id() > 0) {
					j = 1;
				}
				System.out.println("J::::::::::" + j);

			}
		} else {
			String fwdToEmail = forwardToData.get("forwardedToEmail").replace(",,", ",").replaceFirst("^,", "");
//			if (role.equals(Constants.ROLE_USER)) {
//				ForwardFinalAuditTrackDTO forwardFinalAuditTrackDTO = forwardFinalAuditTrackRepository.findByRegistrationno(forwardBydata.get("regNumber"));
//				forwardFinalAuditTrackDTO.setApplicant_email(forwardBydata.get("forwardedByEmail")); 
//				forwardFinalAuditTrackDTO.setApplicant_mobile(forwardBydata.get("forwardedByMobile"));
//				forwardFinalAuditTrackDTO.setApplicant_name(forwardBydata.get("forwardedByName"));
//				forwardFinalAuditTrackDTO.setApplicant_ip("0:0:0:0:0:0:0:1");
//				forwardFinalAuditTrackDTO.setApplicant_datetime(datetime);
//				forwardFinalAuditTrackDTO.setApplicant_remarks( forwardToData.get("remarks"));
//				forwardFinalAuditTrackDTO.setStatus(forwardToData.get("status"));
//				forwardFinalAuditTrackDTO.setTo_email(fwdToEmail);
//				forwardFinalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
//				forwardFinalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
//				ForwardFinalAuditTrackDTO id=forwardFinalAuditTrackRepository.save(forwardFinalAuditTrackDTO);
//				if(id.getTrack_id()>0) {
//					j=1;
//				}
//				System.out.println("ForwardDao:::::updateFinalAuditTrack:::ROLE_USER:::j::"+j);
//			} 

			// else

			if (role.equals(Constants.ROLE_CA)) {
				ImappopFinalAuditTrackDTO forwardFinalAuditTrackDTO = imappopFinalAuditTrackRepo
						.findByRegistrationno(forwardBydata.get("regNumber"));

				System.out.println("*************************" + forwardBydata.get("regNumber"));

				forwardFinalAuditTrackDTO.setCaemail(forwardBydata.get("forwardedByEmail"));
				forwardFinalAuditTrackDTO.setCa_mobile(forwardBydata.get("forwardedByMobile"));
				forwardFinalAuditTrackDTO.setCa_name(forwardBydata.get("forwardedByName"));
				forwardFinalAuditTrackDTO.setCa_ip("0:0:0:0:0:0:0:1");
				forwardFinalAuditTrackDTO.setCa_datetime(datetime);
				forwardFinalAuditTrackDTO.setCa_remarks(forwardToData.get("remarks"));
				forwardFinalAuditTrackDTO.setStatus(forwardToData.get("status"));
				forwardFinalAuditTrackDTO.setToemail(fwdToEmail);
				forwardFinalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
				forwardFinalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
				forwardFinalAuditTrackDTO.setApp_ca_type(forwardBydata.get("app_ca_type"));
				forwardFinalAuditTrackDTO.setApp_ca_path(forwardBydata.get("app_ca_path"));
				ImappopFinalAuditTrackDTO id = imappopFinalAuditTrackRepo.save(forwardFinalAuditTrackDTO);
				if (id.getTrack_id() > 0) {
					j = 1;
				}
				System.out.println("ForwardDao:::::updateFinalAuditTrack@@@@@@@@@@@@@@@@@@@:::ROLE_CA:::j::" + j);
			} else if (role.equals(Constants.ROLE_US)) {
				ImappopFinalAuditTrackDTO forwardFinalAuditTrackDTO = imappopFinalAuditTrackRepo
						.findByRegistrationno(forwardBydata.get("regNumber"));
				forwardFinalAuditTrackDTO.setUs_email(forwardBydata.get("forwardedByEmail"));
				forwardFinalAuditTrackDTO.setUs_mobile(forwardBydata.get("forwardedByMobile"));
				forwardFinalAuditTrackDTO.setUs_name(forwardBydata.get("forwardedByName"));
				forwardFinalAuditTrackDTO.setUs_ip("0:0:0:0:0:0:0:1");
				forwardFinalAuditTrackDTO.setUs_datetime(datetime);
				forwardFinalAuditTrackDTO.setUs_remarks(forwardToData.get("remarks"));
				forwardFinalAuditTrackDTO.setStatus(forwardToData.get("status"));
				forwardFinalAuditTrackDTO.setToemail(fwdToEmail);
				forwardFinalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
				forwardFinalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
				ImappopFinalAuditTrackDTO id = imappopFinalAuditTrackRepo.save(forwardFinalAuditTrackDTO);
				if (id.getTrack_id() > 0) {
					j = 1;
				}
				System.out.println("TrackUserDao:::::updateFinalAuditTrack:::ROLE_US:::j::" + j);
			} else if (role.equals(Constants.ROLE_CO)) {
				ImappopFinalAuditTrackDTO forwardFinalAuditTrackDTO = imappopFinalAuditTrackRepo
						.findByRegistrationno(forwardBydata.get("regNumber"));
				forwardFinalAuditTrackDTO.setCoordinatoremail(forwardBydata.get("forwardedByEmail"));
				forwardFinalAuditTrackDTO.setCoordinator_mobile(forwardBydata.get("forwardedByMobile"));
				forwardFinalAuditTrackDTO.setCoordinator_name(forwardBydata.get("forwardedByName"));
				forwardFinalAuditTrackDTO.setCoordinator_ip("0:0:0:0:0:0:0:1");
				forwardFinalAuditTrackDTO.setCoordinator_datetime(datetime);
				forwardFinalAuditTrackDTO.setCoordinator_remarks(forwardToData.get("remarks"));
				forwardFinalAuditTrackDTO.setStatus(forwardToData.get("status"));
				forwardFinalAuditTrackDTO.setToemail(fwdToEmail);
				forwardFinalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
				forwardFinalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
				ImappopFinalAuditTrackDTO id = imappopFinalAuditTrackRepo.save(forwardFinalAuditTrackDTO);
				if (id.getTrack_id() > 0) {
					j = 1;
				}
				System.out.println("TrackUserDao:::::updateFinalAuditTrack:::ROLE_CO:::j::" + j);
			} else if (role.equals(Constants.ROLE_SUP)) {
				ImappopFinalAuditTrackDTO forwardFinalAuditTrackDTO = imappopFinalAuditTrackRepo
						.findByRegistrationno(forwardBydata.get("regNumber"));
				forwardFinalAuditTrackDTO.setSupportemail(forwardBydata.get("forwardedByEmail"));
				forwardFinalAuditTrackDTO.setSupport_mobile(forwardBydata.get("forwardedByMobile"));
				forwardFinalAuditTrackDTO.setSupport_name(forwardBydata.get("forwardedByName"));
				forwardFinalAuditTrackDTO.setSupport_ip("0:0:0:0:0:0:0:1");
				forwardFinalAuditTrackDTO.setSupport_datetime(datetime);
				forwardFinalAuditTrackDTO.setSupport_remarks(forwardToData.get("remarks"));
				forwardFinalAuditTrackDTO.setStatus(forwardToData.get("status"));
				forwardFinalAuditTrackDTO.setToemail(fwdToEmail);
				forwardFinalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
				forwardFinalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
				ImappopFinalAuditTrackDTO id = imappopFinalAuditTrackRepo.save(forwardFinalAuditTrackDTO);
				if (id.getTrack_id() > 0) {
					j = 1;
				}
				System.out.println("ForwardDao:::::updateFinalAuditTrack:::ROLE_SUP:::j::" + j);
			} else if (role.equals(Constants.ROLE_DA)) {
				ImappopFinalAuditTrackDTO forwardFinalAuditTrackDTO = imappopFinalAuditTrackRepo
						.findByRegistrationno(forwardBydata.get("regNumber"));
				forwardFinalAuditTrackDTO.setDaemail(forwardBydata.get("forwardedByEmail"));
				forwardFinalAuditTrackDTO.setDa_mobile(forwardBydata.get("forwardedByMobile"));
				forwardFinalAuditTrackDTO.setDa_name(forwardBydata.get("forwardedByName"));
				forwardFinalAuditTrackDTO.setDa_ip("0:0:0:0:0:0:0:1");
				forwardFinalAuditTrackDTO.setDa_datetime(datetime);
				forwardFinalAuditTrackDTO.setDa_remarks(forwardToData.get("remarks"));
				forwardFinalAuditTrackDTO.setStatus(forwardToData.get("status"));
				forwardFinalAuditTrackDTO.setToemail(fwdToEmail);
				forwardFinalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
				forwardFinalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
				ImappopFinalAuditTrackDTO id = imappopFinalAuditTrackRepo.save(forwardFinalAuditTrackDTO);
				if (id.getTrack_id() > 0) {
					j = 1;
				}
				System.out.println("ForwardDao:::::updateFinalAuditTrack:::ROLE_DA:::j::" + j);
			} else if (role.equals(Constants.ROLE_MAILADMIN)) {
				ImappopFinalAuditTrackDTO forwardFinalAuditTrackDTO = imappopFinalAuditTrackRepo
						.findByRegistrationno(forwardBydata.get("regNumber"));
				forwardFinalAuditTrackDTO.setAdmin_email(forwardBydata.get("forwardedByEmail"));
				forwardFinalAuditTrackDTO.setAdmin_mobile(forwardBydata.get("forwardedByMobile"));
				forwardFinalAuditTrackDTO.setAdmin_name(forwardBydata.get("forwardedByName"));
				forwardFinalAuditTrackDTO.setAdmin_ip("0:0:0:0:0:0:0:1");
				forwardFinalAuditTrackDTO.setAdmin_datetime(datetime);
				forwardFinalAuditTrackDTO.setAdmin_remarks(forwardToData.get("remarks"));
				forwardFinalAuditTrackDTO.setStatus(forwardToData.get("status"));
				forwardFinalAuditTrackDTO.setToemail(fwdToEmail);
				forwardFinalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
				forwardFinalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
				ImappopFinalAuditTrackDTO id = imappopFinalAuditTrackRepo.save(forwardFinalAuditTrackDTO);
				if (id.getTrack_id() > 0) {
					j = 1;
				}
				System.out.println("ForwardDao:::::updateFinalAuditTrack:::ROLE_ADMIN:::j::" + j);
			}
		}
		return j;
	}

//	public int insertIntoFinalAuditTrack(Map<String, String> forwardBydata, Map<String, String> forwardToData) {
//		int j=0;
//		Date date = new Date();
//		DateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		String pdate = dt.format(date);
//		String ip="0:0:0:0:0:0:0:1";
//		
//		ForwardFinalAuditTrackDTO forwardFinalAuditTrackDTO=new ForwardFinalAuditTrackDTO();
//		forwardFinalAuditTrackDTO.setRegistrationno(forwardBydata.get("regNumber").toString());
//		forwardFinalAuditTrackDTO.setApplicant_email(forwardBydata.get("forwardedByEmail"));
//		forwardFinalAuditTrackDTO.setApplicant_mobile(forwardBydata.get("forwardedByMobile"));
//		forwardFinalAuditTrackDTO.setApplicant_name(forwardBydata.get("forwardedByName"));
//		forwardFinalAuditTrackDTO.setApplicant_ip(ip);
//		forwardFinalAuditTrackDTO.setApplicant_datetime(pdate);
//		forwardFinalAuditTrackDTO.setStatus(forwardToData.get("status"));
//		forwardFinalAuditTrackDTO.setForm_name(forwardBydata.get("formType"));
//		forwardFinalAuditTrackDTO.setTo_email(forwardToData.get("forwardedToEmail").replace(",,", ",").replaceFirst("^,", ""));
//		forwardFinalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
//		forwardFinalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
//		forwardFinalAuditTrackDTO.setTo_datetime(pdate);
//		forwardFinalAuditTrackDTO.setApp_user_type(forwardBydata.get("check"));
//		ForwardFinalAuditTrackDTO i=forwardFinalAuditTrackRepository.save(forwardFinalAuditTrackDTO);
//		  if(i.getTrack_id()>0) {
//	        	j=1;
//	        }
//		  System.out.println("j::::::insertIntoFinalAuditTrack:::::::::::::"+j);
//		return j;
//	}

	public int updateAppType(Map<String, String> forwardBydata, Map<String, String> forwardToData) {
		int i = 0, j = 0;
		Date date = new Date();
		DateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String pdate = dt.format(date);
		if (forwardBydata.get("role").equals(Constants.ROLE_USER)) {
			ImappopBaseDTO forwardBaseDTO = imappopBaseRepo.findByRegistrationno(forwardBydata.get("regNumber"));
			forwardBaseDTO.setPdf_path(forwardBydata.get("filePath"));
			ImappopBaseDTO id = imappopBaseRepo.save(forwardBaseDTO);
			if (id.getId() > 0) {
				j = 1;
			}
//				ForwardAppTypeDTO forwardAppTypeDTO = forwardAppTypeRepository.findByAppregno(forwardBydata.get("regNumber"));
//				forwardAppTypeDTO.setApp_form_type(forwardBydata.get("app_form_type"));
//				forwardAppTypeDTO.setApp_user_type(forwardBydata.get("app_user_type"));
//				forwardAppTypeDTO.setApp_user_path(forwardBydata.get("app_user_path"));
//				forwardAppTypeDTO.setApp_updatedon(pdate);
//				ForwardAppTypeDTO id1=forwardAppTypeRepository.save(forwardAppTypeDTO);
//				if(id1.getApp_id()>0) {
//					i=1;
//				}
			System.out.println("i::::::::::" + i);
		} else if (forwardBydata.get("role").equals(Constants.ROLE_CA)) {
			if (forwardBydata.get("check").equalsIgnoreCase("upload_scanned")) {

				ImappopBaseDTO forwardBaseDTO = imappopBaseRepo.findByRegistrationno(forwardBydata.get("regNumber"));
				forwardBaseDTO.setCa_sign_cert(forwardBydata.get("filePath"));
				forwardBaseDTO.setCa_rename_sign_cert(forwardBydata.get("renameFilePath"));
				ImappopBaseDTO id = imappopBaseRepo.save(forwardBaseDTO);
				if (id.getId() > 0) {
					i = 1;
				}
				System.out.println("i::::::::::::" + i);
			} // else {
//            		ForwardAppTypeDTO forwardAppTypeDTO = forwardAppTypeRepository.findByAppregno(forwardBydata.get("regNumber"));
//    				forwardAppTypeDTO.setApp_form_type(forwardBydata.get("formType"));
//    				forwardAppTypeDTO.setApp_ca_type(forwardBydata.get("check"));
//    				forwardAppTypeDTO.setApp_ca_path(forwardBydata.get("filePath"));
//    				forwardAppTypeDTO.setApp_updatedon(pdate);
//    				ForwardAppTypeDTO id1=forwardAppTypeRepository.save(forwardAppTypeDTO);
//    				if(id1.getApp_id()>0) {
//    					i=1;
//    				}
//    				System.out.println("i::::::::::::"+i);
//               }
		}
		return i;
	}

	public int updateStatusTable(Map<String, String> forwardBydata, Map<String, String> forwardToData) {
		int j = 0;
		SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		Date dt = new Date();
		String datetime = format.format(dt);
		String ip = "0.0.0.0.0.0.0.1";

		ImappopStatusDTO forwardStatusDTO = new ImappopStatusDTO();
		try {
			BeanUtils.populate(forwardStatusDTO, forwardBydata);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		forwardStatusDTO.setStat_form_type(forwardBydata.get("formType"));
		forwardStatusDTO.setStatregno(forwardBydata.get("regNumber"));
		forwardStatusDTO.setStat_type(forwardToData.get("status"));
		forwardStatusDTO.setStatforwardedby(forwardBydata.get("forwardedBy"));
		forwardStatusDTO.setStat_forwarded_by_user(forwardBydata.get("forwardedByEmail"));
		forwardStatusDTO.setStatforwardedto(forwardToData.get("forwardedTo"));
		forwardStatusDTO.setStat_forwarded_to_user(forwardToData.get("forwardedToEmail"));
		forwardStatusDTO.setStat_remarks(forwardToData.get("remarks"));
		forwardStatusDTO.setStatip(ip);
		forwardStatusDTO.setStat_forwarded_by_email(forwardBydata.get("forwardedByEmail"));
		forwardStatusDTO.setStat_forwarded_by_mobile(forwardBydata.get("forwardedByMobile"));
		forwardStatusDTO.setStat_forwarded_by_name(forwardBydata.get("forwardedByName"));
		forwardStatusDTO.setStat_forwarded_by_ip(ip);
		forwardStatusDTO.setStat_forwarded_by_datetime(datetime);
		forwardStatusDTO.setStat_final_id("");
		forwardStatusDTO.setStat_on_hold("n");
		ImappopStatusDTO i = imappopStatusRepo.save(forwardStatusDTO);
		if (i.getStatid() > 0) {
			j = 1;
		}
		System.out.println("j:::::::::::::::::::" + j);
		return j;
	}

	public String fetchPunjabNodalOfficers(String district) {
		String co_email = "", email = "";
		// email = forwardBaseRepository.fetchPunjabNodalOfficers(district);
		if (co_email.isEmpty()) {
			co_email = email;
		} else {
			co_email += "," + email;
		}
		return co_email;
	}

	public String fetchPunjabDA(String employment, String ministry, String department) {
		String da_email = "";
		da_email = fetchPunjabDA(employment, ministry, department);
		if (!da_email.contains(",")) {
			da_email = da_email;
		} else {
			da_email += "," + da_email;
		}
		if (da_email.isEmpty()) {
			da_email = fetchPunjabDA(employment, ministry, "Government of Punjab");
			if (!da_email.contains(",")) {
				da_email = da_email;
			} else {
				da_email += "," + da_email;
			}
		}
		return da_email;
	}

	public Set<String> convertStringToSet(String str) {
		Set<String> elements = new HashSet<>();
		if (!str.isEmpty()) {
			String[] arr = null;
			arr = str.split(",");
			for (int i = 0; i < arr.length; i++) {
				elements.add(arr[i]);
			}
		}
		return elements;
	}

	public Map<String, String> fetchEmailDetails(String employment, String ministry, String department) {
		System.out.println("TrackUserDao:::::::::fetchEmailDetails");
		Map<String, String> map = null;
		System.out.println("################################:::::::::" + imappopEmpCoordRepo
				.findByEmpcategoryAndEmpminstateorgAndEmpdeptAndEmpstatus(employment, ministry, department, "a"));
		ImappopEmpCoordDTO beanlist = imappopEmpCoordRepo
				.findByEmpcategoryAndEmpminstateorgAndEmpdeptAndEmpstatus(employment, ministry, department, "a");
		ObjectMapper m = new ObjectMapper();
		map = m.convertValue(beanlist, Map.class);
		return map;
	}

	public Set<String> removeSupportFromSet(Set<String> coordinators) {
		System.out.println("TrackUserDao:::::::::removeSupportFromSet:::::" + coordinators.size());
		Set<String> coords = new HashSet<>();
		for (String coord : coordinators) {
			System.out.println("coord::::::" + coord);
			if (!verifySupport(coord)) {
				coords.add(coord);
			}
		}
		return coords;
	}

	public boolean verifySupport(String coordinators) {
		System.out.println("TrackUserDao:::::::::verifySupport:::::::" + coordinators);
		if (coordinators.contains("support@nic.in") || coordinators.contains("support@gov.in")
				|| coordinators.contains("support@dummy.nic.in") || coordinators.contains("vpnsupport@nic.in")
				|| coordinators.contains("smssupport@gov.in")) {
			return true;
		}
		return false;
	}

	public Map<String, String> fetchEmpDetails(String regNo) {
		System.out.println("TrackUserDao:::::::::fetchEmploymentDetails");
		Map<String, String> maplist = null;

		ImappopBaseDTO forwardBaseDTO = imappopBaseRepo.findByRegistrationno(regNo);
		ObjectMapper m = new ObjectMapper();
		maplist = m.convertValue(forwardBaseDTO, Map.class);

		return maplist;
	}

	public ImappopBaseDTO findByRegno(String regid) {
		List<ImappopBaseDTO> iterable = imappopBaseListRepo.findByRegistrationno(regid);
		for (ImappopBaseDTO l : iterable) {
			System.out.println(l.getRegistrationno() + "---" + l.getApplicant_email());

			return l;
		}
		return null;
	}

	public boolean validateRefNo(String refNo) {
		int i = imappopBaseRepo.countByregistrationno(refNo);
		System.out.println("i::::::" + i);
		if (i > 0) {
			return false;
		} else {
			return true;
		}
	}

	public boolean checkStatusForEdit(String registrationNumber, String email, String role, String filter,
			Set<String> s) {
		boolean isValid = false;
		if (filter.equals("toEdit")) {
			switch (role) {
			case Constants.ROLE_USER:
				for (String emailAddress1 : s) {
					int i = imappopFinalAuditTrackRepo
							.countByregistrationnoAndApplicantemailAndStatusOrStatusOrStatusOrStatus(registrationNumber,
									emailAddress1, "ca_pending", "manual_upload", "api", "domainapi");
					System.out.println("i:::::::" + i);
					if (i > 0) {
						isValid = true;
					}
				}
				break;
			case Constants.ROLE_CA:
				for (String emailAddress1 : s) {
					int i = imappopFinalAuditTrackRepo.countByregistrationnoAndCaemailAndStatusOrStatusAndToemail(
							registrationNumber, emailAddress1, "coordinator_pending", "ca_pending", emailAddress1);
					System.out.println("i:::::::" + i);
					if (i > 0) {
						isValid = true;
					}
				}
				break;
			case Constants.ROLE_CO:
				for (String emailAddress1 : s) {
					int i = imappopFinalAuditTrackRepo
							.countByregistrationnoAndCoordinatoremailAndStatusOrStatusAndToemail(registrationNumber,
									"%" + emailAddress1 + "%", "mail-admin_pending", "coordinator_pending",
									emailAddress1);
					System.out.println("i:::::::" + i);
					if (i > 0) {
						isValid = true;
					}
				}
				break;
			case Constants.ROLE_SUP:
				for (String emailAddress1 : s) {
					int i = imappopFinalAuditTrackRepo.countByregistrationnoAndSupportemailAndStatusOrStatus(
							registrationNumber, "%" + emailAddress1 + "%", "coordinator_pending", "support_pending");
					System.out.println("i:::::::" + i);
					if (i > 0) {
						isValid = true;
					}
				}
				break;
			case Constants.ROLE_MAILADMIN:
				int i = imappopFinalAuditTrackRepo.countByregistrationnoAndToemailAndStatus(registrationNumber, email,
						"mail-admin_pending");
				System.out.println("i:::::::" + i);
				if (i > 0) {
					isValid = true;
				}
				break;
			default:
			}

		}
		return isValid;
	}

	public boolean checkStatusForPreview(String registrationNumber, String email, String role, String filter,
			Set<String> s) {
		boolean isValid = false;
		if (!(role.equals("admin") || role.equals("sup"))) {
			for (String emailAddress : s) {
				int i = imappopFinalAuditTrackRepo
						.countByregistrationnoAndToemailLikeOrApplicantemailOrCaemailLikeOrCoordinatoremailLike(
								registrationNumber, emailAddress, emailAddress, emailAddress, emailAddress);
				System.out.println("i:::::::" + i);
				if (i > 0) {
					isValid = true;
				}
			}
		}
		return isValid;
	}

	public Map<String, Object> preview(String regno) {
		System.out.println("preview:::::::::preview");
		Map<String, Object> previewlist = null;
		ImappopBaseDTO previewBaseDTO = imappopBaseRepo.findByRegistrationno(regno);
		ObjectMapper m = new ObjectMapper();
		previewlist = m.convertValue(previewBaseDTO, Map.class);
		return previewlist;
	}

	public Map<String, Object> EditPreview(String regno) {
		System.out.println("preview:::::::::preview");
		Map<String, Object> previewlist = preview(regno);
		return previewlist;
	}

	public boolean UpdatePreviewDetails(PreviewFormBean previewFormBean) {
		boolean status = false;
		ImappopBaseDTO previewBaseDTO = imappopBaseRepo.findByRegistrationno(previewFormBean.getRegistrationno());
		try {
			BeanUtils.copyProperties(previewBaseDTO, previewFormBean);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		ImappopBaseDTO a = imappopBaseRepo.save(previewBaseDTO);
		if (a.getId() > 0) {
			status = true;
		}
		return status;
	}

	public boolean putOnHold(String regNo, String role, String on_hold, String statRemarks) {
		int i = 0, j = 0;
		boolean flag = false;
		ImappopStatusDTO putOnHoldStatusDTO = imappopStatusRepo.findByStatregnoAndStatforwardedto(regNo, role);
		putOnHoldStatusDTO.setStat_on_hold(on_hold);
		ImappopStatusDTO id1 = imappopStatusRepo.save(putOnHoldStatusDTO);
		if (id1.getStatid() > 0) {
			i = 1;
		}
		ImappopFinalAuditTrackDTO putOnHoldFinalAuditTrackDTO = imappopFinalAuditTrackRepo.findByRegistrationno(regNo);
		putOnHoldFinalAuditTrackDTO.setOn_hold(on_hold);
		putOnHoldFinalAuditTrackDTO.setHold_remarks(statRemarks);
		ImappopFinalAuditTrackDTO id2 = imappopFinalAuditTrackRepo.save(putOnHoldFinalAuditTrackDTO);
		if (id2.getTrack_id() > 0) {
			j = 1;
		}
		System.out.println("j:::::::" + j);
		System.out.println("i:::::::" + i);

		if (i > 0 && j > 0) {
			flag = true;
		}
		return flag;
	}

	public Map<String, Object> raiseQuery(String regNo, String role, String uemail, String choose_recp, String to_email,
			String statRemarks) {
		int i = 0;
		Map<String, Object> listmap = null;
		ArrayList<String> roles = new ArrayList<>();
		Map<String, Object> map = fetchRaiseQueryData(regNo, role);
		System.out.println("map:::::::" + map);
		Set<String> recpdata = (Set<String>) map.get("recpdata");
		for (String string : recpdata) {
			if (string.contains("=>")) {
				String arr[] = string.split("=>");
				roles.add(arr[0]);
			}
		}
		roles.add("u");
		System.out.println("role:::::::" + role);
		String fetchRole = commonUtility.fetchRole(role);
		System.out.println("fetchRole:::::::" + fetchRole);

		if (roles.contains(choose_recp) && !fetchRole.equals(choose_recp)) {

			String qr_form_type = "imappop";
			String qr_reg_no = regNo;
			String qr_forwarded_by = fetchRole;
			String qr_forwarded_by_user = uemail;
			String qr_forwarded_to = choose_recp;
			String qr_forwarded_to_user = to_email;
			String qr_message = statRemarks;

			ImappopQueryRaiseDTO queryRaiseDTO = new ImappopQueryRaiseDTO();
			queryRaiseDTO.setQr_form_type(qr_form_type);
			queryRaiseDTO.setQrregno(qr_reg_no);
			queryRaiseDTO.setQr_forwarded_by(qr_forwarded_by);
			queryRaiseDTO.setQr_forwarded_by_user(qr_forwarded_by_user);
			queryRaiseDTO.setQr_forwarded_to(qr_forwarded_to);
			queryRaiseDTO.setQr_forwarded_to_user(qr_forwarded_to_user);
			queryRaiseDTO.setQr_message(qr_message);
			ImappopQueryRaiseDTO id = imappopQueryRaiseRepo.save(queryRaiseDTO);
			if (id.getQr_id() > 0) {
				listmap = fetchRaiseQueryData(qr_reg_no, role);
				i = 1;
			}

		}
		return listmap;
	}

	Set<String> fetchAllStakeHolders(String regNo, String role) {

		Set<String> recpdata = new HashSet<>();
		Map<String, String> fetchMap = null;
		List<ImappopStatusDTO> queryRaiseStatusDTO = imappopStatusListRepo.findByStatregno(regNo);
		ObjectMapper m = new ObjectMapper();

		System.out.println("queryRaiseStatusDTO:::" + queryRaiseStatusDTO);

		for (ImappopStatusDTO fetchStatus1 : queryRaiseStatusDTO) {
			System.out.println("fetchStatus1:::" + fetchStatus1);

			String stat_type = fetchStatus1.getStat_type();

			System.out.println("stat_type:::" + stat_type);

			String stat_forwarded_to_user = fetchStatus1.getStat_forwarded_to_user();

			System.out.println("stat_forwarded_to_user:::" + stat_forwarded_to_user);

			System.out.println("role::::::::" + role + ":::::::::stat_type:::::::" + stat_type
					+ ":::::::   stat_forwarded_to_user:::::" + stat_forwarded_to_user);

			recpdata.add("u");
			switch (stat_type) {
			case "ca_pending":
				if (!role.equals(Constants.ROLE_CA)) {

					recpdata.add("ca=>" + stat_forwarded_to_user);
				}
				break;
			case "support_pending":
				if (!role.equals(Constants.ROLE_SUP)) {
					recpdata.add("s=>" + stat_forwarded_to_user);
				}
				break;
			case "co_pending":
				if (!role.equals(Constants.ROLE_CO)) {
					recpdata.add("c=>" + stat_forwarded_to_user);
				}
				break;
			case "da_pending":
				if (!role.equals(Constants.ROLE_DA)) {
					recpdata.add("d=>" + stat_forwarded_to_user);
				}
				break;
			case "mail-admin_pending":
				if (!role.equals(Constants.ROLE_MAILADMIN)) {
					recpdata.add("m=>" + stat_forwarded_to_user);
				}
				break;
			}
		}
		return recpdata;

	}

	public Map<String, Object> fetchRaiseQueryData(String regNo, String role) {
		Map<String, Object> details = new HashMap<>();
		Set<String> recpdata = fetchAllStakeHolders(regNo, role);
		List<String> fetchMap = null;
		List<ImappopQueryRaiseDTO> queryRaiseDTO = imappopQueryRaiseRepo.findByQrregno(regNo);

		System.out.println("recpdata:::::" + recpdata);
		System.out.println("fetchMap:::::" + fetchMap);

		details.put("raiseQueryBtn", true);
		details.put("recpdata", recpdata);
		details.put("querydata", queryRaiseDTO);

//			in case of fetchMap
//			 if (qr_forwarded_by_user.equals(uemail)) {
//	             account_holder = true;
//	         }
		return details;
	}

	public boolean updateTrackTable(Map<String, String> forwardBydata, Map<String, String> forwardToData) {
		boolean retStatus = false;
		SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		Date dt = new Date();
		String datetime = format.format(dt);
		String role = forwardBydata.get("role");
		String fwdToEmail = forwardToData.get("forwardedToEmail").replace(",,", ",").replaceFirst("^,", "");

		int i = 0, j = 0, k = 0;
		if (role.equals(Constants.ROLE_USER)) {

			ImappopFinalAuditTrackDTO rejectFinalAuditTrackDTO = imappopFinalAuditTrackRepo
					.findByRegistrationno(forwardBydata.get("regNumber"));
			rejectFinalAuditTrackDTO.setApplicantemail(forwardBydata.get("forwardedByEmail"));
			rejectFinalAuditTrackDTO.setApplicant_mobile(forwardBydata.get("forwardedByMobile"));
			rejectFinalAuditTrackDTO.setApplicant_name(forwardBydata.get("forwardedByName"));
			rejectFinalAuditTrackDTO.setApplicant_ip("0:0:0:0:0:0:0:1");
			rejectFinalAuditTrackDTO.setApplicant_datetime(datetime);
			rejectFinalAuditTrackDTO.setApplicant_remarks(forwardToData.get("remarks"));
			rejectFinalAuditTrackDTO.setStatus(forwardToData.get("status"));
			rejectFinalAuditTrackDTO.setToemail(fwdToEmail);
			rejectFinalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
			rejectFinalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
			ImappopFinalAuditTrackDTO id = imappopFinalAuditTrackRepo.save(rejectFinalAuditTrackDTO);
			if (id.getTrack_id() > 0) {
				j = 1;
			}
			System.out.println("RejectDAO:::::updateTrackTable@@@@@@@@@@@@@@@@@@@:::ROLE_USER:::j::" + j);
		} else if (role.equals(Constants.ROLE_CA)) {
			ImappopFinalAuditTrackDTO rejectFinalAuditTrackDTO = imappopFinalAuditTrackRepo
					.findByRegistrationno(forwardBydata.get("regNumber"));
			rejectFinalAuditTrackDTO.setCaemail(forwardBydata.get("forwardedByEmail"));
			rejectFinalAuditTrackDTO.setCa_mobile(forwardBydata.get("forwardedByMobile"));
			rejectFinalAuditTrackDTO.setCa_name(forwardBydata.get("forwardedByName"));
			rejectFinalAuditTrackDTO.setCa_ip("0:0:0:0:0:0:0:1");
			rejectFinalAuditTrackDTO.setCa_datetime(datetime);
			rejectFinalAuditTrackDTO.setCa_remarks(forwardToData.get("remarks"));
			rejectFinalAuditTrackDTO.setStatus(forwardToData.get("status"));
			rejectFinalAuditTrackDTO.setToemail(fwdToEmail);
			rejectFinalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
			rejectFinalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
			ImappopFinalAuditTrackDTO id = imappopFinalAuditTrackRepo.save(rejectFinalAuditTrackDTO);
			if (id.getTrack_id() > 0) {
				j = 1;
			}
			System.out.println("RejectDAO:::::updateTrackTable@@@@@@@@@@@@@@@@@@@:::ROLE_CA:::j::" + j);
		} else if (role.equals(Constants.ROLE_US)) {
			ImappopFinalAuditTrackDTO rejectFinalAuditTrackDTO = imappopFinalAuditTrackRepo
					.findByRegistrationno(forwardBydata.get("regNumber"));
			rejectFinalAuditTrackDTO.setUs_email(forwardBydata.get("forwardedByEmail"));
			rejectFinalAuditTrackDTO.setUs_mobile(forwardBydata.get("forwardedByMobile"));
			rejectFinalAuditTrackDTO.setUs_name(forwardBydata.get("forwardedByName"));
			rejectFinalAuditTrackDTO.setUs_ip("0:0:0:0:0:0:0:1");
			rejectFinalAuditTrackDTO.setUs_datetime(datetime);
			rejectFinalAuditTrackDTO.setUs_remarks(forwardToData.get("remarks"));
			rejectFinalAuditTrackDTO.setStatus(forwardToData.get("status"));
			rejectFinalAuditTrackDTO.setToemail(fwdToEmail);
			rejectFinalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
			rejectFinalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
			ImappopFinalAuditTrackDTO id = imappopFinalAuditTrackRepo.save(rejectFinalAuditTrackDTO);
			if (id.getTrack_id() > 0) {
				j = 1;
			}
			System.out.println("RejectDAO:::::updateTrackTable@@@@@@@@@@@@@@@@@@@:::ROLE_US:::j::" + j);
		} else if (role.equals(Constants.ROLE_CO)) {
			ImappopFinalAuditTrackDTO rejectFinalAuditTrackDTO = imappopFinalAuditTrackRepo
					.findByRegistrationno(forwardBydata.get("regNumber"));
			rejectFinalAuditTrackDTO.setCoordinatoremail(forwardBydata.get("forwardedByEmail"));
			rejectFinalAuditTrackDTO.setCoordinator_mobile(forwardBydata.get("forwardedByMobile"));
			rejectFinalAuditTrackDTO.setCoordinator_name(forwardBydata.get("forwardedByName"));
			rejectFinalAuditTrackDTO.setCoordinator_ip("0:0:0:0:0:0:0:1");
			rejectFinalAuditTrackDTO.setCoordinator_datetime(datetime);
			rejectFinalAuditTrackDTO.setCoordinator_remarks(forwardToData.get("remarks"));
			rejectFinalAuditTrackDTO.setStatus(forwardToData.get("status"));
			rejectFinalAuditTrackDTO.setToemail(fwdToEmail);
			rejectFinalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
			rejectFinalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
			ImappopFinalAuditTrackDTO id = imappopFinalAuditTrackRepo.save(rejectFinalAuditTrackDTO);
			if (id.getTrack_id() > 0) {
				j = 1;
			}
			System.out.println("RejectDAO:::::updateTrackTable@@@@@@@@@@@@@@@@@@@:::ROLE_CO:::j::" + j);
		} else if (role.equals(Constants.ROLE_SUP)) {

			ImappopFinalAuditTrackDTO rejectFinalAuditTrackDTO = imappopFinalAuditTrackRepo
					.findByRegistrationno(forwardBydata.get("regNumber"));
			rejectFinalAuditTrackDTO.setSupportemail(forwardBydata.get("forwardedByEmail"));
			rejectFinalAuditTrackDTO.setSupport_mobile(forwardBydata.get("forwardedByMobile"));
			rejectFinalAuditTrackDTO.setSupport_name(forwardBydata.get("forwardedByName"));
			rejectFinalAuditTrackDTO.setSupport_ip("0:0:0:0:0:0:0:1");
			rejectFinalAuditTrackDTO.setSupport_datetime(datetime);
			rejectFinalAuditTrackDTO.setSupport_remarks(forwardToData.get("remarks"));
			rejectFinalAuditTrackDTO.setStatus(forwardToData.get("status"));
			rejectFinalAuditTrackDTO.setToemail(fwdToEmail);
			rejectFinalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
			rejectFinalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
			ImappopFinalAuditTrackDTO id = imappopFinalAuditTrackRepo.save(rejectFinalAuditTrackDTO);
			if (id.getTrack_id() > 0) {
				j = 1;
			}
			System.out.println("RejectDAO:::::updateTrackTable@@@@@@@@@@@@@@@@@@@:::ROLE_SUP:::j::" + j);
		} else if (role.equals(Constants.ROLE_DA)) {

			ImappopFinalAuditTrackDTO rejectFinalAuditTrackDTO = imappopFinalAuditTrackRepo
					.findByRegistrationno(forwardBydata.get("regNumber"));
			rejectFinalAuditTrackDTO.setDaemail(forwardBydata.get("forwardedByEmail"));
			rejectFinalAuditTrackDTO.setDa_mobile(forwardBydata.get("forwardedByMobile"));
			rejectFinalAuditTrackDTO.setDa_name(forwardBydata.get("forwardedByName"));
			rejectFinalAuditTrackDTO.setDa_ip("0:0:0:0:0:0:0:1");
			rejectFinalAuditTrackDTO.setDa_datetime(datetime);
			rejectFinalAuditTrackDTO.setDa_remarks(forwardToData.get("remarks"));
			rejectFinalAuditTrackDTO.setStatus(forwardToData.get("status"));
			rejectFinalAuditTrackDTO.setToemail(fwdToEmail);
			rejectFinalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
			rejectFinalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
			ImappopFinalAuditTrackDTO id = imappopFinalAuditTrackRepo.save(rejectFinalAuditTrackDTO);
			if (id.getTrack_id() > 0) {
				j = 1;
			}
			System.out.println("RejectDAO:::::updateTrackTable@@@@@@@@@@@@@@@@@@@:::ROLE_DA:::j::" + j);
		} else if (role.equals(Constants.ROLE_MAILADMIN)) {
			ImappopFinalAuditTrackDTO rejectFinalAuditTrackDTO = imappopFinalAuditTrackRepo
					.findByRegistrationno(forwardBydata.get("regNumber"));
			rejectFinalAuditTrackDTO.setAdmin_email(forwardBydata.get("forwardedByEmail"));
			rejectFinalAuditTrackDTO.setAdmin_mobile(forwardBydata.get("forwardedByMobile"));
			rejectFinalAuditTrackDTO.setAdmin_name(forwardBydata.get("forwardedByName"));
			rejectFinalAuditTrackDTO.setAdmin_ip("0:0:0:0:0:0:0:1");
			rejectFinalAuditTrackDTO.setAdmin_datetime(datetime);
			rejectFinalAuditTrackDTO.setAdmin_remarks(forwardToData.get("remarks"));
			rejectFinalAuditTrackDTO.setStatus(forwardToData.get("status"));
			rejectFinalAuditTrackDTO.setToemail(fwdToEmail);
			rejectFinalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
			rejectFinalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
			ImappopFinalAuditTrackDTO id = imappopFinalAuditTrackRepo.save(rejectFinalAuditTrackDTO);
			if (id.getTrack_id() > 0) {
				j = 1;
			}
			System.out.println("RejectDAO:::::updateTrackTable@@@@@@@@@@@@@@@@@@@:::ROLE_MAILADMIN:::j::" + j);
		}
		ImappopStatusDTO rejectStatusDTO = new ImappopStatusDTO();
		rejectStatusDTO.setStat_forwarded_by_email(forwardBydata.get("forwardedByEmail"));
		rejectStatusDTO.setStat_forwarded_by_mobile(forwardBydata.get("forwardedByMobile"));
		rejectStatusDTO.setStat_forwarded_by_name(forwardBydata.get("forwardedByName"));
		rejectStatusDTO.setStatip("0:0:0:0:0:0:0:1");
		rejectStatusDTO.setStat_createdon(datetime);
		rejectStatusDTO.setStat_remarks(forwardToData.get("remarks"));
		rejectStatusDTO.setStat_type(forwardToData.get("status"));
		rejectStatusDTO.setStatforwardedto(fwdToEmail);
		rejectStatusDTO.setStatregno(forwardBydata.get("regNumber"));
		rejectStatusDTO.setStat_on_hold("n");
		ImappopStatusDTO id = imappopStatusRepo.save(rejectStatusDTO);
		if (id.getStatid() > 0) {
			k = 1;
		}
		System.out.println("k:::::::" + k);
		if (j > 0) {
			retStatus = true;
		}
		return retStatus;
	}

	public String imappoptab2(ImappopBaseDTO submissionBaseDTO) {
		String dbrefno = "", newref = "";
		int newrefno;
		Date date1 = new Date();
		DateFormat dateFormat1 = new SimpleDateFormat("yyyyMMdd");
		String pdate1 = dateFormat1.format(date1);

//	         SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//	 		Date dt = new Date();
//	 		String datetime = format.format(dt);
//	 	    java.sql.Date d=java.sql.Date.valueOf(datetime);//converting string into sql date  
//	         System.out.println("datetime:::::::::::"+datetime);

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date dt = new Date();
		String datetime = format.format(dt);
		// List<SubmissionBaseDTO>
		// sbd=submissionBaseRepository.findTopByDatetimeOrderByRegistrationnoDesc(datetime);
		List<ImappopBaseDTO> sbd = imappopBaseRepo.findByDatetimeLike("%" + datetime + "%");
		System.out.println("sbd:::::::::::" + sbd);

		for (ImappopBaseDTO subBaseDTO : sbd)
			dbrefno = subBaseDTO.getRegistrationno();

		System.out.println("dbrefno:::::::::::" + dbrefno);

		// dbrefno=submissionBaseRepository.fetchRegNo();
		if (dbrefno == null || dbrefno.equals("")) {
			System.out.println("if:::::::::::");
			newrefno = 1;
			newref = "000" + newrefno;
			System.out.println("newref:::::::::::" + newref);

		} else {
			System.out.println("else:::::::::::");
			String lastst = dbrefno.substring(20, dbrefno.length());
			int last = Integer.parseInt(lastst);
			newrefno = last + 1;
			int len = Integer.toString(newrefno).length();
			if (len == 1) {
				newref = "000" + newrefno;
			} else if (len == 2) {
				newref = "00" + newrefno;
			} else if (len == 3) {
				newref = "0" + newrefno;
			}
		}
		newref = "IMAPPOP-FORM" + pdate1 + newref;
		System.out.println("newref::::::" + newref);
		submissionBaseDTO.setRegistrationno(newref);
		submissionBaseDTO.setUserip("0:0:0:0:0:0:0:1");
		submissionBaseDTO.setDatetime(datetime);
		System.out.println("datetime::::::::::::" + datetime);
		System.out.println("submissionBaseDTO:::::::::::" + submissionBaseDTO);
		ImappopBaseDTO a = imappopBaseRepo.save(submissionBaseDTO);
		System.out.println("a:::::::::::" + a);
		if (a.getId() == 0) {
			newref = null;
		}
		return newref;
	}

//		public String fetchToEmail() {
//			StringBuilder daEmails = new StringBuilder();
//			ArrayList<String> arr = new ArrayList<>();
//			String prefix = "";
//			try {
//				arr = submissionMailadminFormsRepository.findDistinctByMimappop('y');
//				for (int i = 0; i < arr.size(); i++) {
//					daEmails.append(prefix);
//					prefix = ",";
//					daEmails.append(prefix);
//					daEmails.append(arr.get(i));
//				}
//				System.out.println("fetchToEmail" + daEmails.toString());
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			return daEmails.toString();
//		}
//
//		public Map<String, String> fetchHimachalCoord(String dept) {
//			System.out.println("TrackUserDao:::::::::fetchHimachalCoord"); 
//			Map<String, String> coords = new HashMap<>();
//			coords = submissionEmpCoordRepository.fetchHimachalCoord(dept);
//			coords.get("emp_coord_email");
//			coords.get("emp_coord_name");
//			return coords;
//		}

//		public Map<String, String> fetchFormDetail(String regNo)  {
//			System.out.println("regNo::::::"+regNo);
//			Map<String, String> mapbeanMap=null;
//			SubmissionBaseDTO fetchFormDetail=submissionBaseRepository.findByRegistrationno(regNo);
//				System.out.println("fetchFormDetail::::::::"+fetchFormDetail);
//			ObjectMapper m = new ObjectMapper();
//				mapbeanMap = m.convertValue(fetchFormDetail, Map.class);
//				System.out.println("mapbeanMap::::::"+mapbeanMap.get("app_name"));
//			 return mapbeanMap;
//		}

//		public int insertIntoAppType(Map<String, String> forwardBydata, Map<String, String> forwardToData) {
//			int i=0,j=0;
//			if (forwardBydata.get("check").equalsIgnoreCase("upload_scanned")) {
//				SubmissionBaseDTO submissionBaseDTO = submissionBaseRepository.findByRegistrationno(forwardBydata.get("regNumber"));
//				submissionBaseDTO.setSign_cert(forwardBydata.get("filePath"));
//				submissionBaseDTO.setRename_sign_cert(forwardBydata.get("renameFilePath"));
//				SubmissionBaseDTO id=submissionBaseRepository.save(submissionBaseDTO);
//				if(id.getId()>0) {
//					i=1;
//				}
//				System.out.println("i::::::::::"+i);
//			} else {
//				SubmissionBaseDTO submissionBaseDTO = submissionBaseRepository.findByRegistrationno(forwardBydata.get("regNumber"));
//				submissionBaseDTO.setPdf_path(forwardBydata.get("filePath"));
//				SubmissionBaseDTO id=submissionBaseRepository.save(submissionBaseDTO);
//				if(id.getId()>0) {
//					i=1;
//				}
//				System.out.println("j::::::::::"+j);
//				}
//			
//			return i;
//		}

//		public int updateFinalAuditTrack(Map<String, String> forwardBydata, Map<String, String> forwardToData) {
//			int i = 0, j = 0, k = 0;
//			SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
//			Date dt = new Date();
//			String datetime = format.format(dt);
//			String role = forwardBydata.get("role");
//			if (forwardBydata.get("check").equalsIgnoreCase("upload_scanned")) {
//				String fwdToEmail = forwardToData.get("forwardedToEmail").replace(",,", ",").replaceFirst("^,", "");
//				if (role.equalsIgnoreCase(Constants.ROLE_USER)) {
//					SubmissionFinalAuditTrackDTO submissionFinalAuditTrackDTO = submissionFinalAuditTrackRepository.findByRegistrationno(forwardBydata.get("regNumber"));
//					submissionFinalAuditTrackDTO.setStatus(forwardToData.get("status"));
//					submissionFinalAuditTrackDTO.setTo_email(fwdToEmail);
//					submissionFinalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
//					submissionFinalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
//					submissionFinalAuditTrackDTO.setTo_datetime(datetime);
//					submissionFinalAuditTrackDTO.setSign_cert(forwardBydata.get("filePath"));
//					submissionFinalAuditTrackDTO.setRename_sign_cert(forwardBydata.get("renameFilePath"));
//					submissionFinalAuditTrackDTO.setApp_ca_type(forwardBydata.get("check"));
//					
//					
//					
//					SubmissionFinalAuditTrackDTO id=submissionFinalAuditTrackRepository.save(submissionFinalAuditTrackDTO);
//					if(id.getTrack_id()>0) {
//						j=1;
//					}
//					System.out.println("J::::::::::"+j);
//				} else {
//					SubmissionFinalAuditTrackDTO submissionFinalAuditTrackDTO = submissionFinalAuditTrackRepository.findByRegistrationno(forwardBydata.get("regNumber"));
//					submissionFinalAuditTrackDTO.setStatus(forwardToData.get("status"));
//					submissionFinalAuditTrackDTO.setTo_email(fwdToEmail);
//					submissionFinalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
//					submissionFinalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
//					submissionFinalAuditTrackDTO.setTo_datetime(datetime);
//					submissionFinalAuditTrackDTO.setCa_sign_cert(forwardBydata.get("filePath"));
//					submissionFinalAuditTrackDTO.setCa_rename_sign_cert(forwardBydata.get("renameFilePath"));
//					submissionFinalAuditTrackDTO.setApp_ca_type(forwardBydata.get("check"));
//					submissionFinalAuditTrackDTO.setApp_ca_path("");
//					
//					SubmissionFinalAuditTrackDTO id=submissionFinalAuditTrackRepository.save(submissionFinalAuditTrackDTO);
//					if(id.getTrack_id()>0) {
//						j=1;
//					}
//					System.out.println("J::::::::::"+j);
//				}
//			} else {
//				String fwdToEmail = forwardToData.get("forwardedToEmail").replace(",,", ",").replaceFirst("^,", "");
//				if (role.equals(Constants.ROLE_USER)) {
//					SubmissionFinalAuditTrackDTO submissionFinalAuditTrackDTO = submissionFinalAuditTrackRepository.findByRegistrationno(forwardBydata.get("regNumber"));
//					submissionFinalAuditTrackDTO.setApplicant_email(forwardBydata.get("forwardedByEmail")); 
//					submissionFinalAuditTrackDTO.setApplicant_mobile(forwardBydata.get("forwardedByMobile"));
//					submissionFinalAuditTrackDTO.setApplicant_name(forwardBydata.get("forwardedByName"));
//					submissionFinalAuditTrackDTO.setApplicant_ip("0:0:0:0:0:0:0:1");
//					submissionFinalAuditTrackDTO.setApplicant_datetime(datetime);
//					submissionFinalAuditTrackDTO.setApplicant_remarks( forwardToData.get("remarks"));
//					submissionFinalAuditTrackDTO.setStatus(forwardToData.get("status"));
//					submissionFinalAuditTrackDTO.setTo_email(fwdToEmail);
//					submissionFinalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
//					submissionFinalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
//					SubmissionFinalAuditTrackDTO id=submissionFinalAuditTrackRepository.save(submissionFinalAuditTrackDTO);
//					if(id.getTrack_id()>0) {
//						j=1;
//					}
//					System.out.println("ForwardDao:::::updateFinalAuditTrack:::ROLE_USER:::j::"+j);
//				} else if (role.equals(Constants.ROLE_CA)) {
//					SubmissionFinalAuditTrackDTO submissionFinalAuditTrackDTO = submissionFinalAuditTrackRepository.findByRegistrationno(forwardBydata.get("regNumber"));
//					submissionFinalAuditTrackDTO.setCa_email(forwardBydata.get("forwardedByEmail")); 
//					submissionFinalAuditTrackDTO.setCa_mobile(forwardBydata.get("forwardedByMobile"));
//					submissionFinalAuditTrackDTO.setCa_name(forwardBydata.get("forwardedByName"));
//					submissionFinalAuditTrackDTO.setCa_ip("0:0:0:0:0:0:0:1");
//					submissionFinalAuditTrackDTO.setCa_datetime(datetime);
//					submissionFinalAuditTrackDTO.setCa_remarks( forwardToData.get("remarks"));
//					submissionFinalAuditTrackDTO.setStatus(forwardToData.get("status"));
//					submissionFinalAuditTrackDTO.setTo_email(fwdToEmail);
//					submissionFinalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
//					submissionFinalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
//					submissionFinalAuditTrackDTO.setApp_ca_type(forwardBydata.get("check"));
//					submissionFinalAuditTrackDTO.setApp_ca_path("");
//					SubmissionFinalAuditTrackDTO id=submissionFinalAuditTrackRepository.save(submissionFinalAuditTrackDTO);
//					if(id.getTrack_id()>0) {
//						j=1;
//					}
//					System.out.println("ForwardDao:::::updateFinalAuditTrack@@@@@@@@@@@@@@@@@@@:::ROLE_CA:::j::"+j);
//				} else if (role.equals(Constants.ROLE_US)) {
//					SubmissionFinalAuditTrackDTO submissionFinalAuditTrackDTO = submissionFinalAuditTrackRepository.findByRegistrationno(forwardBydata.get("regNumber"));
//					submissionFinalAuditTrackDTO.setUs_email(forwardBydata.get("forwardedByEmail")); 
//					submissionFinalAuditTrackDTO.setUs_mobile(forwardBydata.get("forwardedByMobile"));
//					submissionFinalAuditTrackDTO.setUs_name(forwardBydata.get("forwardedByName"));
//					submissionFinalAuditTrackDTO.setUs_ip("0:0:0:0:0:0:0:1");
//					submissionFinalAuditTrackDTO.setUs_datetime(datetime);
//					submissionFinalAuditTrackDTO.setUs_remarks( forwardToData.get("remarks"));
//					submissionFinalAuditTrackDTO.setStatus(forwardToData.get("status"));
//					submissionFinalAuditTrackDTO.setTo_email(fwdToEmail);
//					submissionFinalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
//					submissionFinalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
//					SubmissionFinalAuditTrackDTO id=submissionFinalAuditTrackRepository.save(submissionFinalAuditTrackDTO);
//					if(id.getTrack_id()>0) {
//						j=1;
//					}
//					System.out.println("TrackUserDao:::::updateFinalAuditTrack:::ROLE_US:::j::"+j);
//				} else if (role.equals(Constants.ROLE_CO)) {
//					SubmissionFinalAuditTrackDTO submissionFinalAuditTrackDTO = submissionFinalAuditTrackRepository.findByRegistrationno(forwardBydata.get("regNumber"));
//					submissionFinalAuditTrackDTO.setCoordinator_email(forwardBydata.get("forwardedByEmail")); 
//					submissionFinalAuditTrackDTO.setCoordinator_mobile(forwardBydata.get("forwardedByMobile"));
//					submissionFinalAuditTrackDTO.setCoordinator_name(forwardBydata.get("forwardedByName"));
//					submissionFinalAuditTrackDTO.setCoordinator_ip("0:0:0:0:0:0:0:1");
//					submissionFinalAuditTrackDTO.setCoordinator_datetime(datetime);
//					submissionFinalAuditTrackDTO.setCoordinator_remarks( forwardToData.get("remarks"));
//					submissionFinalAuditTrackDTO.setStatus(forwardToData.get("status"));
//					submissionFinalAuditTrackDTO.setTo_email(fwdToEmail);
//					submissionFinalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
//					submissionFinalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
//					SubmissionFinalAuditTrackDTO id=submissionFinalAuditTrackRepository.save(submissionFinalAuditTrackDTO);
//					if(id.getTrack_id()>0) {
//						j=1;
//					}
//					System.out.println("TrackUserDao:::::updateFinalAuditTrack:::ROLE_CO:::j::"+j);
//				} else if (role.equals(Constants.ROLE_SUP)) {
//					SubmissionFinalAuditTrackDTO submissionFinalAuditTrackDTO = submissionFinalAuditTrackRepository.findByRegistrationno(forwardBydata.get("regNumber"));
//					submissionFinalAuditTrackDTO.setSupport_email(forwardBydata.get("forwardedByEmail")); 
//					submissionFinalAuditTrackDTO.setSupport_mobile(forwardBydata.get("forwardedByMobile"));
//					submissionFinalAuditTrackDTO.setSupport_name(forwardBydata.get("forwardedByName"));
//					submissionFinalAuditTrackDTO.setSupport_ip("0:0:0:0:0:0:0:1");
//					submissionFinalAuditTrackDTO.setSupport_datetime(datetime);
//					submissionFinalAuditTrackDTO.setSupport_remarks( forwardToData.get("remarks"));
//					submissionFinalAuditTrackDTO.setStatus(forwardToData.get("status"));
//					submissionFinalAuditTrackDTO.setTo_email(fwdToEmail);
//					submissionFinalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
//					submissionFinalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
//					SubmissionFinalAuditTrackDTO id=submissionFinalAuditTrackRepository.save(submissionFinalAuditTrackDTO);
//					if(id.getTrack_id()>0) {
//						j=1;
//					}
//					System.out.println("ForwardDao:::::updateFinalAuditTrack:::ROLE_SUP:::j::"+j);
//				} else if (role.equals(Constants.ROLE_DA)) {
//					SubmissionFinalAuditTrackDTO submissionFinalAuditTrackDTO = submissionFinalAuditTrackRepository.findByRegistrationno(forwardBydata.get("regNumber"));
//					submissionFinalAuditTrackDTO.setDa_email(forwardBydata.get("forwardedByEmail")); 
//					submissionFinalAuditTrackDTO.setDa_mobile(forwardBydata.get("forwardedByMobile"));
//					submissionFinalAuditTrackDTO.setDa_name(forwardBydata.get("forwardedByName"));
//					submissionFinalAuditTrackDTO.setDa_ip("0:0:0:0:0:0:0:1");
//					submissionFinalAuditTrackDTO.setDa_datetime(datetime);
//					submissionFinalAuditTrackDTO.setDa_remarks( forwardToData.get("remarks"));
//					submissionFinalAuditTrackDTO.setStatus(forwardToData.get("status"));
//					submissionFinalAuditTrackDTO.setTo_email(fwdToEmail);
//					submissionFinalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
//					submissionFinalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
//					SubmissionFinalAuditTrackDTO id=submissionFinalAuditTrackRepository.save(submissionFinalAuditTrackDTO);
//					if(id.getTrack_id()>0) {
//						j=1;
//					}
//					System.out.println("ForwardDao:::::updateFinalAuditTrack:::ROLE_DA:::j::"+j);
//				} else if (role.equals(Constants.ROLE_MAILADMIN)) {
//					SubmissionFinalAuditTrackDTO submissionFinalAuditTrackDTO = submissionFinalAuditTrackRepository.findByRegistrationno(forwardBydata.get("regNumber"));
//					submissionFinalAuditTrackDTO.setAdmin_email(forwardBydata.get("forwardedByEmail")); 
//					submissionFinalAuditTrackDTO.setAdmin_mobile(forwardBydata.get("forwardedByMobile"));
//					submissionFinalAuditTrackDTO.setAdmin_name(forwardBydata.get("forwardedByName"));
//					submissionFinalAuditTrackDTO.setAdmin_ip("0:0:0:0:0:0:0:1");
//					submissionFinalAuditTrackDTO.setAdmin_datetime(datetime);
//					submissionFinalAuditTrackDTO.setAdmin_remarks( forwardToData.get("remarks"));
//					submissionFinalAuditTrackDTO.setStatus(forwardToData.get("status"));
//					submissionFinalAuditTrackDTO.setTo_email(fwdToEmail);
//					submissionFinalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
//					submissionFinalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
//					SubmissionFinalAuditTrackDTO id=submissionFinalAuditTrackRepository.save(submissionFinalAuditTrackDTO);
//					if(id.getTrack_id()>0) {
//						j=1;
//					}
//					System.out.println("ForwardDao:::::updateFinalAuditTrack:::ROLE_ADMIN:::j::"+j);
//				}
//			}
//			return j;
//		}

	public int insertIntoFinalAuditTrack(Map<String, String> forwardBydata, Map<String, String> forwardToData) {
		int j = 0;
		Date date = new Date();
		DateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String pdate = dt.format(date);
		String ip = "0:0:0:0:0:0:0:1";
		ImappopFinalAuditTrackDTO submissionFinalAuditTrackDTO = new ImappopFinalAuditTrackDTO();
		submissionFinalAuditTrackDTO.setRegistrationno(forwardBydata.get("regNumber").toString());
		submissionFinalAuditTrackDTO.setApplicantemail(forwardBydata.get("forwardedByEmail"));
		submissionFinalAuditTrackDTO.setApplicant_mobile(forwardBydata.get("forwardedByMobile"));
		submissionFinalAuditTrackDTO.setApplicant_name(forwardBydata.get("forwardedByName"));
		submissionFinalAuditTrackDTO.setApplicant_ip(ip);
		submissionFinalAuditTrackDTO.setApplicant_datetime(pdate);
		submissionFinalAuditTrackDTO.setStatus(forwardToData.get("status"));
		submissionFinalAuditTrackDTO.setForm_name(forwardBydata.get("formType"));
		submissionFinalAuditTrackDTO
				.setToemail(forwardToData.get("forwardedToEmail").replace(",,", ",").replaceFirst("^,", ""));
		submissionFinalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
		submissionFinalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
		submissionFinalAuditTrackDTO.setTo_datetime(pdate);
		// submissionFinalAuditTrackDTO.setApp_user_type(forwardBydata.get("check"));
		submissionFinalAuditTrackDTO.setApp_user_type(forwardBydata.get("check"));
		submissionFinalAuditTrackDTO.setApp_user_path(forwardBydata.get("filePath"));
		submissionFinalAuditTrackDTO.setApp_ca_type(forwardBydata.get(""));
		submissionFinalAuditTrackDTO.setApp_ca_path("");

		ImappopFinalAuditTrackDTO i = imappopFinalAuditTrackRepo.save(submissionFinalAuditTrackDTO);
		if (i.getTrack_id() > 0) {
			j = 1;
		}
		System.out.println("j::::::insertIntoFinalAuditTrack:::::::::::::" + j);
		return j;
	}

//		public int updateAppType(Map<String, String> forwardBydata, Map<String, String> forwardToData) {
//			int i=0,j=0;
//			Date date = new Date();
//			DateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			String pdate = dt.format(date);
//			   if (forwardBydata.get("role").equals(Constants.ROLE_USER)) {
//				   SubmissionBaseDTO submissionBaseDTO = submissionBaseRepository.findByRegistrationno(forwardBydata.get("regNumber"));
//				   submissionBaseDTO.setPdf_path(forwardBydata.get("filePath"));
//				   SubmissionBaseDTO id=submissionBaseRepository.save(submissionBaseDTO);
//					if(id.getId()>0) {
//						j=1;
//					}
//
//			   } else if (forwardBydata.get("role").equals(Constants.ROLE_CA)) {
//	               if (forwardBydata.get("check").equalsIgnoreCase("upload_scanned")) {
//	            	   SubmissionBaseDTO submissionBaseDTO = submissionBaseRepository.findByRegistrationno(forwardBydata.get("regNumber"));
//	            	   submissionBaseDTO.setCa_sign_cert(forwardBydata.get("filePath"));
//	            	   submissionBaseDTO.setCa_rename_sign_cert(forwardBydata.get("renameFilePath"));
//	   				SubmissionBaseDTO id=submissionBaseRepository.save(submissionBaseDTO);
//	   				if(id.getId()>0) {
//	   					i=1;
//	   				}
//	            	   System.out.println("i::::::::::::"+i);
//	               } 
//	           }
//			return i;
//		}

//		public int updateStatusTable(Map<String, String> forwardBydata, Map<String, String> forwardToData) {
//			int j=0;
//	        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
//	        Date dt = new Date();
//	        String datetime = format.format(dt);
//	        String ip="0.0.0.0.0.0.0.1";
//	       
//	        SubmissionStatusDTO submissionStatusDTO=new SubmissionStatusDTO();
//	        try {
//				BeanUtils.populate(submissionStatusDTO, forwardBydata);
//			} catch (IllegalAccessException e) {
//				e.printStackTrace();
//			} catch (InvocationTargetException e) {
//				e.printStackTrace();
//			}
//	        submissionStatusDTO.setStat_form_type(forwardBydata.get("formType"));
//	        submissionStatusDTO.setStat_reg_no(forwardBydata.get("regNumber"));
//	        submissionStatusDTO.setStat_type(forwardToData.get("status"));
//	        submissionStatusDTO.setStat_forwarded_by(forwardBydata.get("forwardedBy"));
//	        submissionStatusDTO.setStat_forwarded_by_user(forwardBydata.get("forwardedByEmail"));
//	        submissionStatusDTO.setStat_forwarded_to(forwardToData.get("forwardedTo"));
//	        submissionStatusDTO.setStat_forwarded_to_user(forwardToData.get("forwardedToEmail"));
//	        submissionStatusDTO.setStat_remarks(forwardToData.get("remarks"));
//	        submissionStatusDTO.setStat_ip(ip);
//			submissionStatusDTO.setStat_forwarded_by_email(forwardBydata.get("forwardedByEmail"));
//			submissionStatusDTO.setStat_forwarded_by_mobile(forwardBydata.get("forwardedByMobile"));
//			submissionStatusDTO.setStat_forwarded_by_name(forwardBydata.get("forwardedByName"));
//			submissionStatusDTO.setStat_forwarded_by_ip(ip);
//			submissionStatusDTO.setStat_forwarded_by_datetime(datetime);
//			submissionStatusDTO.setStat_final_id("");
//			submissionStatusDTO.setStat_on_hold("n");
//			SubmissionStatusDTO i=submissionStatusRepository.save(submissionStatusDTO);
//	        if(i.getStat_id()>0) {
//	        	j=1;
//	        }
//	        System.out.println("j:::::::::::::::::::"+j);
//			return j;
//		}

	// ADDEDDD
//		public String convertSetToString(Set<String> co) {
//			System.out.println("TrackUserDao:::::::::convertSetToString::::::co::::::"+co); 
//			String string = String.join(", ", co);
//			return (string.length() != 0) ? string.toString() : "";
//		}

//		public String fetchPunjabNodalOfficers(String district) {
//			System.out.println("TrackUserDao:::::::::fetchPunjabNodalOfficers"); 
//			String co_email = "", email = "";
//			email = submissionBaseRepository.fetchPunjabNodalOfficers(district);
//			if (co_email.isEmpty()) {
//				co_email = email;
//			} else {
//				co_email += "," + email;
//			}
//			return co_email;
//		}

	// to be checked
//		public String fetchPunjabDA(String employment, String ministry, String department) {
//			System.out.println("TrackUserDao:::::::::fetchPunjabDA"); 
//			String da_email = "";
//			da_email = fetchPunjabDA(employment, ministry, department);
//			if (!da_email.contains(",")) {
//				da_email = da_email;
//			} else {
//				da_email += "," + da_email;
//			}
//			if (da_email.isEmpty()) {
//				da_email = fetchPunjabDA(employment, ministry, "Government of Punjab");
//				if (!da_email.contains(",")) {
//					da_email = da_email;
//				} else {
//					da_email += "," + da_email;
//				}
//			}
//			return da_email;
//		}
	// EO to be checked

	public HashMap<String, Object> fetchTrackDetails(String registration_no) {
		HashMap<String, Object> hmTrack = new HashMap<>();
		List<ImappopStatusDTO> arrRoles = imappopStatusListRepo.findByStatregno(registration_no);
		List<ImappopFinalAuditTrackDTO> currentDetails = imappopFinalAuditTrackListRepo
				.findByRegistrationno(registration_no);
		hmTrack.put("arrRoles", arrRoles);
		hmTrack.put("currentDetails", currentDetails);
		return hmTrack;
	}

	public Map<String, Object> fetchTrackByRole(String registration_no, String forward, String trole, String srole) {
		Map<String, Object> trackData = new HashMap<>();
		Map<String, String> fetchstatus = new HashMap<>();
		List<ImappopStatusDTO> iterable = null;
		String recv_date = "";

		System.out.println("registration_no::::::" + registration_no);
		System.out.println("forward::::::" + forward);
		System.out.println("trole::::::" + trole);
		System.out.println("srole::::::" + srole);

		if (trole.equalsIgnoreCase("undefined")) {
			System.out.println(":::1:::");
			iterable = imappopStatusRepo.findFirstByStatregnoAndStatforwardedtoOrderByStatid(registration_no, trole);
		} else if (srole.equalsIgnoreCase("null") && trole.equalsIgnoreCase("null")) {
			System.out.println(":::2:::");
			iterable = imappopStatusRepo
					.findByStatregnoAndStatforwardedbyIsNullAndStatforwardedtoIsNullOrderByStatid(registration_no);
		} else if (trole.equalsIgnoreCase("null")) {
			System.out.println(":::3:::");
			iterable = imappopStatusRepo
					.findByStatregnoAndStatforwardedbyAndStatforwardedtoIsNullOrderByStatid(registration_no, srole);
		} else {
			System.out.println(":::4:::");
			iterable = imappopStatusRepo.findByStatregnoAndStatforwardedbyAndStatforwardedto(registration_no, srole,
					trole);
		}

		System.out.println(":::iterable:::" + iterable);
		for (ImappopStatusDTO trackStatus : iterable) {
			String remarks = trackStatus.getStat_remarks();
			if (remarks == null) {
				remarks = "";
			}
			recv_date = trackStatus.getStat_createdon();
			String current_user = trackStatus.getStat_forwarded_by_email();
			if (current_user == null) {
				current_user = "User Emaillll";
				// current_user = userdata.getEmail();
			}
			String forwarder = commonUtility.findRole(forward);
			String sender_details = forwarder + "&ensp;(" + current_user + ")";
			if (!forward.isEmpty()) {
				fetchstatus = (Map<String, String>) imappopStatusRepo
						.findByStatregnoAndStatforwardedbyOrStatforwardedto(registration_no, forward, srole);
				recv_date = fetchstatus.get("recv_date");
				String stat_forwarded_by_email = fetchstatus.get("stat_forwarded_by_email");
				sender_details = forwarder + "&ensp;(" + stat_forwarded_by_email + ")";
			}
			trackData.put("remarks", remarks);
			trackData.put("recv_date", recv_date);
			trackData.put("current_user", current_user);
			trackData.put("recv_email", trackStatus.getStat_forwarded_to_user());
			trackData.put("stat_process", trackStatus.getStat_process());
			trackData.put("stat_type", trackStatus.getStat_type());
			trackData.put("stat_on_hold", trackStatus.getStat_on_hold());
			trackData.put("recv_date", recv_date);
			trackData.put("sender_details", sender_details);
		}
		return trackData;
	}
	// ****************************End of Track User Api****************

	public ImappopDocUploadDTO saveDocx(ImappopDocUploadDTO uploadfiles) {
		ImappopDocUploadDTO uploadfilesdto = imappopDocUploadRepo.save(uploadfiles);
		return uploadfilesdto;
	}

	public Map<String, Map<String, String>> viewDocx(String regid, String role) {
		Map<String, String> file = new HashMap<>();
		Map<String, Map<String, String>> filelist = new HashMap<>();
		// For esign and manual
		String pdf = "";
		List<ImappopBaseDTO> list = imappopBaseListRepo.findByRegistrationno(regid);
		for (ImappopBaseDTO dto : list) {

			System.out.println(dto.getPdf_path());
			if (dto.getPdf_path().toLowerCase().contains("esign"))
				pdf = regid + ".pdf (Esigned file)";
			else if (dto.getPdf_path().toLowerCase().contains("manual"))
				pdf = regid + ".pdf (Scanned file)";
			else
				pdf = regid + ".pdf";
			if (role != null && !role.isEmpty())
				role = role.toLowerCase();

			HashMap<String, String> filepath = new HashMap<>();
			switch (role) {
			case "user":
				if (dto.getRename_sign_cert() != null && !dto.getRename_sign_cert().isEmpty())
					file.put(pdf, dto.getRename_sign_cert());
				break;
			case "ca":
				if (dto.getCa_rename_sign_cert() != null && !dto.getCa_rename_sign_cert().isEmpty()) {
					List<ImappopFinalAuditTrackDTO> l = imappopFinalAuditTrackListRepo.findByRegistrationno(regid);
					for (ImappopFinalAuditTrackDTO d : l) {
						filepath.put("esignedpdf", d.getCa_rename_sign_cert());
					}
					if (filepath != null)
						file.put(regid + ".pdf", filepath.get("esignedpdf"));
				}
				break;

			}
		}
		List<ImappopDocUploadDTO> iterable = imappopDocUploadRepo.findByRegistrationnoAndRole(regid, role);
		System.out.println(" -------------------- ");
		for (ImappopDocUploadDTO dto : iterable) {
			System.out.println(dto.getDoc() + " -------------------- " + dto.getDocpath());
			file.put(dto.getDoc(), dto.getDocpath());
			// to show view original name
			// file.put("viewname for "+dto.getDoc(), dto.getOriginal_filename());
		}
		String roleName = null;
		switch (role) {
		case "user":
			roleName = "Applicant";
			break;
		case "ca":
			roleName = "Reporting/Forwarding/Nodal Officer";
			break;
		case "sup":
			roleName = "Support";
			break;
		case "co":
			roleName = "Coordinator";
			break;
		case "admin":
			roleName = "Admin";
			break;
		case "da":
			roleName = "Delegated Admin";
			break;

		}
		if (file.size() > 0)
			filelist.put(roleName, file);
		return filelist;
	}

}
