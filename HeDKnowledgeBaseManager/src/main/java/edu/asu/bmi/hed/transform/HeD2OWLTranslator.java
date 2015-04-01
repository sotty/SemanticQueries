package edu.asu.bmi.hed.transform;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxOntologyFormat;
import org.hl7.knowledgeartifact.r1.KnowledgeDocument;
import org.hl7.knowledgeartifact.r1.Metadata;
import org.hl7.knowledgeartifact.r1.Metadata.Applicability;
import org.hl7.knowledgeartifact.r1.Metadata.Categories;
import org.hl7.knowledgeartifact.r1.Metadata.Contributions;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLFunctionalSyntaxOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


public class HeD2OWLTranslator {

    private static final String HED = "org.hl7.knowledgeartifact.r1";

    public HeD2OWLTranslator() {
        super();

    }

    public void compile( InputStream inStream, OutputStream outStream, OWLOntologyManager manager, PrefixOWLOntologyFormat format ) {
        OWLOntology result = compileAsOntology( inStream, manager, format );

        stream( result,
                outStream,
                format
        );
    }

    public OWLOntology compileAsOntology( InputStream inStream, OWLOntologyManager manager, PrefixOWLOntologyFormat format ) {
        KnowledgeDocument hed = (KnowledgeDocument) loadModel( HED, inStream );

        Object vid = getIdentifiersList( hed ).iterator().next();
        String root = getRoot( vid );
        String version = getVersion( vid );

        PrefixManager prefixManager = format.asPrefixOWLOntologyFormat();
        format.setPrefix( "tns", root + "#" );

        OWLOntology result = transform( hed, root, version, manager, format );

        return result;
    }


    public OWLOntology transform( KnowledgeDocument doc, String root, String version, OWLOntologyManager manager, OWLOntologyFormat format ) {
        System.out.println( "Transforming...." );
        OWLOntology ontology = null;

        try {
            OWLDataFactory factory = manager.getOWLDataFactory();

            ontology = manager.createOntology( new OWLOntologyID(
                    IRI.create( root ),
                    IRI.create( root + "/" + version ) ) );

            HeD2OWLHelper helper = new HeD2OWLHelper( ontology, manager, factory, format.asPrefixOWLOntologyFormat() );
            manager.setOntologyFormat( ontology, format.asPrefixOWLOntologyFormat() );

            new HeD2OWLVisitor().visit( doc, helper );

        } catch ( Exception e ) {
            e.printStackTrace();
            try {
                return OWLManager.createOWLOntologyManager().createOntology();
            } catch (OWLOntologyCreationException e1) {
                e1.printStackTrace();
                return null;
            }
        } finally {
        }

        return ontology;
    }



  
	public boolean stream( OWLOntology onto, OutputStream stream, OWLOntologyFormat format ) {
        try {
            onto.getOWLOntologyManager().saveOntology( onto, format, stream );
            return true;
        } catch (OWLOntologyStorageException e) {
            return false;
        }
    }


    public Object loadModel( String model, InputStream source ) {
        try {
            JAXBContext jc = JAXBContext.newInstance( model );
            Unmarshaller unmarshaller = jc.createUnmarshaller();

            return unmarshaller.unmarshal( source );
        } catch ( JAXBException e ) {
            e.printStackTrace();
            return null;
        }

    }

    public static void dumpModel( Object model, OutputStream target ) {
        try {
            JAXBContext jc = JAXBContext.newInstance( HED );
            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );

            marshaller.marshal( model, target );
        } catch ( JAXBException e ) {
            e.printStackTrace();
        }

    }


    private String getRoot( Object x ) {
        try {
            return (String) x.getClass().getMethod( "getRoot", null ).invoke( x );
        } catch ( IllegalAccessException e ) {
            e.printStackTrace();
        } catch ( InvocationTargetException e ) {
            e.printStackTrace();
        } catch ( NoSuchMethodException e ) {
            e.printStackTrace();
        }
        return "http://" + UUID.randomUUID().toString();
    }

    private String getVersion( Object x ) {
        try {
            return (String) x.getClass().getMethod( "getVersion", null ).invoke( x );
        } catch ( IllegalAccessException e ) {
            e.printStackTrace();
        } catch ( InvocationTargetException e ) {
            e.printStackTrace();
        } catch ( NoSuchMethodException e ) {
            e.printStackTrace();
        }
        return "http://" + UUID.randomUUID().toString() + "/1.0" ;
    }


    public List getIdentifiersList( Object root ) {
        try {
            Object metaData = root.getClass().getMethod( "getMetadata" ).invoke( root );
            Object ids = metaData.getClass().getMethod( "getIdentifiers" ).invoke( metaData );
            return (List) ids.getClass().getMethod( "getIdentifiers" ).invoke( ids );
        } catch ( IllegalAccessException e ) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch ( InvocationTargetException e ) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch ( NoSuchMethodException e ) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return Collections.EMPTY_LIST;
    }
    

}