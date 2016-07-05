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

import org.xwiki.component.annotation.Role;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Wraps annotation initialization and calls, offering a single interface to annotate text.
 * TODO: this is probably an unnecessary abstraction that could be merged into GenericRESTAnnotationService
 *
 * @version $Id$
 */
@Role
public interface RESTWrapper
{

    /**
     * Annotates the text given.
     * @param text the string to annotate
     * @return List of annotations
     * @throws TermAnnotationService.AnnotationException if something goes wrong
     */
    List<RESTAnnotation> annotate(String text) throws TermAnnotationService.AnnotationException;

    /**
     * A text annotation as returned by the service in use.
     *
     * @version $Id$
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    final class RESTAnnotation
    {
        /**
         * The token corresponding to the annotation; in phenotips terms, this is the term.
         */
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static final class Token
        {
            /**
             * The token's id.
             */
            private String id;

            /**
             * Return the id.
             * @return the id
             */
            public String getId()
            {
                return id;
            }

            /**
             * Set the id.
             * @param id the id
             */
            public void setId(String id)
            {
                this.id = id;
            }
        }

        /**
         * The token for this annotation.
         */
        private Token token;

        /**
         * The end of the annotation.
         */
        private int end;

        /**
         * The start of the annotation.
         */
        private int start;

        /**
         * Get the token.
         * @return the token
         */
        public Token getToken()
        {
            return token;
        }

        /**
         * Set the token.
         * @param token the token
         */
        public void setToken(Token token)
        {
            this.token = token;
        }

        /**
         * Get the end of the annotation.
         * @return the end
         */
        public int getEnd()
        {
            return end;
        }

        /**
         * Set the end of the annotation.
         * @param end the end
         */
        public void setEnd(int end)
        {
            this.end = end;
        }

        /**
         * Get the start of the annotation.
         * @return the start
         */
        public int getStart()
        {
            return start;
        }

        /**
         * Set the start of the annotation.
         * @param start the start
         */
        public void setStart(int start)
        {
            this.start = start;
        }
    }
}

