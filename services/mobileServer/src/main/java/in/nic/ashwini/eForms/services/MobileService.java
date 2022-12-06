package in.nic.ashwini.eForms.services;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;

import in.nic.ashwini.eForms.entities.GeneratePdf;
import in.nic.ashwini.eForms.entities.MobileBase;
import in.nic.ashwini.eForms.models.FinalAuditTrack;
import in.nic.ashwini.eForms.models.GeneratePdfBean;
import in.nic.ashwini.eForms.models.PreviewFormBean;
import in.nic.ashwini.eForms.models.Status;
import in.nic.ashwini.eForms.repositories.GeneratePdfRepository;
import in.nic.ashwini.eForms.repositories.MobileBaseRepo;
import in.nic.ashwini.eForms.repositories.MobileEmpCoordRepo;
import in.nic.ashwini.eForms.utils.Util;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@Service
public class MobileService {
	@Value("${fileBasePath}")
	private String EXTERNAL_FILE_PATH;
	@Value("${fileBasePath}")
	private String fileBasePath;

	private final GeneratePdfRepository generatePdfRepository;
	private final MobileBaseRepo mobileBaseRepo;
	private final MobileEmpCoordRepo mobileCoordRepo;
	private final Util utilityService;

	@Autowired
	public MobileService(GeneratePdfRepository generatePdfRepository, MobileBaseRepo mobileBaseRepo,
			Util utilityService,MobileEmpCoordRepo mobileCoordRepo) {
		super();
		this.generatePdfRepository = generatePdfRepository;
		this.mobileBaseRepo = mobileBaseRepo;
		this.utilityService = utilityService;
		this.mobileCoordRepo = mobileCoordRepo;
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
			File file = ResourceUtils.getFile("classpath:relayreport.jrxml");
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
//			parameters.put("appName", generatePdfBean.getAppName());
//			parameters.put("appIp", generatePdfBean.getAppIp());
//			parameters.put("os", generatePdfBean.getOs());
//			parameters.put("relayAppUrl", generatePdfBean.getRelayAppUrl());
//			parameters.put("relaySenderId", generatePdfBean.getRelaySenderId());
//			parameters.put("relayAuthId", generatePdfBean.getRelayAuthId());
//			parameters.put("domainMx", generatePdfBean.getDomainMx());
//			parameters.put("port", generatePdfBean.getPort());
//			parameters.put("spf", generatePdfBean.getSpf());
//			parameters.put("dkim", generatePdfBean.getDkim());
//			parameters.put("dmarc", generatePdfBean.getDmarc());
//			
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

	public MobileBase preview(String regNo) {
		return mobileBaseRepo.findByRegistrationNo(regNo);
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

	public boolean updatePreviewDetails(String regNumber, PreviewFormBean previewFormBean) {
		MobileBase mobileBase = mobileBaseRepo.findByRegistrationNo(regNumber);
		if (mobileBase != null) {
			try {
				BeanUtils.copyProperties(previewFormBean, mobileBase);
				LocalDateTime currentTime = LocalDateTime.now();
			//	mobileBase.setLastUpdationDateTime(currentTime);
				MobileBase mobileUpdated = mobileBaseRepo.save(mobileBase);
				if (mobileUpdated.getId() > 0) {
					return true;
				}
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	public boolean updateStatusTable(String regNumber, String currentRole, PreviewFormBean previewFormBean) {
		MobileBase mobileBase = mobileBaseRepo.findByRegistrationNo(regNumber);
		if (mobileBase != null) {
			try {
				BeanUtils.copyProperties(previewFormBean, mobileBase);
				LocalDateTime currentTime = LocalDateTime.now();
			//	mobileBase.setLastUpdationDateTime(currentTime);
				MobileBase mobileUpdated = mobileBaseRepo.save(mobileBase);
				if (mobileUpdated.getId() > 0) {
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
	
	public Set<String> findBo(MobileBase mobileBase) {
		List<String> bo = null;
		if (mobileBase.getEmployment().trim().equalsIgnoreCase("central")
				|| mobileBase.getEmployment().trim().equalsIgnoreCase("ut")) {

			bo = utilityService.fetchBoByMinistry(mobileBase.getEmployment(), mobileBase.getMinistry(),
					mobileBase.getDepartment());
		} else if (mobileBase.getEmployment().trim().equalsIgnoreCase("state")) {
			bo = utilityService.fetchBoByState(mobileBase.getEmployment(), mobileBase.getState(),
					mobileBase.getDepartment());
		} else {
			bo = utilityService.fetchBoByOrg(mobileBase.getEmployment(), mobileBase.getOrganization());
		}
		Set<String> bos = new HashSet<String>(bo);
		return bos;
	}

	
	
//	public Set<String> findBo(MobileBase mobileBase) {
//		  Set<String> bos = new HashSet<>();
//		if (mobileBase.getEmployment().trim().equalsIgnoreCase("central") || mobileBase.getEmployment().trim().equalsIgnoreCase("ut")) {
//			 
//			bos=mobileCoordRepo.fetchByMinistry(mobileBase.getEmployment(),mobileBase.getMinistry(),mobileBase.getDepartment());
//		 }else if(mobileBase.getEmployment().trim().equalsIgnoreCase("state")) {
//			 bos=mobileCoordRepo.fetchByState(mobileBase.getEmployment(),mobileBase.getState(),mobileBase.getDepartment());
//		 }
//		 else {
//			 bos=mobileCoordRepo.fetchByOrg(mobileBase.getEmployment(),mobileBase.getOrganization());
//		 }
//		return bos;
//	}
	
//	private String generateRegistrationNumber() {
//	if (dbrefno == null || dbrefno.equals("")) {
//        newrefno = 1;
//        newref = "000" + newrefno;
//    } else {
//        String lastst = dbrefno.substring(20, dbrefno.length());
//        int last = Integer.parseInt(lastst);
//        newrefno = last + 1;
//        int len = Integer.toString(newrefno).length();
//        if (len == 1) {
//            newref = "000" + newrefno;
//        } else if (len == 2) {
//            newref = "00" + newrefno;
//        } else if (len == 3) {
//            newref = "0" + newrefno;
//        }
//    }
//    newref = "IMAPPOP-FORM" + pdate1 + newref;
//	}

//	public Map<String, Object> raiseQuery(String regNumber, String uemail, String choose_recp, String to_email,
//			String statRemarks) {
//		Map<String, Object> map = null;
//		map = imappopDao.raiseQuery(regNumber, role, uemail, choose_recp, to_email, statRemarks);
//		return map;
//	}
}
