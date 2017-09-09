/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/
 */
package org.phenotips.textanalysis.internal;

import org.phenotips.textanalysis.TermAnnotationService;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;
import org.xwiki.configuration.ConfigurationSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Wrapper component implementation for a generic annotation service going through a REST API.
 *
 * @version $Id$
 */
@Component
@Singleton
public class RESTWrapperImpl implements RESTWrapper, Initializable
{
    /**
     * The category to search in.
     */
    private static final String CATEGORY = "abnormality";

    /**
     * The default URL of the annotation service.
     */
    private static final String DEFAULT_BASE_URL = "http://localhost:8080/ctakes/";

    /**
     * The config key for the annotations service url.
     */
    private static final String SERVICE_URL_GLOBAL_CONFIGURATION_KEY = "phenotips.textanalysis.internal.serviceURL";

    private static final String SERVICE_URL_CONFIGURATION_KEY = "serviceURL";

    /**
     * The object for API interaction with scigraph.
     */
    @Inject
    private AnnotationAPIFactory apiFactory;

    /**
     * The global configuration.
     */
    @Inject
    @Named("xwikiproperties")
    private ConfigurationSource baseConfiguration;

    @Inject
    @Named("textAnnotationServiceConfiguration")
    private ConfigurationSource wikiConfiguration;

    /**
     * The object mapper to use for json parsing.
     */
    private ObjectMapper mapper;

    @Override
    public void initialize() throws InitializationException
    {
        this.mapper = new ObjectMapper();
    }

    @Override
    public List<RESTAnnotation> annotate(String text) throws TermAnnotationService.AnnotationException
    {
        try {
            Map<String, String> params = new HashMap<>(2);
            params.put("content", text);
            params.put("includeCat", CATEGORY);
            AnnotationAPI api = this.apiFactory.build(this.wikiConfiguration.getProperty(SERVICE_URL_CONFIGURATION_KEY,
                this.baseConfiguration.getProperty(SERVICE_URL_GLOBAL_CONFIGURATION_KEY, DEFAULT_BASE_URL)));
            InputStream is = api.postForm("annotations/entities", params);
            TypeReference reference = new TypeReference<List<RESTAnnotation>>()
            {
            };
            return this.mapper.readValue(is, reference);
        } catch (IOException | AnnotationAPI.ServiceException e) {
            throw new TermAnnotationService.AnnotationException(e.getMessage(), e);
        }
    }
}
