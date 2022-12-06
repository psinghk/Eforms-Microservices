package com.esign.demo.model;

import java.time.LocalDateTime;


import lombok.Data;

@Data
public class ProfileDto {
	private Long id;
	
	private String email;
	private String mobile;
	private String name;
	private String designation;
	private String employment;
	private String ministry;
	private String department;
	private String otherDept;
	private String state;
	private String organization;
	private String empCode;
	private String postingState;
	private String city;
	private String address;
	private String pin;
	private String officePhone;
	private String residencePhone;
	private String hodName;
	private String hodEmail;
	private String hodMobile;
	private String hodDesignation;
	private String hodTelephone;
	private String roName;
	private String roEmail;
	private String roMobile;
	private String roDesignation;
	private String usEmail;
	private String usName;
	private String usMobile;
	private String usDesignation;
	private String usTelephone;
	private LocalDateTime creationTimeStamp;
	private LocalDateTime updationTimeStamp;
	}
