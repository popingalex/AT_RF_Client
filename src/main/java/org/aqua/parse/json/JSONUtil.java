package org.aqua.parse.json;

import java.io.StringReader;

import org.aqua.parse.MarkupDataObject.DataObject;
import org.aqua.parse.MarkupDataObject.DataObject.ObjectType;

public class JSONUtil {

    public static JSONDataObject getDataObject(String content) {
        // try {
        // Element root = new SAXReader().read(new StringReader(content)).getRootElement();
        return new JSONDataObject(new JSONUtil().compose(new StringReader(content)));// �˴�Ӧ����json�İ���
        // } catch (DocumentException e) {
        // e.printStackTrace();
        // }
        // return null;
    }
    private JSONUtil jsonutil;
    private JSONUtil compose(StringReader stringReader) {
        // TODO Auto-generated method stub
        return null;
    }

    public static class JSONDataObject extends DataObject {
        private JSONUtil jsonutil;
        private String   key;
        private JSONDataObject(JSONUtil jsonutil) {
            this.jsonutil = jsonutil;
        }

        public ObjectType getType() {
            return jsonutil.getDataObject(getKey()).getType();
        }

        public Integer countChilds() {

            return jsonutil.getJsonutil().getDataObject(jsonutil);
        }

        public DataObject getChild(String key) {
            return jsonutil.getDataObject(key).getChild(getKey());
        }

        public DataObject getChild(Integer index) {
            return jsonutil.getDataObject(key).getChild(index);
        }

        public String getValue() {
            return getValue();
        }

        public String getKey() {

            return jsonutil.getClass().getName();
        }
    }

    public JSONUtil getJsonutil() {
        return getJsonutil();
    }

    public void setJsonutil(JSONUtil jsonutil) {
        this.jsonutil = jsonutil;
    }

    public String toString() {
        // TODO Auto-generated method stub
        return super.toString();
    }

    public int getDataObject(JSONUtil jsonutil) {
        return jsonutil.getDataObject(getJsonutil());
    }
}
