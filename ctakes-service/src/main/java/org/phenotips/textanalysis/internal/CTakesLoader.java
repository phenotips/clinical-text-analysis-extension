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

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.OWLOntologyWalker;

/**
 * Loads the HPO into a lucene index for use by ctakes.
 *
 * @version $Id$
 */
public class CTakesLoader
{
    /**
     * A visitor to go through ontology elements.
     */
    private CTakesOWLVisitor visitor;

    /**
     * A walker for the visitor.
     */
    private OWLOntologyWalker walker;

    /**
     * Our ontology manager.
     */
    private OWLOntologyManager manager;

    /**
     * Where on the filesystem the ontology is.
     */
    private String location;

    /**
     * The owl ontology object.
     */
    private OWLOntology ontology;

    /**
     * The lucene index writer.
     */
    private IndexWriter indexWriter;

    /**
     * The lucene indexDirectory.
     */
    private Directory indexDirectory;

    /**
     * Construct a new loader.
     *
     * @param ontologyLocation where we can find the ontology.
     * @param indexDirectory the directory where we can find the index
     * @throws IOException if lucene throws on index writer creation
     */
    public CTakesLoader(String ontologyLocation, Directory indexDirectory) throws IOException
    {
        this.manager = OWLManager.createOWLOntologyManager();
        this.location = ontologyLocation;
        this.ontology = getOntology();
        Set<OWLOntology> set = new HashSet<>(1);
        set.add(this.ontology);
        this.walker = new OWLOntologyWalker(set);
        this.visitor = new CTakesOWLVisitor(this.walker);
        /* Ontology doesn't look like a word anymore... */
        this.indexDirectory = indexDirectory;
        this.indexWriter = createIndexWriter();
    }

    /**
     * Create a new index writer.
     *
     * @return the index writer
     */
    private IndexWriter createIndexWriter() throws IOException
    {
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_40);
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, analyzer);
        return new IndexWriter(this.indexDirectory, config);
    }

    /**
     * Load the HPO into a lucene index.
     */
    public void load()
    {
        try {
            this.ontology.accept(this.visitor);
            for (OWLObject object : this.ontology.getNestedClassExpressions()) {
                object.accept(this.visitor);
            }
            for (OWLObject object : this.ontology.getClassesInSignature()) {
                object.accept(this.visitor);
            }
            for (OWLObject object : this.ontology.getAxioms()) {
                object.accept(this.visitor);
            }
            this.indexWriter.addDocuments(this.visitor.getDocuments());
        } catch (IOException e) {
            /* XXX */
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Close this loader.
     *
     * @throws IOException if lucene throws.
     */
    public void close() throws IOException
    {
        this.indexWriter.commit();
        this.indexWriter.close();
    }

    /**
     * Construct an owl ontology and return it.
     */
    private OWLOntology getOntology()
    {
        try {
            return this.manager.loadOntologyFromOntologyDocument(new File(this.location));
        } catch (OWLOntologyCreationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
