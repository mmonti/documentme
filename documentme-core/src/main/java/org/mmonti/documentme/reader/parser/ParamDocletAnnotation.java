package org.mmonti.documentme.reader.parser;

import lombok.Data;

/**
 * Created by mmonti on 11/22/15.
 */
@Data
public class ParamDocletAnnotation extends DocletAnnotation {

    public static final String CONST_PARAM = "param";

    private String parameter;

    /**
     *
     * @param type
     * @param value
     */
    public ParamDocletAnnotation(final String type, final String value) {
        super(type, value.substring(value.indexOf(CONS_SPACE)).trim());
        this.parameter = value.trim().substring(0, value.indexOf(CONS_SPACE));
    }

    /**
     *
     * @param value
     */
    public ParamDocletAnnotation(String value) {
        this(CONST_PARAM, value);
    }
}
