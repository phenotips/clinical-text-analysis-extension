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
package io.scigraph.services;

import com.google.inject.AbstractModule;
import com.google.inject.util.Modules;

import io.scigraph.annotation.EntityProcessor;
import io.scigraph.annotation.PTEntityProcessor;
import io.scigraph.services.configuration.ApplicationConfiguration;
import io.scigraph.vocabulary.PTVocabularyImpl;
import io.scigraph.vocabulary.Vocabulary;

import ru.vyarus.dropwizard.guice.module.support.ConfigurationAwareModule;

/**
 * A guice module for scigraph usage within phenotips.
 *
 * @version $Id$
 */
public class PTSciGraphModule extends AbstractModule implements ConfigurationAwareModule<ApplicationConfiguration>
{
    /* Hackage time. We need to override certain modules, but we also need to allow the SciGraphApplication module
     * to be configured. So we can't just use Modules.override and be done with it. Instead, we have to wrap
     * the SciGraphApplicationModule instance entirely, setting its configuration when requested. Then when
     * configuring this module, we do the override. Has to be an inner module or we'd recurse on configure()
     * forever.
     */
    /**
     * The wrapped module.
     */
    private SciGraphApplicationModule wrapped;

    /**
     * Construct a new PTSciGraphModule wrapping the scigraph application module given.
     * @param wrapped the wrapped module.
     */
    public PTSciGraphModule(SciGraphApplicationModule wrapped)
    {
        this.wrapped = wrapped;
    }

    @Override
    public void configure()
    {
        install(Modules.override(wrapped).with(new InnerPTSGModule()));
    }

    @Override
    public void setConfiguration(ApplicationConfiguration config)
    {
        wrapped.setConfiguration(config);
    }

    /**
     * The module that actually does bindings to phenotips classes.
     */
    public static final class InnerPTSGModule extends AbstractModule
    {
        @Override
        public void configure()
        {
            bind(Vocabulary.class).to(PTVocabularyImpl.class);
            bind(EntityProcessor.class).to(PTEntityProcessor.class);
        }
    }
}
