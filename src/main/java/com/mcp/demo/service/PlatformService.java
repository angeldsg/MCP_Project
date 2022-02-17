package com.mcp.demo.service;

import com.mcp.demo.domain.KpisDTO;
import com.mcp.demo.domain.MetricsDTO;
import com.mcp.demo.exception.RestException;

public interface PlatformService {

	MetricsDTO getMetrics(String dateRequest) throws RestException;

	KpisDTO getServiceKpis() throws RestException;

}
