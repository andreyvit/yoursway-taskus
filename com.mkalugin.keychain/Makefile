
#SWT_DEBUG = -g
ARCHS = -arch i386 -arch ppc
CFLAGS = -c -Wall $(ARCHS) $(SWT_DEBUG) -DUSE_ASSEMBLER -I /System/Library/Frameworks/JavaVM.framework/Headers
LFLAGS = -bundle $(ARCHS) -framework JavaVM -framework Foundation -framework Security
OUTPUT=libkeychain-macosx.jnilib
OBJECTS=keychain.o

.c.o:
	cc $(CFLAGS) $*.c

$(OUTPUT): $(OBJECTS)
	cc -o $(OUTPUT) $(LFLAGS) $(OBJECTS)

headers:
	javah -classpath bin -o keychain.h com.mkalugin.keychain.KeyChain
