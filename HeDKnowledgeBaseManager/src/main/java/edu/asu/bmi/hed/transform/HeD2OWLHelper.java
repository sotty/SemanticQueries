package edu.asu.bmi.hed.transform;

import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import uk.ac.manchester.cs.owl.owlapi.OWLDatatypeImpl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class HeD2OWLHelper extends OWLHelper {
    private OWLOntology ontology;
    private OWLOntologyManager manager;
    private OWLDataFactory factory;
    private PrefixManager prefixManager;

    public HeD2OWLHelper( OWLOntology ontology, OWLOntologyManager manager, OWLDataFactory factory, PrefixManager prefixManager ) {
        super( ontology, manager, factory, prefixManager );
      
    }


    public void assertNullSafeDataProperty( String property, OWLNamedIndividual src, String tgt, String type ) {
        if ( ! "null".equals( tgt ) ) {
            if ( type.startsWith( "xsd:" ) ) {
                // hack: using the short form
                type = IRI.create( "http://www.w3.org/2001/XMLSchema#" + type.substring( 4 ) ).toString();
            } else {
                System.out.println( "Unkokwn type");
            }
            insert( assertTypedDataProperty( property, src, tgt, type ) );
        }
    }

    public void assertCD( String property, OWLNamedIndividual src, Object code ) {
        OWLNamedIndividual cd = asIndividual( code );
        insert( assertObjectProperty( property, src, cd ) );
        insert( assertType( cd, "skos-ext:ConceptCode" ) );

        String codeVal = extractStringProperty( "getCode", code );
        if ( codeVal != null ) {
            insert( assertDataProperty( "skos-ext:code", cd, codeVal ) );
        }
        String codeSystem = extractStringProperty( "getCodeSystem", code );
        if ( codeSystem != null ) {
            insert( assertDataProperty( "skos-ext:codeSystem", cd, codeSystem ) );
        }
        String codeSystemName = extractStringProperty( "getCodeSystemName", code );
        if ( codeSystemName != null ) {
            insert( assertDataProperty( "skos-ext:codeSystemName", cd, codeSystemName ) );
        }
        String text = extractStringProperty( "getOriginalText", code );
        if ( text != null ) {
            insert( assertDataProperty( "skos-ext:label", cd, text ) );
        }
    }

    private String extractStringProperty( String propName, Object code ) {
        try {
            return (String) code.getClass().getMethod( propName ).invoke( code );
        } catch ( IllegalAccessException e ) {
            e.printStackTrace();
        } catch ( InvocationTargetException e ) {
            e.printStackTrace();
        } catch ( NoSuchMethodException e ) {
            e.printStackTrace();
        }
        return null;
    }


    private String mapOperation( String op ) {
        return op;
    }
   

    public OWLNamedIndividual assertPropertyChain( String path, OWLNamedIndividual srcVar, String modelNS ) {
        StringTokenizer tok = new StringTokenizer( path, "." );
        List<String> chain = new ArrayList<String>( tok.countTokens() );
        while ( tok.hasMoreTokens() ) {
            chain.add( tok.nextToken() );
        }
        Collections.reverse( chain );

        OWLNamedIndividual current = srcVar;
        for ( int j = 0; j < chain.size(); j++ ) {
            String prop = chain.get( j );
            OWLNamedIndividual propCode = asIndividual( modelNS + prop );
            insert( assertObjectProperty( "ops:propCode", current, propCode ) );
            insert( assertDataProperty( "skos-ext:code", propCode, modelNS + prop ) );
            insert( assertType( current, "ops:DomainPropertyExpression" ) );

            if ( j != chain.size() -1 ) {
                OWLNamedIndividual expr = asIndividual( "tns:PropertyExpr_" + System.identityHashCode( prop ) );
                insert( assertObjectProperty( "ops:source", current, expr ) );
                current = expr;
            }
        }
        return current;
    }



}