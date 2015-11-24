package org.mmonti.documentme.endpoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.mmonti.documentme.model.Endpoint;
import org.mmonti.documentme.reader.SourceReader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 *
 * @author mmonti
 */
@RestController
public class Endpoints {

    private String source = null;
    private String pattern = null;

    /**
     *
     * @param source
     * @param pattern
     */
    public Endpoints(String source, String pattern) {
        this.source = source;
        this.pattern = pattern;
    }

    @RequestMapping("/documentme/endpoints")
    public ResponseEntity<Set<Endpoint>> getEndpoints() throws JsonProcessingException {
        final SourceReader reader = SourceReader.getInstance(source, pattern);
        final Set<Endpoint> endpointSet = reader.getEndpointSet();
        return ResponseEntity.ok(endpointSet);
    }

}