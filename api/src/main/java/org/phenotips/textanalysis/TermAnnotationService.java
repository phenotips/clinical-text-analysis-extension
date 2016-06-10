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
package org.phenotips.textanalysis;

import org.xwiki.component.annotation.Role;

import java.util.List;

/**
 * Implementations of this class work with free form text and identify phenotype descriptions with it.
 *
 * @version $Id$
 * @since 1.0M1
 */
@Role
public interface TermAnnotationService
{
    /**
     * Scan text for phenotype descriptions.
     *
     * @param text Any kind of free form text.
     * @return list of annotations for items recognized in the text.
     * @throws AnnotationException when annotation failed.
     */
    List<TermAnnotation> annotate(String text) throws AnnotationException;

    /**
     * Exception thrown when the annotation process failed.
     *
     * @version $Id$
     * @since 1.0M1
     */
    class AnnotationException extends Exception
    {
        /**
         * Constructs a new AnnotationException with the specified detail message.
         *
         * @param message the detail message
         */
        public AnnotationException(String message)
        {
            super(message);
        }

        /**
         * Constructs a new AnnotationException with the specified detail message and cause.
         *
         * @param message the detail message
         * @param cause the cause
         */
        public AnnotationException(String message, Exception cause)
        {
            super(message, cause);
        }
    }
}
