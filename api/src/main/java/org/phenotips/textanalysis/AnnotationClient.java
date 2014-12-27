/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.phenotips.textanalysis;

import org.xwiki.component.annotation.Role;


/**
 * AnnotationClient implementations work with free form text
 * and identify phenotype descriptions with it.
 *
 * @version $Id$
 * @since 1.0M11
 */
@Role
public interface AnnotationClient
{
    /**
     * Scan text for phenotype descriptions.
     *
     * @param text Any kind of free form text.
     * @return list of annotations for items recognized in the text.
     * @throws AnnotationException when annotation failed.
     */
    Annotation[] annotate(String text) throws AnnotationException;

    /**
     * Exception thrown when the annotation process failed.
     *
     * @version $Id$
     * @since 1.0M11
     */
    public class AnnotationException extends Exception
    {
        /**
         * Constructs a new AnnotationException with the specified detail message.
         *
         * @param message the detail message
         */
        public AnnotationException(String message) {
            super(message);
        }
    }
}
