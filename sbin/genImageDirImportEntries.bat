echo off
rem <h4>FeatureDomain:</h4>
rem     ImageImport
rem <h4>FeatureDescription:</h4>
rem     job for iterating directories and extract first/lastdate from imagefiles<br>
rem     generates importcommands for processing in ImageImportEditor.html
rem <h4>Example:</h4>
rem     d:\public_projects\MatImageImportEditor\sbin\genImageDirImportEntries.bat D:\Bilder\digifotos\test > d:\tmp\importDigiFotos-test.json
rem 
rem @package de.mat.utils.imageimporteditor
rem @author Michael Schreiner <ich@michas-ausflugstipps.de>
rem @category imagemanagement
rem @copyright Copyright (c) 2005, Michael Schreiner
rem @license http://mozilla.org/MPL/2.0/ Mozilla Public License 2.0


rem set mypath
set BASEPATH=%~dp0

rem set cmd
set CP="%BASEPATH%..\target\matimageimporteditor-1.0-SNAPSHOT-jar-with-dependencies.jar"
set JAVAOPTIONS=-Xmx512m -Xms128m -Dlog4j.configuration=file:%BASEPATH%..\config\log4j_ImageDirImportEntryGenerator.properties
set PROG=de.mat.utils.imageimporteditor.ImageDirImportEntryGenerator

rem run cmd
set CMD=java %JAVAOPTIONS% -cp %CP% %PROG% %1
rem echo %CMD%
%CMD%


