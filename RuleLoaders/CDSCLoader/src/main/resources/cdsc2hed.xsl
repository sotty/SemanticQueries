<?xml version="1.0"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:map="http://mappings"
                xmlns="urn:hl7-org:knowledgeartifact:r1"
                xmlns:dt="urn:hl7-org:cdsdt:r2" xmlns:p1="http://www.w3.org/1999/xhtml"
                xmlns:vmr="urn:hl7-org:vmr:r2" xmlns:xml="http://www.w3.org/XML/1998/namespace"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:fn="http://www.w3.org/2005/xpath-functions"
		>
	<xsl:param name="coverageMapURI" />

	<xsl:template match="/StructuredCDSKnowledge">
		<knowledgeDocument xmlns="urn:hl7-org:knowledgeartifact:r1"
		                   xmlns:dt="urn:hl7-org:cdsdt:r2" xmlns:p1="http://www.w3.org/1999/xhtml"
		                   xmlns:vmr="urn:hl7-org:vmr:r2" xmlns:xml="http://www.w3.org/XML/1998/namespace"
		                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
				>
		<xsl:apply-templates select="Metadata"/>
		</knowledgeDocument>
	</xsl:template>

	<xsl:template match="Metadata">
			<metadata>

				<identifiers>
					<identifier root="TODO"
					            version="TODO" />
				</identifiers>

				<artifactType value="{implementation/knowledgeType}" />

				<schemaIdentifier root="urn:hl7-org:knowledgeartifact:r1" version="1.0" />

				<dataModels>
					<modelReference>
						<description value="Virtual Medical Record model" />
						<referencedModel value="urn:hl7-org:vmr:r2" />
					</modelReference>
				</dataModels>

				<title value="{identity/title}" />

				<description value="{identity/description}" />

				<!--documentation>
					<description value="Explanation" />
					<content>
						<div xmlns="http://www.w3.org/1999/xhtml">TODO</div>
					</content>
				</documentation-->

				<relatedResources>
					<xsl:for-each select="identity/relatedResource">
						<xsl:apply-templates select="." />
					</xsl:for-each>
				</relatedResources>

				<!--supportingEvidence>
					<evidence>
						<qualityOfEvidence code="TODO"/>
						<strengthOfRecommendation code="TODO" codeSystem="CS" codeSystemName="MY" />
						<resources>
							<resource>
								<identifiers>
									<identifier root="TODO" />
								</identifiers>
								<title value="TODO" />
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
					<term><dt:originalText value="TODO"/></term>
				</keyTerms>

				<status value="TODO" />

				<eventHistory>
					<artifactLifeCycleEvent>
						<eventType value="{stateChangeEvent/eventCode}" />
						<eventDateTime value="{stateChangeEvent/eventCode/eventDateTime}" />
					</artifactLifeCycleEvent>
				</eventHistory>


				<contributions>
					<contribution>
						<contributor xsi:type="TODO">
							<name value="{contributor/organization/name}" />
						</contributor>
						<role value="{contributor/roleType}" />
					</contribution>
				</contributions>
<!--Publisher does not exist in CDSC  
				<publishers>
					<publisher xsi:type="TODO">
						<name value="TODO" />
					</publisher>
				</publishers>    --> 

				<usageTerms>
					<rightsDeclaration>
						<assertedRights value="TODO" />
						<rightsHolder xsi:type="TODO">
							<name value="TODO" />
						</rightsHolder>
					</rightsDeclaration>
				</usageTerms>

			</metadata>
	</xsl:template>

	<xsl:template match="relatedResource" >
		<relatedResource>
			<relationship value="{type}" />
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
			<xsl:variable name="cover">   					
				<xsl:apply-templates select="$coverage-table">
					<xsl:with-param name="cdsc-enum">
   						<xsl:value-of select="focus"/>
   					</xsl:with-param>
  				</xsl:apply-templates>					
			</xsl:variable>
			<focus value="{$cover}" />
			
			<description value="{description}" />
			<value code="{code/@code}" codeSystem="{code/@codeSystem}" codeSystemName="{code/@codeSystemName}"><dt:displayName value="{code/@displayName}"/></value>
		</coverage>
	</xsl:template>



	<xsl:key name="cov-lookup" match="map:coverageMapping" use="map:cdsc" />
	<xsl:variable name="coverage-table"
		select="document( $coverageMapURI )/map:coverageMap" />
	<xsl:template match="map:coverageMap">
		<xsl:param name="cdsc-enum" />
		<xsl:value-of select="key('cov-lookup', $cdsc-enum)/map:hed" />
	</xsl:template>



</xsl:stylesheet>