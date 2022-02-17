package com.mcp.demo.domain;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KpisDTO{
    
    private Integer numFiles;
    
    private Integer numRows;
    
    private Integer numCalls;
    
    private Integer numMessages;
    
    private Map<String, Long> fileProcessingDurationMap;
    
    private Integer diffOriginCC;
    
    private Integer diffDestinationCC;
}
