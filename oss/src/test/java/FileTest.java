import io.github.lerojune.oss.files.FileUploadDto;
import io.github.lerojune.oss.files.config.ObsConfig;
import io.github.lerojune.oss.files.exception.FileUploadException;
import io.github.lerojune.oss.files.exception.NotFindException;
import io.github.lerojune.oss.files.impl.LocalService;
import io.github.lerojune.oss.files.impl.ObsService;
import org.junit.Test;

import java.io.File;

public class FileTest {

    @Test
    public void test() throws NotFindException, FileUploadException {
        FileUploadDto fileUploadDto = new FileUploadDto();
        fileUploadDto.setFile(new File("demo.txt"));
        fileUploadDto.setName("demo1.txt");


        LocalService localService = new LocalService();
        localService.upload(fileUploadDto);
    }

    @Test
    public void testObs() throws NotFindException, FileUploadException {
        ObsConfig obsConfig = new ObsConfig();
        obsConfig.ak = "";
        obsConfig.sk = "";
        obsConfig.bucket = "jiaowu";
        obsConfig.domain = "";

        FileUploadDto fileUploadDto = new FileUploadDto();

        fileUploadDto.setFile(new File("demo.txt"));
        fileUploadDto.setName("demo1.txt");

        ObsService obsService = new ObsService();
        obsService.setConfig(obsConfig);
        obsService.upload(fileUploadDto);
    }

}
