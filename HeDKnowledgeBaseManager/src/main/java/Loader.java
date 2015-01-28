
import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import org.coode.owlapi.rdf.model.RDFTranslator;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.io.StreamDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLIndividualAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.OWLOntologyMerger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Loader {

/*
    public static OWLOntologyManager getManagerWithTheories() {

        ClassPathResource[] res = new ClassPathResource[] {
//                (ClassPathResource) ResourceFactory.newClassPathResource( "ontologies/editor_models/DUL.owl" ),
//                (ClassPathResource) ResourceFactory.newClassPathResource( "ontologies/editor_models/IOLite.owl" ),
//                (ClassPathResource) ResourceFactory.newClassPathResource( "ontologies/editor_models/LMM_L1.owl" ),
//                (ClassPathResource) ResourceFactory.newClassPathResource( "ontologies/editor_models/skos-core.owl" ),
//                (ClassPathResource) ResourceFactory.newClassPathResource( "ontologies/editor_models/skos-ext.owl" ),
//                (ClassPathResource) ResourceFactory.newClassPathResource( "ontologies/editor_models/skos_lmm.owl" ),
//                (ClassPathResource) ResourceFactory.newClassPathResource( "ontologies/editor_models/dc_owl2dl.owl" ),
        		(ClassPathResource) ResourceFactory.newClassPathResource("owl/skos-ext.owl"),
                (ClassPathResource) ResourceFactory.newClassPathResource( "owl/skos-core.owl" ),
                (ClassPathResource) ResourceFactory.newClassPathResource( "owl/terms.owl" ),
                (ClassPathResource) ResourceFactory.newClassPathResource( "owl/prov-o.owl" ),
                (ClassPathResource) ResourceFactory.newClassPathResource( "owl/link.owl" )
        };


        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

        try {
            for ( ClassPathResource r : res ) {
                System.out.println( r.getPath() );
                manager.loadOntologyFromOntologyDocument( r.getInputStream() );
                System.out.println( "--" );
            }

        } catch ( OWLOntologyCreationException e ) {
            e.printStackTrace();
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        return manager;
    }




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
