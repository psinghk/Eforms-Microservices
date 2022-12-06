package com.esign.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.esign.demo.entities.esign;
import com.esign.demo.entities.EsignTransaction;

@Repository
public interface EsignBaseRepo extends JpaRepository<esign, Long> {

	@Query(value = "select esigntbl.id from esign esigntbl")
	Long getTransactionId();

	@Modifying
	@Query(value = "update esign esigntbl set esigntbl.id= :id")
	int updateTransactionId(@Param(value = "id") Long id);

}
