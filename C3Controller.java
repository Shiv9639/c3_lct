package com.C3Collection.C3.Controller;
import com.C3Collection.C3.Service.C3ImpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
//@RequestMapping("/filter")
public class C3Controller {

	@Autowired
	private com.C3Collection.C3.Service.C3ImpService c3ImpService;
//	@PostMapping("/f1")
	@Scheduled(fixedRate=30, timeUnit = TimeUnit.SECONDS)
	public void filterC3Data() throws Exception {
			c3ImpService.C3DataFiltering();
		//return new ResponseEntity<>(HttpStatus.OK);
	}
//	@Scheduled(fixedRate = 10, timeUnit = TimeUnit.SECONDS)
	//@PostMapping("/PostToMongo")
	public void PostDataToMongo() throws Exception{
	//	c3ImpService.readDatFileToMongo();
		//return new ResponseEntity<>(HttpStatus.OK);
	}

}