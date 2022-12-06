package in.nic.eForms.models;

import java.time.LocalDate;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import in.nic.eForms.customValidation.EmailValid;
import in.nic.eForms.customValidation.Uid;
import in.nic.eForms.exceptions.DobValid;
import in.nic.eForms.exceptions.DorValid;
import in.nic.eForms.utils.LocalDateDeserializer;
import in.nic.eForms.utils.LocalDateSerializer;
import lombok.Data;

@Data
public class PreviewFormBean {
	private String inst_name;
	
	private String inst_id;
	
	private String nkn_project;
	
	private String preferred_email1;
	private String preferred_email2;
	private String organization;
	private String employment;
	private String department;
	private String otherDept;
	private String state;
	private String ministry;
	private String remarks;
	
	@JsonDeserialize(using=LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	@DateTimeFormat( pattern="dd-MM-yyyy")
	@DobValid
	private LocalDate single_dob;
	
	@JsonDeserialize(using=LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	@DateTimeFormat( pattern="dd-MM-yyyy")
	private LocalDate single_dor;
	
}
