package com.zgczx.repository.mysql3.unifiedlogin.dao;


import com.zgczx.repository.mysql3.unifiedlogin.model.Monitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * 
 * @author changzhijun
 * @date 2017-12-27
 *
 */
@Repository
public interface MonitorDao extends JpaRepository<Monitor, Long> {

	public List<Monitor> getMonitorByIpAndDateAndName(String ip, String date, String name);
	
	@Query(value = "update monitor set pv=pv+1 where ip=?1 and date=?2 and name=?3", nativeQuery = true)
	@Modifying
	public int add(String ip, String date, String name);
	
	@Query(value = "update monitor set pv=pv-1 where  ip=?1 and date=?2 and name=?3 ", nativeQuery = true)
	@Modifying
	public int minus(String ip);


	 

}
