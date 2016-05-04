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

import java.util.Iterator;
import java.util.Properties;

import org.apache.ctakes.dictionary.lookup.MetaDataHit;
import org.apache.ctakes.dictionary.lookup.ae.BaseLookupConsumerImpl;
import org.apache.ctakes.dictionary.lookup.vo.LookupHit;
import org.apache.ctakes.typesystem.type.constants.CONST;
import org.apache.ctakes.typesystem.type.refsem.Entity;
import org.apache.ctakes.typesystem.type.refsem.OntologyConcept;
import org.apache.ctakes.typesystem.type.textsem.EntityMention;
import org.apache.uima.UimaContext;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;

/**
 * A consumer that takes dictionary hits and adds them to a jcas as ontology concepts mapped to the hpo.
 *
 * @version $Id$
 */
public class HPOConsumer extends BaseLookupConsumerImpl
{
    /**
     * The name of the field containing the id name in the properties for this instance.
     */
    private static final String ID_PRP_KEY = "idname";

    /**
     * The name of the field containing the coding scheme in the properties for this instance.
     */
    private static final String CODING_SCHEME_PRP_KEY = "codingScheme";

    private static final String LABEL_PRP_KEY = "labelname";

    /**
     * The properties for this instance.
     */
    private Properties properties;

    /**
     * The maximum number of elements.
     */
    private int maxListSize;

    /**
     * CTOR.
     *
     * @param aCtx the UimaContext
     * @param properties this object's properties
     * @param maxListSize the maximum number of elements.
     */
    public HPOConsumer(UimaContext aCtx, Properties properties, int maxListSize)
    {
        this.properties = properties;
        this.maxListSize = maxListSize;
    }

    @Override
    public void consumeHits(JCas jcas, Iterator<LookupHit> hits)
    {
        while (hits.hasNext()) {
            LookupHit hit = hits.next();
            OntologyConcept concept = getOntologyConcept(jcas, hit);
            Entity e = new Entity(jcas);
            EntityMention annotation = new EntityMention(jcas, hit.getStartOffset(), hit.getEndOffset());
            FSArray array = new FSArray(jcas, 1);
            array.set(0, concept);

            e.setOntologyConcept(concept);
            annotation.setDiscoveryTechnique(CONST.NE_DISCOVERY_TECH_DICT_LOOKUP);
            annotation.setOntologyConceptArr(array);
            annotation.setEntity(e);
            annotation.addToIndexes();
        }
    }

    /**
     * Create an ontology concept array for the lookup hit given.
     *
     * @param jcas the jcas instance
     * @param hit the lookup hit to create the concept array for
     */
    private OntologyConcept getOntologyConcept(JCas jcas, LookupHit hit)
    {
        String codingScheme = this.properties.getProperty(CODING_SCHEME_PRP_KEY);
        String idname = this.properties.getProperty(ID_PRP_KEY);
        String labelName = this.properties.getProperty(LABEL_PRP_KEY);
        MetaDataHit mdh = hit.getDictMetaDataHit();
        String id = mdh.getMetaFieldValue(idname);
        OntologyConcept concept = new OntologyConcept(jcas);
        concept.setCode(id);
        /* I'm not sure what the oui is supposed to be, but it's not being used elsewhere in the pipeline */
        concept.setOui(mdh.getMetaFieldValue(labelName));
        concept.setCodingScheme(codingScheme);
        return concept;
    }
}
