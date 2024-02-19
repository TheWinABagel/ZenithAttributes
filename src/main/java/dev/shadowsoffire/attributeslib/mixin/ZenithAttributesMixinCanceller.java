package dev.shadowsoffire.attributeslib.mixin;

import com.bawnorton.mixinsquared.api.MixinCanceller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class ZenithAttributesMixinCanceller implements MixinCanceller {

    public static final Logger LOGGER = LogManager.getLogger("Zenith Attributes : Mixin Canceller");
    @Override
    public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {
        return false;
    }
}
