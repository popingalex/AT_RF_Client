package org.aqua.parse.yaml.snake;

import java.io.StringReader;

import org.aqua.parse.MarkupDataObject.DataObject;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

public class SnakeYamlUtil {
    public static YamlDataObject getDataObject(String content) {
        return new YamlDataObject(new Yaml().compose(new StringReader(content)));
    }

    public static class YamlDataObject extends DataObject {
        private Node   node;
        private String key;

        private YamlDataObject(Node node) {
            this.node = node;
        }

        public ObjectType getType() {
            switch (node.getNodeId()) {
            case mapping:
                return ObjectType.Map;
            case sequence:
                return ObjectType.List;
            case scalar:
                return ObjectType.Value;
            default:
                return null;
            }
        }

        public Integer countChilds() {
            switch (node.getNodeId()) {
            case mapping:
                return ((MappingNode) node).getValue().size();
            case sequence:
                return ((SequenceNode) node).getValue().size();
            default:
                return 0;
            }
        }

        public DataObject getChild(String key) {
            for (NodeTuple t : ((MappingNode) node).getValue()) {
                if (((ScalarNode) t.getKeyNode()).getValue().equals(key)) {
                    return new YamlDataObject(t.getValueNode());
                }
            }
            return null;
        }

        public DataObject getChild(Integer index) {
            switch (node.getNodeId()) {
            case mapping:

                if (((MappingNode) node).getValue().size() > index) {
                    NodeTuple tuple = ((MappingNode) node).getValue().get(index);
                    YamlDataObject child = new YamlDataObject(tuple.getValueNode());
                    child.key = ((ScalarNode) tuple.getKeyNode()).getValue();
                    return child;
                }
            case sequence:
                if (((SequenceNode) node).getValue().size() > index) {
                    return new YamlDataObject(((SequenceNode) node).getValue().get(index));
                }
            default:
                return null;
            }
        }

        public String getValue() {
            return ((ScalarNode) node).getValue();
        }

        public String getKey() {
            return key;
        }
    }
}
