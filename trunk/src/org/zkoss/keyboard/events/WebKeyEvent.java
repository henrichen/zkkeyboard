package org.zkoss.keyboard.events;

import java.util.List;
import java.util.Map;

import org.zkoss.json.JSONArray;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;

public class WebKeyEvent extends Event {
	private final String key;
	private final List spells;

	public static final WebKeyEvent getKeyEvent(AuRequest request) {
		final Component comp = request.getComponent();
		final Map data = request.getData();

		String code = (String) data.get("key");
		JSONArray spells = (JSONArray) data.get("spells");
		return new WebKeyEvent(request.getCommand(), comp, code, spells);

	}

	public WebKeyEvent(String name, Component target, String key, List spells) {
		super(name, target);
		this.key = key;
		this.spells = spells;
	}

	public List getSpells() {
		return spells;
	}

	public String getKey() {
		return key;
	}

	public static final String NAME = "onWebKeyPress";
}
