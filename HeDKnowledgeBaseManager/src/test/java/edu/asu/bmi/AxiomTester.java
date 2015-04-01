package edu.asu.bmi;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;

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


}
