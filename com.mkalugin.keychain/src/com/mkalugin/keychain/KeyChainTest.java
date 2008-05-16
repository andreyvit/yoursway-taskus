package com.mkalugin.keychain;


public class KeyChainTest {

	public static void putAndAsk() {
		KeyChain.StorePasswordInKeychain("foo", "bar", "pass123");
		String pass = KeyChain.GetPasswordFromKeychain("foo", "bar");
		if (!pass.equals("pass123")) 
			throw new AssertionError();
	}
	
	public static void askWrong() {
		String pass = KeyChain.GetPasswordFromKeychain("foo", "bar2");
		if (pass != null)
			throw new AssertionError();
	}
	
	public static void main(String[] args) {
		putAndAsk();
		askWrong();
	}
	
}
