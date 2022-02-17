package com.mcp.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.mcp.demo.entity.FileNameDurationProjection;
import com.mcp.demo.entity.Kpis;

public interface PlatformRepository extends CrudRepository<Kpis, Integer> {

	@Query("select count(*) from Kpis")
	Integer countProcessedFiles();
	
	@Query("select sum(numRows) from Kpis")
	Integer countProcessedRows();
	
	@Query("select sum(numCalls) from Kpis")
	Integer countProcessedValidCalls();
	
	@Query("select sum(numMessages) from Kpis")
	Integer countProcessedValidMsg();
	
	@Query("select diffOriginCC from Kpis")
	List<String> findDiffOriginCC();
	
	@Query("select diffDestinationCC from Kpis")
	List<String> findDiffDestinationCC();
	
	@Query("select fileName as fileName, duration as duration from Kpis")
	List<FileNameDurationProjection> findProcessedFileDuration();
}
