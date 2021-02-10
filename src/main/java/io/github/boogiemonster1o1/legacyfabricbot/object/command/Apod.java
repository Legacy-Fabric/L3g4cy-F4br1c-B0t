package io.github.boogiemonster1o1.legacyfabricbot.object.command;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class Apod {
	public static final Codec<Apod> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.optionalFieldOf("date", "No Date Provided").forGetter(Apod::getDate),
			Codec.STRING.optionalFieldOf("explanation", "No explanation Provided").forGetter(Apod::getExplanation),
			Codec.STRING.optionalFieldOf("hdurl", "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a6/Anonymous_emblem.svg/1200px-Anonymous_emblem.svg.png").forGetter(Apod::getHdurl),
			Codec.STRING.optionalFieldOf("title", "No Title Provided").forGetter(Apod::getTitle),
			Codec.STRING.optionalFieldOf("url", "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a6/Anonymous_emblem.svg/1200px-Anonymous_emblem.svg.png").forGetter(Apod::getUrl)
	).apply(instance, Apod::new));
	private final String date;
	private final String explanation;
	private final String hdurl;
	private final String title;
	private final String url;

	public Apod(String date, String explanation, String hdurl, String title, String url) {
		this.date = date;
		this.explanation = explanation;
		this.hdurl = hdurl;
		this.title = title;
		this.url = url;
	}

	public String getDate() {
		return this.date;
	}

	public String getExplanation() {
		return this.explanation;
	}

	public String getHdurl() {
		return this.hdurl;
	}

	public String getTitle() {
		return this.title;
	}

	public String getUrl() {
		return this.url;
	}
}
