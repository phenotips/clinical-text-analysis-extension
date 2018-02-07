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
import org.phenotips.textanalysis.TermAnnotationService.AnnotationException;
import org.phenotips.vocabulary.VocabularyManager;
import org.phenotips.vocabulary.VocabularyTerm;

import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.test.mockito.MockitoComponentMockingRule;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GenericRESTAnnotationServiceTest
{
    private TermAnnotationService client;

    /**
     * Mocker for GenericRESTAnnotationService component.
     */
    @Rule
    public final MockitoComponentMockingRule<TermAnnotationService> mocker =
        new MockitoComponentMockingRule<TermAnnotationService>(GenericRESTAnnotationService.class);

    /**
     * Test for cases where there's only one annotation in the text.
     *
     * @throws ComponentLookupException if the mocked component doesn't exist
     * @throws AnnotationException if the annotation process failed
     * @throws IOException if annotateEntities throws (hopefully never)
     */
    @Test
    public void testSingleAnnotation() throws AnnotationException, ComponentLookupException, IOException
    {
        client = this.mocker.getComponentUnderTest();
        String term = "blue eyes";
        String text = "The lady has ";
        int start = text.length();
        text += term;
        int end = start + term.length();
        String termId = "test id";

        List<RESTWrapper.RESTAnnotation> result = new LinkedList<RESTWrapper.RESTAnnotation>();

        /* Mock SciGraph wrapper */
        RESTWrapper wrapper = this.mocker.getInstance(RESTWrapper.class);
        RESTWrapper.RESTAnnotation.Token token = new RESTWrapper.RESTAnnotation.Token();
        token.setId(termId);
        RESTWrapper.RESTAnnotation annotation = new RESTWrapper.RESTAnnotation();
        annotation.setToken(token);
        annotation.setStart(start);
        annotation.setEnd(end);
        result.add(annotation);
        when(wrapper.annotate(text)).thenReturn(result);

        // Mock Ontology Manager
        VocabularyManager vocabularyManager = this.mocker.getInstance(VocabularyManager.class);
        VocabularyTerm t = mock(VocabularyTerm.class);
        when(t.getId()).thenReturn(termId);
        when(vocabularyManager.resolveTerm(termId)).thenReturn(t);

        List<TermAnnotation> expected = new LinkedList<TermAnnotation>();
        expected.add(new TermAnnotation(start, end, t));

        List<TermAnnotation> actual = client.annotate(text);

        assertEquals(expected, actual);
    }

    /**
     * Test for cases where two terms overlap in the text.
     *
     * @throws ComponentLookupException if the mocked component doesn't exist
     * @throws AnnotationException if the annotation process failed
     * @throws IOException if annotateEntities throws (hopefully never)
     */
    @Test
    public void testOverlappingAnnotations() throws AnnotationException, ComponentLookupException, IOException
    {
        client = this.mocker.getComponentUnderTest();
        String term1 = "blue eyes";
        String term2 = "eyes";
        String text = "The layd has ";
        int start1 = text.length();
        int start2 = text.length() + "blue ".length();
        int end1 = start1 + term1.length();
        int end2 = start2 + term2.length();
        String termId1 = "id1";
        String termId2 = "id2";
        text += term1;

        List<RESTWrapper.RESTAnnotation> result = new LinkedList<RESTWrapper.RESTAnnotation>();

        RESTWrapper.RESTAnnotation.Token token1 = new RESTWrapper.RESTAnnotation.Token();
        token1.setId(termId1);
        RESTWrapper.RESTAnnotation annotation1 = new RESTWrapper.RESTAnnotation();
        annotation1.setToken(token1);
        annotation1.setStart(start1);
        annotation1.setEnd(end1);
        result.add(annotation1);

        RESTWrapper.RESTAnnotation.Token token2 = new RESTWrapper.RESTAnnotation.Token();
        token2.setId(termId2);
        RESTWrapper.RESTAnnotation annotation2 = new RESTWrapper.RESTAnnotation();
        annotation2.setToken(token2);
        annotation2.setStart(start2);
        annotation2.setEnd(end2);
        result.add(annotation2);

        /* Mock SciGraph wrapper */
        RESTWrapper wrapper = this.mocker.getInstance(RESTWrapper.class);
        when(wrapper.annotate(text)).thenReturn(result);

        /* Mock Ontology wrapper */
        VocabularyManager vocabularyManager = this.mocker.getInstance(VocabularyManager.class);
        VocabularyTerm t1 = mock(VocabularyTerm.class);
        when(t1.getId()).thenReturn(termId1);
        when(vocabularyManager.resolveTerm(termId1)).thenReturn(t1);
        VocabularyTerm t2 = mock(VocabularyTerm.class);
        when(t2.getId()).thenReturn(termId2);
        when(vocabularyManager.resolveTerm(termId2)).thenReturn(t2);

        List<TermAnnotation> expected = new LinkedList<TermAnnotation>();
        expected.add(new TermAnnotation(start1, end1, t1));
        expected.add(new TermAnnotation(start2, end2, t2));

        List<TermAnnotation> actual = client.annotate(text);
        assertEquals(expected, actual);
    }

    /**
     * Test for cases where we're annotating empty text.
     *
     * @throws ComponentLookupException if the mocked component doesn't exist
     * @throws AnnotationException if the annotation process failed
     * @throws IOException if annotateEntities throws (hopefully never)
     */
    @Test
    public void annotateEmptyTextReturnsDirectly() throws AnnotationException, ComponentLookupException, IOException
    {
        this.client = this.mocker.getComponentUnderTest();

        assertTrue(this.client.annotate(null).isEmpty());
        assertTrue(this.client.annotate("").isEmpty());
        assertTrue(this.client.annotate(" \n\r \t ").isEmpty());

        RESTWrapper wrapper = this.mocker.getInstance(RESTWrapper.class);
        Mockito.verifyZeroInteractions(wrapper);
    }
}
