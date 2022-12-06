package com.esign.demo.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.nic.eoffice.esign.CreateEsignRequest;
import org.nic.eoffice.esign.EsignResponse;
import org.nic.eoffice.esign.esign_enum.AuthMode;
import org.nic.eoffice.esign.model.EsignRequest;
import org.nic.eoffice.esign.model.EsignRequestResponse;
import org.nic.eoffice.esign.util.EsignUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.esign.demo.entities.EsignTransaction;
import com.esign.demo.model.EsignDto;
import com.esign.demo.service.EsignService;

@RequestMapping("/esignPDF")
@RestController

public class Esign {

	private final EsignService esignService;

	@Autowired
	public Esign(EsignService esignService) {
		super();
		this.esignService = esignService;
	}

	@CrossOrigin
	@RequestMapping(value = "/test")
	public String test(@RequestParam("respon") String response) throws FileNotFoundException {
		String txn = response.substring(response.indexOf("txn"), response.indexOf("><"));
		String txnStr[] = txn.split("=");

		List<EsignTransaction> transactionData = esignService
				.fetchDatafromEsignTransaction(txnStr[1].substring(1, txnStr[1].length() - 1));
		java.util.Date date = new java.util.Date();
		DateFormat dt = new SimpleDateFormat("yyyyMMdd-HHmmss");
		Long txnId = esignService.getTransactionId() + 1;
		String str = "";
		if (txnId > -1) {
			str = String.format("%06d", txnId);
			if (str.length() > 6) {
				str = str.substring(1);
			}
		}
		EsignRequest req;
		try {
			req = getEsignFileRequest(transactionData.get(0).getInputfilename(),
			transactionData.get(0).getOutputfilename(), transactionData.get(0).getPageno(),
			transactionData.get(0).getCordinates(), transactionData.get(0).getUid(),
			transactionData.get(0).getEmail());
			EsignRequestResponse esignRequestResponse = getRequestXML(req);
			EsignResponse resp = new EsignResponse();
			EsignRequestResponse esignreqsp1 = resp.getEsignResponse(esignRequestResponse.getContentType(),
					esignRequestResponse.getAppearance(), response); // This
																		// method
																		// will
																		// sign
																		// pdf
			System.out.println("Esign Response Status " + esignreqsp1.getRespStatus());
			System.out.println("Error Message if failed " + esignreqsp1.getErrorMessage());
			System.out.println("Error Code ::" + esignreqsp1.getErrorCode());
			esignService.deleteFromTransaction(txn);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("=============================");
		return "test";
		// return new RedirectView("http://localhost:4200/ESignResponsePage",
		// Boolean.TRUE);

	}

	@CrossOrigin
	@RequestMapping(value = "/esignRequest")
	public ResponseEntity<?> execute(@RequestParam("clientIp") String ip, @RequestParam("regNumber") String regNumber,
			@RequestParam("formType") String form_type, @RequestParam("email") String email,
			@RequestParam("adminRole") String admin_role) throws FileNotFoundException {
		EsignDto esignDto = new EsignDto();
		EsignTransaction esignTransaction = new EsignTransaction();
		String json = null;
		String eMail = "";
		String role = "";
		String outputFilename = "esign_" + regNumber + ".pdf";
		;
		String inputFileName = regNumber + ".pdf";
		String pageNo = "";
		String cordinates = "";
		String uid = "";
		esignDto.setClientrequestURL("https://nic-esign2gateway.nic.in/esign/acceptClient");
		esignDto.setGatewayURL("https://nic-esign2gateway.nic.in/esign/acceptClient");
		esignDto.setUsername("Meenaxi indolia");
		java.util.Date date = new java.util.Date();
		DateFormat dt = new SimpleDateFormat("yyyyMMdd-HHmmss");
		String pdate = dt.format(date);
		Long txnId = esignService.getTransactionId() + 1;
		String str = "";
		if (txnId > -1) {
			str = String.format("%06d", txnId);
			if (str.length() > 6) {
				str = str.substring(1);
			}
		}
		String txn = "032-EFORMS-" + pdate + "-" + str;
		EsignRequest req = getEsignFileRequest(inputFileName, outputFilename, pageNo, cordinates, uid, txn);
		esignTransaction.setInputfilename(inputFileName);
		esignTransaction.setOutputfilename(outputFilename);
		esignTransaction.setPageno(pageNo);
		esignTransaction.setCordinates(cordinates);
		esignTransaction.setUid(uid);
		esignTransaction.setTxn(txn);
		esignTransaction.setRefNum(regNumber);
		esignTransaction.setRole(admin_role);
		esignTransaction.setEmail(email);
		esignService.insertInToTransaction(esignTransaction);
		System.out.println(req);
		EsignRequestResponse esignRequestResponse = getRequestXML(req);
		esignDto.setXml(esignRequestResponse.getRequestXml());
		System.out.println(esignRequestResponse.getRequestXml());
		return ResponseEntity.ok().body(esignDto);
	}

	public static EsignRequest getEsignFileRequest(String inputFileName, String outputFilename, String pageNo,
			String cordinates, String uid, String txn) throws FileNotFoundException {

		String unSignedPath = "/eForms/PDF/";
		String signedPath = "/eForms/PDF/";
		EsignRequest esignReqResp = new EsignRequest();
		esignReqResp.setAspId("NICI-001");
		esignReqResp.setAadhaar("");
		esignReqResp.setSignerConsent("Y");
		esignReqResp.setContentType("file");
		esignReqResp.setAuthMode(AuthMode.OTP);
		esignReqResp.setResponseUrl(
				"https://nic-esign2gateway.nic.in/esign/response?rs=http://localhost:8888/esign-service/Esign/esignPDF/test/");
		esignReqResp.setEsignVersion("2.1");
		esignReqResp.setTxn(txn);
		esignReqResp.setReason("Approved"); // reason to sign PDF
		String signerName = "test"; // For Testing
		esignReqResp.setUserName(new String[] { signerName });// Name of signer
		inputFileName = unSignedPath + inputFileName;
		File file = ResourceUtils.getFile("classpath:PDF/IMAPPOP-FORM202102130001.pdf");
		inputFileName = file.getAbsolutePath();
		outputFilename = signedPath + outputFilename;
		String[] inputFileNames = new String[] { inputFileName };
		String[] outputFileNames = new String[] { outputFilename };
		inputFileNames = EsignUtil.removeNullFromStringArray(inputFileNames);
		outputFileNames = EsignUtil.removeNullFromStringArray(outputFileNames);
		esignReqResp.setDocInfo(new String[] { "test pdf doc info" });
		esignReqResp.setInputFilesPath(inputFileNames);
		esignReqResp.setOutputFilesPath(outputFileNames);
		if (pageNo != null && !pageNo.isEmpty()) {
			esignReqResp.setPageNo(new String[] { pageNo });
		}
		if (cordinates != null && !cordinates.isEmpty()) {
			esignReqResp.setFileCoordinate(new String[] { cordinates });
		}
		return esignReqResp;
	}

	public EsignRequestResponse getRequestXML(EsignRequest esignRequest) {
		EsignRequestResponse esignRequestResponse = null;
		try {
			CreateEsignRequest createEsignRequest = new CreateEsignRequest();
			esignRequestResponse = createEsignRequest.getRequestXml(esignRequest);
			return esignRequestResponse;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
