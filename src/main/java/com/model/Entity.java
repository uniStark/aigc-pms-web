package com.model;

import lombok.Data;

/**
 * @author BeamStark
 * @date 2023-10-30-01:53
 */
@Data
public class Entity {
    private String context;
    private Integer threadCount;
    private Integer timeout;
    private String outFilePath;
    private String apiKey;
    private String url;
    private String csvFile;
}
