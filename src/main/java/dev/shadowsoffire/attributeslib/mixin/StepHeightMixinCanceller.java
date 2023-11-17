package dev.shadowsoffire.attributeslib.mixin;

import com.bawnorton.mixinsquared.api.MixinCanceller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class StepHeightMixinCanceller implements MixinCanceller {

    public static final Logger LOGGER = LogManager.getLogger("Zenith Attributes : Mixin Canceller");
    @Override
    public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {
            if (mixinClassName.equals("io.github.fabricators_of_create.porting_lib.attributes.mixin.EntityMixin")) {
                LOGGER.info("Cancelling old step height mixin");
                return true;
            }
        return false;
    }
}
