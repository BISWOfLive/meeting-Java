package com.easymeeting.utils;

import com.easymeeting.entity.constants.Constants;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.IOException;

@Component
public class FFmpegUtils {

    public String transferImageType(File tempFile, String filePath) {
        final String CMD_CREATE_IMAGE_THUMBNAIL = "ffmpeg -i \"%s\" \"%s\"";
        String cmd = String.format(CMD_CREATE_IMAGE_THUMBNAIL, tempFile, filePath);
        ProcessUtils.executeCommand(cmd);
        tempFile.delete();
        return filePath;
    }

    public void transferVideoType(File tempFile, String filePath, String fileSuffix) throws IOException {
        String codec = getVideoCodec(tempFile.getAbsolutePath());
        if (Constants.VIDEO_CODE_HEVC.equals(codec) || !Constants.VIDEO_SUFFIX.equalsIgnoreCase(fileSuffix)) {
            convertHevc2Mp4(tempFile.getAbsolutePath(), filePath);
        } else {
            FileUtils.copyFile(tempFile, new File(filePath));
        }
        tempFile.delete();
    }

    /**
     * 生成图片缩略图
     * @param filePath
     * @return
     */
    public void createImageThumbnail(String filePath) {
        final String CMD_CREATE_IMAGE_THUMBNAIL = "ffmpeg -i \"%s\" -vf scale=200:-1 \"%s\" -y";
        String thumbnail = StringTools.getImageThumbnail(filePath);
        String cmd = String.format(CMD_CREATE_IMAGE_THUMBNAIL, filePath, thumbnail);
        ProcessUtils.executeCommand(cmd);
    }

    public void createImageThumbnail(File tempFile, String filePath) {
        final String CMD_CREATE_IMAGE_THUMBNAIL = "ffmpeg -i \"%s\" -vf scale=200:-1 \"%s\" -y";
        String cmd = String.format(CMD_CREATE_IMAGE_THUMBNAIL, tempFile, filePath);
        ProcessUtils.executeCommand(cmd);
        tempFile.delete();
    }

    /**
     * 获取视频编码
     * @param videoFilePath
     * @return
     */
    public String getVideoCodec(String videoFilePath) {
        final String CMD_GET_CODE = "ffprobe -v error -select_streams v:0 -show_entries stream=codec_name \"%s\"";
        String cmd = String.format(CMD_GET_CODE, videoFilePath);
        String result = ProcessUtils.executeCommand(cmd);
        result = result.replace("\n", "");
        result = result.substring(result.indexOf("=") + 1);
        String codec = result.substring(0, result.indexOf("["));
        return codec;
    }

    public void convertHevc2Mp4(String newFileName, String videoFilePath) {
        String CMD_HEVC_264 = "ffmpeg -i %s -c:v libx264 -crf 20 %s";
        String cmd = String.format(CMD_HEVC_264, newFileName, videoFilePath);
        ProcessUtils.executeCommand(cmd);
    }
}