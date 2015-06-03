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

import com.sun.xml.internal.fastinfoset.vocab.Vocabulary;
import org.phenotips.vocabulary.VocabularyManager;
import org.phenotips.vocabulary.VocabularyTerm;
import org.phenotips.textanalysis.TermAnnotation;
import org.phenotips.textanalysis.TermAnnotationService;
import org.phenotips.textanalysis.TermAnnotationService.AnnotationException;

import org.phenotips.vocabulary.VocabularyManager;
import org.phenotips.vocabulary.VocabularyTerm;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.test.mockito.MockitoComponentMockingRule;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Matchers;

import au.edu.uq.eresearch.biolark.cr.Annotation;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for methods from {@link BioLarkAnnotationService}.
 *
 * @version $Id$
 */
public class BioLarkAnnotationServiceTest
{
    private TermAnnotationService client;

    /**
     * Mocker for BioLarkAnnotationService component.
     */
    @Rule
    public final MockitoComponentMockingRule<TermAnnotationService> mocker =
        new MockitoComponentMockingRule<TermAnnotationService>(BioLarkAnnotationService.class);

    /**
     * Tests annotation on a sentence that contains exactly one phenotype in it.
     *
     * @throws ComponentLookupException if the mocked component doesn't exist
     * @throws AnnotationException if the annotation process failed
     */
    @Test
    public void testSingleAnnotate() throws ComponentLookupException, AnnotationException
    {
        this.client = this.mocker.getComponentUnderTest();
        String term = "blue eyes";
        String text = "The lady has " + term;
        int start = text.length();
        int end = start + term.length();
        String termId = "test id";

        List<Annotation> biolarkResult = new LinkedList<Annotation>();
        Annotation blueEyesAnnotation = new Annotation();
        blueEyesAnnotation.setStartOffset(start);
        blueEyesAnnotation.setEndOffset(end);
        blueEyesAnnotation.setOriginalSpan(text);
        blueEyesAnnotation.setUri("http://wwww.something.com/blue_eyes");
        biolarkResult.add(blueEyesAnnotation);

        // Mock Biolark component
        BiolarkWrapper biolark = this.mocker.getInstance(BiolarkWrapper.class);
        when(biolark.annotatePlain(eq(text), anyBoolean())).thenReturn(biolarkResult);

        // Mock Ontology Manager
        VocabularyManager ontologyManager = this.mocker.getInstance(VocabularyManager.class);
        VocabularyTerm t = mock(VocabularyTerm.class);
        when(t.getId()).thenReturn(termId);
        when(ontologyManager.resolveTerm("blue:eyes")).thenReturn(t);

        List<TermAnnotation> expected = new LinkedList<TermAnnotation>();
        expected.add(new TermAnnotation(start, end, t));

        List<TermAnnotation> actual = this.client.annotate(text);

        assertEquals(expected, actual);
    }

    /**
     * Tests annotation of an empty string.
     *
     * @throws ComponentLookupException if the mocked component doesn't exist
     * @throws AnnotationException if the annotation process failed
     */
    @Test
    public void testAnnotateEmpty() throws ComponentLookupException, AnnotationException
    {
        this.client = this.mocker.getComponentUnderTest();
        String text = "";
        List<TermAnnotation> expectedAnnotations = new LinkedList<TermAnnotation>();

        BiolarkWrapper biolark = this.mocker.getInstance(BiolarkWrapper.class);
        when(biolark.annotatePlain(eq(text), Matchers.anyBoolean())).
            thenReturn(new ArrayList<Annotation>());

        assertEquals(expectedAnnotations, this.client.annotate(text));
    }

}
