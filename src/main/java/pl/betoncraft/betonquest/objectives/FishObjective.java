/**
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.betonquest.objectives;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Requires the player to catch the fish.
 * 
 * @author Jakub Sapalski
 */
public class FishObjective extends Objective implements Listener {

	private final Material fish;
	private final byte data;
	private final int amount;
	private final boolean notify;

	public FishObjective(String packName, String label, String instructions) throws InstructionParseException {
		super(packName, label, instructions);
		template = FishData.class;
		String[] parts = instructions.split(" ");
		if (parts.length < 3)
			throw new InstructionParseException("Not enough arguments");
		String[] fishParts = parts[1].split(":");
		fish = Material.matchMaterial(fishParts[0]);
		if (fish == null)
			throw new InstructionParseException("Unknown fish type");
		if (fishParts.length > 1) {
			try {
				data = Byte.parseByte(fishParts[1]);
			} catch (NumberFormatException e) {
				throw new InstructionParseException("Could not parse fish data value");
			}
		} else {
			data = -1;
		}
		try {
			amount = Integer.parseInt(parts[2]);
		} catch (NumberFormatException e) {
			throw new InstructionParseException("Could not parse fish amount");
		}
		boolean tempNotify = false;
		for (String part : parts) {
			if (part.equalsIgnoreCase("notify")) {
				tempNotify = true;
			}
		}
		notify = tempNotify;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onFishCatch(PlayerFishEvent event) {
		String playerID = PlayerConverter.getID(event.getPlayer());
		if (!containsPlayer(playerID))
			return;
		if (event.getCaught() == null)
			return;
		if (event.getCaught().getType() != EntityType.DROPPED_ITEM)
			return;
		ItemStack item = ((Item) event.getCaught()).getItemStack();
		if (item.getType() != fish)
			return;
		if (data >= 0 && item.getData().getData() != data)
			return;
		FishData data = (FishData) dataMap.get(playerID);
		if (checkConditions(playerID))
			data.catchFish();
		if (data.getAmount() <= 0)
			completeObjective(playerID);
		else if (notify)
			Config.sendMessage(playerID, "fish_to_catch", new String[] { String.valueOf(data.getAmount()) });
	}

	@Override
	public String getProperty(String name, String playerID) {
		if (name.equalsIgnoreCase("left")) {
			return Integer.toString(((FishData) dataMap.get(playerID)).getAmount());
		} else if (name.equalsIgnoreCase("amount")) {
			return Integer.toString(amount - ((FishData) dataMap.get(playerID)).getAmount());
		}
		return "";
	}

	@Override
	public void start() {
		Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
	}

	@Override
	public void stop() {
		HandlerList.unregisterAll(this);
	}

	@Override
	public String getDefaultDataInstruction() {
		return Integer.toString(amount);
	}

	public static class FishData extends ObjectiveData {

		private int amount;

		public FishData(String instruction, String playerID, String objID) {
			super(instruction, playerID, objID);
			amount = Integer.parseInt(instruction);
		}

		public void catchFish() {
			amount--;
			update();
		}

		public int getAmount() {
			return amount;
		}

		@Override
		public String toString() {
			return String.valueOf(amount);
		}

	}
}
