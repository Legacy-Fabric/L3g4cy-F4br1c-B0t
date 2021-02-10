package io.github.boogiemonster1o1.legacyfabricbot.object.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class RoleSnowflakes {
	public static final Codec<RoleSnowflakes> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.LONG.fieldOf("op_role").forGetter(RoleSnowflakes::getOpRole),
			Codec.LONG.fieldOf("mute_role").forGetter(RoleSnowflakes::getMuteRole)
	).apply(instance, RoleSnowflakes::new));
	public static final RoleSnowflakes DEFAULT = new RoleSnowflakes(730903564708478976L, 763266548320043059L);
	private final long opRole;
	private final long muteRole;

	public RoleSnowflakes(long opRole, long muteRole) {
		this.opRole = opRole;
		this.muteRole = muteRole;
	}

	public long getOpRole() {
		return this.opRole;
	}

	public long getMuteRole() {
		return this.muteRole;
	}
}
