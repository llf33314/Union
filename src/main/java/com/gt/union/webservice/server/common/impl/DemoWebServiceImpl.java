package com.gt.union.webservice.server.common.impl;


import com.gt.union.webservice.server.common.IDemoWebService;
import com.gt.union.webservice.server.common.RequestWrapper;
import com.gt.union.webservice.server.common.ResponseWrapper;

import javax.jws.WebService;

/**
 * Created by Administrator on 2017/7/31 0031.
 */
@WebService(serviceName = "DemoWebServiceImpl", portName = "DemoWebServicePort"
        , targetNamespace = "http://common.server.webservice.union.gt.com/"
        , endpointInterface = "com.gt.union.webservice.server.common.IDemoWebService")
public class DemoWebServiceImpl implements IDemoWebService {
    @Override
    public String test1(String str) {
        return "the result is : " + str;
    }

    @Override
    public ResponseWrapper test2(RequestWrapper requestWrapper) {
        ResponseWrapper responseWrapper = new ResponseWrapper();
        responseWrapper.setResult(requestWrapper != null ? requestWrapper.getName() : "null");
        return responseWrapper;
    }
}
