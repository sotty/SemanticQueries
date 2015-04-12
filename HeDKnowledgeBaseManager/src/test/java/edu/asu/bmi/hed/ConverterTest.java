package edu.asu.bmi.hed;

import edu.asu.bmi.AxiomTester;
import edu.asu.bmi.hed.transform.ReasonerHelper;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;

import java.io.InputStream;
import static org.junit.Assert.*;

public class ConverterTest {

    @Test
    public void testId() {
        String src = "/identifier.xml";
        String tns = "urn:bmi.asu.edu.test1";

        Converter.convert( getInput( src ),
                           System.out );

        OWLOntology onto = Converter.convertToOntology( getInput( src ), false );
        assertEquals( IRI.create( tns ), onto.getOntologyID().getOntologyIRI() );

        new AxiomTester( onto )
                .checkType( "tns:KnowledgeDocument", "foaf:Document" );
    }

    @Test
    public void testContributorOrganization() {
        String src = "/contribOrg.xml";
        String tns = "urn:bmi.asu.edu.test2";

        OWLOntology onto = Converter.convertToOntology( getInput( src ), System.out, true );

        assertEquals( IRI.create( tns ), onto.getOntologyID().getOntologyIRI() );

        new AxiomTester( onto )
                .checkType( "tns:KnowledgeDocument", "foaf:Document" )
                .checkType( "tns:KnowledgeDocument", "frbr:Endeavour" )
                .checkRelationship( "tns:KnowledgeDocument", "frbr:responsibleEntity", "tns:Organization_0" )
                .checkRelationship( "tns:KnowledgeDocument", "frbr:creator", "tns:Organization_0" )
                .checkType( "tns:Organization_0", "foaf:Organization" )
                .checkType( "tns:Organization_0", "frbr:CorporateBody" )
                .checkValue( "tns:Organization_0", "foaf:name", "Zynx Health" )
                .checkRelationship( "tns:Organization_0", "pro:holdsRoleInTime", "tns:Anon_1" )
                .checkType( "tns:Anon_1", "pro:RoleInTime" )
                .checkRelationship( "tns:Anon_1", "pro:withRole", "pro:author" );

    }

    @Test
    public void testPublisherPerson() {
        String src = "/publisherPerson.xml";
        String tns = "urn:bmi.asu.edu.test4";

        Converter.convert( getInput( src ),
                           System.out );

        OWLOntology onto = Converter.convertToOntology( getInput( src ), true );

        assertEquals( IRI.create( tns ), onto.getOntologyID().getOntologyIRI() );

        new AxiomTester( onto )
                .checkType( "tns:KnowledgeDocument", "foaf:Document" )
                .checkRelationship( "tns:KnowledgeDocument", "frbr:responsibleEntity", "tns:Person_0" )
                .checkType( "tns:Person_0", "foaf:Person" )
                .checkValue( "tns:Person_0", "foaf:name", "Aziz Boxwala" )
                ;

    }

    @Test
    public void testEventHistory() {
        String src = "/eventStatus.xml";
        String tns = "urn:bmi.asu.edu.test3";

        OWLOntology onto = Converter.convertToOntology( getInput( src ), true );

        assertEquals( IRI.create( tns ), onto.getOntologyID().getOntologyIRI() );

        new AxiomTester( onto )
                .checkType( "tns:KnowledgeDocument", "foaf:Document" )
                .checkRelationship( "tns:KnowledgeDocument", "pso:holdsStatusInTime", "tns:Anon_0" )
                .checkType( "tns:Anon_0", "pso:StatusInTime" )
                .checkRelationship( "tns:Anon_0", "pso:withStatus", "pso:initial-draft" )
                .checkRelationship( "tns:Anon_0", "tvc:atTime", "tns:Anon_1" )
                .checkType( "tns:Anon_1", "ti:TimeInterval" )
                .checkValue( "tns:Anon_1", "ti:hasIntervalStartDate", "20140703", "xsd:dateTime" )
        ;

    }

    @Test
    public void testRelatedResources() {
        String src = "/relRes.xml";
        String tns = "urn:bmi.asu.edu.test5";

        Converter.convert( getInput( src ),
                           System.out );

        OWLOntology onto = Converter.convertToOntology( getInput( src ), System.out, true );

        assertEquals( IRI.create( tns ), onto.getOntologyID().getOntologyIRI() );

        new AxiomTester( onto )
                .checkType( "tns:KnowledgeDocument", "foaf:Document" )
                .checkType( "tns:d1e22", "frbr:Endeavour" )
                .checkRelationship( "tns:KnowledgeDocument", "frbr:complementof", "tns:d1e22" )
                .checkRelationship( "tns:KnowledgeDocument", "frbr:complementof", "tns:d1e39" )
                .checkRelationship( "tns:KnowledgeDocument", "frbr:relatedEndeavour", "tns:d1e74" )
                .checkRelationship( "tns:KnowledgeDocument", "frbr:relatedEndeavour", "tns:d1e91" )
                .checkRelationship( "tns:KnowledgeDocument", "frbr:relatedEndeavour", "tns:d1e56" )
        ;

    }

    private InputStream getInput( String src ) {
        InputStream is = ConverterTest.class.getResourceAsStream( src );
        assertNotNull( is );
        return is;
    }


}
