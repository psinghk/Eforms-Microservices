package in.nic.ashwini.eForms.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import in.nic.ashwini.eForms.models.PreviewFormBean;
import lombok.extern.slf4j.Slf4j;

@Service
public class ValidationService {
	// private static final String EMAIL_REGEX =
	// "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$";
	private static final String MINISTRY_DEPARTMENT_ORGANIZATION_REGEX = "^[a-zA-Z#0-9\\s,'.\\-\\/\\&\\_(\\)]{2,100}$";
	// private static final String EMPLOYMENT_CATEGORY_REGEX = "^[a-zA-Z0-9
	// .,-_&]{1,50}$";

	public Map<String, Object> validate(PreviewFormBean previewFormBean) {

		Map<String, Object> map = new HashMap<>();
		if (previewFormBean.getVpnFormDetails().isEmpty() || previewFormBean.getVpnFormDetails() == null) {
			System.out.println();
			map.put("Error", "form details empty");
			return map;
		}
		int formBeenSize = previewFormBean.getVpnFormDetails().size();
		for (int i = 0; i <= formBeenSize - 1; i++) {
			boolean serverIp = false;
			boolean ipRangeTo = false;
			boolean ipRangeFrom = false;
			System.out.println("get ip range to value = " + previewFormBean.getVpnFormDetails().get(i).getIpRangeTo());
			if (previewFormBean.getVpnFormDetails().get(i).getIpRangeTo() == null) {
				serverIp = isValidIp(previewFormBean.getVpnFormDetails().get(i).getServerIp());
			} else {
				ipRangeTo = isValidIp(previewFormBean.getVpnFormDetails().get(i).getIpRangeTo());
				ipRangeFrom = isValidIp(previewFormBean.getVpnFormDetails().get(i).getIpRangeFrom());
			}

			boolean applicationUrl = isValidUrl(previewFormBean.getVpnFormDetails().get(i).getApplicationUrl());
			boolean destinationPort = isValidPort(previewFormBean.getVpnFormDetails().get(i).getDestinationPort());
			boolean serverLocation = isValidLocation(previewFormBean.getVpnFormDetails().get(i).getServerLocation());
			boolean iptype = isValidIptype(previewFormBean.getVpnFormDetails().get(i).getIptype());

			if (previewFormBean.getVpnFormDetails().get(i).getIpRangeTo() == null) {
				if (!serverIp == true) {
					map.put("server_ip_error", "Please enter valid IP address");

				}
			} else {

				if (!ipRangeTo == true) {
					map.put("Ip_Range_To_error", "Please enter valid IP address");

				}
				if (!ipRangeFrom == true) {
					map.put("Ip_Range_From_error", "Please enter valid IP address");

				}

			}

			if (!applicationUrl == true) {
				map.put("application_url_error", "Please enter valid Application URL [e.g: (abc.com)]");

			}
			if (!destinationPort == true) {
				map.put("destination_port_error", "Please enter valid Destination Port [e.g: [80,443] OR [8080-8081]]");

			}
			if (!serverLocation == true) {
				map.put("server_location_error", "Enter Server Location is not Correct");

			}

			if (!iptype == true) {
				map.put("ip_type_error", "Please enter valid Application IP [e.g: 10.10.10.10]");

			}

		}
		if (map.size() == 0 || map.size() <= 0) {
			map.put("success", "success");
		}
		System.out.println("Map size is = " + map.size());
		return map;
	}

	public Map<String, Object> validateRequest(PreviewFormBean previewFormBean) {

		Map<String, Object> map = new HashMap<>();
		if (previewFormBean.getVpnFormDetails().isEmpty() || previewFormBean.getVpnFormDetails() == null) {
			System.out.println();
			map.put("Error", "form details empty");
			return map;
		}
		int formBeenSize = previewFormBean.getVpnFormDetails().size();
		for (int i = 0; i <= formBeenSize - 1; i++) {
			boolean serverIp = false;
			boolean ipRangeTo = false;
			boolean ipRangeFrom = false;
			System.out.println("get ip range to value = " + previewFormBean.getVpnFormDetails().get(i).getIpRangeTo());
			if (previewFormBean.getVpnFormDetails().get(i).getIpRangeTo() == null) {
				serverIp = isValidIp(previewFormBean.getVpnFormDetails().get(i).getServerIp());
			} else {
				ipRangeTo = isValidIp(previewFormBean.getVpnFormDetails().get(i).getIpRangeTo());
				ipRangeFrom = isValidIp(previewFormBean.getVpnFormDetails().get(i).getIpRangeFrom());
			}

			boolean applicationUrl = isValidUrl(previewFormBean.getVpnFormDetails().get(i).getApplicationUrl());
			boolean destinationPort = isValidPort(previewFormBean.getVpnFormDetails().get(i).getDestinationPort());
			boolean serverLocation = isValidLocation(previewFormBean.getVpnFormDetails().get(i).getServerLocation());
			boolean iptype = isValidIptype(previewFormBean.getVpnFormDetails().get(i).getIptype());

			if (previewFormBean.getVpnFormDetails().get(i).getIpRangeTo() == null) {
				if (!serverIp == true) {
					map.put("server_ip_error", "Please enter valid IP address");

				}
			} else {

				if (!ipRangeTo == true) {
					map.put("Ip_Range_To_error", "Please enter valid IP address");

				}
				if (!ipRangeFrom == true) {
					map.put("Ip_Range_From_error", "Please enter valid IP address");

				}

			}

			if (!applicationUrl == true) {
				map.put("application_url_error", "Please enter valid Application URL [e.g: (abc.com)]");

			}
			if (!destinationPort == true) {
				map.put("destination_port_error", "Please enter valid Destination Port [e.g: [80,443] OR [8080-8081]]");

			}
			if (!serverLocation == true) {
				map.put("server_location_error", "Enter Server Location is not Correct");

			}

			if (!iptype == true) {
				map.put("ip_type_error", "Please enter valid Application IP [e.g: 10.10.10.10]");

			}

		}
		if (map.size() == 0 || map.size() <= 0) {
			map.put("success", "success");
		}
		System.out.println("Map size is = " + map.size());
		return map;
	}

	public boolean isValidDepartment(String department) {
		boolean response;
		if (department == null)
			response = false;
		if (department.isEmpty())
			response = false;
		if (department.matches(MINISTRY_DEPARTMENT_ORGANIZATION_REGEX)) {
			response = true;
		} else {
			response = false;
		}
		return response;
	}

	public boolean isValidIp(String ip) {

		if (ip == null)
			return false;
		if (ip.isEmpty())
			return false;
		if (!ip.matches(
				"((^\\s*((([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))\\s*$)|(^\\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)"
						+ "(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))(%.+)?\\s*$))")) {
			if (ip.startsWith("0") || ip.equals("0.0.0.0") || ip.equals("127.0.0.1") || ip.equals("255.255.255.255")
					|| ip.endsWith("255")) {
				return true;
			} else {
				return false;
			}
		}
		return true;

	}

	public boolean isValidUrl(String url) {

		boolean flag = false;
		if (!url.isEmpty()) {
			if (url.matches(
					"^(?:(?:(?:https?|ftp):)?\\/\\/)(?:\\S+(?::\\S*)?@)?(?:(?!(?:10|127)(?:\\.\\d{1,3}){3})(?!(?:169\\.254|192\\.168)(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)(?:\\.(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)*(?:\\.(?:[a-z\\u00a1-\\uffff]{2,})).?)(?::\\d{2,5})?(?:[/?#]\\S*)?$")) {
				flag = true;
			} else if (url.matches(
					"^((http:\\/\\/|https:\\/\\/)([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\/[a-z]{1,})*$")) {
				String ip = url.substring(url.indexOf("//") + 2, url.length());
				if (ip.startsWith("0") || ip.equals("0.0.0.0") || ip.equals("127.0.0.1") || ip.equals("255.255.255.255")
						|| ip.endsWith("255")) {
					flag = true;
				}
			}
		} else {
			// if it is empty
			flag = true;
		}
		return flag;

	}

	public boolean isValidPort(String port) {
		boolean flag = false;
		if (port.contains(",")) {
			String[] arr = port.split(",");
			for (String prt : arr) {

				if (prt.isEmpty()) {
					flag = false;
				} else if (prt.matches("^[0-9]{1,10}$")) {
					flag = true;
				}
			}

		}

		else {
			if (port.isEmpty()) {
				flag = false;
			} else if (port.matches("^[0-9]{1,10}$")) {
				flag = true;
			}
		}
		return flag;

	}

	public boolean isValidLocation(String location) {
		boolean flag = false;
		if (location.isEmpty()) {
			flag = false;
		} else if (location.matches("^[a-zA-Z#0-9\\s,.\\-\\/\\(\\)]{2,100}$")) {
			flag = true;
		} else {
			flag = false;
		}
		System.out.println("Location flage ::::: = " + flag);
		return flag;

	}

	public boolean isValidIptype(String iptype) {
		boolean flag = false;
		if (!iptype.isEmpty()) {
			flag = true;
		}
		System.out.println("Location flage ::::: = " + flag);
		return flag;

	}

	public boolean isOrgValid(String iptype) {
		boolean flag = false;
		if (!iptype.isEmpty()) {
			flag = true;
		}
		System.out.println("Location flage ::::: = " + flag);
		return flag;

	}
}
