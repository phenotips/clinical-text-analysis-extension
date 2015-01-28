package org.phenotips.textanalysis.internal;

import java.util.List;

import org.phenotips.textanalysis.TermAnnotation;
import org.phenotips.textanalysis.TermAnnotationClient.AnnotationException;

public class TEST
{

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        try {
            BioLarkAnnotationClient c = new BioLarkAnnotationClient();
            List<TermAnnotation> l = c.annotate("hearing loss and large eyes");
            for (TermAnnotation a : l) {
            	System.out.println(a);
            }
        } catch (AnnotationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
