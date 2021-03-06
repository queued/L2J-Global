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
import com.l2jglobal.gameserver.model.actor.L2Character;
import com.l2jglobal.gameserver.network.client.OutgoingPackets;

/**
 * @author Sdw
 */
public class ExTacticalSign implements IClientOutgoingPacket
{
	private final L2Character _target;
	private final int _tokenId;
	
	public ExTacticalSign(L2Character target, int tokenId)
	{
		_target = target;
		_tokenId = tokenId;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_TACTICAL_SIGN.writeId(packet);
		
		packet.writeD(_target.getObjectId());
		packet.writeD(_tokenId);
		return true;
	}
}