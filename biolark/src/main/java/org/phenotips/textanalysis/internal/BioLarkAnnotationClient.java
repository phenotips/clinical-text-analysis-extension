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
package org.phenotips.textanalysis.internal;

import org.phenotips.ontology.OntologyManager;
import org.phenotips.ontology.OntologyTerm;
import org.phenotips.textanalysis.TermAnnotation;
import org.phenotips.textanalysis.TermAnnotationClient;

import org.xwiki.component.annotation.Component;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.io.FilenameUtils;

import au.edu.uq.eresearch.biolark.cr.Annotation;

/**
 * Implementation of AnnotationClient using BioLark.
 *
 * @version $Id$
 * @since 1.0M1
 */
@Component
@Singleton
public class BioLarkAnnotationClient implements TermAnnotationClient
{
    /**
     * Ontology used to look up Phenotype term Ids.
     */
    @Inject
    private OntologyManager ontologies;

    /**
     * Biolark annotation component.
     */
    @Inject
    private BiolarkWrapper biolark;

    @Override
    public List<TermAnnotation> annotate(String text)
        throws AnnotationException
        {
        final List<Annotation> biolarkAnnotations =
            this.biolark.annotate_plain(text, false);
        final List<TermAnnotation> finalAnnotations =
            new LinkedList<TermAnnotation>();

        // Resolve each termID against Phenotype ontology
        for (final Annotation biolarkAnnotation : biolarkAnnotations) {
            final String termId = FilenameUtils.getBaseName(
                biolarkAnnotation.getUri()).replace('_', ':');
            final OntologyTerm term = this.ontologies.resolveTerm(termId);
            if (term != null) {
                final long start = biolarkAnnotation.getStartOffset();
                final long end = biolarkAnnotation.getEndOffset();
                finalAnnotations.add(new TermAnnotation(start, end, term));
            }
        }
        return finalAnnotations;
        }
}
