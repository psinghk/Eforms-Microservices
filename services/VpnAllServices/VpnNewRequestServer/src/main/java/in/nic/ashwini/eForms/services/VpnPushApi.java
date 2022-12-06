package in.nic.ashwini.eForms.services;

import org.springframework.stereotype.Service;

//this class is created for RabbitMQ
@Service
public class VpnPushApi {
	
	
	public boolean callVpnWebService() {
		/*
		try {
            
            System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ me called nitin");
            NotifyThrouhRabbitMQ object = new NotifyThrouhRabbitMQ();
            object.sendVpnData(reg_no);
            System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ me called nitin"+object+"REGISTRATION NUMBER"+reg_no);
//            //String vpn_url = "http://localhost:8083/vpn/" + reg_no;
//            //URL obj = new URL(vpn_url);
//            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//            con.setRequestMethod("GET");
//            //String user_name = "eforms";
//            //String password = "eforms@#api78$";
//            //String userCredentials = "username : eforms password : eforms@#api78$";
//            //byte[] plainCredsBytes = userCredentials.getBytes();
//            //byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
//            //String base64Creds = new String(base64CredsBytes);
//            // String basicAuth = "Basic " + new String(new Base64().encode(userCredentials.getBytes()));
//            //con.setRequestProperty("Authorization", base64Creds);
//
//            //con.setRequestProperty("Authorization", "Basic ZWZvcm1zOmVmb3Jtc0AjYXBpNzgk");
//            //con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; Win64; x64; rv:27.0) Gecko/20100101 Firefox/27.0.2 Waterfox/27.0");
//            con.setRequestProperty("Content-Type", "application/json");
//
//            String urlParameters = reg_no;
//
//            //String urlParameters = "{ \"vpn_registration_no\": \"" + vpn_no + "\", \"email\": \"" + userdata.getEmail() + "\", \"mobile\": \"" + userdata.getMobile() + "\" }";
//            con.setDoOutput(true);
//            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//            //wr.writeBytes(urlParameters);
//            wr.flush();
//            wr.close();
//            int responseCode = con.getResponseCode();
//            System.out.println("Post parameters : " + urlParameters);
//            System.out.println("Response Code : " + responseCode);
//            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//            String inputLine;
//            StringBuffer response = new StringBuffer();
//            while ((inputLine = in.readLine()) != null) {
//                response.append(inputLine);
//            }
//            in.close();
//            //print result
//            System.out.println("API Response from" + response.toString());
        } catch (Exception e) {
            
            System.out.println(ServletActionContext.getRequest().getSession().getId() + " == IP: " + ip + " timestamp: == "
                    + new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()) + " == " + " RabbitMqConnectionFactory exception " + e.getMessage());
            e.printStackTrace();

        }
		*/
        return true;		
		
	}
	
	

}
