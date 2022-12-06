package in.nic.ashwini.eForms.controller;

import java.util.Map;

import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.nic.ashwini.eForms.services.QueryService;

@RestController
public class QueryController {

	private final QueryService queryService;

	@Autowired
	public QueryController(QueryService queryService) {
		super();
		this.queryService = queryService;
	}

	@GetMapping("/fetchQueries")
	public Map<String, Object> fetchQueries(@RequestParam("regNumber") @NotEmpty String regNumber){
		return queryService.fetchQueries(regNumber);
	}
	
	@PostMapping("/admin/raiseQuery")
	public String adminRaiseQuery(@RequestParam("regNumber") @NotEmpty String regNumber, 
			@RequestParam @NotEmpty String trole, @RequestParam("email") @NotEmpty String email,
			@RequestParam @NotEmpty String remarks){
		return queryService.raiseQuery(regNumber, "m", trole, remarks, email);
	}
	
	@PostMapping("/support/raiseQuery")
	public String supportRaiseQuery(@RequestParam("regNumber") @NotEmpty String regNumber, 
			@RequestParam @NotEmpty String trole, @RequestParam("email") @NotEmpty String email,
			@RequestParam @NotEmpty String remarks){
		return queryService.raiseQuery(regNumber, "s", trole, remarks, email);
	}
	
	@PostMapping("/ro/raiseQuery")
	public String roRaiseQuery(@RequestParam("regNumber") @NotEmpty String regNumber, 
			@RequestParam @NotEmpty String trole, @RequestParam("email") @NotEmpty String email,
			@RequestParam @NotEmpty String remarks){
		return queryService.raiseQuery(regNumber, "ca", trole, remarks, email);
	}
	
	@PostMapping("/coord/raiseQuery")
	public String coordRaiseQuery(@RequestParam("regNumber") @NotEmpty String regNumber, 
			@RequestParam @NotEmpty String trole, @RequestParam("email") @NotEmpty String email,
			@RequestParam @NotEmpty String remarks){
		return queryService.raiseQuery(regNumber, "c", trole, remarks, email);
	}
	
	@PostMapping("/raiseQuery")
	public String userRaiseQuery(@RequestParam("regNumber") @NotEmpty String regNumber, 
			@RequestParam @NotEmpty String trole, @RequestParam("email") @NotEmpty String email,
			@RequestParam @NotEmpty String remarks){
		return queryService.raiseQuery(regNumber, "a", trole, remarks, email);
	}
}
