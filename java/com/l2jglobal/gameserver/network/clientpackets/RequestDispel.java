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
package com.l2jglobal.gameserver.network.clientpackets;

import com.l2jglobal.Config;
import com.l2jglobal.commons.network.PacketReader;
import com.l2jglobal.gameserver.data.xml.impl.SkillData;
import com.l2jglobal.gameserver.model.actor.L2Summon;
import com.l2jglobal.gameserver.model.actor.instance.L2PcInstance;
import com.l2jglobal.gameserver.model.skills.AbnormalType;
import com.l2jglobal.gameserver.model.skills.Skill;
import com.l2jglobal.gameserver.network.client.L2GameClient;

/**
 * @author KenM
 */
public class RequestDispel implements IClientIncomingPacket
{
	private int _objectId;
	private int _skillId;
	private int _skillLevel;
	
	@Override
	public boolean read(L2GameClient client, PacketReader packet)
	{
		_objectId = packet.readD();
		_skillId = packet.readD();
		_skillLevel = packet.readD();
		return true;
	}
	
	@Override
	public void run(L2GameClient client)
	{
		if ((_skillId <= 0) || (_skillLevel <= 0))
		{
			return;
		}
		final L2PcInstance activeChar = client.getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		final Skill skill = SkillData.getInstance().getSkill(_skillId, _skillLevel);
		if (skill == null)
		{
			return;
		}
		if (!skill.canBeDispelled() || skill.isStayAfterDeath() || skill.isDebuff())
		{
			return;
		}
		if (skill.getAbnormalType() == AbnormalType.TRANSFORM)
		{
			return;
		}
		if (skill.isDance() && !Config.DANCE_CANCEL_BUFF)
		{
			return;
		}
		if (activeChar.getObjectId() == _objectId)
		{
			activeChar.stopSkillEffects(true, _skillId);
		}
		else
		{
			final L2Summon pet = activeChar.getPet();
			if ((pet != null) && (pet.getObjectId() == _objectId))
			{
				pet.stopSkillEffects(true, _skillId);
			}
			
			if (activeChar.hasServitor(_objectId))
			{
				activeChar.removeServitor(_objectId);
			}
		}
	}
}
