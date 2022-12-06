package in.nic.eform.ldapip.dao;

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
import in.nic.eform.ldapip.bean.LdapIPFormBean;
import in.nic.eform.ldapip.dto.LdapIPBaseDTO;
import in.nic.eform.ldapip.dto.LdapIPDocUploadDTO;
import in.nic.eform.ldapip.dto.LdapIPEmpCoordDTO;
import in.nic.eform.ldapip.dto.LdapIPFinalAuditTrackDTO;
import in.nic.eform.ldapip.dto.LdapIPMailadminFormsDTO;
import in.nic.eform.ldapip.dto.LdapIPQueryRaiseDTO;
import in.nic.eform.ldapip.dto.LdapIPStatusDTO;
import in.nic.eform.ldapip.repository.LdapIPBaseListRepository;
import in.nic.eform.ldapip.repository.LdapIPBaseRepository;
import in.nic.eform.ldapip.repository.LdapIPDocUploadRepo;
import in.nic.eform.ldapip.repository.LdapIPEmpCoordListRepo;
import in.nic.eform.ldapip.repository.LdapIPEmpCoordRepo;
import in.nic.eform.ldapip.repository.LdapIPFinalAuditTrackListRepo;
import in.nic.eform.ldapip.repository.LdapIPFinalAuditTrackRepo;
import in.nic.eform.ldapip.repository.LdapIPMailadminFormsRepo;
import in.nic.eform.ldapip.repository.LdapIPQueryRaiseRepo;
import in.nic.eform.ldapip.repository.LdapIPStatusListRepo;
import in.nic.eform.ldapip.repository.LdapIPStatusRepo;
import in.nic.eform.utility.ApiUtility;
import in.nic.eform.utility.CommonUtility;
import in.nic.eform.utility.Constants;

@Repository
public class LdapIPDao {
	private static final Logger log = LoggerFactory.getLogger(LdapIPDao.class);
	@Autowired
	LdapIPBaseRepository ldapIPBaseRepository;
	@Autowired
	LdapIPBaseListRepository ldapIPBaseListRepository;
	@Autowired
	LdapIPFinalAuditTrackRepo ldapIPfinalAuditTrackRepository;
	@Autowired
	LdapIPFinalAuditTrackListRepo ldapIPfinalAuditTrackListRepository;
	@Autowired
	LdapIPStatusRepo ldapIPstatusRepository;
	@Autowired
	LdapIPStatusListRepo ldapIPstatusListRepository;
	@Autowired
	LdapIPMailadminFormsRepo ldapIPmailadminFormsRepository;
	@Autowired 
	LdapIPEmpCoordRepo ldapIPempCoordRepository;
	@Autowired
	LdapIPEmpCoordListRepo ldapIPempCoordListRepository;
	@Autowired
	LdapIPQueryRaiseRepo ldapIPqueryRaiseRepository;
	@Autowired
	LdapIPDocUploadRepo ldapIPdocUploadRepository;
	@Autowired
	CommonUtility commonUtility;
	@Autowired
	ApiUtility apiUtility;
	
	
	//********************************Start of Submission******************************
	public String ldap_tab2(LdapIPBaseDTO ldapIPBaseDTO)  {
		log.info("Inside ldap_tab2 method at " + new Date() );
		 String dbrefno = "", newref = "";
		 int newrefno;
		  Date date1 = new Date();
          DateFormat dateFormat1 = new SimpleDateFormat("yyyyMMdd");
          String pdate1 = dateFormat1.format(date1);
          SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd");
  		Date dt = new Date();
  		String datetime = format.format(dt);
  		List<LdapIPBaseDTO> sbd=ldapIPBaseRepository.findByDatetimeLike("%"+datetime+"%");
          log.info("Inside ldap_tab2 method at " + new Date() +" sbd:::"+sbd);
          for(LdapIPBaseDTO libt:sbd)
        	  dbrefno=libt.getRegistrationno();
		 log.info("Inside ldap_tab2 method at " + new Date() +" dbrefno:::"+dbrefno);
		  if (dbrefno == null || dbrefno.equals("")) {
              newrefno = 1;
              newref = "000" + newrefno;
              log.info("Inside ldap_tab2 method at " + new Date() +" newref:::"+newref);
              
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
          log.info("Inside ldap_tab2 method at " + new Date() +" newref:::"+newref);
          ldapIPBaseDTO.setRegistrationno(newref);
          ldapIPBaseDTO.setUserip("0:0:0:0:0:0:0:1");
          ldapIPBaseDTO.setDatetime(datetime);
         LdapIPBaseDTO a = ldapIPBaseRepository.save(ldapIPBaseDTO);
          log.info("Inside ldap_tab2 method at " + new Date() +" a:::"+a);
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
		LdapIPFinalAuditTrackDTO ldapIPfinalAuditTrackDTO=new LdapIPFinalAuditTrackDTO();
		ldapIPfinalAuditTrackDTO.setRegistrationno(forwardBydata.get("regNumber").toString());
		ldapIPfinalAuditTrackDTO.setApplicantemail(forwardBydata.get("forwardedByEmail"));
		ldapIPfinalAuditTrackDTO.setApplicant_mobile(forwardBydata.get("forwardedByMobile"));
		ldapIPfinalAuditTrackDTO.setApplicant_name(forwardBydata.get("forwardedByName"));
		ldapIPfinalAuditTrackDTO.setApplicant_ip(ip);
		ldapIPfinalAuditTrackDTO.setApplicant_datetime(pdate);
		ldapIPfinalAuditTrackDTO.setStatus(forwardToData.get("status"));
		ldapIPfinalAuditTrackDTO.setForm_name(forwardBydata.get("formType"));
		ldapIPfinalAuditTrackDTO.setToemail(forwardToData.get("forwardedToEmail").replace(",,", ",").replaceFirst("^,", ""));
		ldapIPfinalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
		ldapIPfinalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
		ldapIPfinalAuditTrackDTO.setTo_datetime(pdate);
		ldapIPfinalAuditTrackDTO.setApp_user_type(forwardBydata.get("check"));
		ldapIPfinalAuditTrackDTO.setApp_user_path(forwardBydata.get("filePath"));
		ldapIPfinalAuditTrackDTO.setApp_ca_type(forwardBydata.get(""));
		ldapIPfinalAuditTrackDTO.setApp_ca_path("");
		ldapIPfinalAuditTrackDTO.setOn_hold("n");
		LdapIPFinalAuditTrackDTO i=ldapIPfinalAuditTrackRepository.save(ldapIPfinalAuditTrackDTO);
		  if(i.getTrack_id()>0) {
	        	j=1;
	        }
		  log.info("Inside insertIntoFinalAuditTrack method at " + new Date()+"  j:::"+j);
		return j;
	}
	
	
	//********************************End of Submission********************************
	
	
	
	
	//********************************Start of Preview(after submission)***************
	public boolean checkStatusForPreview(String registrationNumber, String email, String role, String filter, Set<String> s) {
		 log.info("Inside checkStatusForPreview method at " + new Date() +"  role:::"+role);  
		boolean isValid = false;
		 if (!(role.equals("admin") || role.equals("sup"))) {
	             for (String emailAddress : s) {
	            	 int i = ldapIPfinalAuditTrackRepository.countByregistrationnoAndToemailLikeOrApplicantemailOrCaemailLikeOrCoordinatoremailLike(registrationNumber,emailAddress,emailAddress,emailAddress,emailAddress);
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
		LdapIPBaseDTO previewBaseDTO=ldapIPBaseRepository.findByRegistrationno(regno);
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
	
	public boolean UpdatePreviewDetails(LdapIPFormBean ldapIPFormBean) {
		log.info("Inside UpdatePreviewDetails method at " + new Date()); 
		boolean status=false;
		LdapIPBaseDTO previewBaseDTO =ldapIPBaseRepository.findByRegistrationno(ldapIPFormBean.getRegistrationno());
		log.info("Inside UpdatePreviewDetails method at " + new Date()+" previewBaseDTO:::"+previewBaseDTO); 
		try {
			BeanUtils.copyProperties(previewBaseDTO, ldapIPFormBean);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		LdapIPBaseDTO pbt = ldapIPBaseRepository.save(previewBaseDTO);
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
 					int i = ldapIPfinalAuditTrackRepository.countByregistrationnoAndApplicantemailAndStatusOrStatusOrStatusOrStatus(registrationNumber,emailAddress1,"ca_pending","manual_upload","api","domainapi");
 					log.info("Inside checkStatusForEdit method at " + new Date() +" i:::"+i); 
 		           	 if(i>0) {
 		           		 isValid = true;
 		           	 }
                 }
 				break;
 			case Constants.ROLE_CA:
 				for (String emailAddress1 : s) {
					int i = ldapIPfinalAuditTrackRepository.countByregistrationnoAndCaemailAndStatusOrStatusAndToemail(registrationNumber,emailAddress1,"coordinator_pending","ca_pending",emailAddress1);
					log.info("Inside checkStatusForEdit method at " + new Date() +" i:::"+i); 
		           	 if(i>0) {
		           		 isValid = true;
		           	 }
                }
 				break;
 			case Constants.ROLE_CO:
 				for (String emailAddress1 : s) {
					int i = ldapIPfinalAuditTrackRepository.countByregistrationnoAndCoordinatoremailAndStatusOrStatusAndToemail(registrationNumber,"%"+emailAddress1+"%","mail-admin_pending","coordinator_pending",emailAddress1);
					log.info("Inside checkStatusForEdit method at " + new Date() +" i:::"+i); 
		           	 if(i>0) {
		           		 isValid = true;
		           	 }
                }
 				break;
 			case Constants.ROLE_SUP:
 				for (String emailAddress1 : s) {
					int i = ldapIPfinalAuditTrackRepository.countByregistrationnoAndSupportemailAndStatusOrStatus(registrationNumber,"%"+emailAddress1+"%","coordinator_pending","support_pending");
					log.info("Inside checkStatusForEdit method at " + new Date() +" i:::"+i); 
		           	 if(i>0) {
		           		 isValid = true;
		           	 }
                }
 				break;
 			case Constants.ROLE_MAILADMIN:
					int i = ldapIPfinalAuditTrackRepository.countByregistrationnoAndToemailAndStatus(registrationNumber,email,"mail-admin_pending");
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
	//********************************End of Preview(after submission)****************
	
	
	
	//********************************Start of Approve/Forward*************************
	public boolean validateRefNo(String refNo) {
		int i=ldapIPBaseRepository.countByregistrationno(refNo);
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
		LdapIPBaseDTO fetchFormDetail=ldapIPBaseRepository.findByRegistrationno(regNo);
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
		List<LdapIPMailadminFormsDTO> arr1 = null;
		String prefix = "";
		try {
			//arr = forwardMailadminFormsRepository.findDistinctByMldap('y');
			arr1=ldapIPmailadminFormsRepository.findDistinctByMldap("y");
			
			for(LdapIPMailadminFormsDTO ldapIPForwardMailadminFormsDTO:arr1) {
				email=ldapIPForwardMailadminFormsDTO.getMemail();
				arr.add(email);
			}
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
		List<LdapIPEmpCoordDTO> coords1 = null;
		coords1 = ldapIPempCoordRepository.findByEmpcoordemailAndEmpadminemailAndEmpdept(Constants.HIMACHAL_DA_ADMIN,Constants.HIMACHAL_DA_ADMIN,dept);
		for(LdapIPEmpCoordDTO a: coords1) {
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
		log.info("Inside convertSetToString method at " + new Date());
		return ldapIPmailadminFormsRepository.findFirstDistinctByMemailAndMldap(email,'y');
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
	                	LdapIPEmpCoordDTO forwardEmpCoordDTO=ldapIPempCoordRepository.findFirstByEmpcoordemailAndEmpadminemail(aliase,aliase);
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
	                	LdapIPEmpCoordDTO empCoordDTO=ldapIPempCoordListRepository.findFirstByEmpcoordemail(email);
	                	String emp_coord_email=empCoordDTO.getEmpcoordemail();
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
			LdapIPBaseDTO forwardBaseDTO = ldapIPBaseRepository.findByRegistrationno(forwardBydata.get("regNumber"));
			forwardBaseDTO.setSign_cert(forwardBydata.get("filePath"));
			forwardBaseDTO.setRename_sign_cert(forwardBydata.get("renameFilePath"));
			LdapIPBaseDTO id=ldapIPBaseRepository.save(forwardBaseDTO);
			if(id.getId()>0) {
				i=1;
			}
			log.info("Inside insertIntoAppType method at " + new Date()+" i:::"+i);
		} else {
			LdapIPBaseDTO ldapIPBaseDTO = ldapIPBaseRepository.findByRegistrationno(forwardBydata.get("regNumber"));
			ldapIPBaseDTO.setPdf_path(forwardBydata.get("filePath"));
			LdapIPBaseDTO id=ldapIPBaseRepository.save(ldapIPBaseDTO);
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
				LdapIPFinalAuditTrackDTO ldapIPforwardFinalAuditTrackDTO = ldapIPfinalAuditTrackRepository.findByRegistrationno(forwardBydata.get("regNumber"));
				ldapIPforwardFinalAuditTrackDTO.setStatus(forwardToData.get("status"));
				ldapIPforwardFinalAuditTrackDTO.setToemail(fwdToEmail);
				ldapIPforwardFinalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
				ldapIPforwardFinalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
				ldapIPforwardFinalAuditTrackDTO.setTo_datetime(datetime);
				ldapIPforwardFinalAuditTrackDTO.setSign_cert(forwardBydata.get("filePath"));
				ldapIPforwardFinalAuditTrackDTO.setRename_sign_cert(forwardBydata.get("renameFilePath"));
				ldapIPforwardFinalAuditTrackDTO.setApp_ca_type(forwardBydata.get("check"));
				ldapIPforwardFinalAuditTrackDTO.setOn_hold("n");
				LdapIPFinalAuditTrackDTO id=ldapIPfinalAuditTrackRepository.save(ldapIPforwardFinalAuditTrackDTO);
				if(id.getTrack_id()>0) {
					j=1;
				}
				log.info("Inside updateFinalAuditTrack method at " + new Date()+" j:::"+j);
			} else {
				LdapIPFinalAuditTrackDTO ldapIPforwardFinalAuditTrackDTO = ldapIPfinalAuditTrackRepository.findByRegistrationno(forwardBydata.get("regNumber"));
				ldapIPforwardFinalAuditTrackDTO.setStatus(forwardToData.get("status"));
				ldapIPforwardFinalAuditTrackDTO.setToemail(fwdToEmail);
				ldapIPforwardFinalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
				ldapIPforwardFinalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
				ldapIPforwardFinalAuditTrackDTO.setTo_datetime(datetime);
				ldapIPforwardFinalAuditTrackDTO.setCa_sign_cert(forwardBydata.get("filePath"));
				ldapIPforwardFinalAuditTrackDTO.setCa_rename_sign_cert(forwardBydata.get("renameFilePath"));
				ldapIPforwardFinalAuditTrackDTO.setApp_ca_type(forwardBydata.get("check"));
				ldapIPforwardFinalAuditTrackDTO.setOn_hold("n");
				LdapIPFinalAuditTrackDTO id=ldapIPfinalAuditTrackRepository.save(ldapIPforwardFinalAuditTrackDTO);
				if(id.getTrack_id()>0) {
					j=1;
				}
				log.info("Inside updateFinalAuditTrack method at " + new Date()+" j:::"+j);
			}
		} else {
			String fwdToEmail = forwardToData.get("forwardedToEmail").replace(",,", ",").replaceFirst("^,", "");
				if (role.equals(Constants.ROLE_CA)) {
				LdapIPFinalAuditTrackDTO ldapIPfinalAuditTrackDTO = ldapIPfinalAuditTrackRepository.findByRegistrationno(forwardBydata.get("regNumber"));
				ldapIPfinalAuditTrackDTO.setCaemail(forwardBydata.get("forwardedByEmail")); 
				ldapIPfinalAuditTrackDTO.setCa_mobile(forwardBydata.get("forwardedByMobile"));
				ldapIPfinalAuditTrackDTO.setCa_name(forwardBydata.get("forwardedByName"));
				ldapIPfinalAuditTrackDTO.setCa_ip("0:0:0:0:0:0:0:1");
				ldapIPfinalAuditTrackDTO.setCa_datetime(datetime);
				ldapIPfinalAuditTrackDTO.setCa_remarks( forwardToData.get("remarks"));
				ldapIPfinalAuditTrackDTO.setStatus(forwardToData.get("status"));
				ldapIPfinalAuditTrackDTO.setToemail(fwdToEmail);
				ldapIPfinalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
				ldapIPfinalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
				ldapIPfinalAuditTrackDTO.setApp_ca_type(forwardBydata.get("app_ca_type"));
				ldapIPfinalAuditTrackDTO.setApp_ca_path(forwardBydata.get("app_ca_path"));
				ldapIPfinalAuditTrackDTO.setOn_hold("n");
				LdapIPFinalAuditTrackDTO id=ldapIPfinalAuditTrackRepository.save(ldapIPfinalAuditTrackDTO);
				if(id.getTrack_id()>0) {
					j=1;
				}
				log.info("Inside updateFinalAuditTrack method at " + new Date()+" j:::"+j);
			} else if (role.equals(Constants.ROLE_US)) {
				LdapIPFinalAuditTrackDTO ldapIPforwardFinalAuditTrackDTO = ldapIPfinalAuditTrackRepository.findByRegistrationno(forwardBydata.get("regNumber"));
				ldapIPforwardFinalAuditTrackDTO.setUs_email(forwardBydata.get("forwardedByEmail")); 
				ldapIPforwardFinalAuditTrackDTO.setUs_mobile(forwardBydata.get("forwardedByMobile"));
				ldapIPforwardFinalAuditTrackDTO.setUs_name(forwardBydata.get("forwardedByName"));
				ldapIPforwardFinalAuditTrackDTO.setUs_ip("0:0:0:0:0:0:0:1");
				ldapIPforwardFinalAuditTrackDTO.setUs_datetime(datetime);
				ldapIPforwardFinalAuditTrackDTO.setUs_remarks( forwardToData.get("remarks"));
				ldapIPforwardFinalAuditTrackDTO.setStatus(forwardToData.get("status"));
				ldapIPforwardFinalAuditTrackDTO.setToemail(fwdToEmail);
				ldapIPforwardFinalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
				ldapIPforwardFinalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
				ldapIPforwardFinalAuditTrackDTO.setOn_hold("n");
				LdapIPFinalAuditTrackDTO id=ldapIPfinalAuditTrackRepository.save(ldapIPforwardFinalAuditTrackDTO);
				if(id.getTrack_id()>0) {
					j=1;
				}
				log.info("Inside updateFinalAuditTrack method at " + new Date()+" j:::"+j);
			} else if (role.equals(Constants.ROLE_CO)) {
				LdapIPFinalAuditTrackDTO ldapIPforwardFinalAuditTrackDTO = ldapIPfinalAuditTrackRepository.findByRegistrationno(forwardBydata.get("regNumber"));
				ldapIPforwardFinalAuditTrackDTO.setCoordinatoremail(forwardBydata.get("forwardedByEmail")); 
				ldapIPforwardFinalAuditTrackDTO.setCoordinator_mobile(forwardBydata.get("forwardedByMobile"));
				ldapIPforwardFinalAuditTrackDTO.setCoordinator_name(forwardBydata.get("forwardedByName"));
				ldapIPforwardFinalAuditTrackDTO.setCoordinator_ip("0:0:0:0:0:0:0:1");
				ldapIPforwardFinalAuditTrackDTO.setCoordinator_datetime(datetime);
				ldapIPforwardFinalAuditTrackDTO.setCoordinator_remarks( forwardToData.get("remarks"));
				ldapIPforwardFinalAuditTrackDTO.setStatus(forwardToData.get("status"));
				ldapIPforwardFinalAuditTrackDTO.setToemail(fwdToEmail);
				ldapIPforwardFinalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
				ldapIPforwardFinalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
				ldapIPforwardFinalAuditTrackDTO.setOn_hold("n");
				LdapIPFinalAuditTrackDTO id=ldapIPfinalAuditTrackRepository.save(ldapIPforwardFinalAuditTrackDTO);
				if(id.getTrack_id()>0) {
					j=1;
				}
				log.info("Inside updateFinalAuditTrack method at " + new Date()+" j:::"+j);
			} else if (role.equals(Constants.ROLE_SUP)) {
				LdapIPFinalAuditTrackDTO ldapIPforwardFinalAuditTrackDTO = ldapIPfinalAuditTrackRepository.findByRegistrationno(forwardBydata.get("regNumber"));
				ldapIPforwardFinalAuditTrackDTO.setSupportemail(forwardBydata.get("forwardedByEmail")); 
				ldapIPforwardFinalAuditTrackDTO.setSupport_mobile(forwardBydata.get("forwardedByMobile"));
				ldapIPforwardFinalAuditTrackDTO.setSupport_name(forwardBydata.get("forwardedByName"));
				ldapIPforwardFinalAuditTrackDTO.setSupport_ip("0:0:0:0:0:0:0:1");
				ldapIPforwardFinalAuditTrackDTO.setSupport_datetime(datetime);
				ldapIPforwardFinalAuditTrackDTO.setSupport_remarks( forwardToData.get("remarks"));
				ldapIPforwardFinalAuditTrackDTO.setStatus(forwardToData.get("status"));
				ldapIPforwardFinalAuditTrackDTO.setToemail(fwdToEmail);
				ldapIPforwardFinalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
				ldapIPforwardFinalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
				ldapIPforwardFinalAuditTrackDTO.setOn_hold("n");
				LdapIPFinalAuditTrackDTO id=ldapIPfinalAuditTrackRepository.save(ldapIPforwardFinalAuditTrackDTO);
				if(id.getTrack_id()>0) {
					j=1;
				}
				log.info("Inside updateFinalAuditTrack method at " + new Date()+" j:::"+j);
			} else if (role.equals(Constants.ROLE_DA)) {
				LdapIPFinalAuditTrackDTO ldapIPforwardFinalAuditTrackDTO = ldapIPfinalAuditTrackRepository.findByRegistrationno(forwardBydata.get("regNumber"));
				ldapIPforwardFinalAuditTrackDTO.setDaemail(forwardBydata.get("forwardedByEmail")); 
				ldapIPforwardFinalAuditTrackDTO.setDa_mobile(forwardBydata.get("forwardedByMobile"));
				ldapIPforwardFinalAuditTrackDTO.setDa_name(forwardBydata.get("forwardedByName"));
				ldapIPforwardFinalAuditTrackDTO.setDa_ip("0:0:0:0:0:0:0:1");
				ldapIPforwardFinalAuditTrackDTO.setDa_datetime(datetime);
				ldapIPforwardFinalAuditTrackDTO.setDa_remarks( forwardToData.get("remarks"));
				ldapIPforwardFinalAuditTrackDTO.setStatus(forwardToData.get("status"));
				ldapIPforwardFinalAuditTrackDTO.setToemail(fwdToEmail);
				ldapIPforwardFinalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
				ldapIPforwardFinalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
				ldapIPforwardFinalAuditTrackDTO.setOn_hold("n");
				LdapIPFinalAuditTrackDTO id=ldapIPfinalAuditTrackRepository.save(ldapIPforwardFinalAuditTrackDTO);
				if(id.getTrack_id()>0) {
					j=1;
				}
				log.info("Inside updateFinalAuditTrack method at " + new Date()+" j:::"+j);
			} else if (role.equals(Constants.ROLE_MAILADMIN)) {
				LdapIPFinalAuditTrackDTO ldapIPforwardFinalAuditTrackDTO = ldapIPfinalAuditTrackRepository.findByRegistrationno(forwardBydata.get("regNumber"));
				ldapIPforwardFinalAuditTrackDTO.setAdminemail(forwardBydata.get("forwardedByEmail")); 
				ldapIPforwardFinalAuditTrackDTO.setAdmin_mobile(forwardBydata.get("forwardedByMobile"));
				ldapIPforwardFinalAuditTrackDTO.setAdmin_name(forwardBydata.get("forwardedByName"));
				ldapIPforwardFinalAuditTrackDTO.setAdmin_ip("0:0:0:0:0:0:0:1");
				ldapIPforwardFinalAuditTrackDTO.setAdmin_datetime(datetime);
				ldapIPforwardFinalAuditTrackDTO.setAdmin_remarks( forwardToData.get("remarks"));
				ldapIPforwardFinalAuditTrackDTO.setStatus(forwardToData.get("status"));
				ldapIPforwardFinalAuditTrackDTO.setToemail(fwdToEmail);
				ldapIPforwardFinalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
				ldapIPforwardFinalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
				ldapIPforwardFinalAuditTrackDTO.setOn_hold("n");
				LdapIPFinalAuditTrackDTO id=ldapIPfinalAuditTrackRepository.save(ldapIPforwardFinalAuditTrackDTO);
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
			   LdapIPBaseDTO forwardBaseDTO = ldapIPBaseRepository.findByRegistrationno(forwardBydata.get("regNumber"));
			   log.info("Inside updateAppType method at " + new Date()+" forwardBaseDTO:::"+forwardBaseDTO);
			   forwardBaseDTO.setPdf_path(forwardBydata.get("filePath"));
			   LdapIPBaseDTO id=ldapIPBaseRepository.save(forwardBaseDTO);
				if(id.getId()>0) {
					i=1;
				}
				 log.info("Inside updateAppType method at " + new Date()+" i:::"+i);
		   } else if (forwardBydata.get("role").equals(Constants.ROLE_CA)) {
               if (forwardBydata.get("check").equalsIgnoreCase("upload_scanned")) {
            	   LdapIPBaseDTO forwardBaseDTO = ldapIPBaseRepository.findByRegistrationno(forwardBydata.get("regNumber"));
   				forwardBaseDTO.setCa_sign_cert(forwardBydata.get("filePath"));
   				forwardBaseDTO.setCa_rename_sign_cert(forwardBydata.get("renameFilePath"));
   				LdapIPBaseDTO id=ldapIPBaseRepository.save(forwardBaseDTO);
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
        LdapIPStatusDTO ldapIPstatusDTO=new LdapIPStatusDTO();
        try {
			BeanUtils.populate(ldapIPstatusDTO, forwardBydata);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
        ldapIPstatusDTO.setStat_form_type(forwardBydata.get("formType"));
        ldapIPstatusDTO.setStatregno(forwardBydata.get("regNumber"));
        ldapIPstatusDTO.setStat_type(forwardToData.get("status"));
        ldapIPstatusDTO.setStatforwardedby(forwardBydata.get("forwardedBy"));
        ldapIPstatusDTO.setStat_forwarded_by_user(forwardBydata.get("forwardedByEmail"));
        ldapIPstatusDTO.setStatforwardedto(forwardToData.get("forwardedTo"));
        ldapIPstatusDTO.setStat_forwarded_to_user(forwardToData.get("forwardedToEmail"));
        ldapIPstatusDTO.setStat_remarks(forwardToData.get("remarks"));
        ldapIPstatusDTO.setStat_ip(ip);
        ldapIPstatusDTO.setStat_forwarded_by_email(forwardBydata.get("forwardedByEmail"));
        ldapIPstatusDTO.setStat_forwarded_by_mobile(forwardBydata.get("forwardedByMobile"));
        ldapIPstatusDTO.setStat_forwarded_by_name(forwardBydata.get("forwardedByName"));
        ldapIPstatusDTO.setStat_forwarded_by_ip(ip);
        ldapIPstatusDTO.setStat_forwarded_by_datetime(datetime);
        ldapIPstatusDTO.setStat_final_id("");
        ldapIPstatusDTO.setStat_on_hold("n");
		LdapIPStatusDTO i=ldapIPstatusRepository.save(ldapIPstatusDTO);
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
		//da_email = fetchPunjabDA(employment, ministry, department);
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
		log.info("Inside fetchEmailDetails method at " + new Date());
		Map<String, String> map = null;
		LdapIPEmpCoordDTO beanlist = ldapIPempCoordRepository.findByEmpcategoryAndEmpminstateorgAndEmpdeptAndEmpstatus(employment, ministry, department,"a");
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
		
		LdapIPBaseDTO ldapIPBaseDTO=ldapIPBaseRepository.findByRegistrationno(regNo);
		ObjectMapper m = new ObjectMapper();
		maplist = m.convertValue(ldapIPBaseDTO, Map.class);
		log.info("Inside fetchEmpDetails method at " + new Date()+" maplist:::"+maplist);
		return maplist;
	}
	//********************************End of Approve/Forward********************************
	
	
	
	
	//********************************Start of Reject***************************************
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
	        int i = 0, j = 0, k = 0;
	        if (role.equals(Constants.ROLE_USER)) {
	        	
	        	LdapIPFinalAuditTrackDTO ldapIPfinalAuditTrackDTO = ldapIPfinalAuditTrackRepository.findByRegistrationno(forwardBydata.get("regNumber"));
	        	ldapIPfinalAuditTrackDTO.setApplicantemail(forwardBydata.get("forwardedByEmail")); 
	        	ldapIPfinalAuditTrackDTO.setApplicant_mobile(forwardBydata.get("forwardedByMobile"));
	        	ldapIPfinalAuditTrackDTO.setApplicant_name(forwardBydata.get("forwardedByName"));
	        	ldapIPfinalAuditTrackDTO.setApplicant_ip("0:0:0:0:0:0:0:1");
	        	ldapIPfinalAuditTrackDTO.setApplicant_datetime(datetime);
	        	ldapIPfinalAuditTrackDTO.setApplicant_remarks( forwardToData.get("remarks"));
	        	ldapIPfinalAuditTrackDTO.setStatus(forwardToData.get("status"));
	        	ldapIPfinalAuditTrackDTO.setToemail(fwdToEmail);
	        	ldapIPfinalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
	        	ldapIPfinalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
	        	LdapIPFinalAuditTrackDTO id=ldapIPfinalAuditTrackRepository.save(ldapIPfinalAuditTrackDTO);
				if(id.getTrack_id()>0) {
					j=1;
				}
				  log.info("Inside updateTrackTable method at " + new Date()+" j:::"+j);
	        }else if (role.equals(Constants.ROLE_CA)) {
	        	LdapIPFinalAuditTrackDTO ldapIPfinalAuditTrackDTO = ldapIPfinalAuditTrackRepository.findByRegistrationno(forwardBydata.get("regNumber"));
	        	ldapIPfinalAuditTrackDTO.setCaemail(forwardBydata.get("forwardedByEmail")); 
	        	ldapIPfinalAuditTrackDTO.setCa_mobile(forwardBydata.get("forwardedByMobile"));
	        	ldapIPfinalAuditTrackDTO.setCa_name(forwardBydata.get("forwardedByName"));
	        	ldapIPfinalAuditTrackDTO.setCa_ip("0:0:0:0:0:0:0:1");
	        	ldapIPfinalAuditTrackDTO.setCa_datetime(datetime);
	        	ldapIPfinalAuditTrackDTO.setCa_remarks( forwardToData.get("remarks"));
	        	ldapIPfinalAuditTrackDTO.setStatus(forwardToData.get("status"));
	        	ldapIPfinalAuditTrackDTO.setToemail(fwdToEmail);
	        	ldapIPfinalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
	        	ldapIPfinalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
	        	LdapIPFinalAuditTrackDTO id=ldapIPfinalAuditTrackRepository.save(ldapIPfinalAuditTrackDTO);
				if(id.getTrack_id()>0) {
					j=1;
				}
				 log.info("Inside updateTrackTable method at " + new Date()+" j:::"+j);
	        } else if (role.equals(Constants.ROLE_US)) {
	        	LdapIPFinalAuditTrackDTO ldapIPfinalAuditTrackDTO = ldapIPfinalAuditTrackRepository.findByRegistrationno(forwardBydata.get("regNumber"));
	        	ldapIPfinalAuditTrackDTO.setUs_email(forwardBydata.get("forwardedByEmail")); 
	        	ldapIPfinalAuditTrackDTO.setUs_mobile(forwardBydata.get("forwardedByMobile"));
	        	ldapIPfinalAuditTrackDTO.setUs_name(forwardBydata.get("forwardedByName"));
	        	ldapIPfinalAuditTrackDTO.setUs_ip("0:0:0:0:0:0:0:1");
	        	ldapIPfinalAuditTrackDTO.setUs_datetime(datetime);
	        	ldapIPfinalAuditTrackDTO.setUs_remarks( forwardToData.get("remarks"));
	        	ldapIPfinalAuditTrackDTO.setStatus(forwardToData.get("status"));
	        	ldapIPfinalAuditTrackDTO.setToemail(fwdToEmail);
	        	ldapIPfinalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
	        	ldapIPfinalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
	        	LdapIPFinalAuditTrackDTO id=ldapIPfinalAuditTrackRepository.save(ldapIPfinalAuditTrackDTO);
				if(id.getTrack_id()>0) {
					j=1;
				}
				 log.info("Inside updateTrackTable method at " + new Date()+" j:::"+j);
	        } else if (role.equals(Constants.ROLE_CO)) {
	        	LdapIPFinalAuditTrackDTO ldapIPfinalAuditTrackDTO = ldapIPfinalAuditTrackRepository.findByRegistrationno(forwardBydata.get("regNumber"));
	        	ldapIPfinalAuditTrackDTO.setCoordinatoremail(forwardBydata.get("forwardedByEmail")); 
	        	ldapIPfinalAuditTrackDTO.setCoordinator_mobile(forwardBydata.get("forwardedByMobile"));
	        	ldapIPfinalAuditTrackDTO.setCoordinator_name(forwardBydata.get("forwardedByName"));
	        	ldapIPfinalAuditTrackDTO.setCoordinator_ip("0:0:0:0:0:0:0:1");
	        	ldapIPfinalAuditTrackDTO.setCoordinator_datetime(datetime);
	        	ldapIPfinalAuditTrackDTO.setCoordinator_remarks( forwardToData.get("remarks"));
	        	ldapIPfinalAuditTrackDTO.setStatus(forwardToData.get("status"));
	        	ldapIPfinalAuditTrackDTO.setToemail(fwdToEmail);
	        	ldapIPfinalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
	        	ldapIPfinalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
	        	LdapIPFinalAuditTrackDTO id=ldapIPfinalAuditTrackRepository.save(ldapIPfinalAuditTrackDTO);
				if(id.getTrack_id()>0) {
					j=1;
				}
				 log.info("Inside updateTrackTable method at " + new Date()+" j:::"+j);
	        } else if (role.equals(Constants.ROLE_SUP)) {
	        	
	        	LdapIPFinalAuditTrackDTO ldapIPfinalAuditTrackDTO = ldapIPfinalAuditTrackRepository.findByRegistrationno(forwardBydata.get("regNumber"));
	        	ldapIPfinalAuditTrackDTO.setSupportemail(forwardBydata.get("forwardedByEmail")); 
	        	ldapIPfinalAuditTrackDTO.setSupport_mobile(forwardBydata.get("forwardedByMobile"));
	        	ldapIPfinalAuditTrackDTO.setSupport_name(forwardBydata.get("forwardedByName"));
	        	ldapIPfinalAuditTrackDTO.setSupport_ip("0:0:0:0:0:0:0:1");
	        	ldapIPfinalAuditTrackDTO.setSupport_datetime(datetime);
	        	ldapIPfinalAuditTrackDTO.setSupport_remarks( forwardToData.get("remarks"));
	        	ldapIPfinalAuditTrackDTO.setStatus(forwardToData.get("status"));
	        	ldapIPfinalAuditTrackDTO.setToemail(fwdToEmail);
	        	ldapIPfinalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
	        	ldapIPfinalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
	        	LdapIPFinalAuditTrackDTO id=ldapIPfinalAuditTrackRepository.save(ldapIPfinalAuditTrackDTO);
				if(id.getTrack_id()>0) {
					j=1;
				}
				 log.info("Inside updateTrackTable method at " + new Date()+" j:::"+j);
	        } else if (role.equals(Constants.ROLE_DA)) {
	        	
	        	LdapIPFinalAuditTrackDTO ldapIPfinalAuditTrackDTO = ldapIPfinalAuditTrackRepository.findByRegistrationno(forwardBydata.get("regNumber"));
	        	ldapIPfinalAuditTrackDTO.setDaemail(forwardBydata.get("forwardedByEmail")); 
	        	ldapIPfinalAuditTrackDTO.setDa_mobile(forwardBydata.get("forwardedByMobile"));
	        	ldapIPfinalAuditTrackDTO.setDa_name(forwardBydata.get("forwardedByName"));
	        	ldapIPfinalAuditTrackDTO.setDa_ip("0:0:0:0:0:0:0:1");
	        	ldapIPfinalAuditTrackDTO.setDa_datetime(datetime);
	        	ldapIPfinalAuditTrackDTO.setDa_remarks( forwardToData.get("remarks"));
	        	ldapIPfinalAuditTrackDTO.setStatus(forwardToData.get("status"));
	        	ldapIPfinalAuditTrackDTO.setToemail(fwdToEmail);
	        	ldapIPfinalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
	        	ldapIPfinalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
	        	LdapIPFinalAuditTrackDTO id=ldapIPfinalAuditTrackRepository.save(ldapIPfinalAuditTrackDTO);
				if(id.getTrack_id()>0) {
					j=1;
				}
				 log.info("Inside updateTrackTable method at " + new Date()+" j:::"+j);
	        }else if (role.equals(Constants.ROLE_MAILADMIN)) {
	        	LdapIPFinalAuditTrackDTO ldapIPfinalAuditTrackDTO = ldapIPfinalAuditTrackRepository.findByRegistrationno(forwardBydata.get("regNumber"));
	        	ldapIPfinalAuditTrackDTO.setAdminemail(forwardBydata.get("forwardedByEmail")); 
	        	ldapIPfinalAuditTrackDTO.setAdmin_mobile(forwardBydata.get("forwardedByMobile"));
	        	ldapIPfinalAuditTrackDTO.setAdmin_name(forwardBydata.get("forwardedByName"));
	        	ldapIPfinalAuditTrackDTO.setAdmin_ip("0:0:0:0:0:0:0:1");
	        	ldapIPfinalAuditTrackDTO.setAdmin_datetime(datetime);
	        	ldapIPfinalAuditTrackDTO.setAdmin_remarks( forwardToData.get("remarks"));
	        	ldapIPfinalAuditTrackDTO.setStatus(forwardToData.get("status"));
	        	ldapIPfinalAuditTrackDTO.setToemail(fwdToEmail);
	        	ldapIPfinalAuditTrackDTO.setTo_mobile(forwardToData.get("forwardedToMobile"));
	        	ldapIPfinalAuditTrackDTO.setTo_name(forwardToData.get("forwardedToName"));
	        	LdapIPFinalAuditTrackDTO id=ldapIPfinalAuditTrackRepository.save(ldapIPfinalAuditTrackDTO);
				if(id.getTrack_id()>0) {
					j=1;
				}
				 log.info("Inside updateTrackTable method at " + new Date()+" j:::"+j);
	        }
	        LdapIPStatusDTO ldapIPstatusDTO =new LdapIPStatusDTO();
	        ldapIPstatusDTO.setStat_forwarded_by_email(forwardBydata.get("forwardedByEmail")); 
	        ldapIPstatusDTO.setStat_forwarded_by_mobile(forwardBydata.get("forwardedByMobile"));
	        ldapIPstatusDTO.setStat_forwarded_by_name(forwardBydata.get("forwardedByName"));
	        ldapIPstatusDTO.setStat_ip("0:0:0:0:0:0:0:1");
	        ldapIPstatusDTO.setStat_createdon(datetime);
	        ldapIPstatusDTO.setStat_remarks( forwardToData.get("remarks"));
	        ldapIPstatusDTO.setStat_type(forwardToData.get("status"));
	       // ldapIPstatusDTO.setStatforwardedto(fwdToEmail);
	        ldapIPstatusDTO.setStatforwardedto(forwardToData.get("forwardedTo"));
	        ldapIPstatusDTO.setStatregno(forwardBydata.get("regNumber"));
	        ldapIPstatusDTO.setStat_on_hold("n");
	        System.err.println("***********ldapIPstatusDTO**********"+ldapIPstatusDTO);
        	LdapIPStatusDTO id=ldapIPstatusRepository.save(ldapIPstatusDTO);
        	
        	
			if(id.getStatid()>0) {
				k=1;
			}
			 log.info("Inside updateTrackTable method at " + new Date()+" k:::"+k);
	        	if(k>0) {
	        		retStatus = true;
	        	}
	        	return retStatus;
	    }
	//********************************End of Reject*****************************************
	
	
	
	//********************************Start of Track User****************************************
	public HashMap<String, Object> fetchTrackDetails(String registration_no) {
		log.info("Entering fetchTrackDetails method at info " + new Date() +" registration_no::::"+registration_no);
		HashMap<String, Object> hmTrack = new HashMap<>();
		List<LdapIPStatusDTO> arrRoles = ldapIPstatusListRepository.findByStatregno(registration_no);
		List<LdapIPFinalAuditTrackDTO> currentDetails = ldapIPfinalAuditTrackListRepository.findByRegistrationno(registration_no);
		hmTrack.put("arrRoles", arrRoles);
		hmTrack.put("currentDetails", currentDetails);
		log.info("Entering fetchTrackDetails method at info " + new Date() +" hmTrack::::"+hmTrack);
		return hmTrack;
	}

	
	public Map<String, Object> fetchTrackByRole(String registration_no, String forward, String trole, String srole) {
		log.info("Entering fetchTrackByRole method at info " + new Date() +" registration_no::::"+registration_no+"  forward:::"+forward+"  trole:::"+trole+" srole:::"+srole);
		Map<String, Object> trackData = new HashMap<>();
		Map<String, String> fetchstatus = new HashMap<>();
		List<LdapIPStatusDTO> iterable = null;
		String recv_date = "";
		if (trole.equalsIgnoreCase("undefined")) {
			iterable = ldapIPstatusRepository.findFirstByStatregnoAndStatforwardedtoOrderByStatid(registration_no, trole);
		} else if (srole.equalsIgnoreCase("null") && trole.equalsIgnoreCase("null")) {
			iterable = ldapIPstatusRepository.findByStatregnoAndStatforwardedbyIsNullAndStatforwardedtoIsNullOrderByStatid(registration_no);
		} else if (trole.equalsIgnoreCase("null")) {
			iterable = ldapIPstatusRepository.findByStatregnoAndStatforwardedbyAndStatforwardedtoIsNullOrderByStatid(registration_no, srole);
		} else {
			iterable = ldapIPstatusRepository.findByStatregnoAndStatforwardedbyAndStatforwardedto(registration_no, srole, trole);
		}
		log.info("Entering fetchTrackByRole method at info " + new Date() +" iterable::::"+iterable);
		for (LdapIPStatusDTO ldapIPstatusDTO : iterable) {
			String remarks = ldapIPstatusDTO.getStat_remarks();
			if (remarks == null) {
				remarks = "";
			}
			recv_date = ldapIPstatusDTO.getStat_createdon();
			String current_user = ldapIPstatusDTO.getStat_forwarded_by_email();
			if (current_user == null) {
				current_user = "User Emaillll";
				//current_user = userdata.getEmail();
			}
			String forwarder = commonUtility.findRole(forward);
			String sender_details = forwarder + "&ensp;(" + current_user + ")";
			if (!forward.isEmpty()) {
				fetchstatus = (Map<String, String>) ldapIPstatusRepository.findByStatregnoAndStatforwardedbyOrStatforwardedto(registration_no, forward, srole);
				recv_date = fetchstatus.get("recv_date");
				String stat_forwarded_by_email = fetchstatus.get("stat_forwarded_by_email");
				sender_details = forwarder + "&ensp;(" + stat_forwarded_by_email + ")";
			}
			trackData.put("remarks", remarks);
			trackData.put("recv_date", recv_date);
			trackData.put("current_user", current_user);
			trackData.put("recv_email", ldapIPstatusDTO.getStat_forwarded_to_user());
			trackData.put("stat_process", ldapIPstatusDTO.getStat_process());
			trackData.put("stat_type", ldapIPstatusDTO.getStat_type());
			trackData.put("stat_on_hold", ldapIPstatusDTO.getStat_on_hold());
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
	        	
	        	
	        	LdapIPQueryRaiseDTO ldapIPqueryRaiseDTO = new LdapIPQueryRaiseDTO();
	        	ldapIPqueryRaiseDTO.setQr_form_type(qr_form_type);
	        	ldapIPqueryRaiseDTO.setQrregno(qr_reg_no);
	        	ldapIPqueryRaiseDTO.setQr_forwarded_by(qr_forwarded_by);
	        	ldapIPqueryRaiseDTO.setQr_forwarded_by_user(qr_forwarded_by_user);
	        	ldapIPqueryRaiseDTO.setQr_forwarded_to(qr_forwarded_to);
	        	ldapIPqueryRaiseDTO.setQr_forwarded_to_user(qr_forwarded_to_user);
	        	ldapIPqueryRaiseDTO.setQr_message(qr_message);
					LdapIPQueryRaiseDTO id=ldapIPqueryRaiseRepository.save(ldapIPqueryRaiseDTO);
					if(id.getQr_id()>0) {
						 listmap=fetchRaiseQueryData(qr_reg_no,role);
						i=1;
					}
	        	
	        }
	        log.info("Entering raiseQuery method at info " + new Date() +" listmap::::"+listmap);
		return listmap;
	}
	 Set<String> fetchAllStakeHolders(String regNo, String role) {
		 log.info("Entering fetchAllStakeHolders method at info " + new Date() );
		 Set<String> recpdata = new HashSet<>();
		List<LdapIPStatusDTO> statusDTO=ldapIPstatusListRepository.findByStatregno(regNo);
			ObjectMapper m = new ObjectMapper();
			log.info("Entering fetchAllStakeHolders method at info " + new Date() +"  queryRaiseStatusDTO:::"+statusDTO);
			for(LdapIPStatusDTO fetchStatus1:statusDTO) {
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
			List<LdapIPQueryRaiseDTO> ldapIPqueryRaiseDTO=ldapIPqueryRaiseRepository.findByQrregno(regNo);
			 log.info("Entering fetchRaiseQueryData method at info " + new Date() +" recpdata:::"+recpdata);
			 log.info("Entering fetchRaiseQueryData method at info " + new Date() +" queryRaiseDTO:::"+ldapIPqueryRaiseDTO);
			details.put("raiseQueryBtn", true);
			details.put("recpdata", recpdata);
			details.put("querydata", ldapIPqueryRaiseDTO);
//			in case of fetchMap
//			 if (qr_forwarded_by_user.equals(uemail)) {
//	             account_holder = true;
//	         }
			 log.info("Entering fetchRaiseQueryData method at info " + new Date() +" details:::"+details);
			return details;
		}
	//********************************End of Raise Query******************************************
	
	
	
	//********************************Start of Put On/Off Hold****************************************
	public boolean putOnHold(String regNo, String on_hold, String statRemarks) {
		log.info("Inside putOnHold method at " + new Date());

		int i = 0, j = 0;
		boolean flag=false;
		LdapIPStatusDTO ldapIPstatusDTO = ldapIPstatusRepository.findByStatregno(regNo);
		ldapIPstatusDTO.setStat_on_hold(on_hold);
		LdapIPStatusDTO id1=ldapIPstatusRepository.save(ldapIPstatusDTO);
			if(id1.getStatid()>0) {
				i=1;
			}
		LdapIPFinalAuditTrackDTO ldapIPfinalAuditTrackDTO = ldapIPfinalAuditTrackRepository.findByRegistrationno(regNo);
		ldapIPfinalAuditTrackDTO.setOn_hold(on_hold);
		ldapIPfinalAuditTrackDTO.setHold_remarks(statRemarks);
		LdapIPFinalAuditTrackDTO id2=ldapIPfinalAuditTrackRepository.save(ldapIPfinalAuditTrackDTO);
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
	
	
	//********************************Start of Generate PDF******************************
	public LdapIPBaseDTO findByRegno(String regid) {
		log.info("Entering findByRegno method at info " + new Date() +" regid::::"+regid);
		List<LdapIPBaseDTO> iterable = ldapIPBaseListRepository.findByRegistrationno(regid);
		for (LdapIPBaseDTO list : iterable) {
			log.info("Entering findByRegno method at info " + new Date() +" list::::"+list);
			return list;
		}
		return null;
	}
	//********************************End of Generate PDF********************************
	
	
	
	
	//********************************Start of Upload PDF******************************
	public LdapIPDocUploadDTO saveDocx(LdapIPDocUploadDTO ldapIPdocUploadDTO) {
		log.info("Entering saveDocx method at info " + new Date() +" uploadfiles::::"+ldapIPdocUploadDTO);
		LdapIPDocUploadDTO docUpload = ldapIPdocUploadRepository.save(ldapIPdocUploadDTO);
		log.info("Entering saveDocx method at info " + new Date() +" uploadfilesdto::::"+docUpload);
		return ldapIPdocUploadDTO;
	}
	//********************************End of Upload PDF********************************
	
	
	
	
	//********************************Start of Download PDF******************************
	
	//********************************End of Download PDF********************************
	
	
	
	
	//********************************Start of View PDF******************************
	public Map<String, Map<String, String>> viewDocx(String regid, String role) {
		log.info("Inside viewDocx method at " + new Date() +"  regid:::"+regid+"  role:::"+role);
		Map<String, String> file = new HashMap<>();
		Map<String,Map<String,String>> filelist = new HashMap<>();
		//For esign and manual
		String pdf="";
		List<LdapIPBaseDTO> list = ldapIPBaseListRepository.findByRegistrationno(regid);
		log.info("Inside viewDocx method at " + new Date() +"  list:::"+list);
		for(LdapIPBaseDTO dto:list) {
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
					List<LdapIPFinalAuditTrackDTO> l = ldapIPfinalAuditTrackListRepository.findByRegistrationno(regid);
					for(LdapIPFinalAuditTrackDTO d:l) {
						filepath.put("esignedpdf", d.getCa_rename_sign_cert());
					}
					if(filepath!=null)
						file.put(regid+".pdf", filepath.get("esignedpdf"));
				}	
				break;
			
			}
		}
		List<LdapIPDocUploadDTO> iterable = ldapIPdocUploadRepository.findByRegistrationnoAndRole(regid,role);
		for(LdapIPDocUploadDTO ldapIPdto:iterable) {
			file.put(ldapIPdto.getDoc(), ldapIPdto.getDocpath());
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
	
	
	public int insertIntoStatusTable(Map<String, String> forwardBydata, Map<String, String> forwardToData) {
		log.info("Inside insertIntoStatusTable method at " + new Date());
		int j=0;
       SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
       Date dt = new Date();
       String datetime = format.format(dt);
       String ip="0.0.0.0.0.0.0.1";
      LdapIPStatusDTO ldapIPstatusDTO=new LdapIPStatusDTO();
       try {
			BeanUtils.populate(ldapIPstatusDTO, forwardBydata);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
       ldapIPstatusDTO.setStat_form_type(forwardBydata.get("formType"));
       ldapIPstatusDTO.setStatregno(forwardBydata.get("regNumber"));
       ldapIPstatusDTO.setStat_type(forwardToData.get("status"));
       ldapIPstatusDTO.setStatforwardedby(forwardBydata.get("forwardedBy"));
       ldapIPstatusDTO.setStat_forwarded_by_user(forwardBydata.get("forwardedByEmail"));
       ldapIPstatusDTO.setStatforwardedto(forwardToData.get("forwardedTo"));
       ldapIPstatusDTO.setStat_forwarded_to_user(forwardToData.get("forwardedToEmail"));
       ldapIPstatusDTO.setStat_remarks(forwardToData.get("remarks"));
       ldapIPstatusDTO.setStat_ip(ip);
       ldapIPstatusDTO.setStat_forwarded_by_email(forwardBydata.get("forwardedByEmail"));
       ldapIPstatusDTO.setStat_forwarded_by_mobile(forwardBydata.get("forwardedByMobile"));
       ldapIPstatusDTO.setStat_forwarded_by_name(forwardBydata.get("forwardedByName"));
       ldapIPstatusDTO.setStat_forwarded_by_ip(ip);
       ldapIPstatusDTO.setStat_forwarded_by_datetime(datetime);
       ldapIPstatusDTO.setStat_final_id("");
       ldapIPstatusDTO.setStat_on_hold("n");
		LdapIPStatusDTO i=ldapIPstatusRepository.save(ldapIPstatusDTO);
       if(i.getStatid()>0) {
       	j=1;
       }
       log.info("Inside insertIntoStatusTable method at " + new Date()+"  j:::"+j);
		return j;
	}
	
	
	public int insertIntoBaseTbl(Map<String, String> forwardBydata, Map<String, String> forwardToData) {
		log.info("Inside insertIntoBaseTbl method at " + new Date());
		int i=0,j=0;
		if (forwardBydata.get("check").equalsIgnoreCase("upload_scanned")) {
			LdapIPBaseDTO ldapBaseDTO = ldapIPBaseRepository.findByRegistrationno(forwardBydata.get("regNumber"));
			ldapBaseDTO.setSign_cert(forwardBydata.get("filePath"));
			ldapBaseDTO.setRename_sign_cert(forwardBydata.get("renameFilePath"));
			LdapIPBaseDTO id=ldapIPBaseRepository.save(ldapBaseDTO);
			if(id.getId()>0) {
				i=1;
			}
			log.info("Inside insertIntoBaseTbl method at " + new Date()+"  i:::"+i);
		} else {
			LdapIPBaseDTO ldapBaseDTO = ldapIPBaseRepository.findByRegistrationno(forwardBydata.get("regNumber"));
			ldapBaseDTO.setPdf_path(forwardBydata.get("filePath"));
			LdapIPBaseDTO id=ldapIPBaseRepository.save(ldapBaseDTO);
			if(id.getId()>0) {
				i=1;
			}
			log.info("Inside insertIntoBaseTbl method at " + new Date()+"  j:::"+j);
			}
		return i;
	}
	
}
