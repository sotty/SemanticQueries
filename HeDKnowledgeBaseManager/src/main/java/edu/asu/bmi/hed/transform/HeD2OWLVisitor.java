package edu.asu.bmi.hed.transform;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.vocabulary.OWL;
import org.hl7.cdsdt.r2.ENXP;
import org.hl7.knowledgeartifact.r1.ArtifactLifeCycleEvent;
import org.hl7.knowledgeartifact.r1.ArtifactLifeCycleEventTypeCore;
import org.hl7.knowledgeartifact.r1.ArtifactStatusTypeCore;
import org.hl7.knowledgeartifact.r1.Contribution;
import org.hl7.knowledgeartifact.r1.ContributorTypeCore;
import org.hl7.knowledgeartifact.r1.KnowledgeDocument;
import org.hl7.knowledgeartifact.r1.KnowledgeResource;
import org.hl7.knowledgeartifact.r1.Metadata;
import org.hl7.knowledgeartifact.r1.Metadata.Applicability;
import org.hl7.knowledgeartifact.r1.Metadata.Categories;
import org.hl7.knowledgeartifact.r1.Metadata.Contributions;
import org.hl7.knowledgeartifact.r1.Organization;
import org.hl7.knowledgeartifact.r1.Party;
import org.hl7.knowledgeartifact.r1.Person;
import org.hl7.knowledgeartifact.r1.ResourceRelationshipReference;
import org.hl7.knowledgeartifact.r1.ResourceRelationshipTypeCore;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

public class HeD2OWLVisitor {
	
	private Map<String,OWLNamedIndividual> map = new HashMap<String, OWLNamedIndividual>();

	public void visit(KnowledgeDocument doc, HeD2OWLHelper helper)
			throws MalformedURLException {

		System.out.println("Visiting HeD Document.... ");

		// local processing
		process(doc, helper);

		// propagate visit to all meaningful children
		visit(doc.getMetadata(), helper);
		System.out.println("Done with HeD Document.... ");
	}

	private void process(KnowledgeDocument doc, HeD2OWLHelper helper) {
		OWLNamedIndividual docInd = helper.asIndividual( doc );
		map.put( "doc", docInd );
		helper.assertType( docInd, "foaf:Document" );
	}

	private void visit(Metadata metadata, HeD2OWLHelper helper) {
		// local...
		process( metadata, helper );

		// all children..
		if ( metadata.getApplicability() != null ) {
			visit( metadata.getApplicability(), helper );
		}

		if ( metadata.getCategories() != null ) {
			visit( metadata.getCategories(), helper );
		}

		if ( metadata.getContributions() != null ) {
			visit( metadata.getContributions(), helper );
		}

		if ( metadata.getEventHistory() != null ) {
			visit( metadata.getEventHistory(), helper );
		}
		// else {
		if ( metadata.getStatus() != null ) {
			visit( metadata.getStatus(), helper );
		}
		//}

		if ( metadata.getPublishers() != null ) {
			visit( metadata.getPublishers(), helper );
		}

		if ( metadata.getRelatedResources() != null ) {
			visit( metadata.getRelatedResources(), helper );
		}


		// TODO : others...
	}

	private void visit( Metadata.RelatedResources relatedResources, HeD2OWLHelper helper ) {
		for ( ResourceRelationshipReference ref : relatedResources.getRelatedResources() ) {
			ResourceRelationshipReference.Resources resss = ref.getResources();
			ResourceRelationshipTypeCore relat = ResourceRelationshipTypeCore.fromValue( ref.getRelationship().getValue() );

			String r;
			switch ( relat ) {
				case ADAPTED_FROM:
					r = "frbr:adaptionof";
					break;
				case ASSOCIATED_RESOURCE:
					r = "frbr:relatedEndeavour";
					break;
				case DEPENDS_ON:
					r = "frbr:complementof";
					break;
				case DERIVED_FROM:
					r = "frbr:successor";
					break;
				case SIMILAR_TO:
					r = "frbr:alternateof";
					break;
				case VERSION_OF:
					r = "frbr:revisionof";
					break;
				default:
					throw new IllegalStateException( "Unrecognized resource relationship " + relat );
			}
			for ( KnowledgeResource kr : resss.getResources() ) {
				OWLNamedIndividual asset = helper.asIndividualInDefaultNS( kr.getIdentifiers().getIdentifier().getRoot() );
				helper.assertType( asset, "foaf:Document" );

				helper.assertObjectProperty( r, map.get( "doc" ), asset );
			}
		}
	}


	private void visit( Metadata.Publishers publishers, HeD2OWLHelper helper ) {
		for ( Party actor : publishers.getPublishers() ) {
			OWLNamedIndividual contri = helper.asIndividual(actor);
			populateParty( actor, contri, helper );
			helper.assertObjectProperty( "frbr:responsibleEntity", map.get( "doc" ), contri );

		}
	}

	private void populateParty( Party actor, OWLNamedIndividual contri, OWLHelper helper ) {
		if ( actor instanceof Person ) {
			helper.assertType(contri, "foaf:Person");
			helper.assertType(contri, "frbr:Person");
			Person p = (Person) actor;
			String name = "";
			for ( ENXP part : p.getName().getParts() ) {
				switch ( part.getType() ) {
					case GIV :
						helper.assertDataProperty("foaf:givenname", contri, part.getValue() );
						name += part.getValue();
						break;
					case FAM :
						helper.assertDataProperty("foaf:surname", contri, part.getValue() );
						if ( name.length() > 0 ) {
							name += " ";
						}
						name += part.getValue();
						break;
				}
			}
			if ( name.length() > 0 ) {
				helper.assertDataProperty("foaf:name", contri, name );
			}

		} else if ( actor instanceof Organization ) {
			helper.assertType(contri, "foaf:Organization" );
			helper.assertType(contri, "frbr:CorporateBody");
			Organization org = (Organization) actor;
			helper.assertDataProperty("foaf:name", contri, org.getName().getValue() );
		}

	}


	private void visit( Metadata.Status state, HeD2OWLHelper helper ) {

		OWLNamedIndividual statusInTime = helper.asIndividual( new Anon() );
		helper.assertType( statusInTime, "pso:StatusInTime" );

		helper.assertObjectProperty("pso:holdsStatusInTime", map.get( "doc" ), statusInTime );

		ArtifactStatusTypeCore type = ArtifactStatusTypeCore.fromValue( state.getValue() );
		OWLNamedIndividual status;
		switch ( type ) {
			case DRAFT:
				status  = helper.asIndividualByQualifiedName( "pso:initial-draft" );
				break;
			case ACTIVE:
				status  = helper.asIndividualByQualifiedName( "pso:published" );
				break;
			case IN_TEST:
				status  = helper.asIndividualByQualifiedName( "pso:reviewed" );
				break;
			case INACTIVE:
				status  = helper.asIndividualByQualifiedName( "pso:withdrawn-from-submission" );
				break;
			default:
				throw new IllegalStateException( "What the ** event type not supported " + type.value() );
		}

		helper.assertObjectProperty("pso:withStatus", statusInTime, status );
	}


	private void visit( Metadata.EventHistory eventHistory, HeD2OWLHelper helper ) {
		for ( ArtifactLifeCycleEvent event : eventHistory.getArtifactLifeCycleEvents() ) {
			String date = event.getEventDateTime().getValue();
			ArtifactLifeCycleEventTypeCore type = ArtifactLifeCycleEventTypeCore.fromValue( event.getEventType().getValue() );

			OWLNamedIndividual status;

			switch ( type ) {
				case CREATED:
					status  = helper.asIndividualByQualifiedName( "pso:initial-draft" );
					break;
				case PRE_PUBLISHED:
					status  = helper.asIndividualByQualifiedName( "pso:accepted-for-publication" );
					break;
				case PUBLISHED:
					status  = helper.asIndividualByQualifiedName( "pso:published" );
					break;
				case REVIEWED:
					status  = helper.asIndividualByQualifiedName( "pso:reviewed" );
					break;
				case SUPERSEDED:
					status  = helper.asIndividualByQualifiedName( "pso:republished" );
					break;
				case WITHDRAWN:
					status  = helper.asIndividualByQualifiedName( "pso:withdrawn-from-submission" );
					break;
				default:
					throw new IllegalStateException( "What the ** event type not supported " + type.value() );
			}


			OWLNamedIndividual statusInTime = helper.asIndividual( new Anon() );
			helper.assertType( statusInTime, "pso:StatusInTime" );

			helper.assertObjectProperty("pso:holdsStatusInTime", map.get( "doc" ), statusInTime );
			helper.assertObjectProperty("pso:withStatus", statusInTime, status);

			OWLNamedIndividual ti = helper.asIndividual( new Anon() );
			helper.assertType( ti, "ti:TimeInterval" );

			helper.assertObjectProperty( "tvc:atTime", statusInTime, ti );
			helper.assertTypedDataProperty( "ti:hasIntervalStartDate", ti, date, "xsd:dateTime" );

		}
	}



	private void process(Metadata metadata, HeD2OWLHelper helper) {
		// TODO Auto-generated method stub

	}

	private void visit(Contributions contributions, HeD2OWLHelper helper) {
		for ( Contribution cont : contributions.getContributions() ) {
			Party actor = cont.getContributor();			
			
			OWLNamedIndividual contri = helper.asIndividual(actor);
			populateParty( actor, contri, helper );

			helper.assertObjectProperty( "frbr:responsibleEntity", map.get( "doc" ), contri );

			OWLNamedIndividual role;
			if ( ContributorTypeCore.AUTHOR.value().equals( cont.getRole().getValue() ) ) {
				role = helper.asIndividualByQualifiedName( "pro:author" );

				helper.assertObjectProperty( "frbr:creator", map.get( "doc" ), contri );
			} else if ( ContributorTypeCore.EDITOR.value().equals( cont.getRole().getValue() ) ) {
				role = helper.asIndividualByQualifiedName( "pro:editor" );

				helper.assertObjectProperty( "frbr:realizer", map.get( "doc" ), contri );
			} else if ( ContributorTypeCore.ENDORSER.value().equals( cont.getRole().getValue() ) ) {
				role = helper.asIndividualByQualifiedName( "pro:authors-agent" );

				helper.assertObjectProperty( "frbr:responsibleEntity", map.get( "doc" ), contri );
			} else if ( ContributorTypeCore.REVIEWER.value().equals( cont.getRole().getValue() ) ) {
				role = helper.asIndividualByQualifiedName( "pro:reviewer" );

				helper.assertObjectProperty( "frbr:responsibleEntity", map.get( "doc" ), contri );
			} else {
				System.err.println( "WARNING : Artifact had an unspecified role, set to author by default " );

				role = helper.asIndividualByQualifiedName( "pro:author" );
				helper.assertObjectProperty( "frbr:creator", map.get( "doc" ), contri );
			}
			
			OWLNamedIndividual roleInTime = helper.asIndividual( new Anon() );
			helper.assertType( roleInTime, "pro:RoleInTime" );
 			
			helper.assertObjectProperty("pro:holdsRoleInTime", contri, roleInTime );
			helper.assertObjectProperty("pro:withRole", roleInTime, role);
		}
	}

	private void visit(Categories categories, HeD2OWLHelper helper) {
		// TODO Auto-generated method stub

	}

	private void visit(Applicability applicability, HeD2OWLHelper helper) {
		// TODO Auto-generated method stub

	}

}
