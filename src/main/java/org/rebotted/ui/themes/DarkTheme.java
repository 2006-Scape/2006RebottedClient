package org.rebotted.ui.themes;


import org.pushingpixels.substance.api.*;
import org.pushingpixels.substance.api.painter.border.ClassicBorderPainter;
import org.pushingpixels.substance.api.painter.decoration.FlatDecorationPainter;
import org.pushingpixels.substance.api.painter.fill.FractionBasedFillPainter;
import org.pushingpixels.substance.api.painter.highlight.FractionBasedHighlightPainter;
import org.pushingpixels.substance.api.shaper.ClassicButtonShaper;

import java.io.File;
import java.net.URL;

public class DarkTheme extends SubstanceSkin {
    private SubstanceColorScheme selected_disabled, selected, disabled, enabled, background, aqua, border, separator, tabHighlight;
    private SubstanceColorSchemeBundle bundle;
    private ComponentState[] states, states2;
    private ColorSchemeAssociationKind associationKind;
    private DecorationAreaType[] decorationAreaTypes;
    private ColorSchemeSingleColorQuery[] colorSchemeSingleColorQueries;

    public DarkTheme() {
        ColorSchemes schemes = SubstanceSkin.getColorSchemes(getClass().getResource("/rebotted.colorschemes"));
        selected_disabled = schemes.get("Rebotted Selected Disabled");
        selected = schemes.get("Rebotted Selected");
        disabled = schemes.get("Rebotted Disabled");
        enabled = schemes.get("Rebotted Enabled");
        background = schemes.get("Rebotted Background");
        bundle = new SubstanceColorSchemeBundle(enabled, enabled, disabled);
        aqua = schemes.get("Rebotted Dark");

        states = new ComponentState[1];

        states[0] = ComponentState.ROLLOVER_UNSELECTED;
        bundle.registerHighlightColorScheme(aqua, 0.75F, states);
        states = new ComponentState[1];
        states[0] = ComponentState.SELECTED;
        bundle.registerHighlightColorScheme(aqua, 0.9F, states);
        states = new ComponentState[1];
        states[0] = ComponentState.ROLLOVER_SELECTED;
        bundle.registerHighlightColorScheme(aqua, 1.0F, states);
        states = new ComponentState[2];
        states[0] = ComponentState.ARMED;
        states[1] = ComponentState.ROLLOVER_ARMED;
        bundle.registerHighlightColorScheme(aqua, 1.0F, states);
        associationKind = ColorSchemeAssociationKind.BORDER;
        states = new ComponentState[3];
        states[0] = ComponentState.ROLLOVER_ARMED;
        states[1] = ComponentState.ROLLOVER_SELECTED;
        states[2] = ComponentState.ROLLOVER_UNSELECTED;
        bundle.registerColorScheme(aqua, associationKind, states);
        associationKind = ColorSchemeAssociationKind.FILL;
        states = new ComponentState[2];
        states[0] = ComponentState.SELECTED;
        states[1] = ComponentState.ROLLOVER_SELECTED;
        bundle.registerColorScheme(aqua, associationKind, states);

        border = schemes.get("Rebotted Border");
        separator = schemes.get("Rebotted Separator");
        bundle.registerColorScheme(aqua, ColorSchemeAssociationKind.HIGHLIGHT_BORDER, ComponentState.getActiveStates());
        associationKind = ColorSchemeAssociationKind.BORDER;
        states = new ComponentState[0];
        bundle.registerColorScheme(border, associationKind, states);
        associationKind = ColorSchemeAssociationKind.SEPARATOR;
        states = new ComponentState[0];
        bundle.registerColorScheme(separator, associationKind, states);
        associationKind = ColorSchemeAssociationKind.MARK;
        states = new ComponentState[0];
        bundle.registerColorScheme(border, associationKind, states);
        associationKind = ColorSchemeAssociationKind.TEXT_HIGHLIGHT;
        states = new ComponentState[2];
        states[0] = ComponentState.SELECTED;
        states[1] = ComponentState.ROLLOVER_SELECTED;
        bundle.registerColorScheme(aqua, associationKind, states);
        states2 = new ComponentState[2];
        states2[0] = ComponentState.ARMED;
        states2[1] = ComponentState.ROLLOVER_ARMED;
        bundle.registerColorScheme(aqua, states2);
        states = new ComponentState[1];
        states[0] = ComponentState.DISABLED_UNSELECTED;
        bundle.registerColorScheme(disabled, 0.5F, states);
        states = new ComponentState[1];
        states[0] = ComponentState.DISABLED_SELECTED;
        bundle.registerColorScheme(selected_disabled, 0.5F, states);
        states2 = new ComponentState[1];
        states2[0] = ComponentState.ROLLOVER_SELECTED;
        bundle.registerColorScheme(aqua, states2);
        states2 = new ComponentState[1];
        states2[0] = ComponentState.SELECTED;
        bundle.registerColorScheme(selected, states2);
        tabHighlight = schemes.get("Rebotted Tab Highlight");
        associationKind = ColorSchemeAssociationKind.TAB;
        states = new ComponentState[1];
        states[0] = ComponentState.ROLLOVER_SELECTED;
        bundle.registerColorScheme(tabHighlight, associationKind, states);
        decorationAreaTypes = new DecorationAreaType[1];
        decorationAreaTypes[0] = DecorationAreaType.NONE;
        this.registerDecorationAreaSchemeBundle(bundle, background, decorationAreaTypes);
        this.setSelectedTabFadeStart(0.15D);
        this.setSelectedTabFadeEnd(0.25D);
        this.buttonShaper = new ClassicButtonShaper();
        this.watermark = null;
        float[] floats = new float[3];
        floats[0] = 0.0F;
        floats[1] = 0.5F;
        floats[2] = 1.0F;
        colorSchemeSingleColorQueries = new ColorSchemeSingleColorQuery[3];
        colorSchemeSingleColorQueries[0] = ColorSchemeSingleColorQuery.LIGHT;
        colorSchemeSingleColorQueries[1] = ColorSchemeSingleColorQuery.MID;
        colorSchemeSingleColorQueries[2] = ColorSchemeSingleColorQuery.MID;
        this.fillPainter = new FractionBasedFillPainter("Rebotted", floats, colorSchemeSingleColorQueries);
        this.decorationPainter = new FlatDecorationPainter();
        floats = new float[3];
        floats[0] = 0.0F;
        floats[1] = 0.5F;
        floats[2] = 1.0F;
        colorSchemeSingleColorQueries = new ColorSchemeSingleColorQuery[3];
        colorSchemeSingleColorQueries[0] = ColorSchemeSingleColorQuery.EXTRALIGHT;
        colorSchemeSingleColorQueries[1] = ColorSchemeSingleColorQuery.LIGHT;
        colorSchemeSingleColorQueries[2] = ColorSchemeSingleColorQuery.MID;
        this.highlightPainter = new FractionBasedHighlightPainter("Rebotted", floats, colorSchemeSingleColorQueries);
        this.borderPainter = new ClassicBorderPainter();
        this.highlightBorderPainter = new ClassicBorderPainter();
    }

    @Override
    public String getDisplayName() {
        return "Rebotted";
    }

    private URL getColorSchemes() {
        try {
            final File scheme = new File("C:\\Users\\Ethan\\IdeaProjects\\Rebotted\\src\\main\\resources\\Rebotted.colorschemes");
            return scheme.toURI().toURL();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
