package edu.asu.bmi.hed.repo.converters;

public class RuleProviderFactory {

    public static RuleProvider getProvider( Class callerType ) {

        if ( ArtifactLoader.class.isAssignableFrom( callerType ) ) {
            return new RuleFolderHunter();
        } else {
            throw new UnsupportedOperationException( "No way to create a provider for class " + callerType );
        }

    }


}
