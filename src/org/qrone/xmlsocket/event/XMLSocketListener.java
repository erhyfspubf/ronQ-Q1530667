package org.qrone.xmlsocket.event;

import org.w3c.dom.Document;

/**
 * XMLSocket �p�C�x���g�n���h���C���^�[�t�F�[�X<BR>
 * <BR>
 * �K�X���̃C���^�[�t�F�[�X�����������N���X�����AXMLSocket.addXMLSocketListener(XMLSocketListener)
 * �œo�^���C�x���g���擾���Ă��������B<BR>
 * <BR>
 * ���̃C���^�[�t�F�[�X�� Macromedia Flash �� ActiveScript �I�u�W�F�N�g XMLSocket �Ƃł������
 * �����ɍ���Ă���܂��B
 * 
 * @author J.Tabuchi
 * @since 2005/8/6
 * @version 1.0
 * @link QrONE Technology : http://www.qrone.org/
 */
public interface XMLSocketListener {
	/**
	 * �ڑ��J�n���ɌĂ΂�܂��B success == false �̎���<b>�ڑ��Ɏ��s���Ă��܂��B</b>�\�����ӂ��Ă��������B
	 * @param success�@�ڑ��J�n����
	 */
	public void onConnect(boolean success);
	/**
	 * �ڑ��I�����ɌĂ΂�܂��B�ʏ�I�����݂̂Ȃ炸�A�G���[�I�����ɂ��K���Ă΂�܂��B
	 */
	public void onClose();
	/**
	 * �G���[���ɂ̂݌Ă΂�܂��B���R�ƂȂ��� Exception ��Ԃ��܂��B
	 * @param e �G���[
	 */
	public void onError(Exception e);
	/**
	 * �ʐM�̃^�C���A�E�g���ɌĂ΂�܂��B��莞�Ԉȏ�ʐM���s���Ă��Ȃ��ꍇ�ɌĂ΂��\��������܂����A
	 * �ݒ�ɂ���Ă͂܂������Ăяo����܂���B
	 */
	public void onTimeout();
	/**
	 * �f�[�^����M�������AXML ��͂��s����O�ɌĂяo����܂��B
	 * @param data
	 */
	public void onData(String data);
	/**
	 * �f�[�^��M�̌�A XML ��͂�������ŌĂяo����܂��B DOM �𗘗p�������ꍇ�ɂ͉�͌�̂�����𗘗p��
	 * ��ƊȒP�ł悢�ł��傤�B
	 * @param doc
	 */
	public void onXML(Document doc);
}
