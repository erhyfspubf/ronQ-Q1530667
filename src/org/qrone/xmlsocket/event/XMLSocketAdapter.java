package org.qrone.xmlsocket.event;

import org.w3c.dom.Document;

/**
 * �������Ȃ��A�����S�Ă̊֐����`���������� XMLSocketListener �ւ̃A�_�v�^�N���X
 * @author J.Tabuchi
 * @since 2005/8/6
 * @version 1.0
 * @link QrONE Technology : http://www.qrone.org/
 */
public class XMLSocketAdapter {
	public void onConnect(boolean success){}
	public void onClose(){}
	public void onClose(Exception e){}
	public void onTimeout(){}
	public void onData(String data){}
	public void onXML(Document doc){}
}
