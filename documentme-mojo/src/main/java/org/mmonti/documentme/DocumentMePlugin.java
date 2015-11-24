package org.mmonti.documentme;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.JsonValueSerializer;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.mmonti.documentme.model.Endpoint;
import org.mmonti.documentme.reader.SourceReader;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * @author mmonti
 */
@Mojo(name = "document")
public class DocumentMePlugin extends AbstractMojo {

    @Parameter(property = "basedir", required = true)
    protected File basedir;
    @Parameter(property = "output", required = true)
    protected File output;
    @Parameter(property = "pattern", required = true)
    protected String pattern;

    /**
     *
     * @throws MojoExecutionException
     */
    public void execute() throws MojoExecutionException {
        getLog().info("basedir=[" + this.getBasedir().getAbsolutePath() +"]");
        getLog().info("basedir=[" + this.getOutput().getAbsolutePath() +"]");
        getLog().info("pattern=[" + this.getPattern() +"]");

        final SourceReader reader = SourceReader.getInstance(this.getBasedir(), this.getPattern());
        getLog().info("Reader has been created");

        final Set<Endpoint> endpointSet = reader.getEndpointSet();
        getLog().info("Endpoints found=[" + endpointSet.size() +"]");

        try {
            File output = getOutput();
            if (!output.exists()) {
                if (output.getParentFile().mkdirs()) {
                    getLog().info("Folder/File created");
                } else {
                    getLog().info("File created");
                }
            }

            new ObjectMapper().writeValue(getOutput(), endpointSet);
            getLog().info("File written=[" + getOutput().getAbsolutePath() +"]");

        } catch (JsonProcessingException e) {
            getLog().debug(e);
            getLog().info("An error occurred trying to serialize endpoint descriptor=[" + e.getMessage() +"]");

        } catch (IOException e) {
            getLog().debug(e);
            getLog().info("An error occurred trying to serialize endpoint descriptor=[" + e.getMessage() +"]");
        }
    }

    public static void main(String[] args) {
        String base = "/home/mmonti/Development/workspace/documentme/documentme-sample/src/main/java/";
        String pattern = "**/sample/*.java";
        final SourceReader reader = SourceReader.getInstance(base, pattern);
        final Set<Endpoint> endpointSet = reader.getEndpointSet();

        try {
            System.out.println(new ObjectMapper().writeValueAsString(endpointSet));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public File getBasedir() {
        return basedir;
    }

    public void setBasedir(File basedir) {
        this.basedir = basedir;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }
}