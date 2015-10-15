package com.example.teamsite;


import com.example.cssdk.utils.CssdkUtils;
import com.interwoven.cssdk.access.CSGroup;
import com.interwoven.cssdk.common.CSClient;
import com.interwoven.cssdk.common.CSException;
import com.interwoven.cssdk.common.CSIterator;
import com.interwoven.cssdk.filesys.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


public class UpdatePermission {
    private static Log log = LogFactory.getLog(UpdatePermission.class);

    private static final String GROUP_NAME = "group_name";
    private static CSClient csClient;
    public static final String DIR_PATH = "TS_VPATH_String";

    public static void main(String[] args) throws CSException {
        updateFileGroupPermission();
    }

    private static void updateFileGroupPermission() throws CSException{
        csClient = CssdkUtils.getSoapClient("ts_url", "user", "passwd");
        CSGroup passedGroup = csClient.getGroup(GROUP_NAME, true);
        log.info("Checking about webdev group: " + passedGroup.getDisplayName());
        List<CSSimpleFile> csSimpleFileList = getFileListFromDir();
        log.info("Got the filelist. Now update the group");

        if(csSimpleFileList != null && csSimpleFileList.size() > 0)
            csSimpleFileList.stream().forEach(getCsSimpleFileConsumer(passedGroup));
    }

    private static Consumer<CSSimpleFile> getCsSimpleFileConsumer(CSGroup webdevGroup) {
        return csSFile ->{
            try {
                CSGroup group = csSFile.getGroup();
                log.info("File name = " + csSFile.getVPath().getAreaRelativePath()
                        + " => Group Owner = " + group.getDisplayName());
                if(!webdevGroup.getDisplayName().equals(group.getDisplayName()))
                    csSFile.setGroup(webdevGroup);
                else
                    log.info("File name = " + csSFile.getVPath().getAreaRelativePath()
                            + " => Group Owner = " + group.getDisplayName());
            } catch (CSException e) {
                e.printStackTrace();
            }
        };
    }

    private static List<CSSimpleFile> getFileListFromDir() throws CSException {
        List<CSSimpleFile> simpleFileList = new ArrayList<>();

        CSSortKey[] sortArray = new CSSortKey[1];
        sortArray[0] = new CSSortKey(CSSortKey.MODDATE, true);
        CSFile passedCSFile = csClient.getFile(new CSVPath(DIR_PATH));

        if(passedCSFile != null && passedCSFile instanceof CSDir){
            CSDir csDir = (CSDir)passedCSFile;
            CSIterator csFileIterator = csDir.getFiles(CSFileKindMask.ALLFILES, sortArray,
                    CSFileKindMask.DIR, null, 0, -1);
            while (csFileIterator.hasNext()){
                CSFile csFile = (CSFile) csFileIterator.next();
                if(csFile != null && csFile instanceof CSSimpleFile){
                    CSSimpleFile simpleFile = (CSSimpleFile) csFile;
                    simpleFileList.add(simpleFile);
                }
            }
        }else{
            log.error("Please pass the directory is not a directory.");
        }
        return simpleFileList;
    }
}
