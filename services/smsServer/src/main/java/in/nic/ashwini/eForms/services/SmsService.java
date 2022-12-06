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
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;
import in.nic.ashwini.eForms.entities.GeneratePdf;
import in.nic.ashwini.eForms.entities.SmsBase;
import in.nic.ashwini.eForms.models.FinalAuditTrack;
import in.nic.ashwini.eForms.models.GeneratePdfBean;
import in.nic.ashwini.eForms.models.PreviewFormBean;
import in.nic.ashwini.eForms.models.Status;
import in.nic.ashwini.eForms.repositories.GeneratePdfRepository;
import in.nic.ashwini.eForms.repositories.SmsBaseRepo;
import in.nic.ashwini.eForms.utils.Util;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@Service
public class SmsService {
	@Value("${fileBasePath}")
	private String EXTERNAL_FILE_PATH;
	@Value("${fileBasePath}")
	private String fileBasePath;
	private final GeneratePdfRepository generatePdfRepository;
	private final SmsBaseRepo smsBaseRepo;
	private final Util utilityService;

	@Autowired
	public SmsService(GeneratePdfRepository generatePdfRepository, SmsBaseRepo smsBaseRepo,
			Util utilityService) {
		super();
		this.generatePdfRepository = generatePdfRepository;
		this.smsBaseRepo = smsBaseRepo;
		this.utilityService = utilityService;

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
			File file = ResourceUtils.getFile("classpath:smsreport.jrxml");
			JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
			JRDataSource datasource = new JREmptyDataSource();
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("regid", generatePdfBean.getRegistrationNo());
			parameters.put("applicant_name", generatePdfBean.getName());
			parameters.put("applicant_email", generatePdfBean.getEmail());
			parameters.put("min", generatePdfBean.getMinistry());
			parameters.put("applicant_mobile", generatePdfBean.getMobile());
			parameters.put("hod_name", generatePdfBean.getHodName());
			parameters.put("hod_email", generatePdfBean.getHodEmail());
			parameters.put("hod_mobile", generatePdfBean.getHodMobile());
		
			parameters.put("app_name", generatePdfBean.getApp_name());
			parameters.put("app_url", generatePdfBean.getApp_url());
			parameters.put("sms_usage", generatePdfBean.getSms_usage());
     		parameters.put("server_loc", generatePdfBean.getServer_loc());
		    parameters.put("server_loc_txt", generatePdfBean.getHodMobile());
		    parameters.put("base_ip", generatePdfBean.getBase_ip());
		    parameters.put("service_ip", generatePdfBean.getService_ip());
			parameters.put("t_off_name", generatePdfBean.getT_off_name());
			parameters.put("tdesignation", generatePdfBean.getTdesignation());
			parameters.put("temp_code", generatePdfBean.getTemp_code());
			parameters.put("taddrs", generatePdfBean.getTaddrs());
			parameters.put("tcity", generatePdfBean.getTcity());
			parameters.put("tstate", generatePdfBean.getTstate());
			parameters.put("tpin", generatePdfBean.getTpin());
			parameters.put("ttel_ofc", generatePdfBean.getTtel_ofc());
			parameters.put("ttel_res", generatePdfBean.getTtel_res());
			parameters.put("tmobile", generatePdfBean.getTmobile());
			parameters.put("tauth_email", generatePdfBean.getTauth_email());
			parameters.put("bauth_off_name", generatePdfBean.getBauth_off_name());
			parameters.put("bdesignation", generatePdfBean.getBdesignation());
			parameters.put("bemp_code", generatePdfBean.getBemp_code());
			parameters.put("baddrs", generatePdfBean.getBaddrs());
			
			parameters.put("bcity", generatePdfBean.getBcity());
			parameters.put("bstate", generatePdfBean.getBsatate());
			parameters.put("bpin", generatePdfBean.getBpin());
			parameters.put("btel_ofc", generatePdfBean.getBtel_ofc());
			parameters.put("btel_res", generatePdfBean.getBtel_res());
			parameters.put("bmobile", generatePdfBean.getBmobile());
			parameters.put("bauth_email", generatePdfBean.getBauth_email());
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

	public SmsBase preview(String regNo) {
		return smsBaseRepo.findByRegistrationNo(regNo);
	}
	
	@Transactional
	public SmsBase insert(SmsBase smsBase) {
		if (smsBase != null) {
			Date date = new Date();
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			String pdate = dateFormat.format(date);
			String oldRegNumber = smsBaseRepo.findLatestRegistrationNo();
			String newRegNumber = "SMS-FORM" + pdate;
			if (oldRegNumber == null || oldRegNumber.isEmpty()) {
				newRegNumber += "0001";
			} else {
				String lastst = oldRegNumber.substring(16, oldRegNumber.length());
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
			smsBase.setRegistrationNo(newRegNumber);
			smsBase.setSupportActionTaken("p");
			return smsBaseRepo.save(smsBase);
		}
		return null;
	}

	public boolean updatePreviewDetails(String regNumber, PreviewFormBean previewFormBean) {
		SmsBase smsBase = smsBaseRepo.findByRegistrationNo(regNumber);
		if (smsBase != null) {
			try {
				BeanUtils.copyProperties(previewFormBean, smsBase);
				LocalDateTime currentTime = LocalDateTime.now();
				SmsBase smsUpdated = smsBaseRepo.save(smsBase);
				if (smsUpdated.getId() > 0) {
					return true;
				}
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	public boolean updateStatusTable(String regNumber, String currentRole, PreviewFormBean previewFormBean) {
		SmsBase smsBase = smsBaseRepo.findByRegistrationNo(regNumber);
		if (smsBase != null) {
			try {
				BeanUtils.copyProperties(previewFormBean, smsBase);
				LocalDateTime currentTime = LocalDateTime.now();
				SmsBase smsUpdated = smsBaseRepo.save(smsBase);
				if (smsUpdated.getId() > 0) {
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
	public boolean updatesmsbase(SmsBase smsbase) {
		SmsBase smsdetails = smsBaseRepo.save(smsbase);
		if (smsdetails.getId() > 0) {
			return true;
		} else {
			return false;
		}
	}
}
