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

////////////////////////////////////////////////////////////////////////////////
//
// This module implements the Localization for the JViewer.
//
package com.ami.kvm.jviewer.gui;

import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.JViewer;


/**
 * This class loads the resource bundle which is used to 
 * localize JViewer GUI components to a particular language.
 */
public class LocaleStrings {

	private static LocaleStrings localeStrings = new LocaleStrings();
	private ResourceBundle resourceBundle = null;
	private ResourceBundle resourceBundleSOC = null;
	private ResourceBundle resourceBundleOEM = null;
	private ResourceBundle prevresourceBundle = null;
	private ResourceBundle prevresourceBundleSOC = null;
	private ResourceBundle prevresourceBundleOEM = null;
	private ResourceBundle parentResourceBundle = null;
	private ResourceBundle parentResourceBundleSOC = null;
	private ResourceBundle parentResourceBundleOEM = null;
	private String errorMsg = "";

	/**
	 * LocaleStrings constructor.
	 * Loads the default language resource bundle(English). 
	 */
	private LocaleStrings() {
		parentResourceBundle = ResourceBundle.getBundle("com.ami.kvm.jviewer.lang.Resources_EN");
		parentResourceBundleSOC = ResourceBundle.getBundle("com.ami.kvm.jviewer.soc.lang.SOCResources_EN");
		try{
			parentResourceBundleOEM = ResourceBundle.getBundle("com.ami.kvm.jviewer.oem.lang.OEMResources_EN");
		}catch(Exception e){
			parentResourceBundleOEM = null;
		}
		resourceBundle = parentResourceBundle;
		resourceBundleSOC = parentResourceBundleSOC;
		resourceBundleOEM = parentResourceBundleOEM;
		prevresourceBundle = resourceBundle;
		prevresourceBundleSOC = resourceBundleSOC;
		prevresourceBundleOEM = resourceBundleOEM;
	}

	/**
	 * Sets the language ID for JViewer.
	 * Loads the resource bundle corresponding to the specified language. 
	 * @param str - language ID
	 */
	public static void setLanguageID(String str)
	{
		if(str.length() == 0 || str.equals(null))
			return;
		//Try to load the resource bundle from the core package.
		try{
			localeStrings.prevresourceBundle = localeStrings.resourceBundle;
			localeStrings.resourceBundle = ResourceBundle.getBundle("com.ami.kvm.jviewer.lang.Resources_"+str);
		}catch(MissingResourceException mre){
			Debug.out.println(mre+getString("AC_2_LS"));
			//If exception occurs while loading the requested langugage resource bundle,
			//set the default language resource bundle.
			localeStrings.resourceBundle = ResourceBundle.getBundle("com.ami.kvm.jviewer.lang.Resources_EN");
		}
		//If the language resource bundle is not available in core try to load it from OEM package
		try{
			localeStrings.prevresourceBundleOEM = localeStrings.resourceBundleOEM;
			localeStrings.resourceBundleOEM = ResourceBundle.getBundle("com.ami.kvm.jviewer.oem.lang.OEMResources_"+str);
		}catch(MissingResourceException mre){
			Debug.out.println(mre+getString("AC_2_LS"));
			//If exception occurs while loading the requested langugage resource bundle,
			//set the default language resource bundle.
			localeStrings.resourceBundle = ResourceBundle.getBundle("com.ami.kvm.jviewer.oem.lang.Resources_EN");
			localeStrings.resourceBundleSOC = ResourceBundle.getBundle("com.ami.kvm.jviewer.soc.lang.SOCResources_EN");
			localeStrings.resourceBundleOEM = ResourceBundle.getBundle("com.ami.kvm.jviewer.oem.lang.OEMResources_EN");
			localeStrings.errorMsg = getString("AC_4_LS");
			JOptionPane.showMessageDialog(null, localeStrings.errorMsg, getString("AC_1_LS"), JOptionPane.ERROR_MESSAGE);
			JViewer.setLanguage(JViewer.DEFAULT_LOCALE);
			/* For disabling language combo box under Standalone Application input dialog */
			JViewer.setDefaultLang(true);
		}

		try{
			localeStrings.prevresourceBundleSOC = localeStrings.resourceBundleSOC;
			localeStrings.resourceBundleSOC = ResourceBundle.getBundle("com.ami.kvm.jviewer.soc.lang.SOCResources_"+str);
		}catch(MissingResourceException mre){
			Debug.out.println(mre+getString("AC_3_LS"));
			//If exception occurs while loading the requested langugage resource bundle,
			//set the default language resource bundle.
			localeStrings.resourceBundleSOC = ResourceBundle.getBundle("com.ami.kvm.jviewer.soc.lang.SOCResources_EN");
		}
	}

	/**
	 * Gets the string corresponding to the given key from the string table of the
	 * resource bundle for a particular language.  
	 * @param key - the sting keys
	 * @return - the localized string.
	 */
	public static String getString(String key)
	{
		String value = LocaleStrings.getOEMString(key);
		if(value != null)
			return value;
		if(containsKey(localeStrings.resourceBundle, key)){
			return localeStrings.resourceBundle.getString(key);
		}
		else{
			String resBundle = localeStrings.resourceBundle.toString();
			Debug.out.printError("The resource for the key "+key+
					" is missing in the resource bundle "+
					resBundle.substring(0, resBundle.indexOf('@')));
			if(containsKey(localeStrings.parentResourceBundle, key))
				return localeStrings.parentResourceBundle.getString(key);
			else{
				resBundle = localeStrings.parentResourceBundle.toString();
				Debug.out.printError("The resource for the key "+key+
						" is missing in the resource bundle "+
						resBundle.substring(0, resBundle.indexOf('@')));
				return null;
			}
		}
	}

	/**
	* Gets the key corresponding to the given text from the string table of the
	* resource bundle for a particular language.
	* @param ResourceBundle - ResourceBundle resource
	* @param text - the string text
	* @return key - the string keys.
	*/
	private static String convertResourceBundleToProperties(ResourceBundle resource ,String text) {

		Properties properties = new Properties();
		Enumeration<String> keys = resource.getKeys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			if(text.equalsIgnoreCase(resource.getString(key)))
			{
				return key;
			}
		}
		return null;
	}

	/**
	* Gets the key corresponding to the given text from the string table of the
	* resource bundle for a particular language.
	* @param text - the string text
	* @return key - the string keys.
	*/
	public static String getStringKey(String text){

		String key = null;
		try{
			//Check in OEM bundle first
			key = convertResourceBundleToProperties(localeStrings.prevresourceBundleOEM,text);
			//If not available in OEM check in the resource bundle in the SOC package 
			if(key == null){
				key = convertResourceBundleToProperties(localeStrings.prevresourceBundleSOC,text);
				//If not available in SOC check in the resource bundle in the common package 
				if(key == null){
					key = convertResourceBundleToProperties(localeStrings.prevresourceBundle,text);
				}
			}
			
		}catch (MissingResourceException mre) {
			Debug.out.println(mre);
			key = convertResourceBundleToProperties(localeStrings.parentResourceBundleOEM,text);
			if(key == null){
				key = convertResourceBundleToProperties(localeStrings.parentResourceBundleSOC,text);
				if(key == null){
					key = convertResourceBundleToProperties(localeStrings.parentResourceBundle,text);
				}
			}
		}
		return key;
	}
	
	/**
	 * Gets the string corresponding to the given key from the string table of the
	 * previously loaded resource bundle.  
	 * @param key - the sting keys
	 * @return - the localized string.
	 */
	public static String getPreviousLocaleString(String key){
		String prevLocaleString = null;
		prevLocaleString = localeStrings.prevresourceBundle.getString(key);
		return prevLocaleString;
	}
	/**
	 * Gets the string corresponding to the given key from the string table of the
	 * resource bundle for a particular language for the SOC package.  
	 * @param key - the sting keys
	 * @return - the localized string.
	 */
	public static String getSOCString(String key)
	{
		String socString = getOEMString(key);
		if(socString == null){
			if(containsKey(localeStrings.resourceBundleSOC, key)){
				socString = localeStrings.resourceBundleSOC.getString(key);
			}
			else{
				String resBundle = localeStrings.resourceBundleSOC.toString();
				Debug.out.printError("The resource for the key "+key+
						" is missing in the resource bundle "+
						resBundle.substring(0, resBundle.indexOf('@')));
				if(containsKey(localeStrings.parentResourceBundleSOC, key))
					socString = localeStrings.parentResourceBundleSOC.getString(key);
				else{
					resBundle = localeStrings.parentResourceBundleSOC.toString();
					Debug.out.printError("The resource for the key "+key+
							" is missing in the resource bundle "+
							resBundle.substring(0, resBundle.indexOf('@')));
					socString = null;
				}
			}
		}
		return socString;
	}
	
	/**
	 * Gets the string corresponding to the given key from the string table of the
	 * resource bundle for a particular language for the OEM package.  
	 * @param key - the sting keys
	 * @return - the localized string.
	 */
	public static String getOEMString(String key)
	{
		if(localeStrings.resourceBundleOEM == null)
			return null;

		if(containsKey(localeStrings.resourceBundleOEM, key)){
			return localeStrings.resourceBundleOEM.getString(key);
		}
		else{
			String resBundle = localeStrings.resourceBundleOEM.toString();
			Debug.out.printError("The resource for the key "+key+
					" is missing in the resource bundle "+
					resBundle.substring(0, resBundle.indexOf('@')));
			if(containsKey(localeStrings.parentResourceBundleOEM, key))
				return localeStrings.parentResourceBundleOEM.getString(key);
			else{
				resBundle = localeStrings.parentResourceBundleOEM.toString();
				Debug.out.printError("The resource for the key "+key+
						" is missing in the resource bundle "+
						resBundle.substring(0, resBundle.indexOf('@')));
				return null;
			}
		}
	}

	/**
	 * Checks whether the given ResourceBundle contains a particular key. 
	 * @param rb - the ResourceBundle object to be searched.
	 * @param key - the key key to be searched for.
	 * @return true - if the key is found in the ResourceBundle.<br>
	 *			false - if the key is not found in the ResourceBundle.
	 */
	private static boolean containsKey(ResourceBundle rb, String key){
		boolean contains = false;
		if(rb == null || key == null)
			return contains;
		Enumeration<String> keys = rb.getKeys();
		while(keys.hasMoreElements()){
			if((keys.nextElement()).equalsIgnoreCase(key)){
				contains = true;
				break;
			}
		}
		return contains;
	}
}
