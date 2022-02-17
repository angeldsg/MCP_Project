package com.mcp.demo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.mcp.demo.domain.KpisDTO;
import com.mcp.demo.domain.MetricsDTO;
import com.mcp.demo.exception.RestException;
import com.mcp.demo.service.PlatformService;


@RunWith(MockitoJUnitRunner.class)
public class PlatformControllerTest {

	private static final String API_PATH = "/api/v1";
	
	@InjectMocks
	private PlatformController platformController;
	
	@Mock
	private PlatformService platformService;
	
	private MockMvc mockMvc;
	
	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders
				.standaloneSetup(platformController)
				.addPlaceholderValue("platform.api.baseUrl", "/api")
				.addPlaceholderValue("platform.api.version", "/v1")
				.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
				.build();
	}
	
	@Test
	public void testMetricsOk() throws Exception {
		
		MetricsDTO metricsDTO = new MetricsDTO();
		metricsDTO.setFileName("MCP_20180101");
		metricsDTO.setOkKoRatio("5/0");
		//TODO: fill data
		
		Mockito.when(platformService.getMetrics(Mockito.anyString())).thenReturn(metricsDTO);
		
		mockMvc.perform(get(API_PATH + "/metrics?dateRequest=20180101").contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.fileName").isNotEmpty())
		.andExpect(jsonPath("$.okKoRatio").isNotEmpty());
		
		Mockito.verify(platformService).getMetrics(Mockito.anyString());
	}
	
	@Test
	public void testMetricsKo() throws Exception {
		
		Mockito.when(platformService.getMetrics(Mockito.anyString())).thenThrow(RestException.class);
		
		mockMvc.perform(get(API_PATH + "/metrics?dateRequest=20180101").contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.fileName").doesNotExist())
		.andExpect(jsonPath("$.okKoRatio").doesNotExist());
		
		Mockito.verify(platformService).getMetrics(Mockito.anyString());
	}
	
	@Test
	public void testKpisOk() throws Exception {
		
		KpisDTO kpisDTO = new KpisDTO();
		kpisDTO.setNumFiles(5);
		kpisDTO.setNumRows(24);
		//TODO: fill data
		
		Mockito.when(platformService.getServiceKpis()).thenReturn(kpisDTO);
		
		mockMvc.perform(get(API_PATH + "/kpis").contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.numFiles").isNotEmpty())
		.andExpect(jsonPath("$.numRows").isNotEmpty());
		
		Mockito.verify(platformService).getServiceKpis();
	}
	
	@Test
	public void testKpisKo() throws Exception {
		
		Mockito.when(platformService.getServiceKpis()).thenThrow(RestException.class);
		
		mockMvc.perform(get(API_PATH + "/kpis").contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.numFiles").doesNotExist())
		.andExpect(jsonPath("$.numRows").doesNotExist());
		
		Mockito.verify(platformService).getServiceKpis();
	}
}
