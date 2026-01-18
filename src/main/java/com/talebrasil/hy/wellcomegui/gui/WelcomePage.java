package com.talebrasil.hy.wellcomegui.gui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.FormattedMessage;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player; // Importante importar isso
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public class WelcomePage extends InteractiveCustomUIPage<WelcomePage.WelcomeGuiData> {

    private static final String PAGE_LAYOUT = "Pages/WelcomeGUI.ui";

    public WelcomePage(@Nonnull PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, WelcomeGuiData.CODEC);
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder commands, @Nonnull UIEventBuilder events, @Nonnull Store<EntityStore> store) {
        commands.append(PAGE_LAYOUT);

        // 1. Vincula o botão de FECHAR
        events.addEventBinding(CustomUIEventBindingType.Activating, "#CloseButton",
                EventData.of("CloseClicked", "true"), false);

        // 2. Vincula o botão do DISCORD
        events.addEventBinding(CustomUIEventBindingType.Activating, "#DiscordButton",
                EventData.of("DiscordClicked", "true"), false);
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull WelcomeGuiData data) {
        // CORREÇÃO: Recuperamos o objeto Player através do 'store' e 'ref'
        Player player = store.getComponent(ref, Player.getComponentType());

        if (player == null) return; // Segurança caso o jogador tenha desconectado

        // Lógica do botão Fechar
        if (data.closeClicked != null) {
            this.close();
        }

        // Lógica do botão Discord
        if (data.discordClicked != null) {
            // 1. Cria o objeto de mensagem
            FormattedMessage msg = new FormattedMessage();

            // 2. Define o texto (rawText) com as cores
            msg.rawText = "[Discord] Clique aqui para entrar: https://discord.gg/qDpeUnGV2q";

            // (Opcional) Se quiser garantir que seja clicável via sistema do jogo, defina o link também:
             msg.link = "https://discord.gg/qDpeUnGV2q";

            // 3. Envia o objeto FormattedMessage
            player.sendMessage(new Message(msg));

            // Opcional: Fechar a janela
             this.close();
        }
    }

    public static class WelcomeGuiData {
        static final String KEY_CLOSE = "CloseClicked";
        static final String KEY_DISCORD = "DiscordClicked";

        public static final BuilderCodec<WelcomeGuiData> CODEC = BuilderCodec.builder(WelcomeGuiData.class, WelcomeGuiData::new)
                .addField(new KeyedCodec<>(KEY_CLOSE, Codec.STRING), (d, v) -> d.closeClicked = v, d -> d.closeClicked)
                .addField(new KeyedCodec<>(KEY_DISCORD, Codec.STRING), (d, v) -> d.discordClicked = v, d -> d.discordClicked)
                .build();

        private String closeClicked;
        private String discordClicked;
    }
}