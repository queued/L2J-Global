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
package handlers.skillconditionhandlers;

import com.l2jglobal.gameserver.model.L2Object;
import com.l2jglobal.gameserver.model.StatsSet;
import com.l2jglobal.gameserver.model.actor.L2Character;
import com.l2jglobal.gameserver.model.skills.ISkillCondition;
import com.l2jglobal.gameserver.model.skills.Skill;

/**
 * @author UnAfraid
 */
public class OpSkillSkillCondition implements ISkillCondition
{
	private final int _skillId;
	private final int _skillLevel;
	private final boolean _hasLearned;
	
	public OpSkillSkillCondition(StatsSet params)
	{
		_skillId = params.getInt("skillId");
		_skillLevel = params.getInt("skillLevel");
		_hasLearned = params.getBoolean("hasLearned");
	}
	
	@Override
	public boolean canUse(L2Character caster, Skill skill, L2Object target)
	{
		final Skill requestedSkill = caster.getKnownSkill(_skillId);
		if (_hasLearned)
		{
			return (requestedSkill != null) && (requestedSkill.getLevel() == _skillLevel);
		}
		return (requestedSkill == null) || (requestedSkill.getLevel() != _skillLevel);
	}
}
