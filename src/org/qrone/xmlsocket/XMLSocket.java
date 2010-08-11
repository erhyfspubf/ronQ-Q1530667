package org.qrone.xmlsocket;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.LinkedList;

import javax.xml.transform.TransformerException;

import org.qrone.XMLTools;
import org.qrone.xmlsocket.event.XMLSocketListener;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * XMLSocket �ʐM�N���X�B<BR>
 * <BR>
 * Macromedia Flash 5 �ȍ~�Ɏ�������Ă��� .swf �t�@�C���Ƃ� XMLSocket �ʐM���s���N���X�B
 * ActionScript �� XMLSocket �I�u�W�F�N�g�Ƃقړ��l�ɐ݌v����Ă���A.swf �t�@�C���Ƃ̒ʐM
 * ���s���܂��B�S�ẴC�x���g�́AaddXMLSocketListener(XMLSocketListener) �ŃC�x���g�n��
 * �h���̌`�œo�^���Ď擾���邱�Ƃ��o���܂��B
 * <code><pre>
 * XMLSocket socket = ...;
 * socket.addXMLSocketListener(new XMLSocketListener(){
 *     public void onConnect(boolean success){}
 *     public void onClose(){}
 *     public void onClose(Exception e){}
 *     public void onTimeout(){}
 *     public void onData(String data){}
 *     public void onXML(Document doc){
 *         Element e = doc.getDocumentElement();
 *         ...
 *     }
 * });
 * 
 * </pre></code>
 * XMLSocketServer ���N������ꍇ�ɂ́AXMLSocketServer.addXMLSocketServerListener(XMLSocketServerListener)
 * �ŃC�x���g�n���h����o�^���AXMLSocketServerListener.onNewClient(XMLSocket)
 * ���\�b�h���炱�̃N���X�̃C���X�^���X���擾���Ă��������B�܂����̃N���X���p�����邱�Ƃł������� Flash �N���C
 * �A���g���̂悤�Ɂ@XMLSocket �T�[�o�[�ƒʐM���邱�Ƃ��\�ł��B
 * 
 * @author J.Tabuchi
 * @since 2005/8/6
 * @version 1.0
 * @link QrONE Technology : http://www.qrone.org/
 */
public class XMLSocket {
	private static final int CLIENT_TIMEOUT = 30000;
	private static final char eof = '\0';
	private static final int BUF_SIZE = 1024;
	
	private Socket socket;
	private InputStream in;
	private BufferedReader reader;

	private OutputStream out;
	private BufferedWriter writer;
	
	private Thread connectThread;
	private Thread readThread;
	private Thread writeThread;
	private boolean connected;
	
	private boolean parsexml = true;
	private LinkedList queue = new LinkedList();
	private LinkedList xmllistener = new LinkedList();
	
	private Charset inputcs  = null;
	private Charset outputcs = null;
	/**
	 * �ڑ�����Ă��Ȃ� XMLSocket �I�u�W�F�N�g�𐶐����܂��B
	 */
	public XMLSocket(){}
	
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
	
	public void dynamicChangeOutputEncode(Charset cs){
		synchronized(queue){
			writer = new BufferedWriter(new OutputStreamWriter(out,cs));
		}
	}
	
	/**
	 * ���łɐڑ�����Ă��� socket �𗘗p���ĒʐM���J�n���܂��B
	 * @param socket�@�ڑ��σ\�P�b�g
	 */
	public void connect(final Socket socket){
		this.socket = socket;
		try {
			socket.setSoTimeout(CLIENT_TIMEOUT);
		} catch (SocketException e) {}
		connectThread = new Thread(new Runnable(){
			public void run() {
				try{
					in=socket.getInputStream();
					out=socket.getOutputStream();
					
					if(inputcs == null){
						reader = new BufferedReader(new InputStreamReader(in));
					}else{
						reader = new BufferedReader(new InputStreamReader(in,inputcs));
					}
					if(outputcs == null){
						writer = new BufferedWriter(new OutputStreamWriter(out));
					}else{
						writer = new BufferedWriter(new OutputStreamWriter(out,outputcs));
					}
					connected = true;

					readThread = new Thread(new Runnable(){
				 		public void run(){
				 			char[] buf = new char[BUF_SIZE];
				 			StringBuffer strBuf = new StringBuffer();
				 			while(connected){
				 				try {
				 					int c = reader.read(buf);
				 					//blocking
				 					
				 					if(c < 0) close();
				 					for(int i=0;i<c;i++){
				 						if(buf[i] == 0){
				 							onData(strBuf.toString());
				 							if(parsexml){
				 						 		try {
				 									onXML(XMLTools.read(strBuf.toString()));
				 								} catch (SAXException e) {}
				 							}
				 							strBuf = new StringBuffer();
				 						}else{
				 							strBuf.append(buf[i]);
				 						}
				 					}
				 				}catch(SocketTimeoutException e) {
				 					onTimeout();
				 				}catch(SocketException e) {
				 					break;
				 				}catch (IOException e) {
				 					close(e);
				 					break;
				 				}
				 				
				 			}
				 		}
				 	});

					writeThread = new Thread(new Runnable(){
				 		public void run(){
				 			MAINROOP:while(connected){
				 				String data;
				 				synchronized (queue) {
				 					while(queue.size() == 0) {
				 						try{
					 						if(!connected){
					 							try {
													socket.close();
												} catch (IOException e2) {}
												break MAINROOP;
					 						}
				 							queue.wait();
				 							//blocking
				 							
				 						}catch(InterruptedException e){
				 							if(!connected) break MAINROOP;
				 						}
				 					}
				 					data = (String)queue.remove(0);
				 				}
				 				
				 				try {
				 					writer.write(data);
				 					writer.write("\0");
				 					writer.flush();
				 				}catch(SocketException e) {
				 					break;
				 				}catch (IOException e) {
				 					close(e);
				 					break;
				 				}
				 			}
				 		}
				 	});
					readThread.start();
					writeThread.start();
					onConnect(true);
				}catch(IOException ecp){
					connected = false;
					onConnect(false);
				}
			}
			
		});
		connectThread.start();
	}
	
	/**
	 * �w�肳�ꂽ address:port �ɐڑ����܂��B
	 * @param address�@�ڑ���z�X�g�A�h���X(��:www.qrone.org)
	 * @param port�@�ڑ���|�[�g(��:9601)
	 */
	public void connect(String address, int port) throws UnknownHostException, IOException{
		socket = new Socket(address,port);
		connect(socket);
	}
	
	/**
	 * �ڑ���ؒf���܂��B�ؒf�������ɂ� onClose() ���Ăяo����܂��B
	 */
	public void close(){
		onClose();
		synchronized(queue){
			connected = false;
			queue.notifyAll();
		}
	}
	
	private void close(Exception e){
		try {
			connected = false;
			writeThread.interrupt();
			socket.close();
		} catch (IOException e2){
			onError(e2);
			onClose();
			return;
		}
		onError(e);
		onClose();
	}
	
	/**
	 * ������ str �𑊎葤�ɑ���܂��B XMLSocket �ʐM�ł� str �͒ʏ� well-formed XML �ł���
	 * �K�v������܂��B
	 * @param str�@���M���镶���� �iXML �ł���ׂ��ł��j
	 */
	public void send(String str){
		if(connected){
			synchronized (queue) {
				queue.add(str);
				queue.notifyAll();
			}
		}
	}

	/**
	 * XML �h�L�������g�𑊎葤�ɑ���܂��B 
	 * @param doc ���M���� XML �h�L�������g
	 */
	public void send(Document doc) throws TransformerException{
		if(connected){
			synchronized (queue) {
				queue.add(XMLTools.write(doc));
				queue.notifyAll();
			}
		}
	}
	/**
	 * XML ��͂��s�����ǂ����̐ݒ�����܂��Btrue �ɂ����ꍇ�ɂ͏�� XML ��͂��s���
	 * �܂����Afalse �ɂ���� XML ��͂��s���Ȃ��Ȃ�AonXML(Document) ���Ăяo��
	 * ��邱�Ƃ��Ȃ��Ȃ�܂��B
	 * @param bool XML ��͂̍s��/�s��Ȃ�
	 */
	public void setXMLParsing(boolean bool){
		parsexml = bool;
	}
	
	/**
	 * �ڑ����s���Ă��� Socket �N���X�̃C���X�^���X��Ԃ��܂��B
	 * @return�@�ڑ����\�P�b�g
	 */
	public Socket getSocket(){
		return socket;
	}
	
	/**
	 * �ڑ��J�n���ɌĂ΂�AXMLSocketListener �ɒʒm���܂��Bsuccess == false 
	 * �̎���<b>�ʐM���m������Ă��܂���B</b><BR>
	 * <BR>
	 * ���̃N���X���p�������N���X�����ꍇ�ɂ͂��̃��\�b�h���p�����邱�Ƃ� onClose(Exception) �C�x���g��
	 * �擾�ł��܂��B
	 * @param success �ڑ��̐���
	 */
	
	protected void onConnect(boolean success){
 		for (Iterator iter = xmllistener.iterator(); iter.hasNext();) {
			((XMLSocketListener)iter.next()).onConnect(success);
		}
	}

	/**
	 * �G���[���o�����ɌĂ΂�AXMLSocketListener �ɒʒm���܂��B<BR>
	 * <BR>
	 * ���̃N���X���p�������N���X�����ꍇ�ɂ͂��̃��\�b�h���p�����邱�Ƃ� onError(Exception) �C�x���g��
	 * �擾�ł��܂��B
	 * @param e�@�G���[
	 */
	protected void onError(Exception e){
		onClose();
		if(e!=null){
			for (Iterator iter = xmllistener.iterator(); iter.hasNext();) {
				((XMLSocketListener)iter.next()).onError(e);
			}
		}
	}

	/**
	 * �ؒf�������ɌĂ΂�AXMLSocketListener �ɒʒm���܂��B<BR>
	 * <BR>
	 * ���̃N���X���p�������N���X�����ꍇ�ɂ͂��̃��\�b�h���p�����邱�Ƃ� onClose() �C�x���g��
	 * �擾�ł��܂�
	 */
	protected void onClose(){
 		for (Iterator iter = xmllistener.iterator(); iter.hasNext();) {
			((XMLSocketListener)iter.next()).onClose();
		}
	}

	/**
	 * �ʐM�^�C���A�E�g���ɌĂ΂�AXMLSocketListener �ɒʒm���܂��B�^�C���A�E�g�͒ʏ�R�O�b���x��
	 * �ݒ肳��Ă��܂��B<BR>
	 * <BR>
	 * ���̃N���X���p�������N���X�����ꍇ�ɂ͂��̃��\�b�h���p�����邱�Ƃ� onTimeout() �C�x���g��
	 * �擾�ł��܂�
	 * @see java.net.Socket#setSoTimeout(int)
	 */
	protected void onTimeout(){
 		for (Iterator iter = xmllistener.iterator(); iter.hasNext();) {
			((XMLSocketListener)iter.next()).onTimeout();
		}
	}
	
	/**
	 * �f�[�^��M���ɌĂ΂�AXMLSocketListener �ɒʒm���܂��B<BR>
	 * <BR>
	 * ���̃N���X���p�������N���X�����ꍇ�ɂ͂��̃��\�b�h���p�����邱�Ƃ� onData(String) �C�x���g��
	 * �擾�ł��܂��B
	 */
	protected void onData(String data){
 		for (Iterator iter = xmllistener.iterator(); iter.hasNext();) {
			((XMLSocketListener)iter.next()).onData(data);
		}
 	}

	/**
	 * �f�[�^��M��̂���� XML ��͌�A�ɌĂ΂�AXMLSocketListener �ɒʒm���܂��B<BR>
	 * <BR>
	 * ���̃N���X���p�������N���X�����ꍇ�ɂ͂��̃��\�b�h���p�����邱�Ƃ� onXML(Document) �C�x���g��
	 * �擾�ł��܂��B���̃C�x���g���擾����ɂ� setXMLParseing(boolean)�@�� true (default) 
	 * ���ݒ肳��Ă���K�v������܂��B
	 */
	protected void onXML(Document doc){
 		for (Iterator iter = xmllistener.iterator(); iter.hasNext();) {
			((XMLSocketListener)iter.next()).onXML(doc);
		}
 	}
	
	/**
	 * �C�x���g�n���h����o�^���܂��B���̃��\�b�h�𗘗p���� XMLSocketListener�@�����������N���X��
	 * �o�^���ăC�x���g���擾�A�K�X�������s���Ă��������B
	 * @param listener�@�C�x���g�n���h��
	 */
	public void addXMLSocketListener(XMLSocketListener listener) {
		xmllistener.add(listener);
	}
	
	/**
	 * �o�^�����C�x���g�n���h�����폜���܂��B
	 * @param listener�@�C�x���g�n���h��
	 */
	public void removeXMLSocketListener(XMLSocketListener listener) {
		xmllistener.remove(listener);
	}
}
