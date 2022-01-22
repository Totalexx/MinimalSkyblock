package ru.totalexx.plugins.minimalskyblock.utils;

import java.util.Objects;
import java.util.UUID;

public class IslandEvent {
    public enum Event {
        INVITE,
        TP
    }

    Event event;
    UUID who;
    UUID whom;

    public IslandEvent(Event event, UUID who, UUID whom) {
        this.event = event;
        this.who = who;
        this.whom = whom;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        IslandEvent event = (IslandEvent) object;
        return this.event == event.event && Objects.equals(who, event.who) && Objects.equals(whom, event.whom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(event, who, whom);
    }
}
