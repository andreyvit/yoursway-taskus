SET	release-file-prefix	yoursway-taskus-[ver]
SET	release-descr-prefix	YourSway Taskus [ver]

SET	CFBundleName	YourSway Taskus
SET	CFBundleShortVersionString	[ver]
SET	CFBundleVersion	[ver]
SET	CFBundleIdentifier	com.yoursway.Taskus
SET	CFBundleGetInfoString	YourSway Taskus [ver]

SET	eclipse-ver	3.4

REPOS	taskus	-	Taskus
	GIT	andreyvit-ys	-	ssh://yoursway.com/~andreyvit/pikachu.git
	GIT	fourdman-ys	-	ssh://yoursway.com/~fourdman/pikachu.git
	GIT	lliypik-ys	-	ssh://yoursway.com/~lliypik/pikachu.git

VERSION	ecabu.cur	ecabu	heads/master
VERSION	updater.cur	updater	heads/master
VERSION	taskus.cur	taskus	heads/tinyupdater
VERSION	libraries.cur	libraries	heads/master
VERSION	commons.cur	commons	heads/master
VERSION	create-dmg.cur	create-dmg	heads/master
VERSION	repackager.cur	repackager	heads/master

SET	nsis-ver	2.35
FILE	nsis.zip	-	nsis-[nsis-ver].zip	megabox-eclipses
NEWDIR	nsis	temp	nsis-[nsis-ver]	-

NEWDIR	build.dir	temp	eclipse-osx-repackager-[ver]-build	-

NEWDIR	taskus.bin	temp	[release-file-prefix]-binaries	[release-descr-prefix] binaries

FILE	eclipse-mac.zip	-	[eclipse-ver]/eclipse-platform-[eclipse-ver]-macosx-carbon.tar.gz	megabox-eclipses
NEWDIR	eclipse-mac	temp	eclipse-platform-[eclipse-ver]-macosx-carbon	-

FILE	launcher-mac.zip	-	[eclipse-ver]/launchers-macosx.carbon.ppc.[eclipse-ver].tar.gz	megabox-eclipses
NEWDIR	launcher-mac	temp	launchers-macosx.carbon.ppc.[eclipse-ver]	-
NEWDIR	launcher-mac.app	temp	eclipse_launcher_mac_bundle-[ver].app	-

FILE	eclipse-win.zip	-	[eclipse-ver]/eclipse-platform-[eclipse-ver]-win32.zip	megabox-eclipses
NEWDIR	eclipse-win	temp	eclipse-platform-[eclipse-ver]-win32	-
FILE	launcher-win.zip	-	[eclipse-ver]/launchers-win32.win32.x86.[eclipse-ver].zip	megabox-eclipses
NEWDIR	launcher-win	temp	launchers-win32.win32.x86.[eclipse-ver]	-

NEWDIR	taskus-mac.app	temp	Taskus [ver].app	Taskus [ver] for Mac OS X (application bundle)
NEWDIR	dmg_temp_dir	temp	taskus-[ver]-dmg-temp-dir	-
NEWFILE	taskus.dmg	featured	[release-file-prefix].dmg	[release-descr-prefix] for Mac OS X

NEWDIR	taskus-win	temp	Taskus [ver]	Taskus [ver] for Windows
NEWFILE	taskus-win.zip	featured	[release-file-prefix]-win.zip	Taskus [ver] for Windows (Zip)
NEWFILE	taskus-winsetup.exe	featured	[release-file-prefix]-setup.exe	Taskus [ver] Setup for Windows

UNZIP	[eclipse-mac.zip]	[eclipse-mac]
	INTO	/	eclipse
UNZIP	[eclipse-win.zip]	[eclipse-win]
	INTO	/	eclipse
	

##############################################################################################################
# build Taskus binaries
##############################################################################################################

INVOKERUBY	[ecabu.cur]/ecabu.rb
	ARGS	--output	[taskus.bin]
	ARGS	--binary	[eclipse-mac]/plugins
	ARGS	--source	[libraries.cur]
	ARGS	--source	[commons.cur]
	ARGS	--source	[updater.cur]
	ARGS	--include-following
	ARGS	--source	[taskus.cur]
	ARGS	--exclude	*tests*
	ARGS	--include	org.eclipse.swt.*
	ARGS	--include	org.eclipse.update.configurator
	ARGS	--include	org.eclipse.core.jobs
	ARGS	--include	com.ibm.icu
	
##############################################################################################################
# build mac & win applications
##############################################################################################################

UNZIP	[launcher-mac.zip]	[launcher-mac]
	INTO	/	eclipse
UNZIP	[launcher-win.zip]	[launcher-win]
	INTO	/	eclipse

INVOKE	[repackager.cur]/EclipseOSXRepackager	[launcher-mac]	[launcher-mac.app]

COPYTO	[taskus-mac.app]
	INTO	/	[launcher-mac.app]
	INTO	Contents/Resources/Java/plugins	[taskus.bin]
	INTO	Contents/Resources/Java/configuration/config.ini	[taskus.cur]/builder/config.ini
	INTO	Contents/Resources/Eclipse.icns	[taskus.cur]/builder/Taskus.icns

COPYTO	[taskus-win]
	INTO	/	[launcher-win]
	INTO	plugins	[taskus.bin]
	INTO	configuration/config.ini	[taskus.cur]/builder/config.ini

INVOKERUBY	[ecabu.cur]/ecabu.rb
	ARGS	--output	[taskus-mac.app<alter>]/Contents/Resources/Java/plugins
	ARGS	--copy-binaries	--disable-building
	ARGS	--binary	[eclipse-mac]/plugins
	ARGS	--source	[libraries.cur]
	ARGS	--source	[commons.cur]
	ARGS	--source	[updater.cur]
	ARGS	--include-following
	ARGS	--source	[taskus.cur]
	ARGS	--exclude	*tests*
	ARGS	--include	org.eclipse.swt.*
	ARGS	--include	org.eclipse.update.configurator
	ARGS	--include	org.eclipse.core.jobs
	ARGS	--include	com.ibm.icu

INVOKERUBY	[ecabu.cur]/ecabu.rb
	ARGS	--output	[taskus-win<alter>]/plugins
	ARGS	--copy-binaries	--disable-building
	ARGS	--binary	[eclipse-win]/plugins
	ARGS	--source	[libraries.cur]
	ARGS	--source	[commons.cur]
	ARGS	--source	[updater.cur]
	ARGS	--include-following
	ARGS	--source	[taskus.cur]
	ARGS	--exclude	*tests*
	ARGS	--include	org.eclipse.swt.*
	ARGS	--include	org.eclipse.update.configurator
	ARGS	--include	org.eclipse.core.jobs
	ARGS	--include	com.ibm.icu

FIXPLIST	[taskus-mac.app<alter>]/Contents/Info.plist
	FIX	CFBundleIdentifier	[CFBundleIdentifier]
	FIX	CFBundleGetInfoString	[CFBundleGetInfoString]
	FIX	CFBundleName	[CFBundleName]
	FIX	CFBundleShortVersionString	[CFBundleShortVersionString]
	FIX	CFBundleVersion	[CFBundleVersion]
	


##############################################################################################################
# tiny updater
##############################################################################################################


NEWDIR	tinyupdater	temp	tinyupdater-[ver]	-

COPYTO	[tinyupdater]
	INTO	tinyupdater.zip	[taskus.bin]/com.yoursway.tinyupdater_1.0.0.jar

# mac ########################################################################################################

NEWDIR	mac-tu-unzipped	temp	tinyupdater-[ver]-mac-unzipped	-

UNZIP	[tinyupdater]/tinyupdater.zip	[mac-tu-unzipped]
	INTO	/	/

SUBSTVARS	[mac-tu-unzipped<alter>]/version.txt
	SET	version	[ver]
	SET	applicationName	Taskus
	SET	platform	mac
	SET	releaseType	continuous
	SET	updateSite	http://updates.yoursway.com/

ZIP	[tinyupdater]/mac.zip
	INTO	/	[mac-tu-unzipped]

COPYTO	[taskus-mac.app]
	INTO	Contents/Resources/Java/plugins/com.yoursway.tinyupdater_1.0.0.jar	[tinyupdater]/mac.zip


# win ########################################################################################################

NEWDIR	win-tu-unzipped	temp	tinyupdater-[ver]-win-unzipped	-

UNZIP	[tinyupdater]/tinyupdater.zip	[win-tu-unzipped]
	INTO	/	/

SUBSTVARS	[win-tu-unzipped<alter>]/version.txt
	SET	version	[ver]
	SET	applicationName	Taskus
	SET	platform	win
	SET	releaseType	continuous
	SET	updateSite	http://updates.yoursway.com/

ZIP	[tinyupdater]/win.zip
	INTO	/	[win-tu-unzipped]

COPYTO	[taskus-win]
	INTO	plugins/com.yoursway.tinyupdater_1.0.0.jar	[tinyupdater]/win.zip



##############################################################################################################
# Mac DMG
##############################################################################################################


COPYTO	[dmg_temp_dir]
	SYMLINK	Applications	/Applications
	INTO	Taskus.app	[taskus-mac.app]

INVOKE	[create-dmg.cur]/create-dmg
	ARGS	--window-size	500	310
	ARGS	--icon-size	96
	ARGS	--background	[taskus.cur]/builder/background.gif
	ARGS	--volname	Taskus [ver]
	ARGS	--icon	Applications	380	205
	ARGS	--icon	Taskus	110	205
	ARGS	[taskus.dmg]
	ARGS	[dmg_temp_dir]

	
##############################################################################################################
# Windows Setup
##############################################################################################################

	
NEWDIR	setup-config	temp	taskus-[ver]-winsetup-config	-
COPYTO	[setup-config]
	INTO	setup.nsi	[taskus.cur]/builder/setup.nsi
	INTO	license.txt	[taskus.cur]/builder/license.txt
	
SUBSTVARS	[setup-config<alter>]/setup.nsi
	SET	LicenseFile	[setup-config]/license.txt
	SET	OutputFile	[taskus-winsetup.exe<nodep>]
	SET	WinVer	[ver].0

NSIS-FILE-LIST	[taskus-win]	[setup-config<alter>]/setup-files.nsi	[setup-config<alter>]/setup-delete.nsi

UNZIP	[nsis.zip]	[nsis]
	INTO	/	nsis-[nsis-ver]

INVOKE	[nsis]/makensis	[setup-config]/setup.nsi
	DEP	[taskus-win]	[setup-config]
	DEP	[taskus-winsetup.exe]	
	
##############################################################################################################
# Publish
##############################################################################################################

	
ZIP	[taskus-win.zip]
	INTO	/	[taskus-win]
	
PUT	megabox-builds	taskus.dmg
PUT	megabox-builds	taskus-winsetup.exe

PUT	s3-builds	taskus.dmg
PUT	s3-builds	taskus-winsetup.exe

PUT	megabox-builds	build.log
PUT	s3-builds	build.log

