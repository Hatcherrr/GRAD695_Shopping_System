package edu.hu.ssbe.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private String id;
    private String providerid;
    @JsonFormat( pattern="yyyy-MM-dd HH:mm:ss")
    private Date createdon;
    @JsonFormat( pattern="yyyy-MM-dd HH:mm:ss")
    private Date lastupdate;
    private String name;
    private String description;
    private Long amount;
    private List<String> tags;
    private List<String> pictures;
    private String status;
}
