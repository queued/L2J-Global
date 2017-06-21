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
package com.l2jglobal.gameserver.instancemanager.tasks;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2jglobal.gameserver.instancemanager.MailManager;
import com.l2jglobal.gameserver.model.L2World;
import com.l2jglobal.gameserver.model.actor.instance.L2PcInstance;
import com.l2jglobal.gameserver.model.entity.Message;
import com.l2jglobal.gameserver.network.SystemMessageId;
import com.l2jglobal.gameserver.network.serverpackets.SystemMessage;

/**
 * Message deletion task.
 * @author xban1x
 */
public final class MessageDeletionTask implements Runnable
{
	private static final Logger _log = Logger.getLogger(MessageDeletionTask.class.getName());
	
	final int _msgId;
	
	public MessageDeletionTask(int msgId)
	{
		_msgId = msgId;
	}
	
	@Override
	public void run()
	{
		final Message msg = MailManager.getInstance().getMessage(_msgId);
		if (msg == null)
		{
			return;
		}
		
		if (msg.hasAttachments())
		{
			try
			{
				final L2PcInstance sender = L2World.getInstance().getPlayer(msg.getSenderId());
				if (sender != null)
				{
					msg.getAttachments().returnToWh(sender.getWarehouse());
					sender.sendPacket(SystemMessageId.THE_MAIL_WAS_RETURNED_DUE_TO_THE_EXCEEDED_WAITING_TIME);
				}
				else
				{
					msg.getAttachments().returnToWh(null);
				}
				
				msg.getAttachments().deleteMe();
				msg.removeAttachments();
				
				final L2PcInstance receiver = L2World.getInstance().getPlayer(msg.getReceiverId());
				if (receiver != null)
				{
					final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THE_MAIL_WAS_RETURNED_DUE_TO_THE_EXCEEDED_WAITING_TIME);
					// sm.addString(msg.getReceiverName());
					receiver.sendPacket(sm);
				}
			}
			catch (Exception e)
			{
				_log.log(Level.WARNING, getClass().getSimpleName() + ": Error returning items:" + e.getMessage(), e);
			}
		}
		MailManager.getInstance().deleteMessageInDb(msg.getId());
	}
}