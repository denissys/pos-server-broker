package com.ifood.service.posserverbroker.config;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDate;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
	
	@Value("${swagger.config.apiInfo.title}")
	private String title;

	@Value("${swagger.config.apiInfo.description}")
	private String description;

	@Value("${swagger.config.apiInfo.version}")
	private String version;

	@Value("${swagger.config.apiInfo.termsOfServiceUrl}")
	private String termsOfServiceUrl;

	@Value("${swagger.config.apiInfo.contact.name}")
	private String contactName;

	@Value("${swagger.config.apiInfo.contact.url}")
	private String contactUrl;

	@Value("${swagger.config.apiInfo.contact.email}")
	private String contactEmail;

	@Value("${swagger.config.apiInfo.license}")
	private String license;

	@Value("${swagger.config.apiInfo.licenseUrl}")
	private String licenseUrl;

	@Autowired
	private TypeResolver typeResolver;

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(this.apiInfo())
				.select()
					.apis(RequestHandlerSelectors.basePackage("com.ifood"))
					.paths(PathSelectors.any()).build()
					.pathMapping("/")
				.directModelSubstitute(LocalDate.class, String.class)
				.genericModelSubstitutes(ResponseEntity.class)
				.alternateTypeRules(getAlternateTypeRules())
				.useDefaultResponseMessages(false)
				.globalResponseMessage(RequestMethod.GET, newArrayList(getResponseMessage()))
				.securitySchemes(newArrayList(apiKey()))
				.securityContexts(newArrayList(securityContext()))
				.enableUrlTemplating(false);
	}

	private Contact contact() {
		final Contact contact = new Contact(this.contactName, this.contactUrl, this.contactEmail);
		return contact;
	}

	private ApiInfo apiInfo() {
		final ApiInfo apiInfo = new ApiInfo(title, description, version, termsOfServiceUrl, this.contact(), license,
				licenseUrl);
		return apiInfo;
	}

	@Bean
	SecurityConfiguration security() {
		return new SecurityConfiguration("test-app-client-id", "test-app-client-secret", "test-app-realm", "test-app",
				"apiKey", ApiKeyVehicle.HEADER, "api_key", ",");
	}

	@Bean
	UiConfiguration uiConfig() {
		return new UiConfiguration("validatorUrl");
	}

	private AlternateTypeRule getAlternateTypeRules() {
		return AlternateTypeRules.newRule(
				typeResolver.resolve(DeferredResult.class,
				typeResolver.resolve(ResponseEntity.class, WildcardType.class)),
				typeResolver.resolve(WildcardType.class));
	}

	private ResponseMessage getResponseMessage() {
		return new ResponseMessageBuilder()
					.code(500).message("500 message")
					.responseModel(new ModelRef("Error")).build();
	}

	private ApiKey apiKey() {
		return new ApiKey("mykey", "api_key", "header");
	}

	private SecurityContext securityContext() {
		return SecurityContext.builder()
					.securityReferences(defaultAuth())
					.forPaths(PathSelectors.regex("/anyPath.*")).build();
	}

	List<SecurityReference> defaultAuth() {
		final AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		final AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return newArrayList(new SecurityReference("mykey", authorizationScopes));
	}
}
