package com.mcp.demo.domain;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Valid
public class MCPFileRowDTO {


    private long id;
    
    @NotNull
    private MessageTypeEnum message_type;
    
    @NotNull
    private Integer timestamp;
    
    @NotNull
    private String origin;
    
    @NotNull
    private String destination;
    
    private Integer duration;
    
    private StatusCodeEnum status_code;
    
    private String status_description;
    
    private String message_content;
    
    @NotNull
    private MessageStatusEnum message_status;

}
