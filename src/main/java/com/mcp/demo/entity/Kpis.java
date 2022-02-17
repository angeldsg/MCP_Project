package com.mcp.demo.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="kpis")
public class Kpis {

//    public Kpis(String fileName, Integer numRows, Integer numCalls, Integer numMessages, long duration,
//    		String diffOriginCC, String diffDestinationCC) {
//		super();
//		this.fileName = fileName;
//		this.numRows = numRows;
//		this.numCalls = numCalls;
//		this.numMessages = numMessages;
//		this.duration = duration;
//		this.diffOriginCC = diffOriginCC;
//		this.diffDestinationCC = diffDestinationCC;
//	}

//	@Id
//    @GeneratedValue(strategy=GenerationType.AUTO)
//    private long id;
    
	@Id
//    @Column(name = "file_name", nullable = false)
    private String fileName;
    
    @Column(name = "num_rows", nullable = false)
    private Integer numRows;
    
    @Column(name = "num_calls", nullable = false)
    private Integer numCalls;
    
    @Column(name = "num_messages", nullable = false)
    private Integer numMessages;
    
    @Column(name = "duration", nullable = false)
    private long duration;
    
    @Column(name = "diff_origin_cc", nullable = false)
    private String diffOriginCC;
    
    @Column(name = "diff_destination_cc", nullable = false)
    private String diffDestinationCC;
    

}
