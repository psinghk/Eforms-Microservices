//package in.nic.ashwini.eForms.repositories;
//
//public class VpnExtra {
//
//	if (arr[1].equals(Constants.VPN_ADD_FORM_KEYWORD) || arr[1].equals(Constants.VPN_RENEW_FORM_KEYWORD) ||
//            arr[1].equals(Constants.VPN_SINGLE_FORM_KEYWORD) || arr[1].equals(Constants.VPN_SURRENDER_FORM_KEYWORD) 
//            || arr[1].equals(Constants.VPN_DELETE_FORM_KEYWORD)) {
//        System.out.println(ServletActionContext.getRequest().getSession().getId() + " == IP: " + ip + " timestamp: == " + new java.sql.Timestamp(Calendar.getInstance().getTime().getTime())
//                + " inside executeAtRO for VPN form - separate block");
//
//        String val = "";
//        String stat_type = "", toWhichAdmin = "";
//        boolean isKeyAvailable = false;
//
//        if (sessionMap != null && sessionMap.get("uservalues") != null) {
//            UserData userdata = (UserData) sessionMap.get("uservalues");
//            if (userdata.isIsHOG() || userdata.isIsHOD()) {
//                //send to Admin Directly
//                stat_type = "mail-admin_pending";
//                toWhichAdmin = "VPN Admin";
//                val = "vpnsupport@nic.in";
//                updateRes = updateStatus(arr[0], stat_type, arr[1], statRemarks, val, sha_us_email, "");
//                if (updateRes) {
//                    if (updateAppType) {
//                        updateAppType(arr[0], arr[1], app_ca_type, app_ca_path);
//                    }
//                    VpnPushApi vpnapi = new VpnPushApi();
//                    vpnapi.callVpnWebService(arr[0]);
//                    isSuccess = true;
//                    isError = false;
//
//                    msg = "Application (" + reg_no + ") Approved and Forwarded Successfully to the " + toWhichAdmin + " ( " + val + " ) !";
//                } else {
//                    isSuccess = false;
//                    isError = true;
//                    msg = "Application (" + reg_no + ") could not be Forwarded !";
//                }
//            } else {
//                if ((dn.contains("o=nic employees") || dn.contains("o=nic-official-id") || dn.contains("o=dio"))) {
//                    if (userdata.isIsNICEmployee()) {
//                        stat_type = "mail-admin_pending";
//                        toWhichAdmin = "VPN Admin";
//                        val = "vpnsupport@nic.in";
//                        updateRes = updateStatus(arr[0], stat_type, arr[1], statRemarks, val, sha_us_email, "");
//                    } else {
//                        // Not possible, hence show error 
//                        updateRes = false;
//                        // fetch coordinator from VPN state coordinator table and send the request to VPN coordinator
//                        String ministry = detailHM.get("ministry").toString();
//                        String organization = detailHM.get("organization").toString();
//                        String coordinators = "";
//                        coordinators = fetchEmpCoordsAtCAForVPN(reg_no);
//
//                        //System.out.println("@@@@@@@@@@coordinators" + coordinators);
//                        Set<String> aliases = userdata.getAliases();
//                        boolean isRoCoordinator = false;
//
//                        if (!coordinators.trim().isEmpty()) {
//                            for (String aliase : aliases) {
//                                if (coordinators.contains(aliase)) {
//                                    isRoCoordinator = true;
//                                    break;
//                                }
//                            }
//
//                            if (!isRoCoordinator) {
//                                stat_type = "coordinator_pending";
//                                toWhichAdmin = "Coordinator";
//                                val = coordinators;
//                            } else {
//                                stat_type = "mail-admin_pending";
//                                toWhichAdmin = "Admin";
//                                val = "vpnsupport@nic.in";
//                            }
//                        } else {
//                            stat_type = "support_pending";
//                            toWhichAdmin = "VPN Support";
//                            val = "vpnsupport@nic.in";
//                        }
//                        updateRes = updateStatus(arr[0], stat_type, arr[1], statRemarks, val, sha_us_email, ""); //statRemarks added by pr on 5thjun18
//                    }
//                } else {
//                    // fetch coordinator from VPN state coordinator table and send the request to VPN coordinator
//                    String ministry = detailHM.get("ministry").toString();
//                    String organization = detailHM.get("organization").toString();
//                    String coordinators = "";
//                    coordinators = fetchEmpCoordsAtCAForVPN(reg_no);
//                    //System.out.println("@@@@@@@@@@coordinators" + coordinators);
//                    Set<String> aliases = userdata.getAliases();
//                    boolean isRoCoordinator = false;
//
//                    if (!coordinators.trim().isEmpty()) {
//                        for (String aliase : aliases) {
//                            if (coordinators.contains(aliase)) {
//                                isRoCoordinator = true;
//                                break;
//                            }
//                        }
//
//                        if (!isRoCoordinator) {
//                            stat_type = "coordinator_pending";
//                            toWhichAdmin = "Coordinator";
//                            val = coordinators;
//                        } else {
//                            stat_type = "mail-admin_pending";
//                            toWhichAdmin = "Admin";
//                            val = "vpnsupport@nic.in";
//                        }
//                    } else {
//                        stat_type = "support_pending";
//                        toWhichAdmin = "VPN Support";
//                        val = "vpnsupport@nic.in";
//                    }
//                    updateRes = updateStatus(arr[0], stat_type, arr[1], statRemarks, val, sha_us_email, ""); //statRemarks added by pr on 5thjun18
//                }
//
//                if (updateRes) {
//                    if (updateAppType) {
//                        updateAppType(arr[0], arr[1], app_ca_type, app_ca_path);
//                    }
//                    if (stat_type.equals("mail-admin_pending")) {
//                        VpnPushApi vpnapi = new VpnPushApi();
//                        vpnapi.callVpnWebService(arr[0]);
//                    }
//                    isSuccess = true;
//                    isError = false;
//
//                    msg = "Application (" + reg_no + ") Approved and Forwarded Successfully to the " + toWhichAdmin + " ( " + val + " ) !";
//                } else {
//                    isSuccess = false;
//                    isError = true;
//                    msg = "Application (" + reg_no + ") could not be Forwarded !";
//                }
//            }
//        }
//    }
//
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//}
