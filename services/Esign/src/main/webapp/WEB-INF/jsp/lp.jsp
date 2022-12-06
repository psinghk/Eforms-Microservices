<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>Insert title here</title>
<script type="text/javascript">
                function proceed() {

                
                window.location.href = "esignPDF";
                document.getElementById("esignForm").submit();

            }
        </script>

    </head>
    <body onload="proceed()">
  
    <form:form method="POST" id="esignForm" commandName="hiddenMessage" action="%{gatewayURL}">
   
        <table>
            <tr>              
                <input name="xml" id="xml" type="hidden" value="%{xmlContent}"/>
            </tr>
              <tr>              
                <input name="clientrequestURL" id="xml" type="hidden" value="https://eforms.nic.in/ESignResponsePage"/>
            </tr>
              <tr>              
                <input name="username" id="xml" type="hidden" value="%{userName}"/>
            </tr>
            <tr>              
                <input name="gatewayURL" id="xml" type="hidden" value="gatewayURL"/>
            </tr>
            <tr>
        </table>
    </form:form>

    </body> 
</html>

 