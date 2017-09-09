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

import org.phenotips.Constants;

import org.xwiki.component.annotation.Component;
import org.xwiki.configuration.internal.AbstractDocumentConfigurationSource;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.DocumentReferenceResolver;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.model.reference.LocalDocumentReference;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @version $Id$
 */
@Component
@Named("textAnnotationServiceConfiguration")
@Singleton
public class RESTWrapperConfigurationSource extends AbstractDocumentConfigurationSource
{
    private static final LocalDocumentReference CONFIGURATION_CLASS =
        new LocalDocumentReference(Constants.CODE_SPACE, "TextAnnotationConfigurationClass");

    private static final LocalDocumentReference CONFIGURATION_DOCUMENT =
        new LocalDocumentReference(Constants.CODE_SPACE, "TextAnnotationConfiguration");

    @Inject
    private DocumentReferenceResolver<EntityReference> resolver;

    @Override
    protected DocumentReference getDocumentReference()
    {
        return this.resolver.resolve(CONFIGURATION_DOCUMENT);
    }

    @Override
    protected LocalDocumentReference getClassReference()
    {
        return CONFIGURATION_CLASS;
    }

    @Override
    protected String getCacheId()
    {
        return "configuration.document.textAnnotationService";
    }
}
