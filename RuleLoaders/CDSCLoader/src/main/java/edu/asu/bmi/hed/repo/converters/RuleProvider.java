package edu.asu.bmi.hed.repo.converters;

import java.net.URL;
import java.util.List;

public interface RuleProvider {

    public List<URL> getRules( String sourceRef );

}
