import com.tkzou.middleware.configcenter.client.core.ConfigService;
import com.tkzou.middleware.configcenter.client.client.domain.ConfigFile;

/**
 * 测试监听
 *
 * @author :zoutongkun
 * @date :2024/4/18 12:21 上午
 * @description :
 * @modyified By:
 */
public class Test {

    public static void main(String[] args) {
        // 创建一个ConfigService，传入配置中心服务端的地址
        ConfigService configService = new ConfigService("localhost:8888");

        // 从服务端获取配置文件的内容，文件的id是新增配置文件时候自动生成
        ConfigFile config = configService.getConfig("69af6110-31e4-4cb4-8c03-8687cf012b77");

        // 对某个配置文件进行监听
        configService.addListener("69af6110-31e4-4cb4-8c03-8687cf012b77", configFile -> System.out.printf("fileId=%s" +
                "配置文件有变动，最新内容为:%s%n", configFile.getFileId(), configFile.getContent()));
    }
}
