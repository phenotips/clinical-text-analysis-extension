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

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.jcas.JCas;

class CasFactory implements PooledObjectFactory<JCas>
{

    /**
     * The uima analysis engine in use.
     */
    private AnalysisEngine engine;

    /**
     * @param engine the engine that can create and process CAS objects
     */
    CasFactory(AnalysisEngine engine)
    {
        this.engine = engine;
    }

    @Override
    public PooledObject<JCas> makeObject() throws Exception
    {
        return new DefaultPooledObject<>(this.engine.newJCas());
    }

    @Override
    public void destroyObject(PooledObject<JCas> p) throws Exception
    {
        p.getObject().release();
    }

    @Override
    public boolean validateObject(PooledObject<JCas> p)
    {
        return true;
    }

    @Override
    public void activateObject(PooledObject<JCas> p) throws Exception
    {
        // Nothing to do
    }

    @Override
    public void passivateObject(PooledObject<JCas> p) throws Exception
    {
        p.getObject().reset();
    }

}
