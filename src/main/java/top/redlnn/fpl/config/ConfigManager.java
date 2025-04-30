package top.redlnn.fpl.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import top.redlnn.fpl.FakePlayerLimitLogger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public final class ConfigManager {
    private static final FakePlayerLimitLogger LOGGER = FakePlayerLimitLogger.getInstance();

    @SuppressWarnings("deprecation")
    private static final Gson GSON = new GsonBuilder()
            .enableComplexMapKeySerialization()
            .setPrettyPrinting()
            .setLenient()
            .create();

    private ConfigManager() {
        // 工具类禁止实例化
    }

    public static String toJson(Object o, Type type) {
        return GSON.toJson(o, type);
    }

    public static <T> T toClass(String json, Type type) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        return GSON.fromJson(new JsonReader(new StringReader(json)), type);
    }

    public static <T> T readFile(File file, Type type) throws IOException {
        validateFileExists(file);
        try {
            return toClass(Files.readString(file.toPath(), StandardCharsets.UTF_8), type);
        } catch (JsonSyntaxException e) {
            throw new IOException("JSON解析错误: " + file.getAbsolutePath(), e);
        }
    }

    public static void writeFile(File file, Object obj) throws IOException {
        if (obj == null) {
            LOGGER.warn("请求写入null对象，已结束本次写入：{}", file);
            return;
        }

        ensureParentDirExists(file);
        File tempFile = createSafeTempFile(file.getName(), file.getParentFile());
        try {
            Files.writeString(tempFile.toPath(), toJson(obj, obj.getClass()), StandardCharsets.UTF_8);
            Files.move(tempFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            Files.deleteIfExists(tempFile.toPath());
            throw new IOException("文件写入失败: " + file, e);
        }
    }

    private static void validateFileExists(File file) throws FileNotFoundException {
        if (!file.exists() || !file.isFile()) {
            throw new FileNotFoundException("文件不存在或不是普通文件: " + file.getAbsolutePath());
        }
    }

    private static void ensureParentDirExists(File file) throws IOException {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IOException("无法创建目录: " + parent);
        }
    }

    private static File createSafeTempFile(String prefix, File directory) throws IOException {
        if (directory != null && !directory.exists() && !directory.mkdirs()) {
            throw new IOException("无法创建临时文件目录: " + directory);
        }
        return File.createTempFile(prefix, ".tmp", directory);
    }
}
