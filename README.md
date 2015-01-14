# RAML Mock Server

#### Build Status

[![Build Status](https://travis-ci.org/ozwolf-software/raml-mock-server.svg?branch=master)](https://travis-ci.org/ozwolf-software/raml-mock-server)

## Description

This tool is designed to allow validation of RAML API specifications against your own descriptors in a MockServer setup.

Utilising both the `MockServer` toolset and the `RAML Java Parser`, it provides abilities to define RAML specifications that you can ensure that `MockServer` Expectations that are hit actually meet the API specifications specified by the remote service.

## Compile and Install

To use the library, clone it locally and then do the following:

+ Local Repository - `mvn clean install`
+ Your Shared Repository
    + Manual Upload - `mvn clean package` then upload the JAR and JavaDoc JAR from `/target`
    + Person Central Repository in Maven `settings.xml` - `mvn clean deploy`
    
## Content Validation

This library currently validates specified request and response content if the `Content-Type` header is compatible with `application/json` and if the RAML specification has an appropriate schema defined for the content.

This validation is provided by the [JSON Schema Validator](https://github.com/fge/json-schema-validator) tool.
    
## Examples

### JUnit Class Rule

```java
public class MyJunitTest {
    @Rule
    public final MockServerRule server = new MockServerRule(5000);
    
    @ClassRule
    public final static RamlSpecificationsRule SPECIFICATIONS = new RamlSpecificationsRule()
            .withSpecifications(
                new ClassPathSpecification("my-local-service", "apispecs/apispecs.yml"),
                new RemoteSpecification("my-remote-service", "http://remote.site.com/apispecs.zip", ZipArchiveHandler.handler("target/specifications/my-remote-service", "apispecs.raml"))
            );
            
    @Test
    public void shouldInteractWithRemoteServiceCorrectly() {
        MockServiceClient client = new MockServiceClient("localhost", 5000);
        
        client.when(
            request()
                .withPath("/hello/world")
                .withMethod("GET")
                .withHeaders(
                    new Header(HttpHeaders.ACCEPT, "text/plain")
                )
        ).respond(
            response()
                .withStatusCode(200)
                .withHeaders(
                    new Header(HttpHeaders.CONTENT_TYPE, "text/plain")
                )
                .withBody("Hello World!")
        )
        
        Client testClient = Client.create(new DefaultClientConfig());
        
        ClientResponse response = testClient.resource("http://localhost:5000/hello/world")
                .header(HttpHeaders.ACCEPT, "text/plain")
                .get(ClientResponse.class);
                
        try {
            assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE), is("text/plain"));
            assertThat(response.getEntity(String.class), is("Hello World!"));
        } finally {
            response.close();
        }
        
        RamlSpecification.Result result = SPECIFICATIONS.get("my-remote-service").obeyedBy(client);
        
        assertTrue(result.getFormattedErrorMessage(), result.isValid());
    }
}
```

### Direct Specification Usage

```java
public class MyDirectSpecificationUsage {
    @Rule
    public final MockServerRule server = new MockServerRule(5000);
    
    private final static RamlSpecification MY_LOCAL_SERVICE = new ClassPathSpecification("my-local-service", "apispecs/apispecs.yml");
    
    @BeforeClass
    public static void setUpClass(){
        MY_LOCAL_SERVICE.initialize();
    }
    
    @Test
    public void shouldInteractWithRemoteServiceCorrectly() {
        MockServiceClient client = new MockServiceClient("localhost", 5000);
        
        client.when(
            request()
                .withPath("/hello/world")
                .withMethod("GET")
                .withHeaders(
                    new Header(HttpHeaders.ACCEPT, "text/plain")
                )
        ).respond(
            response()
                .withStatusCode(200)
                .withHeaders(
                    new Header(HttpHeaders.CONTENT_TYPE, "text/plain")
                )
                .withBody("Hello World!")
        )
        
        Client testClient = Client.create(new DefaultClientConfig());
        
        ClientResponse response = testClient.resource("http://localhost:5000/hello/world")
                .header(HttpHeaders.ACCEPT, "text/plain")
                .get(ClientResponse.class);
                
        try {
            assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE), is("text/plain"));
            assertThat(response.getEntity(String.class), is("Hello World!"));
        } finally {
            response.close();
        }
        
        RamlSpecification.Result result = MY_LOCAL_SERVICE.obeyedBy(client);
        
        assertTrue(result.getFormattedErrorMessage(), result.isValid());
    }
}
```

## Other Documentation

Please see the following documentation for other information:

+ [MockServer](http://www.mock-server.com)
+ [RAML Specifications](https://github.com/raml-org/raml-spec)
+ [RAML Java Parser](https://github.com/raml-org/raml-java-parser)
+ [JSON Schema](http://json-schema.org/)

## Other Credits

+ [JSON Schema Validator](https://github.com/fge/json-schema-validator)
+ [Zip4j](http://www.lingala.net/zip4j/)