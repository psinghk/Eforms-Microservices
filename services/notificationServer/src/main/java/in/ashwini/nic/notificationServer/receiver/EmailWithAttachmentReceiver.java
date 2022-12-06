package in.ashwini.nic.notificationServer.receiver;

import org.springframework.stereotype.Component;

import in.ashwini.nic.notificationServer.model.CustomMessage;

@Component
public class EmailWithAttachmentReceiver {

  public void receiveMessage(CustomMessage message) {
    System.out.println("Received by Email with attachment consumer <" + message.toString() + ">");
  }
}