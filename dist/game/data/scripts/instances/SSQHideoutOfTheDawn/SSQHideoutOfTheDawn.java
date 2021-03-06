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
package instances.SSQHideoutOfTheDawn;

import com.l2jglobal.gameserver.model.actor.L2Npc;
import com.l2jglobal.gameserver.model.actor.instance.L2PcInstance;

import instances.AbstractInstance;

/**
 * Hideout of the Dawn instance zone.
 * @author Adry_85
 */
public final class SSQHideoutOfTheDawn extends AbstractInstance
{
	// NPCs
	private static final int WOOD = 32593;
	private static final int JAINA = 32617;
	// Misc
	private static final int TEMPLATE_ID = 113;
	
	public SSQHideoutOfTheDawn()
	{
		addStartNpc(WOOD);
		addTalkId(WOOD, JAINA);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance talker)
	{
		if (npc.getId() == WOOD)
		{
			enterInstance(talker, npc, TEMPLATE_ID);
			return "32593-01.htm";
		}
		finishInstance(talker, 0);
		return "32617-01.htm";
	}
	
	public static void main(String[] args)
	{
		new SSQHideoutOfTheDawn();
	}
}