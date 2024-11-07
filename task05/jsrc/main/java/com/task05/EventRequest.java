package com.task05;

import java.util.*;
import lombok.*;

//@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor

public class EventRequest {
    public int principalId;
    public Map<String, String> content;
}