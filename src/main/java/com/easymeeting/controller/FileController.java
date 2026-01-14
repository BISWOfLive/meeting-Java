package com.easymeeting.controller;

import com.easymeeting.annotition.GlobalInterceptor;
import com.easymeeting.entity.config.AppConfig;
import com.easymeeting.entity.constants.Constants;
import com.easymeeting.entity.dto.TokenUserInfoDto;
import com.easymeeting.entity.enums.DateTimePatternEnum;
import com.easymeeting.entity.enums.FileTypeEnum;
import com.easymeeting.entity.enums.ResponseCodeEnum;
import com.easymeeting.exception.BusinessException;
import com.easymeeting.utils.DateUtil;
import com.easymeeting.utils.StringTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.*;
import java.util.Date;

@Slf4j
@Validated
@RestController
@RequestMapping("/file")
public class FileController extends ABaseController {

    @Resource
    private AppConfig appConfig;

    @RequestMapping("/getResource")
    @GlobalInterceptor(checkLogin = false)
    public void getResource(HttpServletResponse response,
                            @RequestHeader(required = false, name = "range") String range,
                            @NotNull Long messageId,
                            @NotNull Long sendTime,
                            @NotNull Integer fileType,
                            @NotEmpty String token, Boolean thumbnail) {

        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo(token);
        if (null == tokenUserInfoDto) {
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }

        FileTypeEnum fileTypeEnum = FileTypeEnum.getByType(fileType);

        thumbnail = thumbnail == null ? false : thumbnail;
        String month = DateUtil.format(new Date(sendTime), DateTimePatternEnum.YYYYMM.getPattern());
        String filePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + month + "/" + messageId + fileTypeEnum.getSuffix();
        if (null == fileTypeEnum) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        switch (fileTypeEnum) {
            case IMAGE:
                //缓存30天
                response.setHeader("Cache-Control", "max-age=" + 30 * 24 * 60 * 60);
                response.setContentType("image/jpg");
                break;
        }

        readFile(response, range, filePath, thumbnail);
    }


    protected void readFile(HttpServletResponse response, String range, String filePath, Boolean thumbnail) {
        filePath = thumbnail ? StringTools.getImageThumbnail(filePath) : filePath;
        File file = new File(filePath);
        try (ServletOutputStream out = response.getOutputStream();) {
            RandomAccessFile randomFile = new RandomAccessFile(file, "r");//只读模式
            long contentLength = randomFile.length();
            long start = 0, end = 0;
            if (range != null && range.startsWith("bytes=")) {
                String[] values = range.split("=")[1].split("-");
                start = Integer.parseInt(values[0]);
                if (values.length > 1) {
                    end = Integer.parseInt(values[1]);
                }
            }
            int requestSize = 0;
            if (end != 0 && end > start) {
                requestSize = (int) (end - start + 1);
            } else {
                requestSize = Integer.MAX_VALUE;
            }

            byte[] buffer = new byte[4096];
            response.setHeader("Accept-Ranges", "bytes");
            response.setHeader("Last-Modified", new Date().toString());
            //第一次请求只返回content length来让客户端请求多次实际数据
            if (range == null) {
                response.setHeader("Content-length", contentLength + "");
            } else {
                //以后的多次以断点续传的方式来返回视频数据
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);//206
                long requestStart = 0, requestEnd = 0;
                String[] ranges = range.split("=");
                if (ranges.length > 1) {
                    String[] rangeDatas = ranges[1].split("-");
                    requestStart = Integer.parseInt(rangeDatas[0]);
                    if (rangeDatas.length > 1) {
                        requestEnd = Integer.parseInt(rangeDatas[1]);
                    }
                }

                long length = 0;
                if (requestEnd > 0) {
                    length = requestEnd - requestStart + 1;
                    response.setHeader("Content-length", "" + length);
                    response.setHeader("Content-Range", "bytes " + requestStart + "-" + requestEnd + "/" + contentLength);
                } else {
                    length = contentLength - requestStart;
                    response.setHeader("Content-length", "" + length);
                    response.setHeader("Content-Range", "bytes " + requestStart + "-" + (contentLength - 1) + "/" + contentLength);
                }

                int needSize = requestSize;
                randomFile.seek(start);
                while (needSize > 0) {
                    int len = randomFile.read(buffer);
                    if (needSize < buffer.length) {
                        out.write(buffer, 0, needSize);
                    } else {
                        out.write(buffer, 0, len);
                        if (len < buffer.length) {
                            break;
                        }
                    }
                    needSize -= buffer.length;
                }
            }
            randomFile.close();
        } catch (Exception e) {
            log.error("读取文件信息失败");
        }
    }

    @RequestMapping("downloadFile")
    @GlobalInterceptor(checkLogin = false)
    public void downloadFile(HttpServletResponse response, @NotEmpty
    String token, @NotEmpty String messageId, @NotNull Long sendTime, @NotEmpty String suffix) throws IOException {

        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo(token);
        if (null == tokenUserInfoDto) {
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }

        String month = DateUtil.format(new Date(sendTime), DateTimePatternEnum.YYYYMM.getPattern());
        String filePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + month + "/" + messageId + suffix;
        File file = new File(filePath);

        response.setContentType("application/x-msdownload; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;");
        response.setContentLengthLong(file.length());

        try (FileInputStream in = new FileInputStream(file); OutputStream out = response.getOutputStream()) {
            byte[] byteData = new byte[1024];
            int len = 0;
            while ((len = in.read(byteData)) != -1) {
                out.write(byteData, 0, len);
            }
            out.flush();
        }
    }

    @RequestMapping("/getAvatar")
    @GlobalInterceptor(checkLogin = false)
    public void getAvatar(HttpServletResponse response, @NotNull String userId, @NotEmpty String token) {

        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo(token);
        if (null == tokenUserInfoDto) {
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }

        String filePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + Constants.FILE_FOLDER_AVATAR_NAME + userId + Constants.IMAGE_SUFFIX;
        response.setContentType("image/jpg");
        File file = new File(filePath);
        if (!file.exists()) {
            readLocalFile(response);
            return;
        }
        readFile(response, null, filePath, false);
    }

    private void readLocalFile(HttpServletResponse response) {
        response.setHeader("Cache-Control", "max-age=" + 30 * 24 * 60 * 60);
        response.setContentType("image/jpg");
        //读取文件
        ClassPathResource classPathResource = new ClassPathResource(Constants.DEFAULT_AVATAR);
        try (OutputStream out = response.getOutputStream(); InputStream in = classPathResource.getInputStream()) {
            byte[] byteData = new byte[1024];
            int len = 0;
            while ((len = in.read(byteData)) != -1) {
                out.write(byteData, 0, len);
            }
            out.flush();
        } catch (Exception e) {
            log.error("读取本地文件异常", e);
        }
    }

}