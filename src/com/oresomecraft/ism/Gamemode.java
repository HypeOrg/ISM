package com.oresomecraft.ism;

import com.oresomecraft.ism.maps.ism.*;

public enum Gamemode {
    NONE,
    MOB_SLAUGHTER_SKYFALL,
    FREEFALL,
    TROUBLED_BRIDGES,
    SUPER_LUNGE,
    TOP_SNIPE,
    BOMB_DROP;

    public static void startRoundAccordingToType(Gamemode mode) {
        if (mode == MOB_SLAUGHTER_SKYFALL) {
            new MobSlaughterSkyfall(Storage.currentRound);
        }
        if (mode == FREEFALL) {
            new FreeFall(Storage.currentRound);
        }
        if (mode == TROUBLED_BRIDGES) {
            new TroubledBridges(Storage.currentRound);
        }
        if (mode == TOP_SNIPE) {
            new TopSnipe(Storage.currentRound);
        }
        if (mode == SUPER_LUNGE) {
            new SuperLunge(Storage.currentRound);
        }
        if (mode == BOMB_DROP) {
            new BombDrop(Storage.currentRound);
        }
    }
}
