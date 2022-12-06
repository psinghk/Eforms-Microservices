package in.nic.eForms.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import in.nic.eForms.entities.DocUpload;
import in.nic.eForms.repositories.DocsRepository;


@Repository
public class DocsDao {
	@Autowired
	DocsRepository docxRepo;
	
	public Map<String, Map<String, String>> viewDocx(String regid, String role) {
		Map<String, String> file = new HashMap<>();
		Map<String,Map<String,String>> filelist = new HashMap<>();
		List<DocUpload> iterable = docxRepo.findByRegistrationNoAndRoleAndStatus(regid,role, "a");
		
		for(DocUpload dto:iterable) {
			System.out.println(dto.getId()+" "+dto.getOriginalFilename());
			String id=dto.getId()+"";
			file.put(id, dto.getOriginalFilename());
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
		 if (file.size() > 0)
		 filelist.put(roleName, file);
		return filelist;
	}

	
	public DocUpload deleteDocx(Long id) {
		//return viewDocxRepo.deleteId(id);
		DocUpload uploadedDoc = docxRepo.findById(id);
		uploadedDoc.setStatus("i");
		return docxRepo.save(uploadedDoc);
	}

	
	public DocUpload saveDocx(DocUpload uploadfiles) {
		DocUpload uploadfilesdto = docxRepo.save(uploadfiles);
		return uploadfilesdto;
	}
		
	public DocUpload fetchDocx(Long id) {
		DocUpload viewDocxDTO = docxRepo.findById(id);
		return viewDocxDTO;
	}
}
