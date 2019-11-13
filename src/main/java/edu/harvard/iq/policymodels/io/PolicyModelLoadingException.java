package edu.harvard.iq.policymodels.io;

import java.nio.file.Path;

/**
 * An exception thrown while trying to read a policy model from file.
 * @author michael
 */
public class PolicyModelLoadingException extends Exception {
    
    private final Path policyModelFile;

    public PolicyModelLoadingException(Path policyModelFile, String message) {
        super(message);
        this.policyModelFile = policyModelFile;
    }

    public PolicyModelLoadingException(Path policyModelFile, String message, Throwable cause) {
        super(message, cause);
        this.policyModelFile = policyModelFile;
    }

    public Path getPolicyModelFile() {
        return policyModelFile;
    }
    
}
