/**
 * Get more info at : www.jrebirth.org .
 * Copyright JRebirth.org © 2011-2013
 * Contact : sebastien.bordes@jrebirth.org
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jrebirth.core.resource.parameter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.jrebirth.core.log.JRLogger;
import org.jrebirth.core.log.JRLoggerFactory;
import org.jrebirth.core.resource.ParameterEntry;
import org.jrebirth.core.resource.factory.AbstractResourceBuilder;
import org.jrebirth.core.util.ClasspathUtility;

/**
 * The class <strong>ParameterBuilder</strong>.
 * 
 * Class used to manage parameters with weak reference.
 * 
 * @author Sébastien Bordes
 */
public final class ParameterBuilder extends AbstractResourceBuilder<ParameterItem<?>, ParameterParams, Object> implements ParameterMessages {

    /** The class logger. */
    private static final JRLogger LOGGER = JRLoggerFactory.getLogger(ParameterBuilder.class);

    /** Store all parameter values defined into properties files. */
    private final Map<String, ParameterEntry> propertiesParametersMap = new ConcurrentHashMap<>();

    /** Store all overridden values defined by the call of define method. */
    private final Map<ParameterItem<?>, Object> overriddenParametersMap = new ConcurrentHashMap<>();

    /** The file extension used by configuration files. */
    private String configurationFileExtension;

    /** The Wildcard used to load configuration files. */
    private String configurationFileWildcard;

    /**
     * Search configuration files according to the parameters provided.
     * 
     * @param wildcard the regex wildcard (must not be null)
     * @param extension the file extension without the first dot (ie: properties) (must not be null)
     */
    public void searchConfigurationFiles(final String wildcard, final String extension) {

        // Store parameters
        this.configurationFileWildcard = wildcard;
        this.configurationFileExtension = extension;

        // Search and analyze all properties files available
        readPropertiesFiles();
    }

    /**
     * Read all configuration files available into the application classpath.
     */
    private void readPropertiesFiles() {

        if (this.configurationFileWildcard.isEmpty() || this.configurationFileExtension.isEmpty()) {
            // Skip configuration loading
            LOGGER.log(SKIP_CONF_LOADING);

        } else {
            // Assemble the regex pattern
            final Pattern filePattern = Pattern.compile(this.configurationFileWildcard + "\\." + this.configurationFileExtension);

            // Retrieve all resources from default classpath
            final Collection<String> list = ClasspathUtility.getClasspathResources(filePattern);

            LOGGER.log(CONFIG_FOUND, list.size(), list.size() > 1 ? "s" : "");

            for (final String confFilename : list) {
                readPropertiesFile(confFilename);
            }
        }
    }

    /**
     * Read a customized configuration file to load parameters values.
     * 
     * @param custConfFileName the file to load
     */
    private void readPropertiesFile(final String custConfFileName) {

        final File custConfFile = new File(custConfFileName);

        final Properties p = new Properties();

        LOGGER.log(READ_CONF_FILE, custConfFile.getAbsolutePath());

        try (InputStream is = new FileInputStream(custConfFile)) {

            // Read the properties file
            p.load(is);

            for (final Map.Entry<Object, Object> entry : p.entrySet()) {
                if (this.propertiesParametersMap.containsKey(entry.getKey())) {
                    LOGGER.log(UPDATE_PARAMETER, entry.getKey(), entry.getValue());
                } else {
                    LOGGER.log(STORE_PARAMETER, entry.getKey(), entry.getValue());
                }
                storePropertiesParameter(entry);

            }

        } catch (final IOException e) {
            LOGGER.error(CONF_READING_ERROR, custConfFile.getAbsolutePath());
        }

    }

    /**
     * Store a parameter read from properties files.<br />
     * The parameter is wrapped into a parameterEntry
     * 
     * @param entry the entry to store
     */
    private void storePropertiesParameter(final Map.Entry<Object, Object> entry) {
        this.propertiesParametersMap.put(entry.getKey().toString(), new ParameterEntry(entry.getValue().toString()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object buildResource(final ParameterItem<?> parameterItem, final ParameterParams parameterParams) {
        Object object = null;
        if (parameterParams instanceof ObjectParameter) {

            final ObjectParameter<?> op = (ObjectParameter<?>) parameterParams;

            // Load overridden values first
            if (op.name() != null && this.overriddenParametersMap.containsKey(parameterItem)) {

                // Retrieve the customized parameter
                object = this.overriddenParametersMap.get(parameterItem);
            }

            // No overridden value is defined
            // Check if the parameter has a parameter name and
            // check if the parameter has been loaded from any customized configuration file
            if (object == null && op.name() != null && this.propertiesParametersMap.containsKey(op.name())) {

                // Retrieve the customized parameter
                object = op.parseObject(this.propertiesParametersMap.get(op.name()));
            }

            // Object is still null
            if (object == null) {
                // No customized (properties and overridden) parameter has been loaded, gets the default programmatic one
                object = op.object();
            }

            // // Don't store the parameter into the map if it hasn't got any parameter name
            // if (op.name() != null) {
            //
            // // Store the new parameter into the map
            // this.propertiesParametersMap.put(op.name(), new ParameterEntry("", object));
            // }
        }
        return object;
    }

    /**
     * Override a parameter value.
     * 
     * @param key the parameter item key
     * @param forcedValue the overridden value
     */
    public void define(final ParameterItem<?> key, final Object forcedValue) {
        this.overriddenParametersMap.put(key, forcedValue);
        set(key, forcedValue);
    }
}
