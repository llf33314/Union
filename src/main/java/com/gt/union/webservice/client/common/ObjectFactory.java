
package com.gt.union.webservice.client.common;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.gt.union.webservice.client.common package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Test1_QNAME = new QName("http://common.server.webservice.union.gt.com/", "test1");
    private final static QName _Test1Response_QNAME = new QName("http://common.server.webservice.union.gt.com/", "test1Response");
    private final static QName _Test2_QNAME = new QName("http://common.server.webservice.union.gt.com/", "test2");
    private final static QName _Test2Response_QNAME = new QName("http://common.server.webservice.union.gt.com/", "test2Response");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.gt.union.webservice.client.common
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Test1 }
     * 
     */
    public Test1 createTest1() {
        return new Test1();
    }

    /**
     * Create an instance of {@link Test1Response }
     * 
     */
    public Test1Response createTest1Response() {
        return new Test1Response();
    }

    /**
     * Create an instance of {@link Test2 }
     * 
     */
    public Test2 createTest2() {
        return new Test2();
    }

    /**
     * Create an instance of {@link Test2Response }
     * 
     */
    public Test2Response createTest2Response() {
        return new Test2Response();
    }

    /**
     * Create an instance of {@link RequestWrapper }
     * 
     */
    public RequestWrapper createRequestWrapper() {
        return new RequestWrapper();
    }

    /**
     * Create an instance of {@link ResponseWrapper }
     * 
     */
    public ResponseWrapper createResponseWrapper() {
        return new ResponseWrapper();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Test1 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://common.server.webservice.union.gt.com/", name = "test1")
    public JAXBElement<Test1> createTest1(Test1 value) {
        return new JAXBElement<Test1>(_Test1_QNAME, Test1 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Test1Response }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://common.server.webservice.union.gt.com/", name = "test1Response")
    public JAXBElement<Test1Response> createTest1Response(Test1Response value) {
        return new JAXBElement<Test1Response>(_Test1Response_QNAME, Test1Response.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Test2 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://common.server.webservice.union.gt.com/", name = "test2")
    public JAXBElement<Test2> createTest2(Test2 value) {
        return new JAXBElement<Test2>(_Test2_QNAME, Test2 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Test2Response }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://common.server.webservice.union.gt.com/", name = "test2Response")
    public JAXBElement<Test2Response> createTest2Response(Test2Response value) {
        return new JAXBElement<Test2Response>(_Test2Response_QNAME, Test2Response.class, null, value);
    }

}
