package com.kh.board.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/boards")
public class BoardController {
	
	@PostMapping
	public void board() {
		
	}
}
