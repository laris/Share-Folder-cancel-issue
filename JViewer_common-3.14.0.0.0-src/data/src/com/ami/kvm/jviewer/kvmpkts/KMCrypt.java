/****************************************************************
 **                                                            **
 **    (C) Copyright 2006-2009, American Megatrends Inc.       **
 **                                                            **
 **            All Rights Reserved.                            **
 **                                                            **
 **        5555 Oakbrook Pkwy Suite 200, Norcross,             **
 **                                                            **
 **        Georgia - 30093, USA. Phone-(770)-246-8600          **
 **                                                            **
****************************************************************/

///////////////////////////////////////////////////////////////////////////////
//
// Blowfish wrapper module.
// This module implements a wrapper for keyboard/mouse encryption.
// This is a singleton class
//

package com.ami.kvm.jviewer.kvmpkts;

import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.ami.kvm.jviewer.Debug;

/**
 * KMCrypt class
 */
public class KMCrypt {

	public static final int ENCRYPT = Cipher.ENCRYPT_MODE;
	public static final int DECRYPT = Cipher.DECRYPT_MODE;
	public static final int PLACE_HOLDER_SIZE		= 16;

	private Cipher m_bf = null;

	/**
	 * Initialize crypt
	 *
	 * @param ph place holder.
	 * @param token compression token.
	 * @param tag predefined tag - Ex. "$AMI$".
	 * @param mode crypt mode - ENCRYPT/DECRYPT
	 * @return 0 if initialized, -1 otherwise.
	 */
	public int initialize(String token, int mode) {

		byte[] key = new byte[16];
		byte[] tempkey=token.getBytes();

		if(tempkey.length < 16) {
			for(int i=0;i<tempkey.length;i++)
				key[i]=tempkey[i];
		} else
			key = token.getBytes();

		if (key == null) {
			return -1;
		}

		SecretKeySpec skeySpec = new SecretKeySpec(key, "Blowfish");
		try {
			m_bf = Cipher.getInstance("Blowfish/ECB/NoPadding");
			m_bf.init(mode, skeySpec);
		} catch (Exception e) {
			Debug.out.println("Crypt initialization failed\n");
			Debug.out.println(e);
			return -1;
		}
		return 0;
	}

	/**
	 * Encrypt given buffer.
	 *
	 * @param buf input buffer to be encrypted.
	 * @return encrypted buffer
	 */
	public byte[] encrypt(byte[] buf) {

		byte[] inbuf = pad(buf);
		byte[] encbuf;
		try {
			encbuf = m_bf.doFinal(inbuf);
		} catch (Exception e) {
			Debug.out.println(e);
			Debug.out.println("Encryption failed\n");
			return null;
		}
		return encbuf;
	}

	/**
	 * Decrypt given buffer.
	 *
	 * @param buf input buffer to be decrypted.
	 * @return decrypted buffer
	 */
	public byte[] decrypt(byte[] buf) {

		byte[] decbuf;
		try {
			decbuf = m_bf.doFinal(buf);
		} catch (Exception e) {
			Debug.out.println("Decryption failed\n");
			Debug.out.println(e);
			return null;
		}
		return decbuf;
	}

	/**
	 * Close crypt
	 */
	public void close() {

		m_bf = null;
	}

	/**
	 * Check if encryption initialized
	 *
	 * @return true if initialized, false otherwise
	 */
	public boolean isInitialized() {

		if (m_bf == null) {
			return false;
		}
		return true;
	}

	/*
	 * Pad the buffer to make it multiple of 8.
	 *
	 * @param buf buffer to be padded.
	 * @return padded buffer.
	 */
	private byte[] pad(byte[] buf) {

		if ((buf.length % 8) == 0) {
			return buf;
		}

		byte[] padbuf = new byte[buf.length + (8 - (buf.length % 8))];
		Arrays.fill(padbuf, (byte)0);
		System.arraycopy(buf, 0, padbuf, 0, buf.length);
		return padbuf;
	}
}
