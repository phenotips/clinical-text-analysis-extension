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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;

/**
 * Implements interacting with an annotation REST API.
 *
 * @version $Id$
 */
public class AnnotationAPIImpl implements AnnotationAPI
{
    /**
     * The charset to use when sending requests.
     */
    private static final Charset CHARSET = Charset.forName("UTF-8");

    /**
     * The api endpoint to hit.
     */
    private URL base;

    /**
     * Constructor.
     *
     * @param serviceURL the api endpoint to hit.
     * @throws MalformedURLException if the url given is no good
     */
    public AnnotationAPIImpl(String serviceURL) throws MalformedURLException
    {
        this.base = new URL(serviceURL);
    }

    @Override
    public InputStream postJson(String method, InputStream content) throws ServiceException
    {
        try {
            URI uri = getAbsoluteURI(method);
            return Request.Post(uri).
                bodyStream(content, ContentType.APPLICATION_JSON).
                execute().returnContent().asStream();
        } catch (IOException | URISyntaxException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public InputStream postForm(String method, Map<String, String> params) throws ServiceException
    {
        try {
            URI uri = getAbsoluteURI(method);
            List<NameValuePair> list = new ArrayList<>(params.size());
            for (Map.Entry<String, String> entry : params.entrySet()) {
                NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue());
                list.add(pair);
            }
            return Request.Post(uri).
                bodyForm(list, CHARSET).
                execute().returnContent().asStream();
        } catch (IOException | URISyntaxException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public InputStream postEmpty(String method) throws ServiceException
    {
        try {
            URI uri = getAbsoluteURI(method);
            return Request.Post(uri).execute().returnContent().asStream();
        } catch (IOException | URISyntaxException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public InputStream getEmpty(String method) throws ServiceException
    {
        try {
            URI uri = getAbsoluteURI(method);
            return Request.Get(uri).execute().returnContent().asStream();
        } catch (IOException | URISyntaxException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public String getServiceURL()
    {
        return this.base.toString();
    }

    /**
     * Get the uri to access a method.
     *
     * @param method the name of the method
     * @return the corresponding uri.
     */
    private URI getAbsoluteURI(String method) throws URISyntaxException, MalformedURLException
    {
        URL absolute = new URL(this.base, method);
        return absolute.toURI();
    }
}
