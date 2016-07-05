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
package io.scigraph.vocabulary;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nullable;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.spans.SpanMultiTermQueryWrapper;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.IndexHits;

import io.scigraph.frames.Concept;
import io.scigraph.frames.NodeProperties;
import io.scigraph.lucene.LuceneUtils;
import io.scigraph.neo4j.NodeTransformer;
import io.scigraph.neo4j.bindings.IndicatesNeo4jGraphLocation;
import io.scigraph.owlapi.curies.CurieUtil;

/**
 * A scigraph vocabulary for use with phenotips.
 *
 * @version $Id$
 */
public class PTVocabularyImpl extends VocabularyNeo4jImpl
{
    /**
     * The similarity score used when searching.
     */
    public static final float SIMILARITY = 0.7f;

    /**
     * The length of the prefix that must match when searching.
     */
    public static final int FUZZY_PREFIX = 1;

    /**
     * Our logger.
     */
    private static final Logger LOGGER = Logger.getLogger(PTVocabularyImpl.class.getName());

    /**
     * The neo4j graph we'll be querying.
     */
    private GraphDatabaseService graph;

    /**
     * A transformer to turn nodes into concepts.
     */
    private NodeTransformer transformer;

    /**
     * CTOR. To be used by injectors.
     * @param graph the injected graph database service
     * @param neo4jLocation the location of neo4j
     * @param curieUtil the util to resolve curies
     * @param transformer the transformer to turn nodes into concepts.
     * @throws IOException if the parent throws
     */
    @Inject
    public PTVocabularyImpl(GraphDatabaseService graph, @Nullable @IndicatesNeo4jGraphLocation String neo4jLocation,
        CurieUtil curieUtil, NodeTransformer transformer) throws IOException {
        super(graph, neo4jLocation, curieUtil, transformer);
        this.graph = graph;
        this.transformer = transformer;
    }

    /**
     * Create a fuzzy phrase query for the text and field given.
     * @param text the text to query
     * @param field the field the text should appear in
     * @param fuzzy the fuzzy factor
     * @param boost the boost for the query
     */
    private SpanNearQuery createSpanQuery(String text, String field, float fuzzy, float boost)
    {
        String[] parts = ("^ " + text + " $").split(" ");
        SpanQuery[] clauses = new SpanQuery[parts.length];
        for (int i = 0; i < parts.length; i++) {
            /* There's a limit of 5 terms, which we set just to speed everything along. */
            clauses[i] = new SpanMultiTermQueryWrapper<FuzzyQuery>(
                    new FuzzyQuery(new Term(field, parts[i]), fuzzy, FUZZY_PREFIX, 5));
        }
        /* Slop of 0 and inOrder of true basically turns this into a lucene phrase query */
        SpanNearQuery q = new SpanNearQuery(clauses, 0, true);
        q.setBoost(boost);
        return q;
    }

    /**
     * Add a label:text match to the boolean query given, matching exactly, fuzzily, and with
     * a full-text search, using the boosts given.
     * If any of the boosts are 0, that subquery will not be added.
     * @param parser the parser that's being used
     * @param target the query to add to.
     * @param field the field to query on
     * @param text the text to match
     * @param exactBoost the boost for an exact match
     * @param fuzzyBoost the boost for a fuzzy match
     * @param fullTextBoost the boost for a full-text search match
     * @throws ParseException if badly written...
     */
    private void addToQuery(QueryParser parser, BooleanQuery target, String field, String text,
                            float exactBoost, float fuzzyBoost, float fullTextBoost) throws ParseException
    {
        String exactQuery = String.format("\"\\^ %s $\"", text);
        String prefix = field + ":";
        if (exactBoost > 0) {
            target.add(LuceneUtils.getBoostedQuery(parser, prefix + exactQuery, exactBoost), Occur.SHOULD);
        }
        if (fuzzyBoost > 0) {
            target.add(createSpanQuery(text, field, SIMILARITY, fuzzyBoost), Occur.SHOULD);
        }
        if (fullTextBoost > 0) {
            String query = prefix + StringUtils.join(text.split(" "), " " + prefix);
            target.add(LuceneUtils.getBoostedQuery(parser, query, fullTextBoost), Occur.SHOULD);
        }
    }

    /* We don't wanna override limitHits since other methods should still work the way they do, so
     * just call it filter hits.
     */
    /**
     * Filter the hit list given and return the concepts that should be sent back to the user.
     * @param hits the hits
     * @return the list of concepts to return.
     */
    private List<Concept> filterHits(IndexHits<Node> hits)
    {
        /* We're gonna increase this as we go along, so if a phrase returns very few results, it'll return
         * what it has, but if there's tons and tons of results we'll only return good stuff.
         * This really only works because the hits come back sorted by order of relevance.
         */
        float threshold = 0.11f;
        int count = 2;
        List<Concept> result = new ArrayList<Concept>();
        for (Node n : hits) {
            float score = hits.currentScore();
            Concept c = transformer.apply(n);
            if (score >= threshold) {
                result.add(c);
                threshold += (0.8 / (count));
                count *= 2;
            }
        }
        return result;
    }

    @Override
    public List<Concept> getConceptsFromTerm(Query query)
    {
        /* This stuff is a translated-ish version of the field boosting you can find in
         * org.phenotips.solr.internal.HumanPhenotypeOntology
         * Because we only use scigraph for text annotation, we ignore loads and loads of parameters
         * from the query passed in, such as isIncludeSynonyms/Abbreviations/Acronyms, depcreation of
         * concepts, and the limit. */
        BooleanQuery finalQuery = new BooleanQuery();
        try {
            String text = query.getInput().toLowerCase();
            QueryParser parser = getQueryParser();
            /* The boost for full-text (i.e. non-phrase, non-exact) matching will depend on the length
             * of the phrase fed in. This way, we try to limit irrelevant matches (for instance so that
             * "slow" in "slow growth" doesn't match something like "slow onset"). Similarly, we'll
             * significantly punish the boost if we should be dealing with a single word, simply because
             * we could be dealing with descriptive words such as "abnormality" or "syndrome" that we
             * don't want popping up. */
            float wordCountScore = text.split(" ").length == 1 ? 0.5f : 1.4f;
            float textBoost;

            textBoost = Math.min((text.length() - 1) * wordCountScore, 20.0f);
            addToQuery(parser, finalQuery, NodeProperties.LABEL, text, 100.0f, 36.0f, textBoost);

            textBoost = Math.min((text.length() - 2) * (wordCountScore - 0.2f), 15.0f);
            addToQuery(parser, finalQuery, Concept.SYNONYM, text, 70.0f, 25.0f, textBoost);

            addToQuery(parser, finalQuery, "comment", text, 0.0f, 5.0f, 3.0f);
        } catch (ParseException e) {
            LOGGER.log(Level.WARNING, "Failed to parse query", e);
        }
        try (Transaction tx = graph.beginTx()) {
            IndexHits<Node> hits = graph.index().getNodeAutoIndexer().getAutoIndex().query(finalQuery);
            List<Concept> result = filterHits(hits);
            tx.success();
            return result;
        }
    }
}
