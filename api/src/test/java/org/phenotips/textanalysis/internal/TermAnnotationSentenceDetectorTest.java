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


import org.junit.Test;
import org.phenotips.vocabulary.VocabularyTerm;

import java.util.List;
import java.util.ArrayList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;


public class TermAnnotationSentenceDetectorTest
{

    private TermAnnotationSentenceDetector client;

    /* This is where you miss heredocs. */
    private static final String TEXT = "Now is the winter of our discontent " +
               "Made glorious summer by this son of York; " +
               "And all the clouds that lowered upon our house " +
               "In the deep bosom of the ocean buried. " +
               "Now are our brows bound with victorious wreaths, " +
               "Our bruised arms hung up for monuments, " +
               "Our stern alarums changed to merry meetings, " +
               "Our dreadful marches to delightful measures. " +
               "Grim-visaged war hath smoothed his wrinkled front, " +
               "And now, instead of mounting barbed steeds " +
               "To fright the souls of fearful adversaries, " +
               "He capers nimbly in a lady's chamber " +
               "To the lascivious pleasing of a lute.";

    private static final String SENTENCE1;

    private static final String SENTENCE2;

    private static final String SENTENCE3;

    static {
        SENTENCE1 = TEXT.substring(0, 163);
        /* 163 is just whitespace, skip it. */
        SENTENCE2 = TEXT.substring(164, 342);
        SENTENCE3 = TEXT.substring(343);
    }

    @Test
    public void testBasic()
    {
        client = new TermAnnotationSentenceDetector();

        List<TermAnnotation> annotations = new ArrayList<>(3);
        VocabularyTerm winterTerm = mock(VocabularyTerm.class);
        when(winterTerm.getId()).thenReturn("winter");
        TermAnnotation winter = new TermAnnotation(11, 17, winterTerm);
        annotations.add(winter);

        VocabularyTerm monumentsTerm = mock(VocabularyTerm.class);
        when(monumentsTerm.getId()).thenReturn("monuments");
        TermAnnotation monuments = new TermAnnotation(242, 251, monumentsTerm);
        annotations.add(monuments);

        VocabularyTerm nimblyTerm = mock(VocabularyTerm.class);
        when(nimblyTerm.getId()).thenReturn("nimbly");
        TermAnnotation nimbly = new TermAnnotation(491, 497, nimblyTerm);
        annotations.add(nimbly);

        client.detectSentences(annotations, TEXT);

        assertEquals("winter",
                winter.getSentence().
                substring((int) winter.getStartInSentence(), (int) winter.getEndInSentence()));
        assertEquals(SENTENCE1, winter.getSentence());
        assertEquals(11, winter.getStartInSentence());
        assertEquals(17, winter.getEndInSentence());

        assertEquals("monuments",
                monuments.getSentence().
                substring((int) monuments.getStartInSentence(), (int) monuments.getEndInSentence()));
        assertEquals(SENTENCE2, monuments.getSentence());
        assertEquals(78, monuments.getStartInSentence());
        assertEquals(87, monuments.getEndInSentence());

        assertEquals("nimbly",
                nimbly.getSentence().
                substring((int) nimbly.getStartInSentence(), (int) nimbly.getEndInSentence()));
        assertEquals(SENTENCE3, nimbly.getSentence());
        assertEquals(148, nimbly.getStartInSentence());
        assertEquals(154, nimbly.getEndInSentence());
    }
}
