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
package com.l2jglobal.gameserver.model.stats.finalizers;

import java.util.Optional;

import com.l2jglobal.Config;
import com.l2jglobal.gameserver.model.actor.L2Character;
import com.l2jglobal.gameserver.model.stats.BaseStats;
import com.l2jglobal.gameserver.model.stats.IStatsFunction;
import com.l2jglobal.gameserver.model.stats.Stats;

/**
 * @author UnAfraid
 */
public class PAttackSpeedFinalizer implements IStatsFunction
{
	@Override
	public double calc(L2Character creature, Optional<Double> base, Stats stat)
	{
		throwIfPresent(base);
		double baseValue = calcWeaponBaseValue(creature, stat);
		if (Config.L2JMOD_CHAMPION_ENABLE && creature.isChampion())
		{
			baseValue = Config.L2JMOD_CHAMPION_SPD_ATK;
		}
		final double chaBonus = creature.isPlayer() ? BaseStats.CHA.calcBonus(creature) : 1.;
		final double dexBonus = creature.getDEX() > 0 ? BaseStats.DEX.calcBonus(creature) : 1.;
		baseValue *= dexBonus * chaBonus;
		return validateValue(creature, Stats.defaultValue(creature, stat, baseValue), 1, Config.MAX_PATK_SPEED);
	}
}
