package com.gt.union.config;

import com.gt.union.webservice.server.common.IDemoWebService;
import com.gt.union.webservice.server.common.impl.DemoWebServiceImpl;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.ws.Endpoint;

/**
 * Created by Administrator on 2017/7/19 0019.
 */
@Configuration
public class CxfConfig {

    @Bean(name = Bus.DEFAULT_BUS_ID)
    public SpringBus springBus() {
        return new SpringBus();
    }

    @Bean
    public IDemoWebService demoService() {
        return new DemoWebServiceImpl();
    }

    @Bean
    public Endpoint demoEndPoint() {
        EndpointImpl endpoint = new EndpointImpl(springBus(), demoService());
        endpoint.publish("/demo");
        return endpoint;
    }

}
