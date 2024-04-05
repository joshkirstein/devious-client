package net.runelite.client.plugins.joshplugin;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.joshplugin.mcts.StateUtils;
import net.runelite.client.plugins.joshplugin.pathing.RSPathingUtils;
import net.runelite.client.ui.overlay.OverlayManager;
import net.unethicalite.api.plugins.LoopedPlugin;

import javax.inject.Inject;

@PluginDescriptor(
        name = "Josh Plugin",
        description = "Hello world",
        tags = {"josh", "hey", "there"},
        enabledByDefault = false
)
@Slf4j
public class JoshPlugin extends LoopedPlugin {

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private JoshOverlay joshOverlay;

    public JoshPlugin() {
    }

    @Override
    protected void startUp() throws Exception
    {
        overlayManager.add(joshOverlay);
        log.info("Startup!");
    }

    @Override
    protected void shutDown() throws Exception
    {
        overlayManager.remove(joshOverlay);
        log.info("Shutdown!");
    }

    @Subscribe
    public void onGameTick(GameTick gameTick)
    {

    }

    @Override
    protected int loop() {
        return 0;
    }
}
