package com.gt.union.webservice.server.common;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * Created by Administrator on 2017/7/31 0031.
 */
/**
 * wsdl2java生成客户端命令：wsdl2java -encoding utf-8 -client -p com.gt.union.webservice.client.common http://localhost:8080/webservices/demo?wsdl
 */

@WebService
public interface IDemoWebService {
    @WebMethod(operationName = "test1")
    String test1(String str);

    @WebMethod(operationName = "test2")
    ResponseWrapper test2(RequestWrapper requestWrapper);
}
