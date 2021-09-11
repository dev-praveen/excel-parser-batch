package com.excel.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExcelBatchParserApplication {

	public static void main(String[] args) {
		System.exit(SpringApplication.exit(SpringApplication.run(ExcelBatchParserApplication.class, args)));
	}
}