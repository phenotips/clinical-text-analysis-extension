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

import java.util.Properties;

import org.apache.ctakes.dictionary.lookup.DictionaryEngine;
import org.apache.ctakes.dictionary.lookup.ae.FirstTokenPermLookupInitializerImpl;
import org.apache.ctakes.dictionary.lookup.algorithms.LookupAlgorithm;
import org.apache.ctakes.dictionary.lookup.phrasebuilder.PhraseBuilder;
import org.apache.ctakes.dictionary.lookup.phrasebuilder.VariantPhraseBuilderImpl;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.annotator.AnnotatorInitializationException;

/**
 * Initialize the PTTokenPermutation object.
 *
 * @version $Id$
 */
public class PTTokenPermutationInitializer extends FirstTokenPermLookupInitializerImpl
{
    private static final String CANONICAL_VARIANT_ATTR = "canonicalATTR";

    private static final String TEXT_MFS_PRP_KEY = "textMetaFields";

    private static final String MAX_P_LEVEL_PRP_KEY = "maxPermutationLevel";

    private final Properties ivProps;

    /**
     * CTOR.
     *
     * @param uimaContext the uima context
     * @param props the properties.
     */
    public PTTokenPermutationInitializer(final UimaContext uimaContext, final Properties props)
    {
        super(uimaContext, props);
        ivProps = props;
    }

    @Override
    public LookupAlgorithm getLookupAlgorithm(final DictionaryEngine dictEngine)
        throws AnnotatorInitializationException
    {
        final String textMetaFields = ivProps.getProperty(TEXT_MFS_PRP_KEY);
        String[] textMetaFieldNameArr;
        if (textMetaFields == null) {
            textMetaFieldNameArr = new String[0];
        } else {
            textMetaFieldNameArr = textMetaFields.split("\\|");
        }
        // variant support
        final String[] variantArr = {CANONICAL_VARIANT_ATTR};
        final PhraseBuilder pb = new VariantPhraseBuilderImpl(variantArr, true);
        final int maxPermutationLevel = Integer.parseInt(ivProps.getProperty(MAX_P_LEVEL_PRP_KEY));
        return new PTTokenPermutation(dictEngine, pb, textMetaFieldNameArr, maxPermutationLevel);
    }
}
