package com.himanshu.country.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name="country-blotter", configuration = FeignSSLConfiguration.class, url="${country-blotter.ribbon.listOfServers}")
public interface CountryBlotterHttpClient {
  @RequestMapping(method = RequestMethod.GET, value = "/api/countries/all")
  String getCountries();
}
