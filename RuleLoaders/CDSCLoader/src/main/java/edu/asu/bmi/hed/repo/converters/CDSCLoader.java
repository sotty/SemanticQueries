package edu.asu.bmi.hed.repo.converters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class CDSCLoader extends AbstractLoader {
  
    private static final String XSL = "/cdsc2hed.xsl";

    private static final String SRC = "/rulebase";
    
    private static final String COV_MAP_URI = "coverageMapURI";
    private static final String COV_MAP = "/coverageMap.xml";

    private static final String ART_MAP_URI = "artifactTypeMapURI";
    private static final String ART_MAP = "/artifactTypeMap.xml";

    private static final String REL_MAP_URI = "relationshipTypeMapURI";
    private static final String REL_MAP = "/relationshipTypeMap.xml";
    
    private static final String CON_MAP_URI = "contributorTypeMapURI";
    private static final String CON_MAP = "/contributorTypeMap.xml";
    
    private static final String STAT_MAP_URI = "statusMapURI";
    private static final String STAT_MAP = "/statusMap.xml";


    private static Map<String,Object> params = new HashMap<String,Object>();
    private String pathToRules = SRC;
    private String targetPath;
    
    static {
		try {
			params.put( COV_MAP_URI, CDSCLoader.class.getResource( COV_MAP ).toURI() );
			params.put( ART_MAP_URI, CDSCLoader.class.getResource( ART_MAP ).toURI() );
			params.put( REL_MAP_URI, CDSCLoader.class.getResource( REL_MAP ).toURI() );
			params.put( CON_MAP_URI, CDSCLoader.class.getResource( CON_MAP ).toURI() );
			params.put( STAT_MAP_URI, CDSCLoader.class.getResource( STAT_MAP ).toURI() );
			
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
    }
    

    public CDSCLoader( String[] args ) {
        if ( args.length >= 1 ) {
            pathToRules = args[ 0 ];
        }
        if ( args.length >= 2 ) {
            targetPath = args[ 1 ];
        }

        this.provider = RuleProviderFactory.getProvider( this.getClass() );
    }

    @Override
    protected InputStream getXSLT() {
        return CDSCLoader.class.getResourceAsStream( XSL );
    }

    protected URL getSourceContent() {
        // relative path first
    	URL url = CDSCLoader.class.getResource( pathToRules );
        if ( url == null ) {
            // if not, try an absolute path
            File f = new File( pathToRules );
            if ( f.exists() ) {
                try {
                    return f.toURI().toURL();
                } catch ( MalformedURLException e ) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return null;
    }

    protected URL getOutputURL( URL url ) {
        if ( targetPath != null ) {
            File folder = new File( targetPath );
            if ( ! folder.exists() ) {
                folder.mkdirs();
            }

            try {
                String fileName = url.toURI().toString();
                fileName = fileName.substring( fileName.lastIndexOf( "/" ) );
                File f = new File( folder.getPath() + fileName );
                return f.toURI().toURL();
            } catch ( MalformedURLException e ) {
                e.printStackTrace();
            } catch ( URISyntaxException e ) {
                e.printStackTrace();
            }
        }
        return super.getOutputURL( url );
    }


    public static void main( String... args ) {
        new CDSCLoader( args ).loadRules( params );
    }

  
}
