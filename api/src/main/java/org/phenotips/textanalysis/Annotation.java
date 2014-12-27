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
package org.phenotips.textanalysis;

import org.phenotips.ontology.OntologyTerm;

/**
 * Contains information about a recognized phenotype from text.
 *
 * @version $Id$
 * @since 1.0M1
 */
public class Annotation
{
    private final int mStartPos;
    private final int mEndPos;
    private final OntologyTerm mTerm;

    /**
     * Constructs an annotation for a an ontology term
     * using it's start and end positions within the text.
     *
     * @param startPos position in text where the ontology term occurs
     * @param endPos position in text where the the ontology term occurance ends
     * @param term the ontology term found in the text
     */
    public Annotation(int startPos, int endPos, OntologyTerm term) {
        mStartPos = startPos;
        mEndPos = endPos;
        mTerm = term;
    }

    /**
     * @return position in text where the ontology term occurs
     */
    public int getStartPos() {
        return mStartPos;
    }

    /**
     * @return position in text where the the ontology term occurance ends
     */
    public int getEndPos() {
        return mEndPos;
    }

    /**
     * @return the ontology term found in the text
     */
    public OntologyTerm getTerm() {
        return mTerm;
    }
}
