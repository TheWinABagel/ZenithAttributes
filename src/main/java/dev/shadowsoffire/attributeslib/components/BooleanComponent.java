package dev.shadowsoffire.attributeslib.components;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.nbt.CompoundTag;

public class BooleanComponent implements Component {
    protected final String name;
    private boolean value = false;

    public BooleanComponent(String name, boolean value) {
        this.name = name;
        this.value = value;
    }

    public BooleanComponent(String name) {
        this.name = name;
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
       this.value = tag.getBoolean(this.name);
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.putBoolean(name, value);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof BooleanComponent bool && bool.name.equals(name) && bool.value == value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }
}
