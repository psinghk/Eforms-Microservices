package in.nic.eform.dao;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import in.nic.eform.dto.DocUpload;
import in.nic.eform.repository.DocsRepository;

@Repository
public class DocsDao {
	@Autowired
	DocsRepository viewDocxRepo;
	
	public Map<String,Object> viewDocx(String regid, String role) {
		Map<String, String> file = new HashMap<>();
		Map<String,Object> filelist = new HashMap<>();
		List<DocUpload> iterable = viewDocxRepo.findByRegnoAndRole(regid,role);
	List lmap=new ArrayList<>();
		for(DocUpload dto:iterable) {
			System.out.println(dto.getId()+" "+dto.getFilename());
			String id=dto.getId()+"";
//			file.put(id, dto.getFilename());
			file.put("modifiedname", dto.getDoc());
			file.put("filepath", dto.getDocpath());
			file.put("role", dto.getRole());
			file.put("id",dto.getId()+"");
			lmap.add(file);
		}
		 String roleName = null;
		 switch (role) {
         case "user":
             roleName = "Applicant";
             break;
         case "ca":
             roleName = "Reporting/Forwarding/Nodal Officer";
             break;
         case "sup":
             roleName = "Support";
             break;
         case "co":
             roleName = "Coordinator";
             break;
         case "admin":
             roleName = "Admin";
             break;
         case "da":
             roleName = "Delegated Admin";
             break;
     }
		 System.out.println("****************************");
		 System.out.println(roleName+"####################"+file+"####################"+file.size());
		 System.out.println("****************************");
		 if (lmap.size() > 0)
			 filelist.put("files", lmap);
		return filelist; 
		 
	}

	
	public int deleteDocx(Long id) {
		return viewDocxRepo.deleteId(id);
	}

	
	public DocUpload saveDocx(DocUpload uploadfiles) {
		DocUpload uploadfilesdto = viewDocxRepo.save(uploadfiles);
		return uploadfilesdto;
	}
		
	public DocUpload fetchDocx(Long id) {
		DocUpload viewDocxDTO = viewDocxRepo.findById(id);

		return viewDocxDTO;
	}
}
