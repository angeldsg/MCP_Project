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
	
//	@Value("${onesaitplatform.api.realmId}")
//	protected String realmId;
//	
//	@Value("${openplatform.api.auth.token.clientId}")
//	protected String clientId;
//	
//	@Value("${openplatform.api.auth.token.password}")
//	protected String clientSecret;
	
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select().apis(RequestHandlerSelectors.basePackage(basepackages))
				.paths(PathSelectors.any())
				.build().apiInfo(documentsApiInfo());
	}
	
//	@Bean
//	public Docket documentsApi() {
//		return new Docket(DocumentationType.SWAGGER_2)
//				.securitySchemes(Arrays.asList(apiKey()))
//				.securityContexts(Arrays.asList(securityContext()))
//				.apiInfo(documentsApiInfo()).select()
//				.apis(RequestHandlerSelectors.basePackage(basepackages))
//				.paths(documentsPaths())
//				.build();
//	}

//	@Bean
//	public SecurityConfiguration security() {
//			return SecurityConfigurationBuilder.builder()
////					.clientId(clientId)
////					.clientSecret(clientSecret)
////					.realm(realmId)
//					.appName("swagger-app")
//					.scopeSeparator(",")
//					.additionalQueryStringParams(null)
//					.useBasicAuthenticationWithAccessCodeGrant(false)
//					.build();
//	}	
	
//    private ApiKey apiKey() {
//        return new ApiKey("apiKey", "Authorization", "header");
//    }
//    
//    private SecurityContext securityContext() {
//		return SecurityContext.builder()
//				.securityReferences(defaultAuth())
//				.forPaths(PathSelectors.regex("/api/*"))
//				.build();
//    }
    
//    private List<SecurityReference> defaultAuth() {
//    	AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
//    	AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
//    	authorizationScopes[0] = authorizationScope;
//    	return Arrays.asList(new SecurityReference("apiKey", authorizationScopes));
//    }
//    
//	private Predicate<String> documentsPaths() {
//
//		return regex("/api.*");
//	}
	
	private ApiInfo documentsApiInfo() {
		return new ApiInfoBuilder()
				.title("Mobile Communication Platform "+ serviceName +" Management API")
				.description("Mobile Communication Platform "+ serviceName +" Management REST Services")
				.license("Apache License Version 2.0")
				.version("0.0.1")
				.contact(new Contact("", "", "")).build();
	}
	
}
