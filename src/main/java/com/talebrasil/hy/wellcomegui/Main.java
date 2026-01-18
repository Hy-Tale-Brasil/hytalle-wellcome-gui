package com.talebrasil.hy.wellcomegui;

import com.talebrasil.hy.wellcomegui.command.WellcomeCommand;
import com.talebrasil.hy.wellcomegui.gui.WelcomePage;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Main extends JavaPlugin {

    private static Main INSTANCE;
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();


    public static Main getInstance() {
        return INSTANCE;
    }

    public Main(@Nonnull JavaPluginInit init) {
        super(init);
        INSTANCE = this;
    }

    @Override
    protected void setup() {

        super.setup();
        this.getCommandRegistry().registerCommand(new WellcomeCommand());

        getEventRegistry().registerGlobal(AddPlayerToWorldEvent.class, event -> {
            Holder<EntityStore> holder = event.getHolder();
            if (holder != null) {
                UUIDComponent uuidComp = (UUIDComponent) holder.getComponent(UUIDComponent.getComponentType());
                if (uuidComp != null) {
                    PlayerRef playerRef = Universe.get().getPlayer(uuidComp.getUuid());
                    if (playerRef != null) {
                        LOGGER.atInfo().log("Jogador detectado: " + playerRef.getUsername());

                        CompletableFuture.delayedExecutor(2, TimeUnit.SECONDS).execute(() -> {
                            try {
                                Ref<EntityStore> ref = playerRef.getReference();
                                if (ref != null && ref.isValid()) {
                                    Store<EntityStore> store = ref.getStore();
                                    World world = ((EntityStore) store.getExternalData()).getWorld();

                                    if (world != null) {
                                        world.execute(() -> {
                                            try {
                                                Player player = (Player) store.getComponent(ref, Player.getComponentType());
                                                if (player != null) {
                                                    // Aqui estamos tentando abrir a página
                                                    player.getPageManager().openCustomPage(ref, store, new WelcomePage(playerRef));
                                                    LOGGER.atInfo().log("Comando de GUI enviado para " + playerRef.getUsername());
                                                }
                                            } catch (Exception innerEx) {
                                                LOGGER.atSevere().log("Erro interno ao abrir GUI: " + innerEx.getMessage());
                                            }
                                        });
                                    }
                                }
                            } catch (Exception e) {
                                LOGGER.atSevere().log("Erro ao agendar GUI: " + e.getMessage());
                            }
                        });
                    }
                }
            }
        });


    }
}