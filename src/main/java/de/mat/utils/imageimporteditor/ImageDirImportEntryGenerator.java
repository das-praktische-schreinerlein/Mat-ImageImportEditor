/**
 * <h4>FeatureDomain:</h4>
 *     imagemanagement
 *
 * <h4>FeatureDescription:</h4>
 *     software for management of imagefiles
 * 
 * @author Michael Schreiner <ich@michas-ausflugstipps.de>
 * @category imagemanagement
 * @copyright Copyright (c) 2005-2014, Michael Schreiner
 * @license http://mozilla.org/MPL/2.0/ Mozilla Public License 2.0
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package de.mat.utils.imageimporteditor;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

/**
 * <h4>FeatureDomain:</h4>
 *     ImageImport
 * <h4>FeatureDescription:</h4>
 *     job for iterating directories and extract first/lastdate from imagefiles<br>
 *     generates importcommands for processing in ImageImportEditor.html
 * 
 * @package de.mat.utils.imageimporteditor
 * @author Michael Schreiner <ich@michas-ausflugstipps.de>
 * @category imagemanagement
 * @copyright Copyright (c) 2005-2014, Michael Schreiner
 * @license http://mozilla.org/MPL/2.0/ Mozilla Public License 2.0
 */
public class ImageDirImportEntryGenerator {
    
    /**
     * <h4>FeatureDomain:</h4>
     *     ImageImport
     * <h4>FeatureDescription:</h4>
     *     class with data of imagedir-entries
     * 
     * @package de.mat.utils.imageimporteditor
     * @author Michael Schreiner <ich@michas-ausflugstipps.de>
     * @category imagemanagement
     * @copyright Copyright (c) 2005, Michael Schreiner
     * @license http://mozilla.org/MPL/2.0/ Mozilla Public License 2.0
     */
    public class ImageDirImportEntry {
        public Date firstDate, lastDate;
        public String dir, dateStr, location;

        public ImageDirImportEntry(String dir, String dateStr, Date firstDate, Date lastDate, 
            String location) {
               super();
               this.firstDate = firstDate;
               this.lastDate = lastDate;
               this.dir = dir;
               this.dateStr = dateStr;
               this.location = location;
        }
        
        
    }

    // Logger
    private static final Logger LOGGER =
        Logger.getLogger(ImageDirImportEntryGenerator.class);
    
    // helper
    SimpleDateFormat dfDateTime = 
        new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    SimpleDateFormat dfDate = 
        new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    public ImageDirImportEntryGenerator() {
    }
    
    /**
     * <h4>FeatureDomain:</h4>
     *     FileTools
     * <h4>FeatureDescription:</h4>
     *     iterates over the directories in lstSrcDirs, extracts all 
     *     subDirectories, add them to lstResDirs and if flgRecurse is set, 
     *     scan them recursively too
     * <h4>FeatureResult:</h4>
     *   <ul>
     *     <li>updates parameter lstResDirs - list of full dir-path
     *   </ul> 
     * <h4>FeatureKeywords:</h4>
     *     DirList
     * @param lstResDirs - List to add directoryentries
     * @param lstSrcDirs - List of directoryentries to scan
     * @param flgRecurse - scan recursivly
     * @param node - node for output recursively
     * @param oOptions - options for output (formatter)
     */
    public void scanForSubDirs(List<File> lstResDirs, List<File> lstSrcDirs, boolean flgRecurse) {
        // iterate srcDirs
        for (File srcDir: lstSrcDirs) {
            // read dir
            File[] lstFiles = srcDir.listFiles();
            
            // iterate files
            for (File file: lstFiles) {
                // check if file is a directory
                if (file.isDirectory()) {
                    // add to resultlist
                    lstResDirs.add(file);
                    
                    // read subdirs if flgRecurse is set
                    if (flgRecurse) {
                        this.scanForSubDirs(lstResDirs, 
                                        Collections.singletonList(file), flgRecurse);
                    }
                }
            }
        }
    }

    /**
     * <h4>FeatureDomain:</h4>
     *     ImageImport
     * <h4>FeatureDescription:</h4>
     *     scans the directory baseDirName recursively for subdirs and imagefiles
     *     and creates an ImageDirImportEntry
     * <h4>FeatureResult:</h4>
     *   <ul>
     *     <li>returnValue ImageDirImportEntry - the generated ImageDirImportEntry
     *   </ul> 
     * <h4>FeatureKeywords:</h4>
     *     FileTool ImageTools
     * @param subDir - name of the dir to scan
     * @return - an ImageDirImportEntry for the directory
     */
    public ImageDirImportEntry scanForImages(File subDir) {

        if (LOGGER.isDebugEnabled())
            LOGGER.debug("SubDir:" + subDir);

        // extract dirname
        String dirName = subDir.getName();
        Date firstDate = null;
        Date lastDate = null;

        // read jpg-Files
        File[] arrImgFiles = subDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if (name.toLowerCase().endsWith(".jpg")) {
                    return true;
                }
                return false;
            }
        });
        
        // iterate files
        for (File imgFile : arrImgFiles) {
            // System.out.println("File:" + imgFile);

            // LastModified der Datei einlesen
            Date imageDate = new Date(imgFile.lastModified());

            // read Exif
            try {
                Metadata metadata = JpegMetadataReader.readMetadata(imgFile);

                // obtain the Exif directory
                ExifSubIFDDirectory directory = 
                                metadata.getDirectory(ExifSubIFDDirectory.class);

                // query the tag's value
                imageDate = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
            } catch (Exception ex) {
                //exception -> /dev/null
            }

            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Image: "+ imgFile.getAbsolutePath() + "\t" + dfDateTime.format(imageDate));

            // set dirdate
            if ((firstDate == null) || (firstDate.after(imageDate))) {
                firstDate = imageDate;
            }
            if ((lastDate == null) || (lastDate.before(imageDate))) {
                lastDate = imageDate;
            }
        }

        // extract data from dirname
        String location = dirName;
        Matcher matcher;
        String dateStr = "";
        if ((matcher = Pattern.compile("(\\d\\d\\d\\d)(\\d\\d)(\\d\\d)-(.*)?")
                .matcher(dirName)).matches()) {
            // dirname contains date
            dateStr = matcher.group(3) + "." + matcher.group(2) + "."
                    + matcher.group(1);
            location = matcher.group(4);
        } else if (firstDate != null) {
            // use data of first image
            dateStr = dfDate.format(firstDate);
        }

        // normalize location
        String[] worte = location.split("-");
        location = "";
        for (int i = 0; i < worte.length; ++i) {
            // iterate words
            String wort = worte[i];
            wort = wort.substring(0, 1).toUpperCase()
                    + wort.substring(1);
            location = location.concat(wort).concat(" ");
        }
        location = location.trim();
        
        // create ImageDirImportEntry
        ImageDirImportEntry iDIE = new ImageDirImportEntry(
                        subDir.getAbsolutePath(), dateStr, firstDate, lastDate, 
                        location);
        
        return iDIE;
    }
    
    /**
     * <h4>FeatureDomain:</h4>
     *     ImageImport
     * <h4>FeatureDescription:</h4>
     *     scans the directory baseDirName recursively for subdirs and imagefiles
     *     and returns ImageDirImportEntries
     * <h4>FeatureResult:</h4>
     *   <ul>
     *     <li>returnValue List - list of ImageDirImportEntries
     *   </ul> 
     * <h4>FeatureKeywords:</h4>
     *     ImageImport
     * @param baseDirName - name of the basedir to scan
     * @return - ImageDirImportEntries
     */
    public List<ImageDirImportEntry> getImageDirImportEntries(String baseDirName) {
        List<ImageDirImportEntry> lstImageDirImportEntry = 
            new ArrayList<ImageDirImportEntry>();
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("start for:" + baseDirName);

        // read subdirs
        File baseDir = new File(baseDirName);
        List<File> lstSrcDirs = Collections.singletonList(baseDir);
        List<File> lstSubDirs = new ArrayList<File>();
        this.scanForSubDirs(lstSubDirs, lstSrcDirs, true);
        
        // iterate all subDirs
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("start for:" + baseDirName);
        for (File subDir : lstSubDirs) {
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("SubDir:" + subDir);

            // create ImageDirImportEntry for dir
            ImageDirImportEntry iDIE = scanForImages(subDir);

            // add to cmdList
            lstImageDirImportEntry.add(iDIE);
        }

        return lstImageDirImportEntry;
    }

    
    /**
     * <h4>FeatureDomain:</h4>
     *     ImageImport
     * <h4>FeatureDescription:</h4>
     *     generates an json-entry for ImageDirImportEntry for use with
     *     ImageImportEditor.html
     * <h4>FeatureResult:</h4>
     *   <ul>
     *     <li>returnValue String - json-entry
     *   </ul> 
     * <h4>FeatureKeywords:</h4>
     *     JSON ImageTools
     * @param iDIE - the ImageDirImportEntry to format as json
     * @return - json-entry
     */
    public String genJSONEntry(ImageDirImportEntry iDIE) {
        String res = "{\"Path\":\"" + iDIE.dir + "\","
                   + " \"Ort\":\"" + iDIE.location + "\","
                   + " \"StartDate\":\"" + dfDateTime.format(iDIE.firstDate) + "\","
                   + " \"EndDate\":\"" + dfDateTime.format(iDIE.lastDate) + "\","
                   + " \"MyOrt\":\"" + iDIE.location + "\","
                   + " \"MyStartDate\":\"" + dfDateTime.format(iDIE.firstDate) + "\","
                   + " \"MyEndDate\":\"" + dfDateTime.format(iDIE.lastDate) + "\""
                   + "}"
                ;
        
        return res;
    }
    
    /**
     * <h4>FeatureDomain:</h4>
     *     ImageImport
     * <h4>FeatureDescription:</h4>
     *     returns JSON-import-commands for ImageImportEditor.html
     * <h4>FeatureResult:</h4>
     *   <ul>
     *     <li>returnValue String - import-commands
     *   </ul> 
     * <h4>FeatureKeywords:</h4>
     *     ImageImport
     * @param baseDirName - name of the basedir to scan
     * @return - importcommands
     */
    public String genImageDirJSONImportCommand(String baseDirName) {
        String output = "[\n";
        
        // get ImageDirImportEntries
        List<ImageDirImportEntry> lstImageDirImportEntry = 
            getImageDirImportEntries(baseDirName);
        
        // iterate
        for (ImageDirImportEntry imageDirImportEntry : lstImageDirImportEntry) {
            String cmd = genJSONEntry(imageDirImportEntry);
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Entry:" + imageDirImportEntry + " -> cmd:" + cmd);
            output += cmd + ",\n";
        }
        
        // replace last "," and add "]"
        if (output.endsWith(",\n")) {
            output = output.substring(0, output.length()-2) + "\n";
        }
        output += "]\n";
        output = output.replaceAll("\\\\", "\\\\\\\\");

        return output;
    }

    /**
     * <h4>FeatureDomain:</h4>
     *     ImageImport
     * <h4>FeatureDescription:</h4>
     *     generates an csv-entry for ImageDirImportEntry for use with
     *     Excel an later ImageImportEditor.html
     * <h4>FeatureResult:</h4>
     *   <ul>
     *     <li>returnValue String - csv-entry
     *   </ul> 
     * <h4>FeatureKeywords:</h4>
     *     CSV ImageTools
     * @param iDIE - the ImageDirImportEntry to format as csv
     * @return - csv-entry
     */
    public String genCSVEntry(ImageDirImportEntry iDIE) {
        String res = "\"Ausflug mit XXX nach " + iDIE.location
                        + " " + iDIE.dateStr + "\"" + "\t\""
                        + dfDateTime.format(iDIE.firstDate) + "\" " + "\t\""
                        + dfDateTime.format(iDIE.lastDate) + "\""
                        + "\t\"OFFEN,Micha,\"" + "\t\"" + iDIE.dir + "\""
                        + "\t\"" + iDIE.location + "\"";
                ;
        
        return res;
    }

    /**
     * <h4>FeatureDomain:</h4>
     *     ImageImport
     * <h4>FeatureDescription:</h4>
     *     returns CSV-import-commands for ImageImportEditor.html
     * <h4>FeatureResult:</h4>
     *   <ul>
     *     <li>returnValue String - import-commands
     *   </ul> 
     * <h4>FeatureKeywords:</h4>
     *     ImageImport
     * @param baseDirName - name of the basedir to scan
     * @return - importcommands
     */
    public String genImageDirCSVImportCommand(String baseDirName) {
        String output = "";
        
        // get ImageDirImportEntries
        List<ImageDirImportEntry> lstImageDirImportEntry = 
            getImageDirImportEntries(baseDirName);
        
        // iterate
        for (ImageDirImportEntry imageDirImportEntry : lstImageDirImportEntry) {
            String cmd = genJSONEntry(imageDirImportEntry);
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Entry:" + imageDirImportEntry + " -> cmd:" + cmd);
            output += cmd + "\n";
        }
        
        return output;
    }
    
    /**
     * <h4>FeatureDomain:</h4>
     *     ImageImport
     * <h4>FeatureDescription:</h4>
     *     returns default-import-commands for ImageImportEditor.html
     * <h4>FeatureResult:</h4>
     *   <ul>
     *     <li>returnValue String - import-commands
     *   </ul> 
     * <h4>FeatureKeywords:</h4>
     *     ImageImport
     * @param baseDirName - name of the basedir to scan
     * @return - importcommands
     */
    public String genImageDirDefaultImportCommand(String baseDirName) {
        String output = genImageDirJSONImportCommand(baseDirName);
        
        return output;
    }

    /**
     * @param args- the command line arguments
     */
    public static void main(String[] args) {
        ImageDirImportEntryGenerator ii = new ImageDirImportEntryGenerator();
        String output = ii.genImageDirDefaultImportCommand(args[0]);
        System.out.println(output);
    }
}
