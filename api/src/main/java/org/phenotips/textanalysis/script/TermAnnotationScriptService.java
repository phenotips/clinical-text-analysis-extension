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
package org.phenotips.textanalysis.script;

import org.phenotips.textanalysis.TermAnnotation;
import org.phenotips.textanalysis.TermAnnotationService;
import org.phenotips.textanalysis.TermAnnotationService.AnnotationException;
import org.phenotips.textanalysis.internal.TermAnnotationSentenceDetector;

import org.xwiki.component.annotation.Component;
import org.xwiki.script.service.ScriptService;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Service that suggests plausible diagnoses for a set of features.
 *
 * @see TermAnnotationService
 * @since 1.1M1
 * @version $Id$
 */
@Component
@Named("annotations")
@Singleton
public class TermAnnotationScriptService implements ScriptService
{
    @Inject
    @Named("genericREST")
    private TermAnnotationService service;

    /**
     * Returns a list of annotations of phenotypes found in text.
     *
     * @param text Free form text
     * @return list of annotations
     */
    public List<TermAnnotation> get(String text)
    {
        try {
            List<TermAnnotation> retval = this.service.annotate(text);
            TermAnnotationSentenceDetector detector = new TermAnnotationSentenceDetector();
            detector.detectSentences(retval, text);
            return retval;
        } catch (AnnotationException e) {
            return null;
        }
    }
}
