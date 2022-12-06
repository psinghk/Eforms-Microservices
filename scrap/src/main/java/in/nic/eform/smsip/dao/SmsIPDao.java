package in.nic.eform.smsip.dao;

import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.nic.eform.smsip.bean.SmsIPFormBean;
import in.nic.eform.smsip.dto.SmsIPBaseDTO;
import in.nic.eform.smsip.dto.SmsIPDocUploadDTO;
import in.nic.eform.smsip.dto.SmsIPEmpCoordDTO;
import in.nic.eform.smsip.dto.SmsIPFinalAuditTrackDTO;
import in.nic.eform.smsip.dto.SmsIPMailadminFormsDTO;
import in.nic.eform.smsip.dto.SmsIPQueryRaiseDTO;
import in.nic.eform.smsip.dto.SmsIPStatusDTO;
import in.nic.eform.smsip.repository.SmsIPBaseListRepository;
import in.nic.eform.smsip.repository.SmsIPBaseRepository;
import in.nic.eform.smsip.repository.SmsIPDocUploadRepo;
import in.nic.eform.smsip.repository.SmsIPEmpCoordListRepo;
import in.nic.eform.smsip.repository.SmsIPEmpCoordRepo;
import in.nic.eform.smsip.repository.SmsIPFinalAuditTrackListRepo;
import in.nic.eform.smsip.repository.SmsIPFinalAuditTrackRepo;
import in.nic.eform.smsip.repository.SmsIPMailadminFormsRepo;
import in.nic.eform.smsip.repository.SmsIPQueryRaiseRepo;
import in.nic.eform.smsip.repository.SmsIPStatusListRepo;
import in.nic.eform.smsip.repository.SmsIPStatusRepo;
import in.nic.eform.utility.ApiUtility;
import in.nic.eform.utility.CommonUtility;
import in.nic.eform.utility.Constants;

@Repository
public class SmsIPDao {
	private static final Logger log = LoggerFactory.getLogger(SmsIPDao.class);
	@Autowired
	SmsIPBaseRepository smsIPBaseRepository;
	@Autowired
	SmsIPBaseListRepository smsIPBaseListRepository;
	@Autowired
	SmsIPFinalAuditTrackRepo finalAuditTrackRepository;
	@Autowired
	SmsIPFinalAuditTrackListRepo finalAuditTrackListRepository;
	@Autowired
	SmsIPStatusRepo statusRepository;
	@Autowired
	SmsIPStatusListRepo statusListRepository;
	@Autowired
	SmsIPMailadminFormsRepo mailadminFormsRepository;
	@Autowired 
	SmsIPEmpCoordRepo empCoordRepository;
	@Autowired
	SmsIPEmpCoordListRepo empCoordListRepository;
	@Autowired
	SmsIPQueryRaiseRepo queryRaiseRepository;
	@Autowired
	SmsIPDocUploadRepo docUploadRepository;
	@Autowired
	CommonUtility commonUtility;
	@Autowired
	ApiUtility apiUtility;
	
	
	//********************************Start of Submission*********************************
	public String ldap_tab2(SmsIPBaseDTO smsIPBaseDTO)  {
		log.info("Inside ldap_tab2 method at " + new Date());
		 String dbrefno = "", newref = "";
		 int newrefno;
		  Date date1 = new Date();
          DateFormat dateFormat1 = new SimpleDateFormat("yyyyMMdd");
          String pdate1 = dateFormat1.format(date1);
          SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd");
  		Date dt = new Date();
  		String datetime = format.format(dt);
  		List<SmsIPBaseDTO> sbd=smsIPBaseRepository.findByDatetimeLike("%"+datetime+"%");
  		log.info("Inside ldap_tab2 method at " + new Date()+"  sbd:::"+sbd);

          for(SmsIPBaseDTO subBaseDTO:sbd)
        	  dbrefno=subBaseDTO.getRegistrationno();
          log.info("Inside ldap_tab2 method at " + new Date()+"  dbrefno:::"+dbrefno);
		  if (dbrefno == null || dbrefno.equals("")) {
              newrefno = 1;
              newref = "000" + newrefno;
              log.info("Inside ldap_tab2 method at " + new Date()+"  newref:::"+newref);
              
          } else {
              String lastst = dbrefno.substring(15, dbrefno.length());
              int last = Integer.parseInt(lastst);
              newrefno = last + 1;
              int len = Integer.toString(newrefno).length();
              if (len == 1) {
                  newref = "000" + newrefno;
              } else if (len == 2) {
                  newref = "00" + newrefno;
              } else if (len == 3) {
                  newref = "0" + newrefno;
              }
          }
          newref = "IP-FORM" + pdate1 + newref;
          log.info("Inside ldap_tab2 method at " + new Date()+"  newref:::"+newref);
          smsIPBaseDTO.setRegistrationno(newref);
          smsIPBaseDTO.setUserip("0:0:0:0:0:0:0:1");
          smsIPBaseDTO.setDatetime(datetime);
          smsIPBaseDTO.setSupport_action_taken("p");
          SmsIPBaseDTO a = smsIPBaseRepository.save(smsIPBaseDTO);
          log.info("Inside ldap_tab2 method at " + new Date()+"  a:::"+a);
      	if(a.getId()==0) {
      		newref=null;
      	}
		return newref;
	}
	
	
	
	public int insertIntoFinalAuditTrack(Map<String, String> forwardBydata, Map<String, String> forwardToData) {
		 log.info("Inside insertIntoFinalAuditTrack method at " + new Date());
		int j=0;
		Date date = new Date();
		DateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String pdate = dt.format(date);
		String ip="0:0:0:0:0:0:0:1";
		SmsIPFinalAuditTrackDTO finalAuditTrackDTO=new SmsIPFinalAuditTrackDTO();
		finalAuditTrackDTO.setRegistrationno(forwardBydata.get("regNumber").toString());
		finalAuditTrackDTO.setApplicantemail(forwardBydata.get("forwardedByEmail"));
		finalAuditTrackDTO.setApplicant_mobile(forwardBydata.get("forwardedByMobile"));
		finalAuditTrackDTO.setApplicant_name(forwardBydata.get("forwardedByName"));
		finalAuditTrackDTO.setApplicant_ip(ip);
		finalAuditTrackDTO.setApplicant_datetime(pdate);
		finalAuditTrackDTO.setStatus(forwardToData.get("status"));
		finalAuditTrackDTO.setForm_name(forwardBydata.get("formType"));
		finalAuditTrackDTO.setToemail(forwardToData.get("forwardedToEmail").replace(",,", ",").replaceFirst("^,", ""));
		finalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
		finalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
		finalAuditTrackDTO.setTo_datetime(pdate);
		finalAuditTrackDTO.setApp_user_type(forwardBydata.get("check"));
		finalAuditTrackDTO.setApp_user_path(forwardBydata.get("filePath"));
		finalAuditTrackDTO.setApp_ca_type(forwardBydata.get(""));
		finalAuditTrackDTO.setApp_ca_path("");
		finalAuditTrackDTO.setOn_hold("n");
		SmsIPFinalAuditTrackDTO i=finalAuditTrackRepository.save(finalAuditTrackDTO);
		  if(i.getTrack_id()>0) {
	        	j=1;
	        }
		  log.info("Inside insertIntoFinalAuditTrack method at " + new Date()+" J:::"+j);
		return j;
	}
	//********************************End of Submission***********************************
		
		
	//********************************Start of Preview(after submission)******************
	public boolean checkStatusForPreview(String registrationNumber, String email, String role, String filter, Set<String> s) {
		 log.info("Inside checkStatusForPreview method at " + new Date() +"  role:::"+role);  
		boolean isValid = false;
		 if (!(role.equals("admin") || role.equals("sup"))) {
	             for (String emailAddress : s) {
	            	 int i = finalAuditTrackRepository.countByregistrationnoAndToemailLikeOrApplicantemailOrCaemailLikeOrCoordinatoremailLike(registrationNumber,emailAddress,emailAddress,emailAddress,emailAddress);
	            	 log.info("Inside checkStatusForPreview method at " + new Date() +"  i:::"+i); 
	            	 if(i>0) {
	            		 isValid = true;
	            	 }
	             }
	         }
		 return isValid;
	 }
	
	public Map<String, Object> preview(String regno) {
		 log.info("Inside preview method at " + new Date() +"  regno:::"+regno);  
		Map<String, Object> previewlist=null;
		SmsIPBaseDTO previewBaseDTO=smsIPBaseRepository.findByRegistrationno(regno);
		 log.info("Inside preview method at " + new Date() +"  previewBaseDTO:::"+previewBaseDTO);  
		ObjectMapper m = new ObjectMapper();
		previewlist = m.convertValue(previewBaseDTO, Map.class);
		log.info("Inside preview method at " + new Date() +"  previewlist:::"+previewlist);  
		return previewlist;
	}
	
	public Map<String, Object> EditPreview(String regno) {
		log.info("Inside EditPreview method at " + new Date() +"  regno:::"+regno);  
		Map<String, Object> previewlist=preview(regno);
		log.info("Inside EditPreview method at " + new Date() +"  previewlist:::"+previewlist); 
		return previewlist;
	}
	
	public boolean UpdatePreviewDetails(SmsIPFormBean smsIPFormBean) {
		log.info("Inside UpdatePreviewDetails method at " + new Date()); 
		boolean status=false;
		SmsIPBaseDTO previewBaseDTO =smsIPBaseRepository.findByRegistrationno(smsIPFormBean.getRegistrationno());
		log.info("Inside UpdatePreviewDetails method at " + new Date()+" previewBaseDTO:::"+previewBaseDTO); 
		try {
			BeanUtils.copyProperties(previewBaseDTO, smsIPFormBean);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		SmsIPBaseDTO pbt = smsIPBaseRepository.save(previewBaseDTO);
		 log.info("Inside UpdatePreviewDetails method at " + new Date()+" pbt:::"+pbt); 
		 if(pbt.getId()>0) {
			 status=true;	
	      	}
		return status;
	}
	
	public boolean checkStatusForEdit(String registrationNumber, String email, String role, String filter, Set<String> s) {
		log.info("Inside checkStatusForEdit method at " + new Date() +" filter:::"+filter+"  role:::"+role); 
		boolean isValid = false;
       if (filter.equals("toEdit")) {
      	 switch (role) {
			case Constants.ROLE_USER:
				 for (String emailAddress1 : s) {
					int i = finalAuditTrackRepository.countByregistrationnoAndApplicantemailAndStatusOrStatusOrStatusOrStatus(registrationNumber,emailAddress1,"ca_pending","manual_upload","api","domainapi");
					log.info("Inside checkStatusForEdit method at " + new Date() +" i:::"+i); 
		           	 if(i>0) {
		           		 isValid = true;
		           	 }
               }
				break;
			case Constants.ROLE_CA:
				for (String emailAddress1 : s) {
					int i = finalAuditTrackRepository.countByregistrationnoAndCaemailAndStatusOrStatusAndToemail(registrationNumber,emailAddress1,"coordinator_pending","ca_pending",emailAddress1);
					log.info("Inside checkStatusForEdit method at " + new Date() +" i:::"+i); 
		           	 if(i>0) {
		           		 isValid = true;
		           	 }
              }
				break;
			case Constants.ROLE_CO:
				for (String emailAddress1 : s) {
					int i = finalAuditTrackRepository.countByregistrationnoAndCoordinatoremailAndStatusOrStatusAndToemail(registrationNumber,"%"+emailAddress1+"%","mail-admin_pending","coordinator_pending",emailAddress1);
					log.info("Inside checkStatusForEdit method at " + new Date() +" i:::"+i); 
		           	 if(i>0) {
		           		 isValid = true;
		           	 }
              }
				break;
			case Constants.ROLE_SUP:
				for (String emailAddress1 : s) {
					int i = finalAuditTrackRepository.countByregistrationnoAndSupportemailAndStatusOrStatus(registrationNumber,"%"+emailAddress1+"%","coordinator_pending","support_pending");
					log.info("Inside LdapDao: checkStatusForEdit method at " + new Date() +" i:::"+i); 
		           	 if(i>0) {
		           		 isValid = true;
		           	 }
              }
				break;
			case Constants.ROLE_MAILADMIN:
					int i = finalAuditTrackRepository.countByregistrationnoAndToemailAndStatus(registrationNumber,email,"mail-admin_pending");
					log.info("Inside checkStatusForEdit method at " + new Date() +" i:::"+i); 
		           	 if(i>0) {
		           		 isValid = true;
		           	 }
				break;
				default:
			}
      	 
       } 
       log.info("Inside checkStatusForEdit method at " + new Date() +"  isValid:::"+isValid); 
   return isValid;
}
		//********************************End of Preview(after submission)********************
		
		
		//********************************Start of Approve/Forward****************************
	public boolean validateRefNo(String refNo) {
		int i=smsIPBaseRepository.countByregistrationno(refNo);
		log.info("Inside validateRefNo method at " + new Date()+"  i:::"+i);
		if(i>0) {
			return false;
		}else {
			return true;
		}
	}
	
	public Map<String, String> fetchFormDetail(String regNo)  {
		log.info("Inside fetchFormDetail method at " + new Date());
		Map<String, String> mapbeanMap=null;
		SmsIPBaseDTO fetchFormDetail=smsIPBaseRepository.findByRegistrationno(regNo);
		log.info("Inside fetchFormDetail method at " + new Date()+"  fetchFormDetail:::"+fetchFormDetail);
			ObjectMapper m = new ObjectMapper();
			mapbeanMap = m.convertValue(fetchFormDetail, Map.class);
			log.info("Inside fetchFormDetail method at " + new Date()+"  mapbeanMap:::"+mapbeanMap);
		 return mapbeanMap;
	}
	
	public String fetchToEmail() {
		log.info("Inside fetchToEmail method at " + new Date());
		StringBuilder daEmails = new StringBuilder();
		ArrayList<String> arr = new ArrayList<>();
		//List<String> arr = null;
		String email="";
		List<SmsIPMailadminFormsDTO> arr1 = null;
		String prefix = "";
		try {
			//arr = forwardMailadminFormsRepository.findDistinctByMldap('y');
			arr1=mailadminFormsRepository.findDistinctByMldap("y");
			
			for(SmsIPMailadminFormsDTO mailadminFormsDTO:arr1) {
				email=mailadminFormsDTO.getMemail();
				arr.add(email);
			}
			
			
			System.out.println("***********************arr**********************************************"+arr);
			log.info("Inside fetchToEmail method at " + new Date()+"  arr:::"+arr);
			for (int i = 0; i < arr.size(); i++) {
				daEmails.append(prefix);
				prefix = ",";
				daEmails.append(prefix);
				daEmails.append(arr.get(i));
			}
			log.info("Inside fetchToEmail method at " + new Date()+"  daEmails:::"+daEmails.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return daEmails.toString();
	}
	
	public Map<String, String> fetchHimachalCoord(String dept) {
		log.info("Inside fetchHimachalCoord method at " + new Date());
		Map<String, String> coords = new HashMap<>();
		List<SmsIPEmpCoordDTO> coords1 = null;
		coords1 = empCoordRepository.findByEmpcoordemailAndEmpadminemailAndEmpdept(Constants.HIMACHAL_DA_ADMIN,Constants.HIMACHAL_DA_ADMIN,dept);
		for(SmsIPEmpCoordDTO a: coords1) {
			coords.put("emp_coord_email", a.getEmpcoordemail());
			coords.put("emp_coord_name", a.getEmpcoordname());
		}
		log.info("Inside fetchHimachalCoord method at " + new Date()+"  coords:::"+coords);
		//coords.get("emp_coord_email");
		//coords.get("emp_coord_name");
		return coords;
	}
	
	public String convertSetToString(Set<String> co) {
		log.info("Inside convertSetToString method at " + new Date());
		String string = String.join(", ", co);
		return (string.length() != 0) ? string.toString() : "";
	}
	
	public Set<String> isRecipientAdmin(String email) {
		log.info("Inside isRecipientAdmin method at " + new Date());
		return mailadminFormsRepository.findFirstDistinctByMemailAndMldap(email,'y');
	}
	
	  public Set<String> isRecipientDa(String email, Set<String> fetchAliases) {
		  Set<String> arr = new HashSet<>();  
		  Set<String> aliases = null;
		  String[] coords;
	            if (email.contains(",")) {
	                coords = email.split(",");
	            } else {
	                coords = new String[1];
	                coords[0] = email;
	            }
	            for (String coord : coords) {
	            	  boolean validatetoEmails = apiUtility.validateEmailApi(coord);
	                if (validatetoEmails) {
	                	aliases = fetchAliases;
	                } else {
	                    String[] aliasesInArray = {coord};
	                    aliases = new HashSet<>(Arrays.asList(aliasesInArray));
	                }
	                for (String aliase : aliases) {
	                	SmsIPEmpCoordDTO forwardEmpCoordDTO=empCoordRepository.findFirstByEmpcoordemailAndEmpadminemail(aliase,aliase);
	                	String emp_coord_email=forwardEmpCoordDTO.getEmpcoordemail();
	                	 arr.add(emp_coord_email);
	                }
	            }
	    
	        return arr;
	    }
	  
	
	public Set<String> isRecipientCoordinator(String email, Set<String> fetchAliases) {
		 Set<String> arr = new HashSet<>();  
		  Set<String> aliases = null;
		  String[] coords;
	            if (email.contains(",")) {
	                coords = email.split(",");
	            } else {
	                coords = new String[1];
	                coords[0] = email;
	            }
	            for (String coord : coords) {
	            	  boolean validatetoEmails = apiUtility.validateEmailApi(coord);
	                if (validatetoEmails) {
	                	aliases = fetchAliases;
	                } else {
	                    String[] aliasesInArray = {coord};
	                    aliases = new HashSet<>(Arrays.asList(aliasesInArray));
	                }
	                for (String aliase : aliases) {
	                	SmsIPEmpCoordDTO forwardEmpCoordDTO=empCoordRepository.findFirstByEmpcoordemail(email);
	                	String emp_coord_email=forwardEmpCoordDTO.getEmpcoordemail();
	                	 arr.add(emp_coord_email);
	                }
	            }
		log.info("Inside isRecipientCoordinator method at " + new Date());
		return arr;
	}
	
	public int insertIntoAppType(Map<String, String> forwardBydata, Map<String, String> forwardToData) {
		log.info("Inside insertIntoAppType method at " + new Date());
		int i=0,j=0;
		if (forwardBydata.get("check").equalsIgnoreCase("upload_scanned")) {
			SmsIPBaseDTO smsIPBaseDTO = smsIPBaseRepository.findByRegistrationno(forwardBydata.get("regNumber"));
			smsIPBaseDTO.setSign_cert(forwardBydata.get("filePath"));
			smsIPBaseDTO.setRename_sign_cert(forwardBydata.get("renameFilePath"));
			SmsIPBaseDTO id=smsIPBaseRepository.save(smsIPBaseDTO);
			if(id.getId()>0) {
				i=1;
			}
			log.info("Inside insertIntoAppType method at " + new Date()+" i:::"+i);
		} else {
			SmsIPBaseDTO forwardBaseDTO = smsIPBaseRepository.findByRegistrationno(forwardBydata.get("regNumber"));
			forwardBaseDTO.setPdf_path(forwardBydata.get("filePath"));
			SmsIPBaseDTO id=smsIPBaseRepository.save(forwardBaseDTO);
			if(id.getId()>0) {
				i=1;
			}
			log.info("Inside insertIntoAppType method at " + new Date()+" i:::"+i);
			}
		return i;
	}
	
	public int updateFinalAuditTrack(Map<String, String> forwardBydata, Map<String, String> forwardToData) {
		log.info("Inside updateFinalAuditTrack method at " + new Date());
		int i = 0, j = 0, k = 0;
		SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		Date dt = new Date();
		String datetime = format.format(dt);
		String role = forwardBydata.get("role");
		if (forwardBydata.get("check").equalsIgnoreCase("upload_scanned")) {
			String fwdToEmail = forwardToData.get("forwardedToEmail").replace(",,", ",").replaceFirst("^,", "");
			if (role.equalsIgnoreCase(Constants.ROLE_USER)) {
				SmsIPFinalAuditTrackDTO finalAuditTrackDTO = finalAuditTrackRepository.findByRegistrationno(forwardBydata.get("regNumber"));
				finalAuditTrackDTO.setStatus(forwardToData.get("status"));
				finalAuditTrackDTO.setToemail(fwdToEmail);
				finalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
				finalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
				finalAuditTrackDTO.setTo_datetime(datetime);
				finalAuditTrackDTO.setSign_cert(forwardBydata.get("filePath"));
				finalAuditTrackDTO.setRename_sign_cert(forwardBydata.get("renameFilePath"));
				finalAuditTrackDTO.setApp_ca_type(forwardBydata.get("check"));
				SmsIPFinalAuditTrackDTO id=finalAuditTrackRepository.save(finalAuditTrackDTO);
				if(id.getTrack_id()>0) {
					j=1;
				}
				log.info("Inside updateFinalAuditTrack method at " + new Date()+" j:::"+j);
			} else {
				SmsIPFinalAuditTrackDTO finalAuditTrackDTO = finalAuditTrackRepository.findByRegistrationno(forwardBydata.get("regNumber"));
				finalAuditTrackDTO.setStatus(forwardToData.get("status"));
				finalAuditTrackDTO.setToemail(fwdToEmail);
				finalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
				finalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
				finalAuditTrackDTO.setTo_datetime(datetime);
				finalAuditTrackDTO.setCa_sign_cert(forwardBydata.get("filePath"));
				finalAuditTrackDTO.setCa_rename_sign_cert(forwardBydata.get("renameFilePath"));
				finalAuditTrackDTO.setApp_ca_type(forwardBydata.get("check"));
				
				SmsIPFinalAuditTrackDTO id=finalAuditTrackRepository.save(finalAuditTrackDTO);
				if(id.getTrack_id()>0) {
					j=1;
				}
				log.info("Inside updateFinalAuditTrack method at " + new Date()+" j:::"+j);
			}
		} else {
			String fwdToEmail = forwardToData.get("forwardedToEmail").replace(",,", ",").replaceFirst("^,", "");
				if (role.equals(Constants.ROLE_CA)) {
					SmsIPFinalAuditTrackDTO finalAuditTrackDTO = finalAuditTrackRepository.findByRegistrationno(forwardBydata.get("regNumber"));
				finalAuditTrackDTO.setCaemail(forwardBydata.get("forwardedByEmail")); 
				finalAuditTrackDTO.setCa_mobile(forwardBydata.get("forwardedByMobile"));
				finalAuditTrackDTO.setCa_name(forwardBydata.get("forwardedByName"));
				finalAuditTrackDTO.setCa_ip("0:0:0:0:0:0:0:1");
				finalAuditTrackDTO.setCa_datetime(datetime);
				finalAuditTrackDTO.setCa_remarks( forwardToData.get("remarks"));
				finalAuditTrackDTO.setStatus(forwardToData.get("status"));
				finalAuditTrackDTO.setToemail(fwdToEmail);
				finalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
				finalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
				finalAuditTrackDTO.setApp_ca_type(forwardBydata.get("app_ca_type"));
				finalAuditTrackDTO.setApp_ca_path(forwardBydata.get("app_ca_path"));
				SmsIPFinalAuditTrackDTO id=finalAuditTrackRepository.save(finalAuditTrackDTO);
				if(id.getTrack_id()>0) {
					j=1;
				}
				log.info("Inside updateFinalAuditTrack method at " + new Date()+" j:::"+j);
			} else if (role.equals(Constants.ROLE_US)) {
				SmsIPFinalAuditTrackDTO finalAuditTrackDTO = finalAuditTrackRepository.findByRegistrationno(forwardBydata.get("regNumber"));
				finalAuditTrackDTO.setUs_email(forwardBydata.get("forwardedByEmail")); 
				finalAuditTrackDTO.setUs_mobile(forwardBydata.get("forwardedByMobile"));
				finalAuditTrackDTO.setUs_name(forwardBydata.get("forwardedByName"));
				finalAuditTrackDTO.setUs_ip("0:0:0:0:0:0:0:1");
				finalAuditTrackDTO.setUs_datetime(datetime);
				finalAuditTrackDTO.setUs_remarks( forwardToData.get("remarks"));
				finalAuditTrackDTO.setStatus(forwardToData.get("status"));
				finalAuditTrackDTO.setToemail(fwdToEmail);
				finalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
				finalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
				SmsIPFinalAuditTrackDTO id=finalAuditTrackRepository.save(finalAuditTrackDTO);
				if(id.getTrack_id()>0) {
					j=1;
				}
				log.info("Inside updateFinalAuditTrack method at " + new Date()+" j:::"+j);
			} else if (role.equals(Constants.ROLE_CO)) {
				SmsIPFinalAuditTrackDTO finalAuditTrackDTO = finalAuditTrackRepository.findByRegistrationno(forwardBydata.get("regNumber"));
				finalAuditTrackDTO.setCoordinatoremail(forwardBydata.get("forwardedByEmail")); 
				finalAuditTrackDTO.setCoordinator_mobile(forwardBydata.get("forwardedByMobile"));
				finalAuditTrackDTO.setCoordinator_name(forwardBydata.get("forwardedByName"));
				finalAuditTrackDTO.setCoordinator_ip("0:0:0:0:0:0:0:1");
				finalAuditTrackDTO.setCoordinator_datetime(datetime);
				finalAuditTrackDTO.setCoordinator_remarks( forwardToData.get("remarks"));
				finalAuditTrackDTO.setStatus(forwardToData.get("status"));
				finalAuditTrackDTO.setToemail(fwdToEmail);
				finalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
				finalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
				SmsIPFinalAuditTrackDTO id=finalAuditTrackRepository.save(finalAuditTrackDTO);
				if(id.getTrack_id()>0) {
					j=1;
				}
				log.info("Inside updateFinalAuditTrack method at " + new Date()+" j:::"+j);
			} else if (role.equals(Constants.ROLE_SUP)) {
				SmsIPFinalAuditTrackDTO finalAuditTrackDTO = finalAuditTrackRepository.findByRegistrationno(forwardBydata.get("regNumber"));
				finalAuditTrackDTO.setSupportemail(forwardBydata.get("forwardedByEmail")); 
				finalAuditTrackDTO.setSupport_mobile(forwardBydata.get("forwardedByMobile"));
				finalAuditTrackDTO.setSupport_name(forwardBydata.get("forwardedByName"));
				finalAuditTrackDTO.setSupport_ip("0:0:0:0:0:0:0:1");
				finalAuditTrackDTO.setSupport_datetime(datetime);
				finalAuditTrackDTO.setSupport_remarks( forwardToData.get("remarks"));
				finalAuditTrackDTO.setStatus(forwardToData.get("status"));
				finalAuditTrackDTO.setToemail(fwdToEmail);
				finalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
				finalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
				SmsIPFinalAuditTrackDTO id=finalAuditTrackRepository.save(finalAuditTrackDTO);
				if(id.getTrack_id()>0) {
					j=1;
				}
				log.info("Inside updateFinalAuditTrack method at " + new Date()+" j:::"+j);
			} else if (role.equals(Constants.ROLE_DA)) {
				SmsIPFinalAuditTrackDTO finalAuditTrackDTO = finalAuditTrackRepository.findByRegistrationno(forwardBydata.get("regNumber"));
				finalAuditTrackDTO.setDaemail(forwardBydata.get("forwardedByEmail")); 
				finalAuditTrackDTO.setDa_mobile(forwardBydata.get("forwardedByMobile"));
				finalAuditTrackDTO.setDa_name(forwardBydata.get("forwardedByName"));
				finalAuditTrackDTO.setDa_ip("0:0:0:0:0:0:0:1");
				finalAuditTrackDTO.setDa_datetime(datetime);
				finalAuditTrackDTO.setDa_remarks( forwardToData.get("remarks"));
				finalAuditTrackDTO.setStatus(forwardToData.get("status"));
				finalAuditTrackDTO.setToemail(fwdToEmail);
				finalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
				finalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
				SmsIPFinalAuditTrackDTO id=finalAuditTrackRepository.save(finalAuditTrackDTO);
				if(id.getTrack_id()>0) {
					j=1;
				}
				log.info("Inside updateFinalAuditTrack method at " + new Date()+" j:::"+j);
			} else if (role.equals(Constants.ROLE_MAILADMIN)) {
				SmsIPFinalAuditTrackDTO finalAuditTrackDTO = finalAuditTrackRepository.findByRegistrationno(forwardBydata.get("regNumber"));
				finalAuditTrackDTO.setAdminemail(forwardBydata.get("forwardedByEmail")); 
				finalAuditTrackDTO.setAdmin_mobile(forwardBydata.get("forwardedByMobile"));
				finalAuditTrackDTO.setAdmin_name(forwardBydata.get("forwardedByName"));
				finalAuditTrackDTO.setAdmin_ip("0:0:0:0:0:0:0:1");
				finalAuditTrackDTO.setAdmin_datetime(datetime);
				finalAuditTrackDTO.setAdmin_remarks( forwardToData.get("remarks"));
				finalAuditTrackDTO.setStatus(forwardToData.get("status"));
				finalAuditTrackDTO.setToemail(fwdToEmail);
				finalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
				finalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
				SmsIPFinalAuditTrackDTO id=finalAuditTrackRepository.save(finalAuditTrackDTO);
				if(id.getTrack_id()>0) {
					j=1;
				}
				log.info("Inside updateFinalAuditTrack method at " + new Date()+" j:::"+j);
			}
		}
		return j;
	}
	
	public int updateAppType(Map<String, String> forwardBydata, Map<String, String> forwardToData) {
		log.info("Inside updateAppType method at " + new Date());
		int i=0,j=0;
		Date date = new Date();
		DateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String pdate = dt.format(date);
		 log.info("Inside updateAppType method at " + new Date()+" role:::"+forwardBydata.get("role"));
		   if (forwardBydata.get("role").equals(Constants.ROLE_USER)) {
			   SmsIPBaseDTO smsIPBaseDTO = smsIPBaseRepository.findByRegistrationno(forwardBydata.get("regNumber"));
			   log.info("Inside updateAppType method at " + new Date()+" forwardBaseDTO:::"+smsIPBaseDTO);
			   smsIPBaseDTO.setPdf_path(forwardBydata.get("filePath"));
			   SmsIPBaseDTO id=smsIPBaseRepository.save(smsIPBaseDTO);
				if(id.getId()>0) {
					i=1;
				}
				 log.info("Inside updateAppType method at " + new Date()+" i:::"+i);
		   } else if (forwardBydata.get("role").equals(Constants.ROLE_CA)) {
               if (forwardBydata.get("check").equalsIgnoreCase("upload_scanned")) {
            	   SmsIPBaseDTO smsIPBaseDTO = smsIPBaseRepository.findByRegistrationno(forwardBydata.get("regNumber"));
            	   smsIPBaseDTO.setCa_sign_cert(forwardBydata.get("filePath"));
            	   smsIPBaseDTO.setCa_rename_sign_cert(forwardBydata.get("renameFilePath"));
   				SmsIPBaseDTO id=smsIPBaseRepository.save(smsIPBaseDTO);
   				if(id.getId()>0) {
   					i=1;
   				}
   			 log.info("Inside updateAppType method at " + new Date()+" i:::"+i);
               } 
           }
		return i;
	}
	
	public int updateStatusTable(Map<String, String> forwardBydata, Map<String, String> forwardToData) {
		 log.info("Inside updateStatusTable method at " + new Date());
		int j=0;
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        Date dt = new Date();
        String datetime = format.format(dt);
        String ip="0.0.0.0.0.0.0.1";
        SmsIPStatusDTO statusDTO=new SmsIPStatusDTO();
        try {
			BeanUtils.populate(statusDTO, forwardBydata);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
        statusDTO.setStat_form_type(forwardBydata.get("formType"));
		statusDTO.setStatregno(forwardBydata.get("regNumber"));
		statusDTO.setStat_type(forwardToData.get("status"));
		statusDTO.setStatforwardedby(forwardBydata.get("forwardedBy"));
		statusDTO.setStat_forwarded_by_user(forwardBydata.get("forwardedByEmail"));
		statusDTO.setStatforwardedto(forwardToData.get("forwardedTo"));
		statusDTO.setStat_forwarded_to_user(forwardToData.get("forwardedToEmail"));
		statusDTO.setStat_remarks(forwardToData.get("remarks"));
		statusDTO.setStat_ip(ip);
		statusDTO.setStat_forwarded_by_email(forwardBydata.get("forwardedByEmail"));
		statusDTO.setStat_forwarded_by_mobile(forwardBydata.get("forwardedByMobile"));
		statusDTO.setStat_forwarded_by_name(forwardBydata.get("forwardedByName"));
		statusDTO.setStat_forwarded_by_ip(ip);
		statusDTO.setStat_forwarded_by_datetime(datetime);
		statusDTO.setStat_final_id("");
		statusDTO.setStat_on_hold("n");
		SmsIPStatusDTO i=statusRepository.save(statusDTO);
        if(i.getStatid()>0) {
        	j=1;
        }
        log.info("Inside updateStatusTable method at " + new Date()+"  j:::"+j);
		return j;
	}
	
	public String fetchPunjabNodalOfficers(String district) {
		 log.info("Inside fetchPunjabNodalOfficers method at " + new Date());
		String co_email = "", email = "";
		//email = forwardBaseRepository.fetchPunjabNodalOfficers(district);
		if (co_email.isEmpty()) {
			co_email = email;
		} else {
			co_email += "," + email;
		}
		 log.info("Inside fetchPunjabNodalOfficers method at " + new Date()+"  co_email:::"+co_email);
		return co_email;
	}
	
	public String fetchPunjabDA(String employment, String ministry, String department) {
		log.info("Inside fetchPunjabDA method at " + new Date());
		String da_email = "";
		da_email = fetchPunjabDA(employment, ministry, department);
		if (!da_email.contains(",")) {
			da_email = da_email;
		} else {
			da_email += "," + da_email;
		}
		if (da_email.isEmpty()) {
			da_email = fetchPunjabDA(employment, ministry, "Government of Punjab");
			if (!da_email.contains(",")) {
				da_email = da_email;
			} else {
				da_email += "," + da_email;
			}
		}
		log.info("Inside fetchPunjabDA method at " + new Date()+" da_email:::"+da_email);
		return da_email;
	}
	
	public static Set<String> convertStringToSet(String str) {
		log.info("Inside convertStringToSet method at " + new Date());
		Set<String> elements = new HashSet<>();
		if (!str.isEmpty()) {
			String[] arr = null;
			arr = str.split(",");
			for (int i = 0; i < arr.length; i++) {
				elements.add(arr[i]);
			}
		}
		log.info("Inside convertStringToSet method at " + new Date()+" elements:::"+elements);
		return elements;
	}
	
	public Map<String, String> fetchEmailDetails(String employment, String ministry, String department) {
		log.info("Inside SMSForwardDAO: fetchEmailDetails method at " + new Date());
		Map<String, String> map = null;
		SmsIPEmpCoordDTO beanlist = empCoordRepository.findByEmpcategoryAndEmpminstateorgAndEmpdeptAndEmpstatus(employment, ministry, department,"a");
		ObjectMapper m = new ObjectMapper();
		map = m.convertValue(beanlist, Map.class);
		log.info("Inside fetchEmailDetails method at " + new Date()+" map:::"+map);
	return map;
	}
	
	public Set<String> removeSupportFromSet(Set<String> coordinators) {
		log.info("Inside removeSupportFromSet method at " + new Date());
		Set<String> coords = new HashSet<>();
		for (String coord : coordinators) {
			if (!verifySupport(coord)) {
				coords.add(coord);
			}
		}
		log.info("Inside removeSupportFromSet method at " + new Date()+" coords:::"+coords);
		return coords;
	}
	
	public boolean verifySupport(String coordinators) {
		log.info("Inside verifySupport method at " + new Date() +" coordinators:::"+coordinators);
		if (coordinators.contains("support@nic.in") || coordinators.contains("support@gov.in")
				|| coordinators.contains("support@dummy.nic.in") || coordinators.contains("vpnsupport@nic.in")
				|| coordinators.contains("smssupport@gov.in")) {
			return true;
		}
		return false;
	}
	
	public Map<String, String> fetchEmpDetails(String regNo) {
		log.info("Inside fetchEmpDetails method at " + new Date());
		Map<String, String> maplist=null;
		
		SmsIPBaseDTO smsIPBaseDTO=smsIPBaseRepository.findByRegistrationno(regNo);
		ObjectMapper m = new ObjectMapper();
		maplist = m.convertValue(smsIPBaseDTO, Map.class);
		log.info("Inside fetchEmpDetails method at " + new Date()+" maplist:::"+maplist);
		return maplist;
	}
		//********************************End of Approve/Forward******************************
		
		
		//********************************Start of Reject**************************************
		public boolean updateTrackTable(Map<String, String> forwardBydata, Map<String, String> forwardToData) {
			log.info("Inside updateTrackTable method at " + new Date());
			boolean retStatus = false;
			SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		        Date dt = new Date();
		        String datetime = format.format(dt);
		        String role = forwardBydata.get("role");
		        log.info("Inside updateTrackTable method at " + new Date()+" role:::"+role);
		        String fwdToEmail = forwardToData.get("forwardedToEmail").replace(",,", ",").replaceFirst("^,", "");
		        log.info("Inside updateTrackTable method at " + new Date()+" fwdToEmail:::"+fwdToEmail);
		        int j = 0, k = 0;
		        if (role.equals(Constants.ROLE_USER)) {
		        	SmsIPFinalAuditTrackDTO finalAuditTrackDTO = finalAuditTrackRepository.findByRegistrationno(forwardBydata.get("regNumber"));
		        	finalAuditTrackDTO.setApplicantemail(forwardBydata.get("forwardedByEmail")); 
		        	finalAuditTrackDTO.setApplicant_mobile(forwardBydata.get("forwardedByMobile"));
		        	finalAuditTrackDTO.setApplicant_name(forwardBydata.get("forwardedByName"));
		        	finalAuditTrackDTO.setApplicant_ip("0:0:0:0:0:0:0:1");
		        	finalAuditTrackDTO.setApplicant_datetime(datetime);
		        	finalAuditTrackDTO.setApplicant_remarks( forwardToData.get("remarks"));
		        	finalAuditTrackDTO.setStatus(forwardToData.get("status"));
		        	finalAuditTrackDTO.setToemail(fwdToEmail);
		        	finalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
		        	finalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
		        	SmsIPFinalAuditTrackDTO id=finalAuditTrackRepository.save(finalAuditTrackDTO);
					if(id.getTrack_id()>0) {
						j=1;
					}
					  log.info("Inside updateTrackTable method at " + new Date()+" j:::"+j);
		        }else if (role.equals(Constants.ROLE_CA)) {
		        	SmsIPFinalAuditTrackDTO finalAuditTrackDTO = finalAuditTrackRepository.findByRegistrationno(forwardBydata.get("regNumber"));
		        	finalAuditTrackDTO.setCaemail(forwardBydata.get("forwardedByEmail")); 
		        	finalAuditTrackDTO.setCa_mobile(forwardBydata.get("forwardedByMobile"));
		        	finalAuditTrackDTO.setCa_name(forwardBydata.get("forwardedByName"));
		        	finalAuditTrackDTO.setCa_ip("0:0:0:0:0:0:0:1");
		        	finalAuditTrackDTO.setCa_datetime(datetime);
		        	finalAuditTrackDTO.setCa_remarks( forwardToData.get("remarks"));
		        	finalAuditTrackDTO.setStatus(forwardToData.get("status"));
		        	finalAuditTrackDTO.setToemail(fwdToEmail);
		        	finalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
		        	finalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
		        	SmsIPFinalAuditTrackDTO id=finalAuditTrackRepository.save(finalAuditTrackDTO);
					if(id.getTrack_id()>0) {
						j=1;
					}
					 log.info("Inside updateTrackTable method at " + new Date()+" j:::"+j);
		        } else if (role.equals(Constants.ROLE_US)) {
		        	SmsIPFinalAuditTrackDTO finalAuditTrackDTO = finalAuditTrackRepository.findByRegistrationno(forwardBydata.get("regNumber"));
		        	finalAuditTrackDTO.setUs_email(forwardBydata.get("forwardedByEmail")); 
		        	finalAuditTrackDTO.setUs_mobile(forwardBydata.get("forwardedByMobile"));
		        	finalAuditTrackDTO.setUs_name(forwardBydata.get("forwardedByName"));
		        	finalAuditTrackDTO.setUs_ip("0:0:0:0:0:0:0:1");
		        	finalAuditTrackDTO.setUs_datetime(datetime);
		        	finalAuditTrackDTO.setUs_remarks( forwardToData.get("remarks"));
		        	finalAuditTrackDTO.setStatus(forwardToData.get("status"));
		        	finalAuditTrackDTO.setToemail(fwdToEmail);
		        	finalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
		        	finalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
		        	SmsIPFinalAuditTrackDTO id=finalAuditTrackRepository.save(finalAuditTrackDTO);
					if(id.getTrack_id()>0) {
						j=1;
					}
					 log.info("Inside updateTrackTable method at " + new Date()+" j:::"+j);
		        } else if (role.equals(Constants.ROLE_CO)) {
		        	SmsIPFinalAuditTrackDTO finalAuditTrackDTO = finalAuditTrackRepository.findByRegistrationno(forwardBydata.get("regNumber"));
		        	finalAuditTrackDTO.setCoordinatoremail(forwardBydata.get("forwardedByEmail")); 
		        	finalAuditTrackDTO.setCoordinator_mobile(forwardBydata.get("forwardedByMobile"));
		        	finalAuditTrackDTO.setCoordinator_name(forwardBydata.get("forwardedByName"));
		        	finalAuditTrackDTO.setCoordinator_ip("0:0:0:0:0:0:0:1");
		        	finalAuditTrackDTO.setCoordinator_datetime(datetime);
		        	finalAuditTrackDTO.setCoordinator_remarks( forwardToData.get("remarks"));
		        	finalAuditTrackDTO.setStatus(forwardToData.get("status"));
		        	finalAuditTrackDTO.setToemail(fwdToEmail);
		        	finalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
		        	finalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
		        	SmsIPFinalAuditTrackDTO id=finalAuditTrackRepository.save(finalAuditTrackDTO);
					if(id.getTrack_id()>0) {
						j=1;
					}
					 log.info("Inside updateTrackTable method at " + new Date()+" j:::"+j);
		        } else if (role.equals(Constants.ROLE_SUP)) {
		        	
		        	SmsIPFinalAuditTrackDTO finalAuditTrackDTO = finalAuditTrackRepository.findByRegistrationno(forwardBydata.get("regNumber"));
		        	finalAuditTrackDTO.setSupportemail(forwardBydata.get("forwardedByEmail")); 
		        	finalAuditTrackDTO.setSupport_mobile(forwardBydata.get("forwardedByMobile"));
		        	finalAuditTrackDTO.setSupport_name(forwardBydata.get("forwardedByName"));
		        	finalAuditTrackDTO.setSupport_ip("0:0:0:0:0:0:0:1");
		        	finalAuditTrackDTO.setSupport_datetime(datetime);
		        	finalAuditTrackDTO.setSupport_remarks( forwardToData.get("remarks"));
		        	finalAuditTrackDTO.setStatus(forwardToData.get("status"));
		        	finalAuditTrackDTO.setToemail(fwdToEmail);
		        	finalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
		        	finalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
		        	SmsIPFinalAuditTrackDTO id=finalAuditTrackRepository.save(finalAuditTrackDTO);
					if(id.getTrack_id()>0) {
						j=1;
					}
					 log.info("Inside updateTrackTable method at " + new Date()+" j:::"+j);
		        } else if (role.equals(Constants.ROLE_DA)) {
		        	
		        	SmsIPFinalAuditTrackDTO finalAuditTrackDTO = finalAuditTrackRepository.findByRegistrationno(forwardBydata.get("regNumber"));
		        	finalAuditTrackDTO.setDaemail(forwardBydata.get("forwardedByEmail")); 
		        	finalAuditTrackDTO.setDa_mobile(forwardBydata.get("forwardedByMobile"));
		        	finalAuditTrackDTO.setDa_name(forwardBydata.get("forwardedByName"));
		        	finalAuditTrackDTO.setDa_ip("0:0:0:0:0:0:0:1");
		        	finalAuditTrackDTO.setDa_datetime(datetime);
		        	finalAuditTrackDTO.setDa_remarks( forwardToData.get("remarks"));
		        	finalAuditTrackDTO.setStatus(forwardToData.get("status"));
		        	finalAuditTrackDTO.setToemail(fwdToEmail);
		        	finalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
		        	finalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
		        	SmsIPFinalAuditTrackDTO id=finalAuditTrackRepository.save(finalAuditTrackDTO);
					if(id.getTrack_id()>0) {
						j=1;
					}
					 log.info("Inside updateTrackTable method at " + new Date()+" j:::"+j);
		        }else if (role.equals(Constants.ROLE_MAILADMIN)) {
		        	SmsIPFinalAuditTrackDTO finalAuditTrackDTO = finalAuditTrackRepository.findByRegistrationno(forwardBydata.get("regNumber"));
		        	finalAuditTrackDTO.setAdminemail(forwardBydata.get("forwardedByEmail")); 
		        	finalAuditTrackDTO.setAdmin_mobile(forwardBydata.get("forwardedByMobile"));
		        	finalAuditTrackDTO.setAdmin_name(forwardBydata.get("forwardedByName"));
		        	finalAuditTrackDTO.setAdmin_ip("0:0:0:0:0:0:0:1");
		        	finalAuditTrackDTO.setAdmin_datetime(datetime);
		        	finalAuditTrackDTO.setAdmin_remarks( forwardToData.get("remarks"));
		        	finalAuditTrackDTO.setStatus(forwardToData.get("status"));
		        	finalAuditTrackDTO.setToemail(fwdToEmail);
		        	finalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
		        	finalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
		        	SmsIPFinalAuditTrackDTO id=finalAuditTrackRepository.save(finalAuditTrackDTO);
					if(id.getTrack_id()>0) {
						j=1;
					}
					 log.info("Inside updateTrackTable method at " + new Date()+" j:::"+j);
		        }
		        SmsIPStatusDTO statusDTO =new SmsIPStatusDTO();
		        statusDTO.setStat_forwarded_by_email(forwardBydata.get("forwardedByEmail")); 
		        statusDTO.setStat_forwarded_by_mobile(forwardBydata.get("forwardedByMobile"));
		        statusDTO.setStat_forwarded_by_name(forwardBydata.get("forwardedByName"));
		        statusDTO.setStat_ip("0:0:0:0:0:0:0:1");
		        statusDTO.setStat_createdon(datetime);
		        statusDTO.setStat_remarks( forwardToData.get("remarks"));
		        statusDTO.setStat_type(forwardToData.get("status"));
		       // statusDTO.setStatforwardedto(fwdToEmail);
		        statusDTO.setStatforwardedto(forwardToData.get("forwardedTo"));
		        statusDTO.setStatregno(forwardBydata.get("regNumber"));
		        statusDTO.setStat_on_hold("n");
		        SmsIPStatusDTO id=statusRepository.save(statusDTO);
				if(id.getStatid()>0) {
					k=1;
				}
				 log.info("Inside updateTrackTable method at " + new Date()+" k:::"+k);
		        	if(k>0) {
		        		retStatus = true;
		        	}
		        	return retStatus;
		    }
		//********************************End of Reject************************************************
		
		
		
		//********************************Start of Track User******************************************
		public HashMap<String, Object> fetchTrackDetails(String registration_no) {
			log.info("Entering fetchTrackDetails method at info " + new Date() +" registration_no::::"+registration_no);
			HashMap<String, Object> hmTrack = new HashMap<>();
			List<SmsIPStatusDTO> arrRoles = statusListRepository.findByStatregno(registration_no);
			List<SmsIPFinalAuditTrackDTO> currentDetails = finalAuditTrackListRepository.findByRegistrationno(registration_no);
			hmTrack.put("arrRoles", arrRoles);
			hmTrack.put("currentDetails", currentDetails);
			log.info("Entering fetchTrackDetails method at info " + new Date() +" hmTrack::::"+hmTrack);
			return hmTrack;
		}

		
		public Map<String, Object> fetchTrackByRole(String registration_no, String forward, String trole, String srole) {
			log.info("Entering fetchTrackByRole method at info " + new Date() +" registration_no::::"+registration_no+"  forward:::"+forward+"  trole:::"+trole+" srole:::"+srole);
			Map<String, Object> trackData = new HashMap<>();
			Map<String, String> fetchstatus = new HashMap<>();
			List<SmsIPStatusDTO> iterable = null;
			String recv_date = "";
			if (trole.equalsIgnoreCase("undefined")) {
				iterable = statusRepository.findFirstByStatregnoAndStatforwardedtoOrderByStatid(registration_no, trole);
			} else if (srole.equalsIgnoreCase("null") && trole.equalsIgnoreCase("null")) {
				iterable = statusRepository.findByStatregnoAndStatforwardedbyIsNullAndStatforwardedtoIsNullOrderByStatid(registration_no);
			} else if (trole.equalsIgnoreCase("null")) {
				iterable = statusRepository.findByStatregnoAndStatforwardedbyAndStatforwardedtoIsNullOrderByStatid(registration_no, srole);
			} else {
				iterable = statusRepository.findByStatregnoAndStatforwardedbyAndStatforwardedto(registration_no, srole, trole);
			}
			log.info("Entering fetchTrackByRole method at info " + new Date() +" iterable::::"+iterable);
			for (SmsIPStatusDTO statusDTO : iterable) {
				String remarks = statusDTO.getStat_remarks();
				if (remarks == null) {
					remarks = "";
				}
				recv_date = statusDTO.getStat_createdon();
				String current_user = statusDTO.getStat_forwarded_by_email();
				if (current_user == null) {
					current_user = "User Emaillll";
					//current_user = userdata.getEmail();
				}
				String forwarder = commonUtility.findRole(forward);
				String sender_details = forwarder + "&ensp;(" + current_user + ")";
				if (!forward.isEmpty()) {
					fetchstatus = (Map<String, String>) statusRepository.findByStatregnoAndStatforwardedbyOrStatforwardedto(registration_no, forward, srole);
					recv_date = fetchstatus.get("recv_date");
					String stat_forwarded_by_email = fetchstatus.get("stat_forwarded_by_email");
					sender_details = forwarder + "&ensp;(" + stat_forwarded_by_email + ")";
				}
				trackData.put("remarks", remarks);
				trackData.put("recv_date", recv_date);
				trackData.put("current_user", current_user);
				trackData.put("recv_email", statusDTO.getStat_forwarded_to_user());
				trackData.put("stat_process", statusDTO.getStat_process());
				trackData.put("stat_type", statusDTO.getStat_type());
				trackData.put("stat_on_hold", statusDTO.getStat_on_hold());
				trackData.put("recv_date", recv_date);
				trackData.put("sender_details", sender_details);
			}
			log.info("Entering fetchTrackByRole method at info " + new Date() +" trackData::::"+trackData);
			return trackData;
		}
		//********************************End of Track User******************************************
		
		
		
		//********************************Start of Raise Query****************************************
		public  Map<String, Object>  raiseQuery(String regNo, String role, String uemail,String choose_recp,String to_email,String statRemarks ) {
			log.info("Entering raiseQuery method at info " + new Date() );
			int i=0;
			Map<String, Object> listmap=null;
			 ArrayList<String> roles = new ArrayList<>();
			 Map<String, Object> map=fetchRaiseQueryData(regNo, role);
			  System.out.println("map:::::::"+map);
			 Set<String> recpdata = (Set<String>) map.get("recpdata");
		        for (String string : recpdata) {
		            if (string.contains("=>")) {
		                String arr[] = string.split("=>");
		                roles.add(arr[0]);
		            }
		        }
		        roles.add("u");
		        log.info("Entering raiseQuery method at info " + new Date() +" role::::"+role);
		        String fetchRole=commonUtility.fetchRole(role);
		        log.info("Entering raiseQuery method at info " + new Date() +" fetchRole::::"+fetchRole);
		        
		        if (roles.contains(choose_recp) && !fetchRole.equals(choose_recp)) {
		        	
		        	String qr_form_type="ldap";
		        	String qr_reg_no=regNo;
		        	String qr_forwarded_by=fetchRole;
		        	String qr_forwarded_by_user=uemail;
		        	String qr_forwarded_to=choose_recp;
		        	String qr_forwarded_to_user=to_email;
		        	String qr_message=statRemarks;
		        	
		        	SmsIPQueryRaiseDTO queryRaiseDTO = new SmsIPQueryRaiseDTO();
		        	queryRaiseDTO.setQr_form_type(qr_form_type);
		        	queryRaiseDTO.setQrregno(qr_reg_no);
		        	queryRaiseDTO.setQr_forwarded_by(qr_forwarded_by);
		        	queryRaiseDTO.setQr_forwarded_by_user(qr_forwarded_by_user);
		        	queryRaiseDTO.setQr_forwarded_to(qr_forwarded_to);
		        	queryRaiseDTO.setQr_forwarded_to_user(qr_forwarded_to_user);
		        	queryRaiseDTO.setQr_message(qr_message);
		        	SmsIPQueryRaiseDTO id=queryRaiseRepository.save(queryRaiseDTO);
						if(id.getQr_id()>0) {
							 listmap=fetchRaiseQueryData(qr_reg_no,role);
							i=1;
						}
		        }
		        log.info("Entering raiseQuery method at info " + new Date() +" listmap::::"+listmap);
			return listmap;
		}
		
		
		 Set<String> fetchAllStakeHolders(String regNo, String role) {
			 log.info("Entering fetchAllStakeHolders method at info " + new Date());
			 Set<String> recpdata = new HashSet<>();
			List<SmsIPStatusDTO> statusDTO=statusListRepository.findByStatregno(regNo);
				ObjectMapper m = new ObjectMapper();
				log.info("Entering fetchAllStakeHolders method at info " + new Date() +"  queryRaiseStatusDTO:::"+statusDTO);
				for(SmsIPStatusDTO fetchStatus1:statusDTO) {
				 log.info("Entering fetchAllStakeHolders method at info " + new Date() +"  fetchStatus1:::"+fetchStatus1);
				 String stat_type=fetchStatus1.getStat_type();
				 log.info("Entering fetchAllStakeHolders method at info " + new Date() +"  stat_type:::"+stat_type);
				 String stat_forwarded_to_user=fetchStatus1.getStat_forwarded_to_user();
				 log.info("Entering fetchAllStakeHolders method at info " + new Date() +"  stat_forwarded_to_user:::"+stat_forwarded_to_user);
			 recpdata.add("u");
	         switch (stat_type) {
	             case "ca_pending":
	                 if (!role.equals(Constants.ROLE_CA)) {
	                     recpdata.add("ca=>" + stat_forwarded_to_user);
	                 }
	                 break;
	             case "support_pending":
	                 if (!role.equals(Constants.ROLE_SUP)) {
	                     recpdata.add("s=>" + stat_forwarded_to_user);
	                 }
	                 break;
	             case "co_pending":
	                 if (!role.equals(Constants.ROLE_CO)) {
	                     recpdata.add("c=>" + stat_forwarded_to_user);
	                 }
	                 break;
	             case "da_pending":
	                 if (!role.equals(Constants.ROLE_DA)) {
	                     recpdata.add("d=>" + stat_forwarded_to_user);
	                 }
	                 break;
	             case "mail-admin_pending":
	                 if (!role.equals(Constants.ROLE_MAILADMIN)) {
	                     recpdata.add("m=>" + stat_forwarded_to_user);
	                 }
	                 break;
	         } }
			 return recpdata;
		 }
		 
		 
		 public Map<String, Object> fetchRaiseQueryData(String regNo, String role) {
			 log.info("Entering fetchRaiseQueryData method at info " + new Date() );
				Map<String, Object> details = new HashMap<>();
				Set<String> recpdata = fetchAllStakeHolders(regNo, role);
				List<SmsIPQueryRaiseDTO> queryRaiseDTO=queryRaiseRepository.findByQrregno(regNo);
				 log.info("Entering fetchRaiseQueryData method at info " + new Date() +" recpdata:::"+recpdata);
				 log.info("Entering fetchRaiseQueryData method at info " + new Date() +" queryRaiseDTO:::"+queryRaiseDTO);
				details.put("raiseQueryBtn", true);
				details.put("recpdata", recpdata);
				details.put("querydata", queryRaiseDTO);
//				in case of fetchMap
//				 if (qr_forwarded_by_user.equals(uemail)) {
//		             account_holder = true;
//		         }
				 log.info("Entering fetchRaiseQueryData method at info " + new Date() +" details:::"+details);
				return details;
			}
		//********************************End of Raise Query******************************************
		
		
		
		//********************************Start of Put On/Off Hold************************************
		 public boolean putOnHold(String regNo, String on_hold, String statRemarks) {
				log.info("Inside putOnHold method at " + new Date());
				int i = 0, j = 0;
				boolean flag=false;
				SmsIPStatusDTO statusDTO = statusRepository.findByStatregno(regNo);
				statusDTO.setStat_on_hold(on_hold);
				SmsIPStatusDTO id1=statusRepository.save(statusDTO);
					if(id1.getStatid()>0) {
						i=1;
					}
				SmsIPFinalAuditTrackDTO finalAuditTrackDTO = finalAuditTrackRepository.findByRegistrationno(regNo);
				finalAuditTrackDTO.setOn_hold(on_hold);
				finalAuditTrackDTO.setHold_remarks(statRemarks);
				SmsIPFinalAuditTrackDTO id2=finalAuditTrackRepository.save(finalAuditTrackDTO);
					if(id2.getTrack_id()>0) {
						j=1;
					}
					log.info("Inside putOnHold method at " + new Date()+" j:::"+j);
					log.info("Inside putOnHold method at " + new Date()+" i:::"+i);
				if (i > 0 && j > 0) {
		             flag = true;
		         }
				return flag;
			}
		//********************************End of Put On/Off Hold******************************************
		
		 
		
		//********************************Start of Generate PDF*******************************************
			public SmsIPBaseDTO findByRegno(String regid) {
				log.info("Entering findByRegno method at info " + new Date() +" regid::::"+regid);
				List<SmsIPBaseDTO> iterable = smsIPBaseListRepository.findByRegistrationno(regid);
				for (SmsIPBaseDTO list : iterable) {
					log.info("Entering findByRegno method at info " + new Date() +" list::::"+list);
					return list;
				}
				return null;
			}
		//********************************End of Generate PDF*********************************************
		
		
			
		//********************************Start of Upload PDF********************************************
			public SmsIPDocUploadDTO saveDocx(SmsIPDocUploadDTO docUploadDTO) {
				log.info("Entering saveDocx method at info " + new Date() +" uploadfiles::::"+docUploadDTO);
				SmsIPDocUploadDTO docUpload = docUploadRepository.save(docUploadDTO);
				log.info("Entering saveDocx method at info " + new Date() +" uploadfilesdto::::"+docUpload);
				return docUploadDTO;
			}
		//********************************End of Upload PDF*******************************************
		
		
			
		
		//********************************Start of Download PDF**************************************

		//********************************End of Download PDF****************************************
		
		
		
		
		//********************************Start of View PDF******************************************
			public Map<String, Map<String, String>> viewDocx(String regid, String role) {
				log.info("Inside viewDocx method at " + new Date() +"  regid:::"+regid+"  role:::"+role);
				Map<String, String> file = new HashMap<>();
				Map<String,Map<String,String>> filelist = new HashMap<>();
				//For esign and manual
				String pdf="";
				List<SmsIPBaseDTO> list = smsIPBaseListRepository.findByRegistrationno(regid);
				log.info("Inside viewDocx method at " + new Date() +"  list:::"+list);
				for(SmsIPBaseDTO dto:list) {
					if(dto.getPdf_path().toLowerCase().contains("esign"))
						pdf=regid+".pdf (Esigned file)";
					else if(dto.getPdf_path().toLowerCase().contains("manual"))
						pdf=regid+".pdf (Scanned file)";
					else 
						pdf=regid+".pdf";
					if(role!=null && !role.isEmpty())
						role=role.toLowerCase();
					
					HashMap<String,String> filepath = new HashMap<>();
					switch(role) {
					case "user":
						if(dto.getRename_sign_cert()!=null&& !dto.getRename_sign_cert().isEmpty())
							file.put(pdf, dto.getRename_sign_cert());
						break;
					case "ca":
						if(dto.getCa_rename_sign_cert()!=null && !dto.getCa_rename_sign_cert().isEmpty()){
							List<SmsIPFinalAuditTrackDTO> l = finalAuditTrackListRepository.findByRegistrationno(regid);
							for(SmsIPFinalAuditTrackDTO d:l) {
								filepath.put("esignedpdf", d.getCa_rename_sign_cert());
							}
							if(filepath!=null)
								file.put(regid+".pdf", filepath.get("esignedpdf"));
						}	
						break;
					
					}
				}
				List<SmsIPDocUploadDTO> iterable = docUploadRepository.findByRegistrationnoAndRole(regid,role);
				for(SmsIPDocUploadDTO dto:iterable) {
					file.put(dto.getDoc(), dto.getDocpath());
					//to show view original name
					//file.put("viewname for "+dto.getDoc(), dto.getOriginal_filename());
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
				 if (file.size() > 0)
				 filelist.put(roleName, file);
				 log.info("Inside viewDocx method at " + new Date() +"  filelist:::"+filelist);
				return filelist;
			}
		//********************************End of View PDF********************************

}
