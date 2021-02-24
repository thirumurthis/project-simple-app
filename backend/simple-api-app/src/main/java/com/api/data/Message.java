package com.api.data;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Message {

	int data[];
    String transactionId;
	LocalDateTime date;

}
