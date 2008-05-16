#include "keychain.h"

#import <CoreFoundation/CoreFoundation.h> 
#import <Security/Security.h> 
#import <CoreServices/CoreServices.h>

JNIEXPORT void JNICALL Java_com_mkalugin_keychain_KeyChain_StorePasswordInKeychain
  (JNIEnv * env, jclass klass, jstring appName, jstring login, jstring password) 
{
	const char *c_login =  (*env)->GetStringUTFChars(env, login, NULL);
	const char *c_password =  (*env)->GetStringUTFChars(env, password, NULL);
	const char *c_app_name = (*env)->GetStringUTFChars(env, appName, NULL);

	// Determine whether password exists in Keychain
	OSStatus status = SecKeychainAddGenericPassword (nil, strlen (c_app_name), c_app_name, strlen (c_login), 
			c_login, strlen (c_password), c_password, nil);
			
	// If already exists
	if (status == errSecDuplicateItem)
	{
		UInt32 existingPasswordLength;
		char *existingPassword;
		SecKeychainItemRef existingItemRef;

		// ... get the existing password and a reference to the existing keychain item, then ...
		status = SecKeychainFindGenericPassword (nil, strlen (c_app_name), c_app_name, strlen (c_login), c_login, 
			&existingPasswordLength, (void **) &existingPassword, &existingItemRef);
		
		// ... modify the password for the existing keychain item;  (I'll admit to being mystified as to how this function works;  
		// how does it know that it's the password data that's being modified??;  anyway, it seems to work); and finally...
		// Answer: the data of a keychain item is what is being modified.  In the case of internet or generic passwords, 
		// the data is the password.  For a certificate, for example, the data is the certificate itself.
		
		status = SecKeychainItemModifyContent (existingItemRef, NULL, strlen (c_password), c_password);
		
		// ...free the memory allocated in call to SecKeychainFindGenericPassword() above
		SecKeychainItemFreeContent(NULL, existingPassword);
		CFRelease(existingItemRef);
		return;
	}
	
	else if (status == noErr)
		return;
	
	jclass newExcCls;
	(*env)->ExceptionDescribe(env);
	(*env)->ExceptionClear(env);
	newExcCls = (*env)->FindClass(env, "java/lang/RuntimeException");
	if (newExcCls == NULL) {
		/* Unable to find the exception class, give up. */
		return;
	}
	(*env)->ThrowNew(env, newExcCls, "SecKeychainAddGenericPassword failed.");
}

JNIEXPORT jstring JNICALL Java_com_mkalugin_keychain_KeyChain_GetPasswordFromKeychain
  (JNIEnv * env, jclass klass, jstring appName, jstring login) 
{

	const char *c_login = (*env)->GetStringUTFChars(env, login, NULL);
	char *passwordFromKeychain;
	UInt32 passwordLength;
	const char *c_app_name = (*env)->GetStringUTFChars(env, appName, NULL);;
	
	jstring returnValue;
	
	// Determine whether password exists in Keychain	
	OSStatus status = SecKeychainFindGenericPassword (nil, strlen (c_app_name), c_app_name, strlen (c_login), 
		c_login, &passwordLength, (void **) &passwordFromKeychain, nil); 

	switch (status) {
		case noErr:
			if (passwordFromKeychain [passwordLength] != 0)
			{
				// Don't trust memory allocated from system, copy it over
				// First before making it a CString 
				char *buffer = (char *) malloc ((passwordLength + 1) * sizeof (char));
				strncpy (buffer, passwordFromKeychain, passwordLength);
				buffer[passwordLength] = '\0';
				
				returnValue = (*env)->NewStringUTF(env, buffer); 
				free(buffer);			
			} else { 
				returnValue =  (*env)->NewStringUTF(env, passwordFromKeychain);
			} 
			
			SecKeychainItemFreeContent(NULL, passwordFromKeychain);
			return returnValue;		
			break;
			
		case errSecItemNotFound:
			return 0;
			
		case errSecAuthFailed:
			return 0;
			
		case errSecNoDefaultKeychain:
			return 0;
			
		default:
			return 0;
    }
}
