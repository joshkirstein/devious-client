package net.runelite.client.plugins.joshplugin.misc;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.joshplugin.mcts.Entity;
import net.runelite.client.plugins.joshplugin.mcts.State;
import net.runelite.client.plugins.joshplugin.pathing.MockedPathingUtils;

import java.util.List;

@Slf4j
public class PrintUtils {

    public static void printMatrix(char[][] out) {
        for (char[] line : out) {
            System.out.println(new String(line));
        }
    }

    public static char[][] mergeHorizontal(List<char[][]> outs) {
        final int PADDING = 3;

        int numOuts = outs.size();
        int length = outs.get(0).length;
        int width = outs.get(0)[0].length;

        char[][] merged = new char[length][numOuts * width + numOuts * PADDING];
        for (int i = 0; i < outs.size(); i++) {
            char[][] out = outs.get(i);
            int widthOffset = i * width + i * PADDING;
            for (int j = 0; j < length; j++) {
                for (int k = 0; k < width; k++) {
                    merged[j][k+widthOffset] = out[j][k];
                }
            }
            for (int j = 0; j < length; j++) {
                for (int k = 0; k < PADDING; k++) {
                    merged[j][k+widthOffset+width] = ' ';
                }
            }
        }
        return merged;
    }

    public static char[][] prettyPrintState(State state) {
        if (!(state.pathingUtils instanceof MockedPathingUtils)) {
            log.info("Can't pretty print game state yet.");
            return new char[][] {{'X'}};
        }

        MockedPathingUtils mputils = (MockedPathingUtils) state.pathingUtils;
        char[][] out = new char[mputils.blocked.length][mputils.blocked[0].length];
        for (int i = 0; i < out.length; i++) {
            for (int j = 0; j < out[i].length; j++) {
                out[i][j] = '.';
                if (mputils.blocked[i][j]) out[i][j] = '*';
            }
        }
        out[state.ply.pos.x][state.ply.pos.y] = 'P';
        for (Entity npc : state.npcs) {
            for (int dsz = 0; dsz < npc.size; dsz++) {
                for (int dd = 0; dd < npc.size; dd++) {
                    out[npc.pos.x + dsz][npc.pos.y + dd] = 'N';
                }
            }
        }
        return out;
    }
}
