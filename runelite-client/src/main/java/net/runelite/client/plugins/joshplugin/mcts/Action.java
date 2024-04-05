package net.runelite.client.plugins.joshplugin.mcts;

import net.unethicalite.api.commons.Rand;

import java.util.Random;

public enum Action {
    NOTHING,
    TOGGLE_RUN,
    MOVE_NORTH,
    MOVE_SOUTH,
    MOVE_EAST,
    MOVE_WEST,
    MOVE_NORTH_EAST,
    MOVE_SOUTH_EAST,
    MOVE_NORTH_WEST,
    MOVE_SOUTH_WEST;

    private static final Random RANDOM = new Random();

    static Action getRandom() {
        int idx = RANDOM.nextInt(Action.values().length);
        return Action.values()[idx];
    }
}