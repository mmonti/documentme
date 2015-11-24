package org.mmonti.documentme.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

/**
 * Created by mmonti on 11/22/15.
 */
public class Return extends NameValue {

    public Return(String name, Object value) {
        super(name, value);
    }

    @JsonProperty("type")
    @Override
    public String getName() {
        return super.getName();
    }

    @JsonProperty("description")
    @Override
    public Object getValue() {
        return super.getValue();
    }
}
