package net.runelite.client.plugins.joshplugin.mcts;

public class Entity {
    public Pos pos; // world position, not region position
    public boolean run_enabled = false; // move 1 or 2 tiles per tick
    public int size = 1;

    Entity copy() {
        Entity e = new Entity();
        e.pos = pos.copy();
        e.run_enabled = run_enabled;
        e.size = size;
        return e;
    }
}
