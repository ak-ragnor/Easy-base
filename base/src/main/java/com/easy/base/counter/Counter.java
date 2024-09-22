package com.easy.base.counter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("counters")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Counter {

    @Id
    private String id;
    private long seq;

}
