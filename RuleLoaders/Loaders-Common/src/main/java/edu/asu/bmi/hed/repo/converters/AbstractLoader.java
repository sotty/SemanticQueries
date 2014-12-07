package edu.asu.bmi.hed.repo.converters;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import net.sf.saxon.TransformerFactoryImpl;
import org.w3c.dom.Document;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public abstract class AbstractLoader implements ArtifactLoader {
	
	
	static final String JAXP_SCHEMA_LANGUAGE =
		    "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
		static final String W3C_XML_SCHEMA =
		    "http://www.w3.org/2001/XMLSchema";
		static final String JAXP_SCHEMA_SOURCE =
			    "http://java.sun.com/xml/jaxp/properties/schemaSource";
		
		private static final String SCHEMA_LOC = "/schema/hed/knowledgeartifact/";
		protected static final CharSequence HED_NS = "urn:hl7-org:knowledgeartifact:r1";
		
		
	protected RuleProvider provider;


    public Document loadAsHeD( InputStream source, Map<String,Object> params ) throws Exception {
            OutputStream os = transform( source, getXSLT(), params );

            byte[] data = readBytes( os );
            ByteArrayInputStream is = new ByteArrayInputStream( data );

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance( );
            dbf.setNamespaceAware( true );

            System.out.println( new String(data)); 
            Document dox = dbf.newDocumentBuilder().parse( is );

            save( dox, System.out );

            validate( dox );

            return dox;
    }

    private void validate(Document dox) throws Exception {
		SchemaFactory sf = SchemaFactory.newInstance( W3C_XML_SCHEMA );
			sf.setResourceResolver( new LSResourceResolver() {
				public LSInput resolveResource( String type, String namespaceURI, String publicId,
						String systemId, String baseURI ) {
					if ( ! systemId.endsWith( ".xsd" ) || systemId.startsWith( "http://" ) ) {
                        systemId = "../common/" + systemId.substring( systemId.lastIndexOf( "/" ) + 1 );
                    }
                    String relPath = SCHEMA_LOC + systemId;
                    InputStream is = AbstractLoader.class.getResourceAsStream( relPath );
					return new InputImpl( publicId, systemId, is );
				}
			});
			Schema schema = sf.newSchema( AbstractLoader.class.getResource( SCHEMA_LOC + "knowledgedocument_flat.xsd" ) );
			Validator validator = schema.newValidator();
			validator.validate( new DOMSource( dox ) );
	}

	protected byte[] readBytes( OutputStream os ) {
        if ( os instanceof ByteArrayOutputStream ) {
            return ((ByteArrayOutputStream) os).toByteArray();
        } else {
            throw new IllegalStateException(
                    "Unable to extract bytes from " + os.getClass() + " plase override this method "
            );
        }
    };

    public Document loadDocument( String relativePath ) throws IOException, SAXException, ParserConfigurationException {
        return loadDocument( AbstractLoader.class.getResourceAsStream( relativePath ) );
    }

    public Document loadDocument( InputStream source ) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        configureValidation( dbf );
        dbf.setIgnoringComments( true );
        dbf.setIgnoringElementContentWhitespace( true );
        dbf.setNamespaceAware( true );

        DocumentBuilder db = null;
        db = dbf.newDocumentBuilder();

        return db.parse( source );
    }

    protected void configureValidation( DocumentBuilderFactory dbf ) {
        dbf.setValidating( false );
    }

    protected OutputStream transform( InputStream input, InputStream xslt, Map<String,Object> params ) throws TransformerException, IOException {
        TransformerFactory factory = TransformerFactory.newInstance( TransformerFactoryImpl.class.getName(), null );
        StreamSource xslStream = new StreamSource( xslt );
        Transformer transformer = factory.newTransformer( xslStream );
        
        StreamSource in = new StreamSource( input );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StreamResult out = new StreamResult( baos );

        for ( String key : params.keySet() ) {
        	transformer.setParameter( key, params.get( key ) );
        }
        transformer.transform( in, out );

        return baos;
    }

    protected abstract InputStream getXSLT();
    
    
    protected void loadRules( Map<String,Object> params ) {
    	List<URL> rules = provider.getRules( getSourceContent() );

        for ( URL url : rules ) {
            try {
                File f = new File( url.toURI() );

                Document hed = loadAsHeD( new FileInputStream( f ), params );
                save( hed, getOutputStream( url ) );

            } catch ( Exception e ) {
                e.printStackTrace();
                System.out.println( "Failure with " + url );	
                break;
            }
        }
    }

    protected OutputStream getOutputStream( URL url ) {
    	String srcPath = url.getFile();
    	if ( srcPath != null && ! "".equals( srcPath ) ) {
    		int lastDot = srcPath.lastIndexOf( '.' );
    		if ( lastDot > 0 ) {
    			srcPath = srcPath.substring( 0, lastDot );
    		}
    		srcPath = srcPath + ".hed";
    		try {
				return new FileOutputStream( srcPath );
			} catch (FileNotFoundException e) {				
				e.printStackTrace();
			}
    	}    	
    	return System.err;  	
    }

	protected abstract URL getSourceContent();

	protected void save( Document dox, OutputStream outputStream ) throws IOException {
        XMLSerializer serializer = new XMLSerializer( outputStream, getFormat() );
        serializer.serialize( dox );
        
        if ( outputStream instanceof FileOutputStream ) {
        	FileOutputStream fos = (FileOutputStream) outputStream;
        	fos.flush();
        	fos.close();
        }
    }

    public OutputFormat getFormat() {
        OutputFormat format = new OutputFormat();
        //format.setLineWidth(120);
        format.setIndenting( true );
        format.setIndent( 3 );
        format.setEncoding( "UTF-8" );
        return format;
    }



}
