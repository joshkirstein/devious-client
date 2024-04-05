package net.runelite.client.plugins.joshplugin.testing;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.joshplugin.mcts.Action;
import net.runelite.client.plugins.joshplugin.mcts.State;
import net.runelite.client.plugins.joshplugin.mcts.StateUtils;
import net.runelite.client.plugins.joshplugin.mcts.TreeNode;

import java.util.ArrayList;
import java.util.List;

import static net.runelite.client.plugins.joshplugin.misc.PrintUtils.*;

@Slf4j
public class SafespotSimulation {

    private static final int DEFAULT_NUM_ROLLOUTS = 10000;

    public static void main(String[] args) throws Exception {
//        simulate_easy_small_npc();
        simulate_easy_large_npc();
//        manual_wall();
//        manual_medium();
    }

    public static void manual_wall() {
        State st = StateUtils.fromFile("C:\\Users\\manbe\\Desktop\\devious-client\\runelite-client\\src\\main\\java\\net\\runelite\\client\\plugins\\joshplugin\\scenarios\\test_wall");
        System.out.println(st);
        printMatrix(prettyPrintState(st));
        System.out.println();
        st.step(Action.MOVE_SOUTH);
        System.out.println(st);
        printMatrix(prettyPrintState(st));
        System.out.println();
        st.step(Action.MOVE_SOUTH);
        System.out.println(st);
        printMatrix(prettyPrintState(st));
        System.out.println();
        st.step(Action.MOVE_SOUTH);
        System.out.println(st);
        printMatrix(prettyPrintState(st));
        System.out.println();
    }

    public static void manual_medium() {
        State st = StateUtils.fromFile("C:\\Users\\manbe\\Desktop\\devious-client\\runelite-client\\src\\main\\java\\net\\runelite\\client\\plugins\\joshplugin\\scenarios\\easy_large_npc");
        System.out.println(st);
        printMatrix(prettyPrintState(st));
        System.out.println();
        st.step(Action.TOGGLE_RUN);
        System.out.println(st);
        printMatrix(prettyPrintState(st));
        System.out.println();
        st.step(Action.MOVE_SOUTH_WEST);
        System.out.println(st);
        printMatrix(prettyPrintState(st));
        System.out.println();
        st.step(Action.MOVE_WEST);
        System.out.println(st);
        printMatrix(prettyPrintState(st));
        System.out.println();
        st.step(Action.MOVE_WEST);
        System.out.println(st);
        printMatrix(prettyPrintState(st));
        System.out.println();
    }

    public static void simulate_easy_small_npc() {
        log.info("Starting easy simulation.");
        State st = StateUtils.fromFile("C:\\Users\\manbe\\Desktop\\devious-client\\runelite-client\\src\\main\\java\\net\\runelite\\client\\plugins\\joshplugin\\scenarios\\easy_small_npc");
        System.out.println(st);
        printMatrix(prettyPrintState(st));
    }

    public static void simulate_easy_large_npc() {
        State st = StateUtils.fromFile("C:\\Users\\manbe\\Desktop\\devious-client\\runelite-client\\src\\main\\java\\net\\runelite\\client\\plugins\\joshplugin\\scenarios\\easy_large_npc");

        log.info("Starting easy_large_npc simulation.");
        TreeNode root = new TreeNode(st);
        long ms = System.currentTimeMillis();
        for (int i = 0; i < DEFAULT_NUM_ROLLOUTS; i++) {
            root.explore();
        }
        long done = System.currentTimeMillis();
        log.info("Took " + (done-ms) + "ms to finish "  + DEFAULT_NUM_ROLLOUTS + " rollouts.");

        State state = st;
        List<char[][]> outs = new ArrayList<>();
        outs.add(prettyPrintState(state));
        TreeNode cur = root;
        for (int i = 0; i < 100; i++) {
            TreeNode bestMove = cur.next();
            if (bestMove == null) break;
            Action act = bestMove.action;
            System.out.println("ACTING: " + act + " VALUE: " + bestMove.get_score() + " VISITS: " + bestMove.visits);
            state.step(act);
            outs.add(prettyPrintState(state));
            cur = bestMove;
        }

        printMatrix(mergeHorizontal(outs));
    }
}

