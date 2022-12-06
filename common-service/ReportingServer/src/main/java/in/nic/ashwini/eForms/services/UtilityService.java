package in.nic.ashwini.eForms.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import in.nic.ashwini.eForms.config.ConfigProperties;
import in.nic.ashwini.eForms.models.CustomMessage;
import in.nic.ashwini.eForms.utils.Constants;

@Service
public class UtilityService {

	@LoadBalanced
	private final RestTemplate restTemplate;
	
	private final ConfigProperties configProperties;

	@Autowired
	public UtilityService(RestTemplate restTemplate, ConfigProperties configProperties) {
		super();
		this.restTemplate = restTemplate;
		restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
			protected boolean hasError(HttpStatus statusCode) {
				return false;
			}
		});
		this.configProperties = configProperties;
	}

	public List<String> aliases(String email) {
		if (isGovEmployee(email)) {
			String uri = configProperties.getLdapUrl() + "/fetchAliasesAlongWithPrimary?mail=" + email;
			ResponseEntity<List<String>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<List<String>>() {
					});
			return response.getBody();
		} else {
			return Arrays.asList(email);
		}
	}

	public String fetchService(String regNumber) {
		String uri = configProperties.getAdminUrl() + "/fetchServiceName?regNumber=" + regNumber;
		return restTemplate.getForObject(uri, String.class);
	}

	public Boolean isGovEmployee(String mail) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> entity = new HttpEntity<>(headers);
		String urlToAuthenticate = configProperties.getLdapUrl() + "/isEmailAvailable";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlToAuthenticate).queryParam("mail", mail);
		HttpEntity<Boolean> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity,
				Boolean.class);
		return response.getBody();
	}

	public List<String> fetchListOfServices(List<String> services) {
		List<String> service_list = new ArrayList<>();
		for (String servicesString : services) {
			if (servicesString.equalsIgnoreCase(Constants.EMAIL_REGISTRATION)) {
				service_list.add("bulk");
				service_list.add("single");
				service_list.add("gem");
				service_list.add("nkn_bulk");
				service_list.add("nkn_single");
				service_list.add("email_act");
				service_list.add("email_deact");
			} else if (servicesString.equalsIgnoreCase(Constants.VPN_REGISTRATION)) {
				service_list.add("vpn_single");
				service_list.add("vpn_delete");
				service_list.add("vpn_surrender");
				service_list.add("change_add");
				service_list.add("vpn_renew");
			} else if (servicesString.equalsIgnoreCase(Constants.SMS_REGISTRATION)) {
				service_list.add("sms");
			} else if (servicesString.equalsIgnoreCase(Constants.DNS_REGISTRATION)) {
				service_list.add("dns");
			} else if (servicesString.equalsIgnoreCase(Constants.WIFI_REGISTRATION)) {
				service_list.add("wifi");
			} else if (servicesString.equalsIgnoreCase(Constants.DISTRIBUTIONLIST_REGISTRATION)) {
				service_list.add("dlist");
			} else if (servicesString.equalsIgnoreCase(Constants.IMAPPOP_REGISTRATION)) {
				service_list.add("imappop");
			} else if (servicesString.equalsIgnoreCase(Constants.LDAP_REGISTRATION)) {
				service_list.add("ldap");
			} else if (servicesString.equalsIgnoreCase(Constants.SMTP_REGISTRATION)) {
				service_list.add("relay");
			} else if (servicesString.equalsIgnoreCase(Constants.UPDATEMOBILE_REGISTRATION)) {
				service_list.add("mobile");
			} else if (servicesString.equalsIgnoreCase(Constants.WEBCAST_REGISTRATION)) {
				service_list.add("webcast");
			} else if (servicesString.equalsIgnoreCase(Constants.CENTRALUTM_REGISTRATION)) {
				service_list.add("centralutm");
			}
		}
		return service_list;
	}

	public List<String> fetchListOfStatus(List<String> status, String role) {
		List<String> status_list = new ArrayList<>();

		switch (role) {
		case "user":
			for (String statusString : status) {
				if (statusString.equalsIgnoreCase(Constants.PENDING_REQUESTS)) {
					status_list.add("ca_pending");
					status_list.add("coordinator_pending");
					status_list.add("mail-admin_pending");
					status_list.add("da_pending");
					status_list.add("support_pending");
					status_list.add("us_pending");
					status_list.add("manual");
					status_list.add("manual_upload");
					status_list.add("api");
					status_list.add("domainapi");
				} else if (statusString.equalsIgnoreCase(Constants.FORWARDED_REQUESTS)) {
					status_list.add("ca_pending");
					status_list.add("coordinator_pending");
					status_list.add("mail-admin_pending");
					status_list.add("da_pending");
					status_list.add("support_pending");
					status_list.add("us_pending");
					status_list.add("completed");
				} else if (statusString.equalsIgnoreCase(Constants.COMPLETED_REQUESTS)) {
					status_list.add("completed");
				} else if (statusString.equalsIgnoreCase(Constants.CANCELLED_REQUESTS)) {
					status_list.add("cancel");
				} else if (statusString.equalsIgnoreCase(Constants.REJECTED_REQUESTS)) {
					status_list.add("cancel");
					status_list.add("ca_rejected");
					status_list.add("support_rejected");
					status_list.add("coordinator_rejected");
					status_list.add("mail-admin_rejected");
					status_list.add("da_rejected");
					status_list.add("us_rejected");
				}
			}
			break;
		case "ro":
			for (String statusString : status) {
				if (statusString.equalsIgnoreCase(Constants.PENDING_REQUESTS)) {
					status_list.add("ca_pending");
				} else if (statusString.equalsIgnoreCase(Constants.FORWARDED_REQUESTS)) {
					status_list.add("coordinator_pending");
					status_list.add("mail-admin_pending");
					status_list.add("da_pending");
					status_list.add("support_pending");
					status_list.add("us_pending");
					status_list.add("completed");
					status_list.add("support_rejected");
					status_list.add("coordinator_rejected");
					status_list.add("mail-admin_rejected");
					status_list.add("da_rejected");
					status_list.add("us_rejected");
				} else if (statusString.equalsIgnoreCase(Constants.COMPLETED_REQUESTS)) {
					status_list.add("completed");
				} else if (statusString.equalsIgnoreCase(Constants.REJECTED_REQUESTS)) {
					status_list.add("ca_rejected");
					status_list.add("support_rejected");
					status_list.add("coordinator_rejected");
					status_list.add("mail-admin_rejected");
					status_list.add("da_rejected");
					status_list.add("us_rejected");
				}
			}
			break;
		case "coordinator":
			for (String statusString : status) {
				if (statusString.equalsIgnoreCase(Constants.PENDING_REQUESTS)) {
					status_list.add("coordinator_pending");
				} else if (statusString.equalsIgnoreCase(Constants.FORWARDED_REQUESTS)) {
					status_list.add("mail-admin_pending");
					status_list.add("da_pending");
					status_list.add("completed");
					status_list.add("mail-admin_rejected");
					status_list.add("da_rejected");
				} else if (statusString.equalsIgnoreCase(Constants.COMPLETED_REQUESTS)) {
					status_list.add("completed");
				} else if (statusString.equalsIgnoreCase(Constants.REJECTED_REQUESTS)) {
					status_list.add("coordinator_rejected");
					status_list.add("mail-admin_rejected");
					status_list.add("da_rejected");
				}
			}
			break;
		case "support":
			for (String statusString : status) {
				if (statusString.equalsIgnoreCase(Constants.PENDING_REQUESTS)) {
					status_list.add("support_pending");
				} else if (statusString.equalsIgnoreCase(Constants.FORWARDED_REQUESTS)) {
					status_list.add("coordinator_pending");
					status_list.add("mail-admin_pending");
					status_list.add("da_pending");
					status_list.add("completed");
					status_list.add("coordinator_rejected");
					status_list.add("mail-admin_rejected");
					status_list.add("da_rejected");
				} else if (statusString.equalsIgnoreCase(Constants.COMPLETED_REQUESTS)) {
					status_list.add("completed");
				} else if (statusString.equalsIgnoreCase(Constants.REJECTED_REQUESTS)) {
					status_list.add("support_rejected");
					status_list.add("coordinator_rejected");
					status_list.add("mail-admin_rejected");
					status_list.add("da_rejected");
				}
			}
			break;
		case "admin":
			for (String statusString : status) {
				if (statusString.equalsIgnoreCase(Constants.PENDING_REQUESTS)) {
					status_list.add("support_pending");
				} else if (statusString.equalsIgnoreCase(Constants.FORWARDED_REQUESTS)) {
					status_list.add("completed");
				} else if (statusString.equalsIgnoreCase(Constants.COMPLETED_REQUESTS)) {
					status_list.add("completed");
				} else if (statusString.equalsIgnoreCase(Constants.REJECTED_REQUESTS)) {
					status_list.add("mail-admin_rejected");
				}
			}
			break;
		default:
			break;
		}
		return status_list;
	}
	
	public File createWorkbookHash(List<String> headerList, List<String[]> dataList, String excelFileName, String sheetName) throws IOException {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(sheetName);
		FileOutputStream fileOut = new FileOutputStream(excelFileName);
		if (dataList != null && !dataList.isEmpty()) {
			HSSFRow rowhead = sheet.createRow(0);
			// creating cell by using the createCell() method and setting the
			// values to the cell by using the setCellValue() method

			int i = 0;
			for (String header : headerList) {
				rowhead.createCell(i).setCellValue(header);
				i++;
			}
			i = 1;
			for (String[] auditTrack : dataList) {
				HSSFRow row = sheet.createRow(i);
				row.createCell(0).setCellValue(auditTrack[0]);
				row.createCell(1).setCellValue(auditTrack[1]);
				row.createCell(2).setCellValue(auditTrack[2]);
				row.createCell(3).setCellValue(auditTrack[3]);
				row.createCell(4).setCellValue(auditTrack[4]);
				row.createCell(5).setCellValue(auditTrack[5]);
				row.createCell(6).setCellValue(auditTrack[6]);
				row.createCell(7).setCellValue(auditTrack[7]);
				i++;
			}
		} else {
			HSSFRow rowhead = sheet.createRow(0);
			rowhead.createCell(0).setCellValue("No Record Found!!! ");
		}

		wb.write(fileOut);
		File file = new File(excelFileName);
		fileOut.flush();
		fileOut.close();
		return file;
	}
	
	public String sendEmailSms(CustomMessage message) {
		String uri = configProperties.getNotifyUrl()+"/sendEmailOrSms";
		HttpHeaders headers = new HttpHeaders();
		//headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<CustomMessage> entity = new HttpEntity<>(message, headers);
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity,
				String.class);
		return response.getBody();
	}
}
