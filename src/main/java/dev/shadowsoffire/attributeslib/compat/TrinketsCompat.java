package dev.shadowsoffire.attributeslib.compat;

import net.fabricmc.loader.api.FabricLoader;

public class TrinketsCompat {

    static {
        if (!FabricLoader.getInstance().isModLoaded("trinkets")) {
            throw new UnsupportedOperationException("This optional compat class requires Trinkets to be loaded.");
        }
    }

    public static void init() { //TODO add
  /*      ModifierSourceType.register(new ModifierSourceType<>(){

            @Override
            public void extract(LivingEntity entity, BiConsumer<AttributeModifier, ModifierSource<?>> map) {
                CuriosApi.getCuriosHelper().getCuriosHandler(entity).ifPresent(handler -> {
                    Map<String, ICurioStacksHandler> curios = handler.getCurios();
                    for (Map.Entry<String, ICurioStacksHandler> entry : curios.entrySet()) {
                        ICurioStacksHandler stacksHandler = entry.getValue();
                        String identifier = entry.getKey();
                        IDynamicStackHandler stackHandler = stacksHandler.getStacks();

                        for (int i = 0; i < stacksHandler.getSlots(); i++) {
                            SlotContext slotContext = new SlotContext(identifier, entity, i, false, true);
                            ItemStack stack = stackHandler.getStackInSlot(i);
                            if (!stack.isEmpty()) {
                                UUID uuid = UUID.nameUUIDFromBytes((identifier + i).getBytes());
                                Multimap<Attribute, AttributeModifier> modifiers = CuriosApi.getCuriosHelper().getAttributeModifiers(slotContext, uuid, stack);
                                ModifierSource<?> src = new ItemModifierSource(stack);
                                modifiers.values().forEach(m -> map.accept(m, src));
                            }
                        }
                    }
                });
            }

            @Override
            public int getPriority() {
                return 20;
            }
        });*/

    }
}
