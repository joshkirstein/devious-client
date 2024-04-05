package net.runelite.client.plugins.joshplugin.mcts;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.joshplugin.pathing.AbstractPathingUtils;

import static net.runelite.client.plugins.joshplugin.misc.PrintUtils.prettyPrintState;
import static net.runelite.client.plugins.joshplugin.misc.PrintUtils.printMatrix;

@Slf4j
public class State {
    public Entity ply;
    public Entity[] npcs;
    public boolean done = false; // whether this is a terminal state, if so consult 'result' for the reward
    public long result = 0; // +1 == win
    public AbstractPathingUtils pathingUtils;

    public void step(Action action) {
        // TODO: do we need to check end condition at before AND after npc move?
        boolean moved = false;
        for (Entity npc : npcs) {
            // We are done if all NPCs don't move OR npc attacks player
            WorldPoint pt = new WorldPoint(ply.pos.x, ply.pos.y, 0);
            WorldArea area = new WorldArea(npc.pos.x, npc.pos.y, npc.size, npc.size, 0);
            if (area.distanceTo(pt) <= 1) {
//                log.info("Done due to distance");
                done = true;
                result = 0;
                return;
            }

            // Currently we are only simulating movement, eventually will be attacks as well.
            Pos next = pathingUtils.findNextPos(ply.pos.x, ply.pos.y, npc.pos.x, npc.pos.y, npc.size, this);
            moved |= !next.equals(npc.pos);
            npc.pos = next;
        }

        if (!moved) {
//            log.info("No movement!");
            done = true;
            result = +100; // we lived!
            return;
        }

        // player action after NPC. this way npcs move toward previous position.
        // can always reverse this if it's not true in prod.
        WorldPoint next;
        int delta = ply.run_enabled ? 2 : 1;
        switch (action) {
            case TOGGLE_RUN:
                ply.run_enabled = true;
                break;
            case MOVE_NORTH:
                // TODO: handle run
                next = new WorldPoint(ply.pos.x, ply.pos.y + delta, 0);
                if (!pathingUtils.isBlocked(next, this)) {
                    ply.pos = new Pos(next);
                }
                break;
            case MOVE_SOUTH:
                next = new WorldPoint(ply.pos.x, ply.pos.y - delta, 0);
                if (!pathingUtils.isBlocked(next, this)) {
                    ply.pos = new Pos(next);
                }
                break;
            case MOVE_EAST:
                next = new WorldPoint(ply.pos.x + delta, ply.pos.y, 0);
                if (!pathingUtils.isBlocked(next, this)) {
                    ply.pos = new Pos(next);
                }
                break;
            case MOVE_WEST:
                next = new WorldPoint(ply.pos.x - delta, ply.pos.y, 0);
                if (!pathingUtils.isBlocked(next, this)) {
                    ply.pos = new Pos(next);
                }
                break;
            case MOVE_NORTH_WEST:
                next = new WorldPoint(ply.pos.x - delta, ply.pos.y + delta, 0);
                if (!pathingUtils.isBlocked(next, this)) {
                    ply.pos = new Pos(next);
                }
                break;
            case MOVE_NORTH_EAST:
                next = new WorldPoint(ply.pos.x + delta, ply.pos.y + delta, 0);
                if (!pathingUtils.isBlocked(next, this)) {
                    ply.pos = new Pos(next);
                }
                break;
            case MOVE_SOUTH_EAST:
                next = new WorldPoint(ply.pos.x + delta, ply.pos.y - delta, 0);
                if (!pathingUtils.isBlocked(next, this)) {
                    ply.pos = new Pos(next);
                }
                break;
            case MOVE_SOUTH_WEST:
                next = new WorldPoint(ply.pos.x - delta, ply.pos.y - delta, 0);
                if (!pathingUtils.isBlocked(next, this)) {
                    ply.pos = new Pos(next);
                }
                break;
            case NOTHING:
                // noop
                break;
            default:
                log.info("Unhandled action: " + action);
                break;
        }

        // TODO: Check distance of npcs after step as well
    }

    State copy() {
        State st = new State();
        st.ply = ply.copy();
        st.npcs = new Entity[npcs.length];
        for (int i = 0; i < npcs.length; i++) {
            st.npcs[i] = npcs[i].copy();
        }
        st.pathingUtils = pathingUtils;
        st.done = done;
//        st.result = result;
        return st;
    }

    public String toString() {
        return "State[DONE: " + this.done + " RESULT: " + this.result + "]";
    }
}
