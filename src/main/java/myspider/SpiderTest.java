package myspider;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.HttpUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * @author zhangtf
 * @date 2019-12-17 17:01
 */
public class SpiderTest {
    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            String path = "https://w.yasek3.com/search/?type=pic&keyword=" + args[0];
            List<String> pagerPathList = getPagerPathList(path, args[0]);
            for (String pagerPath : pagerPathList) {
                System.out.println(pagerPath + "  " + "开始");
                spiderYaSe(pagerPath);
            }
        } else {
            System.out.println("请重新输入参数");
            System.exit(-1);
        }


    }

    private static List<String> getPagerPathList(String path, String keyword) {
        int pagerCount = getPagerCount(path);
        List<String> pagerPathList = new ArrayList<String>();
        for (int i = 1; i <= pagerCount; i++) {
            String pagerPath = "https://w.yasek3.com/search/?type=pic&keyword=" + keyword + "&page=" + i;
            pagerPathList.add(pagerPath);
        }
        return pagerPathList;
    }

    private static void spiderYaSe(String path) {
        Document document = Jsoup.parse(HttpUtil.getHtml(path));
        Elements elements = document.select(".pic_info>a");
        List<String> allUrls = new ArrayList<String>();
        for (Element element : elements) {
            allUrls.add("https://w.yasek3.com" + element.attr("href"));
        }
        for (String url : allUrls) {
            Document imageDocument = Jsoup.parse(HttpUtil.getHtml(url));
            String columnName = imageDocument.select("h1").get(0).text();
            Elements imageElements = imageDocument.select("center>a>img");
            for (Element imageElement : imageElements) {
                String imageUrl = imageElement.attr("data-original");
                HttpUtil.downloadPicture(imageUrl, columnName);
            }
        }
    }

    public static int getPagerCount(String path) {
        Document document = Jsoup.parse(HttpUtil.getHtml(path));
        Element element = document.select(".pager>li:nth-last-child(2)>a").get(0);
        System.out.println(element);
        return Integer.parseInt(element.text());
    }

}
