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
package pl.betoncraft.betonquest.compatibility.racesandclasses;

import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.APIs.ManaAPI;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Adds or removes RaC mana.
 * 
 * @author Jakub Sapalski
 */
public class RaCManaEvent extends QuestEvent {

	private int number;
	private boolean refill = false;

	public RaCManaEvent(String packName, String instructions) throws InstructionParseException {
		super(packName, instructions);
		String[] parts = instructions.split(" ");
		if (parts.length < 2) {
			throw new InstructionParseException("Not enough arguments");
		}
		if (parts[1].equalsIgnoreCase("refill")) {
			refill = true;
		} else try {
			number = Integer.parseInt(parts[1]);
		} catch (NumberFormatException e) {
			throw new InstructionParseException("Could not parse a number");
		}
	}

	@Override
	public void run(String playerID) {
		Player p = PlayerConverter.getPlayer(playerID);
		if (refill) {
			ManaAPI.fillMana(p, ManaAPI.getMaxMana(p) - ManaAPI.getCurrentMana(p));
		} else {
			if (number >= 0) {
				ManaAPI.fillMana(p, number);
			} else {
				ManaAPI.drainMana(p, -number);
			}
		}
	}

}
