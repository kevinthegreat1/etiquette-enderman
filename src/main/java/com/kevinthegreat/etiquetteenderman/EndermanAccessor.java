package com.kevinthegreat.etiquetteenderman;

public interface EndermanAccessor {
    boolean etiquetteEnderman$askedForConsent();

    void etiquetteEnderman$setAskedForConsent(boolean asked);

    boolean etiquetteEnderman$getResponded();

    boolean etiquetteEnderman$getConsent();

    void etiquetteEnderman$setConsent(boolean consent);
}
