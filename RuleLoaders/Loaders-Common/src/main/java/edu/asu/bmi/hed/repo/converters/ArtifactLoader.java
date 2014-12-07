package edu.asu.bmi.hed.repo.converters;


import org.w3c.dom.Document;

import java.io.InputStream;
import java.util.Map;

public interface ArtifactLoader {

    public Document loadAsHeD( InputStream source, Map<String,Object> params ) throws Exception;

}
