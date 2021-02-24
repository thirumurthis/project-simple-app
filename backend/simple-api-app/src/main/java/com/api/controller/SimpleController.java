package com.api.controller;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.data.Message;

@RestController
@RequestMapping("/v1")
public class SimpleController {
	
	Random rand = new Random();
	@CrossOrigin
	@GetMapping("/message")
	public Message getMessage() {
		Message msg = new Message();
		msg.setTransactionId(UUID.randomUUID().toString());
		
		int[] data = {rand.nextInt(100),rand.nextInt(100)};
		msg.setData(data);
		msg.setDate(LocalDateTime.now());
		return msg;
	}

}
