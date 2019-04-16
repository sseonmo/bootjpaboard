package me.seon.bootjpaboard.config;

import com.samskivert.mustache.Mustache;
import org.springframework.boot.autoconfigure.mustache.MustacheEnvironmentCollector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.env.Environment;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/**")
				.addResourceLocations("classpath:/static/")
				.setCachePeriod(20)
				;
	}

	//	mustache default headler
//	참조 - https://www.baeldung.com/spring-boot-mustache
//	https://supawer0728.github.io/2018/03/14/Mustache/
	@Bean
	public Mustache.Compiler mustacheCompiler(Mustache.TemplateLoader templateLoader, Environment environment) {

		MustacheEnvironmentCollector collector = new MustacheEnvironmentCollector();
		collector.setEnvironment(environment);

		return Mustache.compiler()
				.defaultValue("Some Default Value")
				.withLoader(templateLoader)
				.withCollector(collector);
	}
}
