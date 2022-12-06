package in.nic.ashwini.eForms.models;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Component
public class FileUploadPojo {
    @JsonIgnore
	private String fileUploadName = null;
    @JsonIgnore
	private String errorMsg = null;
	private String fileUploadValidName = null;
	private String fileUploadNotValidName = null;
	private String fileUploadErrorName = null;
    @JsonIgnore
	private String fileNamePattern = null;
    private String registrationNO= null;
    
	public String getRegistrationNO() {
		return registrationNO;
	}
	public void setRegistrationNO(String registrationNO) {
		this.registrationNO = registrationNO;
	}
	public String getFileUploadName() {
		return fileUploadName;
	}
	public void setFileUploadName(String fileUploadName) {
		this.fileUploadName = fileUploadName;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public String getFileUploadValidName() {
		return fileUploadValidName;
	}
	public void setFileUploadValidName(String fileUploadValidName) {
		this.fileUploadValidName = fileUploadValidName;
	}
	public String getFileUploadNotValidName() {
		return fileUploadNotValidName;
	}
	public void setFileUploadNotValidName(String fileUploadNotValidName) {
		this.fileUploadNotValidName = fileUploadNotValidName;
	}
	public String getFileUploadErrorName() {
		return fileUploadErrorName;
	}
	public void setFileUploadErrorName(String fileUploadErrorName) {
		this.fileUploadErrorName = fileUploadErrorName;
	}
	public String getFileNamePattern() {
		return fileNamePattern;
	}
	public void setFileNamePattern(String fileNamePattern) {
		this.fileNamePattern = fileNamePattern;
	}
	@Override
	public String toString() {
		return "FileUploadPojo [fileUploadName=" + fileUploadName + ", errorMsg=" + errorMsg + ", fileUploadValidName="
				+ fileUploadValidName + ", fileUploadNotValidName=" + fileUploadNotValidName + ", fileUploadErrorName="
				+ fileUploadErrorName + ", fileNamePattern=" + fileNamePattern + "]";
	}
	
}
