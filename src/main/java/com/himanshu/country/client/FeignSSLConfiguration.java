package com.himanshu.country.client;

import feign.Client;
import feign.Logger;
import feign.auth.BasicAuthRequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/**
 * We will not mark this as @Configuration, as it's configuration specific for feign client, and we will load it when we declare Feign interface using @FeignClient
 * Also read this https://cloud.spring.io/spring-cloud-netflix/multi/multi_spring-cloud-feign.html
 * IMPORTANT READ - https://cloud.spring.io/spring-cloud-netflix/multi/multi_spring-cloud-feign.html
 * @See org.springframework.cloud.openfeign.FeignAutoConfiguration
 * {@link feign.Feign}
 * {@link feign.Client.Default}
 */
@Slf4j
public class FeignSSLConfiguration {
  //SSLSocketFactory sslContextFactory, HostnameVerifier hostnameVerifier

  @Bean
  Client feignClient(SSLSocketFactory sslSocketFactory, HostnameVerifier hostnameVerifier) {
    return new Client.Default(sslSocketFactory, hostnameVerifier);
  }

  @Bean
  Logger.Level feignLoggerLevel() {
    return Logger.Level.FULL;
  }


  @Bean
  public SSLSocketFactory sslSocketFactory() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
    KeyStore keyStore = null;
    TrustManagerFactory tmf = null;
    SSLContext sslContext = null;

    try {
      keyStore = KeyStore.getInstance("JKS");
      InputStream is = null;
      String certificateFilePath = FeignSSLConfiguration.class.getResource("/").getFile().concat("/keystore.p12");
      log.info("Certificate file path: {}", certificateFilePath);
      FileInputStream fileInputStream = new FileInputStream(certificateFilePath);
      keyStore.load(fileInputStream, "changeit".toCharArray());
      tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      tmf.init(keyStore);
      sslContext = SSLContext.getInstance("TLSv1.2");
      sslContext.init(null, tmf.getTrustManagers(), null);
      SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
      return sslSocketFactory;
    } catch (Exception e) {
      throw e;
    }
  }

  @Bean
  public HostnameVerifier hostnameVerifier() {
    return (hostname, sslSession) -> {
      if (hostname.equalsIgnoreCase("localhost")) {
        return true;
      }
      return false;
    };
  }

  @Bean
  public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
    return new BasicAuthRequestInterceptor("user", "password");
  }
}
