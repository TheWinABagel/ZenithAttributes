package dev.shadowsoffire.attributeslib.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.shadowsoffire.attributeslib.ALConfig;
import dev.shadowsoffire.attributeslib.AttributesLib;
import dev.shadowsoffire.attributeslib.api.IFormattableAttribute;
import dev.shadowsoffire.attributeslib.mixin.accessors.AbstractContainerScreenAccessor;
import dev.shadowsoffire.attributeslib.mixin.accessors.GuiGraphicsAccessor;
import dev.shadowsoffire.placebo.PlaceboClient;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.*;

public class AttributesGui implements Renderable, GuiEventListener, NarratableEntry {

    public static final ResourceLocation TEXTURES = AttributesLib.loc("textures/gui/attributes_gui.png");
    public static final int ENTRY_HEIGHT = 22;
    public static final int MAX_ENTRIES = 6;
    public static final int WIDTH = 131;

    // There's only one player, so we can just happily track if this menu was open via static field.
    // It isn't persistent through sessions, but that's not a huge issue.
    public static boolean wasOpen = false;
    // Similar to the above, we use a static field to record where the scroll bar was.
    protected static float scrollOffset = 0;
    // Ditto.
    protected static boolean hideUnchanged = false;
    protected static boolean swappedFromTrinkets = false;

    protected final InventoryScreen parent;
    protected final Player player;
    protected final Font font = Minecraft.getInstance().font;
    public final ImageButton toggleBtn;
    protected final ImageButton recipeBookButton;
    public final HideUnchangedButton hideUnchangedBtn;

    protected int leftPos, topPos;
    protected boolean scrolling;
    protected int startIndex;
    protected List<AttributeInstance> data = new ArrayList<>();
    @Nullable
    protected AttributeInstance selected = null;
    protected boolean open = false;
    protected long lastRenderTick = -1;

    public AttributesGui(InventoryScreen parent) {
        this.parent = parent;
        this.player = Minecraft.getInstance().player;
        this.refreshData();
        this.leftPos = ((AbstractContainerScreenAccessor) parent).getLeftPos() - WIDTH;
        this.topPos = ((AbstractContainerScreenAccessor) parent).getTopPos();
        this.toggleBtn = new ImageButton(((AbstractContainerScreenAccessor) parent).getLeftPos() + 63, ((AbstractContainerScreenAccessor) parent).getTopPos() + 10, 10, 10, WIDTH, 0, 10, TEXTURES, 256, 256, btn -> {
            this.toggleVisibility();
        }, Component.translatable("zenith_attributes.gui.show_attributes")) {
            @Override
            public void setFocused(boolean pFocused) {}
        };
        if (this.parent.children().size() > 1) {
            GuiEventListener btn = this.parent.children().get(0);
            this.recipeBookButton = btn instanceof ImageButton imgBtn ? imgBtn : null;
        }
        else this.recipeBookButton = null;
        this.hideUnchangedBtn = new HideUnchangedButton(0, 0);
    }

    public void refreshData() {
        this.data.clear();
        BuiltInRegistries.ATTRIBUTE.stream()
                .map(this.player::getAttribute)
                .filter(Objects::nonNull)
                .filter(ai -> !ALConfig.hiddenAttributes.contains(BuiltInRegistries.ATTRIBUTE.getKey(ai.getAttribute())))
                .filter(ai -> !hideUnchanged || (ai.getBaseValue() != ai.getValue()))
                .forEach(this.data::add);
        this.data.sort(this::compareAttrs);
        this.startIndex = (int) (scrollOffset * this.getOffScreenRows() + 0.5D);
    }

    public void toggleVisibility() {
        this.open = !this.open;
        if (this.open && this.parent.getRecipeBookComponent().isVisible()) {
            this.parent.getRecipeBookComponent().toggleVisibility();
        }
        this.hideUnchangedBtn.visible = this.open;

        int newLeftPos;
        if (this.open && this.parent.width >= 379) {
            newLeftPos = 177 + (this.parent.width - ((AbstractContainerScreenAccessor) this.parent).getImageWidth() - 200) / 2;
        }
        else {
            newLeftPos = (this.parent.width - ((AbstractContainerScreenAccessor) this.parent).getImageWidth()) / 2;
        }

        ((AbstractContainerScreenAccessor) this.parent).setLeftPos(newLeftPos);
        this.leftPos = ((AbstractContainerScreenAccessor) this.parent).getLeftPos() - WIDTH;
        this.topPos = ((AbstractContainerScreenAccessor) this.parent).getTopPos();

        if (this.recipeBookButton != null) this.recipeBookButton.setPosition(((AbstractContainerScreenAccessor) this.parent).getLeftPos() + 104, this.parent.height / 2 - 22);
        this.hideUnchangedBtn.setPosition(this.leftPos + 7, this.topPos + 151);
    }

    protected int compareAttrs(AttributeInstance a1, AttributeInstance a2) {
        String name = I18n.get(a1.getAttribute().getDescriptionId());
        String name2 = I18n.get(a2.getAttribute().getDescriptionId());
        return name.compareTo(name2);
    }

    @Override
    public boolean isMouseOver(double pMouseX, double pMouseY) {
        if (!this.open) return false;
        return this.isHovering(0, 0, WIDTH, 166, pMouseX, pMouseY);
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTicks) {
        this.toggleBtn.setX(((AbstractContainerScreenAccessor) this.parent).getLeftPos() + 63);
        this.toggleBtn.setY(((AbstractContainerScreenAccessor) this.parent).getTopPos() + 10);
        if (this.parent.getRecipeBookComponent().isVisible()) this.open = false;
        wasOpen = this.open;
        if (!this.open) return;

        if (this.lastRenderTick != PlaceboClient.ticks) {
            this.lastRenderTick = PlaceboClient.ticks;
            this.refreshData();
        }

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURES);
        int left = this.leftPos;
        int top = this.topPos;
        gfx.blit(TEXTURES, left, top, 0, 0, WIDTH, 166);
        int scrollbarPos = (int) (117 * scrollOffset);
        gfx.blit(TEXTURES, left + 111, top + 16 + scrollbarPos, 244, this.isScrollBarActive() ? 0 : 15, 12, 15);
        int idx = this.startIndex;
        while (idx < this.startIndex + MAX_ENTRIES && idx < this.data.size()) {
            this.renderEntry(gfx, this.data.get(idx), this.leftPos + 8, this.topPos + 16 + ENTRY_HEIGHT * (idx - this.startIndex), mouseX, mouseY);
            idx++;
        }
        this.renderTooltip(gfx, mouseX, mouseY);
        gfx.drawString(font, Component.translatable("zenith_attributes.gui.attributes"), this.leftPos + 8, this.topPos + 5, 0x404040, false);
        gfx.drawString(font, Component.literal("Hide Unchanged"), this.leftPos + 20, this.topPos + 152, 0x404040, false);
    }

    @SuppressWarnings({"NoTranslation"})
    protected void renderTooltip(GuiGraphics gfx, int mouseX, int mouseY) {
        AttributeInstance inst = this.getHoveredSlot(mouseX, mouseY);
        if (inst != null) {
            Attribute attr = inst.getAttribute();

            IFormattableAttribute fAttr = (IFormattableAttribute) attr;
            List<Component> list = new ArrayList<>();
            MutableComponent name = Component.translatable(attr.getDescriptionId()).withStyle(Style.EMPTY.withColor(ChatFormatting.GOLD).withUnderlined(true));

            if (AttributesLib.getTooltipFlag().isAdvanced()) {
                Style style = Style.EMPTY.withColor(ChatFormatting.GRAY).withUnderlined(false);
                name.append(Component.literal(" [" + BuiltInRegistries.ATTRIBUTE.getKey(attr) + "]").withStyle(style));
            }
            list.add(name);

            String key = attr.getDescriptionId() + ".desc";

            if (I18n.exists(key)) {
                Component txt = Component.translatable(key).withStyle(ChatFormatting.YELLOW, ChatFormatting.ITALIC);
                list.add(txt);
            }
            else if (AttributesLib.getTooltipFlag().isAdvanced()) {
                Component txt = Component.literal(key).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
                list.add(txt);
            }

            list.add(CommonComponents.EMPTY);

            ChatFormatting color = ChatFormatting.GRAY;
            if (attr instanceof RangedAttribute ra) {
                if (inst.getValue() > inst.getBaseValue()) {
                    color = ChatFormatting.YELLOW;
                }
                else if (inst.getValue() < inst.getBaseValue()) {
                    color = ChatFormatting.RED;
                }
            }
            MutableComponent valueComp = fAttr.toValueComponent(null, inst.getValue(), AttributesLib.getTooltipFlag());
            list.add(Component.translatable("zenith_attributes.gui.current", valueComp.withStyle(color)).withStyle(ChatFormatting.GRAY));

            MutableComponent baseVal = fAttr.toValueComponent(null, inst.getBaseValue(), AttributesLib.getTooltipFlag());

            baseVal = Component.translatable("zenith_attributes.gui.base", baseVal);
            if (attr instanceof RangedAttribute ra) {
                Component min = fAttr.toValueComponent(null, ra.getMinValue(), AttributesLib.getTooltipFlag());
                min = Component.translatable("zenith_attributes.gui.min", min);
                double maxValue = ra.getMaxValue();
                if (maxValue == Double.MAX_VALUE) maxValue = 8192D; // to stop Double.MAXVALUE madness
                Component max = fAttr.toValueComponent(null, maxValue, AttributesLib.getTooltipFlag());

                max = Component.translatable("zenith_attributes.gui.max", max);
                list.add(Component.translatable("%s \u2507 %s \u2507 %s", baseVal, min, max).withStyle(ChatFormatting.GRAY));
            }
            else {
                list.add(baseVal.withStyle(ChatFormatting.GRAY));
            }

            List<ClientTooltipComponent> finalTooltip = new ArrayList<>(list.size());
            for (Component txt : list) {
                this.addComp(txt, finalTooltip);
            }

            if (inst.getModifiers().stream().anyMatch(modif -> modif.getAmount() != 0)) {
                this.addComp(CommonComponents.EMPTY, finalTooltip);
                this.addComp(Component.translatable("zenith_attributes.gui.modifiers").withStyle(ChatFormatting.GOLD), finalTooltip);

                Map<UUID, ModifierSource<?>> modifiersToSources = new HashMap<>();

                for (ModifierSourceType<?> type : ModifierSourceType.getTypes()) {
                    type.extract(this.player, (modif, source) -> modifiersToSources.put(modif.getId(), source));
                }

                Component[] opValues = new Component[3];

                for (Operation op : Operation.values()) {
                    List<AttributeModifier> modifiers = new ArrayList<>(inst.getModifiers(op));
                    double opValue = modifiers.stream().mapToDouble(AttributeModifier::getAmount).reduce(op == Operation.MULTIPLY_TOTAL ? 1 : 0, (res, elem) -> op == Operation.MULTIPLY_TOTAL ? res * (1 + elem) : res + elem);

                    modifiers.sort(ModifierSourceType.compareBySource(modifiersToSources));
                    for (AttributeModifier modif : modifiers) {
                        if (modif.getAmount() != 0) {
                            Component comp = fAttr.toComponent(modif, AttributesLib.getTooltipFlag());
                            var src = modifiersToSources.get(modif.getId());
                            finalTooltip.add(new AttributeModifierComponent(src, comp, this.font, this.leftPos - 16));
                        }
                    }
                    color = ChatFormatting.GRAY;
                    double threshold = op == Operation.MULTIPLY_TOTAL ? 1.0005 : 0.0005;

                    if (opValue > threshold) {
                        color = ChatFormatting.YELLOW;
                    }
                    else if (opValue < -threshold) {
                        color = ChatFormatting.RED;
                    }
                    Component valueComp2 = fAttr.toValueComponent(op, opValue, AttributesLib.getTooltipFlag()).withStyle(color);
                    Component comp = Component.translatable("zenith_attributes.gui." + op.name().toLowerCase(Locale.ROOT), valueComp2).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
                    opValues[op.ordinal()] = comp;
                }

                if (AttributesLib.getTooltipFlag().isAdvanced()) {
                    this.addComp(CommonComponents.EMPTY, finalTooltip);
                    for (Component comp : opValues) {
                        this.addComp(comp, finalTooltip);
                    }
                }
            }

            ((GuiGraphicsAccessor) gfx).callRenderTooltipInternal(font, finalTooltip, this.leftPos - 16 - finalTooltip.stream().map(c -> c.getWidth(this.font)).max(Integer::compare).get(), mouseY, DefaultTooltipPositioner.INSTANCE);
        }
    }

    private void addComp(Component comp, List<ClientTooltipComponent> finalTooltip) {
        if (comp == CommonComponents.EMPTY) {
            finalTooltip.add(ClientTooltipComponent.create(comp.getVisualOrderText()));
        }
        else {
            for (FormattedText fTxt : this.font.getSplitter().splitLines(comp, this.leftPos - 16, comp.getStyle())) {
                finalTooltip.add(ClientTooltipComponent.create(Language.getInstance().getVisualOrder(fTxt)));
            }
        }
    }

    private void renderEntry(GuiGraphics gfx, AttributeInstance inst, int x, int y, int mouseX, int mouseY) {
        boolean hover = this.getHoveredSlot(mouseX, mouseY) == inst;
        gfx.blit(TEXTURES, x, y, 142, hover ? ENTRY_HEIGHT : 0, 100, ENTRY_HEIGHT);

        Component txt = Component.translatable(inst.getAttribute().getDescriptionId());
        int splitWidth = 60;
        List<FormattedCharSequence> lines = this.font.split(txt, splitWidth);
        // We can only actually display two lines here, but we need to forcibly create two lines and then scale down.
        while (lines.size() > 2) {
            splitWidth += 10;
            lines = this.font.split(txt, splitWidth);
        }

        PoseStack stack = gfx.pose();

        stack.pushPose();
        float scale = 1;
        int maxWidth = lines.stream().map(this.font::width).max(Integer::compareTo).get();
        if (maxWidth > 66) {
            scale = 66F / maxWidth;
            stack.scale(scale, scale, 1);
        }

        for (int i = 0; i < lines.size(); i++) {
            var line = lines.get(i);
            float width = this.font.width(line) * scale;
            float lineX = (x + 1 + (68 - width) / 2) / scale;
            float lineY = (y + (lines.size() == 1 ? 7 : 2) + i * 10) / scale;
            gfx.drawString(font, line, (int) lineX, (int) lineY, 0x404040, false);
        }
        stack.popPose();
        stack.pushPose();

        var attr = (IFormattableAttribute) inst.getAttribute();
        MutableComponent value = attr.toValueComponent(null, inst.getValue(), TooltipFlag.Default.NORMAL);

        scale = 1;
        if (this.font.width(value) > 27) {
            scale = 27F / this.font.width(value);
            stack.scale(scale, scale, 1);
        }

        int color = 0xFFFFFF;
        if (attr instanceof RangedAttribute) {
            if (inst.getValue() > inst.getBaseValue()) {
                color = 0x55DD55;
            }
            else if (inst.getValue() < inst.getBaseValue()) {
                color = 0xFF6060;
            }
        }
        gfx.drawString(font, value, (int) ((x + 72 + (27 - this.font.width(value) * scale) / 2) / scale), (int) ((y + 7) / scale), color, true);
        stack.popPose();
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (!this.open || !this.isScrollBarActive()) return false;
        this.scrolling = false;
        int left = this.leftPos + 111;
        int top = this.topPos + 15;
        if (pMouseX >= left && pMouseX < left + 12 && pMouseY >= top && pMouseY < top + 155) {
            this.scrolling = true;
            int i = this.topPos + 15;
            int j = i + 138;
            scrollOffset = ((float) pMouseY - i - 7.5F) / (j - i - 15.0F);
            scrollOffset = Mth.clamp(scrollOffset, 0.0F, 1.0F);
            this.startIndex = (int) (scrollOffset * this.getOffScreenRows() + 0.5D);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (!this.open){
            return false;
        }
        if (this.scrolling && this.isScrollBarActive()) {
            int i = this.topPos + 15;
            int j = i + 138;
            scrollOffset = ((float) pMouseY - i - 7.5F) / (j - i - 15.0F);
            scrollOffset = Mth.clamp(scrollOffset, 0.0F, 1.0F);
            this.startIndex = (int) (scrollOffset * this.getOffScreenRows() + 0.5D);
            return true;
        }
        else {
            return false;
        }
    }


    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if (!this.open) return false;
        if (this.isScrollBarActive()) {
            int i = this.getOffScreenRows();
            scrollOffset = (float) (scrollOffset - pDelta / i);
            scrollOffset = Mth.clamp(scrollOffset, 0.0F, 1.0F);
            this.startIndex = (int) (scrollOffset * i + 0.5D);
            return true;
        }
        return false;
    }

    private boolean isScrollBarActive() {
        return this.data.size() > MAX_ENTRIES;
    }

    protected int getOffScreenRows() {
        return Math.max(0, this.data.size() - MAX_ENTRIES);
    }

    @Nullable
    public AttributeInstance getHoveredSlot(int mouseX, int mouseY) {
        for (int i = 0; i < MAX_ENTRIES; i++) {
            if (this.startIndex + i < this.data.size()) {
                if (this.isHovering(8, 14 + ENTRY_HEIGHT * i, 100, ENTRY_HEIGHT, mouseX, mouseY)) return this.data.get(this.startIndex + i);
            }
        }
        return null;
    }

    protected boolean isHovering(int pX, int pY, int pWidth, int pHeight, double pMouseX, double pMouseY) {
        int i = this.leftPos;
        int j = this.topPos;
        pMouseX -= i;
        pMouseY -= j;
        return pMouseX >= pX - 1 && pMouseX < pX + pWidth + 1 && pMouseY >= pY - 1 && pMouseY < pY + pHeight + 1;
    }

    private static DecimalFormat f = ItemStack.ATTRIBUTE_MODIFIER_FORMAT;

    public static String format(int n) {
        int log = (int) StrictMath.log10(n);
        if (log <= 4) return String.valueOf(n);
        if (log == 5) return f.format(n / 1000D) + "K";
        if (log <= 8) return f.format(n / 1000000D) + "M";
        else return f.format(n / 1000000000D) + "B";
    }

    @Override
    public NarrationPriority narrationPriority() {
        return null;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {

    }

    public class HideUnchangedButton extends ImageButton {

        public HideUnchangedButton(int pX, int pY) {
            super(pX, pY, 10, 10, 131, 20, 10, TEXTURES, 256, 256, null, Component.literal("Hide Unchanged Attributes"));
            this.visible = false;
        }

        @Override
        public void onPress() {
            hideUnchanged = !hideUnchanged;
        }

        @Override
        public void renderWidget(GuiGraphics gfx, int pMouseX, int pMouseY, float pPartialTick) {
            int u = 131, v = 20;
            int vOffset = hideUnchanged ? 0 : 10;
            if (this.isHovered) {
                vOffset += 20;
            }

            RenderSystem.enableDepthTest();
            PoseStack pose = gfx.pose();
            pose.pushPose();
            pose.translate(0, 0, 100);
            gfx.blit(TEXTURES, this.getX(), this.getY(), u, v + vOffset, 10, 10, 256, 256);
            pose.popPose();
        }

    }

    @Override
    public void setFocused(boolean pFocused) {}

    @Override
    public boolean isFocused() {
        return false;
    }

    @Override
    public boolean isActive() {
        return false;
    }
}
