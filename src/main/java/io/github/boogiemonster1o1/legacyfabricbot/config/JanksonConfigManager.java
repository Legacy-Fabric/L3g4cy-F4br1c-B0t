package io.github.boogiemonster1o1.legacyfabricbot.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.impl.SyntaxError;
import com.mojang.serialization.Codec;

class JanksonConfigManager<T> extends ConfigManager<T> {
	private static final Jankson JANKSON = Jankson.builder().build();
	private T defaultValue = null;

	private JanksonConfigManager(Path configPath, Codec<T> codec) {
		super(configPath, codec);
	}

	protected JanksonConfigManager(Path configPath, Codec<T> codec, T defaultValue) {
		this(configPath, codec);
		this.defaultValue = Objects.requireNonNull(defaultValue);
	}

	@Override
	public void serialize() throws IOException {
		Files.writeString(
				this.configPath,
				this.codec.encodeStart(
						JanksonOps.INSTANCE,
						this.config
				)
						.getOrThrow(
								false,
								PRINT_TO_STDERR
						)
						.toJson(
								true,
								true
						)
		);
	}

	@Override
	public void deserialize() throws IOException {
		try {
			this.config = this.codec.decode(JanksonOps.INSTANCE, JANKSON.load(Files.newInputStream(this.configPath))).getOrThrow(false, PRINT_TO_STDERR).getFirst();
		} catch (SyntaxError syntaxError) {
			throw new IOException(syntaxError);
		}
	}

	@Override
	protected void writeDefaultData() throws IOException {
		if (this.defaultValue != null) {
			String text = JanksonOps.INSTANCE.withEncoder(this.codec).apply(this.defaultValue).getOrThrow(false, PRINT_TO_STDERR).toJson(true, true);
			Files.writeString(this.configPath, text);
		}
		System.exit(0);
	}
}
