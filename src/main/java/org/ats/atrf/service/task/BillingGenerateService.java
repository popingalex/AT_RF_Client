package org.ats.atrf.service.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.aqua.io.file.FileUtil;
import org.aqua.parse.MarkupDataObject;
import org.aqua.parse.MarkupDataObject.DataObject;
import org.aqua.parse.MarkupDataObject.Language;
import org.ats.atrf.net.FTPUtil;
import org.ats.atrf.net.ParseUtil;
import org.ats.atrf.net.SSHUtil;
import org.ats.atrf.resource.ExcelUtil.SheetObject;
import org.ats.atrf.resource.WordUtil.WordObject;
import org.ats.atrf.service.AbstractService;

public class BillingGenerateService extends AbstractService {

    @Override
    public void service(ServiceContent content) {

        DataObject data_testcase;
        DataObject data_cfg;
        DataObject data_biz;
        SheetObject sheet_rule;

        FTPUtil ftp;
        SSHUtil ssh;

        Result result = null;

        String path_testcase = (String) content.get("path_case");
        String type_actual = null;
        String type_expect = null;
        String name_biz;
        String name_file;
        String in_local;
        String in_remote;
        String out_local;
        String temp_local;
        String temp_remote;

        long time_out;
        long time_begin;
        long time_endin;

        data_testcase = MarkupDataObject.getDataObject(path_testcase, Language.YAML);
        data_cfg = (DataObject) content.get("data_cfg");
        sheet_rule = new SheetObject(data_cfg.getValue("rule"));

        name_biz = data_testcase.getValue("biz");
        data_biz = data_cfg.getChild("biz", name_biz);
        in_local = data_cfg.getValue("path", "local", "in");
        in_remote = data_biz.getValue("in", 0);
        out_local = data_cfg.getValue("path", "local", "out");
        temp_local = data_cfg.getValue("path", "local", "temp");
        temp_remote = data_cfg.getValue("path", "remote", "temp");

        time_out = Integer.parseInt(data_cfg.getValue("timeout"));
        time_begin = System.currentTimeMillis();
        {
            DataObject ftpObj = data_cfg.getChild("remote", "ftp");
            DataObject sshObj = data_cfg.getChild("remote", "ssh");

            int ftp_port = Integer.parseInt(ftpObj.getValue("port"));
            String ftp_host = ftpObj.getValue("host");
            String ftp_acct = ftpObj.getValue("acct");
            String ftp_pswd = ftpObj.getValue("pswd");

            int ssh_port = Integer.parseInt(sshObj.getValue("port"));
            String ssh_host = sshObj.getValue("host");
            String ssh_acct = sshObj.getValue("acct");
            String ssh_pswd = sshObj.getValue("pswd");

            ftp = new FTPUtil(ftp_host, ftp_port, ftp_acct, ftp_pswd);
            ssh = new SSHUtil(ssh_host, ssh_port, ssh_acct, ssh_pswd);
        }
        {
            System.out.println("step 1.1 生成用例");
            System.out.println("手工完成");
        }
        {
            System.out.println("step 2.1 生成话单文件");
            name_file = generateBill(name_biz, in_local, data_testcase.getChild("rec"), sheet_rule);
            System.out.println("话单文件[" + name_file + "]");
        }
        {
            System.out.println("step 2.2 将话单文件拷贝至远程机临时目录");
            ftp.appendFile(new File(temp_remote, name_file), new File(in_local, name_file));
            System.out.println("从[" + in_local + "]拷贝至[" + temp_remote + "]");
        }
        List<String> list_pid;
        {
            System.out.println("step 3.1 检查该业务ecframe运行状态(用不用杀)");
            String command = ssh.execute("ps -ef|grep ecframe|grep imms");
            System.out.println("ps输出:");
            System.out.println(ssh.getOutput());
            list_pid = ParseUtil.parsePS(ssh.getOutput().toString(), command);
            System.out.println("检查到在运行的ecframe程序:" + list_pid);
        }
        {
            System.out.println("step 3.2 [关闭该业务的ecframe]");
            for (String pid : list_pid) {
                ssh.execute("kill", pid);
            }
            for (String command = "ps -ef|grep ecframe|grep ".concat(name_biz); list_pid.size() > 0;) {  // 等待目标程序关闭
                System.out.println("当前运行的pid:" + list_pid);
                System.out.println("关闭操作尚未完成, 1000毫秒之后再次尝试");
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ssh.execute(command);
                list_pid = ParseUtil.parsePS(ssh.getOutput().toString(), command);
            }
            System.out.println("关闭操作完成");
        }
        {
            System.out.println("step 2.3 将话单文件从临时目录移至工作目录");
            ssh.execute("mv", new File(temp_remote, name_file).getPath().replace("\\", "/"), in_remote);
            System.out.println("从[" + temp_remote + "]拷贝至[" + in_remote + "]");
        }
        {
            System.out.println("step 3.3 启动该业务的ecfarme");
            DataObject processors = data_biz.getChild("ecframe");
            ssh.shell();
            for (int i = 0; i < processors.countChilds(); i++) {
                ssh.command("nohup ecframe -i", processors.getValue(i), ">", "outfile", "2>&1 &");
                System.out.println("已启动:" + processors.getValue(i));
            }
        }
        {
            System.out.println("step 3.4 按配置时间等待ecframe运行");
            System.out.println("等待:" + time_out);
            try {
                Thread.sleep(time_out);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        {
            System.out.println("step 4.1 尝试回收结果文件");
            result = fetchResult(name_file, temp_local, data_biz.getChild("out"), ftp);
        }
        {
            // System.out.println("step 3.5 [关闭该业务ecframe]");
            ssh.shutdown();
            /**
             * TODO 保持ecframe打开, 以便其他工作人员使用.
             */

            // System.out.println(ssh.getOutput());
        }
        {
            System.out.println("step 5.1 分析结果文件");
            if (result != null) {
                System.out.println("结果类型:" + result.getType());
                System.out.println("结果文件:" + result.getPath());
                type_actual = result.getType().substring(result.getType().length() - 3);
                type_expect = data_testcase.getValue("result", "type");
                System.out.println("预期结果[" + type_expect + "] 实际结果[" + type_actual + "]");
                if (!type_actual.equals(type_expect)) {
                    if (type_actual.equals("out")) {
                        handleOutput(result, data_testcase.getChild("result"));
                    } else if (type_actual.equals("err")) {
                        handleError(result, data_testcase.getChild("result"));
                    }

                }
            } else {
                System.out.println("获取结果文件失败");
            }
        }
        time_endin = System.currentTimeMillis();
        { // 信息收集
            content.put("biz", name_biz);
            content.put("file", name_file);
            content.put("time_begin", time_begin);
            content.put("time_endin", time_endin);
            content.put("type_actual", type_actual);
            content.put("type_expect", type_expect);
            content.put("result", result);
        }
    }

    /**
     * 根据用例生成话单
     * 
     * @param biz
     * @param rules
     * @param doc
     * @return
     */
    private StringBuffer makeRecord(String biz, DataObject rules, SheetObject doc) {
        StringBuffer buffer = new StringBuffer();
        doc.switchSheet(biz);
        for (int i = 0, sum = rules.countChilds(); i < sum; i++) {
            DataObject rule = rules.getChild(i);
            int index = findNextIndex(rule.getKey(), 0, 2, doc);
            int type = Integer.parseInt((String) rule.getValue());
            String value;
            // 3 virtual value
            // 4 invalid value
            if (type > 0) {
                value = doc.getCell(index, 3).getStringCellValue();
                buffer.append(value.split("/")[type - 1]);
            } else {
                value = doc.getCell(index, 4).getStringCellValue();
                buffer.append(value.split("/")[-type - 1]);
            }
        }
        return buffer;
    }

    /**
     * 获得下一个该字段的下标
     * 
     * @param target
     * @param initial
     * @param column
     * @param doc
     * @return
     */
    private Integer findNextIndex(String target, Integer initial, Integer column, SheetObject doc) {
        for (int i = initial;; i++) {
            Cell cell = doc.getCell(i, column);
            if (cell == null)
                return -1;
            if (cell.getStringCellValue().equals(target))
                return i;
        }
    }

    private String generateBill(String biz, String temp, DataObject testcase, SheetObject rule) {
        String fileName = makeRecord(biz, testcase.getChild("file"), rule).toString();
        StringBuffer fileContent = new StringBuffer();
        fileContent.append(makeRecord(biz, testcase.getChild("head"), rule));
        for (int i = 0, sum = testcase.getChild("content").countChilds(); i < sum; i++) {
            fileContent.append(makeRecord(biz, testcase.getChild("content", i), rule));
        }
        fileContent.append(makeRecord(biz, testcase.getChild("tail"), rule));
        FileUtil.makeFile(new File(temp, fileName), fileContent.toString().replace("\\r", "\r").replace("\\n", "\n"));  // 替换带/的转义字符
        return fileName;
    }

    private Result fetchResult(String filename, String workspace, DataObject outs, FTPUtil ftp) {
        for (int i = 0, sum = outs.countChilds(); i < sum; i++) {
            DataObject out = outs.getChild(i);
            String pattern = out.getValue("name").replace("name", filename);
            System.out.println("尝试搜索[" + filename + "]@[" + out.getValue("path") + "]");
            boolean got = ftp.retrieveFile(new File(out.getValue("path"), pattern), new File(workspace, pattern));
            if (got) {
                ftp.deleteFile(new File(out.getValue("path"), pattern).getPath());
                return new Result(out.getValue("type"), new File(workspace, pattern).getPath());
            }
        }
        return null;
    }

    private static class Result extends ArrayList<Result.Diff> {
        private static final long serialVersionUID = 1L;
        private String            type;
        private String            path;

        public Result(String type, String path) {
            this.type = type;
            this.path = path;
        }
        public final String getType() {
            return type;
        }

        public final String getPath() {
            return path;
        }

        public final boolean isPassed() {
            for (Diff diff : this) {
                System.out.println("pass:" + diff.expect + ":" + diff.actual);
                if (!diff.expect.equals(diff.actual)) {
                    return false;
                }
            }
            return true;
        }

        private static class Diff {
            private int    index;
            private String expect;
            private String actual;
            private String message;

            public Diff(int index, String expect, String actual) {
                this.index = index;
                this.expect = expect;
                this.actual = actual;
            }

            public Diff(int index, String expect, String actual, String message) {
                this(index, expect, actual);
                this.message = message;
            }

            public final int getIndex() {
                return index;
            }

            public final String getExpect() {
                return expect;
            }

            public final String getActual() {
                return actual;
            }

            public final String getMessage() {
                return message;
            }
        }
    }

    private void handleError(Result result, DataObject data_result) {
        System.out.println("err path:"+result.getPath());
        String content = FileUtil.readFile(FileUtil.readReader(result.getPath(), "GBK"));
        System.out.println("TYPE :");
        System.out.println("  expect [" + data_result.getChild("type").getValue() + "] actual [err]");
        if (!data_result.getChild("type").getValue().equals("err")) {
//            return;
        }
        System.out.println("CODE : ");
//        String expect = (String) data_result.getChild("result").getValue();
        String expect = "pass";
        String actual = content.split(":")[0];
        String message = content.split(":")[1];
        System.out.println("  expect [" + expect + "] actual [" + actual + "]");
        System.out.println("TEXT : " + message);
        if (!expect.equals(actual)) {
            result.add(new Result.Diff(0, expect, actual, message));
        }
    }

    private void handleOutput(Result result, DataObject data_result) {
        System.out.println("out path:"+result.getPath());
        System.out.println("TYPE :");
        System.out.println("  expect [" + data_result.getChild("type").getValue() + "] actual [out]");
        if (!data_result.getChild("type").getValue().equals("out")) {
//            return;
        }
        System.out.println("CONTENT :");
        BufferedReader reader = new BufferedReader(FileUtil.readReader(result.getPath(), "GBK"));
        try {
            for (int i = 0; i < 6; i++) {
                reader.readLine();
            }
            int index = 0;
            for (String content = reader.readLine(); content != null; content = reader.readLine(), index++) {
                String[] contents = content.split(";");
                String expect = (String) data_result.getChild("result").getChild(index).getValue();
                String actual = (String) contents[contents.length - 1];
                System.out.println((index + 1) + "  expect [" + expect + "] actual [" + actual + "]");
                if (!expect.equals(actual)) {
                    result.add(new Result.Diff(index, expect, actual));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        String project = "";
        String path_cfg = "config/billbiz.yml";
        String path_out;
        String path_task = args[0];
        String path_tmpl = "C:/workspace/bill/source/billtemplate.docx";

        long date_begin;
        long date_endin;

        int count_biz_run = 0;
        int count_case_run = 0;
        int count_case_done = 0;

        List<String> list_biz = new ArrayList<String>();
        List<Object[]> list_fail = new ArrayList<Object[]>();
        List<Object[]> list_bill = new ArrayList<Object[]>();
        Map<String, Integer> map_case_run = new HashMap<String, Integer>();
        Map<String, Integer> map_case_done = new HashMap<String, Integer>();

        SimpleDateFormat format_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        DataObject data_cfg = MarkupDataObject.getDataObject(path_cfg, Language.YAML);
        path_out = data_cfg.getValue("path", "local", "out");
        List<ServiceContent> contentList = new ArrayList<ServiceContent>();
        try {
            ServiceContent content;
            BufferedReader reader = new BufferedReader((FileUtil.readReader(path_task)));
            project = reader.readLine();
            for (String casepath = reader.readLine(); null != casepath; casepath = reader.readLine()) {
                contentList.add(content = new ServiceContent());
                content.put("data_cfg", data_cfg);
                content.put("path_case", casepath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        date_begin = System.currentTimeMillis();
        AbstractService service = new BillingGenerateService();
        for (int i = 0, sum = contentList.size(); i < sum; i++) {
            ServiceContent content = contentList.get(i);
            service.service(content);

            String biz = content.get("biz").toString();
            if (!list_biz.contains(biz)) {
                list_biz.add(biz);
                count_biz_run++;
                map_case_run.put(biz, 0);
                map_case_done.put(biz, 0);
            }
            count_case_run++;
            Result result = (Result) content.get("result");
            map_case_run.put(biz, map_case_run.get(biz) + 1);
            if (result.isPassed()) {
                count_case_done++;
                map_case_done.put(biz, map_case_done.get(biz) + 1);
            } else {
                Object[] array_fail = new Object[5];
                array_fail[0] = biz;
                array_fail[1] = new File(content.get("path_case").toString()).getName();
                if (content.get("type_actual").equals("out")) {
                    array_fail[2] = "Pass";
                } else if (content.get("type_actual").equals("err")) {
                    array_fail[2] = result.get(0).expect;
                    array_fail[3] = result.get(0).actual;
                    array_fail[4] = result.get(0).message;
                }
                list_fail.add(array_fail);
            }
            Object[] array_bill = new Object[3];
            array_bill[0] = biz;
            array_bill[1] = new File(content.get("path_case").toString()).getName();
            array_bill[2] = FileUtil.readFile(FileUtil.readReader(result.getPath(), "GBK"));
            list_bill.add(array_bill);
        }
        date_endin = System.currentTimeMillis();

        WordObject wo = new WordObject(path_tmpl);

        wo.replaceHolder("%project%", project);

        wo.replaceHolder("%id_task%", "run" + new File(path_task).getName());
        wo.replaceHolder("%date_begin%", format_date.format(new Date(date_begin)));
        wo.replaceHolder("%date_endin%", format_date.format(new Date(date_endin)));
        wo.replaceHolder("%count_biz_run%", count_biz_run);
        wo.replaceHolder("%count_case_run%", count_case_run);
        wo.replaceHolder("%count_case_done%", count_case_done);
        wo.replaceHolder("%count_case_fail%", count_case_run - count_case_done);
        wo.replaceHolder("%rate_case_done%", String.format("%.2f", count_case_done * 1f / count_case_run).concat("%"));

        {
            Object[][] table_result = new Object[list_biz.size()][3];
            for (int i = 0, sum = table_result.length; i < sum; i++) {
                table_result[i][0] = list_biz.get(i);
                table_result[i][1] = map_case_done.get(table_result[i][0]);
                table_result[i][2] = map_case_run.get(table_result[i][0]) - map_case_done.get(table_result[i][0]);
            }
            wo.fillTable("result_brief", table_result);
            table_result = new Object[list_fail.size()][];
            wo.fillTable("result_case", list_fail.toArray(table_result));
            table_result = new Object[list_bill.size()][];
            wo.fillTable("result_bill", list_bill.toArray(table_result));
        }

        wo.replaceHolder(WordObject.PATTERN_HOLDER, "");
        wo.save(new File(path_out, new File(args[0]).getName()).getPath().concat(".doc"));

        /**
         * nohup ecframe -i /cbbs_test4/fszqjf/config/verifile/verifile_00043201_imms.xml > test0.out 2>&1 &
         * nohup ecframe -i /cbbs_test4/fszqjf/config/decode/decode_10043201_imms.xml > test1.out 2>&1 &
         * nohup ecframe -i /cbbs_test4/fszqjf/config/rpling/rpling_20043201_imms.xml > test2.out 2>&1 &
         * nohup ecframe -i /cbbs_test4/fszqjf/config/chkdup/chkdup_30043201_imms.xml > test3.out 2>&1 &
         */

    }
}
