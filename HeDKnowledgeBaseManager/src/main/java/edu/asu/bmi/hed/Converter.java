package edu.asu.bmi.hed;


import edu.asu.bmi.hed.transform.HeD2OWLHelper;
import edu.asu.bmi.hed.transform.HeD2OWLTranslator;
import org.semanticweb.owlapi.model.OWLOntology;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Converter {

    public static void convert( InputStream in, OutputStream out ) {
        new HeD2OWLTranslator().compile( in,
                                         out,
                                         Loader.getManagerWithTheories(),
                                         Loader.getFormatWithPrefixes() );
    }

    public static OWLOntology convertToOntology( InputStream in, boolean withImports ) {
        return new HeD2OWLTranslator().compileAsOntology( in,
                                                          Loader.getManagerWithTheories(),
                                                          Loader.getFormatWithPrefixes(),
                                                          withImports );
    }

    public static OWLOntology convertToOntology( InputStream in, OutputStream out, boolean withImports ) {
        HeD2OWLTranslator tran = new HeD2OWLTranslator();
        OWLOntology onto = tran.compileAsOntology( in,
                                                   Loader.getManagerWithTheories(),
                                                   Loader.getFormatWithPrefixes(),
                                                   withImports );
        tran.stream( onto, out, Loader.getFormatWithPrefixes() );
        return onto;
    }

}
