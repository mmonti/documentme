package org.mmonti.documentme.reader.parser;

import lombok.Data;
import lombok.Getter;

/**
 * Created by mmonti on 11/22/15.
 */
@Getter
public class DocletAnnotation {

    public static final String CONS_SPACE = " ";

    private String type;
    private String description;

    public DocletAnnotation(final String type, final String description) {
        this.type = type;
        this.description = description;
    }

}
