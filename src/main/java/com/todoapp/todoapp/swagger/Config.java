package com.todoapp.todoapp.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Import(BeanValidatorPluginsConfiguration.class)
@Configuration
public class Config {

        @Bean
        public Docket swaggerConfiguration() {
                Docket docket = new Docket(DocumentationType.SWAGGER_2)
                                .select()
                                .apis(RequestHandlerSelectors.basePackage("com.todoapp"))
                                .paths(PathSelectors.ant("/error").negate())
                                .build()
                                .apiInfo(apiInfo());
                docket.useDefaultResponseMessages(false);
                return appendTags(docket);
        }

        private Docket appendTags(Docket docket) {
                return docket.tags(
                                new Tag(DescriptionVariables.User,
                                                "Used to get, create, update and delete user"));
        }

        private ApiInfo apiInfo() {
                return new ApiInfoBuilder()
                                .title("Todo app API")
                                .description("Todo app API")
                                .version("1.0")
                                .build();
        }
}