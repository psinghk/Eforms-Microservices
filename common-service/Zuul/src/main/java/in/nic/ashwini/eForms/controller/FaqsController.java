package in.nic.ashwini.eForms.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class FaqsController {
	
	@GetMapping("/eforms-faqs")
	public Map<String, Object> fetchFaqs() {
		return null;
	}
}
