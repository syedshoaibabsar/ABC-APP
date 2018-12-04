package com.integration.sample;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RestApi extends RouteBuilder {

	@Value("${baeldung.api.path}")
	String contextPath;

	@Value("${server.port}")
	String serverPort;

	@Override
		public void configure() {
		CamelContext context = new DefaultCamelContext();
		restConfiguration()
			.contextPath(contextPath)
			.port(serverPort)
			.enableCORS(true)
			.apiContextPath("/api-doc")
			.apiProperty("api.title", "Test REST API")
			.apiProperty("api.version", "v1")
			.apiContextRouteId("doc-api")
			.component("servlet")
			.bindingMode(RestBindingMode.json);
		rest("/api/")
			.id("api-route")
			.consumes("application/json")
			.post("/bean")
			.bindingMode(RestBindingMode.json_xml)
			.type(MyBean.class)
			.to("direct:remoteService");
		from("direct:remoteService")
			.routeId("direct-route")
			.tracing()
			.log(">>> ${body.id}")
			.log(">>> ${body.name}")
			.transform().simple("Hello ${in.body.name}")
			.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200));


	}





}
