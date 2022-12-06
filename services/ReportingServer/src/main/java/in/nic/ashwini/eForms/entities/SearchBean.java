package in.nic.ashwini.eForms.entities;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchBean {
	public List<String> services;
	public List<String> status;
	public String searchKey;
}
