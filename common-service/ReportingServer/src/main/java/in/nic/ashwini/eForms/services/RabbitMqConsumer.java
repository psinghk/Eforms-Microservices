package in.nic.ashwini.eForms.services;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.nic.ashwini.eForms.db.master.entities.FinalAuditTrack;
import in.nic.ashwini.eForms.db.master.entities.Status;

@Component
public class RabbitMqConsumer { 
	
	@Autowired
	private StatusService statusService;

	//@RabbitListener(queues = "${rabbitmq.queue}")
	public Boolean recievedMessage(Map<String, Object> data) {
		ObjectMapper mapper = new ObjectMapper();
		Object object = data.get("status");
		Status status = mapper.convertValue(object, Status.class);
		object =  data.get("finalAuditTrack");
		FinalAuditTrack finalAuditTrack = mapper.convertValue(object, FinalAuditTrack.class);
		System.out.println("Received a new notification...!!");
		return statusService.updateStatusAndFinalAuditTrack(status,finalAuditTrack);
	}
	
}
