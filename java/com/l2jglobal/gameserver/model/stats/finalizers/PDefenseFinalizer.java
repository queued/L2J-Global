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
import com.l2jglobal.gameserver.model.actor.instance.L2PcInstance;
import com.l2jglobal.gameserver.model.actor.instance.L2PetInstance;
import com.l2jglobal.gameserver.model.itemcontainer.Inventory;
import com.l2jglobal.gameserver.model.items.L2Item;
import com.l2jglobal.gameserver.model.items.instance.L2ItemInstance;
import com.l2jglobal.gameserver.model.stats.BaseStats;
import com.l2jglobal.gameserver.model.stats.IStatsFunction;
import com.l2jglobal.gameserver.model.stats.Stats;

/**
 * @author UnAfraid
 */
public class PDefenseFinalizer implements IStatsFunction
{
	private static final int[] SLOTS =
	{
		Inventory.PAPERDOLL_CHEST,
		Inventory.PAPERDOLL_LEGS,
		Inventory.PAPERDOLL_HEAD,
		Inventory.PAPERDOLL_FEET,
		Inventory.PAPERDOLL_GLOVES,
		Inventory.PAPERDOLL_UNDER,
		Inventory.PAPERDOLL_CLOAK
	};
	
	@Override
	public double calc(L2Character creature, Optional<Double> base, Stats stat)
	{
		throwIfPresent(base);
		double baseValue = creature.getTemplate().getBaseValue(stat, 0);
		if (creature.isPet())
		{
			final L2PetInstance pet = (L2PetInstance) creature;
			baseValue = pet.getPetLevelData().getPetPDef();
		}
		baseValue += calcEnchantedItemBonus(creature, stat);
		
		final Inventory inv = creature.getInventory();
		if (inv != null)
		{
			for (L2ItemInstance item : inv.getPaperdollItems())
			{
				baseValue += item.getItem().getStats(stat, 0);
			}
			
			if (creature.isPlayer())
			{
				final L2PcInstance player = creature.getActingPlayer();
				for (int slot : SLOTS)
				{
					if (!inv.isPaperdollSlotEmpty(slot) || ((slot == Inventory.PAPERDOLL_LEGS) && !inv.isPaperdollSlotEmpty(Inventory.PAPERDOLL_CHEST) && (inv.getPaperdollItem(Inventory.PAPERDOLL_CHEST).getItem().getBodyPart() == L2Item.SLOT_FULL_ARMOR)))
					{
						final int defaultStatValue = player.getTemplate().getBaseDefBySlot(slot);
						baseValue -= creature.getTransformation().map(transform -> transform.getBaseDefBySlot(player, slot)).orElse(defaultStatValue);
					}
				}
			}
			baseValue *= BaseStats.CHA.calcBonus(creature);
		}
		if (creature.isRaid())
		{
			baseValue *= Config.RAID_PDEFENCE_MULTIPLIER;
		}
		baseValue *= creature.getLevelMod();
		
		return defaultValue(creature, stat, baseValue);
	}
	
	private double defaultValue(L2Character creature, Stats stat, double baseValue)
	{
		final double mul = Math.max(creature.getStat().getMul(stat), 0.5);
		final double add = creature.getStat().getAdd(stat);
		return (baseValue * mul) + add + creature.getStat().getMoveTypeValue(stat, creature.getMoveType());
	}
}
