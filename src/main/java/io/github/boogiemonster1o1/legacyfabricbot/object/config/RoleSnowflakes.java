package io.github.boogiemonster1o1.legacyfabricbot.object.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class RoleSnowflakes {
    public static final Codec<RoleSnowflakes> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.LONG.fieldOf("op_role").forGetter(RoleSnowflakes::getOpRole)
    ).apply(instance, RoleSnowflakes::new));
    public static final RoleSnowflakes DEFAULT = new RoleSnowflakes(730903564708478976L);
    private final long opRole;

    public RoleSnowflakes(long opRole) {
        this.opRole = opRole;
    }

    public long getOpRole() {
        return this.opRole;
    }
}
