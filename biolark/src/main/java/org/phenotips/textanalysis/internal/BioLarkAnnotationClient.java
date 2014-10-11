package org.phenotips.textanalysis.internal;

import org.phenotips.ontology.OntologyManager;
import org.phenotips.textanalysis.Annotation;
import org.phenotips.textanalysis.AnnotationClient;

import org.xwiki.component.annotation.Component;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Implementation of AnnotationClient using BioLark.
 *
 * @version $Id$
 * @since
 */
@Component
@Singleton
public class BioLarkAnnotationClient implements AnnotationClient
{
    @Inject
    private OntologyManager ontologies;

    @Override
    public Annotation[] annotate(String text) throws AnnotationException {
        //TODO: implement me
        return null;
    }
}
