package net.runelite.client.plugins.joshplugin.pathing;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.joshplugin.mcts.Entity;
import net.runelite.client.plugins.joshplugin.mcts.Pos;
import net.runelite.client.plugins.joshplugin.mcts.State;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPathingUtils {

    public static boolean collides(int x1, int y1, int s1, int x2, int y2, int s2) {
        return !(x1 > (x2 + s2 - 1) || (x1 + s1 - 1) < x2 || (y1 - s1 + 1) > y2 || y1 < (y2 - s2 + 1));
    }

    public boolean isBlocked(WorldPoint p, State state) {
        if (p.getWorldX() == state.ply.pos.x && p.getWorldY() == state.ply.pos.y) {
            return true;
        }
        for (Entity npc : state.npcs) {
            for (int dx = 0; dx < npc.size; dx++) {
                for (int dy = 0; dy < npc.size; dy++) {
                    if ((npc.pos.x + dx) == p.getWorldX() && (npc.pos.y + dy) == p.getWorldY()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Pos findNextPos(int playerX, int playerY, int npcX, int npcY, int npcSize, State state) {
        int dx = Integer.signum(playerX - npcX);
        int dy = Integer.signum(playerY - npcY);
        if (collides(npcX, npcY, npcSize, playerX, playerY, 1)) {
            // npc directly under, moves to random location
            dx = 0; dy = 0; // don't display anything for now since it's random where npc goes
        } else if (collides(npcX + dx, npcY + dy, npcSize, playerX, playerY, 1)) {
            // next position will put npc directly under player, therefore only move east/west (i.e., nextX)
            // as npc always tries to align on east/west (which allows safespotting)
            dy = 0;
        }

        if (dx == 0 && dy == 0) {
            // no change in pos
            return new Pos(npcX, npcY);
        }

        boolean canMoveDx = canMoveDx(npcX, npcY, npcSize, dx, dy, state);
        boolean canMoveDy = canMoveDy(npcX, npcY, npcSize, dx, dy, state);
        boolean both = canMoveDx && canMoveDy;
        if (!both) {
            canMoveDx = canMoveDx(npcX, npcY, npcSize, dx, 0, state);
            canMoveDy = canMoveDy(npcX, npcY, npcSize, 0, dy, state);
        }

        int nextX = npcX, nextY = npcY;
        if (both) {
            // next tile
            nextX += dx;
            nextY += dy;
        } else if (canMoveDx) {
            nextX += dx;
        } else if (canMoveDy) {
            nextY += dy;
        }

        return new Pos(nextX, nextY);
    }

    public boolean canMoveDx(int futureX, int futureY, int sz, int dx, int dy, State state) {
        // TODO: replace with canTravelInDirection?
        WorldPoint npcPos = new WorldPoint(futureX, futureY, 0 /* TODO will this be a problem ? */);
        List<WorldPoint> points = new ArrayList<>();

        int start = (dy == -1) ? -1 : 0; // if we are moving down next tick
        int end = (dy == 1) ? sz + 1 : sz;
        if (dx > 0) {
            // Move right
            for (int i = start; i < end; i++) {
                points.add(npcPos.dx(sz).dy(i));
            }
        } else if (dx < 0) {
            // Move left
            for (int i = start; i < end; i++) {
                points.add(npcPos.dx(-1).dy(i));
            }
        }

        boolean canMove = true;
        for (WorldPoint p : points) {
            if (isBlocked(p, state)) {
                canMove = false;
            }
        }
        return canMove;
    }

    public boolean canMoveDy(int futureX, int futureY, int sz, int dx, int dy, State state) {
        // TODO: replace with canTravelInDirection?
        WorldPoint npcPos = new WorldPoint(futureX, futureY, 0 /* TODO will this be a problem ? */);
        List<WorldPoint> points = new ArrayList<>();

        int start = (dx == -1) ? -1 : 0; // if we are moving left next tick
        int end = (dx == 1) ? sz + 1 : sz;
        if (dy > 0) {
            // Move up
            for (int i = start; i < end; i++) {
                points.add(npcPos.dx(i).dy(sz));
            }
        } else if (dy < 0) {
            // Move down
            for (int i = start; i < end; i++) {
                points.add(npcPos.dx(i).dy(-1));
            }
        }

        boolean canMove = true;
        for (WorldPoint p : points) {
            if (isBlocked(p, state)) {
                canMove = false;
            }
        }
        return canMove;
    }
}
