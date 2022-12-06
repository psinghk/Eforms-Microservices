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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;

import in.nic.ashwini.eForms.entities.GeneratePdf;
import in.nic.ashwini.eForms.entities.WifiBase;
import in.nic.ashwini.eForms.entities.WifiMacOs;
import in.nic.ashwini.eForms.models.FinalAuditTrack;
import in.nic.ashwini.eForms.models.FormData;
import in.nic.ashwini.eForms.models.GeneratePdfBean;
import in.nic.ashwini.eForms.models.PreviewFormBean;
import in.nic.ashwini.eForms.models.Status;
import in.nic.ashwini.eForms.repositories.GeneratePdfRepository;
import in.nic.ashwini.eForms.repositories.WifiBaseRepo;
import in.nic.ashwini.eForms.repositories.WifiMacOsRepo;
import in.nic.ashwini.eForms.utils.Util;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@Service
public class WifiService {
	@Value("${fileBasePath}")
	private String EXTERNAL_FILE_PATH;
	@Value("${fileBasePath}")
	private String fileBasePath;

	private final GeneratePdfRepository generatePdfRepository;
	private final WifiBaseRepo wifiBaseRepo;
	private final WifiMacOsRepo wifiMacOsRepo;
	private final Util utilityService;

	@Autowired

	public WifiService(GeneratePdfRepository generatePdfRepository, WifiBaseRepo wifiBaseRepo,
			WifiMacOsRepo wifiMacOsRepo, Util utilityService) {
		super();
		this.generatePdfRepository = generatePdfRepository;
		this.wifiBaseRepo = wifiBaseRepo;
		this.utilityService = utilityService;
		this.wifiMacOsRepo = wifiMacOsRepo;

	}

	public JasperPrint generateFormPdf(String regid)
			throws JRException, IOException, IllegalAccessException, InvocationTargetException {
		JasperPrint jasperPrint = null;
		Optional<GeneratePdf> formDetails = generatePdfRepository.findByRegistrationNo(regid);
		List<?> objArr = wifiMacOsRepo.findByRegistrationNo(regid);
		// System.out.println("objArr"+objArr);
		// WifiMacOs wifimacos = wifiMacOsRepo.findByRegistrationNo(regid);
		GeneratePdf generatePdfDTO = null;
		if (formDetails.isPresent()) {
			generatePdfDTO = formDetails.orElse(null);
			GeneratePdfBean generatePdfBean = new GeneratePdfBean();
			org.springframework.beans.BeanUtils.copyProperties(generatePdfDTO, generatePdfBean);
			// GeneratePdfMacOsBean generatePdfmacosBean = new GeneratePdfMacOsBean();
			// org.springframework.beans.BeanUtils.copyProperties(generatePdfMacOsDTO,
			// generatePdfmacosBean);
			File file = ResourceUtils.getFile("classpath:wifireport.jrxml");
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
			parameters.put("MacOperating", objArr);
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

	public List<String> countOfMacWithThisUser(List<String> aliases) {
		return wifiBaseRepo.countOfMacWithThisUser(aliases);
	}

	public List<String> countOfUserWithThisMac(String macAddress) {
		return wifiBaseRepo.countOfUserWithThisMac(macAddress);
	}

	public WifiBase preview(String regNo) {
		return wifiBaseRepo.findByRegistrationNo(regNo);
	}

	public List previewMacOs(String regNo) {
		return wifiMacOsRepo.findByRegistrationNo(regNo);
	}

	public List<FormData> fetchWifiDataForApplicant(List<String> aliases) {
		List<Object[]> objArr = wifiBaseRepo.fetchWifiDataForApplicant(aliases);
		List<FormData> formDataList = new ArrayList<>();
		for (Object[] objects : objArr) {
			FormData formData = new FormData();
			formData.setMachineAddress((String) objects[0]);
			formData.setOperatingSystem((String) objects[1]);
			formData.setDeviceType((String) objects[2]);
			formDataList.add(formData);
		}
		return formDataList;
	}

	public List<FormData> fetchCompleteWifiDataForApplicant(List<String> aliases) {
		List<Object[]> objArr = wifiBaseRepo.fetchCompleteWifiDataForApplicant(aliases);
		List<FormData> formDataList = new ArrayList<>();
		for (Object[] objects : objArr) {
			FormData formData = new FormData();
			formData.setMachineAddress((String) objects[0]);
			formData.setOperatingSystem((String) objects[1]);
			formData.setDeviceType((String) objects[2]);
			formDataList.add(formData);
		}
		return formDataList;
	}

	public List<FormData> fetchPendingWifiDataForApplicant(List<String> aliases) {
		List<Object[]> objArr = wifiBaseRepo.fetchPendingWifiDataForApplicant(aliases);
		List<FormData> formDataList = new ArrayList<>();
		for (Object[] objects : objArr) {
			FormData formData = new FormData();
			formData.setMachineAddress((String) objects[0]);
			formData.setOperatingSystem((String) objects[1]);
			formData.setDeviceType((String) objects[2]);
			formDataList.add(formData);
		}
		return formDataList;
	}

	@Transactional
	public WifiBase insert(WifiBase wifiBase) {
		if (wifiBase != null) {
			Date date = new Date();
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			String currentDate = dateFormat.format(date);
			String oldRegNumber = wifiBaseRepo.findLatestRegistrationNo();
			String newRegNumber = "WIFI-FORM" + currentDate;
			if (oldRegNumber == null || oldRegNumber.isEmpty()) {
				newRegNumber += "0001";
			} else {
				int newRegistrationNo = Integer.parseInt(oldRegNumber.substring(20, oldRegNumber.length())) + 1;
				int length = Integer.toString(newRegistrationNo).length();
				if (length == 1) {
					newRegNumber += "000" + newRegistrationNo;
				} else if (length == 2) {
					newRegNumber += "00" + newRegistrationNo;
				} else if (length == 3) {
					newRegNumber += "0" + newRegistrationNo;
				}
			}
			wifiBase.setRegistrationNo(newRegNumber);
			wifiBase.setSupportActionTaken("p");
			return wifiBaseRepo.save(wifiBase);
		}
		return null;
	}

	public boolean updatePreviewDetails(String regNumber, PreviewFormBean previewFormBean) {
		WifiBase wifiBase = wifiBaseRepo.findByRegistrationNo(regNumber);
		if (wifiBase != null) {
			try {
				BeanUtils.copyProperties(previewFormBean, wifiBase);
				// imapPopBase.setLastUpdationDateTime(currentTime);
				List<FormData> formData = previewFormBean.getWifiFormDetails();
				for (FormData formData2 : formData) {
					WifiMacOs wifiFormData = new WifiMacOs();
					Optional<WifiMacOs> wifiFormDataOptional = wifiMacOsRepo
							.findById(Long.parseLong(formData2.getId()));
					if (wifiFormDataOptional.isPresent()) {
						wifiFormData = wifiFormDataOptional.get();
						wifiFormData.setRegistrationNo(regNumber);
						wifiFormData.setMachineAddress(formData2.getMachineAddress());
						wifiFormData.setOperatingSystem(formData2.getOperatingSystem());
						wifiFormData.setDeviceType(formData2.getDeviceType());
						wifiFormData.setStatus("");
						wifiMacOsRepo.save(wifiFormData);
					}
				}

				WifiBase wifiUpdated = wifiBaseRepo.save(wifiBase);
				if (wifiUpdated.getId() > 0) {
					return true;
				}
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	public boolean updateStatusTable(String regNumber, String currentRole, PreviewFormBean previewFormBean) {
		WifiBase wifiBase = wifiBaseRepo.findByRegistrationNo(regNumber);
		if (wifiBase != null) {
			try {
				BeanUtils.copyProperties(previewFormBean, wifiBase);
				WifiBase wifiUpdated = wifiBaseRepo.save(wifiBase);
				if (wifiUpdated.getId() > 0) {
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

	public boolean isRequestForDelete(String registrationNo) {
		return wifiBaseRepo.isRequestForDelete(registrationNo);
	}

	public boolean updateDeleteWifi(String registrationNo) {
		return wifiMacOsRepo.updateWifiValue(registrationNo);
	}

	@Transactional
	public boolean insertIntoEntry(String registrationNo, List<FormData> formdata) {
		int count = 0;
		for (int i = 0; i < formdata.size(); i++) {
			WifiMacOs wifiMacOs = new WifiMacOs();
			if (StringUtils.isNotBlank(formdata.get(i).getMachineAddress())
					&& StringUtils.isNotBlank(formdata.get(i).getOperatingSystem())
					&& StringUtils.isNotBlank(formdata.get(i).getDeviceType())) {

				wifiMacOs.setMachineAddress(formdata.get(i).getMachineAddress());
				wifiMacOs.setOperatingSystem(formdata.get(i).getOperatingSystem());
				wifiMacOs.setDeviceType(formdata.get(i).getDeviceType());
				wifiMacOs.setRegistrationNo(registrationNo);
				wifiMacOsRepo.save(wifiMacOs);
			}
			count++;
		}
		if (count > 0)
			return true;

		return false;

	}
	
	public boolean updatewifibase(WifiBase wifibase) {
		WifiBase wifidetails = wifiBaseRepo.save(wifibase);
		if (wifidetails.getId() > 0) {
			return true;
		} else {
			return false;
		}
	}

}