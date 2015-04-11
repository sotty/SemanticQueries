package edu.asu.bmi.hed.transform;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.ImportChange;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class OWLHelper {

    protected OWLOntology ontology;
    protected OWLOntologyManager manager;
    protected OWLDataFactory factory;
    protected PrefixManager prefixManager;

    private int next = 0;
    private Map<Integer,Integer> hashing = new HashMap<Integer, Integer>();

    public OWLHelper( OWLOntology onto ) {
        this( onto, new DefaultPrefixManager() );
    }

    public OWLHelper( OWLOntology onto, PrefixManager manager ) {
        this.ontology = onto;
        this.manager = onto.getOWLOntologyManager();
        this.factory = this.manager.getOWLDataFactory();
        this.prefixManager = manager;
    }

    public OWLHelper( OWLOntology ontology, OWLOntologyManager manager, OWLDataFactory factory, PrefixManager prefixManager ) {
        this.ontology = ontology;
        this.manager = manager;
        this.factory = factory;
        this.prefixManager = prefixManager;
    }

    public void registerDataModel( String model ) {
        IRI iri = IRI.create( model );
        ((DefaultPrefixManager) prefixManager).setPrefix( iri.getFragment() + ":", iri.toString() + "#" );
    }


    public String uniqueName( Object x ) {
        if ( x.getClass().getName().contains( "KnowledgeDocument" ) ) {
            return "tns:" + x.getClass().getSimpleName();
        } else {
            return "tns:" + x.getClass().getSimpleName() + "_" + getUid( x );
        }
    }

    private int getUid( Object x ) {
        int hash = System.identityHashCode( x );
        if ( hashing.containsKey( hash ) ) {
            return hashing.get( hash );
        } else {
            int code = next++;
            hashing.put( hash, code );
            return code;
        }
    }

    public OWLClass asClass( String x ) {
        return factory.getOWLClass( IRI.create( x ) );
    }

    public OWLDeclarationAxiom assertClass( OWLClass k ) {
        return factory.getOWLDeclarationAxiom( k );
    }

    public OWLNamedIndividual asIndividual( Object x ) {
        return factory.getOWLNamedIndividual( uniqueName( x ), prefixManager );
    }

    public OWLNamedIndividual asIndividualByFullIri( String x ) {
        return factory.getOWLNamedIndividual( IRI.create( x ) );
    }

    public OWLNamedIndividual asIndividualByQualifiedName( String x ) {
        OWLNamedIndividual ind = factory.getOWLNamedIndividual( x, prefixManager );
        return ind;
    }

    public OWLNamedIndividual asIndividual( String x ) {
        if ( x.startsWith( "{urn:" ) ) {
            String ns = x.substring( 1, x.indexOf( "}" ) );
            String y = x.substring( x.indexOf( "}" ) + 1 );
            return factory.getOWLNamedIndividual( "vmr:" + y, prefixManager );
        } else if ( x.startsWith( "urn:" ) ) {
            String ns = x.substring( 0, x.indexOf( "#" ) + 1 );
            String y = x.substring( x.indexOf( "#" ) + 1 );
            return factory.getOWLNamedIndividual( "vmr:" + y, prefixManager );
        } else {
            return factory.getOWLNamedIndividual( x, prefixManager );
        }
    }

    public OWLNamedIndividual asExpressionIndividual( String op, Object root ) {
        return asIndividual( "tns:" + op + "_" + System.identityHashCode( root ) );
    }

    public OWLNamedIndividual urnAsIndividual( String x ) {
        return factory.getOWLNamedIndividual( IRI.create( x ) );
    }

    public OWLDeclarationAxiom assertIndividual( OWLNamedIndividual ind ) {
        return (OWLDeclarationAxiom) insert( factory.getOWLDeclarationAxiom( ind ) );
    }

    public OWLClassAssertionAxiom assertType( OWLNamedIndividual ind, String klass ) {
        return (OWLClassAssertionAxiom) insert( factory.getOWLClassAssertionAxiom(
                factory.getOWLClass( klass, prefixManager ),
                ind ) );
    }

    public OWLObjectPropertyAssertionAxiom assertObjectProperty( String property, OWLNamedIndividual src, OWLNamedIndividual tgt ) {
        return (OWLObjectPropertyAssertionAxiom) insert ( factory.getOWLObjectPropertyAssertionAxiom(
                factory.getOWLObjectProperty( property, prefixManager ),
                src,
                tgt ) );
    }

    public OWLDataPropertyAssertionAxiom assertTypedDataProperty( String property, OWLNamedIndividual src, String tgt, String type ) {
        return (OWLDataPropertyAssertionAxiom) insert( factory.getOWLDataPropertyAssertionAxiom(
                factory.getOWLDataProperty( property, prefixManager ),
                src,
                factory.getOWLTypedLiteral( tgt, new OWLDatatypeImpl( IRI.create( type ) ) ) ) );
    }

    public OWLDataPropertyAssertionAxiom assertDataProperty( String property, OWLNamedIndividual src, String tgt ) {
        return (OWLDataPropertyAssertionAxiom) insert( factory.getOWLDataPropertyAssertionAxiom(
                factory.getOWLDataProperty( property, prefixManager ),
                src,
                factory.getOWLLiteral( tgt ) ) );
    }

    public OWLAxiom assertDataProperty( String property, OWLNamedIndividual src, boolean flag ) {
        return insert( factory.getOWLDataPropertyAssertionAxiom(
                factory.getOWLDataProperty( property, prefixManager ),
                src,
                factory.getOWLLiteral( flag ) ) );
    }

    public OWLAxiom assertDataProperty( String property, OWLNamedIndividual src, int value ) {
        return insert( factory.getOWLDataPropertyAssertionAxiom(
                factory.getOWLDataProperty( property, prefixManager ),
                src,
                factory.getOWLLiteral( value ) ) );
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




    public void buildOntology( Set set ) {
        for ( Object ax : set ) {
            manager.addAxiom( ontology, (OWLAxiom) ax );
        }
    }

    public OWLAxiom assertSubClass( OWLClass klass, OWLClass superKlass ) {
        return insert( factory.getOWLSubClassOfAxiom( klass, superKlass ) );
    }


	public OWLAxiom insert(OWLAxiom axiom) {
		manager.applyChanges( manager.addAxiom( ontology, axiom ) );
		return axiom;
	}


    public void addImport( OWLOntology onto ) {
        manager.applyChange( new AddImport( ontology, factory.getOWLImportsDeclaration( onto.getOntologyID().getOntologyIRI() ) ) );
    }
}