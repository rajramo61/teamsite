package com.example.teamsite;

import com.example.cssdk.utils.CssdkFileUtils;
import com.example.cssdk.utils.CssdkUtils;
import com.interwoven.cssdk.common.CSClient;
import com.interwoven.cssdk.common.CSException;
import com.interwoven.cssdk.filesys.CSDir;
import com.interwoven.cssdk.filesys.CSVPath;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class WriteFilesToTeamSite {
    private static CSClient csClient;

    private static Log log = LogFactory.getLog(WriteFilesToTeamSite.class);
    private static String FILE_SEPARATOR = System.getProperty("file.separator");

    public WriteFilesToTeamSite() {
        csClient = CssdkUtils.getJavaClient("fqdn", "userid", "password");
    }

    /**
     * Copies files from a filesystem directory to TeamSite directory (Copies the nested directory structure)
     * @param dirPath - Source local filesystem directory path String
     * @param vPath - Target TeamSite vPath String
     * @throws RuntimeException
     */
    public static void copyFiles(final String dirPath, final String vPath)
            throws RuntimeException {
        if(log.isDebugEnabled()) log.debug("Inside copyFiles");

        try {
            //Get the root level files and dirs
            List<File> files = getLocalFileList(dirPath);
            CSDir videoDir = (CSDir)csClient.getFile(new CSVPath(vPath));

            for(File file : files){
                final String fileName = file.getName();
                log.info("File : " + fileName);
                final String newTSVPath = vPath + FILE_SEPARATOR + fileName;

                if(file.isDirectory()){
                    log.info("This is a directory : " + fileName);
                    final String newDir = dirPath + FILE_SEPARATOR + fileName;

                    //Check if directory exists, if no then create directory
                    CssdkFileUtils.createNewTSDirectoryIfNeeded(csClient, newTSVPath, fileName, vPath);

                    //Copy the files from server fileSystem to TS workarea.
                    copyFiles(newDir, newTSVPath);
                } else{
                    CssdkFileUtils.copyFileToTSWorkArea(csClient, newTSVPath, file, fileName, videoDir);
                }
            }
        }catch (CSException cse){
            cse.printStackTrace();
            throw new RuntimeException(cse);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static List<File> getLocalFileList(final String dirPath) {
        File localDir = new File(dirPath);
        return Arrays.asList(localDir.listFiles());
    }
}
