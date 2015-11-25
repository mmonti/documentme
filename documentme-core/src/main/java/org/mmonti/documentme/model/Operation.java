package org.mmonti.documentme.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mmonti on 11/21/15.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode
public class Operation {

    private String path;
    private String methodName;
    private RequestMethod method;
    private String comment;

    private Map<String, Object> responseHeaders;
    private Map<String, Object> responseStatus;

    @JsonProperty("arguments")
    private Map<String, String> methodParameters;

    private NameValue returns;

    private Map<String, String> params;
    private Map<String, String> pathParams;
    private Map<String, String> queryParams;

    public void addParams(String key, String value) {
        if (this.params == null) {
            this.params = new HashMap<>();
        }
        this.params.put(key, value);
    }

    public void addPathParam(String key, String value) {
        if (this.pathParams == null) {
            this.pathParams = new HashMap<>();
        }
        this.pathParams.put(key, value);
    }

    public void addQueryParam(String key, String value) {
        if (this.queryParams == null) {
            this.queryParams = new HashMap<>();
        }
        this.queryParams.put(key, value);
    }



}
