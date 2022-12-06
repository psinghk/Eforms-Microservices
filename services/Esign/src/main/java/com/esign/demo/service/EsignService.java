package com.esign.demo.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.esign.demo.entities.EsignTransaction;
import com.esign.demo.repository.EsignBaseRepo;
import com.esign.demo.repository.EsignTransactionRepo;


@Service
public class EsignService {
	
@Autowired
public EsignBaseRepo esignBaseRepo;
@Autowired
public EsignTransactionRepo esignTransactionrepo;

@Transactional
	public Long getTransactionId() {
		
		Long i = esignBaseRepo.getTransactionId();
		esignBaseRepo.updateTransactionId(i);
 
        return i;
    }


	public List<EsignTransaction> fetchDatafromEsignTransaction(String txn) {
		return esignTransactionrepo.fetchDatafromEsignTransaction(txn);
	}

	@Transactional
	public void deleteFromTransaction(String txn) {
		esignTransactionrepo.deleteFromTransaction(txn);
		// TODO Auto-generated method stub
		
	}

	@Transactional
	public EsignTransaction insertInToTransaction(EsignTransaction esignTransaction) {
		 return esignTransactionrepo.save(esignTransaction);
		 
		
	}
}
