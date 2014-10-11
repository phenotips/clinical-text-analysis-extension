package org.phenotips.textanalysis;

import org.phenotips.ontology.OntologyTerm;

/**
 * Contains information about a recognized phenotype from text.
 *
 * @version $Id$
 * @since 1.0M11
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
