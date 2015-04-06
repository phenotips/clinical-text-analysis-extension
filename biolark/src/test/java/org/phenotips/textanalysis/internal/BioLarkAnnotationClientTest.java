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
import org.phenotips.textanalysis.TermAnnotationClient.AnnotationException;

import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.test.mockito.MockitoComponentMockingRule;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Matchers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import au.edu.uq.eresearch.biolark.cr.Annotation;

public class BioLarkAnnotationClientTest
{
    private TermAnnotationClient client;

    @Rule
    public final MockitoComponentMockingRule<TermAnnotationClient> mocker =
    new MockitoComponentMockingRule<TermAnnotationClient>(
        BioLarkAnnotationClient.class);

    @Test
    public void testSingleAnnotate() throws ComponentLookupException,
        AnnotationException
    {
        this.client = this.mocker.getComponentUnderTest();
        String term = "blue eyes";
        String text = "The lady has " + term;
        int start = text.length();
        int end = start + term.length();

        List<Annotation> biolarkResult = new LinkedList<Annotation>();
        Annotation blueEyesAnnotation = new Annotation();
        blueEyesAnnotation.setStartOffset(start);
        blueEyesAnnotation.setEndOffset(end);
        blueEyesAnnotation.setOriginalSpan(text);
        blueEyesAnnotation.setUri("http://wwww.something.com/blue_eyes");
        biolarkResult.add(blueEyesAnnotation);

        // Mock Biolark component
        BiolarkWrapper biolark = this.mocker.getInstance(BiolarkWrapper.class);
        when(biolark.annotate_plain(eq(text), anyBoolean())).thenReturn(
            biolarkResult);

        // Mock Ontology Manager
        OntologyManager ontologyManager =
            this.mocker.getInstance(OntologyManager.class);
        OntologyTerm t = mock(OntologyTerm.class);
        when(ontologyManager.resolveTerm("blue:eyes")).thenReturn(t);

        List<TermAnnotation> expected = new LinkedList<TermAnnotation>();
        expected.add(new TermAnnotation(start, end, t));

        List<TermAnnotation> actual = this.client.annotate(text);

        assertEquals(expected, actual);
    }

    @Test
    public void testAnnotateEmpty() throws ComponentLookupException,
    AnnotationException
    {
        this.client = this.mocker.getComponentUnderTest();
        String text = "";
        List<TermAnnotation> expectedAnnotations =
            new LinkedList<TermAnnotation>();

        BiolarkWrapper biolark = this.mocker.getInstance(BiolarkWrapper.class);
        when(biolark.annotate_plain(eq(text), Matchers.anyBoolean()))
            .thenReturn(new ArrayList<Annotation>());

        assertEquals(expectedAnnotations, this.client.annotate(text));
    }

}
