package in.nic.ashwini.eForms.db.master.entities;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchBean {
	private List<String> services;
	private List<String> status;
	private String searchKey;
	@NotEmpty(message = "Ministry can not be null")
	private List<String> categoryAndministry;
	private List<String> applicants;
}
