package io.github.danildzambrana.pvptimer.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import io.github.danildzambrana.pvptimer.PvPTimer;
import io.github.danildzambrana.pvptimer.listeners.PlayerListener;
import io.github.danildzambrana.pvptimer.mock.PlayerMockT;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


class PvPTimerCommandsTest {
    private static ServerMock server;
    private static PvPTimer plugin;

    @BeforeEach
    public void setUp() {
        // Start the mock server
        if (MockBukkit.getMock() == null) {
            server = MockBukkit.mock();


            // Load your plugin
            plugin = MockBukkit.load(PvPTimer.class);
        }
    }

    @Test
    void on_player_join() {
        PlayerMockT joined = new PlayerMockT("JOIN PLAYER", UUID.randomUUID());
        server.addPlayer(joined);

        PlayerJoinEvent joinEvent = new PlayerJoinEvent(joined, "");

        new PlayerListener(plugin.getConfigFile(), plugin.getCooldown()).onPlayerJoin(joinEvent);
    }

    @Test
    void on_add_cooldown() {
        Player sender = server.addPlayer();

        new PvPTimerCommands(plugin.getConfigFile(), plugin, plugin.getCooldown()).onCommand(sender
                , null, "pvptimer", new String[]{"grant", sender.getName()});

        assertNotEquals(plugin.getCooldown().getCooldown(sender.getUniqueId()), 0);
    }

    @Test
    void on_remove_cooldown() {
        //First add the player
        Player sender = server.addPlayer();

        new PvPTimerCommands(plugin.getConfigFile(), plugin, plugin.getCooldown()).onCommand(sender
                , null, "pvptimer", new String[]{"grant", sender.getName()});

        assertNotEquals(plugin.getCooldown().getCooldown(sender.getUniqueId()), 0);

        //Now remove it
        new PvPTimerCommands(plugin.getConfigFile(), plugin, plugin.getCooldown()).onCommand(sender
                , null, "pvptimer", new String[]{"enable", sender.getName()});

        assertEquals(plugin.getCooldown().getCooldown(sender.getUniqueId()), 0);
    }
}