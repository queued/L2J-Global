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
package handlers.effecthandlers;

import com.l2jglobal.gameserver.GeoData;
import com.l2jglobal.gameserver.ai.CtrlEvent;
import com.l2jglobal.gameserver.ai.CtrlIntention;
import com.l2jglobal.gameserver.enums.Race;
import com.l2jglobal.gameserver.model.Location;
import com.l2jglobal.gameserver.model.StatsSet;
import com.l2jglobal.gameserver.model.actor.L2Character;
import com.l2jglobal.gameserver.model.actor.instance.L2DefenderInstance;
import com.l2jglobal.gameserver.model.actor.instance.L2FortCommanderInstance;
import com.l2jglobal.gameserver.model.actor.instance.L2SiegeFlagInstance;
import com.l2jglobal.gameserver.model.effects.AbstractEffect;
import com.l2jglobal.gameserver.model.skills.BuffInfo;
import com.l2jglobal.gameserver.model.skills.Skill;
import com.l2jglobal.gameserver.util.Util;

/**
 * Fear effect implementation.
 * @author littlecrow
 */
public final class Fear extends AbstractEffect
{
	public static final int FEAR_RANGE = 500;
	
	public Fear(StatsSet params)
	{
		
	}
	
	@Override
	public boolean canStart(BuffInfo info)
	{
		final L2Character creature = info.getEffected();
		return creature.isPlayer() || creature.isSummon() || (creature.isAttackable() && //
			!((creature instanceof L2DefenderInstance) || (creature instanceof L2FortCommanderInstance) || //
				(creature instanceof L2SiegeFlagInstance) || (creature.getTemplate().getRace() == Race.SIEGE_WEAPON)));
	}
	
	@Override
	public int getTicks()
	{
		return 5;
	}
	
	@Override
	public boolean onActionTime(BuffInfo info)
	{
		fearAction(null, info.getEffected());
		return false;
	}
	
	@Override
	public void onStart(L2Character effector, L2Character effected, Skill skill)
	{
		effected.getAI().notifyEvent(CtrlEvent.EVT_AFRAID);
		fearAction(effector, effected);
	}
	
	@Override
	public void onExit(BuffInfo info)
	{
		if (!info.getEffected().isPlayer())
		{
			info.getEffected().getAI().notifyEvent(CtrlEvent.EVT_THINK);
		}
	}
	
	private void fearAction(L2Character effector, L2Character effected)
	{
		final double radians = Math.toRadians((effector != null) ? Util.calculateAngleFrom(effector, effected) : Util.convertHeadingToDegree(effected.getHeading()));
		
		final int posX = (int) (effected.getX() + (FEAR_RANGE * Math.cos(radians)));
		final int posY = (int) (effected.getY() + (FEAR_RANGE * Math.sin(radians)));
		final int posZ = effected.getZ();
		
		final Location destination = GeoData.getInstance().moveCheck(effected.getX(), effected.getY(), effected.getZ(), posX, posY, posZ, effected.getInstanceWorld());
		effected.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, destination);
	}
}
