package in.nic.ashwini.eForms.services;

import java.io.BufferedInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;
import in.nic.ashwini.eForms.entities.GeneratePdf;
import in.nic.ashwini.eForms.entities.ModeratorBase;
import in.nic.ashwini.eForms.entities.OMGeneratePdf;
import in.nic.ashwini.eForms.entities.DlistBase;
import in.nic.ashwini.eForms.models.FinalAuditTrack;
import in.nic.ashwini.eForms.models.GeneratePdfBean;
import in.nic.ashwini.eForms.models.Moderators;
import in.nic.ashwini.eForms.models.Owners;
import in.nic.ashwini.eForms.models.PreviewFormBean;
import in.nic.ashwini.eForms.models.Status;
import in.nic.ashwini.eForms.repositories.GeneratePdfRepository;
import in.nic.ashwini.eForms.repositories.ModeratorBaseRepo;
import in.nic.ashwini.eForms.repositories.OwnerModeratorPdfRepository;
import in.nic.ashwini.eForms.repositories.DlistBaseRepo;
import in.nic.ashwini.eForms.utils.Util;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@Service
public class DlistService {
	@Value("${fileBasePath}")
	private String EXTERNAL_FILE_PATH;
	@Value("${fileBasePath}")
	private String fileBasePath;

	private final GeneratePdfRepository generatePdfRepository;
	private final DlistBaseRepo dlistBaseRepo;
    private final ModeratorBaseRepo moderatorBaseRepo;
	private final Util utilityService;
	//private ModeratorBase moderatorBase;
	@Autowired
	OwnerModeratorPdfRepository ownerModeratorPdfRepository;

	@Autowired
	public DlistService(GeneratePdfRepository generatePdfRepository, DlistBaseRepo dlistBaseRepo,
			Util utilityService, ModeratorBaseRepo moderatorBaseRepo) {
		super();
		this.generatePdfRepository = generatePdfRepository;
		this.dlistBaseRepo = dlistBaseRepo;
		this.utilityService = utilityService;
        this.moderatorBaseRepo=moderatorBaseRepo;
       // this.moderatorBase =moderatorBase;
	}

	public JasperPrint generateFormPdf(String regid)
			throws JRException, IOException, IllegalAccessException, InvocationTargetException {
		JasperPrint jasperPrint = null;
		Optional<GeneratePdf> formDetails = generatePdfRepository.findByRegistrationNo(regid);
		List<OMGeneratePdf> formDetails1 = ownerModeratorPdfRepository.findByRegistrationNo(regid);
		GeneratePdf generatePdfDTO = null;
		if (formDetails.isPresent()) {
			generatePdfDTO = formDetails.orElse(null);
			GeneratePdfBean generatePdfBean = new GeneratePdfBean();
			org.springframework.beans.BeanUtils.copyProperties(generatePdfDTO, generatePdfBean);
			File file = ResourceUtils.getFile("classpath:dlistreport.jrxml");
			JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
			JRDataSource datasource = new JREmptyDataSource();
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("regid", generatePdfBean.getRegistrationNo());
			parameters.put("applicant_name", generatePdfBean.getApplicantName());
			parameters.put("applicant_email", generatePdfBean.getApplicantEmail());
			parameters.put("min", generatePdfBean.getMinistry());
			parameters.put("applicant_mobile", generatePdfBean.getApplicantMobile());
			parameters.put("hod_name", generatePdfBean.getHodName());
			parameters.put("hod_email", generatePdfBean.getHodEmail());
			parameters.put("hod_mobile", generatePdfBean.getHodMobile());
			
			parameters.put("list_name", generatePdfBean.getList_name());
			parameters.put("description_list", generatePdfBean.getDescription_list());
			parameters.put("list_mod", generatePdfBean.getList_mod());
			parameters.put("allowed_member", generatePdfBean.getAllowed_member());
			parameters.put("non_nicnet", generatePdfBean.getNon_nicnet());
			parameters.put("list_temp", generatePdfBean.getList_temp());
			parameters.put("memberCount", generatePdfBean.getMemberCount());
		    parameters.put("listofmod", formDetails1);
       		parameters.put("logo", "classpath:static/NIC-Logo.jpg");
				System.out.println(parameters);
				jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, datasource);
			}
		return jasperPrint;
	}

	public HttpServletResponse downloadFiles(String filename, HttpServletResponse response) {
		File f = new File(EXTERNAL_FILE_PATH + filename);
		if (f.exists()) {
			String mimeType = URLConnection.guessContentTypeFromName(f.getName());
			System.out.println(mimeType);
			if (mimeType == null) {
				mimeType = "application/octet-stream";
			}
			response.setContentType(mimeType);
			response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + f.getName() + "\""));
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
		return response;
	}

	public DlistBase preview(String regNo) {
		return dlistBaseRepo.findByRegistrationNo(regNo);
	}
	
	public List<ModeratorBase> previewBulk(String regNo) {
		return moderatorBaseRepo.findByRegistrationNo(regNo);
	}

	
	@Transactional
	public DlistBase insert(DlistBase dlistBase,List<Owners> listowners,List<Moderators> listmoderators) {
		if (dlistBase != null) {
			Date date = new Date();
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			String pdate = dateFormat.format(date);
			String oldRegNumber = dlistBaseRepo.findLatestRegistrationNo();
			String newRegNumber = "DLIST-FORM" + pdate;
			if (oldRegNumber == null || oldRegNumber.isEmpty()) {
				newRegNumber += "0001";
			} else {
				String lastst = oldRegNumber.substring(18, oldRegNumber.length());
				int last = Integer.parseInt(lastst);
				int newrefno = last + 1;
				int len = Integer.toString(newrefno).length();
				if (len == 1) {
					newRegNumber += "000" + newrefno;
				} else if (len == 2) {
					newRegNumber += "00" + newrefno;
				} else if (len == 3) {
					newRegNumber += "0" + newrefno;
				}
			}
			dlistBase.setRegistrationNo(newRegNumber);
			dlistBase.setSupportActionTaken("p");
			
			DlistBase dbase = dlistBaseRepo.save(dlistBase);
			String regno=dbase.getRegistrationNo();
			
			if(regno!=null) {
				System.out.println("moderators " + listmoderators.size());
				System.out.println("owners " + listowners.size());
			
				//iterator listowners 
				for(Owners owners:listowners) {
					ModeratorBase base=new ModeratorBase();
					base.setOmName(owners.getOwner_name());
					base.setOmEmail(owners.getOwner_email());
					base.setOmMobile(owners.getOwner_mobile());
					base.setFormType("owner");
					base.setRegistrationNo(regno);
					moderatorBaseRepo.save(base);
					System.out.println("saved owner in moderator table");	
				}
				
				//iterator listmoderators 
				for(Moderators moderators:listmoderators) {
					ModeratorBase base=new ModeratorBase();
					base.setOmName(moderators.getT_off_name());
					base.setOmEmail(moderators.getTauth_email());
					base.setOmMobile(moderators.getTmobile());
					base.setFormType("moderator");
					base.setRegistrationNo(regno);
					moderatorBaseRepo.save(base);
					System.out.println("saved moderator in moderator table");
				}
			}
			return dbase;
		}
		return null;
	}

	public ModeratorBase insertModerator(PreviewFormBean previewFormBean,String email) {
		ModeratorBase moderatorBase= new ModeratorBase ();
		DlistBase dlistBase =new DlistBase();
		
		moderatorBase.setFormType(previewFormBean.getFormType());
		moderatorBase.setOmEmail(email);
		moderatorBase.setOmMobile(previewFormBean.getOmMobile());
		moderatorBase.setOmName(previewFormBean.getOmName());
		moderatorBase.setRegistrationNo(dlistBase.getRegistrationNo());

			return moderatorBaseRepo.save(moderatorBase);
	   }

	  public boolean updatePreviewDetails(String regNumber, PreviewFormBean previewFormBean) {
			DlistBase dlistbase = dlistBaseRepo.findByRegistrationNo(regNumber);
				if (dlistbase != null) {
					try {
						BeanUtils.copyProperties(previewFormBean, dlistbase);
					dlistbase.setRegistrationNo(regNumber);
						LocalDateTime currentTime = LocalDateTime.now();
						DlistBase smsUpdated = dlistBaseRepo.save(dlistbase);
						if (smsUpdated.getId() > 0) {
							return true;
						}
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
				}
				return false;
			}

	public boolean updateStatusTable(String regNumber, String currentRole, PreviewFormBean previewFormBean) {
		DlistBase dlistBase = dlistBaseRepo.findByRegistrationNo(regNumber);
		if (dlistBase != null) {
			try {
				BeanUtils.copyProperties(previewFormBean, dlistBase);
				LocalDateTime currentTime = LocalDateTime.now();
				DlistBase dlistUpdated = dlistBaseRepo.save(dlistBase);
				if (dlistUpdated.getId() > 0) {
					return true;
				}
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	public boolean updateStatusAndFinalAuditTrack(Status status, FinalAuditTrack finalAuditTrack) {
		return utilityService.updateStatusAndFinalAuditTrack(status,finalAuditTrack);
	}
	
   public Optional<ModeratorBase> singleOwnerDataFetch(Long id) {
		
	    return moderatorBaseRepo.findById(id);
	 }
	public void dlistDataDelete(Long id) {
		
		   moderatorBaseRepo.deleteById(id);
	}

	public Optional<ModeratorBase> singleOwnerDataEditPost(Long id , ModeratorBase moderatorBase) {

		Optional<ModeratorBase> moderatorBase1= moderatorBaseRepo.findById(id);
		System.out.println("data are-----: "+moderatorBase1);
		ModeratorBase moderator = new ModeratorBase();
		if(moderatorBase1 !=null ) {
			
			moderator.setId(moderatorBase1.get().getId());
			moderator.setFormType(moderatorBase.getFormType());
			moderator.setOmEmail(moderatorBase.getOmEmail());
			moderator.setOmMobile(moderatorBase.getOmMobile());
			moderator.setOmName(moderatorBase.getOmName());
			moderator.setRegistrationNo(moderatorBase1.get().getRegistrationNo());
		    moderatorBaseRepo.save(moderator);
		}
		return moderatorBase1;
	}
	
	public boolean updatedlistbase(DlistBase dlistBase) {
		DlistBase dlistdetails = dlistBaseRepo.save(dlistBase);
		if (dlistdetails.getId() > 0) {
			return true;
		} else {
			return false;
		}
	}
}