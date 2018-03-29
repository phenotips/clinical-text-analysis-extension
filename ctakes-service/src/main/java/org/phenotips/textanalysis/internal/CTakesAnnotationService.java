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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.ctakes.typesystem.type.textsem.EntityMention;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.JCasPool;
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
    /** The UIMA analysis engine in use. */
    private AnalysisEngine engine;

    /** A simple pool of JCas instances */
    private final JCasPool jcasPool;

    /** Manages the HPO index to be used by CTAKES. */
    private HpoIndex index;

    /** Basic constructor, initializes all the used services. */
    public CTakesAnnotationService()
    {
        super();
        try {
            this.index = new HpoIndex();
            if (!this.index.isIndexed()) {
                this.index.reindex();
            }
            URL engineXML =
                CTakesAnnotationService.class.getClassLoader().getResource("pipeline/AnalysisEngine.xml");
            XMLInputSource in = new XMLInputSource(engineXML);
            ResourceSpecifier specifier = UIMAFramework.getXMLParser().parseResourceSpecifier(in);
            this.engine = UIMAFramework.produceAnalysisEngine(specifier);
            this.jcasPool = new JCasPool(8, this.engine);
        } catch (IOException | InvalidXMLException | ResourceInitializationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Annotate the text given.
     *
     * @param form the urlencoded form containing params
     * @return the http response object
     * @throws Exception from CTAKES
     */
    @Post
    @Produces("application/json")
    @Consumes("application/x-www-form-urlencoded")
    public List<Map<String, Object>> annotate(Form form) throws Exception
    {
        String content = form.getFirstValue("content");
        JCas jcas = null;
        try {
            jcas = jcasPool.getJCas(0);
            List<EntityMention> annotations = annotateText(content, jcas);
            List<Map<String, Object>> transformed = new ArrayList<>(annotations.size());
            for (EntityMention annotation : annotations) {
                if (annotation == null) {
                    continue;
                }
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
        } catch (Exception e) {
            throw new ResourceException(e);
        } finally {
            if (jcas != null) {
                this.jcasPool.releaseJCas(jcas);
            }
        }
    }

    /**
     * Fetch the HPO and reindex it.
     *
     * @return whether it worked
     */
    @Post("json")
    public Response reindex()
    {
        try {
            this.index.reindex();
            return Response.ok().build();
        } catch (Exception ex) {
            return Response.serverError().build();
        }
    }

    /**
     * Actually interact with CTakes to have the text annotated.
     *
     * @param text the text to annotate
     * @return a list of CTakes EntityMentions
     * @throws AnalysisEngineProcessException if ctakes throws.
     */
    private List<EntityMention> annotateText(String text, JCas jcas) throws AnalysisEngineProcessException
    {
        jcas.setDocumentText(text);
        this.engine.process(jcas);
        List<EntityMention> mentions = new ArrayList<>();
        Iterator<Annotation> iter = jcas.getAnnotationIndex(EntityMention.type).iterator();
        while (iter.hasNext()) {
            mentions.add((EntityMention) iter.next());
        }
        return mentions;
    }
}
