package org.mmonti.documentme.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by mmonti on 11/22/15.
 */
@Data
@AllArgsConstructor
public class NameValue {

    private String name;
    private Object value;

}
