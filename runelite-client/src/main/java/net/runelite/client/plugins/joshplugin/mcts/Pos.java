package net.runelite.client.plugins.joshplugin.mcts;

import net.runelite.api.coords.WorldPoint;

public class Pos {
    public int x, y;

    public Pos(int x, int y) { this.x = x; this.y = y; }
    public Pos(WorldPoint point) { this.x = point.getWorldX(); this.y = point.getWorldY(); }

    Pos copy() {
        return new Pos(x, y);
    }

    public boolean equals(Object other) {
        if (!(other instanceof Pos)) return false;
        Pos oth = (Pos) other;
        return x == oth.x && y == oth.y;
    }

    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
