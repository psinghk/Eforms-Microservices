package in.nic.ashwini.eForms.models;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DnsListBean {
	//@Valid
	private List<ValidatedDnsBean> dnsList;
	//@NotBlank(message = "requestType must be either new, modify or delete")
	private String requestType;
	//@NotBlank(message = "recordType can not be empty. It must be either aaaa, mx, srv, spf, txt, dmarc or ptr")
	private String recordType;
	private String renamedFilePath;
	private String uploadedFile;
}
