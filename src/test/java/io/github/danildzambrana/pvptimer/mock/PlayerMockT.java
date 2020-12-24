package io.github.danildzambrana.pvptimer.mock;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.block.SkullOwner;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class PlayerMockT extends PlayerMock implements Player {
    public PlayerMockT(String name, UUID uuid) {
        super(name, uuid);
    }

    public PlayerMockT(String name) {
        super(name);
    }

    @Override
    public boolean hasPlayedBefore() {
        return false;
    }

    @Override
    public SkullOwner.Texture getTexture() {
        return null;
    }

    @Override
    public Map<String, Object> getData() {
        return null;
    }
}
