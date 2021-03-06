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
package handlers.targethandlers.affectscope;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.l2jglobal.gameserver.handler.AffectObjectHandler;
import com.l2jglobal.gameserver.handler.IAffectObjectHandler;
import com.l2jglobal.gameserver.handler.IAffectScopeHandler;
import com.l2jglobal.gameserver.model.L2CommandChannel;
import com.l2jglobal.gameserver.model.L2Object;
import com.l2jglobal.gameserver.model.L2Party;
import com.l2jglobal.gameserver.model.L2World;
import com.l2jglobal.gameserver.model.actor.L2Character;
import com.l2jglobal.gameserver.model.actor.L2Playable;
import com.l2jglobal.gameserver.model.actor.instance.L2PcInstance;
import com.l2jglobal.gameserver.model.skills.Skill;
import com.l2jglobal.gameserver.model.skills.targets.AffectScope;

/**
 * Dead command channel/party affect scope implementation.
 * @author Nik
 */
public class DeadUnion implements IAffectScopeHandler
{
	@Override
	public void forEachAffected(L2Character activeChar, L2Object target, Skill skill, Consumer<? super L2Object> action)
	{
		final IAffectObjectHandler affectObject = AffectObjectHandler.getInstance().getHandler(skill.getAffectObject());
		final int affectRange = skill.getAffectRange();
		final int affectLimit = skill.getAffectLimit();
		
		if (target.isPlayable())
		{
			final L2PcInstance player = target.getActingPlayer();
			final L2Party party = player.getParty();
			final L2CommandChannel commandChannel = party != null ? party.getCommandChannel() : null;
			
			// Create the target filter.
			final AtomicInteger affected = new AtomicInteger(0);
			final Predicate<L2Playable> filter = plbl ->
			{
				if ((affectLimit > 0) && (affected.get() >= affectLimit))
				{
					return false;
				}
				
				final L2PcInstance p = plbl.getActingPlayer();
				if ((p == null) || !p.isDead())
				{
					return false;
				}
				
				if (p != player)
				{
					final L2Party targetParty = p.getParty();
					if ((party == null) || (targetParty == null))
					{
						return false;
					}
					
					if (party.getLeaderObjectId() != targetParty.getLeaderObjectId())
					{
						final L2CommandChannel targetCommandChannel = targetParty.getCommandChannel();
						if ((commandChannel == null) || (targetCommandChannel == null) || (commandChannel.getLeaderObjectId() != targetCommandChannel.getLeaderObjectId()))
						{
							return false;
						}
					}
				}
				
				if ((affectObject != null) && !affectObject.checkAffectedObject(activeChar, p))
				{
					return false;
				}
				
				affected.incrementAndGet();
				return true;
			};
			
			// Affect object of origin since its skipped in the forEachVisibleObjectInRange method.
			if (filter.test((L2Playable) target))
			{
				action.accept(target);
			}
			
			// Check and add targets.
			L2World.getInstance().forEachVisibleObjectInRange(target, L2Playable.class, affectRange, c ->
			{
				if (filter.test(c))
				{
					action.accept(c);
				}
			});
		}
	}
	
	@Override
	public Enum<AffectScope> getAffectScopeType()
	{
		return AffectScope.DEAD_UNION;
	}
}
