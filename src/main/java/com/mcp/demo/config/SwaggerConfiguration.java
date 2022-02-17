package com.mcp.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class SwaggerConfiguration {

	@Value("${swagger.basepackages}")
	protected String basepackages;
	
	@Value("${swagger.serviceName}")
	protected String serviceName;
	
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select().apis(RequestHandlerSelectors.basePackage(basepackages))
				.paths(PathSelectors.any())
				.build().apiInfo(documentsApiInfo());
	}
	
	private ApiInfo documentsApiInfo() {
		return new ApiInfoBuilder()
				.title("Mobile Communication Platform "+ serviceName +" Management API")
				.description("Mobile Communication Platform "+ serviceName +" Management REST Services")
				.license("Apache License Version 2.0")
				.version("0.0.1")
				.contact(new Contact("", "", "")).build();
	}
	
}
