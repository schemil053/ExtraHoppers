package de.schemil053.extrahoppers.util;

import de.schemil053.extrahoppers.Config;

import java.util.function.Supplier;

public enum HopperSpeed implements Supplier<Integer> {
    NORMAL(() -> Config.hopperSpeed), PLUS(() -> Config.hopperPlusSpeed), PLUS_PLUS(() -> Config.hopperPlusPlusSpeed);
    private final Supplier<Integer> supplier;

    HopperSpeed(Supplier<Integer> supplier) {
        this.supplier = supplier;
    }

    @Override
    public Integer get() {
        return supplier.get();
    }
}
