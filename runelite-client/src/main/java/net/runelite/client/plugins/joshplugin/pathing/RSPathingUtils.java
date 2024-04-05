package net.runelite.client.plugins.joshplugin.pathing;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.joshplugin.mcts.*;
import net.unethicalite.api.movement.Reachable;

import java.util.*;
import java.util.List;

@Slf4j
public class RSPathingUtils extends AbstractPathingUtils {
    private final Client client;

    // TODO: make pathing utils instanced to support custom environments

    public RSPathingUtils(Client client) {
        this.client = client;
    }

    @Override
    public boolean isBlocked(WorldPoint p, State state) {
        if (super.isBlocked(p, state)) {
            return true;
        }
        // TODO: make sure this doesn't include existing NPCs / Players ...
        return Reachable.isObstacle(p);
    }
}
