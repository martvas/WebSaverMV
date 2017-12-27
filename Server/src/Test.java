import fileWork.FileWork;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//Это просто тестовый класс, не обращайте внимания
public class Test {
    public static void main(String[] args) throws Exception {

        File file = new File("Server/clients_folder/nos/15.png");
        System.out.println(file.getName());
        System.out.println(Files.size(file.toPath()));

        byte[] content = Files.readAllBytes(file.toPath());
        System.out.println(content.length);

//        byte[] content = Files.readAllBytes(file.toPath());
//        System.out.println(Arrays.toString(content));
//
//        Path path = Paths.get("Server/clients_folder/nos/15-new.png");
//        Files.write(path, content);

//        String fileName = file.getName();
//        Path path = file.toPath();
//
//        System.out.println(FilenameUtils.removeExtension(fileName));
//        System.out.println(FilenameUtils.getExtension(fileName));
//
////        String.format("%s%d.%s", baseName, i, extension);
    }

//
//    private static Path findFileName(final Path dir, final String baseName, final String extension) {
//        Path ret = Paths.get(dir, String.format("%s.%s", baseName, extension));
//        if (!Files.exists(ret))
//            return ret;
//
//        for (int i = 0; i < Integer.MAX_VALUE; i++) {
//            ret = Paths.get(dir, String.format("%s%d.%s", baseName, i, extension));
//            if (!Files.exists(ret))
//                return ret;
//        }
//        throw new IllegalStateException("What the...");
//    }
}
