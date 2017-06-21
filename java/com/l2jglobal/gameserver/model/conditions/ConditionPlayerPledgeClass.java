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
package com.l2jglobal.gameserver.model.conditions;

import com.l2jglobal.gameserver.model.actor.L2Character;
import com.l2jglobal.gameserver.model.items.L2Item;
import com.l2jglobal.gameserver.model.skills.Skill;

/**
 * The Class ConditionPlayerPledgeClass.
 * @author MrPoke
 */
public final class ConditionPlayerPledgeClass extends Condition
{
	
	private final int _pledgeClass;
	
	/**
	 * Instantiates a new condition player pledge class.
	 * @param pledgeClass the pledge class
	 */
	public ConditionPlayerPledgeClass(int pledgeClass)
	{
		_pledgeClass = pledgeClass;
	}
	
	/**
	 * Test impl.
	 * @return true, if successful
	 */
	@Override
	public boolean testImpl(L2Character effector, L2Character effected, Skill skill, L2Item item)
	{
		if ((effector.getActingPlayer() == null) || (effector.getActingPlayer().getClan() == null))
		{
			return false;
		}
		return (_pledgeClass == -1) ? effector.getActingPlayer().isClanLeader() : (effector.getActingPlayer().getPledgeClass() >= _pledgeClass);
	}
}