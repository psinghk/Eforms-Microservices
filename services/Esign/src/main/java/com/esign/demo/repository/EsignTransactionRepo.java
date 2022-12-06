package com.esign.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.esign.demo.entities.EsignTransaction;

@Repository
public interface EsignTransactionRepo extends JpaRepository<EsignTransaction, Long> {


	@Query(value = "select esigntransaction from EsignTransaction esigntransaction where esigntransaction.txn=:txn")
	List<EsignTransaction> fetchDatafromEsignTransaction(@Param(value = "txn") String txn);
	
	@Query(value = "delete from EsignTransaction esignTransaction where esignTransaction.txn=:txn")
	void deleteFromTransaction(@Param(value = "txn") String txn);

}
