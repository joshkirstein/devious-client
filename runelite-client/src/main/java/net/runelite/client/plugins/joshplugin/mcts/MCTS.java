package net.runelite.client.plugins.joshplugin.mcts;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Constants;

import java.util.ArrayList;
import java.util.List;

import static net.runelite.client.plugins.joshplugin.misc.PrintUtils.mergeHorizontal;
import static net.runelite.client.plugins.joshplugin.misc.PrintUtils.printMatrix;
import static net.runelite.client.plugins.joshplugin.mcts.StateUtils.getFakeGamestate;
import static net.runelite.client.plugins.joshplugin.misc.PrintUtils.prettyPrintState;

@Slf4j
public class MCTS {
    public void simulate() {
        final int NUM_ROLLOUTS = 50000;

        log.info("Simulating!");
        TreeNode root = new TreeNode(getFakeGamestate());
        long ms = System.currentTimeMillis();
        for (int i = 0; i < NUM_ROLLOUTS; i++) {
            root.explore();
        }
        long done = System.currentTimeMillis();

        State state = getFakeGamestate();
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
        log.info("Took " + (done - ms) + "ms to run " + NUM_ROLLOUTS + " rollouts.");
    }
}
