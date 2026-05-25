package com.example.demo.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActiveWorkerDTO {
    private Long workerId;
    private String workerName;
    private Long siteId;
    private String siteName;
    private String clockInTime;
}