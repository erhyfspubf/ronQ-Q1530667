package org.qrone.xmlsocket.event;

import org.qrone.xmlsocket.XMLSocket;

/**
 * XMLServerSocket �p�C�x���g�n���h���C���^�[�t�F�[�X<BR>
 * <BR>
 * �K�X���̃C���^�[�t�F�[�X�����������N���X�����AXMLServerSocket.addXMLServerSocketListener(XMLSocketListener)
 * �œo�^���C�x���g���擾���Ă��������B
 * 
 * @author J.Tabuchi
 * @since 2005/8/6
 * @version 1.0
 * @link QrONE Technology : http://www.qrone.org/
 */
public interface XMLSocketServerListener {
	/**
	 * �T�[�o�[�J�n���ɌĂ΂�܂��B success == false �̎���<b>�J�n�Ɏ��s���Ă��܂��B</b>�\�����ӂ��Ă��������B
	 * @param success �T�[�o�[�J�n����
	 */
	public void onOpen(boolean success);
	/**
	 * �T�[�o�[�I�����ɌĂ΂�܂��B�ʏ�I�����݂̂Ȃ炸�A�G���[�I�����ɂ��K���Ă΂�܂��B
	 */
	public void onClose();
	/**
	 * �G���[�I�����ɂ̂݌Ă΂�܂��B�I�����R�ƂȂ����v���I Exception ��Ԃ��܂��B
	 * @param e �ؒf���R�G���[
	 */
	public void onClose(Exception e);
	/**
	 * �V���� Macromedia Flash �� .swf �t�@�C������ XMLSocket �ʐM��v�����ꂽ����
	 * �Ăяo����Aswf �t�@�C���ƒʐM���m�����钼�O�� XMLSocket �I�u�W�F�N�g���n����܂��B<BR>
	 * @param xmlsocket�@�ڑ����m������ XMLSocket �I�u�W�F�N�g
	 */
	public void onNewClient(XMLSocket xmlsocket);
}
