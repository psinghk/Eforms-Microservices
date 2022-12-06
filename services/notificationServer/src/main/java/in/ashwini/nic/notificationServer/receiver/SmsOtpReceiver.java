package in.ashwini.nic.notificationServer.receiver;


import org.springframework.stereotype.Component;
import in.ashwini.nic.notificationServer.model.CustomMessage;

@Component
public class SmsOtpReceiver {

  public void receiveMessage(CustomMessage message) {
    System.out.println("Received by SMS OTP consumer<" + message.toString() + ">");
  }
}