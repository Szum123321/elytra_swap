package net.szum123321.elytra_swap.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface GameReadyCallback {
    /**
     * Called when all mods should be loaded.
     */
    Event<GameReadyCallback> EVENT = EventFactory.createArrayBacked(GameReadyCallback.class,
            (listeners) -> (gameInstance) -> {
                for(GameReadyCallback listener : listeners ) {
                    listener.ready(gameInstance);
                }
            });

    void ready(Object gameInstance);
}

