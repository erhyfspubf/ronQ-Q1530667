package org.qrone.xmlsocket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.LinkedList;

import org.qrone.xmlsocket.event.XMLSocketServerListener;

/**
 * XMLSocket �ʐM�p�̃T�[�o�[�N���X�B<BR>
 * <BR>
 * Macromedia Flash 5 �ȍ~�Ɏ�������Ă��� .swf �t�@�C���Ƃ̒ʐM���s���T�[�o�[�N���X
 * �ł��B�ڑ����Ă��������̃N���C�A���g�Ɛڑ����m�����AXMLSocket �I�u�W�F�N�g�Ƃ����`�Ŋe�N��
 * �C�A���g�Ƃ̒ʐM��i��񋟂��܂��B<BR>
 * <code><pre>
 * XMLSocketServer socketServer = new XMLSocketServer();
 * 
 * socketServer.addXMLSocketServerListener(new XMLSocketServerAdapter(){
 *     public void onNewClient(final XMLSocket socket) {
 *         System.out.println("newclient:");
 *         
 *         socket.addXMLSocketListener(new XMLSocketAdapter(){
 *             public void onXML(Document doc) {
 *                 Element e = doc.getDocumentElement();
 *                 ...
 *             }
 *         });
 *     }
 * });
 * socketServer.open(9601);
 * </pre></code>
 * addXMLSocketServerListener(XMLServerSoketLister) �ŃC�x���g�n���h����o�^���邱�Ƃ�
 * �C�x���g���擾���ė��p���邱�Ƃ��ł��܂����A���̃N���X���p������ protected �w�肳��Ă��邢������
 * ���\�b�h���I�[�o�[���C�h���邱�Ƃł����p�ł��܂��B
 * 
 * @author J.Tabuchi
 * @since 2005/8/6
 * @version 1.0
 * @link QrONE Technology : http://www.qrone.org/
 */
public class XMLSocketServer {
	private static final int SERVER_TIMEOUT = 30000;
	private ServerSocket serversocket;
	
	private Thread startThread;
	private Thread acceptThread;
	
	private LinkedList socketlist = new LinkedList();
	private boolean opened = false;
	
	private LinkedList serverlistener = new LinkedList();

	private Charset inputcs  = null;
	private Charset outputcs = null;
	
	/**
	 * XMLSocketServer �C���X�^���X�𐶐����܂�
	 */
	public XMLSocketServer(){}

	public void setEncoding(Charset cs){
		setEncoding(cs,cs);
	}
	
	public void setEncoding(String charset){
		setEncoding(Charset.forName(charset));
	}
	
	public void setEncoding(Charset input, Charset output){
		inputcs = input;
		outputcs = output;
	}
	
	/**
	 * �w�肵�� port ���J���ăT�[�o�[���J�n���܂��B
	 * @param port �ڑ��󂯓���|�[�g�i��:9601�j
	 */
	public void open(int port){
		try {
			open(new ServerSocket(port));
		} catch (IOException e) {
			onOpen(false);
		}
	}
	
	/**
	 * �w�肵�� serversocket ��p���ăT�[�o�[���J�n���܂��B
	 * @param serversocket�@�󂯓��ꑤ�T�[�o�[�\�P�b�g
	 */
	public void open(final ServerSocket serversocket){
		this.serversocket = serversocket;
		try {
			serversocket.setSoTimeout(SERVER_TIMEOUT);
		} catch (SocketException e) {}
		startThread = new Thread(new Runnable(){
			public void run() {
				acceptThread = new Thread(new Runnable(){
					public void run() {
						while(opened){
							try {
								Socket socket = serversocket.accept();
								XMLSocket xmlsocket = new XMLSocket();
								xmlsocket.setEncoding(inputcs,outputcs);
								
								onNewClient(xmlsocket);
								xmlsocket.connect(socket);
								socketlist.add(xmlsocket);
								
							} catch (SocketTimeoutException e){
							} catch (SocketException e){
							} catch (IOException e) {
								close(e);
							}
						}
					}
				});
				opened = true;
				onOpen(true);
				acceptThread.start();
			}
		});
		startThread.start();
	}
	
	/**
	 * XMLSocketServer �T�[�o�[���I�����܂��B
	 */
	public void close(){
		close(null);
	}
	
	/**
	 * XMLSocketServer�@�T�[�o�[�Ɋ֘A�Â����Ă��� ServerSocket �C���X�^���X��Ԃ��܂��B
	 * @return ���p���̃T�[�o�[�\�P�b�g
	 */
	public ServerSocket getServerSocket(){
		return serversocket;
	}
	
	private void close(Exception e){
		opened = false;
		for (Iterator iter = socketlist.iterator(); iter.hasNext();) {
			XMLSocket xmlsocket = (XMLSocket) iter.next();
			xmlsocket.close();
		}
		try {
			serversocket.close();
		} catch (IOException e1) {
			e = e1;
		}
		onClose();
		if(e!=null) onClose(e);
	}
	
	/**
	 * �T�[�o�[�J�n����ɌĂяo����܂��B success == false �̎��ɂ�<b>�T�[�o�[���J�n����Ă��܂���B</b><BR>
	 * �p�������N���X�ł��̃��\�b�h���I�[�o�[���C�h����ƃC�x���g�n���h���̃C�x���g���Ă΂�Ȃ��Ȃ�܂��B<BR>
	 * <BR>
	 * �ʏ�� addXMLSocketServerListener(XMLSocketServerListener) �ŃC�x���g�n���h���𗘗p���Ă��������B
	 * @see #addXMLSocketServerListener(XMLSocketServerListener)
	 * @param success �T�[�o�[�J�n����
	 */
	protected void onOpen(boolean success){
 		for (Iterator iter = serverlistener.iterator(); iter.hasNext();) {
			((XMLSocketServerListener)iter.next()).onOpen(success);
		}
	}

	/**
	 * �T�[�o�[���G���[�ŏI����������ɌĂяo����܂��B<BR>
	 * �p�������N���X�ł��̃��\�b�h���I�[�o�[���C�h����ƃC�x���g�n���h���̃C�x���g���Ă΂�Ȃ��Ȃ�܂��B<BR>
	 * <BR>
	 * �ʏ�� addXMLSocketServerListener(XMLSocketServerListener) �ŃC�x���g�n���h���𗘗p���Ă��������B
	 * @see #addXMLSocketServerListener(XMLSocketServerListener)
	 * @param e �T�[�o�[�I�����R�G���[
	 */
	protected void onClose(Exception e){
 		for (Iterator iter = serverlistener.iterator(); iter.hasNext();) {
			((XMLSocketServerListener)iter.next()).onClose(e);
		}
	}

	/**
	 * �T�[�o�[�I������ɌĂяo����܂��B<BR>
	 * �p�������N���X�ł��̃��\�b�h���I�[�o�[���C�h����ƃC�x���g�n���h���̃C�x���g���Ă΂�Ȃ��Ȃ�܂��B<BR>
	 * <BR>
	 * �ʏ�� addXMLSocketServerListener(XMLSocketServerListener) �ŃC�x���g�n���h���𗘗p���Ă��������B
	 * @see #addXMLSocketServerListener(XMLSocketServerListener)
	 */
	protected void onClose(){
 		for (Iterator iter = serverlistener.iterator(); iter.hasNext();) {
			((XMLSocketServerListener)iter.next()).onClose();
		}
	}

	/**
	 * �V���� Macromedia Flash �� .swf �t�@�C������ XMLSocket �ʐM��v�����ꂽ����
	 * �Ăяo����Aswf �t�@�C���ƒʐM���m�����钼�O�� XMLSocket �I�u�W�F�N�g���n����܂��B<BR>
	 * �p�������N���X�ł��̃��\�b�h���I�[�o�[���C�h����ƃC�x���g�n���h���̃C�x���g���Ă΂�Ȃ��Ȃ�܂��B<BR>
	 * <BR>
	 * �ʏ�� addXMLSocketServerListener(XMLSocketServerListener) �ŃC�x���g�n���h���𗘗p���Ă��������B
	 * @see #addXMLSocketServerListener(XMLSocketServerListener)
	 */
	protected void onNewClient(XMLSocket socket){
 		for (Iterator iter = serverlistener.iterator(); iter.hasNext();) {
			((XMLSocketServerListener)iter.next()).onNewClient(socket);
		}
	}
	
	/**
	 * �C�x���g�n���h����o�^���Ċe��C�x���g���擾���܂��B
	 * @param listener�@�C�x���g�n���h��
	 */
	public void addXMLSocketServerListener(XMLSocketServerListener listener) {
		serverlistener.add(listener);
	}

	/**
	 * �o�^����Ă���C�x���g�n���h�����폜���܂��B
	 * @param listener�@�C�x���g�n���h��
	 */
	public void removeXMLSocketServerListener(XMLSocketServerListener listener) {
		serverlistener.remove(listener);
	}
	
}
