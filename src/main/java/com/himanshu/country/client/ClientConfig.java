package com.himanshu.country.client;

import feign.auth.BasicAuthRequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

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

@Slf4j
@Configuration
public class ClientConfig {
  @Bean
  @LoadBalanced
  public RestTemplate restTemplate() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, KeyManagementException {
    HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(new SSLConnectionSocketFactory(sslSocketFactory(), hostnameVerifier())).build();

    ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
    RestTemplate sslRestTemplate = new RestTemplate(requestFactory);
    return sslRestTemplate;
  }

  private SSLSocketFactory sslSocketFactory() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
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

  private HostnameVerifier hostnameVerifier() {
    return (hostname, sslSession) -> {
      if (hostname.equalsIgnoreCase("localhost")) {
        return true;
      }
      return false;
    };
  }

  public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
    return new BasicAuthRequestInterceptor("user", "password");
  }
}
