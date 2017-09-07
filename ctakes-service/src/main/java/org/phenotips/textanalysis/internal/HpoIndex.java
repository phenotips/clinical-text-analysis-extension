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
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.restlet.resource.ResourceException;

/**
 * Manages the HPO index to be used by CTAKES.
 *
 * @version $Id$
 */
class HpoIndex
{
    /**
     * The location where the HPO index is.
     */
    private static final String INDEX_LOCATION = "data/ctakes/hpo";

    /**
     * The directory where the Lucene index is contained.
     */
    private Directory indexDirectory;

    HpoIndex()
    {
        try {
            this.indexDirectory = new MMapDirectory(new File(INDEX_LOCATION));
            if (!isIndexed()) {
                reindex();
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Return whether the HPO has already been indexed.
     */
    boolean isIndexed()
    {
        return DirectoryReader.indexExists(this.indexDirectory);
    }

    /**
     * Fetch the HPO and reindex it.
     *
     * @return whether it worked
     */
    synchronized Map<String, Object> reindex()
    {
        try {
            URL url =
                new URL("https://raw.githubusercontent.com/obophenotype/human-phenotype-ontology/master/hp.owl");
            Path temp = Files.createTempFile("hpoLoad", ".owl");
            FileUtils.copyURLToFile(url, temp.toFile());
            CTakesLoader loader = new CTakesLoader(temp.toFile().toString(), this.indexDirectory);
            loader.load();
            loader.close();
            Map<String, Object> retval = new HashMap<>(1);
            retval.put("success", true);
            return retval;
        } catch (IOException e) {
            throw new ResourceException(e);
        }
    }

    Directory getIndex()
    {
        return this.indexDirectory;
    }
}
