package com.task05;

import lombok.*;
import java.util.*;

//@Builder
//@Setter
//@Getter
//@NoArgsConstructor
//@AllArgsConstructor

public class EventResponse {
    public int statusCode;
    public Map<String, Object> event;

    public EventResponse(int statusCode, Map<String, Object> event) {
        this.statusCode = statusCode;
        this.event = event;
    }
}