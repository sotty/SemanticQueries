package edu.asu.bmi.hed.repo.converters;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RuleFolderHunter implements RuleProvider {

    @Override
    public List<URL> getRules( String sourceRef ) {
        URL srcFolderURL = CDSCLoader.class.getResource( sourceRef );
        List<URL> rules = new ArrayList<URL>();

        try {
            File srcFolder = new File( srcFolderURL.toURI() );

            if ( srcFolder.exists() && srcFolder.isDirectory() ) {
                for ( File file : srcFolder.listFiles( new FilenameFilter() {
                    @Override
                    public boolean accept( File dir, String name ) {
                        return name.endsWith( ".xml" );
                    }
                } ) ) {
                    try {
                        rules.add( file.toURI().toURL() );
                    } catch ( MalformedURLException e ) {
                        e.printStackTrace();
                        System.err.println( "WARNING : unable to load file " + file.getAbsolutePath() );
                    }
                }

                return rules;
            } else {
                throw new IllegalStateException( "The provided path does not point to a rule folder" );
            }

        } catch ( URISyntaxException e ) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

}
