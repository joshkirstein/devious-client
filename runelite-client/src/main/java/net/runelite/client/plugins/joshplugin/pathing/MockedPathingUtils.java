package net.runelite.client.plugins.joshplugin.pathing;

import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.joshplugin.mcts.Entity;
import net.runelite.client.plugins.joshplugin.mcts.State;
import net.unethicalite.api.movement.Reachable;

public class MockedPathingUtils extends AbstractPathingUtils {

    public boolean[][] blocked;

    public MockedPathingUtils(boolean[][] blocked) {
        this.blocked = blocked;
    }

    @Override
    public boolean isBlocked(WorldPoint p, State state) {
        if (super.isBlocked(p, state)) {
            return true;
        }
        return p.getWorldX() >= blocked.length || p.getWorldX() < 0 || p.getWorldY() >= blocked[0].length || p.getWorldY() < 0 || blocked[p.getWorldX()][p.getWorldY()];
    }
}
