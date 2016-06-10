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

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LengthFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.WhitespaceTokenizer;

import io.scigraph.lucene.LuceneUtils;
import io.scigraph.lucene.PatternReplaceFilter;

/**
 * An entity processor for use within Phenotips.
 *
 * @version $Id$
 */
public class PTEntityProcessor extends EntityProcessorImpl
{

    /**
     * The character encoding to use.
     */
    private static final String ENCODING = "UTF-8";

    /**
     * CTOR.
     * @param recognizer the injected recognizer.
     */
    @Inject
    public PTEntityProcessor(EntityRecognizer recognizer)
    {
        super(recognizer);
    }

    @Override
    BlockingQueue<List<Token<String>>> startShingleProducer(String content)
    {
        BlockingQueue<List<Token<String>>> queue = new LinkedBlockingQueue<List<Token<String>>>();
        Reader r;
        try {
            r = new InputStreamReader(new ByteArrayInputStream(content.getBytes(ENCODING)), ENCODING);
        } catch (UnsupportedEncodingException e) {
            /* The encoding is hardcoded, and it's the pretty standard utf-8, so if it's not
             * supported that's a problem that should probably be fixed. */
            throw new RuntimeException(e.getMessage(), e);
        }
        ShingleProducer producer = new PTShingleProducer(new PTAnalyzer(), r, queue);
        Thread t = new Thread(producer, "Shingle Producer Thread");
        t.start();
        return queue;
    }

    /**
     * A lucene analyzer used to tokenize and filter input text.
     *
     * @version $Id$
     */
    public static class PTAnalyzer extends Analyzer
    {
        @Override
        public TokenStream tokenStream(String fieldName, Reader reader) {
            Tokenizer tokenizer = new WhitespaceTokenizer(LuceneUtils.getVersion(), reader);
            TokenStream result = new PunctuationFilter(tokenizer);
            result = new PatternReplaceFilter(result,
                Pattern.compile("^([\\.!\\?,:;\"'\\(\\)]*)(.*?)([\\.!\\?,:;\"'\\(\\)]*)$"), "$2", true);
            result = new PatternReplaceFilter(result, Pattern.compile("'s"), "s", true);
            /* Makes no sense to have lone symbols hanging around, and they'll trip up the lucene parser. */
            result = new PatternReplaceFilter(result, Pattern.compile("^\\W$"), "", true);
            result = new LengthFilter(false, result, 1, Integer.MAX_VALUE);
            return result;
        }
    }
}
