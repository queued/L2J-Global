/*
 * This file is part of the L2J Global project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package events.EveTheFortuneTeller;

import com.l2jglobal.gameserver.enums.ChatType;
import com.l2jglobal.gameserver.model.Location;
import com.l2jglobal.gameserver.model.actor.L2Npc;
import com.l2jglobal.gameserver.model.actor.instance.L2PcInstance;
import com.l2jglobal.gameserver.model.quest.LongTimeEvent;
import com.l2jglobal.gameserver.network.NpcStringId;
import com.l2jglobal.gameserver.network.serverpackets.NpcSay;
import com.l2jglobal.gameserver.network.serverpackets.luckygame.ExStartLuckyGame;
import com.l2jglobal.gameserver.util.Broadcast;

/**
 * Eve the Fortune Teller Returns<br>
 * Info - http://www.lineage2.com/en/news/events/11182015-eve-the-fortune-teller-returns.php
 * @author Global
 */
public final class EveTheFortuneTeller extends LongTimeEvent
{
	// NPCs
	private static final int EVE = 8542;
	private static final int JAYCE = 8540;
	// Misc
	private static final Location JAYCE_SPAWN = new Location(148090, 26644, -2209, 16383);
	private static final NpcStringId[] JAYCE_TEXT =
	{
		NpcStringId.I_LOOK_WEST,
		NpcStringId.EVE_WILL_BRING_YOU_GREAT_FORTUNE,
		NpcStringId.YOU_WILL_ONE_DAY_ASK_ME_FOR_GUIDANCE_IN_YOUR_PATH
	};
	
	private EveTheFortuneTeller()
	{
		addStartNpc(EVE);
		addFirstTalkId(EVE);
		addTalkId(EVE);
		addSpawnId(JAYCE);
		if (isEventPeriod())
		{
			addSpawn(JAYCE, JAYCE_SPAWN, false, 0);
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		switch (event)
		{
			case "8542.htm":
			case "8542-1.htm":
			{
				htmltext = event;
				break;
			}
			case "FortuneReadingGame":
			{
				player.sendPacket(new ExStartLuckyGame(player, 1));
				break;
			}
			case "LuxuryFortuneReadingGame":
			{
				player.sendPacket(new ExStartLuckyGame(player, 2));
				break;
			}
			case "JAYCE_SHOUT":
			{
				Broadcast.toKnownPlayersInRadius(npc, new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getId(), JAYCE_TEXT[getRandom(JAYCE_TEXT.length)]), 1000);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		return "8542.htm";
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		startQuestTimer("JAYCE_SHOUT", 45000, npc, null, true);
		return super.onSpawn(npc);
	}
	
	public static void main(String[] args)
	{
		new EveTheFortuneTeller();
	}
}
