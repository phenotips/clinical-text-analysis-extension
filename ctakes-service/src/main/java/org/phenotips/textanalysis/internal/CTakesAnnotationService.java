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

import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

import org.apache.commons.io.FileUtils;
import org.apache.ctakes.typesystem.type.textsem.EntityMention;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;

import org.restlet.data.Form;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;


/**
 * Annotates free text using apache ctakes.
 *
 * @version $Id$
 */
public class CTakesAnnotationService extends ServerResource
{
    /**
     * The location where the HPO index is.
     */
    private static final String INDEX_LOCATION = "data/ctakes/hpo";

    /**
     * The JCas instance used for analysis.
     */
    private static JCas jcas;

    /**
     * The uima analysis engine in use.
     */
    private static AnalysisEngine ae;

    /**
     * The directory where the lucene index is contained.
     */
    private Directory indexDirectory;

    /**
     * CTOR.
     */
    public CTakesAnnotationService()
    {
        super();
        try {
            indexDirectory = new MMapDirectory(new File(INDEX_LOCATION));
            if (!isIndexed()) {
                reindex();
            }
            if (jcas == null) {
                URL engineXML = CTakesAnnotationService.class.getClassLoader().
                                getResource("pipeline/AnalysisEngine.xml");
                XMLInputSource in = new XMLInputSource(engineXML);
                ResourceSpecifier specifier = UIMAFramework.getXMLParser().parseResourceSpecifier(in);
                ae = UIMAFramework.produceAnalysisEngine(specifier);
                jcas = ae.newJCas();
            }
        } catch (IOException | InvalidXMLException | ResourceInitializationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Return whether the HPO has already been indexed.
     */
    private boolean isIndexed()
    {
        return DirectoryReader.indexExists(indexDirectory);
    }

    /**
     * Annotate the text given.
     * @param form the urlencoded form containing params
     * @return the http response object
     */
    @Post
    @Produces("application/json")
    @Consumes("application/x-www-form-urlencoded")
    public List<Map<String, Object>> annotate(Form form)
    {
        String content = form.getFirstValue("content");
        try {
            List<EntityMention> annotations = annotateText(content);
            List<Map<String, Object>> transformed = new ArrayList<>(annotations.size());
            for (EntityMention annotation : annotations) {
                Map<String, Object> map = new HashMap<>(3);
                map.put("start", annotation.getBegin());
                map.put("end", annotation.getEnd());
                Map<String, Object> token = new HashMap<>(1);
                token.put("id", annotation.getEntity().getOntologyConcept().getCode());
                List<Object> terms = new ArrayList<>(1);
                terms.add(annotation.getEntity().getOntologyConcept().getOui());
                token.put("terms", terms);
                map.put("token", token);
                transformed.add(map);
            }
            return transformed;
        } catch (AnalysisEngineProcessException e) {
            throw new ResourceException(e);
        }
    }

    /**
     * Fetch the HPO and reindex it.
     * @return whether it worked
     */
    @Post("json")
    public Map<String, Object> reindex()
    {
        synchronized (CTakesAnnotationService.class) {
            try {
                URL url = new URL("https://compbio.charite.de/jenkins/job/hpo/lastStableBuild/artifact/hp/hp.owl");
                Path temp = Files.createTempFile("hpoLoad", ".owl");
                FileUtils.copyURLToFile(url, temp.toFile());
                CTakesLoader loader = new CTakesLoader(temp.toFile().toString(), indexDirectory);
                loader.load();
                loader.close();
                Map<String, Object> retval = new HashMap<>(1);
                retval.put("success", true);
                return retval;
            } catch (IOException e) {
                throw new ResourceException(e);
            }
        }
    }

    /**
     * Actually interact with CTakes to have the text annotated.
     * @param text the text to annotate
     * @return a list of CTakes EntityMentions
     * @throws AnalysisEngineProcessException if ctakes throws.
     */
    private List<EntityMention> annotateText(String text) throws AnalysisEngineProcessException
    {
        jcas.reset();
        jcas.setDocumentText(text);
        ae.process(jcas);
        List<EntityMention> mentions = new ArrayList<>();
        Iterator<Annotation> iter = jcas.getAnnotationIndex(EntityMention.type).iterator();
        while (iter.hasNext()) {
            mentions.add((EntityMention) iter.next());
        }
        return mentions;
    }
}
