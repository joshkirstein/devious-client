package net.runelite.client.plugins.joshplugin;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.client.plugins.joshplugin.pathing.RSPathingUtils;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;

@Singleton
@Slf4j
public class JoshOverlay extends Overlay {
    private static final Font FONT = FontManager.getRunescapeFont().deriveFont(Font.BOLD, 16);
    private static final Color RED = new Color(221, 44, 0);
    private static final Color GREEN = new Color(0, 200, 83);
    private static final Color ORANGE = new Color(255, 109, 0);
    private static final Color CYAN = new Color(0, 184, 212);
    private static final Color BLUE = new Color(41, 98, 255);
    private static final Color DEEP_PURPLE = new Color(98, 0, 234);

    private final Client client;
    private final JoshPlugin plugin;

    @Inject
    private JoshOverlay(Client client, JoshPlugin plugin) {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPriority(PRIORITY_HIGHEST);
        this.client = client;
        this.plugin = plugin;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
//        Player localPlayer = client.getLocalPlayer();
//        List<NPC> npcs = client.getNpcs();
//        for (NPC npc : npcs) {
//            long ms = System.currentTimeMillis();
//            utils.findAndDrawPathToPlayer(npc, localPlayer, graphics);
//            // System.out.println("Took: " + (System.currentTimeMillis() - ms) + " ms.");
//        }

        return null;
    }
}
