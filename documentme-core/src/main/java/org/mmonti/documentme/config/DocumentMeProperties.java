package org.mmonti.documentme.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by mmonti on 11/23/15.
 */
@ConfigurationProperties(prefix = "documentme")
public class DocumentMeProperties {

    private String source;
    private String pattern;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
}
