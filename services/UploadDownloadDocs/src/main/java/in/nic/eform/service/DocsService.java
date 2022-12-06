package in.nic.eform.service;

import java.io.BufferedInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import in.nic.eform.bean.UploadMultipleFilesBean;
import in.nic.eform.dao.DocsDao;
import in.nic.eform.dto.DocUpload;

@Service
public class DocsService {
	@Autowired
	DocsDao viewDocxDao;
	//@Value("${fileBasePath}")
	private String fileBasePath="D://";
	//@Value("${fileBasePath}")
	private String EXTERNAL_FILE_PATH="D://";
	
	
	public ArrayList<UploadMultipleFilesBean> uploadDocx(UploadMultipleFilesBean uploadfiles, String role) {
		ArrayList<UploadMultipleFilesBean> list = new ArrayList<>();
		UploadMultipleFilesBean response;
		List<MultipartFile> infile = uploadfiles.getInfile();
		String registrationno = uploadfiles.getRegno();
		if (infile.size() <= 5) {
			for (MultipartFile f : infile) {
				if (!f.isEmpty()) {
					String[] contenttype = f.getContentType().split("/");
					String ext = contenttype[1];
					try {
						String outputfile = new StringBuilder().append(registrationno).append("_")
								.append(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
								.append(".").append(ext).toString();
						byte[] bytes = f.getBytes();
						Path path = Paths.get(fileBasePath + outputfile);
						DocUpload uploadfilesdto = new DocUpload();
						uploadfilesdto.setDoc(outputfile);
						uploadfilesdto.setDocpath(fileBasePath);
						uploadfilesdto.setExtension(ext);
						uploadfilesdto.setFilename(f.getOriginalFilename());
						uploadfilesdto.setRegno(registrationno);
						uploadfilesdto.setRole(role.toUpperCase());
						uploadfilesdto = viewDocxDao.saveDocx(uploadfilesdto);

						System.out.println("::::::::::::" + uploadfilesdto.getId());
						if (uploadfilesdto.getId() == 0) {
							response = new UploadMultipleFilesBean();
							response.setDoc(f.getOriginalFilename());
							response.setStatus("failed");
							list.add(response);
						} else {
							Files.write(path, bytes);
							response = new UploadMultipleFilesBean();
							response.setDoc(f.getOriginalFilename());
							response.setModifiedfile(outputfile);
							response.setRegno(registrationno);
							response.setDocpath(fileBasePath);
							response.setStatus("success");
							list.add(response);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			response = new UploadMultipleFilesBean();
			response.setStatus("Can not upload more than 5 files!!");
			list.add(response);
			System.out.println(" Can not upload more than 5 files!!");
		}
		return list;
	}
	
	
	public Map<String, Object> viewDocx(String regid, String role) {
		Map<String, Object> alldocx = null;
		Map<String, Object> files = new HashMap<>();
		List<Object> listsup=new ArrayList<>();
		List<Object> listadmin=new ArrayList<>();
		List<Object> listca=new ArrayList<>();
		List<Object> listco=new ArrayList<>();
		List<Object> listuser=new ArrayList<>();
		List<Object> listda=new ArrayList<>();
		role = role.toLowerCase();
		switch (role) {
		case "sup":
			alldocx = viewDocxDao.viewDocx(regid, "sup");
			alldocx = viewDocxDao.viewDocx(regid, "admin");
			alldocx = viewDocxDao.viewDocx(regid, "da");
			listsup.add(alldocx);
		case "admin":
			alldocx = viewDocxDao.viewDocx(regid, "admin");
			 //files.putAll(alldocx);
			 listadmin.add(alldocx);
			 files.put("admin", listadmin);
			
		case "da":
			if (!role.equals("sup")) {
				  if (role.equals("da")) {
					  alldocx=viewDocxDao.viewDocx(regid, "da");
                  } else {
                	  alldocx=viewDocxDao.viewDocx(regid, "admin");
                  }
				 // files.putAll(alldocx);
				  listda.add(alldocx);
					// files.put("da", listda);
			}
		case "co":
			alldocx = viewDocxDao.viewDocx(regid, "co");
			listco.add(alldocx);
			//files.putAll(alldocx);
			files.put("co", listco);
			
		case "ca":
			alldocx = viewDocxDao.viewDocx(regid, "ca");
			//files.putAll(alldocx);
			listca.add(alldocx);
			files.put("ca", listca);
		case "user":
			alldocx = viewDocxDao.viewDocx(regid, "user");
			//files.putAll(alldocx);
			listuser.add(alldocx);
			files.put("user", listuser);
			break;
		}
		System.out.println(files);
		
		return files;
	}
	
	
	public Map<String,String> deleteDocx(Long id) {
		Map<String, String> map = new HashMap<>();
		int i=viewDocxDao.deleteDocx(id);
		if(i>0) {
		 map.put("status", "File Deleted successfully!!");
		}
		return map;
	}
	
	
	public HttpServletResponse downloadFiles(String filename,HttpServletResponse response)  {
		File f = new File(EXTERNAL_FILE_PATH + filename);
		System.out.println(f+" *********************");
		if (f.exists()) {
			String mimeType = URLConnection.guessContentTypeFromName(f.getName());
			System.out.println(mimeType);
			if (mimeType == null) {
				mimeType = "application/octet-stream";
			}
			response.setContentType(mimeType);
			response.setHeader("Content-Disposition", String.format("inline; filename=\"" + f.getName() + "\""));
			response.setContentLength((int) f.length());
			response.addHeader("status", "success");
			InputStream inputStream;
			try {
				inputStream = new BufferedInputStream(new FileInputStream(f));
				FileCopyUtils.copy(inputStream, response.getOutputStream());
			} catch (IOException e) {
				response.addHeader("status", e.getMessage());
			}
		} else {
			response.addHeader("status", "failed");
		}
		System.out.println(response.getStatus()+"    "+response.getHeader("status"));
		return response;
}
	
	
	public ArrayList<UploadMultipleFilesBean> updateDocx(MultipartFile infile,Long id) {
		    ArrayList<UploadMultipleFilesBean> list = new ArrayList<>();
		    DocUpload docs = viewDocxDao.fetchDocx(id);
		    String doc=docs.getDoc();
		    String docpath=docs.getDocpath();
		    String regNumber=docs.getRegno();
		    System.out.println("********"+doc+"********"+docpath);
			String[] pathnames;
			File f = new File(docpath);
			 pathnames = f.list();
			 for (String pathname : pathnames) {
		        	 System.out.println(pathname);
		        	if(pathname.equals(doc)) {
		        		System.out.println("******Matched*******");
		        		File file = new File(docpath+doc); 
		        		 boolean isdelete = file.delete();
		        		 System.out.println("isdelete  "+isdelete);
		        		 if(isdelete) {
		        			 System.out.println("isdeletedddddd  ");
		        			 list=updateSingleFile(regNumber,infile,docs);
		        		 }
		        	} 
	        }
			return list;
		}
	

	
	public  ArrayList<UploadMultipleFilesBean> updateSingleFile(String regNumber,MultipartFile infile,DocUpload docs){
		ArrayList<UploadMultipleFilesBean> list = new ArrayList<>();
		UploadMultipleFilesBean response;
		 if(!infile.isEmpty()) {
			 String[] contenttype = infile.getContentType().split("/"); 
				String ext = contenttype[1];
		try {
			String outputfile = new StringBuilder().append(regNumber).append("_")
					.append(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
					.append(".").append(ext).toString();
			 System.out.println("outputfile  "+outputfile);
			
			byte[] bytes = infile.getBytes();
			Path path = Paths.get(fileBasePath + outputfile);
			docs.setDoc(outputfile);
			docs.setDocpath(fileBasePath);
			docs.setExtension(ext);
			docs.setFilename(infile.getOriginalFilename());
			DocUpload viewDocxDTO= viewDocxDao.saveDocx(docs);
			
			System.out.println("::::::::::::" + viewDocxDTO.getId());
			if (viewDocxDTO.getId() == 0) {
			    response=new UploadMultipleFilesBean();
				response.setDoc(infile.getOriginalFilename());
				response.setStatus("failed");
				list.add(response);
			}
			else {
				Files.write(path, bytes);
				response=new UploadMultipleFilesBean();
				response.setDoc(infile.getOriginalFilename());
				response.setStatus("success");
				list.add(response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
		return list;
	}

}
