package edu.asu.bmi.hed.repo.converters;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class CDSCLoader extends AbstractLoader {
  
    private static final String XSL = "/cdsc2hed.xsl";
    private static final String COV_MAP = "/coverageMap.xml";
    private static final String SRC = "/rulebase";
    
    private static final String COV_MAP_URI = "coverageMapURI";
    
    
    private static Map<String,Object> params = new HashMap<String,Object>();
    
    static {
    	URI covMapURI;
		try {
			covMapURI = CDSCLoader.class.getResource( COV_MAP ).toURI();
			params.put( COV_MAP_URI, covMapURI );
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}  	
    }
    
    public CDSCLoader() {
        this.provider = RuleProviderFactory.getProvider( this.getClass() );
    }

    @Override
    protected InputStream getXSLT() {
        return CDSCLoader.class.getResourceAsStream( XSL );
    }

    protected URL getSourceContent() {
    	return CDSCLoader.class.getResource( SRC );       
    }


    public static void main( String... args ) {
        new CDSCLoader().loadRules( params );
    }

  
}
