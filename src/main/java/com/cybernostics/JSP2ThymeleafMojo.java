package com.cybernostics;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import com.cybernostics.jsp2thymeleaf.JSP2Thymeleaf;
import com.cybernostics.jsp2thymeleaf.JSP2ThymeleafConfiguration;
import com.cybernostics.jsp2thymeleaf.converters.JSP2ThymeLeafConverterException;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Goal which touches a timestamp file.
 *
 */
@Mojo(name = "jsp2tl", defaultPhase = LifecyclePhase.PROCESS_SOURCES, requiresProject = true)
public class JSP2ThymeleafMojo
        extends AbstractMojo
{

    /**
     * Location of the generated sources.
     */
    @Parameter(defaultValue = "${project.build.directory}/classes/templates", property = "outputDir", required = true)
    private File outputDirectory;

    @Parameter(defaultValue = "true", property = "updateLinks", required = true)
    private Boolean updateLinks;

    @Parameter(defaultValue = "${project.basedir}/src/main/webapp/WEB-INF/jsp", required = true)
    private File srcDirectory;

    private static final String[] DEFAULT_INCLUDES =
    {
        "**/*.jsp", "**/*.jspx", "**/*.jspf"
    };

    @Parameter
    private String[] includes = DEFAULT_INCLUDES;

    @Parameter
    private String[] excludes = new String[0];

    public File getOutputDirectory()
    {
        return outputDirectory;
    }

    public Boolean getUpdateLinks()
    {
        return updateLinks;
    }

    public File getSrcDirectory()
    {
        return srcDirectory;
    }

    public String[] getIncludes()
    {
        return includes;
    }

    public void execute()
            throws MojoExecutionException

    {
        final Log log = getLog();
        log.info("includes:" + Arrays.toString(getIncludes()));
        log.info("outputDirectory = " + outputDirectory.getAbsolutePath());
        log.info("srcDirectory = " + srcDirectory.getAbsolutePath());
        log.info("updateLinks = " + updateLinks);
        JSP2ThymeleafConfiguration config = JSP2ThymeleafConfiguration
                .getBuilder()
                .withIncludes(includes)
                .withExcludes(excludes)
                .withSrcFolder(srcDirectory.toString())
                .withDestFolder(outputDirectory.toString())
                .build();
        JSP2Thymeleaf jSP2Thymeleaf = new JSP2Thymeleaf(config);
        final List<JSP2ThymeLeafConverterException> exceptions = jSP2Thymeleaf.run();
        if (exceptions.isEmpty())
        {
            log.info("JSP2Thymeleaf converted all files successfully.");
        } else
        {
            String message = exceptions.stream().map(it -> it.getCause().getMessage()).collect(Collectors.joining("\n"));
            for (JSP2ThymeLeafConverterException exception : exceptions)
            {
                log.error(exception.getCause().getLocalizedMessage() + "\n" + ExceptionUtils.getStackTrace(exception));
            }
            throw new MojoExecutionException("Failed to convert all files");
        }
    }

}
