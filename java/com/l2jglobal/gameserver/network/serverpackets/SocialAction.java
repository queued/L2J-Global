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
package com.l2jglobal.gameserver.network.serverpackets;

import com.l2jglobal.commons.network.PacketWriter;
import com.l2jglobal.gameserver.network.client.OutgoingPackets;

public class SocialAction implements IClientOutgoingPacket
{
	// TODO: Enum
	public static final int LEVEL_UP = 2122;
	
	private final int _charObjId;
	private final int _actionId;
	
	public SocialAction(int objectId, int actionId)
	{
		_charObjId = objectId;
		_actionId = actionId;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.SOCIAL_ACTION.writeId(packet);
		
		packet.writeD(_charObjId);
		packet.writeD(_actionId);
		packet.writeD(0x00); // TODO: Find me!
		return true;
	}
}
