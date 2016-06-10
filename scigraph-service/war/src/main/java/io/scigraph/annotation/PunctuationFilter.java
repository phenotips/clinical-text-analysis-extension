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
package io.scigraph.annotation;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.FlagsAttribute;

/**
 * Flags any words that end with a punctuation mark.
 *
 * @version $Id$
 */
public final class PunctuationFilter extends TokenFilter
{
    /**
     * The flag set when a word ends with punctuation.
     */
    public static final int PUNCTUATION_FLAG = 0x1;

    /**
     * The current term.
     */
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);

    /**
     * The current flags attribute.
     */
    private final FlagsAttribute flattribute = addAttribute(FlagsAttribute.class);

    /**
     * The pattern we use to figure out if there's punctuation.
     */
    private final Pattern pattern = Pattern.compile("^(.*?)([\\.!\\?,:;\"'\\(\\)]+)$");

    /**
     * Our string matcher.
     */
    private final Matcher m;

    /**
     * CTOR.
     * @param in the input token stream.
     */
    public PunctuationFilter(TokenStream in)
    {
        super(in);
        m = pattern.matcher(termAtt);
    }

    /**
     * Return whether the flags given has the punctuation bit set.
     * @param flags the flags to look at
     * @return whether the punctuation flag is set
     */
    public static boolean isPunctuationSet(int flags)
    {
        return (PUNCTUATION_FLAG & flags) == PUNCTUATION_FLAG;
    }

    @Override
    public boolean incrementToken() throws IOException
    {
        if (!input.incrementToken()) {
            return false;
        }
        m.reset();
        if (m.find()) {
            int flags = flattribute.getFlags();
            flattribute.setFlags(flags | PUNCTUATION_FLAG);
        }
        return true;
    }
}
