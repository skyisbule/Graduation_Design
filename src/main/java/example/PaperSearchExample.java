package example;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.github.skyisbule.db.SkyDB;
import com.github.skyisbule.db.data.DataObject;
import com.github.skyisbule.db.index.Index;
import com.github.skyisbule.db.index.IndexManager;
import example.po.Node;
import example.po.Paper;

/**
 * 这个例子主要是为了演示：
 * 如何高效率查找数据
 */
public class PaperSearchExample {

    private static DataObject dataObject;
    private static Index index;

    public static void main(String[] args) {
        SkyDB skyDB = SkyDB.getInstance();
        dataObject = skyDB.getOrCreate(Paper.class, "1.1");
        index = IndexManager.getIndex(dataObject);

        //开始进行迭代查找
        PaperSearchExample e = new PaperSearchExample();
        e.buildGridData(2,1);
    }

    //待分析的起始论文id,查询的层数
    private List<Node> buildGridData(int sourceId, int hierarchy) {
        List<Node> nodeList = new ArrayList<>();
        Node source = buildNode(Objects.requireNonNull(searchById(sourceId)));
        source.setHierarchy(0);
        nodeList.add(source);
        List<Node> temp;

        for (int nowHierarchy = 0; nowHierarchy < hierarchy; nowHierarchy++) {
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

}
