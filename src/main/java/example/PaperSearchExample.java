package example;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.github.skyisbule.db.SkyDB;
import com.github.skyisbule.db.data.DataObject;
import com.github.skyisbule.db.index.Index;
import com.github.skyisbule.db.index.IndexManager;
import com.github.skyisbule.db.util.IOUtil;
import com.github.skyisbule.db.util.JsonUtil;
import example.po.Node;
import example.po.Paper;

/**
 * 这个例子主要是为了演示：
 * 如何高效率查找数据
 */
public class PaperSearchExample {

    private static final String workPath = "/users/hqt/Desktop/db/";

    private static DataObject dataObject;
    private static Index index;

    private static final int SOURCE_ID = 1;
    private static final int HIERARCHY = 3;

    public static void main(String[] args) {
        SkyDB skyDB = SkyDB.getInstance(workPath);
        dataObject = skyDB.getOrCreate(Paper.class, "1.1");
        index = IndexManager.getIndex(dataObject);

        //开始进行迭代查找
        PaperSearchExample e = new PaperSearchExample();
        List<Node> nodes = e.buildGridData();
        String html = e.buildHTML(nodes);
        IOUtil.forceWriteFile("/Users/hqt/Downloads/ECharts-Relationship-map-master/" + Paper.class.getName() + ".html",
            html);
    }

    //待分析的起始论文id,查询的层数
    private List<Node> buildGridData() {
        List<Node> nodeList = new ArrayList<>();
        Node source = buildNode(Objects.requireNonNull(searchById(PaperSearchExample.SOURCE_ID)));
        source.setHierarchy(0);
        nodeList.add(source);
        List<Node> temp;

        for (int nowHierarchy = 0; nowHierarchy < PaperSearchExample.HIERARCHY; nowHierarchy++) {
            temp = new ArrayList<>();
            for (Node node : nodeList) {
                if (node.getHierarchy() == nowHierarchy) {
                    for (Integer refId : node.getRefIds()) {
                        Node refNode = buildNode(Objects.requireNonNull(searchById(refId)));
                        refNode.setHierarchy(nowHierarchy + 1);
                        temp.add(refNode);
                    }
                }
            }
            nodeList.addAll(temp);
        }

        return nodeList;
    }

    private Node buildNode(Paper paper) {
        List<Integer> refIds = new ArrayList<>();
        for (String s : paper.getRefs().split(" ")) {
            if (s.equals(" ")) { break; }
            int id = Integer.parseInt(s);
            refIds.add(id);
        }
        Node node = new Node();
        node.setId(paper.getId());
        node.setRefIds(refIds);
        return node;
    }

    private Paper searchById(int id) {
        int pageId = index.getPageNum(id);

        for (Object o : dataObject.getPage(pageId)) {
            Paper paper = (Paper)o;
            if (paper.getId() == id) {
                return paper;
            }
        }
        return null;
    }

    private String buildHTML(List<Node> nodeList) {
        List<HtmlNode> htmlNodes = new ArrayList<>();
        List<HtmlLink> links = new ArrayList<>();
        Set<Integer> set = new HashSet<>();
        for (Node node : nodeList) {
            if (node.getHierarchy() == HIERARCHY - 1) {
                break;
            }
            set.add(node.getId());
            set.addAll(node.getRefIds());
        }
        for (Integer id : set) {
            HtmlNode htmlNode = new HtmlNode();
            htmlNode.name = String.valueOf(id);
            htmlNodes.add(htmlNode);
        }

        for (Node node : nodeList) {
            if (node.getHierarchy() == HIERARCHY - 1) {
                break;
            }

            for (Integer id : node.getRefIds()) {
                HtmlLink link = new HtmlLink();
                link.source = String.valueOf(node.getId());
                link.target = String.valueOf(id);
                links.add(link);
            }
        }
        System.out.println(JsonUtil.getJson(htmlNodes));
        System.out.println("--------------------------");
        System.out.println(JsonUtil.getJson(links));

        String htmlHead = "<!DOCTYPE html>\n"
            + "<html>\n"
            + "<head>\n"
            + "    <meta name=\"viewport\" content=\"width=device-width\" />\n"
            + "    <title>关系图谱</title>\n"
            + "    <script src=\"jquery-1.10.2.min.js\"></script>\n"
            + "    <script src=\"echarts.min.js\"></script>\n"
            + "    <style type=\"text/css\">\n"
            + "        html, body, #main { height: 100%; width: 100%; margin: 0; padding: 0 }\n"
            + "    </style>\n"
            + "</head>";
        String htmlTemplate = "<!DOCTYPE html>\n"
            + "<body>\n"
            + "    <div id=\"main\" style=\"\"></div>\n"
            + "    <script type=\"text/javascript\">\n"
            + "        var myChart = echarts.init(document.getElementById('main'));\n"
            + "        option = {\n"
            + "            title: { text: '论文关系图谱' },\n"
            + "            tooltip: {\n"
            + "                formatter: function (x) {\n"
            + "                    return x.data.des;\n"
            + "                }\n"
            + "            },\n"
            + "            series: [\n"
            + "                {\n"
            + "                    type: 'graph',\n"
            + "                    layout: 'force',\n"
            + "                    symbolSize: 80,\n"
            + "                    roam: true,\n"
            + "                    edgeSymbol: ['circle', 'arrow'],\n"
            + "                    edgeSymbolSize: [4, 10],\n"
            + "                    edgeLabel: {\n"
            + "                        normal: {\n"
            + "                            textStyle: {\n"
            + "                                fontSize: 20\n"
            + "                            }\n"
            + "                        }\n"
            + "                    },\n"
            + "                    force: {\n"
            + "                        repulsion: 2500,\n"
            + "                        edgeLength: [10, 50]\n"
            + "                    },\n"
            + "                    draggable: true,\n"
            + "                    itemStyle: {\n"
            + "                        normal: {\n"
            + "                            color: '#4b565b'\n"
            + "                        }\n"
            + "                    },\n"
            + "                    lineStyle: {\n"
            + "                        normal: {\n"
            + "                            width: 2,\n"
            + "                            color: '#4b565b'\n"
            + "\n"
            + "                        }\n"
            + "                    },\n"
            + "                    edgeLabel: {\n"
            + "                        normal: {\n"
            + "                            show: true,\n"
            + "                            formatter: function (x) {\n"
            + "                                return x.data.name;\n"
            + "                            }\n"
            + "                        }\n"
            + "                    },\n"
            + "                    label: {\n"
            + "                        normal: {\n"
            + "                            show: true,\n"
            + "                            textStyle: {\n"
            + "                            }\n"
            + "                        }\n"
            + "                    },\n"
            + "                    data: %s\n"
            + ",\n"
            + "                    links: %s\n"
            + "\n"
            + "\n"
            + "                }\n"
            + "            ]\n"
            + "        };\n"
            + "        myChart.setOption(option);\n"
            + "    </script>\n"
            + "</body>\n"
            + "</html>\n";
        return htmlHead + String.format(htmlTemplate, JsonUtil.getJson(htmlNodes), JsonUtil.getJson(links));
    }

    private static class HtmlNode {
        public String name;
    }

    private static class HtmlLink {
        String source;
        String target;
    }

}
