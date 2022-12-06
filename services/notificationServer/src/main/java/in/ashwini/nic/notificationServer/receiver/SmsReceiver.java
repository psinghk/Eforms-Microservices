package in.ashwini.nic.notificationServer.receiver;


import org.springframework.stereotype.Component;
import in.ashwini.nic.notificationServer.model.CustomMessage;
import in.ashwini.nic.notificationServer.model.FinalCustomMessage;

@Component
public class SmsReceiver {

  public void receiveMessage(CustomMessage message) {
    System.out.println("Received by SMS consumer<" + message.toString() + ">");
  }
}