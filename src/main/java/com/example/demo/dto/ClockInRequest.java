package com.example.demo.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClockInRequest {
    private Long workerId;
    private Long siteId;
}