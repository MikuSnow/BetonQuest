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
package pl.betoncraft.betonquest.compatibility.skript;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Fires the custom event for Skript to listen to
 * 
 * @author Jakub Sapalski
 */
public class BQEventSkript extends QuestEvent {

	private final String id;

	public BQEventSkript(String packName, String instructions) throws InstructionParseException {
		super(packName, instructions);
		String[] parts = instructions.split(" ");
		if (parts.length < 2) {
			throw new InstructionParseException("Not enough arguments");
		}
		id = parts[1];
	}

	/**
	 * Custom event, which runs for Skript to listen.
	 * 
	 * @author Coosh
	 */
	public static class CustomEventForSkript extends PlayerEvent {

		private static final HandlerList handlers = new HandlerList();
		/**
		 * ID of the event, as defined by the BetonQuest's event
		 */
		private final String id;

		/**
		 * @param the
		 *            Player
		 */
		public CustomEventForSkript(Player who, String id) {
			super(who);
			this.id = id;
		}

		/**
		 * @return ID of the event, as defined by the BetonQuest's event
		 */
		public String getID() {
			return id;
		}

		public HandlerList getHandlers() {
			return handlers;
		}

		public static HandlerList getHandlerList() {
			return handlers;
		}

	}

	@Override
	public void run(String playerID) {
		Player player = PlayerConverter.getPlayer(playerID);
		CustomEventForSkript event = new CustomEventForSkript(player, id);
		Bukkit.getServer().getPluginManager().callEvent(event);
	}
}
