package in.ashwini.nic.notificationServer.receiver;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import in.ashwini.nic.notificationServer.config.SmsProperties;
import in.ashwini.nic.notificationServer.model.FinalCustomMessage;

@Component
public class SmsOtpReceiver {

	@Autowired
	SmsProperties smsProperties;

	public void receiveMessage(FinalCustomMessage message) {
		System.out.println("Received by SMS OTP consumer<" + message.toString() + ">");

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
//							+ smsProperties.getUsernameForOtp() + "&pin=" + smsProperties.getPasswordForOtp()
//							+ "&message=" + msg + "&mnumber=" + mobile
//							+ "&signature=EFORMS&dlt_entity_id=110100001364&dlt_template_id="
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