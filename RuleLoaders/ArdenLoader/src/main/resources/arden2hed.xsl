<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="urn:hl7-org:knowledgeartifact:r1"
                xmlns:dt="urn:hl7-org:cdsdt:r2" xmlns:p1="http://www.w3.org/1999/xhtml"
                xmlns:vmr="urn:hl7-org:vmr:r2" xmlns:xml="http://www.w3.org/XML/1998/namespace"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		>

	<xsl:template match="/StructuredCDSKnowledge">
		<knowledgeDocument xmlns="urn:hl7-org:knowledgeartifact:r1"
		                   xmlns:dt="urn:hl7-org:cdsdt:r2" xmlns:p1="http://www.w3.org/1999/xhtml"
		                   xmlns:vmr="urn:hl7-org:vmr:r2" xmlns:xml="http://www.w3.org/XML/1998/namespace"
		                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
				>
		<xsl:apply-templates select="Metadata"/>
		<actionGroup/>
		</knowledgeDocument>
	</xsl:template>

	<xsl:template match="Metadata">
			<metadata>

				<identifiers>
					<identifier root=""
					            version="{Maintenance/Version}" />
				</identifiers>

				<!--artifactType value="Rule" /> All Arden are ECA artifacts  -->

				<schemaIdentifier root="urn:hl7-org:knowledgeartifact:r1" version="{Maintenance/Arden}" />

				<dataModels>
					<modelReference>
						<description value="Virtual Medical Record model" />
						<referencedModel value="urn:hl7-org:vmr:r2" />
					</modelReference>
				</dataModels>

				<title value="{Maintenance/Title}" />

				<description value="{Library/Purpose`}" />

				<documentation>
					<description value="{Library/CitationText}" />
					<content>
						<!-- div xmlns="http://www.w3.org/1999/xhtml">TODO</div> -->
					</content>
				</documentation>

				<relatedResources>
					<xsl:for-each select="identity/relatedResource">
						<xsl:apply-templates select="." />
					</xsl:for-each>
				</relatedResources>

				<supportingEvidence>
					<evidence>
						<qualityOfEvidence code="TODO"/>
						<strengthOfRecommendation code="TODO" codeSystem="CS" codeSystemName="MY" />
						<resources>
							<resource>
								<identifiers>
									<identifier root="TODO" />
								</identifiers>
								<title value="{Library/Citations}" />
								<location value="TODO" />
							</resource>
						</resources>
					</evidence>
				</supportingEvidence-->

				<applicability>
					<xsl:for-each select="coverage">
							<xsl:apply-templates select="." />
					</xsl:for-each>
				</applicability>

				<keyTerms>
					<term><dt:originalText value="{Library/Keywords}"/></term>
				</keyTerms>

				<status value="{Maintenance/Validation}" />

				<!-- eventHistory>
					<artifactLifeCycleEvent>
						<eventType value="TODO" />
						<eventDateTime value="TODO" />
					</artifactLifeCycleEvent>
				</eventHistory> -->

				<contributions>
					<contribution>
						<contributor xsi:type="TODO">
							<name value="{Maintenance/Author}" />
						</contributor>
						<role/>
					</contribution>
				</contributions>

				<publishers>
					<publisher xsi:type="TODO">
						<name value="{Maintenance/Institution}" />
					</publisher>
				</publishers>

				<!-- usageTerms>
					<rightsDeclaration>
						<assertedRights value="TODO" />
						<rightsHolder xsi:type="TODO">
							<name value="TODO" />
						</rightsHolder>
					</rightsDeclaration>
				</usageTerms> -->

			</metadata>
	</xsl:template>

	<xsl:template match="relatedResource" >
		<relatedResource>
			<!--relationship value="{type}" />  -->
			<resources>
				<resource>
					<identifiers>
						<identifier root="{generate-id(resource/location)}" />
					</identifiers>
					<title value="{resource/title}" />
					<description value="{resource/description}" />
					<location value="{resource/location}" />
				</resource>
			</resources>
		</relatedResource>
	</xsl:template>

	<xsl:template match="coverage" >
		<coverage>
			<focus value="ClinicalFocus" />
			<!-- TODO based on admissible values for CDSC:focus, maybe apply a mapping as eplained here http://www.ibm.com/developerworks/library/x-xsltip.html -->
			<description value="{description}" />
			<value code="{code/@code}" codeSystem="{code/@codeSystem}" codeSystemName="{code/@codeSystemName}"><dt:displayName value="@{code/displayName}"/></value>
		</coverage>
	</xsl:template>


</xsl:stylesheet>