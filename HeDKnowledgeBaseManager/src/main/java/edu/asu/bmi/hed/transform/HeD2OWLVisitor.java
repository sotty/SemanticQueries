package edu.asu.bmi.hed.transform;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.hl7.cdsdt.r2.ENXP;
import org.hl7.cdsdt.r2.EntityNamePartType;
import org.hl7.knowledgeartifact.r1.Contribution;
import org.hl7.knowledgeartifact.r1.Contribution.Role;
import org.hl7.knowledgeartifact.r1.KnowledgeDocument;
import org.hl7.knowledgeartifact.r1.Metadata;
import org.hl7.knowledgeartifact.r1.Metadata.Applicability;
import org.hl7.knowledgeartifact.r1.Metadata.Categories;
import org.hl7.knowledgeartifact.r1.Metadata.Contributions;
import org.hl7.knowledgeartifact.r1.Organization;
import org.hl7.knowledgeartifact.r1.Party;
import org.hl7.knowledgeartifact.r1.Person;
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
		helper.assertType(docInd, "foaf:Document");
	}

	private void visit(Metadata metadata, HeD2OWLHelper helper) {
		// local...
		process(metadata, helper);

		// all children..
		visit(metadata.getApplicability(), helper);
		visit(metadata.getCategories(), helper);
		visit(metadata.getContributions(), helper);
		
		// TODO : others...
	}

	private void process(Metadata metadata, HeD2OWLHelper helper) {
		// TODO Auto-generated method stub

	}

	private void visit(Contributions contributions, HeD2OWLHelper helper) {
		for ( Contribution cont : contributions.getContributions() ) {
			Party actor = cont.getContributor();			
			
			OWLNamedIndividual contri = helper.asIndividual(actor);
			if ( contri instanceof Person ) {
				helper.assertType(contri, "foaf:Person");
				Person p = (Person) contri;
				for ( ENXP part : p.getName().getParts() ) {
					switch ( part.getType() ) {
						case GIV :
							helper.assertDataProperty("foaf:givenname", contri, part.getValue() );
							break;
						case FAM :
							helper.assertDataProperty("foaf:surname", contri, part.getValue() );
							break;
					} 
				}
					
			} else if ( contri instanceof Organization ) {
				helper.assertType(contri, "foaf:Organization");
			}
			
			OWLNamedIndividual role;
			if ( cont.getRole().getValue().equals( "author" ) ) {
				role = helper.asIndividualByFullIri( "pro:author" );
			} else {
				throw new UnsupportedOperationException( "TODO - map role " + cont.getRole() );
			}
			
			OWLNamedIndividual roleInTime = helper.asIndividual( new Object() );
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
