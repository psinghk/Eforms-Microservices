package in.nic.ashwini.eForms.services;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
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

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;

import in.nic.ashwini.eForms.entities.DnsBase;
import in.nic.ashwini.eForms.entities.DnsBulkCampaigns;
import in.nic.ashwini.eForms.entities.DnsBulkCname;
import in.nic.ashwini.eForms.entities.DnsBulkDmarc;
import in.nic.ashwini.eForms.entities.DnsBulkMx;
import in.nic.ashwini.eForms.entities.DnsBulkPtr;
import in.nic.ashwini.eForms.entities.DnsBulkSpf;
import in.nic.ashwini.eForms.entities.DnsBulkSrv;
import in.nic.ashwini.eForms.entities.DnsBulkTxt;
import in.nic.ashwini.eForms.entities.DnsBulkUpload;
import in.nic.ashwini.eForms.entities.DnsRegistrationCname;
import in.nic.ashwini.eForms.entities.DnsRegistrationNewip;
import in.nic.ashwini.eForms.entities.DnsRegistrationOldip;
import in.nic.ashwini.eForms.entities.DnsRegistrationUrl;
import in.nic.ashwini.eForms.entities.GeneratePdf;
import in.nic.ashwini.eForms.models.FinalAuditTrack;
import in.nic.ashwini.eForms.models.GeneratePdfBean;
import in.nic.ashwini.eForms.models.PreviewFormBean;
import in.nic.ashwini.eForms.models.Status;
import in.nic.ashwini.eForms.models.ValidatedDnsBean;
import in.nic.ashwini.eForms.repositories.DnsBulkAaaaRepository;
import in.nic.ashwini.eForms.repositories.DnsBulkCnameRepository;
import in.nic.ashwini.eForms.repositories.DnsBulkDmarcRepository;
import in.nic.ashwini.eForms.repositories.DnsBulkMxRepository;
import in.nic.ashwini.eForms.repositories.DnsBulkPtrRepository;
import in.nic.ashwini.eForms.repositories.DnsBulkSpfRepository;
import in.nic.ashwini.eForms.repositories.DnsBulkSrvRepository;
import in.nic.ashwini.eForms.repositories.DnsBulkTxtRepository;
import in.nic.ashwini.eForms.repositories.DnsCampaignRepository;
import in.nic.ashwini.eForms.repositories.DnsRegistrationCnameRepository;
import in.nic.ashwini.eForms.repositories.DnsRegistrationNewIpRepository;
import in.nic.ashwini.eForms.repositories.DnsRegistrationOldIpRepository;
import in.nic.ashwini.eForms.repositories.DnsRegistrationUrlRepository;
import in.nic.ashwini.eForms.repositories.DnsRepository;
import in.nic.ashwini.eForms.repositories.DnsTeamDataRepository;
import in.nic.ashwini.eForms.repositories.GeneratePdfRepository;
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
public class DnsService {
	@Value("${fileBasePath}")
	private String EXTERNAL_FILE_PATH;
	@Value("${fileBasePath}")
	private String fileBasePath;

	private final GeneratePdfRepository generatePdfRepository;
	private final DnsRepository dnsBaseRepo;
	private final DnsCampaignRepository dnsCampaignRepo;
	private final DnsBulkCnameRepository dnsBulkCnameRepo;
	private final DnsBulkMxRepository dnsBulkMxRepo;
	private final DnsBulkPtrRepository dnsBulkPtrRepo;
	private final DnsBulkSpfRepository dnsBulkSpfRepo;
	private final DnsBulkTxtRepository dnsBulkTxtRepo;
	private final DnsBulkDmarcRepository dnsBulkDmarcRepo;
	private final DnsBulkSrvRepository dnsBulkSrvRepo;
	private final DnsBulkAaaaRepository dnsBulkAaaaRepo;
	private final DnsRegistrationUrlRepository dnsRegistrationUrlRepo;
	private final DnsRegistrationNewIpRepository dnsRegistrationNewIpRepo;
	private final DnsRegistrationOldIpRepository dnsRegistrationOldIpRepo;
	private final DnsRegistrationCnameRepository dnsRegistrationCnameRepo;
	private final DnsTeamDataRepository dnsTeamDataRepo;
	private final Util utilityService;

	@Autowired
	public DnsService(GeneratePdfRepository generatePdfRepository, DnsRepository dnsBaseRepo,
			DnsCampaignRepository dnsCampaignRepo, DnsBulkCnameRepository dnsBulkCnameRepo,
			DnsBulkMxRepository dnsBulkMxRepo, DnsBulkPtrRepository dnsBulkPtrRepo, DnsBulkSpfRepository dnsBulkSpfRepo,
			DnsBulkTxtRepository dnsBulkTxtRepo, DnsBulkDmarcRepository dnsBulkDmarcRepo,
			DnsBulkSrvRepository dnsBulkSrvRepo, DnsBulkAaaaRepository dnsBulkAaaaRepo, Util utilityService,
			DnsRegistrationUrlRepository dnsRegistrationUrlRepo,
			DnsRegistrationNewIpRepository dnsRegistrationNewIpRepo, DnsTeamDataRepository dnsTeamdataRepo,
			DnsRegistrationCnameRepository dnsRegistrationCnameRepo,
			DnsRegistrationOldIpRepository dnsRegistrationOldIpRepo) {
		super();
		this.generatePdfRepository = generatePdfRepository;
		this.dnsBaseRepo = dnsBaseRepo;
		this.dnsCampaignRepo = dnsCampaignRepo;
		this.dnsBulkCnameRepo = dnsBulkCnameRepo;
		this.dnsBulkMxRepo = dnsBulkMxRepo;
		this.dnsBulkSpfRepo = dnsBulkSpfRepo;
		this.dnsBulkSrvRepo = dnsBulkSrvRepo;
		this.dnsBulkAaaaRepo = dnsBulkAaaaRepo;
		this.dnsBulkDmarcRepo = dnsBulkDmarcRepo;
		this.dnsBulkTxtRepo = dnsBulkTxtRepo;
		this.dnsBulkPtrRepo = dnsBulkPtrRepo;
		this.utilityService = utilityService;
		this.dnsRegistrationUrlRepo = dnsRegistrationUrlRepo;
		this.dnsRegistrationNewIpRepo = dnsRegistrationNewIpRepo;
		this.dnsTeamDataRepo = dnsTeamdataRepo;
		this.dnsRegistrationCnameRepo = dnsRegistrationCnameRepo;
		this.dnsRegistrationOldIpRepo = dnsRegistrationOldIpRepo;
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

	public DnsBase preview(String regNo) {
		return dnsBaseRepo.findByRegistrationNo(regNo);
	}

	@Transactional
	public DnsBase insert(DnsBase dnsService) {
		if (dnsService != null) {
			Date date = new Date();
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			String pdate = dateFormat.format(date);
			String oldRegNumber = dnsBaseRepo.findLatestRegistrationNo();
			String newRegNumber = "DNS-FORM" + pdate;
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
			dnsService.setRegistrationNo(newRegNumber);
			dnsService.setSupportActionTaken("p");
			if(dnsService.getDnsType() == null) {
				dnsService.setDnsType("nic");
			}else if(dnsService.getDnsType().isEmpty()) {
				dnsService.setDnsType("nic");
			}
			dnsService.setEmailSent("n");
			dnsService.setEmailSentTo("n");
			dnsService.setSmsSent("n");
			dnsService.setSmsSentTo("n");
			dnsService.setServiceUrl("web_url");
			log.info("Inserting DNS request {} in database",newRegNumber);
			return dnsBaseRepo.save(dnsService);
		}
		return null;
	}

	@Transactional
	public DnsBase update(DnsBase dnsBase) {
		if (dnsBase != null) {
			return dnsBaseRepo.save(dnsBase);
		}
		return null;
	}

	public DnsBase findByRegistrationNo(String regNumber) {
		if (!regNumber.isEmpty()) {
			return dnsBaseRepo.findByRegistrationNo(regNumber);
		}
		return null;
	}

	public boolean updatePreviewDetails(String regNumber, PreviewFormBean previewFormBean) {
		
		DnsBase dnsBase = dnsBaseRepo.findByRegistrationNo(regNumber);
		if (dnsBase != null) {
			try {
				BeanUtils.copyProperties(previewFormBean, dnsBase);
				LocalDateTime currentTime = LocalDateTime.now();
				dnsBase.setLastUpdationDateTime(currentTime);
				DnsBase imapPopUpdated = dnsBaseRepo.save(dnsBase);
				if (imapPopUpdated.getId() > 0) {
					return true;
				}
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	public boolean updateStatusTable(String regNumber, String currentRole, PreviewFormBean previewFormBean) {
		DnsBase dnsBase = dnsBaseRepo.findByRegistrationNo(regNumber);
		if (dnsBase != null) {
			try {
				BeanUtils.copyProperties(previewFormBean, dnsBase);
				LocalDateTime currentTime = LocalDateTime.now();
				dnsBase.setLastUpdationDateTime(currentTime);
				DnsBase imapPopUpdated = dnsBaseRepo.save(dnsBase);
				if (imapPopUpdated.getId() > 0) {
					return true;
				}
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	public boolean updateStatusAndFinalAuditTrack(Status status, FinalAuditTrack finalAuditTrack) {
		return utilityService.updateStatusAndFinalAuditTrack(status, finalAuditTrack);
	}

	public DnsBulkCampaigns addDnsCampaign(@NotEmpty String name, @NotEmpty String email,
			@Valid List<ValidatedDnsBean> data, @NotEmpty String requestType, @NotEmpty String recordType,
			String renamedFilePath, String uploadedFile) {
		Random rand = new Random();
		// Generate random integers in range 0 to 999999999
		long random = rand.nextInt(1000000000);
		LocalDateTime currentTime = LocalDateTime.now();
		DnsBulkCampaigns dnsBulkCampaign = new DnsBulkCampaigns();
		dnsBulkCampaign.setId(random);
		if (renamedFilePath != null)
			dnsBulkCampaign.setFilePath(renamedFilePath);
		else
			dnsBulkCampaign.setFilePath("");
		if (uploadedFile != null)
			dnsBulkCampaign.setUploadedFile(uploadedFile);
		else
			dnsBulkCampaign.setUploadedFile("");
		dnsBulkCampaign.setDiscardStatus("0");
		dnsBulkCampaign.setOwnerEmail(email);
		dnsBulkCampaign.setReqOtherAdd(recordType);
		dnsBulkCampaign.setRequestType(requestType);
		dnsBulkCampaign.setOwnerName(name);
		dnsBulkCampaign.setStatus("0");
		dnsBulkCampaign.setSubmissionTimeStamp(currentTime);
		return dnsCampaignRepo.save(dnsBulkCampaign);
	}

	public Long addValidRecordsInBulkTable(ValidatedDnsBean dnsData, Long campaignId, String recordType) {
		dnsData = initializeDnsDataBeanWithEmptyValues(dnsData);
		Long id = -1l;
		switch (recordType) {
		case "cname":
			DnsBulkCname dnsBulkCname = new DnsBulkCname();
			dnsBulkCname.setCampaignId(campaignId);
			dnsBulkCname.setDomain((dnsData.getDomain().isEmpty()) ? "N/A" : dnsData.getDomain());
			dnsBulkCname.setCname((dnsData.getCnameTxt().isEmpty()) ? "N/A" : dnsData.getCnameTxt());
			dnsBulkCname.setLocation((dnsData.getServerLocation().isEmpty()) ? "N/A" : dnsData.getServerLocation());
			dnsBulkCname.setMigrationDate(
					(dnsData.getMigrationDate().toString().isEmpty()) ? "N/A" : dnsData.getMigrationDate().toString());
			dnsBulkCname
					.setOldCname((dnsData.getReq().equals("req_modify") && dnsData.getOldCnameTxt().isEmpty()) ? "N/A"
							: dnsData.getOldCnameTxt());
			dnsBulkCname.setErrorStatus("0");
			dnsBulkCname.setDeleteStatus("0");
			dnsBulkCname = dnsBulkCnameRepo.save(dnsBulkCname);
			id = dnsBulkCname.getId();
			break;
		case "mx":
			DnsBulkMx dnsBulkMx = new DnsBulkMx();
			dnsBulkMx.setCampaignId(campaignId);
			dnsBulkMx.setDomain((dnsData.getDomain().isEmpty()) ? "N/A" : dnsData.getDomain());
			dnsBulkMx.setMx((dnsData.getMxTxt().isEmpty()) ? "N/A" : dnsData.getMxTxt());
			dnsBulkMx.setLocation((dnsData.getServerLocation().isEmpty()) ? "N/A" : dnsData.getServerLocation());
			dnsBulkMx.setMigrationDate(
					(dnsData.getMigrationDate().toString().isEmpty()) ? "N/A" : dnsData.getMigrationDate().toString());
			dnsBulkMx.setOldMx((dnsData.getReq().equals("req_modify") && dnsData.getOldMxTxt().isEmpty()) ? "N/A"
					: dnsData.getOldMxTxt());
			dnsBulkMx.setErrorStatus("0");
			dnsBulkMx.setDeleteStatus("0");
			dnsBulkMx = dnsBulkMxRepo.save(dnsBulkMx);
			id = dnsBulkMx.getId();
			break;
		case "txt":
			DnsBulkTxt dnsBulkTxt = new DnsBulkTxt();
			dnsBulkTxt.setCampaignId(campaignId);
			dnsBulkTxt.setDomain((dnsData.getDomain().isEmpty()) ? "N/A" : dnsData.getDomain());
			dnsBulkTxt.setTxt((dnsData.getTxtTxt().isEmpty()) ? "N/A" : dnsData.getTxtTxt());
			dnsBulkTxt.setLocation((dnsData.getServerLocation().isEmpty()) ? "N/A" : dnsData.getServerLocation());
			dnsBulkTxt.setMigrationDate(
					(dnsData.getMigrationDate().toString().isEmpty()) ? "N/A" : dnsData.getMigrationDate().toString());
			dnsBulkTxt.setOldTxt((dnsData.getReq().equals("req_modify") && dnsData.getOldTxtTxt().isEmpty()) ? "N/A"
					: dnsData.getOldTxtTxt());
			dnsBulkTxt.setErrorStatus("0");
			dnsBulkTxt.setDeleteStatus("0");
			dnsBulkTxt = dnsBulkTxtRepo.save(dnsBulkTxt);
			id = dnsBulkTxt.getId();
			break;
		case "ptr":
			DnsBulkPtr dnsBulkPtr = new DnsBulkPtr();
			dnsBulkPtr.setCampaignId(campaignId);
			dnsBulkPtr.setDomain((dnsData.getDomain().isEmpty()) ? "N/A" : dnsData.getDomain());
			dnsBulkPtr.setPtr((dnsData.getPtrTxt().isEmpty()) ? "N/A" : dnsData.getPtrTxt());
			dnsBulkPtr.setLocation((dnsData.getServerLocation().isEmpty()) ? "N/A" : dnsData.getServerLocation());
			dnsBulkPtr.setMigrationDate(
					(dnsData.getMigrationDate().toString().isEmpty()) ? "N/A" : dnsData.getMigrationDate().toString());
			dnsBulkPtr.setOldPtr((dnsData.getReq().equals("req_modify") && dnsData.getOldPtrTxt().isEmpty()) ? "N/A"
					: dnsData.getOldPtrTxt());
			dnsBulkPtr.setErrorStatus("0");
			dnsBulkPtr.setDeleteStatus("0");
			dnsBulkPtr = dnsBulkPtrRepo.save(dnsBulkPtr);
			id = dnsBulkPtr.getId();
			break;
		case "spf":
			DnsBulkSpf dnsBulkSpf = new DnsBulkSpf();
			dnsBulkSpf.setCampaignId(campaignId);
			dnsBulkSpf.setDomain((dnsData.getDomain().isEmpty()) ? "N/A" : dnsData.getDomain());
			dnsBulkSpf.setSpf((dnsData.getSpfTxt().isEmpty()) ? "N/A" : dnsData.getSpfTxt());
			dnsBulkSpf.setLocation((dnsData.getServerLocation().isEmpty()) ? "N/A" : dnsData.getServerLocation());
			dnsBulkSpf.setMigrationDate(
					(dnsData.getMigrationDate().toString().isEmpty()) ? "N/A" : dnsData.getMigrationDate().toString());
			dnsBulkSpf.setOldSpf((dnsData.getReq().equals("req_modify") && dnsData.getOldSpfTxt().isEmpty()) ? "N/A"
					: dnsData.getOldSpfTxt());
			dnsBulkSpf.setErrorStatus("0");
			dnsBulkSpf.setDeleteStatus("0");
			dnsBulkSpf = dnsBulkSpfRepo.save(dnsBulkSpf);
			id = dnsBulkSpf.getId();
			break;
		case "srv":
			DnsBulkSrv dnsBulkSrv = new DnsBulkSrv();
			dnsBulkSrv.setCampaignId(campaignId);
			dnsBulkSrv.setDomain((dnsData.getDomain().isEmpty()) ? "N/A" : dnsData.getDomain());
			dnsBulkSrv.setSrv((dnsData.getSrvTxt().isEmpty()) ? "N/A" : dnsData.getSrvTxt());
			dnsBulkSrv.setLocation((dnsData.getServerLocation().isEmpty()) ? "N/A" : dnsData.getServerLocation());
			dnsBulkSrv.setMigrationDate(
					(dnsData.getMigrationDate().toString().isEmpty()) ? "N/A" : dnsData.getMigrationDate().toString());
			dnsBulkSrv.setOldSrv((dnsData.getReq().equals("req_modify") && dnsData.getOldSrvTxt().isEmpty()) ? "N/A"
					: dnsData.getOldSrvTxt());
			dnsBulkSrv.setErrorStatus("0");
			dnsBulkSrv.setDeleteStatus("0");
			dnsBulkSrv = dnsBulkSrvRepo.save(dnsBulkSrv);
			id = dnsBulkSrv.getId();
			break;
		case "dmarc":
			DnsBulkDmarc dnsBulkDmarc = new DnsBulkDmarc();
			dnsBulkDmarc.setCampaignId(campaignId);
			dnsBulkDmarc.setDomain((dnsData.getDomain().isEmpty()) ? "N/A" : dnsData.getDomain());
			dnsBulkDmarc.setDmarc((dnsData.getDmarcTxt().isEmpty()) ? "N/A" : dnsData.getDmarcTxt());
			dnsBulkDmarc.setLocation((dnsData.getServerLocation().isEmpty()) ? "N/A" : dnsData.getServerLocation());
			dnsBulkDmarc.setMigrationDate(
					(dnsData.getMigrationDate().toString().isEmpty()) ? "N/A" : dnsData.getMigrationDate().toString());
			dnsBulkDmarc
					.setOldDmarc((dnsData.getReq().equals("req_modify") && dnsData.getOldDmarcTxt().isEmpty()) ? "N/A"
							: dnsData.getOldDmarcTxt());
			dnsBulkDmarc.setErrorStatus("0");
			dnsBulkDmarc.setDeleteStatus("0");
			dnsBulkDmarc = dnsBulkDmarcRepo.save(dnsBulkDmarc);
			id = dnsBulkDmarc.getId();
			break;
		case "aaaa":
			DnsBulkUpload dnsBulkUpload = new DnsBulkUpload();
			dnsBulkUpload.setCampaignId(campaignId);
			dnsBulkUpload.setDomain((dnsData.getDomain().isEmpty()) ? "N/A" : dnsData.getDomain());
			dnsBulkUpload.setCname((dnsData.getCname().isEmpty()) ? "N/A" : dnsData.getCname());
			dnsBulkUpload.setLocation((dnsData.getServerLocation().isEmpty()) ? "N/A" : dnsData.getServerLocation());
			dnsBulkUpload.setMigrationDate(
					(dnsData.getMigrationDate().toString().isEmpty()) ? "N/A" : dnsData.getMigrationDate().toString());
			dnsBulkUpload.setOldIp((dnsData.getReq().equals("req_modify") && dnsData.getOldIp().isEmpty()) ? "N/A"
					: dnsData.getOldIp());
			dnsBulkUpload.setNewIp((dnsData.getNewIp().isEmpty()) ? "N/A" : dnsData.getNewIp());
			dnsBulkUpload.setErrorStatus("0");
			dnsBulkUpload.setDeleteStatus("0");
			dnsBulkUpload = dnsBulkAaaaRepo.save(dnsBulkUpload);
			id = dnsBulkUpload.getId();
			break;
		}
		return id;
	}

	public Long addInvalidRecordsInBulkTable(ValidatedDnsBean dnsData, String errorInString, Long campaignId,
			String recordType) {
		dnsData = initializeDnsDataBeanWithEmptyValues(dnsData);
		Long id = -1l;
		switch (recordType) {
		case "cname":
			DnsBulkCname dnsBulkCname = new DnsBulkCname();
			dnsBulkCname.setCampaignId(campaignId);
			dnsBulkCname.setDomain((dnsData.getDomain().isEmpty()) ? "N/A" : dnsData.getDomain());
			dnsBulkCname.setCname((dnsData.getCnameTxt().isEmpty()) ? "N/A" : dnsData.getCnameTxt());
			dnsBulkCname.setLocation((dnsData.getServerLocation().isEmpty()) ? "N/A" : dnsData.getServerLocation());
			dnsBulkCname.setMigrationDate(
					(dnsData.getMigrationDate().toString().isEmpty()) ? "N/A" : dnsData.getMigrationDate().toString());
			dnsBulkCname
					.setOldCname((dnsData.getReq().equals("req_modify") && dnsData.getOldCnameTxt().isEmpty()) ? "N/A"
							: dnsData.getOldCnameTxt());
			dnsBulkCname.setErrorStatus("1");
			dnsBulkCname.setDeleteStatus("0");
			dnsBulkCname.setDnsError(errorInString);
			dnsBulkCname = dnsBulkCnameRepo.save(dnsBulkCname);
			id = dnsBulkCname.getId();
			break;
		case "mx":
			DnsBulkMx dnsBulkMx = new DnsBulkMx();
			dnsBulkMx.setCampaignId(campaignId);
			dnsBulkMx.setDomain((dnsData.getDomain().isEmpty()) ? "N/A" : dnsData.getDomain());
			dnsBulkMx.setMx((dnsData.getMxTxt().isEmpty()) ? "N/A" : dnsData.getMxTxt());
			dnsBulkMx.setLocation((dnsData.getServerLocation().isEmpty()) ? "N/A" : dnsData.getServerLocation());
			dnsBulkMx.setMigrationDate(
					(dnsData.getMigrationDate().toString().isEmpty()) ? "N/A" : dnsData.getMigrationDate().toString());
			dnsBulkMx.setOldMx((dnsData.getReq().equals("req_modify") && dnsData.getOldMxTxt().isEmpty()) ? "N/A"
					: dnsData.getOldMxTxt());
			dnsBulkMx.setErrorStatus("1");
			dnsBulkMx.setDeleteStatus("0");
			dnsBulkMx.setDnsError(errorInString);
			dnsBulkMx = dnsBulkMxRepo.save(dnsBulkMx);
			id = dnsBulkMx.getId();
			break;
		case "txt":
			DnsBulkTxt dnsBulkTxt = new DnsBulkTxt();
			dnsBulkTxt.setCampaignId(campaignId);
			dnsBulkTxt.setDomain((dnsData.getDomain().isEmpty()) ? "N/A" : dnsData.getDomain());
			dnsBulkTxt.setTxt((dnsData.getTxtTxt().isEmpty()) ? "N/A" : dnsData.getTxtTxt());
			dnsBulkTxt.setLocation((dnsData.getServerLocation().isEmpty()) ? "N/A" : dnsData.getServerLocation());
			dnsBulkTxt.setMigrationDate(
					(dnsData.getMigrationDate().toString().isEmpty()) ? "N/A" : dnsData.getMigrationDate().toString());
			dnsBulkTxt.setOldTxt((dnsData.getReq().equals("req_modify") && dnsData.getOldTxtTxt().isEmpty()) ? "N/A"
					: dnsData.getOldTxtTxt());
			dnsBulkTxt.setErrorStatus("1");
			dnsBulkTxt.setDeleteStatus("0");
			dnsBulkTxt.setDnsError(errorInString);
			dnsBulkTxt = dnsBulkTxtRepo.save(dnsBulkTxt);
			id = dnsBulkTxt.getId();
			break;
		case "ptr":
			DnsBulkPtr dnsBulkPtr = new DnsBulkPtr();
			dnsBulkPtr.setCampaignId(campaignId);
			dnsBulkPtr.setDomain((dnsData.getDomain().isEmpty()) ? "N/A" : dnsData.getDomain());
			dnsBulkPtr.setPtr((dnsData.getPtrTxt().isEmpty()) ? "N/A" : dnsData.getPtrTxt());
			dnsBulkPtr.setLocation((dnsData.getServerLocation().isEmpty()) ? "N/A" : dnsData.getServerLocation());
			dnsBulkPtr.setMigrationDate(
					(dnsData.getMigrationDate().toString().isEmpty()) ? "N/A" : dnsData.getMigrationDate().toString());
			dnsBulkPtr.setOldPtr((dnsData.getReq().equals("req_modify") && dnsData.getOldPtrTxt().isEmpty()) ? "N/A"
					: dnsData.getOldPtrTxt());
			dnsBulkPtr.setErrorStatus("1");
			dnsBulkPtr.setDeleteStatus("0");
			dnsBulkPtr.setDnsError(errorInString);
			dnsBulkPtr = dnsBulkPtrRepo.save(dnsBulkPtr);
			id = dnsBulkPtr.getId();
			break;
		case "spf":
			DnsBulkSpf dnsBulkSpf = new DnsBulkSpf();
			dnsBulkSpf.setCampaignId(campaignId);
			dnsBulkSpf.setDomain((dnsData.getDomain().isEmpty()) ? "N/A" : dnsData.getDomain());
			dnsBulkSpf.setSpf((dnsData.getSpfTxt().isEmpty()) ? "N/A" : dnsData.getSpfTxt());
			dnsBulkSpf.setLocation((dnsData.getServerLocation().isEmpty()) ? "N/A" : dnsData.getServerLocation());
			dnsBulkSpf.setMigrationDate(
					(dnsData.getMigrationDate().toString().isEmpty()) ? "N/A" : dnsData.getMigrationDate().toString());
			dnsBulkSpf.setOldSpf((dnsData.getReq().equals("req_modify") && dnsData.getOldSpfTxt().isEmpty()) ? "N/A"
					: dnsData.getOldSpfTxt());
			dnsBulkSpf.setErrorStatus("1");
			dnsBulkSpf.setDeleteStatus("0");
			dnsBulkSpf.setDnsError(errorInString);
			dnsBulkSpf = dnsBulkSpfRepo.save(dnsBulkSpf);
			id = dnsBulkSpf.getId();
			break;
		case "srv":
			DnsBulkSrv dnsBulkSrv = new DnsBulkSrv();
			dnsBulkSrv.setCampaignId(campaignId);
			dnsBulkSrv.setDomain((dnsData.getDomain().isEmpty()) ? "N/A" : dnsData.getDomain());
			dnsBulkSrv.setSrv((dnsData.getSrvTxt().isEmpty()) ? "N/A" : dnsData.getSrvTxt());
			dnsBulkSrv.setLocation((dnsData.getServerLocation().isEmpty()) ? "N/A" : dnsData.getServerLocation());
			dnsBulkSrv.setMigrationDate(
					(dnsData.getMigrationDate().toString().isEmpty()) ? "N/A" : dnsData.getMigrationDate().toString());
			dnsBulkSrv.setOldSrv((dnsData.getReq().equals("req_modify") && dnsData.getOldSrvTxt().isEmpty()) ? "N/A"
					: dnsData.getOldSrvTxt());
			dnsBulkSrv.setErrorStatus("1");
			dnsBulkSrv.setDeleteStatus("0");
			dnsBulkSrv.setDnsError(errorInString);
			dnsBulkSrv = dnsBulkSrvRepo.save(dnsBulkSrv);
			id = dnsBulkSrv.getId();
			break;
		case "dmarc":
			DnsBulkDmarc dnsBulkDmarc = new DnsBulkDmarc();
			dnsBulkDmarc.setCampaignId(campaignId);
			dnsBulkDmarc.setDomain((dnsData.getDomain().isEmpty()) ? "N/A" : dnsData.getDomain());
			dnsBulkDmarc.setDmarc((dnsData.getDmarcTxt().isEmpty()) ? "N/A" : dnsData.getDmarcTxt());
			dnsBulkDmarc.setLocation((dnsData.getServerLocation().isEmpty()) ? "N/A" : dnsData.getServerLocation());
			dnsBulkDmarc.setMigrationDate(
					(dnsData.getMigrationDate().toString().isEmpty()) ? "N/A" : dnsData.getMigrationDate().toString());
			dnsBulkDmarc
					.setOldDmarc((dnsData.getReq().equals("req_modify") && dnsData.getOldDmarcTxt().isEmpty()) ? "N/A"
							: dnsData.getOldDmarcTxt());
			dnsBulkDmarc.setErrorStatus("1");
			dnsBulkDmarc.setDeleteStatus("0");
			dnsBulkDmarc.setDnsError(errorInString);
			dnsBulkDmarc = dnsBulkDmarcRepo.save(dnsBulkDmarc);
			id = dnsBulkDmarc.getId();
			break;
		case "aaaa":
			DnsBulkUpload dnsBulkUpload = new DnsBulkUpload();
			dnsBulkUpload.setCampaignId(campaignId);
			dnsBulkUpload.setDomain((dnsData.getDomain().isEmpty()) ? "N/A" : dnsData.getDomain());
			dnsBulkUpload.setCname((dnsData.getCname().isEmpty()) ? "N/A" : dnsData.getCname());
			dnsBulkUpload.setLocation((dnsData.getServerLocation().isEmpty()) ? "N/A" : dnsData.getServerLocation());
			dnsBulkUpload.setMigrationDate(
					(dnsData.getMigrationDate().toString().isEmpty()) ? "N/A" : dnsData.getMigrationDate().toString());
			dnsBulkUpload.setOldIp((dnsData.getReq().equals("req_modify") && dnsData.getOldIp().isEmpty()) ? "N/A"
					: dnsData.getOldIp());
			dnsBulkUpload.setNewIp((dnsData.getNewIp().isEmpty()) ? "N/A" : dnsData.getNewIp());
			dnsBulkUpload.setErrorStatus("1");
			dnsBulkUpload.setDeleteStatus("0");
			dnsBulkUpload.setDnsError(errorInString);
			dnsBulkUpload = dnsBulkAaaaRepo.save(dnsBulkUpload);
			id = dnsBulkUpload.getId();
			break;
		}
		
		return id;

	}

	private ValidatedDnsBean initializeDnsDataBeanWithEmptyValues(ValidatedDnsBean dnsData) {
		if (dnsData.getDomain() == null)
			dnsData.setDomain("");
		if (dnsData.getCname() == null)
			dnsData.setCname("");
		if (dnsData.getNewIp() == null)
			dnsData.setNewIp("");
		if (dnsData.getOldIp() == null)
			dnsData.setOldIp("");

		if (dnsData.getCnameTxt() == null)
			dnsData.setCnameTxt("");
		if (dnsData.getDmarcTxt() == null)
			dnsData.setDmarcTxt("");
		if (dnsData.getMxTxt() == null)
			dnsData.setMxTxt("");
		if (dnsData.getSpfTxt() == null)
			dnsData.setSpfTxt("");
		if (dnsData.getSrvTxt() == null)
			dnsData.setSrvTxt("");
		if (dnsData.getTxtTxt() == null)
			dnsData.setTxtTxt("");
		if (dnsData.getPtrTxt() == null)
			dnsData.setPtrTxt("");

		if (dnsData.getOldCnameTxt() == null)
			dnsData.setOldCnameTxt("");
		if (dnsData.getOldDmarcTxt() == null)
			dnsData.setOldDmarcTxt("");
		if (dnsData.getOldMxTxt() == null)
			dnsData.setOldMxTxt("");
		if (dnsData.getOldPtrTxt() == null)
			dnsData.setOldPtrTxt("");
		if (dnsData.getOldSpfTxt() == null)
			dnsData.setOldSpfTxt("");
		if (dnsData.getOldSrvTxt() == null)
			dnsData.setOldSrvTxt("");
		if (dnsData.getOldTxtTxt() == null)
			dnsData.setOldTxtTxt("");

		if (dnsData.getMigrationDate() == null) {
			LocalDate currentDate = LocalDate.now();
			dnsData.setMigrationDate(currentDate);
		}

		if (dnsData.getServerLocation() == null)
			dnsData.setServerLocation("");
		if (dnsData.getReq() == null)
			dnsData.setReq("");
		if (dnsData.getRegistrationNumber() == null)
			dnsData.setRegistrationNumber("");
		if (dnsData.getReqOtherAdd() == null)
			dnsData.setReqOtherAdd("");
		return dnsData;
	}

	public Set<String> fetchOwner(String url) {
		List<String> completedRegNumbers = fetchCompletedDnsRegistrationNumbers(url, "url");
		List<String> ownersFromEformsTable = null;
		Set<String> finalSet = null;
		List<String> finalListOfOwners = new ArrayList<>();

		if (completedRegNumbers != null && !completedRegNumbers.isEmpty()) {
			ownersFromEformsTable = dnsBaseRepo.findApplicants(completedRegNumbers);
		}
		List<String> ownersFromDnsTeamTable = dnsTeamDataRepo.findOwnersByDomain(url);
		List<String> finalOwners = new ArrayList<>();
		if (ownersFromEformsTable != null)
			finalOwners.addAll(ownersFromEformsTable);

		if (ownersFromDnsTeamTable != null)
			finalOwners.addAll(ownersFromDnsTeamTable);

		if (!finalOwners.isEmpty())
			finalSet = new HashSet<>(finalOwners);
		else
			finalSet = new HashSet<>();

		for (String email : finalSet) {
			email = email.replaceAll("\\r\\n|\\r|\\n", "");
			// String lineSeparator=System.lineSeparator();
			// String newString=email.replace(lineSeparator, "");
			List<String> aliases = utilityService.aliases(email);
			finalListOfOwners.addAll(aliases);
		}

		return new HashSet<>(finalListOfOwners);
	}

	public List<String> fetchCompletedDnsRegistrationNumbers(String url, String flag) {
		List<String> regNumbers = null;
		if (flag.equalsIgnoreCase("url")) {
			regNumbers = dnsRegistrationUrlRepo.findByDnsUrl(url);
		} else if (flag.equalsIgnoreCase("ip")) {
			regNumbers = dnsRegistrationNewIpRepo.findByNewIp(url);
		}

		if (!regNumbers.isEmpty()) {
			return utilityService.fetchCompletedRegNumbers(regNumbers);
		} else {
			return new ArrayList<>();
		}
	}

	public String checkDnsEformsFinalMapping(String url, String flag) {
		String result = "";
		List<String> completedRegNumbers = fetchCompletedDnsRegistrationNumbers(url, flag);

		if (!completedRegNumbers.isEmpty()) {
			List<DnsBase> dnsBaseList = dnsBaseRepo.findByRegistrationNoIn(completedRegNumbers);
			TreeSet<String> treeSet = new TreeSet<>();
			for (DnsBase dnsBase : dnsBaseList) {
				String r = dnsBase.getRegistrationNo() + ":" + dnsBase.getReqFor();
				treeSet.add(r);
			}

			String[] arr = treeSet.last().split(":");
			if (!arr[1].equals("req_delete")) {
				if (flag.equals("url")) {
					result = dnsRegistrationNewIpRepo.findNewIpByRegNo(arr[0]);
				} else if (flag.equals("ip")) {
					result = dnsRegistrationUrlRepo.findDomainByRegNo(arr[0]);
				}
			} else {
				result = "deleted";
			}
		}
		if (result == null) {
			return "";
		} else {
			return result;
		}
	}

	public String checkDnsDataFromDnsAdminFinalMapping(String url, String flag) {
		String result = "";
		List<String> dnsTeamDataResult = null;
		if (flag.equalsIgnoreCase("url")) {
			dnsTeamDataResult = dnsTeamDataRepo.findIpByDomain(url);
		} else if (flag.equalsIgnoreCase("ip")) {
			dnsTeamDataResult = dnsTeamDataRepo.findDomainByIp(url);
		}

		if (dnsTeamDataResult != null && !dnsTeamDataResult.isEmpty()) {
			for (int i = 0; i < dnsTeamDataResult.size(); i++) {
				String ipOrDomain = dnsTeamDataResult.get(i);
				result = ipOrDomain;
				if (flag.equalsIgnoreCase("url")) {
					break;
				} else if (flag.equalsIgnoreCase("ip")) {
					if (isThirdLevelDomain(result)) {
						break;
					}
				}
			}
		}
		return result;
	}

	public String dbReverseDNSLookup(String ip) {
		String domain = "";
		List<String> regNumbers = dnsRegistrationNewIpRepo.findByNewIp(ip);
		List<String> completedRegNos = utilityService.fetchCompletedRegNumbers(regNumbers);
		completedRegNos.replaceAll(String::toUpperCase);
		Collections.sort(completedRegNos, Collections.reverseOrder());
		for (String registrationNo : completedRegNos) {
			DnsBase dnsBase = dnsBaseRepo.findByRegistrationNo(registrationNo);
			if (dnsBase.getReqFor().equalsIgnoreCase("req_delete")) {
				domain = "";
			} else {
				List<DnsRegistrationUrl> dnsRegUrlList = dnsRegistrationUrlRepo.findByRegistrationNo(registrationNo);
				if (dnsRegUrlList == null) {
					domain = "";
				} else {
					Set<String> uniqueRegNumbers = new HashSet<>();
					List<String> regNumbersbyUrls = null;
					for (DnsRegistrationUrl dnsRegUrl : dnsRegUrlList) {
						regNumbersbyUrls = dnsRegistrationUrlRepo.findByDnsUrl(dnsRegUrl.getDnsUrl());
						if(regNumbersbyUrls != null) {
							uniqueRegNumbers.addAll(regNumbersbyUrls);
						}
					}
					List<String> finalRegNumbersByUrls = new ArrayList<>(uniqueRegNumbers); 
					List<String> completedRegNosForUrls = utilityService.fetchCompletedRegNumbers(finalRegNumbersByUrls);
					completedRegNos.replaceAll(String::toUpperCase);
					Collections.sort(completedRegNos, Collections.reverseOrder());
					for (String registrationNo1 : completedRegNosForUrls) {
						DnsBase dnsBase1 = dnsBaseRepo.findByRegistrationNo(registrationNo1);
						if (dnsBase1.getReqFor().equalsIgnoreCase("req_new")
								|| dnsBase1.getReqFor().equalsIgnoreCase("req_modify")) {
							List<DnsRegistrationNewip> dnsRegNewIpList = dnsRegistrationNewIpRepo
									.findByRegistrationNo(registrationNo1);
							if (dnsRegNewIpList == null) {
								domain = "";
							}else {
								boolean flag = false;
								for (DnsRegistrationNewip dnsRegNewIp : dnsRegNewIpList) {
									if(dnsRegNewIp.getNewIp().equalsIgnoreCase(ip)) {
										flag = true;
										break;
									}
								}
								
								if(!flag) {
									// verify IP through nslookup
									flag = false;
									for (DnsRegistrationUrl dnsRegUrl : dnsRegUrlList) {
										String ipFromNslookup = checkDomain(dnsRegUrl.getDnsUrl());
										if (ipFromNslookup.equalsIgnoreCase(ip)) {
											flag = true;
											break;
										}
									}
									if(!flag) {
										domain = "";
									}
								}
							} 
						}
					}

				}
			}
		}
		return domain;

	}

	public Object fetchSuccessfullyEnteredRecords(@NotEmpty String regNo) {
		DnsBulkCampaigns campaignData = dnsCampaignRepo.findByRegistrationNo(regNo);
		if (campaignData != null)
			return fetchSuccessfulBulkUploadData(campaignData.getId(), campaignData.getReqOtherAdd());

		return null;
	}

	public Object fetchSuccessfulBulkUploadData(Long campaignId, String request_other_add) {
		Object o = null;
		switch (request_other_add) {
		case "cname":
			o = dnsBulkCnameRepo.findByCampaignIdAndErrorStatusAndDeleteStatus(campaignId, "0", "0");
			break;
		case "mx":
			o = dnsBulkMxRepo.findByCampaignIdAndErrorStatusAndDeleteStatus(campaignId, "0", "0");
			break;
		case "txt":
			o = dnsBulkTxtRepo.findByCampaignIdAndErrorStatusAndDeleteStatus(campaignId, "0", "0");
			break;
		case "ptr":
			o = dnsBulkPtrRepo.findByCampaignIdAndErrorStatusAndDeleteStatus(campaignId, "0", "0");
			break;
		case "spf":
			o = dnsBulkSpfRepo.findByCampaignIdAndErrorStatusAndDeleteStatus(campaignId, "0", "0");
			break;
		case "srv":
			o = dnsBulkSrvRepo.findByCampaignIdAndErrorStatusAndDeleteStatus(campaignId, "0", "0");
			break;
		case "dmarc":
			o = dnsBulkDmarcRepo.findByCampaignIdAndErrorStatusAndDeleteStatus(campaignId, "0", "0");
			break;
		case "aaaa":
			o = dnsBulkAaaaRepo.findByCampaignIdAndErrorStatusAndDeleteStatus(campaignId, "0", "0");
			break;
		}
		return o;
	}

	public List<DnsBulkCampaigns> fetchOpenCampaigns(String email) {
		return dnsCampaignRepo.findByOwnerEmailAndStatusAndDiscardStatus(email, "0", "0");
	}

	public Object fetchSingleRecord(@NotEmpty Long id, @NotEmpty String recordType) {
		Object o = null;
		switch (recordType) {
		case "cname":
			Optional<DnsBulkCname> dnsBulkCnameOptional = dnsBulkCnameRepo.findById(id);
			if (dnsBulkCnameOptional.isPresent()) {
				o = dnsBulkCnameOptional.get();
			}
			break;
		case "mx":
			Optional<DnsBulkMx> dnsBulkMxOptional = dnsBulkMxRepo.findById(id);
			if (dnsBulkMxOptional.isPresent()) {
				o = dnsBulkMxOptional.get();
			}
			break;
		case "txt":
			Optional<DnsBulkTxt> dnsBulkTxtOptional = dnsBulkTxtRepo.findById(id);
			if (dnsBulkTxtOptional.isPresent()) {
				o = dnsBulkTxtOptional.get();
			}
			break;
		case "ptr":
			Optional<DnsBulkPtr> dnsBulkPtrOptional = dnsBulkPtrRepo.findById(id);
			if (dnsBulkPtrOptional.isPresent()) {
				o = dnsBulkPtrOptional.get();
			}
			break;
		case "spf":
			Optional<DnsBulkSpf> dnsBulkSpfOptional = dnsBulkSpfRepo.findById(id);
			if (dnsBulkSpfOptional.isPresent()) {
				o = dnsBulkSpfOptional.get();
			}
			break;
		case "srv":
			Optional<DnsBulkSrv> dnsBulkSrvOptional = dnsBulkSrvRepo.findById(id);
			if (dnsBulkSrvOptional.isPresent()) {
				o = dnsBulkSrvOptional.get();
			}
			break;
		case "dmarc":
			Optional<DnsBulkDmarc> dnsBulkDmarcOptional = dnsBulkDmarcRepo.findById(id);
			if (dnsBulkDmarcOptional.isPresent()) {
				o = dnsBulkDmarcOptional.get();
			}
			break;
		case "aaaa":
			Optional<DnsBulkUpload> dnsBulkAaaaOptional = dnsBulkAaaaRepo.findById(id);
			if (dnsBulkAaaaOptional.isPresent()) {
				o = dnsBulkAaaaOptional.get();
			}
			break;
		}
		return o;
	}

	public List<Object> findDuplicateRecords(ValidatedDnsBean dnsBean) {
		List<Object> o = new ArrayList<>();
		switch (dnsBean.getReqOtherAdd()) {
		case "cname":
			List<DnsBulkCname> dnsBulkCname = dnsBulkCnameRepo
					.findByCampaignIdAndDomainAndCnameAndErrorStatusAndDeleteStatus((long) dnsBean.getCampaignId(),
							dnsBean.getDomain(), dnsBean.getCnameTxt(), "0", "0");
			if (dnsBulkCname != null && !dnsBulkCname.isEmpty())
				o.add(dnsBulkCname);
			break;
		case "mx":
			List<DnsBulkMx> dnsBulkMx = dnsBulkMxRepo.findByCampaignIdAndDomainAndMxAndErrorStatusAndDeleteStatus(
					(long) dnsBean.getCampaignId(), dnsBean.getDomain(), dnsBean.getMxTxt(), "0", "0");
			if (dnsBulkMx != null && !dnsBulkMx.isEmpty())
				o.add(dnsBulkMx);
			break;
		case "txt":
			List<DnsBulkTxt> dnsBulkTxt = dnsBulkTxtRepo.findByCampaignIdAndDomainAndTxtAndErrorStatusAndDeleteStatus(
					(long) dnsBean.getCampaignId(), dnsBean.getDomain(), dnsBean.getTxtTxt(), "0", "0");
			if (dnsBulkTxt != null && !dnsBulkTxt.isEmpty())
				o.add(dnsBulkTxt);
			break;
		case "ptr":
			List<DnsBulkPtr> dnsBulkPtr = dnsBulkPtrRepo.findByCampaignIdAndDomainAndPtrAndErrorStatusAndDeleteStatus(
					(long) dnsBean.getCampaignId(), dnsBean.getDomain(), dnsBean.getPtrTxt(), "0", "0");
			if (dnsBulkPtr != null && !dnsBulkPtr.isEmpty())
				o.add(dnsBulkPtr);
			break;
		case "spf":
			List<DnsBulkSpf> dnsBulkSpf = dnsBulkSpfRepo.findByCampaignIdAndDomainAndSpfAndErrorStatusAndDeleteStatus(
					(long) dnsBean.getCampaignId(), dnsBean.getDomain(), dnsBean.getSpfTxt(), "0", "0");
			if (dnsBulkSpf != null && !dnsBulkSpf.isEmpty())
				o.add(dnsBulkSpf);
			break;
		case "srv":
			List<DnsBulkSrv> dnsBulkSrv = dnsBulkSrvRepo.findByCampaignIdAndDomainAndSrvAndErrorStatusAndDeleteStatus(
					(long) dnsBean.getCampaignId(), dnsBean.getDomain(), dnsBean.getSrvTxt(), "0", "0");
			if (dnsBulkSrv != null && !dnsBulkSrv.isEmpty())
				o.add(dnsBulkSrv);
			break;
		case "dmarc":
			List<DnsBulkDmarc> dnsBulkDmarc = dnsBulkDmarcRepo
					.findByCampaignIdAndDomainAndDmarcAndErrorStatusAndDeleteStatus((long) dnsBean.getCampaignId(),
							dnsBean.getDomain(), dnsBean.getDmarcTxt(), "0", "0");
			if (dnsBulkDmarc != null && !dnsBulkDmarc.isEmpty())
				o.add(dnsBulkDmarc);
			break;
		case "aaaa":
			List<DnsBulkUpload> dnsBulkUpload = null;
			if (dnsBean.getReq().equalsIgnoreCase("req_delete")) {
				dnsBulkUpload = dnsBulkAaaaRepo.findByCampaignIdAndDomainAndErrorStatusAndDeleteStatus(
						(long) dnsBean.getCampaignId(), dnsBean.getDomain(), "0", "0");
			} else {
				dnsBulkUpload = dnsBulkAaaaRepo.findByCampaignIdAndDomainAndNewIpAndErrorStatusAndDeleteStatus(
						(long) dnsBean.getCampaignId(), dnsBean.getDomain(), dnsBean.getNewIp(), "0", "0");
			}
			if (dnsBulkUpload != null && !dnsBulkUpload.isEmpty())
				o.add(dnsBulkUpload);
			break;
		}
		return o;
	}

	public void updateUrlTable(ValidatedDnsBean dnsBean) {
		DnsRegistrationUrl dnsRegUrl = dnsRegistrationUrlRepo.findByDnsId(dnsBean.getId());
		dnsRegUrl.setDnsUrl(dnsBean.getDomain());
		dnsRegistrationUrlRepo.save(dnsRegUrl);
	}

	public void updateCnameTable(ValidatedDnsBean dnsBean) {
		DnsRegistrationCname dnsRegCname = dnsRegistrationCnameRepo.findByDnsId(dnsBean.getId());
		dnsRegCname.setCname(dnsBean.getCname());
		dnsRegistrationCnameRepo.save(dnsRegCname);
	}

	public void updateNewIpTable(ValidatedDnsBean dnsBean) {
		DnsRegistrationNewip dnsRegNewIp = dnsRegistrationNewIpRepo.findByDnsId(dnsBean.getId());
		dnsRegNewIp.setNewIp(dnsBean.getNewIp());
		dnsRegistrationNewIpRepo.save(dnsRegNewIp);
	}

	public void updateOldIpTable(ValidatedDnsBean dnsBean) {
		DnsRegistrationOldip dnsRegOldIp = dnsRegistrationOldIpRepo.findByDnsId(dnsBean.getId());
		dnsRegOldIp.setOldip(dnsBean.getOldIp());
		dnsRegistrationOldIpRepo.save(dnsRegOldIp);
	}

	public void updateValidRecordsInBulkTable(ValidatedDnsBean dnsData) {
		switch (dnsData.getReqOtherAdd()) {
		case "cname":
			Optional<DnsBulkCname> dnsBulkCnameOptional = dnsBulkCnameRepo.findById(dnsData.getId());
			if (dnsBulkCnameOptional.isPresent()) {
				DnsBulkCname dnsBulkCname = dnsBulkCnameOptional.get();
				dnsBulkCname.setDomain((dnsData.getDomain().isEmpty()) ? "N/A" : dnsData.getDomain());
				dnsBulkCname.setCname((dnsData.getCnameTxt().isEmpty()) ? "N/A" : dnsData.getCnameTxt());
				dnsBulkCname.setLocation((dnsData.getServerLocation().isEmpty()) ? "N/A" : dnsData.getServerLocation());
				dnsBulkCname.setMigrationDate((dnsData.getMigrationDate().toString().isEmpty()) ? "N/A"
						: dnsData.getMigrationDate().toString());
				dnsBulkCname.setOldCname(
						(dnsData.getReq().equals("req_modify") && dnsData.getOldCnameTxt().isEmpty()) ? "N/A"
								: dnsData.getOldCnameTxt());
				dnsBulkCname.setErrorStatus("0");
				dnsBulkCname.setDeleteStatus("0");
				dnsBulkCname = dnsBulkCnameRepo.save(dnsBulkCname);
				log.info("For registration no {} cname update in ID {}",dnsBulkCname.getRegistrationNo(),dnsBulkCname.getId());
			}
			break;
		case "mx":
			Optional<DnsBulkMx> dnsBulkMxOptional = dnsBulkMxRepo.findById(dnsData.getId());
			if (dnsBulkMxOptional.isPresent()) {
				DnsBulkMx dnsBulkMx = dnsBulkMxOptional.get();
				dnsBulkMx.setDomain((dnsData.getDomain().isEmpty()) ? "N/A" : dnsData.getDomain());
				dnsBulkMx.setMx((dnsData.getMxTxt().isEmpty()) ? "N/A" : dnsData.getMxTxt());
				dnsBulkMx.setLocation((dnsData.getServerLocation().isEmpty()) ? "N/A" : dnsData.getServerLocation());
				dnsBulkMx.setMigrationDate((dnsData.getMigrationDate().toString().isEmpty()) ? "N/A"
						: dnsData.getMigrationDate().toString());
				dnsBulkMx.setOldMx((dnsData.getReq().equals("req_modify") && dnsData.getOldMxTxt().isEmpty()) ? "N/A"
						: dnsData.getOldMxTxt());
				dnsBulkMx.setErrorStatus("0");
				dnsBulkMx.setDeleteStatus("0");
				dnsBulkMx = dnsBulkMxRepo.save(dnsBulkMx);
				log.info("For registration no {} mx update in ID {}",dnsBulkMx.getRegistrationNo(),dnsBulkMx.getId());
			}
			break;
		case "txt":
			Optional<DnsBulkTxt> dnsBulkTxtOptional = dnsBulkTxtRepo.findById(dnsData.getId());
			if (dnsBulkTxtOptional.isPresent()) {
				DnsBulkTxt dnsBulkTxt = dnsBulkTxtOptional.get();
				dnsBulkTxt.setDomain((dnsData.getDomain().isEmpty()) ? "N/A" : dnsData.getDomain());
				dnsBulkTxt.setTxt((dnsData.getTxtTxt().isEmpty()) ? "N/A" : dnsData.getTxtTxt());
				dnsBulkTxt.setLocation((dnsData.getServerLocation().isEmpty()) ? "N/A" : dnsData.getServerLocation());
				dnsBulkTxt.setMigrationDate((dnsData.getMigrationDate().toString().isEmpty()) ? "N/A"
						: dnsData.getMigrationDate().toString());
				dnsBulkTxt.setOldTxt((dnsData.getReq().equals("req_modify") && dnsData.getOldTxtTxt().isEmpty()) ? "N/A"
						: dnsData.getOldTxtTxt());
				dnsBulkTxt.setErrorStatus("0");
				dnsBulkTxt.setDeleteStatus("0");
				dnsBulkTxt = dnsBulkTxtRepo.save(dnsBulkTxt);
				log.info("For registration no {} txt update in ID {}",dnsBulkTxt.getRegistrationNo(),dnsBulkTxt.getId());
			}
			break;
		case "ptr":
			Optional<DnsBulkPtr> dnsBulkPtrOptional = dnsBulkPtrRepo.findById(dnsData.getId());
			if (dnsBulkPtrOptional.isPresent()) {
				DnsBulkPtr dnsBulkPtr = dnsBulkPtrOptional.get();
				dnsBulkPtr.setDomain((dnsData.getDomain().isEmpty()) ? "N/A" : dnsData.getDomain());
				dnsBulkPtr.setPtr((dnsData.getPtrTxt().isEmpty()) ? "N/A" : dnsData.getPtrTxt());
				dnsBulkPtr.setLocation((dnsData.getServerLocation().isEmpty()) ? "N/A" : dnsData.getServerLocation());
				dnsBulkPtr.setMigrationDate((dnsData.getMigrationDate().toString().isEmpty()) ? "N/A"
						: dnsData.getMigrationDate().toString());
				dnsBulkPtr.setOldPtr((dnsData.getReq().equals("req_modify") && dnsData.getOldPtrTxt().isEmpty()) ? "N/A"
						: dnsData.getOldPtrTxt());
				dnsBulkPtr.setErrorStatus("0");
				dnsBulkPtr.setDeleteStatus("0");
				dnsBulkPtr = dnsBulkPtrRepo.save(dnsBulkPtr);
				log.info("For registration no {} ptr update in ID {}",dnsBulkPtr.getRegistrationNo(),dnsBulkPtr.getId());
			}
			break;
		case "spf":
			Optional<DnsBulkSpf> dnsBulkSpfOptional = dnsBulkSpfRepo.findById(dnsData.getId());
			if (dnsBulkSpfOptional.isPresent()) {
				DnsBulkSpf dnsBulkSpf = dnsBulkSpfOptional.get();
				dnsBulkSpf.setDomain((dnsData.getDomain().isEmpty()) ? "N/A" : dnsData.getDomain());
				dnsBulkSpf.setSpf((dnsData.getSpfTxt().isEmpty()) ? "N/A" : dnsData.getSpfTxt());
				dnsBulkSpf.setLocation((dnsData.getServerLocation().isEmpty()) ? "N/A" : dnsData.getServerLocation());
				dnsBulkSpf.setMigrationDate((dnsData.getMigrationDate().toString().isEmpty()) ? "N/A"
						: dnsData.getMigrationDate().toString());
				dnsBulkSpf.setOldSpf((dnsData.getReq().equals("req_modify") && dnsData.getOldSpfTxt().isEmpty()) ? "N/A"
						: dnsData.getOldSpfTxt());
				dnsBulkSpf.setErrorStatus("0");
				dnsBulkSpf.setDeleteStatus("0");
				dnsBulkSpf = dnsBulkSpfRepo.save(dnsBulkSpf);
				log.info("For registration no {} spf update in ID {}",dnsBulkSpf.getRegistrationNo(),dnsBulkSpf.getId());
			}
			break;
		case "srv":
			Optional<DnsBulkSrv> dnsBulkSrvOptional = dnsBulkSrvRepo.findById(dnsData.getId());
			if (dnsBulkSrvOptional.isPresent()) {
				DnsBulkSrv dnsBulkSrv = dnsBulkSrvOptional.get();
				dnsBulkSrv.setDomain((dnsData.getDomain().isEmpty()) ? "N/A" : dnsData.getDomain());
				dnsBulkSrv.setSrv((dnsData.getSrvTxt().isEmpty()) ? "N/A" : dnsData.getSrvTxt());
				dnsBulkSrv.setLocation((dnsData.getServerLocation().isEmpty()) ? "N/A" : dnsData.getServerLocation());
				dnsBulkSrv.setMigrationDate((dnsData.getMigrationDate().toString().isEmpty()) ? "N/A"
						: dnsData.getMigrationDate().toString());
				dnsBulkSrv.setOldSrv((dnsData.getReq().equals("req_modify") && dnsData.getOldSrvTxt().isEmpty()) ? "N/A"
						: dnsData.getOldSrvTxt());
				dnsBulkSrv.setErrorStatus("0");
				dnsBulkSrv.setDeleteStatus("0");
				dnsBulkSrv = dnsBulkSrvRepo.save(dnsBulkSrv);
				log.info("For registration no {} srv update in ID {}",dnsBulkSrv.getRegistrationNo(),dnsBulkSrv.getId());
			}
			break;
		case "dmarc":
			Optional<DnsBulkDmarc> dnsBulkDmarcOptional = dnsBulkDmarcRepo.findById(dnsData.getId());
			if (dnsBulkDmarcOptional.isPresent()) {
				DnsBulkDmarc dnsBulkDmarc = dnsBulkDmarcOptional.get();
				dnsBulkDmarc.setDomain((dnsData.getDomain().isEmpty()) ? "N/A" : dnsData.getDomain());
				dnsBulkDmarc.setDmarc((dnsData.getDmarcTxt().isEmpty()) ? "N/A" : dnsData.getDmarcTxt());
				dnsBulkDmarc.setLocation((dnsData.getServerLocation().isEmpty()) ? "N/A" : dnsData.getServerLocation());
				dnsBulkDmarc.setMigrationDate((dnsData.getMigrationDate().toString().isEmpty()) ? "N/A"
						: dnsData.getMigrationDate().toString());
				dnsBulkDmarc.setOldDmarc(
						(dnsData.getReq().equals("req_modify") && dnsData.getOldDmarcTxt().isEmpty()) ? "N/A"
								: dnsData.getOldDmarcTxt());
				dnsBulkDmarc.setErrorStatus("0");
				dnsBulkDmarc.setDeleteStatus("0");
				dnsBulkDmarc = dnsBulkDmarcRepo.save(dnsBulkDmarc);
				log.info("For registration no {} dmarc update in ID {}",dnsBulkDmarc.getRegistrationNo(),dnsBulkDmarc.getId());
			}
			break;
		case "aaaa":
			Optional<DnsBulkUpload> dnsBulkUploadOptional = dnsBulkAaaaRepo.findById(dnsData.getId());
			if (dnsBulkUploadOptional.isPresent()) {
				DnsBulkUpload dnsBulkUpload = dnsBulkUploadOptional.get();
				dnsBulkUpload.setDomain((dnsData.getDomain().isEmpty()) ? "N/A" : dnsData.getDomain());
				dnsBulkUpload.setCname((dnsData.getCname().isEmpty()) ? "N/A" : dnsData.getCname());
				dnsBulkUpload
						.setLocation((dnsData.getServerLocation().isEmpty()) ? "N/A" : dnsData.getServerLocation());
				dnsBulkUpload.setMigrationDate((dnsData.getMigrationDate().toString().isEmpty()) ? "N/A"
						: dnsData.getMigrationDate().toString());
				dnsBulkUpload.setOldIp((dnsData.getReq().equals("req_modify") && dnsData.getOldIp().isEmpty()) ? "N/A"
						: dnsData.getOldIp());
				dnsBulkUpload.setNewIp((dnsData.getNewIp().isEmpty()) ? "N/A" : dnsData.getNewIp());
				dnsBulkUpload.setErrorStatus("0");
				dnsBulkUpload.setDeleteStatus("0");
				dnsBulkUpload = dnsBulkAaaaRepo.save(dnsBulkUpload);
				log.info("For registration no {} aaaa update in ID {}",dnsBulkUpload.getRegistrationNo(),dnsBulkUpload.getId());
			}
			break;
		}
	}

	public int updateRegNumberInBulkTable(Long campaignId, String recordType, String regNumber) {
		int i = -1;
		switch (recordType) {
		case "cname":
			i = dnsBulkCnameRepo.updateRegNumberByCampaignId(campaignId, regNumber);
			break;
		case "mx":
			i = dnsBulkMxRepo.updateRegNumberByCampaignId(campaignId, regNumber);
			break;
		case "txt":
			i = dnsBulkTxtRepo.updateRegNumberByCampaignId(campaignId, regNumber);
			break;
		case "ptr":
			i = dnsBulkPtrRepo.updateRegNumberByCampaignId(campaignId, regNumber);
			break;
		case "spf":
			i = dnsBulkSpfRepo.updateRegNumberByCampaignId(campaignId, regNumber);
			break;
		case "srv":
			i = dnsBulkSrvRepo.updateRegNumberByCampaignId(campaignId, regNumber);
			break;
		case "dmarc":
			i = dnsBulkDmarcRepo.updateRegNumberByCampaignId(campaignId, regNumber);
			break;
		case "aaaa":
			i = dnsBulkAaaaRepo.updateRegNumberByCampaignId(campaignId, regNumber);
			break;
		}
		return i;
	}
	
	public int updateRegNumberInCampaignTable(Long campaignId, String regNumber) {
		return dnsCampaignRepo.updateRegNumberByCampaignId(campaignId, regNumber);
	}


	public boolean doNslookup(String domain) {
		boolean flag = false;
		try {
			InetAddress yes = java.net.InetAddress.getByName(domain);
			if (!yes.getHostAddress().isEmpty()) {
				flag = true;
			}
		} catch (Exception ex) {
			System.out.println("Error in doNslookup :" + ex);
		}
		return flag;
	}

	public boolean isThirdLevelDomain(String domain) {
		int count_char = countChar(domain, '.');
		if (count_char == 3 && domain.startsWith("www.")) {
			return true;
		} else if (count_char < 3) {
			return true;
		} else {
			return false;
		}
	}

	public boolean doesIPbelongToNIC(String ip) {
		return ip.startsWith("164.100") || ip.startsWith("14.139") || ip.startsWith("180.149") || ip.startsWith("10.");
	}

	public boolean isApplicantOwner(String url, String applicantEmail) {
		Set<String> owners = fetchOwner(url);

		if (!applicantEmail.isEmpty()) {
			return owners.contains(applicantEmail);
		} else {
			return false;
		}
	}

	public boolean isValidIp(String ip) {
		if (ip != null) {
			if (!ip.isEmpty()) {
				if (!ip.matches(
						"((^\\s*((([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))\\s*$)|(^\\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))(%.+)?\\s*$))")) {
					if (ip.startsWith("0") || ip.equals("0.0.0.0") || ip.equals("127.0.0.1")
							|| ip.equals("255.255.255.255") || ip.endsWith("255")) {
						return true;
					} else {
						return false;
					}
				} else {
					return true;
				}
			} else {
				return true;
			}
		} else {
			return true;
		}
	}

	public boolean isGovDomain(String domain) {
		String[] a = domain.split(".");
		int lastIndex, secondLastIndex;
		lastIndex = domain.lastIndexOf('.');
		secondLastIndex = domain.lastIndexOf('.', lastIndex - 1);
		String d = domain.substring(secondLastIndex + 1, lastIndex) + domain.substring(lastIndex, domain.length());
		return d.equals("gov.in");
	}

	public String getDomainName(String ip) {
		String cmd = "dig -x " + ip + " +short";
		return digHostName(cmd);
	}

	public int countChar(String str, char c) {
		int count = 0;

		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == c) {
				count++;
			}
		}

		return count;
	}

	private String digHostName(String cmd) {
		String s = null;
		String hostString = "";
		boolean flag = false;
		try {
			Process p = Runtime.getRuntime().exec(cmd);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			// read the output from the command
			while ((s = stdInput.readLine()) != null) {
				flag = true;
				hostString += s + ",";
			}
			if (!flag) {
				hostString = "";
			}

			// read any errors from the attempted command
//			while ((s = stdError.readLine()) != null) {
//				System.out.println(s);
//			}
// 
		} catch (IOException e) {
			System.out.println("exception happened - here's what I know: ");
			s = "";
		}
		hostString = hostString.replaceAll("\\s*,\\s*$", "");
		return hostString;
	}

	public String checkDomain(String url) {
		String returnString = checkDnsEformsFinalMapping(url, "url");
		if (returnString.isEmpty()) {
			returnString = checkDnsDataFromDnsAdminFinalMapping(url, "url");
			if (returnString.isEmpty()) {
				try {
					InetAddress yes = java.net.InetAddress.getByName(url);
					returnString = yes.getHostAddress();
					System.out.println("ReturnString in fetch domain" + returnString);
				} catch (Exception ex) {
					returnString = "";
				}
			}
		} else if (returnString.equalsIgnoreCase("deleted")) {
			try {
				InetAddress yes = java.net.InetAddress.getByName(url);
				returnString = yes.getHostAddress();
				System.out.println("ReturnString in fetch domain" + returnString);
			} catch (Exception ex) {
				returnString = "";
			}
		}
		return returnString;
	}

	public String checkIP(String ip) {
		String returnString1 = "";
		String returnString = checkDnsEformsFinalMapping(ip, "ip");

		if (returnString.isEmpty()) {
			returnString = checkDnsDataFromDnsAdminFinalMapping(ip, "ip");
			if (returnString.isEmpty()) {
				returnString = getDomainName(ip);
				if (returnString.isEmpty()) {
					returnString = dbReverseDNSLookup(ip);
					if (isThirdLevelDomain(returnString) && !returnString.isEmpty()) {
					} else {
						returnString = "";
					}
				} else {
					if (!isThirdLevelDomain(returnString)) {
						returnString = "";
					}
				}
			}
		} else if (returnString.equalsIgnoreCase("deleted")) {
			returnString = getDomainName(ip);
			if (returnString.isEmpty()) {
				returnString = dbReverseDNSLookup(ip);
				if (isThirdLevelDomain(returnString) && !returnString.isEmpty()) {
				} else {
					returnString = "";
				}
			} else {
				if (!isThirdLevelDomain(returnString)) {
					returnString = "";
				}
			}
		} else {
			returnString1 = checkDnsEformsFinalMapping(returnString, "url");
			if (returnString1.equalsIgnoreCase("deleted")) {
				returnString = "";
			} else if (!returnString1.equalsIgnoreCase(ip)) {
				returnString = "";
			}
		}
		return returnString;
	}

	
	
}
