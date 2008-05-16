package com.mkalugin.keychain;

public class KeyChain {

	static {
        System.loadLibrary("keychain-macosx");
    }
    
    public static native void StorePasswordInKeychain(String appName, String login, String password);
    
    public static native String GetPasswordFromKeychain(String appName, String login);
	
}
