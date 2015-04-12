package edu.asu.bmi.hed.transform;

import com.clarkparsia.owlapi.explanation.BlackBoxExplanation;
import com.clarkparsia.owlapi.explanation.HSTExplanationGenerator;
import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.ConsoleProgressMonitor;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredClassAssertionAxiomGenerator;
import org.semanticweb.owlapi.util.InferredDataPropertyCharacteristicAxiomGenerator;
import org.semanticweb.owlapi.util.InferredDisjointClassesAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentDataPropertiesAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentObjectPropertyAxiomGenerator;
import org.semanticweb.owlapi.util.InferredInverseObjectPropertiesAxiomGenerator;
import org.semanticweb.owlapi.util.InferredObjectPropertyCharacteristicAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredPropertyAssertionGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredSubDataPropertyAxiomGenerator;
import org.semanticweb.owlapi.util.InferredSubObjectPropertyAxiomGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ReasonerHelper {

    public static final List<InferredAxiomGenerator<? extends OWLAxiom>> defaultAxiomGenerators = Collections.unmodifiableList(
            new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>(
                    Arrays.asList(
                            new InferredClassAssertionAxiomGenerator(),
//                            new InferredSubClassAxiomGenerator(),
//                            new InferredEquivalentClassAxiomGenerator(),
//                            new InferredDisjointClassesAxiomGenerator(),

//                            new InferredDataPropertyCharacteristicAxiomGenerator(),
//                            new InferredSubDataPropertyAxiomGenerator(),
//                            new InferredEquivalentDataPropertiesAxiomGenerator(),

//                            new InferredSubObjectPropertyAxiomGenerator(),
//                            new InferredEquivalentObjectPropertyAxiomGenerator(),
//                            new InferredInverseObjectPropertiesAxiomGenerator(),
//                            new InferredObjectPropertyCharacteristicAxiomGenerator(),

                            new InferredPropertyAssertionGenerator()
                    ) ) );


    public void makeInferences( OWLOntology ontology ) {
        OWLOntologyManager manager = ontology.getOWLOntologyManager();
        OWLReasoner owler = initReasoner( ontology );

        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        for (InferredAxiomGenerator<? extends OWLAxiom> axiomGenerator : defaultAxiomGenerators) {
            for (OWLAxiom ax : axiomGenerator.createAxioms(manager, owler)) {
                if ( ! ontology.containsAxiom( ax, true ) ) {
                    if ( ax.isOfType( AxiomType.CLASS_ASSERTION ) ) {
                        OWLClassAssertionAxiom cax = (OWLClassAssertionAxiom) ax;
                        if ( isIndividualInScope( cax.getIndividual(), ontology ) ) {
                            changes.add( new AddAxiom( ontology, ax ) );
                        }
                    } else if ( ax.isOfType( AxiomType.OBJECT_PROPERTY_ASSERTION ) ) {
                        OWLObjectPropertyAssertionAxiom pax = (OWLObjectPropertyAssertionAxiom) ax;
                        if ( isIndividualInScope( pax.getSubject(), ontology ) && ! pax.getProperty().isOWLTopObjectProperty() ) {
                            changes.add( new AddAxiom( ontology, ax ) );
                        }
                    } else if ( ax.isOfType( AxiomType.DATA_PROPERTY_ASSERTION ) ) {
                        OWLDataPropertyAssertionAxiom dax = (OWLDataPropertyAssertionAxiom) ax;
                        if ( isIndividualInScope( dax.getSubject(), ontology ) ) {
                            changes.add( new AddAxiom( ontology, ax ) );
                        }
                    }
                }
            }
        }
        ontology.getOWLOntologyManager().applyChanges( changes );
    }

    private boolean isIndividualInScope( OWLIndividual individual, OWLOntology ontology ) {
        IRI iri = individual.asOWLNamedIndividual().getIRI();
        IRI oiri = ontology.getOntologyID().getOntologyIRI();
        String ns = iri.getNamespace();
        if ( ns.endsWith( "#" ) || ns.endsWith( "/" ) ) {
            ns = ns.substring( 0, ns.length() - 1 );
        }
        if ( ontology.containsIndividualInSignature( iri, false ) ) {
            if ( ns.equals( oiri.toString() ) ) {
                return true;
            }
        }
        return false;
    }


    protected OWLReasoner initReasoner( OWLOntology ontoDescr ) {

        ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
        Configuration config = new Configuration();
        config.ignoreUnsupportedDatatypes = true;
        config.reasonerProgressMonitor = progressMonitor;
        config.throwInconsistentOntologyException = false;


        Configuration.PrepareReasonerInferences prep = new Configuration.PrepareReasonerInferences();

        prep.classClassificationRequired = false;
        prep.objectPropertyClassificationRequired = false;
        prep.realisationRequired = false;

        prep.objectPropertyDomainsRequired = false;
        prep.objectPropertyRangesRequired = false;
        prep.objectPropertyRealisationRequired = false;

        prep.dataPropertyRealisationRequired = false;
        prep.dataPropertyClassificationRequired = false;
        prep.sameAs = false;

        config.prepareReasonerInferences = prep;

        OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();
        OWLReasoner owler = reasonerFactory.createReasoner( ontoDescr, config );
        owler.precomputeInferences(
//                InferenceType.CLASS_HIERARCHY,
                InferenceType.CLASS_ASSERTIONS,

                InferenceType.OBJECT_PROPERTY_ASSERTIONS,
                InferenceType.DATA_PROPERTY_ASSERTIONS

//                InferenceType.DIFFERENT_INDIVIDUALS,
//                InferenceType.SAME_INDIVIDUAL,

//                InferenceType.DISJOINT_CLASSES,

//                InferenceType.OBJECT_PROPERTY_HIERARCHY,
//                InferenceType.DATA_PROPERTY_HIERARCHY
        );


        return owler;
    }


}
