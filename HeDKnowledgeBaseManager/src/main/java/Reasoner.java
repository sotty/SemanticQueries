
import java.io.IOException;

import org.mindswap.pellet.jena.PelletInfGraph;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

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


public class Reasoner {

    public static void runJenaQuery( InfModel model ) {

        Query query = QueryFactory.create( "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
                                           "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                                           "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                                           "PREFIX meta: <http://asu.edu/sharpc2b/metadata#> \n" +
                                           "PREFIX sharp: <http://asu.edu/sharpc2b/sharp#> \n" +
                                           "PREFIX ps: <http://asu.edu/sharpc2b/prr-sharp#> \n" +
                                           "PREFIX terms: <http://bmi.asu.edu/terms#>\n" +
                                           "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                                           "PREFIX DUL: <http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#>\n" +
                                           "PREFIX provo: <http://www.w3.org/ns/prov-o#>\n" +
                                           "PREFIX skos-ext:<http://asu.edu/sharpc2b/skos-ext#>\n"+
                                           
                                           "SELECT ?cdsRule ?code \n" +
                                           "WHERE { ?cdsRule rdf:type ps:HeDKnowledgeDocument . "
                                           + "		?cdsRule  meta:applicability ?cover ."
                                           + "		?cover meta:coveredConcept ?code ."
                                           + "      ?code rdf:type terms:DiabetesMellituscode " 
                                           + "}" );

                                           

        QueryExecution qe = QueryExecutionFactory.create( query, model );
        ResultSet results = qe.execSelect();

        ResultSetFormatter.out( System.out, results, query );

        qe.close();

    }


    public static void main( String... args ) throws IOException, OWLOntologyCreationException {

        OWLOntology repositoryContent = Loader.getKnowledgeBase();

        PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( repositoryContent );
        PelletInfGraph graph = new org.mindswap.pellet.jena.PelletReasoner().bind( reasoner.getKB() );
        InfModel model = ModelFactory.createInfModel( graph ) ;

        runJenaQuery( model);

    }


}
