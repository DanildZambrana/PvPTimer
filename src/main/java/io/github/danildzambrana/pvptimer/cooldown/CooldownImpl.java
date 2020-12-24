package io.github.danildzambrana.pvptimer.cooldown;

import io.github.danildzambrana.pvptimer.file.FileManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CooldownImpl implements Cooldown {
    private FileManager file;
    private Map<UUID, Long> cooldownMap;

    public CooldownImpl(FileManager file) {
        this.cooldownMap = new HashMap<>();
        this.file = file;
    }

    @Override
    public boolean isCooldown(UUID id) {
        if (!cooldownMap.containsKey(id)) {
            return false;
        }

        long cooldown = cooldownMap.get(id);
        if ((cooldown - System.currentTimeMillis()) > 0) {
            cooldownMap.remove(id);
            return true;
        }
        return false;
    }


    @Override
    public void addCooldown(UUID id, int time) {
        cooldownMap.put(id, ((time * 1000L) + System.currentTimeMillis()));

        file.getBukkitFile().set(id.toString(), true);
        file.saveFile();
    }

    @Override
    public void removeCooldown(UUID id) {
        cooldownMap.remove(id);
    }

    @Override
    public long getCooldown(UUID id) {
        if (!cooldownMap.containsKey(id)) {
            return 0;
        }

        long remaining = cooldownMap.get(id) - System.currentTimeMillis();

        return (remaining > 0) ? remaining : 0;
    }
}
