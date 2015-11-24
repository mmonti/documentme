package org.mmonti.documentme.config;
import org.mmonti.documentme.endpoint.Endpoints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by mmonti on 11/23/15.
 */
@Configuration
@EnableConfigurationProperties
public class DocumentMeConfiguration implements DocumentMeInitializer {

    @Autowired
    private DocumentMeProperties documentMeProperties;

    @Bean
    public Endpoints endpoints(){
        return new Endpoints(documentMeProperties.getSource(), documentMeProperties.getPattern());
    }

    @Bean
    public DocumentMeProperties documentMeProperties() {
        return new DocumentMeProperties();
    }

}

