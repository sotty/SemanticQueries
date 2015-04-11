package edu.asu.bmi;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;
import uk.ac.manchester.cs.owl.owlapi.OWLDatatypeImpl;

import java.util.Set;

import static org.junit.Assert.assertTrue;

public class AxiomTester {

    private OWLOntology ontology;
    private OWLDataFactory odf;
    private PrefixManager pfm;

    public AxiomTester( OWLOntology ontology ) {
        this.ontology = ontology;
        this.odf = ontology.getOWLOntologyManager().getOWLDataFactory();
        this.pfm = (PrefixManager) ontology.getOWLOntologyManager().getOntologyFormat( ontology );
    }

    public AxiomTester checkRelationship( String subject, String predicate, String object ) {

        subject = resolvePrefix( subject, pfm );
        object = resolvePrefix( object, pfm );
        predicate = resolvePrefix( predicate, pfm );

        OWLNamedIndividual sub = odf.getOWLNamedIndividual( IRI.create( subject ) );
        OWLNamedIndividual obj = odf.getOWLNamedIndividual( IRI.create( object ) );
        OWLObjectProperty prp = odf.getOWLObjectProperty( IRI.create( predicate ) );

        Set<OWLObjectPropertyAssertionAxiom> info = ontology.getObjectPropertyAssertionAxioms( sub );
        OWLObjectPropertyAssertionAxiom testX = odf.getOWLObjectPropertyAssertionAxiom( prp, sub, obj );

        assertTrue( info.contains( testX ) );

        return this;
    }

    public AxiomTester checkType( String subject, String type ) {

        subject = resolvePrefix( subject, pfm );
        type = resolvePrefix( type, pfm );

        OWLNamedIndividual sub = odf.getOWLNamedIndividual( IRI.create( subject ) );
        OWLClass cls = odf.getOWLClass( IRI.create( type ) );

        Set<OWLClassAssertionAxiom> info = ontology.getClassAssertionAxioms( sub );
        OWLClassAssertionAxiom testX = odf.getOWLClassAssertionAxiom( cls, sub );

        assertTrue( info.contains( testX ) );

        return this;
    }

    private String resolvePrefix( String qname, PrefixManager pfm ) {
        int idx = qname.indexOf( ":" );
        if ( idx < 0 ) {
            return qname;
        }
        String candidatePrefix = qname.substring( 0, idx + 1 );
        if ( pfm.containsPrefixMapping( candidatePrefix ) ) {
            String prefix = pfm.getPrefix( candidatePrefix );
            return prefix + qname.substring( idx + 1 );
        } else {
            return qname;
        }
    }


    public AxiomTester checkValue( String subject, String predicate, String val ) {
        return checkValue( subject, predicate, val, null );
    }

    public AxiomTester checkValue( String subject, String predicate, String val, String type ) {
        subject = resolvePrefix( subject, pfm );
        predicate = resolvePrefix( predicate, pfm );

        OWLNamedIndividual sub = odf.getOWLNamedIndividual( IRI.create( subject ) );
        OWLDataProperty prp = odf.getOWLDataProperty( IRI.create( predicate ) );

        Set<OWLDataPropertyAssertionAxiom> info = ontology.getDataPropertyAssertionAxioms( sub );
        OWLDataPropertyAssertionAxiom testX = odf.getOWLDataPropertyAssertionAxiom( prp, sub,
                                                                                    type == null ? odf.getOWLLiteral( val ) : odf.getOWLTypedLiteral( val, new OWLDatatypeImpl( IRI.create( type ) ) ) );

        assertTrue( info.contains( testX ) );
        return this;
    }


}
