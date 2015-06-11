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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.phenotips.textanalysis.internal;

import org.phenotips.textanalysis.TermAnnotation;
import org.phenotips.textanalysis.TermAnnotationService;
import org.phenotips.vocabulary.VocabularyManager;
import org.phenotips.vocabulary.VocabularyTerm;

import org.xwiki.component.annotation.Component;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.io.FilenameUtils;

import au.edu.uq.eresearch.biolark.cr.Annotation;

/**
 * Implementation of {@link TermAnnotationService} using BioLark.
 *
 * @version $Id$
 * @since 1.0M1
 */
@Component
@Named("biolark")
@Singleton
public class BioLarkAnnotationService implements TermAnnotationService
{
    /**
     * Ontology used to look up Phenotype term Ids.
     */
    @Inject
    private VocabularyManager vocabularies;

    /**
     * Biolark annotation component.
     */
    @Inject
    private BiolarkWrapper biolark;

    @Override
    public List<TermAnnotation> annotate(String text) throws AnnotationException
    {
        final List<Annotation> biolarkAnnotations =
            this.biolark.annotatePlain(text, false);
        final List<TermAnnotation> finalAnnotations =
            new LinkedList<TermAnnotation>();

        // Resolve each termID against the ontology
        for (final Annotation biolarkAnnotation : biolarkAnnotations) {
            final String termId = FilenameUtils.getBaseName(biolarkAnnotation.getUri()).replace('_', ':');
            final VocabularyTerm term = this.vocabularies.resolveTerm(termId);
            if (term != null) {
                final long start = biolarkAnnotation.getStartOffset();
                final long end = biolarkAnnotation.getEndOffset();
                finalAnnotations.add(new TermAnnotation(start, end, term));
            }
        }
        return finalAnnotations;
    }
}
