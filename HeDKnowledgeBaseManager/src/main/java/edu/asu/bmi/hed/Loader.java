package edu.asu.bmi.hed;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxOntologyFormat;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.DefaultOntologyFormat;
import org.semanticweb.owlapi.io.OWLFunctionalSyntaxOntologyFormat;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


public class Loader {

    private static final String FULL_MERGE = "/ontologies/metadata-full.owl";

    public static PrefixOWLOntologyFormat getFormatWithPrefixes() {
        PrefixOWLOntologyFormat format = new ManchesterOWLSyntaxOntologyFormat();

        format.setPrefix( "co", "http://purl.org/co/" );
        format.setPrefix( "owl", "http://www.w3.org/2002/07/owl#" );
        format.setPrefix( "pso", "http://purl.org/spar/pso/" );
        format.setPrefix( "pro", "http://purl.org/spar/pro/" );
        format.setPrefix( "rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#" );
        format.setPrefix( "xsd", "http://www.w3.org/2001/XMLSchema#" );
        format.setPrefix( "foaf", "http://xmlns.com/foaf/0.1/" );
        format.setPrefix( "frbr", "http://purl.org/vocab/frbr/core#" );
        format.setPrefix( "rdfs", "http://www.w3.org/2000/01/rdf-schema#" );
        format.setPrefix( "skos", "http://www.w3.org/2004/02/skos/core#" );
        format.setPrefix( "time", "http://www.w3.org/2006/time#" );
        format.setPrefix( "fabio", "http://purl.org/spar/fabio/" );
        format.setPrefix( "hed", "http://hl7.org/hed#" );
        format.setPrefix( "part", "http://www.ontologydesignpatterns.org/cp/owl/participation.owl#" );
        format.setPrefix( "tvc", "http://www.essepuntato.it/2012/04/tvc/" );
        format.setPrefix( "ti", "http://www.ontologydesignpatterns.org/cp/owl/timeinterval.owl#" );

        return format;
    }
    

    public static OWLOntologyManager getManagerWithTheories() {

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

        try {

            URL url = Loader.class.getResource( FULL_MERGE );
            //System.out.println( "Loading part theory from " + url.getFile() );
            InputStream is = url.openStream();
            manager.loadOntologyFromOntologyDocument( is );
            is.close();
            //System.out.println( "--" );

        } catch ( OWLOntologyCreationException e ) {
            e.printStackTrace();
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        return manager;
    }



/*
    public static OWLOntology getKnowledgeBase() throws IOException, OWLOntologyCreationException {
        OWLOntologyManager manager = getManagerWithTheories();
      
        List<String> resourceNames = Arrays.asList(
        		"owl/DiabetesRule.owl",
        		"owl/CorticoSteroidInhalerReminder.owl",
        		"owl/SDCPertussis.owl",
        		"owl/DopamineComplexIVOrder.owl",
        		"owl/HeartFailureAdmissionToMedSurgOrderset.owl",
        		"owl/NQF0024A_NQF0421.owl"
                 );
        for ( String name : resourceNames ) {
        	ClassPathResource rule1 = (ClassPathResource) ResourceFactory.newClassPathResource( name );
        	addResource( manager, rule1 );      	
        }

        OWLOntologyMerger merger = new OWLOntologyMerger( manager );
        OWLOntology repositoryContent = merger.createMergedOntology(manager, IRI.create("http://bmi.asu.edu/knowledgeRepo") );
        return repositoryContent;
    }

    private static void addResource( OWLOntologyManager manager, ClassPathResource rule ) throws IOException, OWLOntologyCreationException {
        manager.loadOntologyFromOntologyDocument( rule.getInputStream() );
    }
    */

}
