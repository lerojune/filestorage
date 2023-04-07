package io.github.lerojune.oss.files.impl;

import com.obs.services.ObsClient;
import com.obs.services.model.PutObjectRequest;
import com.obs.services.model.PutObjectResult;
import io.github.lerojune.oss.files.FileUploadDto;
import io.github.lerojune.oss.files.FileUploadService;
import io.github.lerojune.oss.files.config.ObsConfig;
import io.github.lerojune.oss.files.exception.FileUploadException;
import io.github.lerojune.oss.files.exception.NotFindException;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Objects;

public class ObsService implements FileUploadService {

    private ObsConfig obsConfig;
    public void setConfig(ObsConfig obsConfig){
        this.obsConfig = obsConfig;
    }

    public void upload(FileUploadDto fileUploadDto) throws NotFindException, FileUploadException {
        if (Objects.isNull(obsConfig)){
            throw new FileUploadException("没有OBS相关的配置");
        }

        InputStream inputStream = null;
        try{
            ObsClient obsClient = new ObsClient(obsConfig.ak, obsConfig.sk, obsConfig.endPoint);

            PutObjectRequest request = new PutObjectRequest();
            request.setBucketName(obsConfig.bucket);
            request.setObjectKey(fileUploadDto.getName());

            if (!Objects.isNull(fileUploadDto.getFileInputStream())){
                inputStream = fileUploadDto.getFileInputStream();
            }else{
                inputStream = new FileInputStream(fileUploadDto.getFile());
            }
            request.setInput(inputStream);
            PutObjectResult result = obsClient.putObject(request);
        }catch (Exception e){
            throw new FileUploadException(e.getMessage());
        }finally {
            if (!Objects.isNull(inputStream)){
                try{
                    inputStream.close();
                }catch (Exception e){
                }
            }
        }
    }

    public void clean() {

    }

}
