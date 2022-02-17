package com.mcp.demo.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcp.demo.domain.CalculationsDTO;
import com.mcp.demo.domain.KpisDTO;
import com.mcp.demo.domain.MCPFileRowDTO;
import com.mcp.demo.domain.MessageTypeEnum;
import com.mcp.demo.domain.MetricsDTO;
import com.mcp.demo.domain.StatusCodeEnum;
import com.mcp.demo.entity.FileNameDurationProjection;
import com.mcp.demo.entity.Kpis;
import com.mcp.demo.exception.RestException;
import com.mcp.demo.repository.PlatformRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PlatformServiceImpl implements PlatformService{

	@Autowired
	private PlatformRepository repository;
	
	protected static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	
	protected static final ObjectMapper mapper = new ObjectMapper();
	
	@Override
	public MetricsDTO getMetrics(String inputStringDate) throws RestException {

		MetricsDTO metricsDTO = null;
		Kpis fileKpis = null;
		
		// Validate input date
		try {
			formatter.parse(inputStringDate);
		} catch (Exception e) {
			throw new RestException(HttpStatus.BAD_REQUEST, "invalid date");
		}
		
		
		// Connect to remote files repository
		try {
			URL remoteUrl = new URL("https://raw.githubusercontent.com/vas-test/test1/master/logs/MCP_" +inputStringDate+".json");
			
			try {
				Instant start = Instant.now();
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(remoteUrl.openStream()));
				
				CalculationsDTO calculationsDTO = new CalculationsDTO();
				String fileName = "MCP_" + inputStringDate;
				Integer missingFieldRows = 0;
				Integer errorFieldRows = 0;
				Map<String, Integer> grouppedCalls = new HashMap<>(); //map of <CC code, number of calls>
				String okKoRatio = "";
				Map<String, List<Integer>> grouppedCallDuration = new HashMap<>(); //map of <CC code, all calls duration>
				Map<String, Double> avgGrouppedCallDuration = new HashMap<>(); //map of <CC code, average call duration>
				Map<String, Integer> wordOccurrenceRanking = new HashMap<>(); //map of <word, number of occurrence>
				
				Integer numRows = 0;
				Integer numCalls = 0;
				Integer numMessages = 0;
				long duration = 0L;
				Set<String> diffOriginCC = new HashSet<>();
				Set<String> diffDestinationCC = new HashSet<>();
			    
				String line = ""; 
				
		        List<String> wordsList = new ArrayList<String>();    
				
		        // For each line (JSON) of the file:
		        while ((line = reader.readLine()) != null) {
		        	
		        	numRows++;
		        	String lineAux = line;
		        	List<String> errorList = new ArrayList<>();
		        	calculationsDTO.setHasErrors(false);
		        	
		        	try {
		        		// Try to parse the json row to an actual DTO. That includes Spring Validations
		        		MCPFileRowDTO mcpFileRowDTO = mapper.readValue(line, MCPFileRowDTO.class);
		        		
		        		// Call validations
		        		if (mcpFileRowDTO.getMessage_type().equals(MessageTypeEnum.CALL)) {
		        			numCalls++;
		        			callValidations(mcpFileRowDTO, calculationsDTO, errorList);
		        		} 
		        		// Message validations
		        		else if (mcpFileRowDTO.getMessage_type().equals(MessageTypeEnum.MSG)) {
		        			numMessages++;
		        			msgValidations(mcpFileRowDTO, calculationsDTO, wordsList, errorList);
		        		}
		        		// MSISDN country code validations
						countryCodeValidations(mcpFileRowDTO, calculationsDTO, diffOriginCC, diffDestinationCC,
								grouppedCalls, grouppedCallDuration, errorList);

		        		if (calculationsDTO.isHasErrors()) {
		        			errorList.forEach(error -> log.warn(error + "At line: "+ lineAux));
		        			errorFieldRows++;
		        		}
		        		
					} catch (Exception e) {
						log.warn("Error processing line. Missing fields at: " + line);
						missingFieldRows++;
					}
		        	
		        }
		        
		        // Do some calculations
		        Instant end = Instant.now();
		        
		        // Calculate OK / KO ratio
		        okKoRatio = calculationsDTO.getOkCalls().toString().concat("/").concat(calculationsDTO.getKoCalls().toString());
		        
		        // Calculate call durations
		        avgGrouppedCallDuration = calculateCallDurations(grouppedCallDuration);
		        
		        // Calculate the most repeated words  
		        wordOccurrenceRanking = calculateWordFrequency(wordsList);
				
				metricsDTO = new MetricsDTO(fileName, missingFieldRows, calculationsDTO.getBlankContentMessages(), errorFieldRows,
						grouppedCalls, okKoRatio, avgGrouppedCallDuration, wordOccurrenceRanking);

				// Calculate JSON processing duration (in miliseconds)
		        duration= Duration.between(start, end).getNano()/1000000;
		        
		        // Generate the arrays of different Country Codes
		        String diffOriginString = stringSetToStringList(diffOriginCC);
		        String diffDestinationString = stringSetToStringList(diffDestinationCC);
		        
				// Save the entity for kpi data
				fileKpis = new Kpis(fileName, numRows, numCalls, numMessages, duration, diffOriginString,
						diffDestinationString);
				
				// This is a saveOrUpdate method, so we used the filename as Id, and every time
				// we run the service over the same file, the application will update its values 
				repository.save(fileKpis);
				
			} catch (Exception e) {
				throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, "error reading remote file");
			}
			
		} catch (Exception e) {
			throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, "invalid URL");
		}
		
		return metricsDTO;
	}

	/*
	 * Validations for CALL message type
	 */
	private void callValidations(MCPFileRowDTO mcpFileRowDTO, CalculationsDTO calculationsDTO, List<String> errorList) {
		
		if (null == mcpFileRowDTO.getDuration()) {
			calculationsDTO.setHasErrors(true);
			errorList.add("Duration is mandatory for Calls.");
		}
		if (null == mcpFileRowDTO.getStatus_code()) {
			calculationsDTO.setHasErrors(true);
			errorList.add("Status Code is mandatory for Calls.");
		} else {
			if (!calculationsDTO.isHasErrors()) {
				// Only process the valid ones.
				if (mcpFileRowDTO.getStatus_code().equals(StatusCodeEnum.OK)) {
					calculationsDTO.setOkCalls(calculationsDTO.getOkCalls() + 1);
				} else {
					calculationsDTO.setKoCalls(calculationsDTO.getKoCalls() + 1);
				}
			}
		}
		if (StringUtils.isEmpty(mcpFileRowDTO.getStatus_description())) {
			calculationsDTO.setHasErrors(true);
			errorList.add("Status Description is mandatory for Calls.");
		}
		
	}
	
	/*
	 * Validations for MSG message type
	 */
	private void msgValidations(MCPFileRowDTO mcpFileRowDTO, CalculationsDTO calculationsDTO, List<String> wordsList,
			List<String> errorList) {
	
		if (StringUtils.isEmpty(mcpFileRowDTO.getMessage_content())) {
			calculationsDTO.setHasErrors(true);
			errorList.add("Message Content is mandatory for Messages.");
			calculationsDTO.setBlankContentMessages(calculationsDTO.getBlankContentMessages() + 1);
		} else {
			
			// Get all the words of the message content. Only process the valid ones.
			String wordsArray[] = messageToArray(mcpFileRowDTO.getMessage_content());
			for (String currentWord : wordsArray) {
				wordsList.add(currentWord);
			}
		}
	}
	
	/*
	 * MSISDN country code validations.
	 * We consider all the numbers are spanish ones, and getting the country code from the two first digits.
	 */
	private void countryCodeValidations(MCPFileRowDTO mcpFileRowDTO, CalculationsDTO calculationsDTO,
			Set<String> diffOriginCC, Set<String> diffDestinationCC, Map<String, Integer> grouppedCalls,
			Map<String, List<Integer>> grouppedCallDuration, List<String> errorList) {

		String originCountryCode = "";
		String destinationCountryCode = "";
		
		// Validate Origin field
		if (StringUtils.isEmpty(mcpFileRowDTO.getOrigin())) {
			calculationsDTO.setHasErrors(true);
			errorList.add("Origin is mandatory.");
		} else {
			originCountryCode = mcpFileRowDTO.getOrigin().substring(0, 2);
			diffOriginCC.add(originCountryCode);
		}
		
		// Validate Destination field
		if (StringUtils.isEmpty(mcpFileRowDTO.getDestination())) {
			calculationsDTO.setHasErrors(true);
			errorList.add("Destination is mandatory.");
		} else {
			destinationCountryCode = mcpFileRowDTO.getDestination().substring(0, 2);
			diffDestinationCC.add(destinationCountryCode);
		}
		
		// Collect data from calls. Only process the valid ones.
		if (!calculationsDTO.isHasErrors() && null != mcpFileRowDTO.getMessage_type()
				&& mcpFileRowDTO.getMessage_type().equals(MessageTypeEnum.CALL)) {
	
			// Fill <cc,list<duration>> map
			if (null != mcpFileRowDTO.getDuration()) {
				if (!grouppedCallDuration.containsKey(originCountryCode)) {
					grouppedCallDuration.put(originCountryCode, new ArrayList<>());
				}
				grouppedCallDuration.get(originCountryCode).add((int) mcpFileRowDTO.getDuration());
			}
			// Fill <origin/destination,numCalls> map
			if (!StringUtils.isEmpty(mcpFileRowDTO.getOrigin()) && !StringUtils.isEmpty(mcpFileRowDTO.getDestination())) {
				String key = mcpFileRowDTO.getOrigin().concat("/").concat(mcpFileRowDTO.getDestination());
				if (!grouppedCalls.containsKey(key)) {
					grouppedCalls.put(key, 0);
				}
				Integer value = grouppedCalls.get(key);
				grouppedCalls.put(key, value + 1);
			}
		}
	}
	
	/*
	 * Calculate call durations groupped by Country Code
	 */
	private Map<String, Double> calculateCallDurations(Map<String, List<Integer>> grouppedCallDuration) {
		
		Map<String, Double> avgGrouppedCallDuration = new HashMap<>();
		
		grouppedCallDuration.entrySet().stream().forEach(entry -> {
			
			List<Integer> valuesList = entry.getValue();
			int[] valuesArray = valuesList.stream().mapToInt(Integer::intValue).toArray();
			double avg = Arrays.stream(valuesArray).average().orElse(Double.NaN);
			avgGrouppedCallDuration.put(entry.getKey(), avg);
		});
		
		return avgGrouppedCallDuration;
	}

	/*
	 * Calculate the words frequency in a List
	 */
	private Map<String, Integer> calculateWordFrequency(List<String> wordsList) {
		
		Map<String, Integer> wordOccurenceRanking = new HashMap<>();
		
		wordsList.forEach(word -> {
			if (!StringUtils.isEmpty(word)) {
				int wordCount=Collections.frequency(wordsList, word);
		    	if (!wordOccurenceRanking.containsKey(word)) {
		    		wordOccurenceRanking.put(word, wordCount);
		    	}
			}
		});
		
		return wordOccurenceRanking;
	}

	/*
	 * Converts a Set of Strings into a String representing an Array
	 */
	private String stringSetToStringList(Set<String> inputSet) {
		
		String inputString = inputSet.toString();
		return inputString.substring(1, inputString.length()-1);
	}

	/*
	 * Remove all non words and split by words
	 */
	private String[] messageToArray (String message) {
		
		String messageAux = message.replaceAll("[\\d+]", " ").replaceAll("[^\\w]", " ");
		String[] words = messageAux.split("\\s+");

		return words;
	}

	@Override
	public KpisDTO getServiceKpis() {

		// We can improve the speed of this service, using threads (one for each query),
		// but considering the amount of data processed, the election of in-memory database and the fact that this is a demo
		// application we can run the queries synchronous pretty fast.
		
		KpisDTO kpisDTO= new KpisDTO();

	    // Total number of processed JSON files (even duplicated)
		Integer countProcessedFiles = repository.countProcessedFiles();
		
	    // Total number of rows
		Integer countProcessedRows = repository.countProcessedRows();
		
	    // Total number of calls (valid ones)
		Integer countProcessedValidCalls = repository.countProcessedValidCalls();
		
	    // Total number of messages (valid ones)
		Integer countProcessedValidMsg = repository.countProcessedValidMsg();
		
	    // Total number of different origin country codes (https://en.wikipedia.org/wiki/MSISDN)
		List<String> findDiffOriginCC = repository.findDiffOriginCC();
		Set<String> originSet = new HashSet<>();
		findDiffOriginCC.forEach(origin -> {
			List<String> originList = new ArrayList<String>(Arrays.asList(origin.split(",")));
			originList.forEach(x-> originSet.add(x.trim()));
		});
		
	    // Total number of different destination country codes (https://en.wikipedia.org/wiki/MSISDN)
		List<String> findDiffDestinationCC = repository.findDiffDestinationCC();
		Set<String> destinationSet = new HashSet<>();
		findDiffDestinationCC.forEach(destination -> {
			List<String> destinationList = new ArrayList<String>(Arrays.asList(destination.split(",")));
			destinationList.forEach(x-> destinationSet.add(x.trim()));
		});
		
	    // Duration of each JSON process
		List<FileNameDurationProjection> findProcessedFileDuration = repository.findProcessedFileDuration();
		Map<String, Long> fileDurationMap = new HashMap<>();
		findProcessedFileDuration.forEach(e -> {
			fileDurationMap.put(e.getFileName(), e.getDuration());
		});
		
		kpisDTO.setNumFiles(countProcessedFiles);
		kpisDTO.setNumRows(countProcessedRows);
		kpisDTO.setNumCalls(countProcessedValidCalls);
		kpisDTO.setNumMessages(countProcessedValidMsg);
		kpisDTO.setDiffOriginCC(originSet.size());
		kpisDTO.setDiffDestinationCC(destinationSet.size());
		kpisDTO.setFileProcessingDurationMap(fileDurationMap);
		
		return kpisDTO;
	}

}
