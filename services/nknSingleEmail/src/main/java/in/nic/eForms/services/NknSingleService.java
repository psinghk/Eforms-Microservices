package in.nic.eForms.services;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.codec.digest.DigestUtils;
import org.modelmapper.ModelMapper;

import in.nic.eForms.controllers.AdminController;
import in.nic.eForms.entities.GeneratePdf;
import in.nic.eForms.entities.NknSingleBase;
import in.nic.eForms.entities.NknSingleEmpCoord;
import in.nic.eForms.entities.NknSingleSha;
import in.nic.eForms.models.ErrorResponseForOrganizationValidationDto;
import in.nic.eForms.models.FinalAuditTrack;
import in.nic.eForms.models.GeneratePdfBean;
import in.nic.eForms.models.HodDetailsDto;
import in.nic.eForms.models.MobileAndName;
import in.nic.eForms.models.OrganizationBean;
import in.nic.eForms.models.OrganizationDto;
import in.nic.eForms.models.PreviewFormBean;
import in.nic.eForms.models.ProfileDto;
import in.nic.eForms.models.ResponseBean;
import in.nic.eForms.models.Status;
import in.nic.eForms.models.UserForCreate;
import in.nic.eForms.models.UserForSearch;
import in.nic.eForms.repositories.GeneratePdfRepository;
import in.nic.eForms.repositories.NknSingleBaseRepo;
import in.nic.eForms.repositories.NknSingleEmpCoordRepo;
import in.nic.eForms.repositories.NknSingleShaRepo;
import in.nic.eForms.utils.Constants;
import in.nic.eForms.utils.Util;
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
public class NknSingleService {
	@Value("${fileBasePath}")
	private String EXTERNAL_FILE_PATH;
	@Value("${fileBasePath}")
	private String fileBasePath;

	private final GeneratePdfRepository generatePdfRepository;
	private final NknSingleBaseRepo nknSingleBaseRepo;
	private final NknSingleShaRepo nknSingleShaRepo;
	private final NknSingleEmpCoordRepo nknSingleEmpCoordRepo;
	private final Util utilityService;

	@Autowired
	public NknSingleService(GeneratePdfRepository generatePdfRepository, NknSingleBaseRepo nknSingleBaseRepo,
			NknSingleShaRepo nknSingleShaRepo, NknSingleEmpCoordRepo nknSingleEmpCoordRepo, Util utilityService) {
		super();
		this.generatePdfRepository = generatePdfRepository;
		this.nknSingleBaseRepo = nknSingleBaseRepo;
		this.utilityService = utilityService;
		this.nknSingleShaRepo = nknSingleShaRepo;
		this.nknSingleEmpCoordRepo = nknSingleEmpCoordRepo;

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
			File file = ResourceUtils.getFile("classpath:nknsinglereport.jrxml");
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
			parameters.put("Institute Name", generatePdfBean.getInst_name());
			parameters.put("Institute ID", generatePdfBean.getInst_id());
			parameters.put("NKN Project", generatePdfBean.getNkn_project());
			parameters.put("DOB", generatePdfBean.getSingle_dob());
			parameters.put("DOR", generatePdfBean.getSingle_dor());
			parameters.put("Preferred Email1", generatePdfBean.getPreferred_email1());
			parameters.put("Preferred Email1", generatePdfBean.getPreferred_email2());
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

	public NknSingleBase preview(String regNo) {
		return nknSingleBaseRepo.findByRegistrationNo(regNo);
	}
	
	public boolean updatePreviewDetails(String regNumber, PreviewFormBean previewFormBean) {
		NknSingleBase ldapBase = nknSingleBaseRepo.findByRegistrationNo(regNumber);
		if (ldapBase != null) {
			try {
				BeanUtils.copyProperties(previewFormBean, ldapBase);
				LocalDateTime currentTime = LocalDateTime.now();
				// ldapBase.setLastUpdationDateTime(currentTime);
				NknSingleBase nknUpdated = nknSingleBaseRepo.save(ldapBase);
				if (nknUpdated.getId() > 0) {
					return true;
				}
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

}
