package com.himanshu.country.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Slf4j
@SpringBootApplication
@EnableFeignClients
public class CountryRestApiClient implements CommandLineRunner {

  @Autowired
  private CountryBlotterHttpClient countryBlotterHttpClient;

  @Autowired
  private RestTemplate restTemplate; //Ribbon LoadBalanced one

  public static void main(String[] args) {
    SpringApplication.run(CountryRestApiClient.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    log.info("Making call via FeignClient: {}", countryBlotterHttpClient.getCountries());
    log.info("Making call via RibbonClient: {}", getDataUsingRibbon());
    System.exit(0);
  }

  private ResponseEntity<String> getDataUsingRibbon() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Basic "+Base64.getEncoder().encodeToString("user:password".getBytes()));
    return restTemplate.exchange("http://country-blotter/api/countries/all", HttpMethod.GET, new HttpEntity<>(headers), String.class);
  }
}
