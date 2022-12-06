package in.nic.ashwini.eForms.services;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.nic.ashwini.eForms.entities.GeneratePdf;
import in.nic.ashwini.eForms.entities.VpnBase;
import in.nic.ashwini.eForms.entities.VpnEntryBase;
import in.nic.ashwini.eForms.models.FinalAuditTrack;
import in.nic.ashwini.eForms.models.FormData;
import in.nic.ashwini.eForms.models.GeneratePdfBean;
import in.nic.ashwini.eForms.models.PreviewFormBean;
import in.nic.ashwini.eForms.models.Status;
import in.nic.ashwini.eForms.repositories.GeneratePdfRepository;
import in.nic.ashwini.eForms.repositories.VpnBaseRepo;
import in.nic.ashwini.eForms.repositories.VpnEntryBaseRepo;
import in.nic.ashwini.eForms.utils.Util;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import sun.net.www.protocol.http.HttpURLConnection;

@Service
public class VpnService {
	@Value("${fileBasePath}")
	private String EXTERNAL_FILE_PATH;
	@Value("${fileBasePath}")
	private String fileBasePath;

	@Value("${url}")
	private String url;

	@Value("${user.credentials}")
	private String UserCredentials;

	private final GeneratePdfRepository generatePdfRepository;
	private final VpnBaseRepo vpnBaseRepo;
	private final Util utilityService;
	private final VpnEntryBaseRepo vpnEntryBaseRepo;
	@Autowired
	RestTemplate restTemplate;

	@Autowired
	public VpnService(GeneratePdfRepository generatePdfRepository, VpnBaseRepo vpnBaseRepo, Util utilityService,
			VpnEntryBaseRepo vpnEntryBaseRepo) {
		super();
		this.generatePdfRepository = generatePdfRepository;
		this.vpnBaseRepo = vpnBaseRepo;
		this.utilityService = utilityService;
		this.vpnEntryBaseRepo = vpnEntryBaseRepo;

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
			File file = ResourceUtils.getFile("classpath:vpnreport.jrxml");
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

	public VpnBase preview(String regNo) {
		return vpnBaseRepo.findByRegistrationNo(regNo);
	}

	public List<VpnEntryBase> previewFormDetails(String regNo) {
		return vpnEntryBaseRepo.findByRegistrationNo(regNo);
	}

	@Transactional
	public VpnBase insert(VpnBase vpnBase) {
		if (vpnBase != null) {
			Date date = new Date();
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			String currentDate = dateFormat.format(date);
			String oldRegNumber = vpnBaseRepo.findLatestRegistrationNo();
			String newRegNumber = "VPNADD-FORM" + currentDate;
			if (oldRegNumber == null || oldRegNumber.isEmpty()) {
				newRegNumber += "0001";
			} else {
				int newRegistrationNo = Integer.parseInt(oldRegNumber.substring(21, oldRegNumber.length())) + 1;
				int length = Integer.toString(newRegistrationNo).length();
				if (length == 1) {
					newRegNumber += "000" + newRegistrationNo;
				} else if (length == 2) {
					newRegNumber += "00" + newRegistrationNo;
				} else if (length == 3) {
					newRegNumber += "0" + newRegistrationNo;
				}
			}
			vpnBase.setRegistrationNo(newRegNumber);
			vpnBase.setSupportActionTaken("p");
			return vpnBaseRepo.save(vpnBase);
		}
		return null;
	}

	public boolean updatePreviewDetails(String regNumber, PreviewFormBean previewFormBean) {
		VpnBase vpnBase = vpnBaseRepo.findByRegistrationNo(regNumber);
		if (vpnBase != null) {
			try {
				BeanUtils.copyProperties(previewFormBean, vpnBase);
				VpnBase vpnUpdated = vpnBaseRepo.save(vpnBase);
				if (vpnUpdated.getId() > 0) {
					return true;
				}
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	public boolean updateStatusTable(String regNumber, String currentRole, PreviewFormBean previewFormBean) {
		VpnBase vpnBase = vpnBaseRepo.findByRegistrationNo(regNumber);
		if (vpnBase != null) {
			try {
				BeanUtils.copyProperties(previewFormBean, vpnBase);
				VpnBase vpnUpdated = vpnBaseRepo.save(vpnBase);
				if (vpnUpdated.getId() > 0) {
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

	@Transactional
	public String insertIntoEntry(String regno, List<FormData> value, String req_for) {
		try {
			for (int i = 0; i < value.size(); i++) {
				String ipType = "", ip1 = "", serverLoc = "", destPort = "", appUrl = "", ipFrom = "";
				ipType = value.get(i).getIptype();
				if (value.get(i).getIpRangeTo() == null) {
					ip1 = value.get(i).getServerIp();
				} else {
					ip1 = value.get(i).getIpRangeTo();
				}

				serverLoc = value.get(i).getServerLocation();
				destPort = value.get(i).getDestinationPort();
				appUrl = value.get(i).getApplicationUrl();
				ipFrom = value.get(i).getIpRangeFrom();
				VpnEntryBase vpnEntries = new VpnEntryBase();
				vpnEntries.setRegistrationNo(regno);
				// Ip1 or ipTo are same
				vpnEntries.setServerIp(ip1);
				vpnEntries.setDestinationPort(destPort);
				vpnEntries.setApplicationUrl(appUrl);
				vpnEntries.setIptype(ipType);
				vpnEntries.setServerLocation(serverLoc);
				if (req_for.equalsIgnoreCase("change_mod")) {
					vpnEntries.setActionType("Delete");
				} else {
					vpnEntries.setActionType("Add");
				}
				vpnEntries.setIpRangeFrom(ipFrom);

				vpnEntryBaseRepo.save(vpnEntries);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "success";
	}

	/*
	 * @Transactional public String insertIntoEntr(String regno, List<FormData>
	 * value,String req_for) { try { for (int i = 0; i < value.size(); i++) { String
	 * ipType = "", ip1 = "", serverLoc = "", destPort = "", appUrl = "", ipFrom =
	 * ""; ipType = value.get(i).getIptype(); ip1 = value.get(i).getServerIp();
	 * serverLoc = value.get(i).getServerLocation(); destPort =
	 * value.get(i).getDestinationPort(); appUrl = value.get(i).getApplicationUrl();
	 * ipFrom = value.get(i).getIpRangeFrom(); VpnEntryBase vpnEntries = new
	 * VpnEntryBase(); vpnEntries.setRegistrationNo(regno); // Ip1 or ipTo are same
	 * vpnEntries.setServerIp(ip1); vpnEntries.setDestinationPort(destPort);
	 * vpnEntries.setApplicationUrl(appUrl); vpnEntries.setIptype(ipType); /*
	 * vpnEntries.setIp1(ip1); vpnEntries.setDestPort(destPort);
	 * vpnEntries.setAppUrl(appUrl); vpnEntries.setIpType(ipType);
	 */

	/*
	 * vpnEntries.setServerLocation(serverLoc);
	 * if(req_for.equalsIgnoreCase("change_mod")) {
	 * vpnEntries.setActionType("Delete"); }else { vpnEntries.setActionType("Add");
	 * } vpnEntries.setIpRangeFrom(ipFrom);
	 * 
	 * vpnEntryBaseRepo.save(vpnEntries);
	 * 
	 * } } catch (Exception e) { e.printStackTrace(); } return "success"; }
	 * 
	 */
	public String fetchAliases(String uemail) {
		return utilityService.fetchAliases(uemail);
	}

	public List<Object> getVpnNumbers(String email, String mobile) throws MalformedURLException, IOException {
		List<Object> vpn = new ArrayList<>();
		String aliasesData = fetchAliases(email);
		URL obj = new URL(url + "exist");
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		String userCredentials = UserCredentials;
		byte[] plainCredsBytes = userCredentials.getBytes();
		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
		String base64Creds = new String(base64CredsBytes);
		String basicAuth = "Basic " + new String(new Base64().encode(userCredentials.getBytes()));
		con.setRequestProperty("Authorization", "Basic ZWZvcm1zOmVmb3Jtc0AjYXBpNzgk");
		con.setRequestProperty("Content-Type", "application/json");
		String email_to_api = aliasesData.toString();
		String urlParameters = "{ \"email\": [" + email_to_api + "], \"mobile\": \"" + mobile + "\" }";
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode actualObj = mapper.readTree(inputLine);
			JsonNode vpnNumberJson = actualObj;
			vpn.add(vpnNumberJson);
		}
		in.close();
		return vpn;
	}

	public String getVpnDetails(String vpn_no, String email, String mobile) throws MalformedURLException, IOException {

		String aliasesData = fetchAliases(email);
		URL obj = new URL(url + "fetch");
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		String userCredentials = UserCredentials;
		byte[] plainCredsBytes = userCredentials.getBytes();
		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
		String base64Creds = new String(base64CredsBytes);
		String basicAuth = "Basic " + new String(new Base64().encode(userCredentials.getBytes()));
		con.setRequestProperty("Authorization", "Basic ZWZvcm1zOmVmb3Jtc0AjYXBpNzgk");
		con.setRequestProperty("Content-Type", "application/json");
		String email_to_api = aliasesData.replaceAll("'", "\"");
		String urlParameters = "{ \"vpn_registration_no\": \"" + vpn_no + "\", \"email\": [" + email_to_api
				+ "], \"mobile\": \"" + mobile + "\" }";
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			System.out.println("jsone data formate data = " + inputLine);
			response.append(inputLine);
		}
		in.close();
		return response.toString();

	}

	public boolean updatevpnbase(VpnBase vpnBase) {
		VpnBase vpndetails = vpnBaseRepo.save(vpnBase);
		if (vpndetails.getId() > 0) {
			return true;
		} else {
			return false;
		}
	}

}
