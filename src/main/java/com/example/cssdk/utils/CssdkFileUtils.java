package com.example.cssdk.utils;


import com.interwoven.cssdk.common.CSClient;
import com.interwoven.cssdk.common.CSException;
import com.interwoven.cssdk.filesys.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;


public class CssdkFileUtils {
    private static Log log = LogFactory.getLog(CssdkFileUtils.class);

    /**
     * Creates a Directory in TeamSite WorkArea.
     * @param csClient - CSClient object
     * @param vPath - The existing directory where new directory need to becreated.
     * @param dirName - The new directory name.
     * @throws CSException
     */
    public static void createTSDir(final CSClient csClient, final String vPath, final String dirName) throws CSException {
        log.info("Inside createTSDir - creating : " + vPath + "/" + dirName);
        CSDir existingDir = (CSDir)csClient.getFile( new CSVPath(vPath));
        existingDir.createChildDirectory(dirName);
    }

    /**
     * Checks if CSFile exists. It doesn't check for files deleted in WorkArea.
     * @param csClient - CSClient object
     * @param tsVPath - vPath String to check the CSFile existance
     * @return - True or False if file exists or not.
     * @throws RuntimeException
     */
    public static boolean isTSFileExists(final CSClient csClient, final String tsVPath) throws RuntimeException{
        if(log.isDebugEnabled()) log.debug("checking if exists : " + tsVPath);
        try {
            CSVPath csvPath = new CSVPath(tsVPath);
            return csClient.getFile(csvPath) != null;
        }catch (CSException cse){
            throw new RuntimeException(cse);
        }
    }

    /**
     * Copies file from local filesystem to TeamSite workarea.
     * @param csClient - Content Service client object
     * @param newTSVPath - VPath String of the new file
     * @param file - Source file object
     * @param fileName - name of file to be created
     * @param tsDir - CSDir object of TeamSite directory where file need to be created.
     * @throws CSException
     * @throws IOException
     */
    public static void copyFileToTSWorkArea(final CSClient csClient,
                                            final String newTSVPath, final File file, final String fileName,
                                            final CSDir tsDir) throws CSException, IOException {
        if(log.isDebugEnabled()) log.debug("Copying file : " + fileName);
        CSSimpleFile csSimpleFile = null;
        if(isTSFileExists(csClient, newTSVPath)) {
            CSFile csFile = csClient.getFile(new CSVPath(newTSVPath));
            if(csFile instanceof CSSimpleFile){
                csSimpleFile = (CSSimpleFile) csFile;
                if(log.isDebugEnabled())  log.debug("File name existing on TS : " + csSimpleFile.getVPath().toString());
            }else if(csFile instanceof CSHole){
                if(log.isDebugEnabled()) log.debug("File is instance of CSHole(deleted in workarea) so create a new file. ");
                csSimpleFile = tsDir.createChildSimpleFile(fileName);
                if(log.isDebugEnabled()) log.debug("New File created : " + csSimpleFile.getName());
            }else{
                log.error("Unexpected scenario.");
            }
        }else {
            csSimpleFile = tsDir.createChildSimpleFile(fileName);
            if(log.isDebugEnabled()) log.debug("File does not exists. Created a new file name " + csSimpleFile.getName());
        }

        byte[] fileBytes = FileUtils.readFileToByteArray(file);
        if(log.isDebugEnabled()) log.debug("Read the file bytes");

        if(csSimpleFile != null)
            csSimpleFile.write(fileBytes, 0, fileBytes.length, false);
        if(log.isDebugEnabled()) log.debug("Finished writing files to ts.");
    }

    /**
     * Creates a new directory if directory doesn't exist on given path
     * @param csClient - CSClient object
     * @param newTSVPath - Directory path in question
     * @param dirName - Directory name which is needed to create if doesn't already exist.
     * @param vPath - vPath String of the directory where new directory will be created
     * @throws CSException
     */
    public static void createNewTSDirectoryIfNeeded(final CSClient csClient,
                                                    final String newTSVPath, final String dirName, final String vPath)
            throws CSException {
        if(isTSFileExists(csClient, newTSVPath)){
            CSFile csFile = csClient.getFile(new CSVPath(newTSVPath));
            if(csFile instanceof CSHole) createTSDir(csClient, vPath, dirName);
            else log.info("Directory already exists in TS : " + csFile.getName());
        }else{
            createTSDir(csClient, vPath, dirName);
        }
    }
}