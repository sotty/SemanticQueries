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

public class ArdenLoader extends AbstractLoader {

    private RuleProvider provider;

    public ArdenLoader() {
        this.provider = RuleProviderFactory.getProvider( this.getClass() );
    }

    @Override
    protected InputStream getXSLT() {
        return ArdenLoader.class.getResourceAsStream( "/arden2hed.xsl" );
    }

    public static void main( String... args ) {
        new ArdenLoader().loadRules( Collections.EMPTY_MAP );
    }


    public OutputStream getOutputStream( URL url ) {
        return System.err;
    }

	@Override
	protected URL getSourceContent() {
		return ArdenLoader.class.getResource( "/rulebase" );
	}
}


