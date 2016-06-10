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
/* We need access to some protected stuff in the MainApplication, so there's no
 * choice but to place this in this package. No biggie, since we're already dumping
 * services in that package anyway. */
package io.scigraph.services;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.FileUtils;

import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.views.ViewBundle;
import io.scigraph.owlapi.loader.BatchOwlLoader;
import io.scigraph.owlapi.loader.OwlLoadConfiguration;
import io.scigraph.owlapi.loader.OwlLoadConfigurationLoader;
import io.scigraph.services.configuration.ApplicationConfiguration;

import ru.vyarus.dropwizard.guice.GuiceBundle;

/**
 * A Scigraph application for use with phenotips.
 *
 * @version $Id$
 */
public class PTSciGraphApplication extends MainApplication
{
    /**
     * The load config object.
     */
    private OwlLoadConfiguration config;

    /**
     * Construct a new PTSciGraphApplication.
     * @param loadConfig the location of the load configuration.
     */
    public PTSciGraphApplication(String loadConfig)
    {
        try {
            OwlLoadConfigurationLoader owlLoadConfigurationLoader =
                new OwlLoadConfigurationLoader(new File(loadConfig));
            config = owlLoadConfigurationLoader.loadConfig();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Load the HPO into a scigraph.
     */
    private void loadOntology()
    {
        File graph = new File(config.getGraphConfiguration().getLocation());
        /* There's no other way to reindex, so unfortunately we will have to delete everything every time */
        FileUtils.deleteQuietly(graph);
        try {
            /* In theory SciGraph can download the url on its own. In theory.
             * In practice, it tends to crash about half the time, because it streams the
             * file and hits EOF while reading... So we have to hack around this by
             * downloading the thing ourselves.
             */
            URL url = new URL(config.getOntologies().get(0).url());
            Path temp = Files.createTempFile("hpoLoad", "owl");
            FileUtils.copyURLToFile(url, temp.toFile());
            config.getOntologies().get(0).setUrl(temp.toString());
            BatchOwlLoader.load(config);
        } catch (ExecutionException | InterruptedException | IOException e) {
            /* TODO is this the best way to deal with this? */
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void initialize(Bootstrap<ApplicationConfiguration> bootstrap)
    {
        loadOntology();
        /* Sadly, sadly, there's no way to remove a bundle from bootstrap, so we can't just
         * super.initialize here, as we'd like. Instead we copy paste (!) the code, just to
         * replace the SciGraphApplicationModule down there.
         */
        bootstrap.addBundle(new AssetsBundle("/swagger/", "/docs", "index.html"));
        bootstrap.addBundle(new ViewBundle<ApplicationConfiguration>() {
            @Override
            public Map<String, Map<String, String>> getViewConfiguration(
                ApplicationConfiguration configuration) {
                    return new HashMap<>();
            }
        });
        bootstrap.addBundle(GuiceBundle.builder()
            .enableAutoConfig("io.scigraph.services")
            .injectorFactory(factory).modules(new PTSciGraphModule(new SciGraphApplicationModule()))
            .build());
    }
}
