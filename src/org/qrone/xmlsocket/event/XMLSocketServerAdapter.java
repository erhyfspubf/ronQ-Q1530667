package org.qrone.xmlsocket.event;

import org.qrone.xmlsocket.XMLSocket;

/**
 * �������Ȃ��A�����S�Ă̊֐����`���������� XMLServerSocketListener �ւ̃A�_�v�^�N���X
 * @author J.Tabuchi
 * @since 2005/8/6
 * @version 1.0
 * @link QrONE Technology : http://www.qrone.org/
 */
public class XMLSocketServerAdapter {
	public void onOpen(boolean success){}
	public void onClose(Exception e){}
	public void onClose(){}
	public void onNewClient(XMLSocket socket){}
}
