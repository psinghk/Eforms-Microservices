package in.nic.eForms.models;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import in.nic.eForms.customValidation.Conditional;
import in.nic.eForms.customValidation.ConditionalDesignation;
import in.nic.eForms.customValidation.ConditionalEmployeeCode;
import in.nic.eForms.customValidation.ConditionalMobile;
import in.nic.eForms.customValidation.FieldMatch;
import in.nic.eForms.customValidation.UniqueEmail;
import in.nic.eForms.exceptions.DobValid;
import in.nic.eForms.utils.LocalDateDeserializer;
import in.nic.eForms.utils.LocalDateSerializer;
import lombok.Data;

@Data
public class PreviewFormBean {
	
	
	
	@NotEmpty(message = "Type  should not be empty")
	private String type;//com
	
	@NotEmpty(message = "Id Type  should not be empty")
	private String duplicateCheck;
	
	private List<MultipartFile> infile;   //xls file
	public List<MultipartFile> getInfile() {
		return infile;
	}
}
