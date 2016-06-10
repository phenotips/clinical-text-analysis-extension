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

import org.phenotips.textanalysis.TermAnnotation;
import org.phenotips.textanalysis.TermAnnotationService;
import org.phenotips.vocabulary.VocabularyManager;
import org.phenotips.vocabulary.VocabularyTerm;

import org.xwiki.component.annotation.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Implementation of {@link TermAnnotationService} using a generic REST API endpoint.
 * To use this with any given RESTful service it must offer a compatible API. At the moment,
 * this consists of one method, annotations/entities, that accepts parameters as a URLEncoded form
 * (non-ideal but needed for scigraph support). The parameters are:
 *
 * <pre>
 * {
 *      "content" : "the text to use",
 *      "includeCat" : "the category that returned ontology elements should belong to"
 * }
 * </pre>
 *
 * The service must respond as follows:
 * <pre>
 * [
 *      {
 *          "start" : "the first position of the annotation",
 *          "end" : "the last position of the annotation",
 *          "token" : {
 *              "id" : "the phenotype's id",
 *          }
 *      }
 *  ]
 * </pre>
 *
 * @version $Id$
 */
@Component
@Named("genericREST")
@Singleton
public class GenericRESTAnnotationService implements TermAnnotationService
{
    /**
     * The ontology used to look up phenotypes.
     */
    @Inject
    private VocabularyManager vocabularies;

    /**
     * The wrapper that will actually interact with the service.
     */
    @Inject
    private RESTWrapper wrapper;

    @Override
    public List<TermAnnotation> annotate(String text) throws AnnotationException
    {
        List<RESTWrapper.RESTAnnotation> annotations = wrapper.annotate(text);
        List<TermAnnotation> retval = new ArrayList<>(annotations.size());
        for (RESTWrapper.RESTAnnotation annotation : annotations) {
            String termId = annotation.getToken().getId().replace("hpo:", "").replace("_", ":");
            VocabularyTerm term = this.vocabularies.resolveTerm(termId);
            if (term != null) {
                long start = annotation.getStart();
                long end = annotation.getEnd();
                retval.add(new TermAnnotation(start, end, term));
            }
        }
        return retval;
    }
}
