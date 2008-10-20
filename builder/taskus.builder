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
REPOS	updater	-	Software Update
	GIT	lliypik-ys	-	ssh://yoursway.com/~lliypik/autoupdate.git
	GIT	lliypik-gh	-	git://github.com/lliypik/yoursway-software-update.git
	GIT	andreyvit-gh	-	git://github.com/andreyvit/yoursway-software-update.git
REPOS	magicecabu	-	Magic Ecabu
	GIT	andreyvit-ys	-	ssh://yoursway.com/~andreyvit/magicecabu.git

VERSION	ecabu.cur	ecabu	heads/master
VERSION	updater.cur	updater	heads/master
VERSION	taskus.cur	taskus	heads/master
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
# update site
##############################################################################################################

VERSION	magicecabu.cur	magicecabu	heads/master

NEWDIR	mae	temp	taskus-mae	-

SET	MAE_DIR	[mae<mkdir,keep>]
SET	MAE_DEF_DIR	[taskus.cur]/builder/mae-def

SYNC	mae	s3-updates
	MAP	catalog	add,remove	catalog	readonly

INVOKE	[magicecabu.cur]/bin/mae-create-tree	taskus/mac/[ver]	[taskus-mac.app]
	DEP	[mae<alter>]
INVOKE	[magicecabu.cur]/bin/mae-create-tree	taskus/win/[ver]	[taskus-win]
	DEP	[mae<alter>]
INVOKE	[magicecabu.cur]/bin/mae-create-tree	taskus-extinstaller/win/[ver]	[extinstaller.win]
	DEP	[mae<alter>]
INVOKE	[magicecabu.cur]/bin/mae-create-tree	taskus-extinstaller/mac/[ver]	[extinstaller.mac]
	DEP	[mae<alter>]

INVOKE	[magicecabu.cur]/bin/mae-pack-tree	taskus/mac/[ver]	taskus/win/[ver]
	DEP	[mae<alter>]
INVOKE	[magicecabu.cur]/bin/mae-pack-tree	taskus-extinstaller/win/[ver]	taskus-extinstaller/mac/[ver]
	DEP	[mae<alter>]

INVOKE	[magicecabu.cur]/bin/mae-create-version	taskus/mac/[ver]
	DEP	[mae<alter>]
INVOKE	[magicecabu.cur]/bin/mae-create-version	taskus/win/[ver]
	DEP	[mae<alter>]
INVOKE	[magicecabu.cur]/bin/mae-create-version	taskus-extinstaller/win/[ver]
	DEP	[mae<alter>]
INVOKE	[magicecabu.cur]/bin/mae-create-version	taskus-extinstaller/mac/[ver]
	DEP	[mae<alter>]

INVOKE	[magicecabu.cur]/bin/mae-promote-component	taskus	mac	taskus/mac/[ver]	continuous
	DEP	[mae<alter>]
INVOKE	[magicecabu.cur]/bin/mae-promote-component	taskus	win	taskus/win/[ver]	continuous
	DEP	[mae<alter>]
INVOKE	[magicecabu.cur]/bin/mae-promote-component	taskus	mac	taskus-extinstaller/mac/[ver]	continuous
	DEP	[mae<alter>]
INVOKE	[magicecabu.cur]/bin/mae-promote-component	taskus	win	taskus-extinstaller/win/[ver]	continuous
	DEP	[mae<alter>]

INVOKE	[magicecabu.cur]/bin/mae-release-product-version	-f	taskus	taskus	mac	[ver]	continuous
	DEP	[mae<alter>]
INVOKE	[magicecabu.cur]/bin/mae-release-product-version	-f	taskus	taskus	win	[ver]	continuous
	DEP	[mae<alter>]

SYNC	mae	s3-updates
	MAP	/	readonly	/	add
	MAP	products	readonly	products	add,append
	MAP	suites	readonly	suites	add,append


##############################################################################################################
# tiny updater
##############################################################################################################


UNZIP	[taskus-mac.app]


COPYTO	[]

[taskus.cur]/builder/setup.nsi


SUBSTVARS	[taskus.cur]/builder/setup.nsi
	SET	Version	[ver]
	SET	ApplicationName	taskus
	SET	Platform	mac
	SET	ReleaseType	continuous
	SET	Link	http://updates.yoursway.com/



	
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

