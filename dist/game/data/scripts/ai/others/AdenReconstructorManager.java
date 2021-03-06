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
package ai.others;

import com.l2jglobal.gameserver.enums.ChatType;
import com.l2jglobal.gameserver.model.actor.L2Npc;
import com.l2jglobal.gameserver.model.actor.instance.L2PcInstance;
import com.l2jglobal.gameserver.network.NpcStringId;

import ai.AbstractNpcAI;

/**
 * Aden Reconstructor Manager AI.
 * @author St3eT
 */
public final class AdenReconstructorManager extends AbstractNpcAI
{
	// NPCs
	private static final int[] NPCS =
	{
		33584, // Moe
		33581, // Eeny
	};
	
	private AdenReconstructorManager()
	{
		addSpawnId(NPCS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (npc != null)
		{
			switch (event)
			{
				case "SPAM_TEXT":
				{
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.THE_LAND_OF_ADEN_IS_IN_NEED_OF_MATERIALS_TO_REBUILD_FROM_SHILEN_S_DESTRUCTION);
					startQuestTimer("SPAM_TEXT2", 1000, npc, null);
					break;
				}
				case "SPAM_TEXT2":
				{
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.PLEASE_DONATE_ANY_UNUSED_MATERIALS_YOU_HAVE_TO_HELP_REBUILD_ADEN);
					startQuestTimer("SPAM_TEXT3", 1000, npc, null);
					break;
				}
				case "SPAM_TEXT3":
				{
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.YOU_LL_RECEIVE_A_GIFT_FOR_ANY_APPLICABLE_DONATION);
					break;
				}
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		startQuestTimer("SPAM_TEXT", (5 * 60 * 1000), npc, null, true);
		return super.onSpawn(npc);
	}
	
	public static void main(String[] args)
	{
		new AdenReconstructorManager();
	}
}
