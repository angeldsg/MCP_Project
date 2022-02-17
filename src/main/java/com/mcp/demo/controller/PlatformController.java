package com.mcp.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mcp.demo.domain.KpisDTO;
import com.mcp.demo.domain.MetricsDTO;
import com.mcp.demo.service.PlatformService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
@RestController
@RequestMapping("${platform.api.baseUrl}" + "${platform.api.version}")
@Api("Api REST for Mobile Communication Platform services management")
@Slf4j
@CrossOrigin // needed to allow clients to access to resources located in a different server 
public class PlatformController {

	@Autowired
	private PlatformService service;
	
    @GetMapping("/metrics")
    @ApiOperation(value = "Get Metrics", notes = "Return 200")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Get Metrics", response = MetricsDTO.class),
            @ApiResponse(code = 400, message = "Bad request, review the request param"),
            @ApiResponse(code = 401, message = "Wrong credentials. Incorrect username or password"),
            @ApiResponse(code = 500, message = "Unexpected exception (Internal Server Error)")})
    public ResponseEntity<?> getMetrics(
    		@ApiParam(value = "Date requested", name = "dateRequest", required = true, type = "String") @RequestParam(value = "dateRequest", required = true) String dateRequest) throws Exception {
    	
    	log.debug("Get Metrics Init");

        HttpStatus code = HttpStatus.OK;
        MetricsDTO metricsDTO = new MetricsDTO();

        try {
        	metricsDTO = service.getMetrics(dateRequest);

        } catch (Exception e) {
            code = HttpStatus.NOT_FOUND;
        }

        log.debug("Get Metrics End ");

        return new ResponseEntity<>(metricsDTO, code);
    }
    
    @GetMapping("/kpis")
    @ApiOperation(value = "Get Service Kpis", notes = "Return 200")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Get Service Kpis", response = KpisDTO.class),
            @ApiResponse(code = 400, message = "Bad request, review the request param"),
            @ApiResponse(code = 401, message = "Wrong credentials. Incorrect username or password"),
            @ApiResponse(code = 500, message = "Unexpected exception (Internal Server Error)")})
    public ResponseEntity<?> getServiceKpis() throws Exception {
    	
    	log.debug("Get Service Kpis Init");

        HttpStatus code = HttpStatus.OK;
        KpisDTO kpisDTO = new KpisDTO();

        try {
        	kpisDTO = service.getServiceKpis();

        } catch (Exception e) {
            code = HttpStatus.NOT_FOUND;
        }

        log.debug("Get Service Kpis End ");

        return new ResponseEntity<>(kpisDTO, code);
    }
}
