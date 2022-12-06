package in.nic.ashwini.eForms.exceptions;



import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import in.nic.ashwini.eForms.models.PreviewFormBean;

public class RequestForValidator implements ConstraintValidator<RequestForValid, PreviewFormBean> {
	@Override
	public boolean isValid(PreviewFormBean previewFormBean, ConstraintValidatorContext context) {
		
		
		String request = previewFormBean.getRequestFor();
		
		if(request.contains("new")) {
			
			if (previewFormBean.getAppIp().contains(";")) {
				String[] relayip = previewFormBean.getAppIp().split(";");
				for (String ip1 : relayip) {
					
					if (!ip1.isEmpty()) {
			            if (!ip1.matches("^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$")) {
			                return false;
			            } else if (ip1.startsWith("0") || ip1.equals("0.0.0.0") || ip1.equals("127.0.0.1") || ip1.equals("255.255.255.255") || ip1.endsWith("255")) {
			                return false;
			            }
			        }

				}

			} else {
				if (previewFormBean.getAppIp() == null || previewFormBean.getAppIp().isEmpty()) {
		            return false;
		        } else if (!previewFormBean.getAppIp().matches("^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$")) {
		        	//throw new IllegalArgumentException("@Address only applies to Address");
		        	return  false;
		            
		        } else if (previewFormBean.getAppIp().startsWith("0") || previewFormBean.getAppIp().equals("0.0.0.0") || previewFormBean.getAppIp().equals("127.0.0.1") || previewFormBean.getAppIp().equals("255.255.255.255") || previewFormBean.getAppIp().endsWith("255")) {
		            return false;
		        }
			}

			if (previewFormBean.getStagingIp().equals("no")) {

				if (previewFormBean.getCertFile() == null ||previewFormBean.getCertFile().isEmpty()) {
					
					return false;
				}
			}

			
		}
		
		
		
		if (previewFormBean.getRequestFor().equals("add") || previewFormBean.getRequestFor().equals("modify")) {
			
			if (previewFormBean.getAppIp().contains(";")) {
				String[] relayip = previewFormBean.getAppIp().split(";");
				for (String ip1 : relayip) {
					
					if (!ip1.isEmpty()) {
			            if (!ip1.matches("^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$")) {
			                return false;
			            } else if (ip1.startsWith("0") || ip1.equals("0.0.0.0") || ip1.equals("127.0.0.1") || ip1.equals("255.255.255.255") || ip1.endsWith("255")) {
			                return false;
			            }
			        }

				}

			} else {
				if (previewFormBean.getAppIp() == null || previewFormBean.getAppIp().isEmpty()) {
		            return false;
		        } else if (!previewFormBean.getAppIp().matches("^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$")) {
		        	//throw new IllegalArgumentException("@Address only applies to Address");
		        	return  false;
		            
		        } else if (previewFormBean.getAppIp().startsWith("0") || previewFormBean.getAppIp().equals("0.0.0.0") || previewFormBean.getAppIp().equals("127.0.0.1") || previewFormBean.getAppIp().equals("255.255.255.255") || previewFormBean.getAppIp().endsWith("255")) {
		            return false;
		        }
			}
			
			
			
						if (previewFormBean.getOldAppIp().contains(";")) {
							String[] oldRelayip = previewFormBean.getOldAppIp().split(";");
							for (String ip3 : oldRelayip) {
								
								if (!ip3.isEmpty()) {
						            if (!ip3.matches("^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$")) {
						                return false;
						            } else if (ip3.startsWith("0") || ip3.equals("0.0.0.0") || ip3.equals("127.0.0.1") || ip3.equals("255.255.255.255") || ip3.endsWith("255")) {
						                return false;
						            }
						        }

							}
						}
						else {
							if (previewFormBean.getOldAppIp() == null || previewFormBean.getOldAppIp().isEmpty()) {
					            return false;
					        } else if (!previewFormBean.getOldAppIp().matches("^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$")) {
					        	
					        	return  false;
					            
					        } else if (previewFormBean.getOldAppIp().startsWith("0") || previewFormBean.getOldAppIp().equals("0.0.0.0") || previewFormBean.getOldAppIp().equals("127.0.0.1") || previewFormBean.getOldAppIp().equals("255.255.255.255") || previewFormBean.getOldAppIp().endsWith("255")) {
					            return false;
					        }
						}
		

						if (previewFormBean.getStagingIp().equals("no")) {

							if (previewFormBean.getCertFile() == null ||previewFormBean.getCertFile().isEmpty()) {
								
								return false;
							}
						}
			
					}

	
	
		
		
		
		return true;
	}
}
