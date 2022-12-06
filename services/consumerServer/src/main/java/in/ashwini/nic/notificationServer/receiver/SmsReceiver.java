package in.ashwini.nic.notificationServer.receiver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import in.ashwini.nic.notificationServer.config.SmsProperties;
import in.ashwini.nic.notificationServer.model.CustomMessage;
import in.ashwini.nic.notificationServer.model.FinalCustomMessage;

@Component
public class SmsReceiver {

	@Autowired
	SmsProperties smsProperties;

	public void receiveMessage(CustomMessage message) {
		System.out.println("Received by SMS consumer<" + message.toString() + ">");

//		if (smsProperties.getIsSmsEnabled()) {
//			if (!message.getMobile().isEmpty()) {
//				String msg = URLEncoder.encode(message.getSmsContent());
//				String mid = "";
//				String line1 = "";
//				String mobile = message.getMobile().replace("+", "");
//				System.out.println("mobile in sms sender:::::::" + mobile);
//				try {
//					Date date = new Date();
//					SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss yyyy-MM-dd");
//					URL ur = new URL("http://" + smsProperties.getIp() + "/failsafe/HttpLink?username="
//							+ smsProperties.getUsername() + "&pin=" + smsProperties.getPassword() + "&message=" + msg
//							+ "&mnumber=" + mobile + "&signature=EFORMS&dlt_entity_id=110100001364&dlt_template_id="
//							+ message.getTemplateId());
//					System.out.println(formatter.format(date) + "SMS url is -------> " + ur);
//					InputStream respons = ur.openStream();
//					BufferedReader reader = new BufferedReader(new InputStreamReader(respons));
//					String line;
//					while ((line = reader.readLine()) != null) {
//						if (line.contains("Request ID=")) {
//							line1 = line;
//							mid = line.substring(line.indexOf("Request ID=") + 12, line.indexOf("~"));
//							System.out.println("---- MID: " + mid);
//							System.out.println("RESPONSE : " + line1);
//						}
//					}
//					reader.close();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				if (line1.startsWith("Message Accepted")) {
//					return "SUCCESS";
//				} else {
//					return "FAILED";
//				}
//			} else {
//				return "SUCCESS";
//			}
//		}
//		return "FAILED";
	}
}