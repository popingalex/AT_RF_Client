package org.ats.atrf.service.task;

import org.ats.atrf.resource.ExcelUtil.DataObject;
import org.ats.atrf.resource.FileUtil;
import org.ats.atrf.service.AbstractService;

public class BillingGenerateService extends AbstractService {

    @Override
    public void service(ServiceContent content) {
        DataObject dObj = new DataObject((String) content.get("path"));
        dObj.switchSheet(1);
        // 3 virtual value
        // 4 invalid value
        StringBuffer filename = new StringBuffer();
        StringBuffer headRec = new StringBuffer();
        StringBuffer contentRec = new StringBuffer();
        StringBuffer tailRec = new StringBuffer();

        // 1 + 0-3 文件名
        for (int r = 0; r < 3; r++) {
            // System.out.println(dObj.getCell(r + 1, 2));
            filename.append(dObj.getCell(r + 1, 3));
        }
        filename.append(".");
        filename.append(dObj.getCell(3 + 1, 3));
        
        System.out.println("filename");
        System.out.println(filename);

        // 5 + 0-10 头记录
        for (int r = 0; r < 11; r++) {
            // System.out.println(dObj.getCell(r + 4, 2));
            headRec.append(dObj.getCell(r + 1 + 4, 3));
        }
        System.out.println("head");
        System.out.println(headRec);

        // 31 + 0-37
        for (int r = 0; r < 38; r++) {
            // System.out.println(dObj.getCell(r + 30, 2));
            String part = dObj.getCell(r + 1 + 30, 3).getStringCellValue();
            if (part.contains("/")) {
                contentRec.append(part.split("/")[0]);
            } else {
                contentRec.append(part);
            }
        }
        System.out.println("content");
        System.out.println(contentRec);
        // 16 + 0-14
        for (int r = 0; r < 15; r++) {
            // System.out.println(dObj.getCell(r + 15, 2));
            tailRec.append(dObj.getCell(r + 1 + 15, 3));
        }
        System.out.println("tail");
        System.out.println(tailRec);
        StringBuffer result = new StringBuffer();
        result.append(headRec);
        result.append(contentRec);
        result.append(tailRec);
        FileUtil.makeFile(filename.toString(), result.toString().replace("\\r", "\r").replace("\\n", "\n"));
    }
    public static void main(String[] args) {
        ServiceContent content = new ServiceContent();
        String path = "C:\\Users\\Administrator\\git\\AT_RF_Client\\source\\话单文件名称及各记录字段统计_template.xlsx";
        path = "C:\\Users\\Administrator\\git\\AT_RF_Client\\source\\话单文件名称及各记录字段统计2.xlsx";
        content.put("path", path);
        new BillingGenerateService().service(content);
    }
}
