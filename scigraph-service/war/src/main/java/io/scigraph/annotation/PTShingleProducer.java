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
import java.io.Reader;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.FlagsAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;


/**
 * Splits text into shingles, with no shingle containing more than one grammatical clause
 * (ie they stop at punctuation).
 *
 * @version $Id$
 */
@NotThreadSafe
public class PTShingleProducer extends ShingleProducer
{
    /**
     * Our logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ShingleProducer.class.getName());

    /**
     * The analyzer.
     */
    private Analyzer analyzer;

    /**
     * The reader containing the text.
     */
    private Reader reader;

    /**
     * The maximum length of a shingle.
     */
    private int shingleCount;

    /**
     * The blocking queue to use to feed back to the annotating thread.
     */
    private final BlockingQueue<List<Token<String>>> queue;

    /**
     * CTOR.
     * @param analyzer the analyzer to use.
     * @param reader the reader containing text.
     * @param queue the queue to use to feed back to the annotating thread.
     */
    public PTShingleProducer(Analyzer analyzer, Reader reader, BlockingQueue<List<Token<String>>> queue)
    {
        this(analyzer, reader, queue, DEFAULT_SHINGLE_COUNT);
    }

    /**
     * CTOR.
     * @param analyzer the analyzer to use.
     * @param reader the reader containing text.
     * @param queue the queue to use to feed back to the annotating thread.
     * @param shingleCount the maximum length of any given shingle.
     */
    public PTShingleProducer(Analyzer analyzer, Reader reader, BlockingQueue<List<Token<String>>> queue,
                             int shingleCount)
    {
        super(analyzer, reader, queue, shingleCount);
        this.analyzer = analyzer;
        this.reader = reader;
        this.shingleCount = shingleCount;
        this.queue = queue;
    }

    /**
     * Exhaust the TokenStream given, placing it in the buffer as we go along. The buffer will be periodically
     * emptied into our queue over time, but it will not be emptied after the stream is exhausted; in other
     * words, the buffer may come back with some elements still inside it.
     * @param stream the tokenstream to read
     * @param offset the offset attribute of the stream
     * @param term the term attribute of the stream
     * @param flags the flags attribute of the stream
     * @param buffer the buffer to put tokens into
     * @throws IOException if the tokenstream throws
     * @throws InterruptedException if insertion into the shared queue fails
     */
    private void exhaustStream(TokenStream stream, OffsetAttribute offset, CharTermAttribute term,
                               FlagsAttribute flags, Deque<Token<String>> buffer)
        throws IOException, InterruptedException
    {
        boolean punctuation = false;
        while (punctuation || stream.incrementToken()) {
            if (!punctuation) {
                Token<String> token = new Token<String>(term.toString(), offset.startOffset(),
                    offset.endOffset());
                buffer.offer(token);
                punctuation = PunctuationFilter.isPunctuationSet(flags.getFlags());
                if (!punctuation && buffer.size() < shingleCount) {
                    // Fill the buffer first, before offering anything to the queue
                    continue;
                }
            }
            addBufferToQueue(buffer);
            if (punctuation || shingleCount == buffer.size()) {
                buffer.pop();
            }
            if (punctuation && buffer.isEmpty()) {
                punctuation = false;
            }
        }
    }

    @Override
    public void run() {
        Deque<Token<String>> buffer = new LinkedList<>();
        try {
            TokenStream stream = analyzer.tokenStream("", reader);
            OffsetAttribute offset = stream.getAttribute(OffsetAttribute.class);
            CharTermAttribute term = stream.getAttribute(CharTermAttribute.class);
            FlagsAttribute flags = stream.getAttribute(FlagsAttribute.class);
            exhaustStream(stream, offset, term, flags, buffer);
            while (!buffer.isEmpty()) {
                addBufferToQueue(buffer);
                buffer.pop();
            }
            queue.put(END_TOKEN);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to produce shingle", e);
        }
    }
}
