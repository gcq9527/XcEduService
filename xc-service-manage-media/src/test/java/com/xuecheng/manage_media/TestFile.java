package com.xuecheng.manage_media;

import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

/**
 * @author yd
 * @version 1.0
 * @date 2020/4/19 14:37
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestFile {

    @Autowired
    MediaFileRepository mediaFileRepository;

    /**
     * 测试文件分块
     */
    @Test
    public void testChunk() throws Exception {
        //源文件
        File sourceFile = new File("D:\\java\\阶段5\\阶段5 学成在线\\阶段5 3.微服务项目【学成在线】·\\day13 在线学习 HLS\\资料\\lucene.avi");
        //块文件目录
        String chunkFileFloder = "F:\\develop\\chunks\\";

        //先定义块文件大小 1*1024 = 1K 然后再* 1024 就是1M(兆)
        long chunkFileSize = 1 * 1024 * 1024;

        //块数 ceil向上取整  * 1.0变成证数 除块的大小
        long chunkFileNum = (long) Math.ceil(sourceFile.length() *1.0 /chunkFileSize);

        //创建读文件的对象  r 就是只读
        RandomAccessFile raf_read = new RandomAccessFile(sourceFile,"r");
        //缓冲区
        byte[] b = new byte[1024];
        for (int i = 0; i<chunkFileNum; i++){
            //块文件 每次加一
            File chunkFile = new File(chunkFileFloder+i);
            //rw 就是写入
            RandomAccessFile raf_write = new RandomAccessFile(chunkFile,"rw");
            int len = -1;
            while((len = raf_read.read(b))!=-1){
                //创建向块文件的写对象
                raf_write.write(b,0,len);
                //如果块文件的数量 大于1M 开始写下一块
                if(chunkFile.length() >= chunkFileSize){
                    break;
                }
            }
            raf_write.close();
        }
        raf_read.close();
    }

    /**
     * 测试合并文件
     */
    @Test
    public void testMergeFile() throws IOException {
        //块文件目录
        String chunkFileFolderPath = "F:\\develop\\chunks\\";
        //块文件目录对象
        File chunkFileFolder = new File(chunkFileFolderPath);
        //块文件列表
        File[] files = chunkFileFolder.listFiles();
        //将块文件排序，按名称升序
        List<File> fileList = Arrays.asList(files);
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (Integer.parseInt(o1.getName())> Integer.parseInt(o2.getName())){
                    return 1;
                }
                return -1;
            }
        });
        //合并文件
        File mergeFile = new File("F:\\develop\\chunks\\lucene_merage.avi");
        //创建新文件
        boolean newFIle = mergeFile.createNewFile();
        //写对象
        RandomAccessFile raf_write = new RandomAccessFile(mergeFile,"rw");
        byte[] b = new byte[1024];
        for(File chunKFile : fileList){
            //创建一个读块文件的对象
            RandomAccessFile raf_read = new RandomAccessFile(chunKFile,"r");
            int len = -1;
            while((len = raf_read.read(b))!=-1){
                raf_write.write(b,0,len);
            }
            raf_read.close();;
        }
        raf_write.close();
    }

    @Test
    public void testSaveMedia(){
        MediaFile mediaFile = new MediaFile();
        mediaFile.setFileId("12");
        mediaFile.setFileName("123");
        mediaFile.setFileOriginalName("123");
        mediaFile.setFilePath("c:");
        mediaFile.setFileSize(100l);
        mediaFile.setFileStatus("1");
        mediaFile.setFileUrl("http:");
        mediaFile.setMediaFileProcess_m3u8(null);
        mediaFile.setProcessStatus("123");
        mediaFile.setMimeType("12");
        mediaFile.setTag("12");
        mediaFile.setUploadTime(new Date());
        mediaFile.setFileType("1");

        mediaFileRepository.save(mediaFile);
    }
}