package net.runelite.client.plugins.joshplugin.mcts;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class TreeNode {
    private static final Random RANDOM = new Random();

    public TreeNode(State game) {
        this.game = game;
    }

    // Tree structure
    public TreeNode parent = null; // which parent we came from
    public Action action = null; // which action got us here
    public Map<Action, TreeNode> children = new HashMap<>(); // list of future actions

    // Monte carlo data
    public long rewards = 0; // total wins we've seen so far
    public long visits = 0; // total number of times this node has been visited

    // Game state
    public State game; // current state of the game for this node

    // get upper confidence bound score
    public double get_score() {
        if (visits == 0) return Double.MAX_VALUE;
        TreeNode prev = this;
        if (parent != null) {
            prev = parent;
        }
        return (1.0 * rewards / visits) + Math.sqrt(2) * Math.sqrt(Math.log(prev.visits) / visits);
    }

    // Generate children tree nodes
    public void init_children() {
        if (game.done) return;

        // TODO: get *VALID* actions based on current state
        for (Action act : Action.values()) {
            TreeNode child = new TreeNode(game.copy());
            child.parent = this;
            child.action = act;
            child.game.step(child.action);

            children.put(act, child);
        }
    }

    // tree search algo: from current node, find most probable leaf and then
    // choose a random move and run a rollout
    public void explore() {
        // Keep going until we find an unexplored leaf
        TreeNode cur = this;
        while (cur.children.size() != 0) {
            // Randomly pick a child which has maximum score
            double maxScore = -Double.MAX_VALUE;
            for (Map.Entry<Action, TreeNode> child : cur.children.entrySet()) {
                maxScore = Math.max(maxScore, child.getValue().get_score());
            }

            List<TreeNode> options = new ArrayList<>();
            for (TreeNode child : cur.children.values()) {
                if (Math.abs(child.get_score() - maxScore) <= 0.000001) {
                    options.add(child);
                }
            }

            int opts = options.size();
            int idx = RANDOM.nextInt(opts);
            cur = options.get(idx);
        }

        // "Visit" the node and run a rollout
        long improvement = 0;
        if (cur.visits < 1) {
            // Just play random moves on first visit
            improvement = cur.random_rollout();
        } else {
            // Otherwise create tree node children, choose one
            // and then run a rollout from that.
            cur.init_children();
            int sz = cur.children.size();
            if (sz != 0) {
                int idx = RANDOM.nextInt(sz);
                for (TreeNode next : cur.children.values()) {
                    if (idx <= 0) {
                        cur = next;
                        break;
                    }
                    idx--;
                }
            }

            // If we chose a new action, we run a rollout there. Otherwise,
            // run the rollout locally. Technically this can work even if action wasn't
            // chosen (for example, if you are frozen and nothing to do). However, in future
            // we'll likely always have a "DO_NOTHING" action to combat that. Hmph.
            improvement = cur.random_rollout();
        }
        cur.visits++;

        // "Backpropagate" and add up the childs new-found reward
        // up through its parents
        TreeNode parent = cur;
        parent.rewards += improvement;
        while (parent.parent != null) {
            parent = parent.parent;
            parent.visits++;
            parent.rewards += improvement;
        }
    }

    // play randomly until a terminal state (i.e., state.done) and return result
    public long random_rollout() {
        if (game.done) return game.result;

        int reward = 0;
        boolean done = false;
        State rollout_game = game.copy();
        while (!done) {
            Action act = Action.getRandom();
            rollout_game.step(act);
            reward += rollout_game.result;
            done = rollout_game.done;
        }

        return reward;
    }

    public TreeNode next() {
        if (game.done) {
            log.info("Game is over!");
            return null;
        }

        if (children.size() == 0) {
            log.info("No children!");
            return null;
        }

        long maxVisit = Long.MIN_VALUE;
        for (TreeNode child : children.values()) {
            maxVisit = Math.max(maxVisit, child.visits);
        }

        List<TreeNode> options = new ArrayList<>();
        for (TreeNode child : children.values()) {
            if (child.visits == maxVisit) {
                options.add(child);
            }
        }

        return options.get(RANDOM.nextInt(options.size()));
    }

    public String toString() {
        return "[Reward: " + rewards + " Visits: " + visits + " Action: " + action + "]";
    }
}