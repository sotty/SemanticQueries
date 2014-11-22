package edu.asu.bmi.hed.repo.converters;


import org.w3c.dom.Document;

import java.io.InputStream;

public interface ArtifactLoader {

    public Document loadAsHeD( InputStream source );

}
