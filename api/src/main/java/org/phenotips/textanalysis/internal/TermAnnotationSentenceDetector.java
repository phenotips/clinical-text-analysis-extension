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

import java.text.BreakIterator;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Takes in lists of term annotations, as well as the text they appear in and assigns sentences to them from the text.
 *
 * @version $Id$
 */
public class TermAnnotationSentenceDetector
{
    /**
     * Attaches sentences to the term annotations given.
     *
     * @param annotations the annotations
     * @param text the text where the annotations appear
     */
    public void detectSentences(List<TermAnnotation> annotations, String text)
    {
        BreakIterator sentences = BreakIterator.getSentenceInstance(Locale.US);
        sentences.setText(text);
        Collections.sort(annotations);
        int currentAnnotation = 0;
        int currentSentence = 0;
        while (currentSentence != BreakIterator.DONE && currentAnnotation < annotations.size()) {
            TermAnnotation annotation = annotations.get(currentAnnotation);
            int nextSentence = sentences.next();
            /* next() pushes the iterator forward, so bring it back */
            sentences.previous();
            /* Does this annotation fall within the current sentence? */
            if (annotation.getStartPos() >= currentSentence && annotation.getStartPos() < nextSentence) {
                long start = annotation.getStartPos() - currentSentence;
                long end = annotation.getEndPos() - currentSentence;
                String sentence;
                if (annotation.getEndPos() <= nextSentence) {
                    /* Yay, straightforward! */
                    sentence = text.substring(currentSentence, nextSentence);
                } else {
                    /* Uh-oh, cross sentence annotation */
                    int crossSentenceEnd = sentences.following((int) annotation.getEndPos());
                    sentence = text.substring(currentSentence, crossSentenceEnd);
                    /* Rewind the iterator */
                    sentences.preceding(currentSentence + 1);
                }
                annotation.setSentence(sentence, start, end);
                currentAnnotation++;
            } else {
                currentSentence = sentences.next();
            }
        }
    }
}
