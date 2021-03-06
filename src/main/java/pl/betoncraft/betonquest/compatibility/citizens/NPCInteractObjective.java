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
package pl.betoncraft.betonquest.compatibility.citizens;

import net.citizensnpcs.api.event.NPCRightClickEvent;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Player has to right click the NPC
 * 
 * @author Jakub Sapalski
 */
public class NPCInteractObjective extends Objective implements Listener {

	private final int id;
	private final boolean cancel;

	public NPCInteractObjective(String packName, String label, String instruction) throws InstructionParseException {
		super(packName, label, instruction);
		template = ObjectiveData.class;
		String[] parts = instructions.split(" ");
		if (parts.length < 2) {
			throw new InstructionParseException("Not enough arguments");
		}
		try {
			id = Integer.parseInt(parts[1]);
		} catch (NumberFormatException e) {
			throw new InstructionParseException("Could not parse ID");
		}
		if (id < 0) {
			throw new InstructionParseException("ID cannot be negative");
		}
		boolean tempCancel = false;
		for (String part : parts) {
			if (part.equalsIgnoreCase("cancel")) {
				tempCancel = true;
			}
		}
		cancel = tempCancel;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onNPCClick(NPCRightClickEvent event) {
		String playerID = PlayerConverter.getID(event.getClicker());
		if (event.getNPC().getId() != id || !containsPlayer(playerID)) {
			return;
		}
		if (checkConditions(playerID)) {
			if (cancel)
				event.setCancelled(true);
			completeObjective(playerID);
		}
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
		return "";
	}

}
