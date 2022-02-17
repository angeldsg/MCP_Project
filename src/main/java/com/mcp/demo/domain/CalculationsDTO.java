package com.mcp.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalculationsDTO {

	Integer missingFieldRows = 0;
	Integer blankContentMessages = 0;
	Integer errorFieldRows = 0;
	Integer okCalls = 0;
	Integer koCalls = 0;
	boolean hasErrors = false;
}
