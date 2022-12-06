package com.esign.demo.entities;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "esigntransaction")
@Access(value = AccessType.FIELD)
@Data
public class EsignTransaction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String inputfilename;
	private String outputfilename;
	private String pageno;
	private String cordinates;
	private String uid;
	private String refNum;
	private String role;
	private String email;
	private String txn;
	

}
