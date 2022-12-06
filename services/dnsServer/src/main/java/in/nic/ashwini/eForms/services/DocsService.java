package in.nic.ashwini.eForms.services;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import in.nic.ashwini.eForms.entities.DocUpload;
import in.nic.ashwini.eForms.models.UploadMultipleFilesBean;

@Service
public class DocsService {
	@Autowired
	DocsDao docxDao;

	@Value("${fileBasePath}")
	private String fileBasePath;
	@Value("${fileBasePath}")
	private String EXTERNAL_FILE_PATH;

	public ArrayList<UploadMultipleFilesBean> uploadDocx(UploadMultipleFilesBean uploadfiles, String role) {
		ArrayList<UploadMultipleFilesBean> list = new ArrayList<>();
		UploadMultipleFilesBean response;
		List<MultipartFile> infile = uploadfiles.getInfile();
		String registrationNo = uploadfiles.getRegistrationNo();
		if (infile.size() <= 1) {
			for (MultipartFile f : infile) {
				if (!f.isEmpty()) {
					String[] contenttype = f.getContentType().split("/");
					String ext = contenttype[1];
					try {
						String outputfile = new StringBuilder().append(registrationNo).append("_")
								.append(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
								.append(".").append(ext).toString();
						byte[] bytes = f.getBytes();
						Path path = Paths.get(fileBasePath + outputfile);
						DocUpload uploadfilesdto = new DocUpload();
						uploadfilesdto.setDoc(outputfile);
						uploadfilesdto.setDocPath(fileBasePath);
						uploadfilesdto.setExtension(ext);
						uploadfilesdto.setOriginalFilename(f.getOriginalFilename());
						uploadfilesdto.setRegistrationNo(registrationNo);
						uploadfilesdto.setRole(role.toUpperCase());
						uploadfilesdto = docxDao.saveDocx(uploadfilesdto);

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
			response.setStatus("Can not upload more than 1 file!!");
			list.add(response);
			System.out.println(" Can not upload more than 1 file!!");
		}
		return list;
	}

	public Map<String, Map<String, String>> viewDocx(String regid, String role) {
		Map<String, Map<String, String>> alldocx = null;
		Map<String, Map<String, String>> files = new HashMap<>();
		role = role.toLowerCase();
		switch (role) {
		case "sup":
			alldocx = docxDao.viewDocx(regid, "sup");
			alldocx = docxDao.viewDocx(regid, "admin");
			alldocx = docxDao.viewDocx(regid, "da");
		case "admin":
			alldocx = docxDao.viewDocx(regid, "admin");
		case "da":
			if (!role.equals("sup")) {
				if (role.equals("da")) {
					docxDao.viewDocx(regid, "da");
				} else {
					docxDao.viewDocx(regid, "admin");
				}
				files.putAll(alldocx);
			}
		case "co":
			alldocx = docxDao.viewDocx(regid, "co");
			files.putAll(alldocx);
		case "ca":
			alldocx = docxDao.viewDocx(regid, "ca");
			files.putAll(alldocx);
		case "user":
			alldocx = docxDao.viewDocx(regid, "user");
			files.putAll(alldocx);
			break;
		}
		System.out.println(files);
		return files;
	}

	public Map<String, String> deleteDocx(Long id) {
		Map<String, String> map = new HashMap<>();
		DocUpload deletedDoc = docxDao.deleteDocx(id);
		if (deletedDoc.getId() > 0) {
			map.put("status", "File Deleted successfully!!");
		}
		return map;
	}

	public HttpServletResponse downloadFiles(String filename, HttpServletResponse response) {
		File f = new File(EXTERNAL_FILE_PATH + filename);
		System.out.println(f + " *********************");
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
		System.out.println(response.getStatus() + "    " + response.getHeader("status"));
		return response;
	}

	public ArrayList<UploadMultipleFilesBean> updateDocx(MultipartFile infile, Long id) {
		ArrayList<UploadMultipleFilesBean> list = new ArrayList<>();
		DocUpload docs = docxDao.fetchDocx(id);
		String doc = docs.getDoc();
		String docpath = docs.getDocPath();
		String regNumber = docs.getRegistrationNo();
		System.out.println("********" + doc + "********" + docpath);
		String[] pathnames;
		File f = new File(docpath);
		pathnames = f.list();
		for (String pathname : pathnames) {
			System.out.println(pathname);
			if (pathname.equals(doc)) {
				System.out.println("******Matched*******");
				File file = new File(docpath + doc);
				boolean isdelete = file.delete();
				System.out.println("isdelete  " + isdelete);
				if (isdelete) {
					System.out.println("isdeletedddddd  ");
					list = updateSingleFile(regNumber, infile, docs);
				}
			}
//		        	else {
//		        		 System.out.println("******NOT Matched*********");
//		        	}
		}
		return list;
	}

	public ArrayList<UploadMultipleFilesBean> updateSingleFile(String regNumber, MultipartFile infile,
			DocUpload docs) {
		ArrayList<UploadMultipleFilesBean> list = new ArrayList<>();
		UploadMultipleFilesBean response;
		if (!infile.isEmpty()) {
			String[] contenttype = infile.getContentType().split("/");
			String ext = contenttype[1];
			try {
				String outputfile = new StringBuilder().append(regNumber).append("_")
						.append(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
						.append(".").append(ext).toString();
				System.out.println("outputfile  " + outputfile);

				byte[] bytes = infile.getBytes();
				Path path = Paths.get(fileBasePath + outputfile);
				docs.setDoc(outputfile);
				docs.setDocPath(fileBasePath);
				docs.setExtension(ext);
				docs.setOriginalFilename(infile.getOriginalFilename());
				DocUpload viewDocxDTO = docxDao.saveDocx(docs);

				System.out.println("::::::::::::" + viewDocxDTO.getId());
				if (viewDocxDTO.getId() == 0) {
					response = new UploadMultipleFilesBean();
					response.setDoc(infile.getOriginalFilename());
					response.setStatus("failed");
					list.add(response);
				} else {
					Files.write(path, bytes);
					response = new UploadMultipleFilesBean();
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
