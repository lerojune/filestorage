package io.github.lerojune.oss.files.impl;

import io.github.lerojune.oss.files.FileUploadDto;
import io.github.lerojune.oss.files.FileUploadService;
import io.github.lerojune.oss.files.exception.FileUploadException;
import io.github.lerojune.oss.files.exception.NotFindException;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class LocalService implements FileUploadService {


    public void upload(FileUploadDto fileUploadDto) throws NotFindException, FileUploadException {
        if (Objects.isNull(fileUploadDto.getFile()) && Objects.isNull(fileUploadDto.getFileUploadType())){
            throw new NotFindException("没有文件");
        }

        try{
            if (!Objects.isNull(fileUploadDto.getFile())){
                //create
                createDestFile(fileUploadDto.getName());

                //move
                Files.move(Paths.get(fileUploadDto.getName()), Paths.get(fileUploadDto.getFile().getName()));
            }else if (!Objects.isNull(fileUploadDto.getFileInputStream())){
                //create file by the input stream
                FileOutputStream fileOutputStream = new FileOutputStream(fileUploadDto.getName());

                byte[] bytes = new byte[1024];
                int count = 0;
                while(count != -1){
                    count = fileUploadDto.getFileInputStream().read(bytes,0, 1024);
                    fileOutputStream.write(bytes, 0, count);
                }
                fileOutputStream.close();
                fileUploadDto.getFileInputStream().close();
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new FileUploadException(e.getMessage());
        }
    }

    private void createDestFile(String path){
        File destFile = new File(path);
        if ( destFile.exists() ){
            destFile.delete();
        }
        if ( Objects.isNull(destFile.getParentFile())){
            return;
        }
        destFile.getParentFile().mkdirs();
    }

    public void clean() {
        //to do something
    }
}
