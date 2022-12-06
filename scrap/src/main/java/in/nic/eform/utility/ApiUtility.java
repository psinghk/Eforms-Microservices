package in.nic.eform.utility;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
@Component
public class ApiUtility {
	@Autowired
	RestTemplate restTemplate;
	
	public boolean isNICEmployeeApi(String uemail) {
	 HttpHeaders headers = new HttpHeaders();
     // headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
      HttpEntity <String> entity = new HttpEntity<String>(headers);
       ResponseEntity<Boolean> status = restTemplate.exchange("http://localhost:8081/isNicEmployee?mail="+uemail, HttpMethod.POST, entity, Boolean.class);
       boolean apiResponse=status.getBody();
      System.out.println("isNICEmployeeApi:::::apiResponse:::::"+apiResponse+" uemail:::"+uemail);
      return apiResponse;
	}
	
	public boolean isNICOutSourcedApi(String uemail) {
		 HttpHeaders headers = new HttpHeaders();
	     // headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	      HttpEntity <String> entity = new HttpEntity<String>(headers);
	       ResponseEntity<Boolean> status = restTemplate.exchange("http://localhost:8081/isNICOutSourced?mail="+uemail, HttpMethod.POST, entity, Boolean.class);
	       boolean apiResponse=status.getBody();
	      System.out.println("isNICOutSourcedApi:::::apiResponse:::::"+apiResponse+" uemail:::"+uemail);
	      return apiResponse;
		}
	
	
	public List<String> fetchAliases(String uemail) {
		  System.out.println("fetchAliases:::::uemail:::::"+uemail+" uemail:::"+uemail);
		HttpHeaders headers = new HttpHeaders();
		  HttpEntity <String> entity = new HttpEntity<String>(headers);
		  System.out.println("fetchAliases:::::entity:::::"+entity);
	       ResponseEntity<List> status = restTemplate.exchange("http://localhost:8081/fetchAliases?mail="+uemail, HttpMethod.POST, entity, List.class);
			  System.out.println("fetchAliases:::::status:::::"+status);
	       List<String> apiResponse = status.getBody();
	       System.out.println("fetchAliases:::::apiResponse:::::"+apiResponse);
	       apiResponse.add(uemail);
	      System.out.println("fetchAliases:::::apiResponse:::::"+apiResponse);
	      return apiResponse;
	}
	

	
	public boolean validateEmailApi(String uemail) {
		 HttpHeaders headers = new HttpHeaders();
	     // headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	      HttpEntity <String> entity = new HttpEntity<String>(headers);
	       ResponseEntity<String> status = restTemplate.exchange("http://localhost:8081/validateEmail?mail="+uemail, HttpMethod.POST, entity, String.class);
	      String apiResponse=status.getBody();
	      System.out.println("validateEmailApi:::::apiResponse:::::"+apiResponse+" uemail:::"+uemail);
	      if(apiResponse.contains("true")) {
	    	  return true;
	      }else {
	    	  return false;
	      }
		
		}
	
	public String findMobilebyId(String uemail) {
		 HttpHeaders headers = new HttpHeaders();
	     // headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	      HttpEntity <String> entity = new HttpEntity<String>(headers);
	       ResponseEntity<String> status = restTemplate.exchange("http://localhost:8081/findMobilebyId?mail="+uemail, HttpMethod.POST, entity, String.class);
	      String apiResponse=status.getBody();
	      System.out.println("findMobilebyId:::::apiResponse:::::"+apiResponse+" uemail:::"+uemail);
	    	  return apiResponse;
		}
	
	
	public String getUserDn(String uemail) {
		HttpHeaders headers = new HttpHeaders();
		  HttpEntity <String> entity = new HttpEntity<String>(headers);
	       ResponseEntity<String> status = restTemplate.exchange("http://localhost:8081/findDn?uid="+uemail, HttpMethod.POST, entity, String.class);
	       String apiResponse = status.getBody();
	      System.out.println("fetchAliases:::::apiResponse:::::"+apiResponse);
	      return apiResponse;
	}
	
	
}
