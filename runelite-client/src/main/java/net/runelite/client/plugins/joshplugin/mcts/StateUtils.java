package net.runelite.client.plugins.joshplugin.mcts;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.client.plugins.joshplugin.pathing.MockedPathingUtils;
import net.runelite.client.plugins.joshplugin.pathing.RSPathingUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class StateUtils {
    // Copies current live game data into a game state so we can use it for
    // MTCS/alpha beta pruning
    public static State getLiveGamestate(Client client) {
        State gameState = new State();
        gameState.ply = new Entity();
        gameState.ply.pos = new Pos(client.getLocalPlayer().getWorldLocation());
        List<NPC> npcs = client.getNpcs();
        gameState.npcs = new Entity[npcs.size()];
        int i = 0;
        for (NPC n : client.getNpcs()) {
            gameState.npcs[i] = new Entity();
            gameState.npcs[i].pos = new Pos(n.getWorldLocation());
        }
        gameState.pathingUtils = new RSPathingUtils(client);
        return gameState;
    }

    public static State getFakeGamestate() {
        // **********
        // *********
        // **********
        // **......**
        // N*......**
        // ***...****
        // ***P******
        // **********
        // **********
        // ******N***
        boolean[][] blocked = new boolean[50][50];
        for (int i = 2; i < 8; i++) {
            blocked[3][i] = true;
            blocked[4][i] = true;
        }
        blocked[5][4] = true;
        blocked[5][5] = true;
        MockedPathingUtils mputils = new MockedPathingUtils(blocked);

        State st = new State();
        st.ply = new Entity();
        st.ply.pos = new Pos(6, 3);
        st.ply.run_enabled = false;
        st.npcs = new Entity[2];
        st.npcs[0] = new Entity();
        st.npcs[0].pos = new Pos(9, 5);
        st.npcs[1] = new Entity();
        st.npcs[1].pos = new Pos(3, 0);
        st.pathingUtils = mputils;
        return st;
    }

    public static State fromFile(String filename) {
        File f = new File(filename);
        Map<String, List<String>> sectionMap = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            List<String> lines = new ArrayList<>();
            String str;
            String curSection = null;
            while ((str = br.readLine()) != null) {
                if (str.startsWith("=")) {
                    curSection = str;
                } else {
                    sectionMap.putIfAbsent(curSection, new ArrayList<>());
                    sectionMap.get(curSection).add(str);
                }
            }
            System.out.println(sectionMap);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Read map section
        List<String> map = sectionMap.get("=map");
        if (map.size() == 0) {
            throw new RuntimeException("Map section has no contents.");
        }

        int height = map.size();
        int width = map.get(0).length();
        State st = new State();
        st.ply = new Entity();

        List<Entity> npcs = new ArrayList<>();
        boolean[][] blocked = new boolean[height][width];
        boolean[][] floodfill = new boolean[height][width];

        for (int i = 0; i < height; i++) {
            String str = map.get(i);
            for (int j = 0; j < width; j++) {
                char ch = str.charAt(j);
                if (ch == '*') {
                    blocked[i][j] = true;
                } else if (ch == 'P') {
                    st.ply.pos = new Pos(i, j);
                } else if (ch == '.') {
                    // Empty spot, ignore
                } else if (ch == 'N') {
                    if (floodfill[i][j]) continue;

                    List<Pos> npcPos = new ArrayList<>();
                    depth(i, j, map, npcPos, floodfill);

                    // NPC
                    Entity npc = new Entity();
                    // since we iterate 0->N we always get the southwest tile first as these npcs are squares.
                    npc.pos = new Pos(i, j);
                    npc.size = (int)Math.sqrt(npcPos.size());
                    log.info("NPC: " + i + "," + j + " SZ: " + npc.size);
                    npcs.add(npc);

                    if (npc.size * npc.size != npcPos.size()) throw new RuntimeException("NPC does not square at pos: " + i + "," + j);
                }
            }
        }

        st.npcs = npcs.toArray(new Entity[0]);
        st.pathingUtils = new MockedPathingUtils(blocked);

        return st;
    }

    private static void depth(int i, int j, List<String> map, List<Pos> npcPos, boolean[][] floodfill) {
        if (i < 0 || j < 0 || i >= map.size() || j >= map.get(0).length()) return;
        if (floodfill[i][j]) return;

        floodfill[i][j] = true;
        npcPos.add(new Pos(i, j));
        for (int dx : new int[]{-1, 0, 1}) {
            for (int dy : new int[]{-1, 0, 1}) {
                if (Math.abs(dx)+Math.abs(dy) != 1) continue; // only N,S,E,W

                int x = i+dx;
                int y = j+dy;

                if (x < 0 || y < 0 || x >= map.size() || j >= map.get(0).length()) continue;
                if (map.get(x).charAt(y) != 'N') continue; // not an npc tile

                depth(x, y, map, npcPos, floodfill);
            }
        }
    }
}
