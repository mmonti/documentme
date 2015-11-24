package org.mmonti.documentme.sample;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by mmonti on 11/21/15.
 */
@RestController
@RequestMapping("/api/v1")
public class SimpleRestController {

    /**
     * Simple GET Controller.
     *
     * @headers {
     *  "x-token" : "User session token"
     * }
     * @responses {
     *  "200" : "Success",
     *  "404" : "Not found"
     * }
     *
     * @param param a sample for path variables
     * @param parameter a sample for query parameter
     *
     * @return String String with the word OK.
     */
    @RequestMapping(value = "/get/{name}", method = RequestMethod.GET)
    public ResponseEntity<String> simpleGet(
            @PathVariable(value = "name") String param,
            @RequestParam(value = "age", required = false) int parameter) {

        return ResponseEntity.ok("OK");
    }

    /**
     * Another Simpler POST Controller.
     *
     * @headers {
     *  "x-token" : "User session token"
     * }
     * @responses {
     *  "201" : "Created",
     *  "401" : "Bad Request"
     * }
     *
     * @return Object New Object created.
     */
    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public ResponseEntity<Object> simplePost(Map<String, Object> parameters) {
        return ResponseEntity.ok("OK");
    }
}
