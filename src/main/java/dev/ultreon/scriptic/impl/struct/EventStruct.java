package dev.ultreon.scriptic.impl.struct;

import dev.ultreon.scriptic.lang.obj.Event;
import dev.ultreon.scriptic.lang.obj.Struct;
import org.jetbrains.annotations.Nullable;

public class EventStruct extends Struct<Event> {
    public EventStruct(@Nullable Event detected, String block) {
        super(detected, block);
    }

    public Event getEventType() {
        return getDetected();
    }

    @Override
    public String getRegistryName() {
        return getDetected().getRegistryName();
    }
}
