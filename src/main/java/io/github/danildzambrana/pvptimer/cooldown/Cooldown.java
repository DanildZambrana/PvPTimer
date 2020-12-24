package io.github.danildzambrana.pvptimer.cooldown;

import java.util.UUID;

public interface Cooldown {
    /**
     * Get information about cooldown of object represented by the provided id.
     * @param id the id to get information about the cooldown.
     * @return true if the object has cooldown, otherwise return false.
     */
    boolean isCooldown(UUID id);

    /**
     * Add cooldown to object and save in the file.
     * @param id the id of the object to add the cooldown.
     * @param time the time of the cooldown.
     */
    void addCooldown(UUID id, int time);

    void removeCooldown(UUID id);

    /**
     * Get remaining time of cooldown of object.
     * @param id the id of the object to find the remaining time.
     * @return 0 if the object not is in cooldown, otherwise return the remaining time.
     */
    long getCooldown(UUID id);
}
