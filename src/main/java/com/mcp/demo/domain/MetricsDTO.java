package com.mcp.demo.domain;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetricsDTO{

    private String fileName;
    
    private Integer missingFieldRows;
    
    private Integer blankContentMessages;
    
    private Integer errorFieldRows;
    
    private Map<String, Integer> grouppedCalls;
    
    private String okKoRatio;
    
    private Map<String, Double> avgGrouppedCallDuration;
    
    private Map<String, Integer> wordOccurenceRanking;

    
}
