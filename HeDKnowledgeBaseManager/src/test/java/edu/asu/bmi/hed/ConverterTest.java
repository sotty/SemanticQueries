package edu.asu.bmi.hed;

import edu.asu.bmi.AxiomTester;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;

import java.io.InputStream;
import static org.junit.Assert.*;

public class ConverterTest {


    @Test
    public void testFull() {
        String src = "/identifier.xml";
        String tns = "urn:bmi.asu.edu.test1";

        Converter.convert( getInput( src ),
                           System.out );

        OWLOntology onto = Converter.convertToOntology( getInput( src ) );
        assertEquals( IRI.create( tns ), onto.getOntologyID().getOntologyIRI() );

        new AxiomTester( onto )
                .checkType( "tns:KnowledgeDocument", "foaf:Document" );
    }

    private InputStream getInput( String src ) {
        InputStream is = ConverterTest.class.getResourceAsStream( src );
        assertNotNull( is );
        return is;
    }


}
