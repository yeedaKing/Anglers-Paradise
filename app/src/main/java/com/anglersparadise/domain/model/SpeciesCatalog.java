// app/src/main/java/com/anglersparadise/domain/model/SpeciesCatalog.java
package com.anglersparadise.domain.model;

import java.util.Random;

public final class SpeciesCatalog {
    private static final String[] NAMES = new String[]{
            "Bluegill","Shrimp","Carp","Catfish",
            "Perch","Pike","Sunfish"
    };
    private static final Random RNG = new Random();

    public static String randomName() {
        return NAMES[RNG.nextInt(NAMES.length)];
    }

    /** Size bucket 1..5 */
    public static int randomSize() {
        return 1 + RNG.nextInt(5);
    }

    private SpeciesCatalog() {}
}
