<?xml version="1.0"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:map="http://mappings"
	xmlns="urn:hl7-org:knowledgeartifact:r1" xmlns:dt="urn:hl7-org:cdsdt:r2"
	xmlns:vmr="urn:hl7-org:vmr:r2" xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:fn="http://www.w3.org/2005/xpath-functions">
	<xsl:param name="coverageMapURI" />
	<xsl:param name="artifactTypeMapURI" />
	<xsl:param name="relationshipTypeMapURI" />
	<xsl:param name="contributorTypeMapURI" />
	<xsl:param name="statusMapURI" />

	<xsl:template match="/StructuredCDSKnowledge | /StructuredGuideline ">
		<knowledgeDocument xmlns="urn:hl7-org:knowledgeartifact:r1"
			xsi:type="KnowledgeDocument">
			<xsl:apply-templates select="Metadata" />
			<actionGroup>
				<subElements>
					<simpleAction xsi:type="CreateAction" />
				</subElements>
			</actionGroup>
		</knowledgeDocument>
	</xsl:template>

	<xsl:template match="Metadata">
		<metadata>

			<identifiers>
				<identifier root="TODO" version="TODO" />
			</identifiers>
<xsl:choose>
<xsl:when test="implementation/knowledgeType">
			<xsl:variable name="aType">
				<xsl:apply-templates select="$artifactType-table">
					<xsl:with-param name="cdsc-enum">
						<xsl:value-of select="implementation/knowledgeType" />
					</xsl:with-param>
				</xsl:apply-templates>
			</xsl:variable>
			<artifactType value="{$aType}" />
			</xsl:when>
			<xsl:otherwise>
			<artifactType value="Rule" />
			</xsl:otherwise>
			</xsl:choose>

			<schemaIdentifier root="urn:hl7-org:knowledgeartifact:r1"
				version="1.0" />

			<dataModels>
				<modelReference>
					<description value="Virtual Medical Record model" />
					<referencedModel value="urn:hl7-org:vmr:r2" />
				</modelReference>
			</dataModels>

			<title value="{identity/title}" />

			<description value="{identity/description}" />

			<!--documentation> <description value="Explanation" /> <content> <div 
				xmlns="http://www.w3.org/1999/xhtml">TODO</div> </content> </documentation -->

			<xsl:if test="identity/relatedResource">
				<relatedResources>
					<xsl:for-each select="identity/relatedResource">
						<xsl:apply-templates select="." />
					</xsl:for-each>
				</relatedResources>
			</xsl:if>

			<!--supportingEvidence> <evidence> <qualityOfEvidence code="TODO"/> <strengthOfRecommendation 
				code="TODO" codeSystem="CS" codeSystemName="MY" /> <resources> <resource> 
				<identifiers> <identifier root="TODO" /> </identifiers> <title value="TODO" 
				/> <location value="TODO" /> </resource> </resources> </evidence> </supportingEvidence -->
<xsl:if test="coverage">
			<applicability>
				<xsl:for-each select="coverage">
					<xsl:apply-templates select="." />
				</xsl:for-each>
			</applicability>
			</xsl:if>

			<xsl:variable name="stat">
				<xsl:apply-templates select="$status-table">
					<xsl:with-param name="cdsc-enum">
						<xsl:value-of select="stateChangeEvent/eventCode" />
					</xsl:with-param>
				</xsl:apply-templates>
			</xsl:variable>
			<status value="{$stat}" />


			<xsl:if test="stateChangeEvent">
				<eventHistory>
					<artifactLifeCycleEvent>
						<eventType value="{stateChangeEvent/eventCode}" />
						<xsl:variable name="dts">
							<xsl:value-of select="stateChangeEvent/eventDateTime" />
						</xsl:variable>
						<eventDateTime
							value="{fn:format-date( xs:date($dts),'[Y0001][M01][D01]')}" />
					</artifactLifeCycleEvent>
				</eventHistory>
			</xsl:if>

			<contributions>
				<contribution>
					<contributor xsi:type="Organization">
						<name value="{contributor/organization/name}" />
					</contributor>

					<xsl:variable name="cType">
						<xsl:apply-templates select="$contributorType-table">
							<xsl:with-param name="cdsc-enum">
								<xsl:value-of select="contributor/roleType" />
							</xsl:with-param>
						</xsl:apply-templates>
					</xsl:variable>
					<role value="{$cType}" />

				</contribution>
			</contributions>
			<!--Publisher does not exist in CDSC <publishers> <publisher xsi:type="TODO"> 
				<name value="TODO" /> </publisher> </publishers> -->

			<!--usageTerms> <rightsDeclaration> <assertedRights value="TODO" /> <rightsHolder 
				xsi:type="TODO"> <name value="TODO" /> </rightsHolder> </rightsDeclaration> 
				</usageTerms> -->

		</metadata>
	</xsl:template>

	<xsl:template match="relatedResource">
		<relatedResource>
			<xsl:variable name="relType">
				<xsl:apply-templates select="$relationshipType-table">
					<xsl:with-param name="cdsc-enum">
						<xsl:value-of select="type" />
					</xsl:with-param>
				</xsl:apply-templates>
			</xsl:variable>
			<relationship value="{$relType}" />

			<resources>
				<resource>
					<identifiers>
						<identifier root="{generate-id(resource/location[1])}" />
					</identifiers>
					<title value="{resource/title}" />
					<location value="{resource/location}" />
					<description value="{resource/description}" />
				</resource>
			</resources>
		</relatedResource>
	</xsl:template>

	<xsl:template match="coverage">
		<coverage>
			<xsl:variable name="cover">
				<xsl:apply-templates select="$coverage-table">
					<xsl:with-param name="cdsc-enum">
						<xsl:value-of select="focus" />
					</xsl:with-param>
				</xsl:apply-templates>
			</xsl:variable>
			<focus value="{$cover}" />

			<description value="{description}" />
			<value code="{code/@code}" codeSystem="{code/@codeSystem}"
				codeSystemName="{code/@codeSystemName}">
				<dt:displayName value="{code/@displayName}" />
			</value>
		</coverage>
	</xsl:template>




	<xsl:variable name="coverage-table" select="document( $coverageMapURI )/map:Map" />
	<xsl:key name="cov-lookup" match="map:Mapping" use="map:cdsc" />
	<xsl:template match="map:Map">
		<xsl:param name="cdsc-enum" />
		<xsl:value-of select="key('cov-lookup', $cdsc-enum)/map:hed" />
	</xsl:template>

	<xsl:variable name="artifactType-table"
		select="document( $artifactTypeMapURI )/map:Map" />
	<xsl:key name="artifactType-lookup" match="map:Mapping" use="map:cdsc" />
	<xsl:template match="map:Map">
		<xsl:param name="cdsc-enum" />
		<xsl:value-of select="key('artifactType-lookup', $cdsc-enum)/map:hed" />
	</xsl:template>


	<xsl:variable name="relationshipType-table"
		select="document( $relationshipTypeMapURI )/map:Map" />
	<xsl:key name="relationshipType-lookup" match="map:Mapping"
		use="map:cdsc" />
	<xsl:template match="map:Map">
		<xsl:param name="cdsc-enum" />
		<xsl:message>
			<xsl:value-of select="$cdsc-enum" />
		</xsl:message>
		<xsl:value-of select="key('relationshipType-lookup', $cdsc-enum)/map:hed" />
	</xsl:template>

	<xsl:variable name="contributorType-table"
		select="document( $contributorTypeMapURI )/map:Map" />
	<xsl:key name="contributorType-lookup" match="map:Mapping" use="map:cdsc" />
	<xsl:template match="map:Map">
		<xsl:param name="cdsc-enum" />
		<xsl:value-of select="key('contributorType-lookup', $cdsc-enum)/map:hed" />
	</xsl:template>
	<xsl:variable name="status-table" select="document( $statusMapURI )/map:Map" />
	<xsl:key name="status-lookup" match="map:Mapping" use="map:cdsc" />
	<xsl:template match="map:Map">
		<xsl:param name="cdsc-enum" />
		<xsl:value-of select="key('status-lookup', $cdsc-enum)/map:hed" />
	</xsl:template>


</xsl:stylesheet>