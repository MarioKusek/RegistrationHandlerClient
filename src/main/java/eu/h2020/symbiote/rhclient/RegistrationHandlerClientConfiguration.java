package eu.h2020.symbiote.rhclient;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableDiscoveryClient
@EnableFeignClients
@ComponentScan(basePackages = {"eu.h2020.symbiote.rhclient"})
public class RegistrationHandlerClientConfiguration {
}
