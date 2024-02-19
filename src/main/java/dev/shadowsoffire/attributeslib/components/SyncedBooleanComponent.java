package dev.shadowsoffire.attributeslib.components;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;

public class SyncedBooleanComponent extends BooleanComponent implements AutoSyncedComponent {

    public SyncedBooleanComponent(String name) {
        super(name);
    }
}
