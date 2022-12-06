package in.nic.eForms.models;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import in.nic.eForms.utils.LocalDateDeserializer;
import in.nic.eForms.utils.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidateFormBean {
	private String inst_name;
	
	private String inst_id;
	
	private String nkn_project;
	
	@JsonDeserialize(using=LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	@DateTimeFormat( pattern="dd-MM-yyyy")
	private LocalDate single_dob;
	
	private String preferred_email1;
	
	private String preferred_email2;
	
	@JsonDeserialize(using=LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	@DateTimeFormat( pattern="dd-MM-yyyy")
	private LocalDate single_dor;
	
}
