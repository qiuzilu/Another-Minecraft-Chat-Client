package net.defekt.mc.chatclient.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AutoResponseRule implements Serializable {
	private static final long serialVersionUID = 2057896755819059984L;

	public static enum EffectType {
		RANDOM, ORDERED, ALL
	}

	public static enum TriggerType {
		AND, OR
	}

	private final transient Random rand = new Random();

	private String name;
	private int interval;
	private List<String> triggers = new ArrayList<String>();
	private List<String> exceptions = new ArrayList<String>();
	private List<String> effects = new ArrayList<String>();
	private EffectType effectType;
	private TriggerType triggerType;

	private transient int index = 0;

	public String[] match(String message) {
		if (effects.size() == 0)
			return null;
		message = message.toLowerCase();
		boolean matches = false;
		for (String trigger : triggers)
			if (message.contains(trigger.toLowerCase())) {
				matches = true;
				if (triggerType == TriggerType.OR)
					break;
			} else if (triggerType == TriggerType.AND)
				return null;
		for (String exception : exceptions)
			if (message.contains(exception.toLowerCase())) {
				matches = false;
				break;
			}

		if (matches) {
			switch (effectType) {
				case ALL: {
					return (String[]) effects.toArray(new String[effects.size()]);
				}
				case ORDERED: {
					String rt = effects.get(index);
					index++;
					index %= effects.size();
					return new String[] { rt };
				}
				default: {
					return new String[] { effects.get(rand.nextInt(effects.size())) };
				}
			}
		} else
			return null;
	}

	public AutoResponseRule(String name, EffectType effectType, TriggerType triggerType, int interval,
			String[] triggers, String[] exceptions, String[] effects) {
		this.name = name;
		this.effectType = effectType;
		this.triggerType = triggerType;
		this.interval = interval;
		if (triggers != null)
			for (String trigger : triggers)
				this.triggers.add(trigger);
		if (exceptions != null)
			for (String exception : exceptions)
				this.exceptions.add(exception);
		if (effects != null)
			for (String effect : effects)
				this.effects.add(effect);
	}

	public String getName() {
		return name;
	}

	public int getInterval() {
		return interval;
	}

	public List<String> getTriggers() {
		return new ArrayList<>(triggers);
	}

	public List<String> getExceptions() {
		return new ArrayList<String>(exceptions);
	}

	public List<String> getEffects() {
		return new ArrayList<String>(effects);
	}

	public EffectType getType() {
		return effectType;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public void setTriggers(String[] triggers) {
		this.triggers = new ArrayList<String>();
		for (String trigger : triggers)
			this.triggers.add(trigger);
	}

	public void setExceptions(String[] exceptions) {
		this.exceptions = new ArrayList<String>();
		for (String exception : exceptions)
			this.exceptions.add(exception);
	}

	public void setEffects(String[] effects) {
		this.effects = new ArrayList<String>();
		for (String effect : effects)
			this.effects.add(effect);
	}

	public void setType(EffectType type) {
		this.effectType = type;
	}

	public TriggerType getTriggerType() {
		return triggerType;
	}

	public void setTriggerType(TriggerType triggerType) {
		this.triggerType = triggerType;
	}
}
