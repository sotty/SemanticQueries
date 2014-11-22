package edu.asu.bmi.hed.repo.converters;

import org.w3c.dom.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

public class CDSCLoader extends AbstractLoader {

    private RuleProvider provider;

    public CDSCLoader() {
        this.provider = RuleProviderFactory.getProvider( this.getClass() );
    }

    @Override
    protected InputStream getXSLT() {
        return CDSCLoader.class.getResourceAsStream( "/cdsc2hed.xsl" );
    }

    private void loadRules() {

        List<URL> rules = provider.getRules( "/rulebase" );

        for ( URL url : rules ) {
            try {
                File f = new File( url.toURI() );

                Document hed = loadAsHeD( new FileInputStream( f ) );
                save( hed, getOutputStream( url ) );

                // TODO remove once ready
                break;
            } catch ( Exception e ) {
                e.printStackTrace();
            }
        }
    }

    public static void main( String... args ) {
        new CDSCLoader().loadRules();
    }


    public OutputStream getOutputStream( URL url ) {
        return System.err;
    }
}
