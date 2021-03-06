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

import static com.l2jglobal.gameserver.model.actor.L2Npc.INTERACTION_DISTANCE;

import java.util.HashSet;
import java.util.Set;

import com.l2jglobal.Config;
import com.l2jglobal.commons.network.PacketReader;
import com.l2jglobal.gameserver.data.sql.impl.OfflineTradersTable;
import com.l2jglobal.gameserver.enums.PrivateStoreType;
import com.l2jglobal.gameserver.model.ItemRequest;
import com.l2jglobal.gameserver.model.L2Object;
import com.l2jglobal.gameserver.model.L2World;
import com.l2jglobal.gameserver.model.TradeList;
import com.l2jglobal.gameserver.model.actor.instance.L2PcInstance;
import com.l2jglobal.gameserver.model.ceremonyofchaos.CeremonyOfChaosEvent;
import com.l2jglobal.gameserver.network.SystemMessageId;
import com.l2jglobal.gameserver.network.client.L2GameClient;
import com.l2jglobal.gameserver.network.serverpackets.ActionFailed;
import com.l2jglobal.gameserver.util.Util;

/**
 * This class ...
 * @version $Revision: 1.2.2.1.2.5 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestPrivateStoreBuy implements IClientIncomingPacket
{
	private static final int BATCH_LENGTH = 20; // length of the one item
	
	private int _storePlayerId;
	private Set<ItemRequest> _items = null;
	
	@Override
	public boolean read(L2GameClient client, PacketReader packet)
	{
		_storePlayerId = packet.readD();
		final int count = packet.readD();
		if ((count <= 0) || (count > Config.MAX_ITEM_IN_PACKET) || ((count * BATCH_LENGTH) != packet.getReadableBytes()))
		{
			return false;
		}
		_items = new HashSet<>();
		
		for (int i = 0; i < count; i++)
		{
			final int objectId = packet.readD();
			final long cnt = packet.readQ();
			final long price = packet.readQ();
			
			if ((objectId < 1) || (cnt < 1) || (price < 0))
			{
				_items = null;
				return false;
			}
			
			_items.add(new ItemRequest(objectId, cnt, price));
		}
		return true;
	}
	
	@Override
	public void run(L2GameClient client)
	{
		final L2PcInstance player = client.getActiveChar();
		if (player == null)
		{
			return;
		}
		
		if (_items == null)
		{
			client.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// Cannot set private store in Ceremony of Chaos event.
		if (player.isOnEvent(CeremonyOfChaosEvent.class))
		{
			client.sendPacket(SystemMessageId.YOU_CANNOT_OPEN_A_PRIVATE_STORE_OR_WORKSHOP_IN_THE_CEREMONY_OF_CHAOS);
			return;
		}
		
		if (!client.getFloodProtectors().getTransaction().tryPerformAction("privatestorebuy"))
		{
			player.sendMessage("You are buying items too fast.");
			return;
		}
		
		final L2Object object = L2World.getInstance().getPlayer(_storePlayerId);
		if ((object == null) || player.isCursedWeaponEquipped())
		{
			return;
		}
		
		final L2PcInstance storePlayer = (L2PcInstance) object;
		if (!player.isInsideRadius(storePlayer, INTERACTION_DISTANCE, true, false))
		{
			return;
		}
		
		if (player.getInstanceWorld() != storePlayer.getInstanceWorld())
		{
			return;
		}
		
		if (!((storePlayer.getPrivateStoreType() == PrivateStoreType.SELL) || (storePlayer.getPrivateStoreType() == PrivateStoreType.PACKAGE_SELL)))
		{
			return;
		}
		
		final TradeList storeList = storePlayer.getSellList();
		if (storeList == null)
		{
			return;
		}
		
		if (!player.getAccessLevel().allowTransaction())
		{
			player.sendMessage("Transactions are disabled for your Access Level.");
			client.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (storePlayer.getPrivateStoreType() == PrivateStoreType.PACKAGE_SELL)
		{
			if (storeList.getItemCount() > _items.size())
			{
				final String msgErr = "[RequestPrivateStoreBuy] player " + client.getActiveChar().getName() + " tried to buy less items than sold by package-sell, ban this player for bot usage!";
				Util.handleIllegalPlayerAction(client.getActiveChar(), msgErr, Config.DEFAULT_PUNISH);
				return;
			}
		}
		
		final int result = storeList.privateStoreBuy(player, _items);
		if (result > 0)
		{
			client.sendPacket(ActionFailed.STATIC_PACKET);
			if (result > 1)
			{
				_log.warning("PrivateStore buy has failed due to invalid list or request. Player: " + player.getName() + ", Private store of: " + storePlayer.getName());
			}
			return;
		}
		
		// Update offline trade record, if realtime saving is enabled
		if (Config.OFFLINE_TRADE_ENABLE && Config.STORE_OFFLINE_TRADE_IN_REALTIME && ((storePlayer.getClient() == null) || storePlayer.getClient().isDetached()))
		{
			OfflineTradersTable.onTransaction(storePlayer, storeList.getItemCount() == 0, false);
		}
		
		if (storeList.getItemCount() == 0)
		{
			storePlayer.setPrivateStoreType(PrivateStoreType.NONE);
			storePlayer.broadcastUserInfo();
		}
	}
}
