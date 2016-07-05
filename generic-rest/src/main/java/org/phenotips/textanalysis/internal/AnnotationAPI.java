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

import org.xwiki.component.annotation.Role;

import java.io.InputStream;
import java.util.Map;

/**
 * Interacts with an annotation REST API endpoint, with methods as defined in {@link GenericRESTAnnotationService}.
 *
 * @version $Id$
 */
@Role
public interface AnnotationAPI
{
    /**
     * Execute a post request to the method given, taking content to be the json body of the request.
     * @param method the method
     * @param content the content, to be interpreted as containing a json object
     * @return the response
     * @throws ServiceException if there's an error accessing the method
     */
    InputStream postJson(String method, InputStream content) throws ServiceException;

    /**
     * Post the form given to the method given.
     * @param method the method
     * @param params the form parameters
     * @return the response
     * @throws ServiceException if there's an error accessing the method
     */
    InputStream postForm(String method, Map<String, String> params) throws ServiceException;

    /**
     * Send an empty post to the method given.
     * @param method the method
     * @return the response
     * @throws ServiceException if there's an error accessing the method
     */
    InputStream postEmpty(String method) throws ServiceException;

    /**
     * Send an empty get to the method given.
     * @param method the method
     * @return the response
     * @throws ServiceException if there's an error accessing the method
     */
    InputStream getEmpty(String method) throws ServiceException;

    /**
     * An exception returned by SciGraph.
     *
     * @version $Id$
     */
    class ServiceException extends Exception
    {
        /**
         * CTOR.
         * @param message the message
         */
        public ServiceException(String message)
        {
            super(message);
        }

        /**
         * CTOR with cause.
         * @param message the message
         * @param cause the cause.
         */
        public ServiceException(String message, Exception cause)
        {
            super(message, cause);
        }
    }
}
