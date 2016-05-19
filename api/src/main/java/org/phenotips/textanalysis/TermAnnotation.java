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
package org.phenotips.textanalysis;

import org.phenotips.vocabulary.VocabularyTerm;

/**
 * Contains information about a recognized phenotype from text.
 *
 * @version $Id$
 * @since 1.0M1
 */
public class TermAnnotation implements Comparable<TermAnnotation>
{
    private final long mStartPos;

    private final long mEndPos;

    private final VocabularyTerm mTerm;

    private String sentence;

    private long startInSentence;

    private long endInSentence;

    /**
     * Constructs an annotation for a an ontology term using it's start and end positions within the text.
     *
     * @param start position in text where the ontology term occurs
     * @param end position in text where the the ontology term occurance ends
     * @param term the ontology term found in the text
     */
    public TermAnnotation(long start, long end, VocabularyTerm term)
    {
        this.mStartPos = start;
        this.mEndPos = end;
        this.mTerm = term;
    }

    /**
     * @return position in text where the ontology term occurs
     */
    public long getStartPos()
    {
        return this.mStartPos;
    }

    /**
     * @return position in text where the the ontology term occurance ends
     */
    public long getEndPos()
    {
        return this.mEndPos;
    }

    /**
     * @return the ontology term found in the text
     */
    public VocabularyTerm getTerm()
    {
        return this.mTerm;
    }

    /**
     * @return the sentence in which the term occurs.
     */
    public String getSentence()
    {
        return sentence;
    }

    /**
     * @return the position within the sentence where the term starts
     */
    public long getStartInSentence()
    {
        return startInSentence;
    }

    /**
     * @return the position within the sentence where the term ends
     */
    public long getEndInSentence()
    {
        return endInSentence;
    }

    /**
     * Set the sentence that this term appears in.
     * @param sentence the sentence.
     * @param startInSentence the position within the sentence where the term starts
     * @param endInSentence the position within the sentence where the term ends
     */
    public void setSentence(String sentence, long startInSentence, long endInSentence)
    {
        this.sentence = sentence.trim();
        this.startInSentence = startInSentence;
        this.endInSentence = endInSentence;
    }

    @Override
    public int compareTo(TermAnnotation other)
    {
        /* TODO: Casting. Hopefully they're not that far off that we overflow... */
        return (int) (this.getStartPos() - other.getStartPos());
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (this.mEndPos ^ (this.mEndPos >>> 32));
        result = prime * result + (int) (this.mStartPos ^ (this.mStartPos >>> 32));
        result = prime * result + ((this.mTerm == null) ? 0 : this.mTerm.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TermAnnotation other = (TermAnnotation) obj;
        if (this.mEndPos != other.mEndPos) {
            return false;
        }
        if (this.mStartPos != other.mStartPos) {
            return false;
        }
        if (this.mTerm == null) {
            if (other.mTerm != null) {
                return false;
            }
        } else if (!this.mTerm.getId().equals(other.mTerm.getId())) {
            return false;
        }
        return true;
    }

}
