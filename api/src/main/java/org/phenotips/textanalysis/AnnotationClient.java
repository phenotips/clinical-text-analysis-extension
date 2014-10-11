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
