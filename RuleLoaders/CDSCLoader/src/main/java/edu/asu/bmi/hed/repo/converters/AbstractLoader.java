package edu.asu.bmi.hed.repo.converters;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class AbstractLoader implements ArtifactLoader {


    @Override
    public Document loadAsHeD( InputStream source ) {
        try {
            OutputStream os = transform( source, getXSLT() );

            ByteArrayInputStream is = new ByteArrayInputStream( readBytes( os ) );
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse( is );

        } catch ( Exception e ) {
            e.printStackTrace();
            return null;
        }
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

    protected OutputStream transform( InputStream input, InputStream xslt ) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        StreamSource xslStream = new StreamSource( xslt );
        Transformer transformer = factory.newTransformer( xslStream );
        StreamSource in = new StreamSource( input );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StreamResult out = new StreamResult( baos );

        transformer.transform( in, out );

        System.out.println( new String( baos.toByteArray() ) );

        return baos;
    }

    protected abstract InputStream getXSLT();

    protected void save( Document dox, OutputStream outputStream ) throws IOException {
        XMLSerializer serializer = new XMLSerializer( outputStream, getFormat() );
        serializer.serialize( dox );
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
