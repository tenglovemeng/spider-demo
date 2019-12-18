package util;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.*;

/**
 * @author ZTF
 */
public class HttpUtil {
    static CloseableHttpClient httpClient = HttpClients.createDefault();

    public static String getHtml(String url) {
        CloseableHttpResponse httpResponse = null;
        String htmlString = "";
        try {
            HttpGet httpGet = new HttpGet(url);
            httpResponse = httpClient.execute(httpGet);
            if(httpResponse.getStatusLine().getStatusCode() == 200) {
                HttpEntity httpEntity = httpResponse.getEntity();
                htmlString = EntityUtils.toString(httpEntity);
                EntityUtils.consume(httpEntity);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return htmlString;
    }

    public static void downloadPicture(String path, String columnName) {
        CloseableHttpResponse response = null;
        File localFile = getLocalFile(path, columnName);
            try {
                HttpGet httpGet = new HttpGet(path);
                response = httpClient.execute(httpGet);
                InputStream inputStream = response.getEntity().getContent();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(localFile));
                int flag;
                byte[] bytes = new byte[1024];
                while ((flag = bufferedInputStream.read(bytes)) != -1) {
                    bufferedOutputStream.write(bytes,0,flag);
                }
                bufferedOutputStream.close();
                bufferedInputStream.close();
                System.out.println(columnName + " " + getFileName(path) + " download complete");
            } catch (IOException e) {
                System.out.println("文件下载失败" + columnName + path);
                e.printStackTrace();
            } finally {
                if (response != null) {
                    try {
                        response.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

    }

    private static File getLocalFile(String path, String columnName){
        String fileName = getFileName(path);
        File outputFile = new File("C:\\Spider\\pictures\\" + columnName + "\\" + fileName);
        File parent = outputFile.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        if (!outputFile.exists()) {
            try {
                outputFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return outputFile;
    }

    private static String getFileName(String path) {
        return path.substring(path.lastIndexOf("/") + 1);
    }
}
