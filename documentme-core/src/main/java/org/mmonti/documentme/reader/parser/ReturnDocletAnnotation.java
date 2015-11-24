package org.mmonti.documentme.reader.parser;

/**
 * Created by mmonti on 11/22/15.
 */
public class ReturnDocletAnnotation extends ParamDocletAnnotation {

    public static final String CONST_RETURN = "return";

    public ReturnDocletAnnotation(String value) {
        super(CONST_RETURN, value);
    }
}
