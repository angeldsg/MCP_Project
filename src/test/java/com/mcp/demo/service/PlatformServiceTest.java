package com.mcp.demo.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.mcp.demo.domain.KpisDTO;
import com.mcp.demo.entity.FileNameDurationProjection;
import com.mcp.demo.repository.PlatformRepository;


@RunWith(MockitoJUnitRunner.Silent.class)
public class PlatformServiceTest {

	// IMPORTANT! tests must be executed with JUnit 4, not 5.
	
	@InjectMocks
	PlatformServiceImpl platformService;
	
	@Mock
	PlatformRepository platformRepository;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);		
	}
	
	@Test
	public void testGetKpisOK() {
		
		List<String> originList = Arrays.asList(new String[] { "34", "49" });
		List<String> destinationList = Arrays.asList(new String[] { "34", "41" });
		List<FileNameDurationProjection> durationList = new ArrayList<>();
		
		Mockito.when(platformRepository.countProcessedFiles()).thenReturn(3);
		Mockito.when(platformRepository.countProcessedRows()).thenReturn(20);
		Mockito.when(platformRepository.countProcessedValidCalls()).thenReturn(3);
		Mockito.when(platformRepository.countProcessedValidMsg()).thenReturn(01);
		Mockito.when(platformRepository.findDiffOriginCC()).thenReturn(originList);
		Mockito.when(platformRepository.findDiffDestinationCC()).thenReturn(destinationList);
		Mockito.when(platformRepository.findProcessedFileDuration()).thenReturn(durationList);
		
		KpisDTO response = platformService.getServiceKpis();
		assertNotNull(response);
		assertEquals(response.getNumFiles(), 3);
		assertEquals(response.getDiffOriginCC(), 2);
		assertEquals(response.getDiffDestinationCC(), 2);
	}
}
