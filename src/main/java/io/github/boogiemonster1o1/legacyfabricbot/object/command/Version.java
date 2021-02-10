package io.github.boogiemonster1o1.legacyfabricbot.object.command;

import java.util.Objects;

import com.google.common.base.MoreObjects;
import io.github.boogiemonster1o1.legacyfabricbot.command.gen.YarnVersionCommand;

public class Version implements Comparable<Version> {
	public String gameVersion;
	public String separator;
	public long build;
	public String maven;
	public String version;
	public boolean stable;

	@Override
	public int compareTo(Version o) {
		return this.build > o.build ? 1 : -1;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		Version version1 = (Version) o;
		return this.build == version1.build && this.stable == version1.stable && Objects.equals(this.gameVersion, version1.gameVersion) && Objects.equals(this.separator, version1.separator) && Objects.equals(this.maven, version1.maven) && Objects.equals(this.version, version1.version);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.gameVersion, this.separator, this.build, this.maven, this.version, this.stable);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("gameVersion", this.gameVersion)
				.add("separator", this.separator)
				.add("build", this.build)
				.add("maven", this.maven)
				.add("version", this.version)
				.add("stable", this.stable)
				.toString();
	}
}
