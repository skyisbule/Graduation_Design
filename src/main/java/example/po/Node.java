package example.po;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Node {

    int id;
    List<Integer> refIds = new ArrayList<>();
    int hierarchy;

}
