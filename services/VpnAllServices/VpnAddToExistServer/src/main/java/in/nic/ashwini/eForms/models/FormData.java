package in.nic.ashwini.eForms.models;

import javax.validation.constraints.NotNull;

import in.nic.ashwini.eForms.validation.Ip;
import in.nic.ashwini.eForms.validation.IpRangeFrom;
import in.nic.ashwini.eForms.validation.Location;
import in.nic.ashwini.eForms.validation.Port;
import in.nic.ashwini.eForms.validation.Url;
import lombok.Data;

@Data
//@IpRangeFrom
public class FormData {

	// @Ip
	String serverIp;

//	@Url
	String applicationUrl;

	// @Port
	@NotNull(message = " Destination Port should not be empty")
	String destinationPort;

//	@Location
	@NotNull(message = " Server Location should not be empty")
	String serverLocation;

	String ipRangeFrom;
	String ipRangeTo;

	// @NotNull(message = " Type should not be empty")
	String iptype;

//	@NotNull(message = "Action Type should not be empty")
	String actionType;

}
