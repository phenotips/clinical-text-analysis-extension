package org.phenotips.textanalysis.internal;

import java.io.IOException;
import java.util.List;

import au.edu.uq.eresearch.biolark.cr.BioLarK_CR;
import au.edu.uq.eresearch.biolark.cr.Annotation;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

@Component
public class BiolarkWrapperImpl implements BiolarkWrapper, Initializable
{
    private BioLarK_CR biolark;

    @Override
    public void initialize() throws InitializationException
    {
        String propertiesPath;
        try {
            propertiesPath = (new BioLarkResourceGenerator()).generateBiolarkResources();
            this.biolark = new BioLarK_CR(propertiesPath);
        } catch (IOException e) {
            throw new InitializationException(e.getMessage());
        }
    }

    @Override
    public List<Annotation> annotate_plain(String text, boolean longestMatch)
    {
        return this.biolark.annotate_plain(text, longestMatch);
    }

}
