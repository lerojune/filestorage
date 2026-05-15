import io.github.lerojune.oss.files.FileUploadDto;
import io.github.lerojune.oss.files.config.ObsConfig;
import io.github.lerojune.oss.files.exception.FileUploadException;
import io.github.lerojune.oss.files.exception.NotFindException;
import io.github.lerojune.oss.files.impl.LocalService;
import io.github.lerojune.oss.files.impl.ObsService;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.avro.AvroParquetWriter;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.example.data.simple.SimpleGroupFactory;
import org.apache.parquet.example.data.GroupWriter;
import org.apache.parquet.hadoop.example.GroupWriteSupport;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.MessageTypeParser;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.sql.*;

@Slf4j
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



    @Test
    public void testObsConfig() throws IOException {

        // 1. 读取 Avro Schema
        Schema.Parser parser = new Schema.Parser();
        Schema schema = parser.parse(new File("schema/schema.avsc")); // 注意: 替换为你的 schema 文件路径

        // 2. 创建 Hadoop 配置
        Configuration conf = new Configuration();

        // 3. 指定输出文件路径
        Path outputPath = new Path("output.parquet"); // 输出文件名
        //HadoopOutputFile outputFile = HadoopOutputFile.fromPath(outputPath, conf);

        // 4. 创建 Parquet Writer
        try (ParquetWriter<GenericRecord> writer = AvroParquetWriter
                .<GenericRecord>builder(outputPath)//outputFile
                .withSchema(schema)
                .withCompressionCodec(CompressionCodecName.UNCOMPRESSED) // 可选: GZIP, SNAPPY, LZO, BROTLI, LZ4, ZSTD
                .build()) {

            // 5. 创建并写入记录
            GenericRecord record1 = new GenericData.Record(schema);
            record1.put("id", 1);
            record1.put("name", "Alice");
            record1.put("age", 30);

            GenericRecord record2 = new GenericData.Record(schema);
            record2.put("id", 2);
            record2.put("name", "Bob");
            record2.put("age", 25);

            writer.write(record1);
            writer.write(record2);

            System.out.println("Parquet 文件已写入: " + outputPath.toString());
        } // try-with-resources 会自动关闭 writer
    }


    @Test
    public void queryArchivedData() {
//        System.setProperty("sun.reflect.serialization.extendedSerializableAccess", "true");
//        String logFile = "README.md"; // Should be some file on your system
//        SparkSession spark = SparkSession.builder().appName("Simple Application").master("local").getOrCreate();
//        Dataset<String> logData = spark.read().textFile(logFile).cache();
//
//        long numAs = logData.filter((Function1<String, Object>) s -> s.contains("a")).count();
//        long numBs = logData.filter((Function1<String, Object>) s -> s.contains("b")).count();
//
//        System.out.println("Lines with a: " + numAs + ", lines with b: " + numBs);
//        log.error("As a file: " + numAs + " and b: " + numBs);
//        logData.show();
//
//        spark.stop();
    }

    @Test
    public void queryFile() throws ClassNotFoundException, SQLException {
        // 加载 DuckDB 驱动
        Class.forName("org.duckdb.DuckDBDriver");

        // 建立内存数据库连接
        try (Connection conn = DriverManager.getConnection("jdbc:duckdb:");
             Statement stmt = conn.createStatement()) {

            // 直接用 SQL 查询本地 Parquet 文件！
            String sql = "SELECT * FROM 'offline_data_java.parquet' WHERE status = 2";

            try (ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    System.out.println("admin_name: " + rs.getString("admin_name") + ", Status: " + rs.getInt("status"));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }


    @Test
    public void saveFile() throws SQLException, IOException {
        System.setProperty("hadoop.home.dir", "D:\\Appdata\\hadoop-3.5.0");
        // 1. 定义 Parquet 的 Schema (必须与数据库字段对应)
        String schemaStr  = "message task {\n" +
                "  optional binary id (UTF8);\n" +
                "  optional int32 school_id;\n" +
                "  optional int32 center_id;\n" +
                "  optional int32 status;\n" +
                "  optional boolean is_pause;\n" +
                "  optional binary type (UTF8);\n" +
                "  optional int32 admin_id;\n" +
                "  optional int32 admin_type;\n" +
                "  optional binary admin_name (UTF8);\n" +
                "  optional int96 regtime;\n" +
                "  optional binary memo (UTF8);\n" +
                "  optional binary job_name (UTF8);\n" +
                "  optional binary job_data (UTF8);\n" +
                "  optional binary result (UTF8);\n" +
                "  optional binary path (UTF8);\n" +
                "  optional int96 expire_time;\n" +
                "}";
        MessageType schema = MessageTypeParser.parseMessageType(schemaStr);

        Configuration conf = new Configuration();
        GroupWriteSupport.setSchema(schema, conf);


        // 2. 创建 ParquetWriter
        Path outputPath = new Path("offline_data_java.parquet");
        ParquetWriter<Group> writer = new ParquetWriter<>(
                outputPath,
                conf,
                new GroupWriteSupport()
        );

        // 3. 数据库流式读取（使用 ResultSet 游标，避免一次性加载到内存）
        String url = "jdbc:mysql://127.0.0.1:3306/toolcenter?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2b8&allowPublicKeyRetrieval=true";
        String user = "jiaowu";
        String password = "Jiaowu";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {

            // 设置 MySQL 驱动流式读取参数，防止 OOM
            stmt.setFetchSize(Integer.MIN_VALUE);
            ResultSet rs = stmt.executeQuery("SELECT * FROM task limit 0, 1000");

            SimpleGroupFactory factory = new SimpleGroupFactory(schema);
            int count = 0;

            // 4. 逐行写入 Parquet
            while (rs.next()) {
                Group group = factory.newGroup()
                        .append("id", rs.getString("id"))
                        .append("school_id", rs.getInt("school_id"))
                        .append("center_id", rs.getInt("center_id"))
                        .append("status", rs.getInt("status"))

                        .append("admin_name", rs.getString("admin_name")!=null?rs.getString("admin_name"):null)
                        .append("memo", rs.getString("memo")!=null?rs.getString("memo"):"")
                        .append("job_name", rs.getString("job_name")!=null?rs.getString("job_name"):"")
                        .append("job_data", rs.getString("job_data")!=null?rs.getString("job_data"):"")
                        .append("result", rs.getString("result")!=null?rs.getString("result"):"")
                        .append("path", rs.getString("path")!=null?rs.getString("path"):"");
                writer.write(group);

                count++;
                if (count % 10000 == 0) {
                    System.out.println("已写入 " + count + " 条数据...");
                }
            }
        }
        writer.close();
        System.out.println("数据导出完成！");
    }


    private static String mapJdbcTypeToParquet(int jdbcType, String columnName) {
        switch (jdbcType) {
            case Types.INTEGER:
            case Types.SMALLINT:
            case Types.TINYINT:
                return "int32 " + columnName; // 实际使用时把 col_name 替换成真实字段名
            case Types.BIGINT:
                return "int64 " + columnName;
            case Types.FLOAT:
            case Types.REAL:
                return "float " + columnName;
            case Types.DOUBLE:
                return "double " + columnName;
            case Types.VARCHAR:
            case Types.CHAR:
            case Types.LONGVARCHAR:
            case Types.CLOB:
                return "binary " + columnName+" (UTF8)";
            case Types.BOOLEAN:
            case Types.BIT:
                return "boolean " + columnName;
            case Types.DATE:
                return "int32 " + columnName+" (DATE)";
            case Types.TIMESTAMP:
                return "int96 " + columnName;
            case Types.DECIMAL:
            case Types.NUMERIC:
                return "fixed_len_byte_array(16) " + columnName+" (DECIMAL(10,2))"; // 精度可根据实际情况调整
            default:
                return "binary " + columnName+" (UTF8)"; // 兜底处理为字符串
        }
    }

    @Test
    public void readFile() throws SQLException {
        String url = "jdbc:mysql://127.0.0.1:3306/toolcenter?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2b8&allowPublicKeyRetrieval=true";
        String user = "jiaowu";
        String password = "Jiaowu";

        try (Connection conn = DriverManager.getConnection(url, user, password);) {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, "task", null);

            StringBuilder schemaBuilder = new StringBuilder("message task {\n");
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                int dataType = columns.getInt("DATA_TYPE");
                // 根据 dataType 自动映射（比如 Types.VARCHAR 映射为 binary (UTF8)）
                String parquetType = mapJdbcTypeToParquet(dataType, columnName);
                //schemaBuilder.append("  optional ").append(parquetType).append(" ").append(columnName).append(";\n");
                schemaBuilder.append("  optional ").append(parquetType).append(";\n");
            }
            schemaBuilder.append("}");
            System.out.println(schemaBuilder.toString());
        }
    }



    @Test
    public void writeFile() throws SQLException, IOException {

    }

}
